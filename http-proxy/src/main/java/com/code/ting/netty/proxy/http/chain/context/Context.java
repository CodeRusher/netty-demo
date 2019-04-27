package com.code.ting.netty.proxy.http.chain.context;


import com.google.common.collect.Maps;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

public class Context {

    @Getter
    @Setter
    private Request request;
    @Getter
    @Setter
    private Response response;

    @Getter
    private HashMap<Class<Process>, Object> data = Maps.newHashMap();

}
