package com.code.ting.netty.proxy.http.io.netty.proxy;

import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultContext;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultRequest;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;


public class HttpProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        DefaultContext context = new DefaultContext();

        DefaultRequest defaultRequest = new DefaultRequest();
        defaultRequest.setHttpRequest(msg);
        context.setRequest(defaultRequest);

        Connector connector = new Connector();
        connector.setProxyChannel(ctx.channel());
        connector.setProxyHttpRequest(msg);
        context.setConnector(connector);

        context.setResponse(new DefaultResponse());

        msg.retain();
        ctx.channel().attr(Consts.CHAIN_KEY).get().fireChain(context);
    }
}
