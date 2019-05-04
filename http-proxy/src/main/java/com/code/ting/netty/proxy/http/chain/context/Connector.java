package com.code.ting.netty.proxy.http.chain.context;


import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import java.util.LinkedList;
import lombok.Getter;
import lombok.Setter;

public class Connector {

    @Getter
    @Setter
    private Channel proxyChannel;

    @Getter
    @Setter
    private HttpRequest proxyHttpRequest;

    /**
     * for multipart
     */
    @Getter
    @Setter
    private LinkedList<HttpContent> proxyHttpContents;

    @Getter
    @Setter
    private volatile Channel clientChannel;

    @Getter
    @Setter
    private FullHttpResponse clientFullHttpResponse;

}
