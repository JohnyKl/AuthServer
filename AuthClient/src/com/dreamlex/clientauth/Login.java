package com.dreamlex.clientauth;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import org.apache.mina.core.buffer.IoBuffer;

public class Login extends Packet {

    public int success;
    public String login;
    public String password;
    public int userID;
    public String userName;
    public int tempSessionKey;
    public String serverMessage;   
    public int serverMessageType;
    public int clientVersion;
    public int clientCurrentVersion;
    public byte[] deviceHash;
    public int controlSum;
    public int gameID;
    public int gameVersion;
    public int offsetDataLength;
    public byte[] offsetData;

    public Login() {
    }

    public Login(String login, String password, int clientVersion, int currentClientVersion, byte[] deviceHash, int gameID, int gameVersion) {
        this.login = login;
        this.password = password;
        this.clientVersion = clientVersion;
        this.clientCurrentVersion = currentClientVersion;
        this.deviceHash = deviceHash;
        this.gameID = gameID;
        this.gameVersion = gameVersion;
    }
    
    @Override
    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);
        
        while(!packetEnd(in)) {
            try {
                int fieldId = in.getInt();
                
                if(hashMapPacketFields.containsKey(fieldId)) {                
                    switch(fieldId) {   
                        case PacketFields.SERVER_MESSAGE:
                        case PacketFields.USER_NAME:  
                            setField(this, (String) hashMapPacketFields.get(fieldId), readUTF(in));
                            break;                            
                        case PacketFields.OFFSET_DATA:  
                            offsetData = new byte[offsetDataLength];
                            
                            in.get(offsetData);
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
    
    @Override
    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);
        out.putInt(PacketFields.LOGIN);   
        writeUTF(out, login);
        out.putInt(PacketFields.PASSWORD);   
        writeUTF(out, password);        
        out.putInt(PacketFields.CLIENT_VERSION);   
        writeInt(out, clientVersion);
        out.putInt(PacketFields.CLIENT_CURRENT_VERSION);   
        writeInt(out, clientCurrentVersion);
        out.putInt(PacketFields.GAME_ID);   
        writeInt(out, gameID);
        out.putInt(PacketFields.GAME_VERSION);   
        writeInt(out, gameVersion);
        out.putInt(PacketFields.DEVICE_HASH); 
        //writeUTF(out, new String(deviceHash));
        //writeInt(out, 1234);
        writeInt(out, ByteBuffer.wrap(deviceHash).getInt());
        out.putInt(PacketFields.CONTROL_SUM);
        writeInt(out, countControlSum());
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
    
    private int countControlSum() {
        return 1;
    }
    
    private boolean checkControlSum() {
        return true;
    }

    @Override
    public int size() {
        return 1 + this.userName.length() + 32 + serverMessage.length() + 1;
        //return 1 + this.login.length() + this.password.length() + 1;
    }
}
