package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.context.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    private ProcessorChain chain;

    public HttpProxyHandler(ProcessorChain chain){
        this.chain = chain;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){

        // gen Context
        Context<ByteBuf> context = null;

        // fire chain
        chain.fireChain(context);

    }

}
