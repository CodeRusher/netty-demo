package com.code.ting.netty.proxy.http.io.netty.context;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.ContextIdGenerator;
import com.code.ting.netty.proxy.http.chain.context.Request;
import com.code.ting.netty.proxy.http.chain.context.Response;
import com.code.ting.netty.proxy.http.chain.context.Result;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.google.common.collect.Maps;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.DefaultAttributeMap;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

public class DefaultContext extends DefaultAttributeMap implements RouteContext {

    public DefaultContext(FilterChain chain){
        this.chain = chain;
    }

    @Getter
    @Setter
    private Request request;
    @Getter
    @Setter
    private Response response;
    @Getter
    @Setter
    private Connector connector;
    @Getter
    @Setter
    private Result result;

    @Getter
    @Setter
    private Status status = Status.NEW;

    @Getter
    FilterChain chain;

    @Getter
    Long id = ContextIdGenerator.genId();

}
