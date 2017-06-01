package com.belsoft.packet;

class PacketCounter {

    private int packetCount;
    private long size;

    private PacketCounter() {}

    public void addPacket(int packetSize) {
        ++this.packetCount;
        this.size += (long) packetSize;
    }

    /*PacketCounter(EmptyClass1 emptyclass1) {
        this();
    }*/
}
