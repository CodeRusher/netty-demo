package com.code.ting.netty.proxy.http.chain.context;


public interface RouteContext extends FilterContext, Context {

    Connector getConnector();

    void setStatus(Status status);

    Status getStatus();
}
