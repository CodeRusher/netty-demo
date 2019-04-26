package com.code.ting.netty.proxy.http.chain.context;


import lombok.Getter;

public class Context<T> {

    @Getter
    Request<T> request;
    @Getter
    Response<T> response;

}
