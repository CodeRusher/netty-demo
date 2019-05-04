package com.code.ting.netty.proxy.http.util;


import lombok.Getter;
import lombok.Setter;

public class StatusCode {

    public static final StatusCode SYS_ERROR = StatusCode.of("10000", " proxy error ");
    public static final StatusCode AUTH_FAIL = StatusCode.of("10001", " auth fail ");
    public static final StatusCode ROUTE_FAIL = StatusCode.of("10002", " route fail ");

    @Getter
    private String code;
    @Setter
    @Getter
    private String msg;

    public StatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static StatusCode of(String code, String msg) {
        return new StatusCode(code, msg);
    }


}
