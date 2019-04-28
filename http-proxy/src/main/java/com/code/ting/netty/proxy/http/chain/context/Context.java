package com.code.ting.netty.proxy.http.chain.context;


import java.util.HashMap;

public interface Context {
    Request getRequest();

    Response getResponse();

    Connector getConnector();

    Status getStatus();

    HashMap<Class<Process>, Object> getData();

}
