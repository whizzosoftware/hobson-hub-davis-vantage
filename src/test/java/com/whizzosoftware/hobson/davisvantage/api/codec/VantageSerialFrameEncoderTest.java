/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage.api.codec;

import com.whizzosoftware.hobson.davisvantage.api.command.VersionRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class VantageSerialFrameEncoderTest {
    @Test
    public void testTestEncode() throws Exception {
        VantageSerialFrameEncoder encoder = new VantageSerialFrameEncoder();

        com.whizzosoftware.hobson.davisvantage.api.command.Test test = new com.whizzosoftware.hobson.davisvantage.api.command.Test();
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, test, buf);
        assertEquals("TEST\n", buf.toString(Charset.forName("UTF8")));
    }

    @Test
    public void testVersion() throws Exception {
        VantageSerialFrameEncoder encoder = new VantageSerialFrameEncoder();

        VersionRequest ver = new VersionRequest();
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, ver, buf);
        assertEquals("VER\n", buf.toString(Charset.forName("UTF8")));
    }

    @Test
    public void testLoopRequest() throws Exception {
        VantageSerialFrameEncoder encoder = new VantageSerialFrameEncoder();

        com.whizzosoftware.hobson.davisvantage.api.command.LoopRequest ver = new com.whizzosoftware.hobson.davisvantage.api.command.LoopRequest(5);
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, ver, buf);
        assertEquals("LOOP 5\n", buf.toString(Charset.forName("UTF8")));
    }
}
