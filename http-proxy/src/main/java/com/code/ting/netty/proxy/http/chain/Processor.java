package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.io.netty.NettyContext;

public interface Processor {

    void pre(NettyContext context);

    boolean process(NettyContext context);

    void after(NettyContext context);

}
