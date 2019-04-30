package com.code.ting.netty.proxy.http.chain.context;


import java.util.Map;

public interface Request {

    String getMethod();

    String getUrl();

    String getHeader(String key);

    void setHeader(String key,String value);

    Map<String, String> getHeaders();

    boolean isFull();

    byte[] getBody();

}
