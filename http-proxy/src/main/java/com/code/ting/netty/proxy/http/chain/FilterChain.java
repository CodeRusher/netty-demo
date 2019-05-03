package com.code.ting.netty.proxy.http.chain;


import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.chain.context.Step;
import com.code.ting.netty.proxy.http.chain.route.Router;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;
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
        RouteContext context = holder.getContext();

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
                FullHttpResponse response = genResponse("1000", "cancel");
                response(holder.getContext().getConnector().getProxyChannel(), response);
                return;
            }
            if (holder.hasThrowable) {
                FullHttpResponse response = genResponse("1000", "error");
                response(holder.getContext().getConnector().getProxyChannel(), response);
                return;
            }
            Connector connector = context.getConnector();
            connector.getProxyChannel().writeAndFlush(connector.getClientFullHttpResponse());

        } finally {
            contexts.remove(context.getId());
        }
    }


    @Data
    private static class ContextHolder {

        RouteContext context;
        int index = -1;
        Step step = Step.PRE;
        boolean hasThrowable = false;
    }

    private void response(Channel channel, FullHttpResponse response) {
        channel.writeAndFlush(response);
    }

    private FullHttpResponse genResponse(String code, String msg) {
        byte[] content = msg.getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
        response.headers().set(CONTENT_TYPE, "application/txt");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
