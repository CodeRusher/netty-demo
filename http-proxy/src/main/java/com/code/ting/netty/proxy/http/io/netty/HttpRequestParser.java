package com.code.ting.netty.proxy.http.io.netty;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestParser {

    @Getter
    private NettyRequest request = new NettyRequest();

    @Getter
    private boolean headerReaded = false;

    @Getter
    private boolean complete;

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

            if (!headerReaded) {
                String[] strs = line.split(":");
                request.addHead(strs[0], strs[1]);
                continue;
            }

            if (line.isEmpty()) {
                headerReaded = true;
            }

            if (headerReaded) {
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
