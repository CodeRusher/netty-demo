package com.code.ting.netty.proxy.http.io.netty.context;


import com.code.ting.netty.proxy.http.chain.context.Request;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Setter;

public class DefaultRequest implements Request {

    @Setter
    private HttpRequest httpRequest;

    @Override
    public HttpMethod method() {
        return httpRequest.method();
    }

    @Override
    public String uri() {
        return httpRequest.uri();
    }

    @Override
    public void setUri(String uri) {
        httpRequest.setUri(uri);
    }

    @Override
    public HttpHeaders headers() {
        return httpRequest.headers();
    }

    @Override
    public boolean isFull() {
        return httpRequest instanceof FullHttpRequest;
    }

    @Override
    public byte[] getBody() {
        if (!isFull()) {
            throw new RuntimeException(" httpRequest is not FullHttpRequest, can use isFull() to check first");
        }
        return ((FullHttpRequest) httpRequest).content().array();
    }
}
