package com.belsoft.packet;

import static com.belsoft.packet.Packet.hashMapPacketFields;
import static com.belsoft.packet.Packet.readHead;
import static com.belsoft.packet.Packet.readTail;
import static com.belsoft.packet.Packet.readUTF;
import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.mina.core.buffer.IoBuffer;

public class Ping extends Packet {

    public int tempSessionKey;
    
    public Ping() {}

    public void doWork(NetHandler nethandler) {}

    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);
        
        try {
            setField(this, (String) hashMapPacketFields.get(in.getInt()), readInt(in));                
        }
        catch(Exception e) {
            throw new IOException(e.getMessage());                
        }  
        
        readTail(in);
    }

    private static void setField(Ping packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }
    
    public void writePacket(IoBuffer out) {}


    public int size() {
        return 0;
    }
}
