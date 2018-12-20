package com.code.ting.netty.rest;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Slf4j
@Sharable
public class SpringMvcChannelHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private AnnotationConfigWebApplicationContext ctx;
    private DispatcherServlet dispatcherServlet;
    private ServletContext servletContext;
    private ServletConfig servletConfig;


    public SpringMvcChannelHandler() {
        // init spring-mvc
        ServletContext servletContext = new MockServletContext();
        ServletConfig servletConfig = new MockServletConfig();

        ctx = new AnnotationConfigWebApplicationContext();
        ctx.setServletContext(servletContext);
        ctx.setServletConfig(servletConfig);
        ctx.scan("com.code.ting.netty.rest");
        ctx.refresh();

        dispatcherServlet = new DispatcherServlet(ctx);
        try {
            dispatcherServlet.init();
        } catch (Exception e) {
            log.error("SpringMvcChannelHandler error:{}", e.getMessage(), e);
        }

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest request) throws Exception {
        MockHttpServletRequest servletRequest = createServletRequest(request);
    }

    private MockHttpServletRequest createServletRequest(HttpRequest httpRequest) {

        return null;
    }
}
