package com.code.ting.netty.proxy.http.io.netty;


import com.code.ting.netty.proxy.http.chain.context.Status;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestParser {



    @Getter
    @Setter
    private NettyContext context;

    @Getter
    private NettyRequest request = new NettyRequest();


    public void parse(ByteBuf in) {
        while (in.isReadable()) {

            String line = readLine(in);

            if (StringUtils.isBlank(request.getMethod())) {
                request.setRequestLine(line);
                String[] strs = line.split(" ");
                request.setMethod(strs[0]);
                request.setUrl(strs[1]);
                continue;
            }

            if (Status.NEW == context.getStatus()) {
                String[] strs = line.split(":");
                request.addHead(strs[0], strs[1]);
                continue;
            }

            if (line.isEmpty()) {
                context.setStatus(Status.REQUEST_HEADER_READ);
            }

            if (Status.REQUEST_HEADER_READ == context.getStatus()) {
                if (Integer.parseInt(request.getHeader("")) <= 1024) {
                    request.setFull(true);
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
