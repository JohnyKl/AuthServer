/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dreamlex.clientauth;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.mina.core.buffer.IoBuffer;

public class InfoMessage extends Packet {
   /* player_id - сквозной номер играющего, связанный по email
name - имя персонажа
realm - имя игрового мира
balance - гол-во голды у игрока
last_update - время текущего обновления
level - уровень игрока
maxlevel - максимальный уровень игрока
player_class - битовое число, описывающее класс игрока и рассу
comp_name */
    int playerID;
    String name;
    String realm;
    float balance;
    long lastUpdate;
    int level;
    int maxLevel;
    int playerClass;
    String compName;
    int success;
    int tempSessionKey;
    public int controlSum;
    
    public InfoMessage() {
        
    }
    
    public InfoMessage(int playerID, int sessionKey, float balance, long lastUpdate, 
                       int level,    int maxLevel,   String compName) {
        this.playerID = playerID;
        this.balance = balance;
        this.lastUpdate = lastUpdate;
        this.level = level;
        this.maxLevel = maxLevel;
        this.compName = compName;
        this.tempSessionKey = sessionKey;       
    }
    
    @Override
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
        
        readTail(in);    
                
        if(!checkControlSum()) {
            throw new IOException("Bad control sum");
        }
    }

    @Override
    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);        
        out.putInt(PacketFields.PLAYER_ID);   
        writeInt(out, playerID);
        out.putInt(PacketFields.TEMP_SESSION_KEY);   
        writeInt(out, tempSessionKey);
        out.putInt(PacketFields.BALANCE);
        writeFloat(out, balance);
        out.putInt(PacketFields.LEVEL);
        writeInt(out, level);
        out.putInt(PacketFields.MAX_LEVEL);
        writeInt(out, maxLevel);
        out.putInt(PacketFields.COMP_NAME);
        writeUTF(out, compName);
        out.putInt(PacketFields.LAST_UPDATE);   
        writeLong(out, lastUpdate);
        out.putInt(PacketFields.CONTROL_SUM);
        writeInt(out, countControlSum());
        writeTail(out);
    }
    
    private static void setField(InfoMessage packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }

    public void doWork(NetHandler nethandler) { }

    private int countControlSum() {
        return 1;
    }
    
    private boolean checkControlSum() {
        return true;
    }
    
    @Override
    public int size() {
        return 1 + 24 + this.compName.length() + 24 + 1;
        //return 1 + this.login.length() + this.password.length() + 1;
    }
}
