package com.dreamlex.packet;

import java.io.IOException;
import java.lang.reflect.Field;

import com.dreamlex.PacketFields;
import org.apache.mina.core.buffer.IoBuffer;

public class Ping extends Packet {

    public int tempSessionKey;
    public String serverMessage;
    public int serverMessageType;
    public int floatingKey;
    public int controlSum;

    public Ping() { }

    public Ping(int serverMessageType, String serverMessage) {
        this.serverMessage = serverMessage;
        this.serverMessageType = serverMessageType;
        this.floatingKey = 0;
    }

    public void doWork(NetHandler nethandler) {}

    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);

        while(!packetEnd(in)) {
            try {
                int fieldId = in.getInt();

                if(hashMapPacketFields.containsKey(fieldId)) {
                    switch(fieldId) {
                        default:
                            setField(this, (String) hashMapPacketFields.get(fieldId), readInt(in));
                    }
                }
                else {
                    throw new IOException("Bad packet`s field ID");
                }
            }
            catch(Exception e) {
                throw new IOException(e.getMessage());
            }
        }

        if(!checkControlSum()) {
            throw new IOException("Bad control sum");
        }
    }

    private static void setField(Ping packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }
    
    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);

        out.putInt(PacketFields.FLOATING_KEY);
        writeInt(out, floatingKey);

        if(serverMessage != null) {
            out.putInt(PacketFields.SERVER_MESSAGE_TYPE);
            writeInt(out, serverMessageType);
            out.putInt(PacketFields.SERVER_MESSAGE);
            writeUTF(out, serverMessage);
        }
        out.putInt(PacketFields.CONTROL_SUM);
        writeInt(out, countControlSum());
        writeTail(out);
    }

    private int countControlSum() {
        return 1;
    }

    private boolean checkControlSum() {
        return true;
    }

    public int size() {
        return 8 + 2;
    }

    public String toString() {
        return "Ping";
    }
}
