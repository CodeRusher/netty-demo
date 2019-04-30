package com.code.ting.netty.proxy.http.chain.route;


import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.client.ChannelPool;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class DefaultRouter implements Router {

    private RouteFinder finder;

    public DefaultRouter(RouteFinder finder) {
        this.finder = finder;
    }

    @Override
    public YieldResult route(RouteContext context) throws Throwable {

        From from = new From();
        To to = finder.find(from);
        context.getRequest().setHeader("host", to.getHost());
        SocketAddress address = new InetSocketAddress(to.getHost(), to.getPort());

        Future<Channel> clientChannelFuture = context.getRequest().isFull() ?
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
