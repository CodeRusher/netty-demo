package com.code.ting.netty.proxy.http.chain.processor;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.client.ChannelPool;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NettyRouteProcessor implements Processor {


    @Override
    public void pre(Context context) {

    }

    @Override
    public boolean process(Context context) throws Throwable {
        SocketAddress address = new InetSocketAddress("", 8887);

        Future<Channel> f = ChannelPool.INSTANCE.acquireSync(address);

        f.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {
                Channel clientChannel = future.get();
                context.getConnector().setClient(clientChannel);
                clientChannel.attr(Consts.CONTEXT_KEY).set(context);
                // write header

                // wirte body

            }
        });

        // blocked???

        return true;
    }

    @Override
    public void after(Context context) {

    }


}
