package com.code.ting.netty.proxy.http.chain.context;


import com.google.common.collect.Maps;
import java.util.HashMap;
import lombok.Getter;

public class Context<T> {

    @Getter
    private Request<T> request;
    @Getter
    private Response<T> response;

    @Getter
    private HashMap<Class,Object> data = Maps.newHashMap();

    private boolean throwed = false;

}
