package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;

public interface Router {
    YieldResult route(Context context) throws Throwable;
}
