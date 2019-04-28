package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.io.netty.client.HttpResponseParser;
import com.code.ting.netty.proxy.http.io.netty.proxy.HttpRequestParser;
import io.netty.util.AttributeKey;

public class Consts {

    public static final AttributeKey<ProcessorChain> CHAIN_KEY = AttributeKey.newInstance("chain");
    public static final AttributeKey<HttpRequestParser> REQUEST_PARSER_KEY = AttributeKey.newInstance("request_parser");
    public static final AttributeKey<HttpResponseParser> RESPONSE_PARSER_KEY = AttributeKey.newInstance("response_parser");


}
