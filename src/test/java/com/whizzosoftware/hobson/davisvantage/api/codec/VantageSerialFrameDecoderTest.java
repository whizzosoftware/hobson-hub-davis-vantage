/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage.api.codec;

import com.whizzosoftware.hobson.davisvantage.api.command.VersionResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class VantageSerialFrameDecoderTest {
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
        ByteBuf buf = Unpooled.copiedBuffer(new byte[] { 10, 13, 'T', 'E', 'S', 'T', 10, 13});
        List<Object> l = new ArrayList<>();
        d.decode(null, buf, l);
        assertEquals(1, l.size());
        assertTrue(l.get(0) instanceof com.whizzosoftware.hobson.davisvantage.api.command.Test);
    }
}
