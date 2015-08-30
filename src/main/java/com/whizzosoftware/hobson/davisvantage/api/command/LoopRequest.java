package com.whizzosoftware.hobson.davisvantage.api.command;

public class LoopRequest extends VantageSerialCommand {
    public static final String TYPE = "LOOP";

    private int numberOfPackets;

    /**
     * Constructor.
     *
     * @param numberOfPackets the number of loop packets to respond with (1 every 2 seconds)
     */
    public LoopRequest(int numberOfPackets) {
        super(TYPE);
        this.numberOfPackets = numberOfPackets;
    }

    public int getNumberOfPackets() {
        return numberOfPackets;
    }

    @Override
    public byte[] getBytes() {
        return (TYPE + " " + getNumberOfPackets()).getBytes();
    }
}
