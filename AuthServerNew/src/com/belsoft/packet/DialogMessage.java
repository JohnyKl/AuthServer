package com.belsoft.packet;

import com.belsoft.PacketFields;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.mina.core.buffer.IoBuffer;

public class DialogMessage extends Packet {

    public int success;
    public String message;

    public DialogMessage() {
    }

    public DialogMessage(int success, String message) {
        this.success = success;
        this.message = message;
    }

    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);
        
        try {
            setField(this, (String) hashMapPacketFields.get(in.getInt()), readUTF(in));                
        }
        catch(Exception e) {
            throw new IOException(e.getMessage());                
        }  
        
        readTail(in);
    }

    private static void setField(DialogMessage packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }
    
    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);
        out.putInt(PacketFields.SUCCESS);
        writeInt(out, success);
        out.putInt(PacketFields.MESSAGE);
        writeUTF(out, message);
        writeTail(out);
    }

    public int size() {
        return 1 + 4 + this.message.length() + 1;
    }
}
