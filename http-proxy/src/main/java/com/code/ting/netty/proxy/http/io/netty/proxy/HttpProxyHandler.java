package com.code.ting.netty.proxy.http.io.netty.proxy;

import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import com.code.ting.netty.proxy.http.io.netty.context.NettyRequest;
import com.code.ting.netty.proxy.http.io.netty.context.NettyResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;


public class HttpProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        NettyContext context = new NettyContext();

        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.setHttpRequest(msg);

        Connector connector = new Connector();
        connector.setProxyChannel(ctx.channel());
        connector.setProxyHttpRequest(msg);
        context.setConnector(connector);

        context.setResponse(new NettyResponse());

        ctx.channel().attr(Consts.CHAIN_KEY).get().fireChain(context);
    }
}
