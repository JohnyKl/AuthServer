package com.belsoft.packet;

import com.belsoft.PacketFields;
import static com.belsoft.packet.Packet.hashMapPacketFields;
import static com.belsoft.packet.Packet.readHead;
import static com.belsoft.packet.Packet.readTail;
import static com.belsoft.packet.Packet.readUTF;
import static com.belsoft.packet.Packet.writeHead;
import static com.belsoft.packet.Packet.writeInt;
import static com.belsoft.packet.Packet.writeTail;
import static com.belsoft.packet.Packet.writeUTF;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.mina.core.buffer.IoBuffer;

public class Logout extends Packet {

    public int success;
    public int userID;
    public int tempSessionKey;

    public Logout() {
    }

    public Logout(int success) {
        this.success = success;
    }

    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);
        
        int fieldsCounter = 0;
                
        while (fieldsCounter < 2 ) {
            try {
                int fieldId = in.getInt();
                  
                switch(fieldId) {                    
                    case PacketFields.TEMP_SESSION_KEY:                         
                    case PacketFields.USER_ID:
                         setField(this, (String) hashMapPacketFields.get(fieldId), readInt(in));

                         fieldsCounter++;
                         break; 
                    default :
                        throw new IOException("Bad packet`s field id");
                }  
            }
            catch(Exception e) {
                throw new IOException(e.getMessage());                
            }         
        }       
        
        readTail(in);
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
        writeTail(out);
    }

    public void doWork(NetHandler nethandler) {
        //nethandler.logout(this);
    }

    public int size() {
        return 1 + 4 + 4 + 1;
    }
}
