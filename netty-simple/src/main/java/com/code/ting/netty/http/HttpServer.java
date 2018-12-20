package com.code.ting.netty.http;


import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import java.net.InetSocketAddress;

/**
 * see https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/http/helloworld/HttpHelloWorldServer.java
 */
public class HttpServer {

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        final AppHandler serverHandler = new AppHandler();
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG,1024);
            bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        // http编解码
                        pipeline.addLast("codec", new HttpServerCodec());
                        // http聚合: FullHttpRequest + FullHttpResponse
                        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                        // http压缩
                        pipeline.addLast("compressor", new HttpContentCompressor());
                        // 业务逻辑
                        pipeline.addLast("handler", new AppHandler());
                    }
                });

            ChannelFuture f = bootstrap.bind().sync();
            System.out.println("started...");
            f.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }
    }

    @Sharable
    private static class AppHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("received msg:");

            FullHttpRequest request = (FullHttpRequest) msg;
            System.out.println(request);

            FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("hello".getBytes()));

            response.headers().set(CONTENT_TYPE, "application/json");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            ctx.channel().write(response).addListener(ChannelFutureListener.CLOSE);
            ctx.flush();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 1) {
            port = Integer.parseInt(args[0]);
        }

        HttpServer server = new HttpServer(port);
        server.start();
    }

}
