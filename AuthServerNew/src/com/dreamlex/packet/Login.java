package com.dreamlex.packet;

import com.dreamlex.PacketFields;
import com.dreamlex.entity.User;
import java.io.IOException;
import java.lang.reflect.Field;

import jdk.nashorn.internal.codegen.ObjectClassGenerator;
import org.apache.mina.core.buffer.IoBuffer;

public class Login extends Packet {

    public int success;
    public String login;
    public String password;
    public String userName;
    public String serverMessage;
    public int userID;
    public int tempSessionKey;
    public int serverMessageType;
    public int clientVersion;
    public int clientCurrentVersion;
    public int controlSum;
    public int deviceHash;
    public int gameID;
    public int gameVersion;
    public byte[] offsetData;

    public Login() {
    }

    public Login(int success, User user, int tempSessionKey, int serverMessageType, String serverMessage, byte[] offsetData) {
        this.success = success;
        this.tempSessionKey = tempSessionKey;
        this.serverMessage = serverMessage;
        this.serverMessageType = serverMessageType;
        this.offsetData = offsetData;

        if(user != null) {
            userID = user.getId();
            userName = user.getUserName();
        }
    }

    public void readPacket(IoBuffer in) throws IOException  {
        readHead(in);

        while(!packetEnd(in)) {
            try {
                int fieldId = in.getInt();

                if(hashMapPacketFields.containsKey(fieldId)) {
                    switch(fieldId) {
                        case PacketFields.LOGIN:
                        case PacketFields.PASSWORD:
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

        //System.out.println(deviceHash);

        if(!checkControlSum()) {
            throw new IOException("Bad control sum");
        }
        /*readHead(in);
        
        int fieldsCounter = 0;
                
        while (fieldsCounter < 5 ) {
            try {
                int fieldId = in.getInt();
                  
                switch(fieldId) {                    
                    case PacketFields.LOGIN:                         
                    case PacketFields.PASSWORD:
                         setField(this, (String) hashMapPacketFields.get(fieldId), readUTF(in));

                         fieldsCounter++;
                         break;
                    case PacketFields.CLIENT_VERSION:
                    case PacketFields.CLIENT_CURRENT_VERSION:
                        setField(this, (String) hashMapPacketFields.get(fieldId), readInt(in));

                        fieldsCounter++;

                        break;
                    case PacketFields.DEVICE_HASH:
                        //readDeviceHash(in);

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
        
        readTail(in);*/
    }

    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);
        out.putInt(PacketFields.SUCCESS);        
        writeInt(out, success);
        out.putInt(PacketFields.USER_ID);    
        writeInt(out, userID);
        out.putInt(PacketFields.USER_NAME);          
        writeUTF(out, userName);

        if(serverMessage != null) {
            out.putInt(PacketFields.SERVER_MESSAGE_TYPE);
            writeInt(out, serverMessageType);
            out.putInt(PacketFields.SERVER_MESSAGE);
            writeUTF(out, serverMessage);
        }

        out.putInt(PacketFields.TEMP_SESSION_KEY);
        writeInt(out, tempSessionKey);
        out.putInt(PacketFields.OFFSET_DATA_LENGTH);
        writeInt(out, offsetData.length);
        out.putInt(PacketFields.OFFSET_DATA);
        out.put (offsetData);
        out.putInt(PacketFields.CONTROL_SUM);
        writeInt(out, countControlSum());
        writeTail(out);
    }

    private static void setField(Login packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
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
    
    public void doWork(NetHandler nethandler) {
        nethandler.login(this);
    }

    public int size() {
        return 1 + 4 + login.length() + 4 + password.length() + 1;
    }

    @Override
    public String toString() {
        return "Login";
    }
}
