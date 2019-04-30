package com.code.ting.netty.proxy.http.io.netty.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import java.net.SocketAddress;

public class ChannelPool {

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup workers = new NioEventLoopGroup(4);
    private ChannelPoolMap<SocketAddress, SimpleChannelPool> defaultPoolMap;
    private ChannelPoolMap<SocketAddress, SimpleChannelPool> multiPartPoolMap;

    public static final ChannelPool INSTANCE = new ChannelPool();

    private ChannelPool() {
        bootstrap
            .group(workers)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 200)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, true);

        defaultPoolMap = new AbstractChannelPoolMap<SocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(SocketAddress socketAddress) {
                return new FixedChannelPool(
                    bootstrap.remoteAddress(socketAddress),
                    new DefaultChannelPoolHandler(),
                    200);
            }
        };
        multiPartPoolMap = new AbstractChannelPoolMap<SocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(SocketAddress socketAddress) {
                return new FixedChannelPool(
                    bootstrap.remoteAddress(socketAddress),
                    new MultipartChannelPoolHandler(),
                    200);
            }
        };
    }

    public Future<Channel> acquireSync(SocketAddress address) throws InterruptedException {
        final SimpleChannelPool pool = defaultPoolMap.get(address);
        return pool.acquire().sync();
    }

    public Future<Channel> acquireMultiPartChannelSync(SocketAddress address) throws InterruptedException {
        final SimpleChannelPool pool = multiPartPoolMap.get(address);
        return pool.acquire().sync();
    }

}
