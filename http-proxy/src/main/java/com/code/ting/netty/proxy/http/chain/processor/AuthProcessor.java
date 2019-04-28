package com.code.ting.netty.proxy.http.chain.processor;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.io.netty.NettyContext;
import org.apache.commons.lang3.StringUtils;

public class AuthProcessor implements Processor {

    @Override
    public void pre(NettyContext context) {

    }

    @Override
    public boolean process(NettyContext context) {
        if (StringUtils.isBlank(context.getRequest().getHeader("auth"))) {
            return false;
        }

        return true;
    }

    @Override
    public void after(NettyContext context) {

    }
}
