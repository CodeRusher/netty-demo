package com.code.ting.netty.proxy.http.chain.proccesser;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.context.Context;
import org.apache.commons.lang3.StringUtils;

public class AuthProcessor implements Processor {

    @Override
    public void pre(Context context) {

    }

    @Override
    public boolean process(Context context) {
        if (StringUtils.isBlank(context.getRequest().getHead("auth"))) {
            return false;
        }

        return true;
    }

    @Override
    public void after(Context context) {

    }
}