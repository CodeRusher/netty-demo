package com.code.ting.netty.proxy.http.chain.processor;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.Context;
import org.apache.commons.lang3.StringUtils;

public class AuthProcessor implements Processor {

    @Override
    public void pre(Context context) {

    }

    @Override
    public YieldResult process(Context context) {
        if (StringUtils.isBlank(context.getRequest().getHeader("auth"))) {
            return YieldResult.FAIL;
        }

        return YieldResult.SUCCESS;
    }

    @Override
    public void after(Context context) {

    }
}
