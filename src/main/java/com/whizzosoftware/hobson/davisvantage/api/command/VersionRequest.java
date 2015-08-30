package com.whizzosoftware.hobson.davisvantage.api.command;

public class VersionRequest extends VantageSerialCommand {
    public static final String TYPE = "VER";

    public VersionRequest() {
        super(TYPE);
    }

    @Override
    public byte[] getBytes() {
        return TYPE.getBytes();
    }
}
