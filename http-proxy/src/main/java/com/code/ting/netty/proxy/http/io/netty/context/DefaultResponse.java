package com.code.ting.netty.proxy.http.io.netty.context;


import com.code.ting.netty.proxy.http.chain.context.Response;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Setter;

public class DefaultResponse implements Response {

    @Setter
    private FullHttpResponse fullHttpResponse;

    @Override
    public HttpResponseStatus status() {
        return fullHttpResponse.status();
    }

    @Override
    public HttpHeaders headers() {
        return fullHttpResponse.headers();
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
