/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.belsoft.packet;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.mina.core.buffer.IoBuffer;
import com.belsoft.PacketFields;

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
    public int playerID;
    public String name;
    public String realm;
    public float balance;
    public long lastUpdate;
    public int level;
    public int maxLevel;
    public int playerClass;
    public String compName;    
    public int tempSessionKey;
    int success;
    
    public InfoMessage() {
        
    }
    
    public InfoMessage(int success) {
        this.success = success;
    }
    
    @Override
    public void readPacket(IoBuffer in) throws IOException {
        readHead(in);
        
        int fieldsCounter = 0;
        boolean isReversePacket = false;
        boolean packetEnd = false;
        
        while (fieldsCounter < 7 && !packetEnd) {
            try {
                int fieldId = in.getInt();
                        
                if(hashMapPacketFields.containsKey(fieldId)){
                    switch(fieldId) {                        
                        case PacketFields.COMP_NAME:
                            setField(this, (String) hashMapPacketFields.get(fieldId), readUTF(in));                            
                                   
                            fieldsCounter++;
                            break;   
                        case PacketFields.LAST_UPDATE:
                            setField(this, (String) hashMapPacketFields.get(fieldId), readLong(in));                            
                            
                            if(fieldsCounter == 0) isReversePacket = true;                              
                            else packetEnd = true;
                                   
                            fieldsCounter++;
                            break; 
                        case PacketFields.BALANCE:
                            setField(this, (String) hashMapPacketFields.get(fieldId), readFloat(in));
                                                        
                            fieldsCounter++;
                            break;   
                        case PacketFields.PLAYER_ID:
                            if(isReversePacket) packetEnd = true;                             
                        default: 
                            setField(this, (String) hashMapPacketFields.get(fieldId), readInt(in));
                            
                            fieldsCounter++;
                            
                            break;
                    }
                }
                else {
                    throw new IOException("Bad packet`s field id");
                }
            }
            catch(Exception e) {
                throw new IOException(e.getMessage());
            }     
        }        
        readTail(in);     
    }

    @Override
    public void writePacket(IoBuffer out) throws IOException {
        writeHead(out);      
        out.putInt(PacketFields.SUCCESS);
        writeInt(out, success);
        /*out.putInt(PacketFields.PLAYER_ID);   
        writeInt(out, playerID);
        out.putInt(PacketFields.BALANCE);
        writeFloat(out, balance);
        out.putInt(PacketFields.LEVEL);
        writeInt(out, level);
        out.putInt(PacketFields.MAX_LEVEL);
        writeInt(out, maxLevel);
        out.putInt(PacketFields.COMP_NAME);
        writeUTF(out, compName);
        out.putInt(PacketFields.LAST_UPDATE);   
        writeLong(out, lastUpdate);*/
        writeTail(out);
    }
    
    private static void setField(InfoMessage packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }

    public void doWork(NetHandler nethandler) { }

    @Override
    public int size() {
        return 1 + 24 + this.compName.length() + 24 + 1;
        //return 1 + this.login.length() + this.password.length() + 1;
    }
}
