package com.code.ting.netty.proxy.http.chain;


import com.code.ting.netty.proxy.http.chain.context.Context;

public interface Proccesser {

    void pre(Context context);

    boolean proccess(Context context);

    void after(Context context);

}
