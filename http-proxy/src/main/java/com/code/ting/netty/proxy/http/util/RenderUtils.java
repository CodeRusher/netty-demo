package com.code.ting.netty.proxy.http.util;


import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.EmptyArrays;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenderUtils {

    private static final String RESPONSE_ERROR_FORMAT = "{\"errorCode\":\"%s\",\"errorMsg\":\"%s\",\"status\":\"ERROR\"}";

    public static void response(Channel channel, String code, String msg) {

        String json = String.format(RESPONSE_ERROR_FORMAT, code, msg);
        FullHttpResponse response = genResponse(json);
        channel.writeAndFlush(response);
    }

    private static FullHttpResponse genResponse(String json) {
        byte[] content = EmptyArrays.EMPTY_BYTES;
        try {
            content = json.getBytes(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error(" error:{} ", e.getMessage(), e);
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
        response.headers().set(CONTENT_TYPE, "application/json; charset=utf-8");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

}
