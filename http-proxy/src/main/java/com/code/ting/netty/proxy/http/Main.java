package com.code.ting.netty.proxy.http;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.chain.route.DefaultRouter;
import com.code.ting.netty.proxy.http.demo.DefaultRouteFinder;
import com.code.ting.netty.proxy.http.demo.filter.MonitorFilter;
import com.code.ting.netty.proxy.http.io.netty.proxy.Proxy;
import com.code.ting.netty.proxy.http.demo.filter.AddResponseHeaderFilter;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        FilterChain chain = buildChain();

        Proxy proxy = new Proxy();
        proxy.setChain(chain);
        proxy.start();
    }

    private static FilterChain buildChain() {
        FilterChain chain = new FilterChain(new DefaultRouter(new DefaultRouteFinder()));
        chain.addFilter(new AddResponseHeaderFilter());
        chain.addFilter(new MonitorFilter());
//        chain.addFilter(new AuthFilter());
        return chain;
    }

}
