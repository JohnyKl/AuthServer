package com.dreamlex.packet;

import com.dreamlex.PacketFields;
import static com.dreamlex.packet.Packet.hashMapPacketFields;
import static com.dreamlex.packet.Packet.readHead;
import static com.dreamlex.packet.Packet.readTail;
import static com.dreamlex.packet.Packet.readUTF;
import static com.dreamlex.packet.Packet.writeHead;
import static com.dreamlex.packet.Packet.writeInt;
import static com.dreamlex.packet.Packet.writeTail;
import static com.dreamlex.packet.Packet.writeUTF;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.mina.core.buffer.IoBuffer;

public class Logout extends Packet {

    public int success;
    public int userID;
    public int tempSessionKey;
    public String serverMessage;
    public int serverMessageType;
    public int controlSum;

    public Logout() {

    }

    public Logout(int success, int serverMessageType,  String serverMessage) {
        this.serverMessage = serverMessage;
        this.success = success;
        this.serverMessageType = serverMessageType;
    }

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

    private static void setField(Logout packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }
    
    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);
        out.putInt(PacketFields.SUCCESS);
        writeInt(out, success);

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

    public void doWork(NetHandler nethandler) {
        //nethandler.logout(this);
    }

    private int countControlSum() {
        return 1;
    }

    private boolean checkControlSum() {
        return true;
    }

    public int size() {
        return 1 + 16 + 1;
    }

    @Override
    public String toString() {
        return "Logout";
    }
}
