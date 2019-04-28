package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.ProcessorChain;
import com.code.ting.netty.proxy.http.chain.context.Connector;
import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import com.code.ting.netty.proxy.http.io.netty.context.NettyResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    private ProcessorChain chain;
    private HttpRequestParser httpParser = new HttpRequestParser();

    public HttpProxyHandler(ProcessorChain chain) {
        this.chain = chain;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // the fisrt msg
        if (httpParser.getContext() == null) {
            httpParser.setContext(new NettyContext());
        }

        // New http request coming
        if (httpParser.getContext().getStatus() == Status.RESPONSE_COMPLETED) {
            httpParser = new HttpRequestParser();
            httpParser.setContext(new NettyContext());
        }

        NettyContext context = httpParser.getContext();

        /*---------------------  direct forward  ---------------------------*/
        if (httpParser.getContext().getStatus() == Status.REQUEST_HEADER_READ) {
            if (!httpParser.getRequest().isFull()) {
                Connector connector = httpParser.getContext().getConnector();
                if (connector.getClient() instanceof Channel) {
                    // completed???
                    ((Channel) (connector.getClient())).writeAndFlush(msg);

                }

                if (connector.getClient() instanceof java.net.Socket) {
                    // ...
                }
            }
        }

        /*---------------------  parse request  ---------------------------*/
        ByteBuf in = (ByteBuf) msg;
        httpParser.parse(in);

        if ((context.getStatus() == Status.NEW) ||
            (context.getRequest().isFull() && context.getStatus() != Status.REQUEST_COMPLETED)) {
            in.release();
            return;
        }


        /*---------------------  chain  ---------------------------*/
        // disable until client is ready
        if (!context.getRequest().isFull()) {
            ctx.channel().config().setAutoRead(false);
        }
        // gen NettyContext
        context.setRequest(httpParser.getRequest());
        NettyResponse response = new NettyResponse();
        context.setResponse(response);
        Connector connector = new Connector();
        connector.setProxy(ctx.channel());
        context.setConnector(connector);

        // fire chain
        if (httpParser.getRequest().isFull()) {
            // 同步调用处理链
            chain.fireChain(context);
        } else {
            // 异步调用 处理链
            chain.fireChain(context);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.error(" error:{} ", e.getMessage(), e);
//        chain.handleThrowable(context);
        ctx.channel().close();
    }

}
