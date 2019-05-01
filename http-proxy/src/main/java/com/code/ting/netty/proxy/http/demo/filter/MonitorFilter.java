package com.code.ting.netty.proxy.http.demo.filter;


import com.code.ting.netty.proxy.http.chain.Filter;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.FilterContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorFilter implements Filter {

    private final AttributeKey<Long> startAt = AttributeKey.newInstance("startAt");

    @Override
    public YieldResult pre(FilterContext context) throws Throwable {
        context.attr(startAt).set(System.currentTimeMillis());
        return YieldResult.SUCCESS;
    }

    @Override
    public void after(FilterContext context) {

        log.info("time: {} ", System.currentTimeMillis() - context.attr(startAt).get());

    }
}
