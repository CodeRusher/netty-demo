package com.code.ting.netty.proxy.http.io.netty.client;


import com.code.ting.netty.proxy.http.chain.context.Status;
import com.code.ting.netty.proxy.http.io.netty.context.NettyContext;
import com.code.ting.netty.proxy.http.io.netty.context.NettyResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class HttpResponseParser {

    @Getter
    @Setter
    private NettyContext context;

    @Getter
    private int contentLength;

    @Getter
    @Setter
    private int bodyReadedLength = 0;

    public void parse(ByteBuf in) {
        NettyResponse response = (NettyResponse) context.getResponse();
        while (in.isReadable()) {

            // body
            if (response.isFull()) {
                response.getContent().writeBytes(in);
                if (response.getContent().readableBytes() == contentLength) {
                    context.setStatus(Status.RESPONSE_COMPLETED);
                }
            }

            String line = readLine(in);

            // 请求行
            if (StringUtils.isBlank(response.getVersion())) {
                response.setResponseLine(line);
                String[] strs = line.split(" ");
                response.setVersion(strs[0]);
                response.setCode(strs[1]);
                response.setStatus(strs[2]);
                continue;
            }

            // headers
            if (Status.REQUEST_COMPLETED == context.getStatus()) {
                String[] strs = line.split(":");
                response.addHead(strs[0], strs[1].trim());
                continue;
            }

            // 分隔行
            if (line.isEmpty()) {
                context.setStatus(Status.RESPONSE_HEADER_READ);
                contentLength = Integer.parseInt(response.getHeader("Content-Length"));
                if (contentLength <= 1024) {
                    response.setFull(true);
                    response.setContent(Unpooled.buffer(contentLength));
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
