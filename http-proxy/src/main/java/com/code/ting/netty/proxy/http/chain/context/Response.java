package com.code.ting.netty.proxy.http.chain.context;


import java.util.Map;

public interface Response {

    String getVersion();

    String getCode();

    String getStatus();

    String getHeader(String key);

    Map<String, String> getHeaders();

    boolean isFull();

    byte[] getResponseHeader();

    byte[] getBody();

}
