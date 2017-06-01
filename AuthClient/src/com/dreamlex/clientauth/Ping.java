package com.dreamlex.clientauth;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.mina.core.buffer.IoBuffer;

public class Ping extends Packet {
    private int sessionKey;
    public String serverMessage;
    public int serverMessageType;
    public int floatingKey;
    public int controlSum;
    
    public Ping() {}
    
    public Ping(int sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void doWork(NetHandler nethandler) {}

    @Override
    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);
        
        while(!packetEnd(in)) {
            try {
                int fieldId = in.getInt();
                
                if(hashMapPacketFields.containsKey(fieldId)) {                
                    switch(fieldId) {                    
                        case PacketFields.SERVER_MESSAGE:
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
        out.putInt(PacketFields.TEMP_SESSION_KEY);
        writeInt(out, sessionKey);
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
        return 1 + 4 + serverMessage.length() + 1;
    }
}
