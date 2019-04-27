package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.context.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    private ProcessorChain chain;
    private Context context;
    private HttpParser httpParser = new HttpParser();

    public HttpProxyHandler(ProcessorChain chain) {
        this.chain = chain;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (httpParser.isComplete()) {
            Channel receiver = httpParser.getRequest().getReceiver();
            if (receiver != null) {
                receiver.writeAndFlush(msg);
            }
        }

        ByteBuf in = (ByteBuf) msg;
        httpParser.parse(in);

        if (!httpParser.isComplete()) {
            in.release();
            return;
        }

        // gen Context
        Context<Channel, Channel> context = new Context<>();
        context.setRequest(httpParser.getRequest());
        NettyResponse response = new NettyResponse();
        response.channel = ctx.channel();
        context.setResponse(response);

        // fire chain
        chain.fireChain(context);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.error(" error:{} ", e.getMessage(), e);
        chain.handleThrowable(context);
        ctx.channel().close();
    }

}
