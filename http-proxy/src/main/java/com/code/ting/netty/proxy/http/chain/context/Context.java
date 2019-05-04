package com.code.ting.netty.proxy.http.chain.context;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import io.netty.util.AttributeMap;

public interface Context extends AttributeMap {

    FilterChain getChain();

    Long getId();

    Request getRequest();

    Response getResponse();

    void setCancelReason(CancelReason cancelReason);

}
