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

    /**
     * 这里表示请求头
     */
    @Getter
    @Setter
    private HttpRequest proxyHttpRequest;

    /**
     * Http 请求体
     * just for multipart
     */
    @Getter
    @Setter
    private LinkedList<HttpContent> proxyHttpContents;

    /**
     * 目标服务Channel
     */
    @Getter
    @Setter
    private volatile Channel clientChannel;

    /**
     * 目标服务返回的响应
     */
    @Getter
    @Setter
    private FullHttpResponse clientFullHttpResponse;

}
