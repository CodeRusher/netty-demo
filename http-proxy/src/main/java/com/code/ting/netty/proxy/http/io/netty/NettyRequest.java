package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.context.Request;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class NettyRequest implements Request<Channel> {

    @Getter
    @Setter
    private String method;
    @Getter
    @Setter
    private String url;
    @Setter
    private String requestLine;

    @Setter
    private ByteBuf body;

    @Getter
    @Setter
    private boolean full;

    private Map<String, String> headers = Maps.newHashMap();

    public void addHead(String key, String value) {
        headers.put(key, value);
    }


    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }


    @Override
    public byte[] getBody() {
        return new byte[0];
    }

    @Getter
    @Setter
    private Channel receiver;

}
