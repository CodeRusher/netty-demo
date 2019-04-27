package com.code.ting.netty.proxy.http.chain.context;


import com.google.common.collect.Maps;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

public class Context<R, S> {

    @Getter
    @Setter
    private Request<R> request;
    @Getter
    @Setter
    private Response<S> response;

    @Getter
    private HashMap<Class<Process>, Object> data = Maps.newHashMap();

}
