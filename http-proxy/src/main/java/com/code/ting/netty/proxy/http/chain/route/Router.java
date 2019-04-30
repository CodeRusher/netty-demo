package com.code.ting.netty.proxy.http.chain.route;


import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;

public interface Router {
    YieldResult route(RouteContext context) throws Throwable;
}
