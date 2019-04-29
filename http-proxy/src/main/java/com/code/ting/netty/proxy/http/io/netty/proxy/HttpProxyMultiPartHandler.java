package com.code.ting.netty.proxy.http.io.netty.proxy;


import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import com.code.ting.netty.proxy.http.io.netty.context.NettyRequest;
import com.code.ting.netty.proxy.http.io.netty.context.NettyResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpProxyMultiPartHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            if (httpRequest.headers().get("Content-Type").equalsIgnoreCase("multipart")) {
                ctx.pipeline().remove(Consts.AGGREGATOR_HANDLER_KEY);
                ctx.pipeline().remove(Consts.HTTP_PROXY_HANDLER_KEY);

                NettyContext context = new NettyContext();

                NettyRequest request = new NettyRequest();
                request.setHttpRequest(httpRequest);
                context.setRequest(request);

                Connector connector = new Connector();
                connector.setProxyChannel(ctx.channel());
                connector.setProxyHttpRequest(httpRequest);
                context.setConnector(connector);

                context.setResponse(new NettyResponse());

                ctx.channel().config().setAutoRead(false);
                ctx.channel().attr(Consts.CONTEXT_KEY).set(context);
                ctx.channel().attr(Consts.CHAIN_KEY).get().fireChain(context);

            } else {
                ctx.channel().attr(Consts.CONTEXT_KEY).set(null);
            }
        }

        if (ctx.channel().attr(Consts.CONTEXT_KEY).get() != null && msg instanceof HttpContent) {

            Context context = ctx.channel().attr(Consts.CONTEXT_KEY).get();
            if (context.getStatus() == Status.CANCEL) {
                // ??? need
                ((HttpContent) msg).release();
                return;
            }

            if (msg instanceof LastHttpContent) {
                context.getConnector().getClientChannel().writeAndFlush(msg);
            } else {
                context.getConnector().getClientChannel().write(msg);
            }
        }
    }
}
