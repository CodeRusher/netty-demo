package com.code.ting.netty.proxy.http.chain.context;


public interface Response<T> {

    void addHead(String key, String value);

    void writeBody(byte[] body);

    void direct(T data);
}
