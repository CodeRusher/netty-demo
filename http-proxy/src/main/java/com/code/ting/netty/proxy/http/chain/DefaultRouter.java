package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.client.ChannelPool;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class DefaultRouter implements Router{

    @Override
    public YieldResult route(Context context) throws Throwable {
        String host = "localhost";
        int port = 8888;
        context.getRequest().setHeader("host",host);
        SocketAddress address = new InetSocketAddress(host, port);

        Future<Channel> clientChannelFuture = context.getRequest().isFull()?
            ChannelPool.INSTANCE.acquireSync(address) :
            ChannelPool.INSTANCE.acquireMultiPartChannelSync(address);

        clientChannelFuture.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {

                Channel clientChannel = future.getNow();

                if (context.getRequest().isFull()) {

                    clientChannel.attr(Consts.CHAIN_KEY).set(context.getChain());
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                    clientChannel.writeAndFlush(context.getConnector().getProxyHttpRequest());

                } else {

                    clientChannel.attr(Consts.CHAIN_KEY).set(context.getChain());
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                    clientChannel.write(context.getConnector().getProxyHttpRequest());
                    context.getConnector().setClientChannel(clientChannel);
                    context.getConnector().getProxyChannel().config().setAutoRead(true);
                }
            }
        });

        return YieldResult.YIELD;
    }
}
