package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;

public interface Processor {

    void pre(Context context);

    YieldResult process(Context context) throws Throwable;

    void after(Context context);

}
