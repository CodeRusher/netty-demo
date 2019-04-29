package com.code.ting.netty.proxy.http.chain.context;


import java.util.Map;

public interface Response {

    String getVersion();

    Integer getCode();

    String getReasonPhrase();

    void addHeader(String key, String value);

    String getHeader(String key);

    Map<String, String> getHeaders();

    boolean isFull();

    byte[] getBody();

}
