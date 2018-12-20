package com.code.ting.netty.rest;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Slf4j
public class DispatcherServletChannelInitializer extends ChannelInitializer<SocketChannel> {

    private DispatcherServlet dispatcherServlet;

    public DispatcherServletChannelInitializer(String[] basePackages) {
        // mock servlet
        ServletContext servletContext = new MockServletContext();
        ServletConfig servletConfig = new MockServletConfig(servletContext);

        // init spring-mvc
        AnnotationConfigWebApplicationContext wac = new AnnotationConfigWebApplicationContext();
        wac.setServletContext(servletContext);
        wac.setServletConfig(servletConfig);
        wac.scan(basePackages);
        wac.refresh();

        this.dispatcherServlet = new DispatcherServlet(wac);
        try {
            dispatcherServlet.init(servletConfig);
        } catch (Exception e) {
            log.error("ServletChannelHandler error:{}", e.getMessage(), e);
        }
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunk", new ChunkedWriteHandler());
        pipeline.addLast("mvc", new ServletChannelHandler(dispatcherServlet));
    }
}
