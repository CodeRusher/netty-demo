package com.code.ting.netty.proxy.http.chain.context;


import java.util.Map;

public interface Request {

    String getMethod();

    String getUrl();

    String getVersion();

    String getHeader(String key);

    Map<String, String> getHeaders();

    boolean isFull();

    byte[] getRequestHeader();

    byte[] getBody();

    void setReceiver(Object receiver);

    Object getReceiver();
}
