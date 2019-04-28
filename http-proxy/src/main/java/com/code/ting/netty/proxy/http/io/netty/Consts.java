package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.context.Context;
import io.netty.util.AttributeKey;

public class Consts {

    public static final AttributeKey<Context> CONTEXT_KEY = AttributeKey.newInstance("context");


}
