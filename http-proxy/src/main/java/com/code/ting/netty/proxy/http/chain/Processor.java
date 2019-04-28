package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;

public interface Processor {

    void pre(Context context);

    boolean process(Context context);

    void after(Context context);

}
