package com.code.ting.netty.udp;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class EventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf data = datagramPacket.content();
        int tag = data.readInt();
        String msg = data.slice(0, data.readableBytes()).toString();

        Event event = new Event();
        event.setTag(tag);
        event.setMsg(msg);
        out.add(event);
    }
}
