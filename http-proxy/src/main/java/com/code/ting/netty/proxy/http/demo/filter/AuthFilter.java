package com.code.ting.netty.proxy.http.demo.filter;


import com.code.ting.netty.proxy.http.chain.Filter;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.FilterContext;
import com.code.ting.netty.proxy.http.chain.context.CancelReason;
import org.apache.commons.lang3.StringUtils;

public class AuthFilter implements Filter {

    @Override
    public YieldResult pre(FilterContext context) {
        if (StringUtils.isBlank(context.getRequest().getHeader("auth"))) {
            context.setCancelReason(CancelReason.of("01", "auth error"));
            return YieldResult.FAIL;
        }

        return YieldResult.SUCCESS;
    }

    @Override
    public void after(FilterContext context) {

    }
}
