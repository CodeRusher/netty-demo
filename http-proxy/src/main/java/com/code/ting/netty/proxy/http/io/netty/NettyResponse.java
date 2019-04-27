package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.context.Response;
import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class NettyResponse implements Response {

    @Getter
    @Setter
    Object sender;

    private String responseLine;
    private Map<String, String> headers = Maps.newHashMap();

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public void writeBody(byte[] body) {

    }

    @Getter
    @Setter
    boolean full;

    @Override
    public byte[] getBody() {
        return new byte[0];
    }


}
