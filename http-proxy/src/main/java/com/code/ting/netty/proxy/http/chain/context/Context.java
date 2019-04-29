package com.code.ting.netty.proxy.http.chain.context;


import java.util.HashMap;

public interface Context {

    Request getRequest();

    Response getResponse();

    Connector getConnector();

    void setStatus(Status status);

    Status getStatus();

    HashMap<Class<Process>, Object> getData();

    Long getId();

    void setId(Long id);

    void setResult(Result result);

}
