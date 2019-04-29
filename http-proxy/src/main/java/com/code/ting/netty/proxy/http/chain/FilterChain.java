package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.Status;
import io.netty.handler.codec.http.FullHttpResponse;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilterChain {

    private ConcurrentSkipListMap<Long, ContextHolder> contexts = new ConcurrentSkipListMap<>();

    private LinkedList<Filter> filters = new LinkedList<>();




    private Router router;

    public FilterChain(Router router) {
        this.router = router;
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void fireChain(Context context) {

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
        if (holder.step == Step.ROUTE) {
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
            }
        }

        // render response
        try {
            if (holder.getContext().getStatus() == Status.CANCEL) {
                holder.getContext().getConnector().getProxyChannel().writeAndFlush("cancel");
                return;
            }
            if (holder.hasThrowable) {
                context.getConnector().getProxyChannel().writeAndFlush("error".getBytes());
                return;
            }
            Connector connector = context.getConnector();
            connector.getProxyChannel().writeAndFlush(connector.getClientHttpResponse());

        } finally {
            contexts.remove(context.getId());
        }

    }



    @Data
    private static class ContextHolder {

        Context context;
        int index = -1;
        Step step = Step.PRE;
        boolean hasThrowable = false;
    }
}
