package com.code.ting.netty.proxy.http.util;


import lombok.Getter;
import lombok.Setter;

public class StatusCode {

    public static final StatusCode AUTH_STATUS_CODE = StatusCode.of("10000", " auth invalid ");
    public static final StatusCode SYS_STATUS_CODE = StatusCode.of("10001", " proxy error ");

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
