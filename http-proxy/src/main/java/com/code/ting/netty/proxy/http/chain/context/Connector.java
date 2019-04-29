package com.code.ting.netty.proxy.http.chain.context;


import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.Getter;
import lombok.Setter;

public class Connector {

    @Getter
    @Setter
    private Channel proxyChannel;

    @Getter
    @Setter
    private HttpRequest proxyHttpRequest;

    @Getter
    @Setter
    private Channel clientChannel;

    @Getter
    @Setter
    private FullHttpResponse clientHttpResponse;

}
