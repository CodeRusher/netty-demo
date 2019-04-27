package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.context.Request;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class NettyRequest implements Request<ByteBuf> {

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

    private Map<String, String> headers = Maps.newHashMap();

    public void addHead(String key, String value) {
        headers.put(key, value);
    }


    @Override
    public String getHead(String key) {
        return headers.get(key);
    }

    @Override
    public byte[] getBody() {
        return new byte[0];
    }

    @Override
    public ByteBuf direct() {
        return null;
    }
}
