package com.code.ting.netty.proxy.http.io.netty.context;


import com.code.ting.netty.proxy.http.chain.context.Request;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import lombok.Setter;

public class DefaultRequest implements Request {

    @Setter
    private HttpRequest httpRequest;


    @Override
    public String getMethod() {
        return httpRequest.method().name();
    }

    @Override
    public String getUrl() {
        return httpRequest.uri();
    }

    @Override
    public String getHeader(String key) {
        return httpRequest.headers().get(key);
    }

    @Override
    public void setHeader(String key, String value) {
        httpRequest.headers().set(key,value);
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public boolean isFull() {
        return httpRequest instanceof FullHttpRequest;
    }

    @Override
    public byte[] getBody() {
        return ((FullHttpRequest) httpRequest).content().array();
    }
}