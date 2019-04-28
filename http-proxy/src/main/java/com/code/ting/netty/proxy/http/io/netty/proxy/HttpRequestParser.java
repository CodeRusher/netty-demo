package com.code.ting.netty.proxy.http.io.netty.proxy;


import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import com.code.ting.netty.proxy.http.io.netty.context.NettyRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestParser {


    @Getter
    @Setter
    private NettyContext context;

    @Getter
    private NettyRequest request = new NettyRequest();

    @Getter
    private int contentLength;

    @Getter
    @Setter
    private int bodyReadedLength = 0;


    public void parse(ByteBuf in) {
        while (in.isReadable()) {

            // body
            if (request.isFull()) {
                request.getContent().writeBytes(in);
                if (request.getContent().readableBytes() == contentLength) {
                    context.setStatus(Status.REQUEST_COMPLETED);
                }
            }

            String line = readLine(in);

            // 请求行
            if (StringUtils.isBlank(request.getMethod())) {
                request.setRequestLine(line);
                String[] strs = line.split(" ");
                request.setMethod(strs[0]);
                request.setUrl(strs[1]);
                continue;
            }

            // headers
            if (Status.NEW == context.getStatus()) {
                String[] strs = line.split(":");
                request.addHead(strs[0], strs[1].trim());
                continue;
            }

            // 分隔行
            if (line.isEmpty()) {
                context.setStatus(Status.REQUEST_HEADER_READ);
                contentLength = Integer.parseInt(request.getHeader(""));
                if (contentLength <= 1024) {
                    request.setFull(true);
                    request.setContent(Unpooled.buffer(contentLength));
                }
            }

        }
    }

    private final StringBuilder lineBuf = new StringBuilder();

    private String readLine(ByteBuf in) {
        while (in.isReadable()) {
            byte b = in.readByte();
            lineBuf.append((char) b);
            int len = lineBuf.length();
            if (len >= 2 && lineBuf.substring(len - 2).equals("\r\n")) {
                String line = lineBuf.substring(0, len - 2);
                lineBuf.delete(0, len);
                return line;
            }
        }

        return null;
    }

}
