package com.code.ting.netty.proxy.http.chain.proccesser;


import com.code.ting.netty.proxy.http.chain.Processor;
import com.code.ting.netty.proxy.http.chain.context.Context;
import com.code.ting.netty.proxy.http.chain.context.Request;
import com.code.ting.netty.proxy.http.chain.context.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class RouteProcessor implements Processor {

    private Call.Factory callFactory = new OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build();

    public RouteProcessor() {

    }

    @Override
    public void pre(Context context) {

    }

    @Override
    public boolean process(Context context) {
        Request request = context.getRequest();
        Response response = context.getResponse();

        try {
            Socket socket = new Socket("", 8888);
            socket.getOutputStream().write(request.getRequestHeader());
            if (request.isFull()) {
                socket.getOutputStream().write(request.getBody());
            } else {
                request.setReceiver(socket);
            }


        } catch (Exception e) {

        }

        return true;
    }

    @Override
    public void after(Context context) {

    }

    private static class CallerHandler extends ChannelInboundHandlerAdapter {

        private Context context;


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {

        }
    }
}
