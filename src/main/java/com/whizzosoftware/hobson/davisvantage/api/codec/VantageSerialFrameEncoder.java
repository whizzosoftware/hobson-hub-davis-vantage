/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage.api.codec;

import com.whizzosoftware.hobson.davisvantage.api.command.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Netty encoder for Davis Vantage serial command frames.
 *
 * @author Dan Noguerol
 */
public class VantageSerialFrameEncoder extends MessageToByteEncoder<VantageSerialCommand> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, VantageSerialCommand cmd, ByteBuf out) throws Exception {
        byte[] b = null;

        switch (cmd.getType()) {
            case VersionRequest.TYPE:
            case Test.TYPE:
            case LoopRequest.TYPE:
            case LPSRequest.TYPE:
                b = cmd.getBytes();
                break;
            default:
                logger.error("Attempt to send invalid command: {}", cmd);
        }

        if (b != null) {
            logger.trace("encode: {}", new String(b));
            out.writeBytes(b);
            out.writeByte('\n');
        } else {
            logger.error("Attempt to send unknown command class: {}", cmd);
        }
    }
}
