package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.Status;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessorChain {

    private ConcurrentSkipListMap<Long, ContextHolder> contexts = new ConcurrentSkipListMap<>();
    private final AtomicLong ids = new AtomicLong();

    private LinkedList<Processor> processors = new LinkedList<>();


    public void addProcessor(Processor processor) {
        processors.add(processor);
    }

    public void fireChain(Context context) {

        ContextHolder holder;
        if (context.getId() == null) {
            context.setId(genId());
            holder = new ContextHolder();
            holder.setContext(context);
        } else {
            holder = contexts.get(context.getId());
        }

        if (holder == null) {
            throw new IllegalStateException(" error in chain, ContextHolder has been removed ,context:");
        }

        if (holder.step == Step.PRE || holder.step == Step.PROCESS) {
            while (holder.index < processors.size()) {
                if (holder.step == Step.PROCESS) {
                    holder.index++;
                    Processor p = processors.get(holder.index);
                    try {
                        holder.step = Step.PRE;
                        p.pre(holder.context);
                        holder.step = Step.PROCESS;
                        YieldResult yieldResult = p.process(context);
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
                }
            }//end while
        }

        if (holder.step == Step.AFTER) {
            // Processor.after must invoke
            while (holder.index >= 0) {
                try {
                    Processor p = processors.get(holder.index);
                    p.after(context);
                } catch (Throwable t) {
                    log.error("error in chain.after :{}", t.getMessage(), t);
                    holder.hasThrowable = true;
                }

                holder.index--;
            }
        }

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


    private Long genId() {
        long id = ids.decrementAndGet();
        if (id > Long.MAX_VALUE - 1000000) {
            synchronized (ids) {
                if (ids.get() >= id) {
                    ids.set(0);
                }
            }
        }
        return id;
    }


    @Data
    private static class ContextHolder {

        Context context;
        int index = 0;
        Step step = Step.PRE;
        boolean hasThrowable = false;
    }
}
