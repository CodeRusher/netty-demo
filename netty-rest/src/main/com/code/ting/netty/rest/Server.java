package com.code.ting.netty.rest;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new DispatcherServletChannelInitializer(
                    new String[]{"com.code.ting.netty.rest"}));

            ChannelFuture f = bootstrap.bind().sync();
            log.info("server started...");
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
