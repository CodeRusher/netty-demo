package com.code.ting.netty.proxy.http.io.netty.proxy;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import java.net.InetSocketAddress;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Proxy {

    @Setter
    private FilterChain chain;

    private int port = 8081;

    public Proxy() {
    }

    public Proxy(int port) {
        this.port = port;
    }


    public void start() throws InterruptedException {

        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.attr(Consts.CHAIN_KEY).set(chain);
                        ch.pipeline().addLast("codec", new HttpServerCodec());
                        ch.pipeline().addLast(Consts.HTTP_PROXY_MULTIPART_HANDLER_KEY, new HttpProxyMultiPartHandler());
                        ch.pipeline().addLast(Consts.AGGREGATOR_HANDLER_KEY, new HttpObjectAggregator(200 * 1024 * 1024));
                        ch.pipeline().addLast(Consts.HTTP_PROXY_HANDLER_KEY, new HttpProxyHandler());
                    }
                });

            ChannelFuture f = bootstrap.bind().sync();
            log.info("started...");
            f.channel().closeFuture().sync();

        } finally {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }
    }


}
