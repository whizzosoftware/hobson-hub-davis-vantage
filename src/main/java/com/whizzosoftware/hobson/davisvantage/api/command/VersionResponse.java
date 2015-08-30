package com.whizzosoftware.hobson.davisvantage.api.command;

public class VersionResponse extends VantageSerialCommand {
    public static final String TYPE = "VER";

    private String value;

    public VersionResponse(String value) {
        super(TYPE);

        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public byte[] getBytes() {
        return TYPE.getBytes();
    }

    public String toString() {
        return "Version=" + getValue();
    }
}
