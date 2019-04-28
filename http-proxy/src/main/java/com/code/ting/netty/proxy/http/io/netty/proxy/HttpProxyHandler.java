package com.code.ting.netty.proxy.http.io.netty.proxy;


import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import com.code.ting.netty.proxy.http.io.netty.context.NettyResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        HttpRequestParser httpParser = ctx.channel().attr(Consts.REQUEST_PARSER_KEY).get();
        // the fisrt msg of the Channel
        if (httpParser == null) {
            httpParser = new HttpRequestParser();
            httpParser.setContext(new NettyContext());
            ctx.channel().attr(Consts.REQUEST_PARSER_KEY).set(httpParser);
        }

        // New http request coming
        if (httpParser.getContext().getStatus() == Status.RESPONSE_COMPLETED) {
            httpParser = new HttpRequestParser();
            ctx.channel().attr(Consts.REQUEST_PARSER_KEY).set(httpParser);
            httpParser.setContext(new NettyContext());
        }

        NettyContext context = httpParser.getContext();

        ByteBuf in = (ByteBuf) msg;
        /*---------------------  direct forward  ---------------------------*/
        if (httpParser.getContext().getStatus() == Status.REQUEST_HEADER_READ) {
            if (!httpParser.getRequest().isFull()) {
                Connector connector = httpParser.getContext().getConnector();
                if (connector.getClient() instanceof Channel) {
                    int readableBytes = in.readableBytes();
                    if (readableBytes + httpParser.getBodyReadedLength() >= httpParser.getContentLength()) {
                        ((Channel) (connector.getClient())).writeAndFlush(in);
                        httpParser.getContext().setStatus(Status.REQUEST_COMPLETED);
                    } else {
                        ((Channel) (connector.getClient())).write(in);
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

        if ((context.getStatus() == Status.NEW) ||
            (context.getRequest().isFull() && context.getStatus() == Status.REQUEST_HEADER_READ)) {
            in.release();
            return;
        }

        /*---------------------  chain  ---------------------------*/
        // disable until client is ready
        if (!context.getRequest().isFull()) {
            ctx.channel().config().setAutoRead(false);
        }
        // gen NettyContext
        context.setRequest(httpParser.getRequest());
        context.setResponse(new NettyResponse());
        Connector connector = new Connector();
        connector.setProxy(ctx.channel());
        context.setConnector(connector);

        // fire chain
        ctx.channel().attr(Consts.CHAIN_KEY).get().fireChain(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.error(" error:{} ", e.getMessage(), e);
//        chain.handleThrowable(context);
        ctx.channel().close();
    }

}
