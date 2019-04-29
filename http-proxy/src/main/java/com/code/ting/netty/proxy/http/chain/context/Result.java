package com.code.ting.netty.proxy.http.chain.context;


import lombok.Data;

@Data
public class Result {

    private String code;
    private String msg;


    public static Result of(String code, String msg) {
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        return result;
    }

}
