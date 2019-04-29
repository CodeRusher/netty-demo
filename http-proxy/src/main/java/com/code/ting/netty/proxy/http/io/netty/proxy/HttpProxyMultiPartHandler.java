package com.code.ting.netty.proxy.http.io.netty.proxy;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultContext;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultRequest;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import org.apache.commons.lang3.StringUtils;

public class HttpProxyMultiPartHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // ctx.fireChannelRead()
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            String contentType = httpRequest.headers().get("Content-Type");
            if (StringUtils.isNoneBlank(contentType) && contentType.equalsIgnoreCase("multipart")) {
                ctx.pipeline().remove(Consts.AGGREGATOR_HANDLER_KEY);
                ctx.pipeline().remove(Consts.HTTP_PROXY_HANDLER_KEY);

                FilterChain chain = ctx.channel().attr(Consts.CHAIN_KEY).get();
                DefaultContext context = new DefaultContext(chain);

                DefaultRequest request = new DefaultRequest();
                request.setHttpRequest(httpRequest);
                context.setRequest(request);

                Connector connector = new Connector();
                connector.setProxyChannel(ctx.channel());
                connector.setProxyHttpRequest(httpRequest);
                context.setConnector(connector);

                context.setResponse(new DefaultResponse());

                ctx.channel().config().setAutoRead(false);
                ctx.channel().attr(Consts.CONTEXT_KEY).set(context);
                chain.fireChain(context);

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
