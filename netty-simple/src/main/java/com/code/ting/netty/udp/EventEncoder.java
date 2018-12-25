package com.code.ting.netty.udp;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.net.InetSocketAddress;
import java.util.List;

public class EventEncoder extends MessageToMessageDecoder<Event> {

    private InetSocketAddress remoteAddress;

    public EventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, Event e, List<Object> out) throws Exception {
        int tag = e.getTag();
        byte[] msg = e.getMsg().getBytes();
        ByteBuf buf = ctx.alloc().buffer(4 + msg.length);
        buf.writeInt(tag);
        buf.writeBytes(msg);
        out.add(new DatagramPacket(buf, remoteAddress));

    }
}
