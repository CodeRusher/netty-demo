package com.code.ting.netty.proxy.http.chain.context;


import lombok.Data;

@Data
public class CancelReason {

    public static final CancelReason DEFAULT_CANCEL_REASON = CancelReason.of("10002", "");

    private String code;
    private String msg;


    public static CancelReason of(String code, String msg) {
        CancelReason cancelReason = new CancelReason();
        cancelReason.code = code;
        cancelReason.msg = msg;
        return cancelReason;
    }

}
