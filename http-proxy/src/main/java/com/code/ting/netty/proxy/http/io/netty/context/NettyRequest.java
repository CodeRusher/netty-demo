package com.code.ting.netty.proxy.http.io.netty.context;


import com.code.ting.netty.proxy.http.chain.context.Request;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class NettyRequest implements Request {

    @Getter
    @Setter
    private String method;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String version;
    @Setter
    private String requestLine;

    @Setter
    @Getter
    private ByteBuf content;

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
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public byte[] getRequestHeader() {
        StringBuilder s = new StringBuilder(requestLine).append("\r\n");
        headers.forEach((k, v) -> s.append(k).append(":").append(v).append("\r\n"));
        s.append("\r\n");
        return s.toString().getBytes();
    }


    @Override
    public byte[] getBody() {
        return content.array();
    }


}
