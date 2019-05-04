package com.code.ting.netty.proxy.http.chain.context;


import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public interface Response {

    HttpResponseStatus status();

    HttpHeaders headers();

    boolean isFull();

    byte[] getBody();

}
