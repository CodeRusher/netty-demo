package com.code.ting.netty.rest;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.stream.ChunkedStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;
import javax.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@Slf4j
@Sharable
public class ServletChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Servlet servlet;

    public ServletChannelHandler(Servlet servlet) {
        this.servlet = servlet;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {

        // mock servlet
        MockHttpServletRequest servletRequest = createServletRequest(fullHttpRequest);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        this.servlet.service(servletRequest, servletResponse);

        HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        for (String name : servletResponse.getHeaderNames()) {
            for (Object value : servletResponse.getHeaderValues(name)) {
                response.headers().set(name, value);
            }
        }

        ctx.write(response);
        InputStream contentStream = new ByteArrayInputStream(servletResponse.getContentAsByteArray());
        ctx.writeAndFlush(new ChunkedStream(contentStream))
            .addListener(ChannelFutureListener.CLOSE);

    }

    private MockHttpServletRequest createServletRequest(FullHttpRequest fullHttpRequest) {

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(fullHttpRequest.uri()).build();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest(this.servlet.getServletConfig().getServletContext());
        servletRequest.setRequestURI(uriComponents.getPath());
        servletRequest.setPathInfo(uriComponents.getPath());
        servletRequest.setMethod(fullHttpRequest.method().name());

        if (uriComponents.getScheme() != null) {
            servletRequest.setScheme(uriComponents.getScheme());
        }
        if (uriComponents.getHost() != null) {
            servletRequest.setServerName(uriComponents.getHost());
        }
        if (uriComponents.getPort() != -1) {
            servletRequest.setServerPort(uriComponents.getPort());
        }

        for (String name : fullHttpRequest.headers().names()) {
            for (String value : fullHttpRequest.headers().getAll(name)) {
                servletRequest.addHeader(name, value);
            }
        }

        try {
            ByteBuf buf = fullHttpRequest.content();
            int readable = buf.readableBytes();
            byte[] bytes = new byte[readable];
            buf.readBytes(bytes);
            String contentStr = UriUtils.decode(new String(bytes, "UTF-8"), "UTF-8");
            for (String params : contentStr.split("&")) {
                String[] para = params.split("=");
                if (para.length > 1) {
                    servletRequest.addParameter(para[0], para[1]);
                } else {
                    servletRequest.addParameter(para[0], "");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            if (uriComponents.getQuery() != null) {
                String query = UriUtils.decode(uriComponents.getQuery(), "UTF-8");
                servletRequest.setQueryString(query);
            }

            for (Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
                for (String value : entry.getValue()) {
                    servletRequest.addParameter(
                        UriUtils.decode(entry.getKey(), "UTF-8"),
                        UriUtils.decode(value, "UTF-8"));
                }
            }
        } catch (UnsupportedEncodingException ex) {
            // shouldn't happen
        }
        return servletRequest;
    }
}
