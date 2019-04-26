package com.code.ting.netty.proxy.http.chain.context;


public interface Request<T> {

    String getHead(String key);

    byte[] getBody();

    T direct();

}
