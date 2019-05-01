package com.code.ting.netty.proxy.http.io.netty.client;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultContext;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        System.out.println("end : " + System.currentTimeMillis());
        RouteContext context = ctx.channel().attr(Consts.CONTEXT_KEY).get();

        DefaultResponse response = new DefaultResponse();
        response.setFullHttpResponse(msg);
        ((DefaultContext) context).setResponse(response);
        context.getConnector().setClientFullHttpResponse(msg);

        FilterChain chain = ctx.channel().attr(Consts.CHAIN_KEY).get();
        msg.retain();
        chain.fireChain(context);
    }
}
