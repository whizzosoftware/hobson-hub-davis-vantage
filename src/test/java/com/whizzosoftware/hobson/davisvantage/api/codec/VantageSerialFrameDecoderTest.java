/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage.api.codec;

import com.whizzosoftware.hobson.davisvantage.api.command.LoopResponse;
import com.whizzosoftware.hobson.davisvantage.api.command.VersionResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class VantageSerialFrameDecoderTest {
    @Test
    public void testLoopResponse() throws Exception {
        VantageSerialFrameDecoder d = new VantageSerialFrameDecoder();
        ByteBuf buf = Unpooled.copiedBuffer(new byte[] {
            0x4c, 0x4f, 0x4f, 0x00, 0x01,
            (byte)0xff, 0x7f, (byte)0x88, 0x74, (byte)0xbd,
            0x02, 0x15, (byte)0xb5, 0x01, 0x01,
            (byte)0xff, (byte)0xfa, 0x00, 0x11, 0x00,
            0x13, 0x00, 0x04, 0x00, 0x0e,
            0x01, (byte)0xff, 0x7f, (byte)0xff, 0x7f,
            0x12, 0x00, (byte)0xff, 0x23, (byte)0xff,
            0x2a, 0x00, 0x2b, 0x00, (byte)0xff,
            0x7f, 0x00, 0x00, (byte)0xff, (byte)0xff,
            0x7f, 0x00, 0x00, (byte)0xff, (byte)0xff,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x02, 0x00, 0x00, (byte)0xe5, (byte)0xff,
            0x7c, 0x5c, 0x7c, 0x5c, (byte)0x8a,
            0x74, (byte)0xff, 0x02, 0x07, 0x0c,
            0x13, 0x05, 0x11, 0x0e, 0x20,
            0x0c, 0x04, 0x04, (byte)0xff, 0x7f,
            (byte)0xff, 0x7f, (byte)0xff, 0x7f, (byte)0xff,
            0x7f, (byte)0xff, 0x7f, (byte)0xff, 0x7f,
            0x10, 0x13, (byte)0x8c, (byte)0xe2
        });
        List<Object> l = new ArrayList<>();
        d.decode(null, buf, l);
        assertEquals(1, l.size());
        assertTrue(l.get(0) instanceof LoopResponse);
        LoopResponse lr = (LoopResponse)l.get(0);
        assertEquals(437.0, lr.getOutsideTemp(), 0);
    }

    @Test
    public void testSegmentedLoopResponse() throws Exception {
        VantageSerialFrameDecoder d = new VantageSerialFrameDecoder();
        ByteBuf buf1 = Unpooled.copiedBuffer(new byte[]{
            0x4c, 0x4f, 0x4f, 0x00, 0x01,
            (byte)0xff, 0x7f, (byte)0x88, 0x74, (byte)0xbd,
            0x02, 0x15, (byte)0xb5, 0x01, 0x01,
            (byte)0xff, (byte)0xfa, 0x00, 0x11, 0x00,
            0x13, 0x00, 0x04, 0x00, 0x0e,
            0x01, (byte)0xff, 0x7f, (byte)0xff, 0x7f,
            0x12, 0x00, (byte)0xff, 0x23, (byte)0xff,
            0x2a, 0x00, 0x2b, 0x00, (byte)0xff,
            0x7f, 0x00, 0x00, (byte)0xff, (byte)0xff,
            0x7f, 0x00, 0x00, (byte)0xff, (byte)0xff,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x02, 0x00, 0x00, (byte)0xe5, (byte)0xff,
            0x7c, 0x5c, 0x7c, 0x5c, (byte)0x8a,
            0x74, (byte)0xff, 0x02, 0x07, 0x0c,
            0x13, 0x05, 0x11, 0x0e
        });
        ByteBuf buf2 = Unpooled.copiedBuffer(new byte[] {
            0x20,
            0x0c, 0x04, 0x04, (byte)0xff, 0x7f,
            (byte)0xff, 0x7f, (byte)0xff, 0x7f, (byte)0xff,
            0x7f, (byte)0xff, 0x7f, (byte)0xff, 0x7f,
            0x10, 0x13, (byte)0x8c, (byte)0xe2
        });

        List<Object> l = new ArrayList<>();
        d.decode(null, buf1, l);
        assertEquals(0, l.size());
        d.decode(null, Unpooled.copiedBuffer(buf1, buf2), l);
        assertEquals(1, l.size());
        assertTrue(l.get(0) instanceof LoopResponse);
        LoopResponse lr = (LoopResponse)l.get(0);
        assertEquals(437.0, lr.getOutsideTemp(), 0);
    }

    @Test
    public void testVersionResponse() throws Exception {
        VantageSerialFrameDecoder d = new VantageSerialFrameDecoder();
        ByteBuf buf = Unpooled.copiedBuffer(new byte[] { 0x4D, 0x61, 0x79, 0x20, 0x20, 0x31, 0x20, 0x32, 0x30, 0x31, 0x32, 10, 13});
        List<Object> l = new ArrayList<>();
        d.decode(null, buf, l);
        assertEquals(1, l.size());
        assertTrue(l.get(0) instanceof VersionResponse);
        assertEquals("May  1 2012", ((VersionResponse)l.get(0)).getValue());
    }

    @Test
    public void testTestResponse() throws Exception {
        VantageSerialFrameDecoder d = new VantageSerialFrameDecoder();
        ByteBuf buf = Unpooled.copiedBuffer(new byte[] { 'T', 'E', 'S', 'T', 10, 13});
        List<Object> l = new ArrayList<>();
        d.decode(null, buf, l);
        assertEquals(1, l.size());
        assertTrue(l.get(0) instanceof com.whizzosoftware.hobson.davisvantage.api.command.Test);
    }
}
