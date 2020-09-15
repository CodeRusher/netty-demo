package com.code.ting.netty.proxy.http.io.netty.proxy;


import com.code.ting.netty.proxy.http.chain.FilterChain;
import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.RouteContext;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.Consts;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultContext;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultRequest;
import com.code.ting.netty.proxy.http.io.netty.context.DefaultResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.util.LinkedList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class HttpProxyMultiPartHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final String MULTIPART_KEY_WORD = "multipart";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        ReferenceCountUtil.retain(msg);

        // 请求头
        if (msg instanceof HttpRequest) {

            HttpRequest httpRequest = (HttpRequest) msg;

            String contentType = httpRequest.headers().get("Content-Type");
            if (StringUtils.isNoneBlank(contentType) && contentType.toLowerCase().contains(MULTIPART_KEY_WORD)) {
                // 备注：FilterChain是在ChannelInitializer中添加的
                FilterChain chain = ctx.channel().attr(Consts.CHAIN_KEY).get();
                DefaultContext context = new DefaultContext(chain);
                log.debug("{} request at : {}", context.getId(), System.currentTimeMillis());

                DefaultRequest request = new DefaultRequest();
                request.setHttpRequest(httpRequest);
                context.setRequest(request);

                Connector connector = new Connector();
                connector.setProxyChannel(ctx.channel());
                connector.setProxyHttpRequest(httpRequest);
                connector.setProxyHttpContents(new LinkedList<>());
                context.setConnector(connector);
                context.setResponse(new DefaultResponse());

                ctx.channel().attr(Consts.CONTEXT_KEY).set(context);

//                chain.fireChain(context);
                FilterChain.THREAD_POOL_EXECUTOR.execute(() -> chain.fireChain(context));
                log.debug("{} fireChain from : HttpProxyMultiPartHandler", context.getId());

            } else {
                ctx.channel().attr(Consts.CONTEXT_KEY).set(null);
                // 非MultiPart请求，传递给ChannelPipeline中的下一个ChannelHandler
                ctx.fireChannelRead(msg);
            }
        }

        // 请求体
        if (msg instanceof HttpContent) {

            if (ctx.channel().attr(Consts.CONTEXT_KEY).get() == null) {
                // 非MultiPart请求，传递给ChannelPipeline中的下一个ChannelHandler
                ctx.fireChannelRead(msg);
                return;
            }

            HttpContent httpContent = (HttpContent) msg;

            RouteContext context = ctx.channel().attr(Consts.CONTEXT_KEY).get();
            if (context.getStatus() == Status.CANCEL) {
                ReferenceCountUtil.release(msg);
                return;
            }

            Connector connector = context.getConnector();
            Channel clientChannel = connector.getClientChannel();

            // 基本逻辑应该是如果clientChannel已经建立，则直接使用clientChannel发送，如果还没有建立，则先缓存
            // synchronized锁的作用是，等待clientChannel将缓存中的数据发送完毕，这里可能产生阻塞，不知道性能如何
            // todo：这里逻辑写的有点乱，需要优化
            if (clientChannel == null) {
                synchronized (context) {
                    clientChannel = connector.getClientChannel();
                    if (clientChannel == null) {
                        connector.getProxyHttpContents().addLast(httpContent);
                    } else {
                        clientChannel.pipeline().context("aggregator").writeAndFlush(httpContent);
                    }
                }
            } else {
                clientChannel.pipeline().context("aggregator").writeAndFlush(httpContent);
            }
        }
    }
}
