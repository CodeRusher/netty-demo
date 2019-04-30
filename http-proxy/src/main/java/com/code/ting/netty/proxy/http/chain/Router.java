package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.RouteContext;

public interface Router {
    YieldResult route(RouteContext context) throws Throwable;
}
