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
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A Netty decoder for Davis Vantage serial command frames.
 *
 * @author Dan Noguerol
 */
public class VantageSerialFrameDecoder extends ByteToMessageDecoder {
    static final Logger logger = LoggerFactory.getLogger(VantageSerialFrameDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> list) throws Exception {
        while (buffer.readableBytes() > 0) {
            int ix = buffer.readerIndex();
            logger.trace("Readable bytes remaining to read: {}", buffer.readableBytes());
            if (buffer.getByte(ix) == '\r' || buffer.getByte(ix) == '\n') {
                logger.trace("Discarding whitespace");
                buffer.readByte();
            } else if (buffer.getByte(ix) == 0x06) {
                buffer.readByte();
                list.add(new ACK());
            } else if (buffer.readableBytes() >= 4 && buffer.getByte(ix) == 'O' && buffer.getByte(ix+1) == 'K' && buffer.getByte(ix+2) == '\n' && buffer.getByte(ix+3) == '\r') {
                logger.trace("Got OK");
                buffer.readBytes(4);
                list.add(new OK());
            } else if (buffer.readableBytes() >= 99 && buffer.getByte(ix) == 'L' && buffer.getByte(ix+1) == 'O' && buffer.getByte(ix+2) == 'O') {
                logger.trace("Got LOOP");
                byte[] bytes = new byte[99];
                buffer.readBytes(bytes, 0, 99);
                list.add(new LoopResponse(bytes));
            } else {
                byte b = buffer.readByte();
                logger.trace("Discarding unknown byte: {}", Hex.encodeHexString(new byte[] {b}));
            }
        }
    }
}
