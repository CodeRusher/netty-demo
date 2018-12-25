package com.code.ting.netty.udp;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class UdpServer {

    EventLoopGroup group;
    Bootstrap bootstrap;

    InetSocketAddress address;
    Channel channel;


    public UdpServer(InetSocketAddress address) {
        this.address = address;
    }

    public void start() throws InterruptedException {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
            .channel(NioDatagramChannel.class)
            .option(ChannelOption.SO_BROADCAST, true)
            .localAddress(address)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new EventDecoder());
                    pipeline.addLast(new LogInboundHandler());

                }
            });

        channel = bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() throws InterruptedException {
        channel.closeFuture().sync();
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        UdpServer server = new UdpServer(new InetSocketAddress(9999));
        server.start();

        TimeUnit.SECONDS.sleep(60);

        server.stop();
    }


    private static class LogInboundHandler extends SimpleChannelInboundHandler<Event> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Event msg) throws Exception {
            System.out.println("get tag:" + msg.getTag() + "  msg:" + msg.getMsg());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

}
