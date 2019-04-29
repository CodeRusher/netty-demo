package com.code.ting.netty.proxy.http.chain.context;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import java.util.HashMap;

public interface Context {

    FilterChain getChain();

    Request getRequest();

    Response getResponse();

    void setResult(Result result);

    Connector getConnector();

    void setStatus(Status status);

    Status getStatus();

    HashMap<Class<Process>, Object> getData();

    Long getId();


}
