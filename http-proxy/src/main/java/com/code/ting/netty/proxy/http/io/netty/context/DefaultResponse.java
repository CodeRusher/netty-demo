package com.code.ting.netty.proxy.http.io.netty.context;


import com.code.ting.netty.proxy.http.chain.context.Response;
import io.netty.handler.codec.http.FullHttpResponse;
import java.util.Map;
import lombok.Setter;

public class DefaultResponse implements Response {

    @Setter
    private FullHttpResponse fullHttpResponse;

    @Override
    public String getVersion() {
        return fullHttpResponse.protocolVersion().text();
    }

    @Override
    public Integer getCode() {
        return fullHttpResponse.status().code();
    }

    @Override
    public String getReasonPhrase() {
        return fullHttpResponse.status().reasonPhrase();
    }

    @Override
    public void addHeader(String key, String value) {
        fullHttpResponse.headers().set(key,value);
    }

    @Override
    public String getHeader(String key) {
        return fullHttpResponse.headers().get(key);
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public byte[] getBody() {
        return fullHttpResponse.content().array();
    }
}
