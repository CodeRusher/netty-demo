package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.ProccesserChain;
import com.code.ting.netty.proxy.http.chain.proccesser.AuthProccessor;
import com.code.ting.netty.proxy.http.chain.proccesser.RouteProccessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;

public class Proxy {

    private int port = 8081;

    public Proxy() {
    }

    public Proxy(int port) {
        this.port = port;
    }


    public void start() throws InterruptedException {
        ProccesserChain chain = new ProccesserChain();

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
                        ch.pipeline().addLast(new HttpProxyHandler(chain));
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

    private ProccesserChain buildChain(){
        ProccesserChain chain = new ProccesserChain();
        chain.addProccesser(new AuthProccessor());
        chain.addProccesser(new RouteProccessor());

        return chain;
    }

    public static void main(String[] args) throws InterruptedException {
        Proxy proxy = new Proxy();
        proxy.start();
    }

}
