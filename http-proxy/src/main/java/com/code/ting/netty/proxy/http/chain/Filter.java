package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.FilterContext;

public interface Filter {

    YieldResult pre(FilterContext context) throws Throwable;

    void after(FilterContext context);

}
