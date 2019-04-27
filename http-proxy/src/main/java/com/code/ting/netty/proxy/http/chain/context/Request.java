package com.code.ting.netty.proxy.http.chain.context;


public interface Request<T> {

    String getMethod();

    String getUrl();

    String getHead(String key);

    byte[] getBody();

    T direct();
}
