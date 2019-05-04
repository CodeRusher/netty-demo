package com.code.ting.netty.proxy.http.demo.filter;


import com.code.ting.netty.proxy.http.chain.Filter;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.FilterContext;

public class AddResponseHeaderFilter implements Filter {

    @Override
    public YieldResult pre(FilterContext context) throws Throwable {
        return YieldResult.SUCCESS;
    }

    @Override
    public void after(FilterContext context) {
        context.getResponse().headers().set("hi", "1");
    }
}
