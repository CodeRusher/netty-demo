package com.code.ting.netty.proxy.http.chain.processor;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.io.netty.client.ChannelPool;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;

public class NettyRouteProcessor implements Processor {


    @Override
    public void pre(Context context) {

    }

    @Override
    public boolean process(Context context) throws Throwable {
        SocketAddress address = new InetSocketAddress("", 8887);

        Future<Channel> f = ChannelPool.INSTANCE.acquireSync(address);
        context.getConnector().setClient(f.get());

        return true;
    }

    @Override
    public void after(Context context) {

    }


}
