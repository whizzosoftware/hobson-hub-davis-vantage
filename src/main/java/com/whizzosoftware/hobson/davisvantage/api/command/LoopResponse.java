/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.davisvantage.api.command;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.text.ParseException;

public class LoopResponse {
    private Integer barTrend;
    private Integer barometer;
    private Integer dewPoint;
    private Integer insideTemp;
    private Integer insideHumidity;
    private Integer outsideHumidity;
    private Integer outsideTemp;
    private Type type;
    private Integer windSpeed;
    private Integer windDirection;

    public LoopResponse(byte[] data) throws ParseException {
        if (data.length == 99 && data[0] == 'L' && data[1] == 'O' && data[2] == 'O') {
            if (data[3] != 'P') {
                barTrend = (int)data[3];
            }
            type = (data[4] == 1) ? Type.LOOP : Type.LOOP2;
            barometer = convertTwoBytesToUnsignedInt(data[8], data[7]);
            insideTemp = convertTwoBytesToUnsignedInt(data[10], data[9]);
            insideHumidity = (int)data[11];
            outsideTemp = convertTwoBytesToUnsignedInt(data[13], data[12]);
            outsideHumidity = (int)data[33];
            windSpeed = (int)data[14];
            windDirection = convertTwoBytesToUnsignedInt(data[17], data[16]);
            dewPoint = convertTwoBytesToSignedInt(data[31], data[30]);
        } else {
            throw new ParseException(new String(data), 0);
        }
    }

    public Integer getBarTrend() {
        return barTrend;
    }

    public Integer getBarometer() {
        return barometer;
    }

    public Integer getDewPoint() {
        return dewPoint;
    }

    public Integer getInsideTemp() {
        return insideTemp;
    }

    public Integer getInsideHumidity() {
        return insideHumidity;
    }

    public Integer getOutsideHumidity() {
        return outsideHumidity;
    }

    public Integer getOutsideTemp() {
        return outsideTemp;
    }

    public Type getType() {
        return type;
    }

    public Integer getWindSpeed() {
        return windSpeed;
    }

    public Integer getWindDirection() {
        return windDirection;
    }

    private int convertTwoBytesToUnsignedInt(byte msb, byte lsb) {
        return ((msb << 8) & 0x0000ff00) | (lsb & 0x000000ff);
    }

    private int convertTwoBytesToSignedInt(byte msb, byte lsb) {
        return (msb << 8) | (lsb & 0xff);
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("barTrend", barTrend)
            .append("barometer", barometer)
            .append("dewPoint", dewPoint)
            .append("insideTemp", insideTemp)
            .append("insideHumidity", insideHumidity)
            .append("outsideTemp", outsideTemp)
            .append("outsideHumidity", outsideHumidity)
            .append("type", type)
            .append("windSpeed", windSpeed)
            .append("windDirection", windDirection)
            .build();
    }

    public enum Type {
        LOOP,
        LOOP2
    }
}
