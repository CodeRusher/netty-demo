package com.code.ting.netty.proxy.http.chain.processor;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.io.netty.NettyContext;

public class RouteProcessor implements Processor {


    public RouteProcessor() {

    }

    @Override
    public void pre(NettyContext context) {

    }

    @Override
    public boolean process(NettyContext context) {

        return true;
    }

    @Override
    public void after(NettyContext context) {

    }


}
