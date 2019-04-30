package com.code.ting.netty.proxy.http.demo;


import com.code.ting.netty.proxy.http.chain.route.From;
import com.code.ting.netty.proxy.http.chain.route.RouteFinder;
import com.code.ting.netty.proxy.http.chain.route.To;

public class DefaultRouteFinder implements RouteFinder{

    @Override
    public To find(From from) {
        To to = new To();
        to.setHost("localhost");
        to.setPort(8888);
        return to;
    }
}
