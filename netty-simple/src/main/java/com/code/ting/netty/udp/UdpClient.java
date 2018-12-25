package com.code.ting.netty.udp;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class UdpClient {

    EventLoopGroup group;
    Bootstrap bootstrap;

    public void execute() throws InterruptedException {
        try {
            group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
//                .handler(new EventEncoder(new InetSocketAddress("127.0.0.1", 9999)));
                .handler(new EventEncoder(new InetSocketAddress("255.255.255.255", 9999)));

            Channel ch = bootstrap.bind(0).sync().channel();
            Event e = new Event();
            e.setMsg("hello");
            e.setTag(0);
            ch.writeAndFlush(e);

        } finally {

        }
    }

    public void stop(){
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        UdpClient client = new UdpClient();
        client.execute();
        TimeUnit.SECONDS.sleep(1);
        client.stop();
    }


}
