package com.code.ting.netty.proxy.http.chain.proccesser;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.context.Context;

public class RouteProcessor implements Processor {

    @Override
    public void pre(Context context) {

    }

    @Override
    public boolean process(Context context) {
        return false;
    }

    @Override
    public void after(Context context) {

    }
}
