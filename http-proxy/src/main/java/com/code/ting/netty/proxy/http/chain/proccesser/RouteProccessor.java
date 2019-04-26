package com.code.ting.netty.proxy.http.chain.proccesser;


import com.code.ting.netty.proxy.http.chain.Proccesser;
import com.code.ting.netty.proxy.http.chain.context.Context;

public class RouteProccessor implements Proccesser {

    @Override
    public boolean proccess(Context context) {
        return false;
    }
}
