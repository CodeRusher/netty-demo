package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.io.netty.client.ChannelPool;
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

                if (context.getRequest().isFull()) {

                    clientChannel.attr(Consts.CHAIN_KEY).set(chain);
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                    clientChannel.writeAndFlush(context.getConnector().getProxyHttpRequest());

                } else {

                    clientChannel.attr(Consts.CHAIN_KEY).set(chain);
                    clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                    clientChannel.write(context.getConnector().getProxyHttpRequest());
                    context.getConnector().setClientChannel(clientChannel);
                    context.getConnector().getProxyChannel().config().setAutoRead(true);

                }
            }
        });

        return YieldResult.YIELD;
    }

    @Override
    public void after(Context context) {

    }


}
