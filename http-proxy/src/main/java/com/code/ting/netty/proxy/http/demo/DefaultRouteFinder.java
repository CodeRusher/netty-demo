package com.code.ting.netty.proxy.http.demo;


import com.code.ting.netty.proxy.http.chain.route.From;
import com.code.ting.netty.proxy.http.chain.route.RouteFinder;
import com.code.ting.netty.proxy.http.chain.route.To;

public class DefaultRouteFinder implements RouteFinder{

    @Override
    public To find(From from) {
        return null;
    }
}
