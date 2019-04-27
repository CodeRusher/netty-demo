package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.context.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Setter;

public class NettyResponse implements Response<ByteBuf>{

    @Setter
    Channel channel;


    @Override
    public void addHead(String key, String value) {

    }

    @Override
    public void writeBody(byte[] body) {


    }

    @Override
    public void direct(ByteBuf data) {
        channel.writeAndFlush(data);
    }
}
