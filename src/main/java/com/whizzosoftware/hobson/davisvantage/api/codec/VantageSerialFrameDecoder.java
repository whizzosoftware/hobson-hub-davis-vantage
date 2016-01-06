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
        int ix = buffer.readerIndex();
        int readableBytes = buffer.readableBytes();
        logger.trace("Readable bytes remaining to read: {}", readableBytes);
        if (readableBytes > 0) {
            if (buffer.readableBytes() >= 6 && buffer.getByte(ix) == 'T' && buffer.getByte(ix + 1) == 'E' && buffer.getByte(ix + 2) == 'S' && buffer.getByte(ix + 3) == 'T' && buffer.getByte(ix + 4) == '\n' && buffer.getByte(ix + 5) == '\r') {
                buffer.readBytes(6);
                logger.trace("Got TEST");
                list.add(new Test());
            } else if (buffer.getByte(ix) == '\r' || buffer.getByte(ix) == '\n') {
                logger.trace("Discarding whitespace: {}", (int)buffer.getByte(ix));
                buffer.readByte();
            } else if (buffer.getByte(ix) == 0x06) {
                buffer.readByte();
                list.add(new ACK());
            } else if (buffer.readableBytes() >= 4 && buffer.getByte(ix) == 'O' && buffer.getByte(ix + 1) == 'K' && buffer.getByte(ix + 2) == '\n' && buffer.getByte(ix + 3) == '\r') {
                logger.trace("Got OK");
                buffer.readBytes(4);
                list.add(new OK());
            } else if (buffer.getByte(ix) == 'L' && buffer.getByte(ix + 1) == 'O' && buffer.getByte(ix + 2) == 'O') {
                if (buffer.readableBytes() >= 99) {
                    logger.trace("Got LOOP");
                    byte[] bytes = new byte[99];
                    buffer.readBytes(bytes, 0, 99);
                    list.add(new LoopResponse(bytes));
                }
            } else if (
                (buffer.getByte(ix) == 'A' && buffer.getByte(ix + 1) == 'p' && buffer.getByte(ix + 2) == 'r') ||
                (buffer.getByte(ix) == 'A' && buffer.getByte(ix + 1) == 'u' && buffer.getByte(ix + 2) == 'g') ||
                (buffer.getByte(ix) == 'D' && buffer.getByte(ix + 1) == 'e' && buffer.getByte(ix + 2) == 'c') ||
                (buffer.getByte(ix) == 'F' && buffer.getByte(ix + 1) == 'e' && buffer.getByte(ix + 2) == 'b') ||
                (buffer.getByte(ix) == 'J' && buffer.getByte(ix + 1) == 'a' && buffer.getByte(ix + 2) == 'n') ||
                (buffer.getByte(ix) == 'J' && buffer.getByte(ix + 1) == 'u' && buffer.getByte(ix + 2) == 'l') ||
                (buffer.getByte(ix) == 'J' && buffer.getByte(ix + 1) == 'u' && buffer.getByte(ix + 2) == 'n') ||
                (buffer.getByte(ix) == 'M' && buffer.getByte(ix + 1) == 'a' && buffer.getByte(ix + 2) == 'r') ||
                (buffer.getByte(ix) == 'M' && buffer.getByte(ix + 1) == 'a' && buffer.getByte(ix + 2) == 'y') ||
                (buffer.getByte(ix) == 'N' && buffer.getByte(ix + 1) == 'o' && buffer.getByte(ix + 2) == 'v') ||
                (buffer.getByte(ix) == 'O' && buffer.getByte(ix + 1) == 'c' && buffer.getByte(ix + 2) == 't') ||
                (buffer.getByte(ix) == 'D' && buffer.getByte(ix + 1) == 'e' && buffer.getByte(ix + 2) == 'c')
                ) {
                if (buffer.readableBytes() >= 13) {
                    byte[] bytes = new byte[13];
                    buffer.readBytes(bytes, 0, 13);
                    list.add(new VersionResponse(new String(bytes, 0, 11)));
                }
            } else {
                byte b = buffer.readByte();
                logger.trace("Discarding unknown byte: {}", Hex.encodeHexString(new byte[]{b}));
            }
        }
    }
}
