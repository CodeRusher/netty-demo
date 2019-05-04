package com.code.ting.netty.proxy.http.chain.context;


import java.util.concurrent.atomic.AtomicLong;

public class ContextIdGenerator {

    private static final AtomicLong ids = new AtomicLong(0);

    public static Long genId() {
        long id = ids.incrementAndGet();
        if (id > Long.MAX_VALUE - 1000000) {
            synchronized (ids) {
                if (ids.get() >= id) {
                    ids.set(0);
                }
            }
        }
        return id;
    }
}
