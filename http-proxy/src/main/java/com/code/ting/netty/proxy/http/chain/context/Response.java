package com.code.ting.netty.proxy.http.chain.context;


import java.util.Map;

public interface Response {

    String getVersion();

    Integer getCode();

    String getReasonPhrase();

    String getHeader(String key);

    Map<String, String> getHeaders();

    boolean isFull();

    byte[] getBody();

}
