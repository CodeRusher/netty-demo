package com.code.ting.netty.proxy.http.chain.filter;


import com.code.ting.netty.proxy.http.chain.Filter;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.Context;

public class AddHeaderFilter implements Filter {

    @Override
    public YieldResult pre(Context context) throws Throwable {
        return YieldResult.SUCCESS;
    }

    @Override
    public void after(Context context) {
        context.getResponse().addHeader("hi", "1");

    }
}
