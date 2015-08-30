package com.whizzosoftware.hobson.davisvantage.api.command;

public class LPSRequest extends VantageSerialCommand {
    public static final String TYPE = "LPS";

    private int packetType;
    private int numberOfPackets;

    /**
     * Constructor.
     *
     * @param numberOfPackets the number of loop packets to respond with (1 every 2 seconds)
     */
    public LPSRequest(int packetType, int numberOfPackets) {
        super(TYPE);
        this.packetType = packetType;
        this.numberOfPackets = numberOfPackets;
    }

    public int getPacketType() {
        return packetType;
    }

    public int getNumberOfPackets() {
        return numberOfPackets;
    }

    @Override
    public byte[] getBytes() {
        return (TYPE + " " + getPacketType() + " " + getNumberOfPackets()).getBytes();
    }
}
