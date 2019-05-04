package com.code.ting.netty.proxy.http.chain.context;


import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public interface Request {

    HttpMethod method();

    String uri();

    void setUri(String uri);

    HttpHeaders headers();

    boolean isFull();

    byte[] getBody();

}
