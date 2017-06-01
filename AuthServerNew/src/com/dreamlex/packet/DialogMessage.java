package com.dreamlex.packet;

import com.dreamlex.PacketFields;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.mina.core.buffer.IoBuffer;

public class DialogMessage extends Packet {

    public int success;
    public String message;
    public int controlSum;

    public DialogMessage() {
    }

    public DialogMessage(int success, String message) {
        this.success = success;
        this.message = message;
    }

    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);

        while(!packetEnd(in)) {
            try {
                int fieldId = in.getInt();

                if(hashMapPacketFields.containsKey(fieldId)) {
                    switch(fieldId) {
                        case PacketFields.MESSAGE:
                            setField(this, (String) hashMapPacketFields.get(fieldId), readUTF(in));
                            break;
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

        readTail(in);

        if(!checkControlSum()) {
            throw new IOException("Bad control sum");
        }
    }

    private static void setField(DialogMessage packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }

    private int countControlSum() {
        return 1;
    }

    private boolean checkControlSum() {
        return true;
    }
    
    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);
        out.putInt(PacketFields.SUCCESS);
        writeInt(out, success);
        out.putInt(PacketFields.MESSAGE);
        writeUTF(out, message);
        out.putInt(PacketFields.CONTROL_SUM);
        writeInt(out, countControlSum());
        writeTail(out);
    }

    public int size() {
        return 1 + 4 + this.message.length() + 1;
    }

    @Override
    public String toString() {
        return "DialogMessage";
    }
}
