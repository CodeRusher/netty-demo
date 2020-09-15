package com.code.ting.netty.buf;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

public class ReleaseTest {

    @Test
    public void test() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        System.out.println(buf.refCnt());

        buf.retain();
        System.out.println(buf.refCnt());

        buf.release(buf.refCnt());
        System.out.println(buf.refCnt());

        buf.retain();
        System.out.println(buf.refCnt());
    }
}
