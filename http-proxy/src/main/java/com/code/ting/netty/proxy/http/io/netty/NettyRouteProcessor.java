package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.io.netty.client.ChannelPool;
import com.code.ting.netty.proxy.http.io.netty.client.HttpResponseParser;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NettyRouteProcessor implements Processor {

    private ProcessorChain chain;

    public NettyRouteProcessor(ProcessorChain chain) {
        this.chain = chain;
    }

    @Override
    public void pre(Context context) {

    }

    @Override
    public YieldResult process(Context context) throws Throwable {
        SocketAddress address = new InetSocketAddress("127.0.0.1", 8888);

        Future<Channel> clientChannelFuture = ChannelPool.INSTANCE.acquireSync(address);

        clientChannelFuture.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {
                Channel clientChannel = future.get();
                context.getConnector().setClient(clientChannel);
                HttpResponseParser httpResponseParser = new HttpResponseParser();
                httpResponseParser.setContext((NettyContext) context);
                clientChannel.attr(Consts.RESPONSE_PARSER_KEY).set(httpResponseParser);
                clientChannel.attr(Consts.CHAIN_KEY).set(chain);
                // write header
                clientChannel.write(Unpooled.wrappedBuffer(context.getRequest().getRequestHeader()));
                // write body
                if (context.getRequest().isFull()) {
                    clientChannel.writeAndFlush(Unpooled.wrappedBuffer(context.getRequest().getBody()));
                } else {
                    Channel proxyChannel = (Channel) context.getConnector().getProxy();
                    proxyChannel.config().setAutoRead(true);
                }
            }
        });

        return YieldResult.YIELD;
    }

    @Override
    public void after(Context context) {

    }


}
