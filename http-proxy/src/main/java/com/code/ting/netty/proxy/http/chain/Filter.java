package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;

public interface Filter {

    YieldResult pre(Context context) throws Throwable;

    void after(Context context);

}
