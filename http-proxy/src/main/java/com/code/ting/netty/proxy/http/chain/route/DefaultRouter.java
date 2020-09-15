package com.code.ting.netty.proxy.http.chain.route;


import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.CancelReason;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.client.ChannelPool;
import com.code.ting.netty.proxy.http.util.StatusCode;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultRouter implements Router {

    private RouteFinder finder;

    public DefaultRouter(RouteFinder finder) {
        this.finder = finder;
    }

    @Override
    public YieldResult route(RouteContext context) throws Throwable {

        From from = From.of(context.getRequest(), null);
        To to = finder.find(from);
        if (to == null) {
            context.setCancelReason(CancelReason.of(StatusCode.ROUTE_FAIL.getCode(), "route not found"));
            return YieldResult.FAIL;
        }

        context.getRequest().headers().set("host", to.getHost());
        SocketAddress address = new InetSocketAddress(to.getHost(), to.getPort());

        // 获取client channel
        Future<Channel> clientChannelFuture = ChannelPool.INSTANCE.acquireSync(address);

        clientChannelFuture.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {

                Channel clientChannel = future.getNow();

                if (context.getRequest().isFull()) {
                    log.debug("{} route full", context.getId());

                    clientChannel.attr(Consts.CHAIN_KEY).set(context.getChain());
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                    System.out.println("begin : " + System.currentTimeMillis());
                    clientChannel.writeAndFlush(context.getConnector().getProxyHttpRequest());

                } else {
                    log.debug("{} route multipart", context.getId());

                    clientChannel.attr(Consts.CHAIN_KEY).set(context.getChain());
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);

                    // 建立连接后，先把Http请求头和缓存的Http内容都先发出去
                    // todo:需要优化
                    clientChannel.pipeline().context("aggregator")
                        .write(context.getConnector().getProxyHttpRequest());

                    synchronized (context) {
                        context.getConnector().setClientChannel(clientChannel);
                        LinkedList<HttpContent> contents = context.getConnector().getProxyHttpContents();
                        while (!contents.isEmpty()) {
                            HttpContent stageHttpContent = contents.removeFirst();
                            clientChannel.pipeline().context("aggregator").writeAndFlush(stageHttpContent);
                        }

                        log.debug("{} client set", context.getId());
                    }

                }
            }
        });

        log.debug("yield from router");
        return YieldResult.YIELD;
    }
}
