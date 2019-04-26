package com.code.ting.netty.proxy.http.io.netty;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class HttpProxyChannelInitializer extends ChannelInitializer<SocketChannel> {
    // read header
    // rewrite header
    // forward body(streaming)


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

    }
}
