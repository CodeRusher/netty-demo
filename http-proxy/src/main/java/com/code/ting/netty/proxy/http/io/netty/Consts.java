package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import io.netty.util.AttributeKey;

public class Consts {

    public static final AttributeKey<FilterChain> CHAIN_KEY = AttributeKey.newInstance("chain");
    public static final AttributeKey<RouteContext> CONTEXT_KEY = AttributeKey.newInstance("route_context");

    public static final String HTTP_PROXY_MULTIPART_HANDLER_KEY = "netty_handler_multipart";
    public static final String HTTP_PROXY_HANDLER_KEY = "netty_handler_proxy";
    public static final String AGGREGATOR_HANDLER_KEY = "netty_handler_aggregator";


}
