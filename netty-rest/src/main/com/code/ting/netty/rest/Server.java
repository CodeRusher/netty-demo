package com.code.ting.netty.rest;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class Server {

    private final int port;
    private SpringMvcChannelHandler mvcChannelHandler;

    public Server(int port) {
        this.port = port;
        mvcChannelHandler = new SpringMvcChannelHandler();
    }

    public void run() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("decoder", new HttpRequestDecoder());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                        pipeline.addLast("encoder", new HttpResponseEncoder());
                        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                        pipeline.addLast("mvc", mvcChannelHandler);
                    }
                });

            ChannelFuture f = bootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        int port = 8080;
        if (args.length > 1) {
            port = Integer.parseInt(args[0]);
        }

        new Server(port).run();
    }

}
