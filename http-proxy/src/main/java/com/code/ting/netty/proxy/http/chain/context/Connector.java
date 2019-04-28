package com.code.ting.netty.proxy.http.chain.context;


import lombok.Getter;
import lombok.Setter;

public class Connector {
    @Getter
    @Setter
    private Object proxy;

    @Getter
    @Setter
    private Object client;

}
