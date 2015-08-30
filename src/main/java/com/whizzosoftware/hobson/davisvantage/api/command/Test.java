package com.whizzosoftware.hobson.davisvantage.api.command;

public class Test extends VantageSerialCommand {
    public static final String TYPE = "TEST";

    public Test() {
        super(TYPE);
    }

    @Override
    public byte[] getBytes() {
        return TYPE.getBytes();
    }
}
