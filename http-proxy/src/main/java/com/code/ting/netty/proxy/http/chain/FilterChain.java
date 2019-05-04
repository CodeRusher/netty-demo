package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.CancelReason;
import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.chain.context.Step;
import com.code.ting.netty.proxy.http.chain.route.Router;
import com.code.ting.netty.proxy.http.util.RenderUtils;
import com.code.ting.netty.proxy.http.util.StatusCode;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.ReferenceCountUtil;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilterChain {

    public static ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2,
        10, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(1024)
    );

    private ConcurrentSkipListMap<Long, ContextHolder> contexts = new ConcurrentSkipListMap<>();

    private LinkedList<Filter> filters = new LinkedList<>();

    private Router router;

    private ScheduledThreadPoolExecutor cleanExecutor = new ScheduledThreadPoolExecutor(
        1,
        new ThreadFactoryBuilder().setDaemon(true).setNameFormat("FilterChain-clean-%d").build());

    public FilterChain(Router router) {
        this.router = router;
        cleanExecutor.scheduleAtFixedRate(() -> {
            log.info("filter-chain-clean running...");
            Iterator<Entry<Long, ContextHolder>> iterator = contexts.entrySet().iterator();
            if (iterator.hasNext()) {
                Entry<Long, ContextHolder> entry = iterator.next();
                if (entry != null) {
                    // 过期清理(超过10分钟)
                    if (System.currentTimeMillis() - entry.getValue().getStartAt() > 1000 * 60 * 10) {
                        cleanContext(entry.getValue().context, true);
                        iterator.remove();
                    }
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }


    public void fireChain(Long id) {
        ContextHolder holder = contexts.get(id);
        if (holder == null) {
            throw new IllegalStateException(" error in chain, ContextHolder has been removed ,context:");
        }

        fireChain(holder);
    }

    public void fireChain(RouteContext context) {

        ContextHolder holder;
        if (context.getStatus() == Status.NEW) {
            context.setStatus(Status.IN_CHAIN);
            holder = new ContextHolder();
            holder.setContext(context);
            contexts.put(context.getId(), holder);
        } else {
            holder = contexts.get(context.getId());
        }

        if (holder == null) {
            throw new IllegalStateException(" error in chain, ContextHolder has been removed ,context:");
        }

        fireChain(holder);
    }

    private void fireChain(ContextHolder holder) {
        log.debug("{} fireChain.........", holder.getContext().getId());
        RouteContext context = holder.getContext();

        // pre
        if (holder.step == Step.PRE) {
            while (holder.index + 1 < filters.size()) {
                holder.index++;
                Filter filter = filters.get(holder.index);
                try {
                    holder.step = Step.PRE;
                    YieldResult yieldResult = filter.pre(holder.context);
                    // yield for chain
                    if (yieldResult.yield) {
                        return;
                    }
                    if (!yieldResult.success) {
                        holder.getContext().setStatus(Status.CANCEL);
                        break;
                    }
                } catch (Throwable t) {
                    log.error("error in chain :{}", t.getMessage(), t);
                    holder.step = Step.AFTER;
                    holder.hasThrowable = true;
                    break;
                }
            }//end while

            holder.setStep(Step.ROUTE);
        }

        // route
        if (Status.CANCEL != holder.getContext().getStatus() && !holder.hasThrowable
            && holder.step == Step.ROUTE) {
            holder.setStep(Step.AFTER);
            try {

                YieldResult yieldResult = router.route(holder.getContext());
                // yield for chain
                if (yieldResult.yield) {
                    return;
                }
                if (!yieldResult.success) {
                    holder.getContext().setStatus(Status.CANCEL);
                }
            } catch (Throwable t) {
                log.error("error in router :{}", t.getMessage(), t);
                holder.hasThrowable = true;
            }
        }

        // after
        if (holder.step == Step.AFTER) {
            // Filter.after must invoke
            while (holder.index >= 0) {
                try {
                    Filter p = filters.get(holder.index);
                    p.after(context);
                } catch (Throwable t) {
                    log.error("error in chain.after :{}", t.getMessage(), t);
                    holder.hasThrowable = true;
                }

                holder.index--;
            }// end while
        }

        // render response
        Channel channel = holder.getContext().getConnector().getProxyChannel();
        boolean needCleanResponse = true;
        try {
            // cancel
            if (holder.getContext().getStatus() == Status.CANCEL) {
                CancelReason cancelReason = holder.getContext().getCancelReason();
                cancelReason = Objects.isNull(cancelReason) ? CancelReason.DEFAULT_CANCEL_REASON : cancelReason;
                RenderUtils.response(channel, cancelReason.getCode(), cancelReason.getMsg());
                return;
            }
            // error occurred
            if (holder.hasThrowable) {
                RenderUtils.response(channel, StatusCode.SYS_STATUS_CODE.getCode(), StatusCode.SYS_STATUS_CODE.getMsg());
                return;
            }
            // remote's response
            Connector connector = context.getConnector();
            connector.getProxyChannel().writeAndFlush(connector.getClientFullHttpResponse());
            needCleanResponse = false;

        } finally {
            cleanContext(context, needCleanResponse);
        }

    }

    private void cleanContext(RouteContext context, boolean needCleanResponse) {
        try {
            contexts.remove(context.getId());

            Connector connector = context.getConnector();
            // clean request
            release(connector.getProxyHttpRequest());
            LinkedList<HttpContent> requestContents = connector.getProxyHttpContents();
            if (requestContents != null && !requestContents.isEmpty()) {
                requestContents.forEach(this::release);
            }

            if (needCleanResponse) {
                release(connector.getClientFullHttpResponse());
            }

        } catch (Throwable t) {
            log.error("clean context error:{}", t.getMessage(), t);
        }
    }

    private void release(Object msg) {
        int count = ReferenceCountUtil.refCnt(msg);
        if (count < 1) {
            return;
        }

        ReferenceCountUtil.safeRelease(msg, count);
    }


    @Data
    private static class ContextHolder {

        RouteContext context;
        int index = -1;
        Step step = Step.PRE;
        boolean hasThrowable = false;

        long startAt = System.currentTimeMillis();

    }


}
