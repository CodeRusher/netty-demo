package com.code.ting.netty.proxy.http.chain;

import lombok.Data;

@Data
public class YieldResult {

    boolean yield = false;
    boolean success = true;

    private YieldResult(boolean yield, boolean success) {
        this.yield = yield;
        this.success = success;
    }

    public static final YieldResult SUCCESS = new YieldResult(false, true);
    public static final YieldResult YIELD = new YieldResult(true, true);
    public static final YieldResult FAIL = new YieldResult(false, false);

}
