package com.code.ting.netty.proxy.http.chain.context;


public interface Request<R> {

    String getMethod();

    String getUrl();

    String getHeader(String key);

    boolean isFull();

    byte[] getBody();

    void setReceiver(R receiver);

    R getReceiver();
}
