package com.code.ting.netty.proxy.http.chain.route;

import lombok.Data;

@Data
public class From {

    private String uri;
    private String service;
    private String host;
    private int port;
}
