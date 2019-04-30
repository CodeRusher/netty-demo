package com.code.ting.netty.proxy.http.demo.filter;


import com.code.ting.netty.proxy.http.chain.Filter;
import com.code.ting.netty.proxy.http.chain.YieldResult;
import com.code.ting.netty.proxy.http.chain.context.FilterContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorFilter implements Filter {

    Map<Long, Long> times = new ConcurrentHashMap<>();

    @Override
    public YieldResult pre(FilterContext context) throws Throwable {
        times.put(context.getId(), System.currentTimeMillis());
        return YieldResult.SUCCESS;
    }

    @Override
    public void after(FilterContext context) {
        try {
            log.info("time: {} ", System.currentTimeMillis() - times.get(context.getId()));
        } finally {
            times.remove(context.getId());
        }
    }
}
