package com.code.ting.netty.proxy.http.io.netty.client;


import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        HttpResponseParser httpParser = ctx.channel().attr(Consts.RESPONSE_PARSER_KEY).get();
        NettyContext context = httpParser.getContext();

        ByteBuf in = (ByteBuf) msg;
        /*---------------------  direct forward  ---------------------------*/
        if (context.getStatus() == Status.RESPONSE_HEADER_READ) {
            if (!context.getResponse().isFull()) {
                Connector connector = context.getConnector();
                if (connector.getProxy() instanceof Channel) {
                    int readableBytes = in.readableBytes();
                    if (readableBytes + httpParser.getBodyReadedLength() >= httpParser.getContentLength()) {
                        ((Channel) (connector.getProxy())).writeAndFlush(in);

                        context.setStatus(Status.RESPONSE_COMPLETED);
                        ctx.channel().attr(Consts.CHAIN_KEY).get().fireChain(context);
                    } else {
                        ((Channel) (connector.getProxy())).write(in);
                    }

                    httpParser.setBodyReadedLength(httpParser.getBodyReadedLength() + readableBytes);
                }

                if (connector.getClient() instanceof java.net.Socket) {
                    // ...
                    log.error(" client is Socket?");
                }

                return;
            }
        }

        /*---------------------  parse request  ---------------------------*/
        httpParser.parse(in);

        if ((context.getStatus() == Status.REQUEST_COMPLETED) ||
            (context.getResponse().isFull() && context.getStatus() == Status.RESPONSE_HEADER_READ)) {
            in.release();
            return;
        }

        if (context.getStatus() == Status.RESPONSE_COMPLETED) {
            ctx.channel().write(Unpooled.wrappedBuffer(context.getResponse().getResponseHeader()));
            if (context.getResponse().isFull()) {
                ctx.channel().write(Unpooled.wrappedBuffer(context.getResponse().getBody()));
                ctx.channel().attr(Consts.CHAIN_KEY).get().fireChain(context);
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.error(" error:{} ", e.getMessage(), e);
//        chain.handleThrowable(context);
        ctx.channel().close();
    }

}
