package com.code.ting.netty.proxy.http.io.netty.context;


import com.code.ting.netty.proxy.http.chain.context.Response;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class NettyResponse implements Response {

    @Getter
    @Setter
    private String version;
    @Getter
    @Setter
    private String code;
    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private String responseLine;

    private Map<String, String> headers = Maps.newHashMap();

    public void addHead(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Setter
    @Getter
    private ByteBuf content;

    @Getter
    @Setter
    boolean full;

    @Override
    public byte[] getResponseHeader() {
        return new byte[0];
    }

    @Override
    public byte[] getBody() {
        return new byte[0];
    }


}
