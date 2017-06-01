package com.belsoft.packet;

//import com.belsoft.entity.Clan;
import com.belsoft.PacketFields;
import com.belsoft.entity.User;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.mina.core.buffer.IoBuffer;

public class Login extends Packet {

    public int success;
    public String login;
    public String password;
    public User user;
    public int tempSessionKey;

    public Login() {
    }

    public Login(int success, User user, int tempSessionKey) {
        this.success = success;
        this.user = user;
        this.tempSessionKey = tempSessionKey;
    }

    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);
        
        int fieldsCounter = 0;
                
        while (fieldsCounter < 2 ) {
            try {
                int fieldId = in.getInt();
                  
                switch(fieldId) {                    
                    case PacketFields.LOGIN:                         
                    case PacketFields.PASSWORD:
                         setField(this, (String) hashMapPacketFields.get(fieldId), readUTF(in));

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

    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);
        out.putInt(PacketFields.SUCCESS);        
        writeInt(out, success);
        out.putInt(PacketFields.USER_ID);    
        writeInt(out, user.getId());
        out.putInt(PacketFields.USER_NAME);          
        writeUTF(out, user.getUserName());
        out.putInt(PacketFields.TEMP_SESSION_KEY);
        writeInt(out, tempSessionKey);
        writeTail(out);
    }

    private static void setField(Login packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }
    
    public void doWork(NetHandler nethandler) {
        nethandler.login(this);
    }

    public int size() {
        return 1 + 4 + this.login.length() + 4 + this.password.length() + 1;
    }
}
