package com.code.ting.netty.proxy.http.chain.context;


public interface Response<S> {

    String getHeader(String key);

    void writeBody(byte[] body);

    boolean isFull();

    byte[] getBody();

    S getSender();

}
