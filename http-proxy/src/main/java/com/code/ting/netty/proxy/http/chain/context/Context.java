package com.code.ting.netty.proxy.http.chain.context;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import java.util.HashMap;

public interface Context {

    FilterChain getChain();

    Long getId();

    Request getRequest();

    Response getResponse();

    void setResult(Result result);

    HashMap<Class<Process>, Object> getData();



}
