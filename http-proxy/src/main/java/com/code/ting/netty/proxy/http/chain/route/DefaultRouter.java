package com.code.ting.netty.proxy.http.chain.route;


import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.Result;
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
        if (to == null) {
            context.setResult(Result.of("11000", "route not found"));
            return YieldResult.FAIL;
        }

        context.getRequest().setHeader("host", to.getHost());
        SocketAddress address = new InetSocketAddress(to.getHost(), to.getPort());

        Future<Channel> clientChannelFuture = ChannelPool.INSTANCE.acquireSync(address);

        clientChannelFuture.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {

                Channel clientChannel = future.getNow();

                if (context.getRequest().isFull()) {

                    System.out.println("route full");

                    clientChannel.attr(Consts.CHAIN_KEY).set(context.getChain());
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                    System.out.println("begin : " + System.currentTimeMillis());
                    clientChannel.writeAndFlush(context.getConnector().getProxyHttpRequest());

                } else {

                    System.out.println("route multipart");

                    clientChannel.attr(Consts.CHAIN_KEY).set(context.getChain());
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                    clientChannel.pipeline().context("aggregator").write(context.getConnector().getProxyHttpRequest());
//                    clientChannel.write(context.getConnector().getProxyHttpRequest());
                    context.getConnector().setClientChannel(clientChannel);
                }
            }
        });

        return YieldResult.YIELD;
    }
}
