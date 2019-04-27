package com.code.ting.netty.proxy.http.chain.proccesser;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RouteProcessor implements Processor {

    private Channel caller;

    public RouteProcessor() {
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup(1))
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new CallerHandler());
                }
            });
        ChannelFuture f = b.connect("", 8887);
        caller = f.channel();
    }

    @Override
    public void pre(Context context) {

    }

    @Override
    public boolean process(Context context) {
        Request request = context.getRequest();
        if (request.isFull()) {

        } else {
            request.setReceiver(caller);
        }

        return true;
    }

    @Override
    public void after(Context context) {

    }

    private static class CallerHandler extends ChannelInboundHandlerAdapter {

        private Context context;


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {

        }
    }
}
