package com.code.ting.netty.proxy.http.io.netty.client;


import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;

public class DefaultChannelPoolHandler implements ChannelPoolHandler {

    @Override
    public void channelReleased(Channel ch) throws Exception {

    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {

    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        NioSocketChannel channel = (NioSocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpContentDecompressor());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 200));
        pipeline.addLast(new ChunkedWriteHandler());

        pipeline.addLast(new ClientHandler());
    }
}
