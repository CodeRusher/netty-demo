package com.code.ting.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


public class AbsIntegerEncoderTest {

    @Test
    public void test() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());

        Assert.assertTrue(channel.writeOutbound(buf));
        Assert.assertTrue(channel.finish());

        for (int i = 1; i < 10; i++) {
            Assert.assertEquals(i, (int) channel.readOutbound());
        }

        Assert.assertNull(channel.readOutbound());
    }

}