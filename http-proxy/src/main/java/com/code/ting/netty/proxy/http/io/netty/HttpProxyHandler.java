package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.context.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    private ProcessorChain chain;
    private Context context;
    private HttpRequestParser httpParser = new HttpRequestParser();

    public HttpProxyHandler(ProcessorChain chain) {
        this.chain = chain;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (httpParser.isComplete()) {
            Object receiver = httpParser.getRequest().getReceiver();
            if (receiver != null) {
                if (receiver instanceof Socket) {
//                    Socket socket = (Socket)receiver;
//                    socket.getOutputStream().write(((ByteBuf)msg).array());

                }
            }
        }

        ByteBuf in = (ByteBuf) msg;
        httpParser.parse(in);

        if (!httpParser.isComplete()) {
            in.release();
            return;
        }

        // gen Context
        Context context = new Context();
        context.setRequest(httpParser.getRequest());
        NettyResponse response = new NettyResponse();
        response.sender = ctx.channel();
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
