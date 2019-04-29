package com.code.ting.netty.proxy.http.io.netty.client;


import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import com.code.ting.netty.proxy.http.io.netty.context.NettyResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        Context context = ctx.channel().attr(Consts.CONTEXT_KEY).get();

        NettyResponse response = new NettyResponse();
        ((NettyContext) context).setResponse(response);
        context.getConnector().setClientHttpResponse(msg);

        ProcessorChain chain = ctx.channel().attr(Consts.CHAIN_KEY).get();
        chain.fireChain(context);
    }
}
