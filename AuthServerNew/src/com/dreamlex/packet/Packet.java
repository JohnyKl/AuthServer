package com.dreamlex.packet;

import com.dreamlex.ActionCommand;
import com.dreamlex.DebugLog;
import com.dreamlex.PacketFields;
import com.dreamlex.managers.RSAEncryptionManager;
import java.io.DataInputStream;
import java.io.UTFDataFormatException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Packet {

    final static byte byteType = 0x00;
    final static byte shortType = 0x01;
    final static byte intType = 0x02;
    final static byte floatType = 0x03;
    final static byte UTFType = 0x04;
    final static byte doubleType = 0x07;
    final static byte longType = 0x10;

    final static byte headMarker = (byte)0xaa;
    final static byte tailMarker = (byte)0x55;

    public final static int SERVER_MESSAGE_INFO = 0x50005001;
    public final static int SERVER_MESSAGE_WARNING = 0x50005002;
    public final static int SERVER_MESSAGE_ERROR = 0x50005003;

    //private static final Logger log = LoggerFactory.getLogger(Packet.class);
    private static String defaultPolicy = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>";

    public static Map hashMapPacketFields = new HashMap();
    private static Map hashMapPacketClass = new HashMap();
    private static Map hashMapReversePacketClass = new HashMap();
    public final long j = System.currentTimeMillis();
    public boolean k = false;
    private static HashMap c;
    private static int packetsCount;

    private int size;
    public int version;
    public int roleId;
    public int communicationsServerID;
    public int severSerial;
    public int serverReservation1;
    public int serverReservation2;
    public int clientSerial;
    public int clientReservations1;
    public int clientReservations2;
    public int messageID;
    public long time;
    public Packet packetBody;

    public Packet() {}

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    static void addPacketFields() {
        hashMapPacketFields.put(PacketFields.SUCCESS, "success");
        hashMapPacketFields.put(PacketFields.LOGIN, "login");
        hashMapPacketFields.put(PacketFields.PASSWORD, "password");
        hashMapPacketFields.put(PacketFields.USER_NAME, "userName");
        hashMapPacketFields.put(PacketFields.USER_ID, "userID");
        hashMapPacketFields.put(PacketFields.MESSAGE, "message");
        hashMapPacketFields.put(PacketFields.TEMP_SESSION_KEY, "tempSessionKey");
        hashMapPacketFields.put(PacketFields.SERVER_MESSAGE, "serverMessage");
        hashMapPacketFields.put(PacketFields.SERVER_MESSAGE_TYPE, "serverMessageType");
        hashMapPacketFields.put(PacketFields.CLIENT_VERSION, "clientVersion");
        hashMapPacketFields.put(PacketFields.FLOATING_KEY, "floatingKey");
        hashMapPacketFields.put(PacketFields.DEVICE_HASH, "deviceHash");
        hashMapPacketFields.put(PacketFields.CLIENT_CURRENT_VERSION, "clientCurrentVersion");
        hashMapPacketFields.put(PacketFields.CONTROL_SUM, "controlSum");
        hashMapPacketFields.put(PacketFields.GAME_ID, "gameID");
        hashMapPacketFields.put(PacketFields.GAME_VERSION, "gameVersion");
        hashMapPacketFields.put(PacketFields.OFFSET_DATA, "offsetData");
        hashMapPacketFields.put(PacketFields.OFFSET_DATA_LENGTH, "offsetDataLength");

        hashMapPacketFields.put(PacketFields.VERSION, "version");
        hashMapPacketFields.put(PacketFields.ROLE_ID, "roleId");
        hashMapPacketFields.put(PacketFields.COMMUNICATION_SERVER_ID, "communicationsServerID");
        hashMapPacketFields.put(PacketFields.SERVER_SERIAL, "severSerial");
        hashMapPacketFields.put(PacketFields.SERVER_RESERVATION_1, "serverReservation1");
        hashMapPacketFields.put(PacketFields.SERVER_RESERVATION_2, "serverReservation2");
        hashMapPacketFields.put(PacketFields.CLIENT_SERIAL, "clientSerial");
        hashMapPacketFields.put(PacketFields.CLIENT_RESERVATION_1, "clientReservations1");
        hashMapPacketFields.put(PacketFields.CLIENT_RESERVATION_2, "clientReservations2");
        hashMapPacketFields.put(PacketFields.MESSAGE_ID, "messageID");
        hashMapPacketFields.put(PacketFields.TIME, "time");        
        hashMapPacketFields.put(PacketFields.PLAYER_ID, "playerID");
        hashMapPacketFields.put(PacketFields.BALANCE, "balance");
        hashMapPacketFields.put(PacketFields.LAST_UPDATE, "lastUpdate");
        hashMapPacketFields.put(PacketFields.LEVEL, "level");
        hashMapPacketFields.put(PacketFields.MAX_LEVEL, "maxLevel");
        hashMapPacketFields.put(PacketFields.COMP_NAME, "compName");
    }    

    static void addPacketClass(int i, Class oclass) {
        if (hashMapPacketClass.containsKey(Integer.valueOf(i))) {
            throw new IllegalArgumentException("Duplicate packet id:" + i);
        } else if (hashMapReversePacketClass.containsKey(oclass)) {
            throw new IllegalArgumentException("Duplicate packet class:" + oclass);
        } else {
            hashMapPacketClass.put(Integer.valueOf(i), oclass);
            hashMapReversePacketClass.put(oclass, Integer.valueOf(i));
        }
    }

    public static Packet getPacketClass(int i) {
        try {
            Class oclass = (Class) hashMapPacketClass.get(Integer.valueOf(i));

            return oclass == null ? null : (Packet) oclass.newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
            DebugLog.debug("Skipping packet with id " + i);
            return null;
        }
    }

    public final int getPacketId() {
        //return ((Integer) hashMapReversePacketClass.get(this.getClass()));
        return ((Integer) hashMapReversePacketClass.get(this.getClass())).intValue();
    }

    public static Packet loadPacket(IoBuffer in)  throws IOException {
        Packet packet = new Packet() {

            @Override
            public void readPacket(IoBuffer in) throws IOException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void writePacket(IoBuffer out) throws IOException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int size() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String toString() { throw new UnsupportedOperationException("Not supported yet."); }
        };


            if (in.remaining() >= 84) {
                try{
                    decrypt(in);
                }
                catch (Exception ex) {
                    DebugLog.error("line 168: " + ex.getMessage());

                    throw new IOException(ex.getMessage());
                }

                if(readPacketFields(in, packet)){
                    packet.packetBody = getPacketClass(packet.messageID);

                    if (packet.packetBody == null) {
                        throw new IOException("Bad packet id " + Integer.toHexString(packet.messageID));
                    }
                    packet.packetBody.readPacket(in);
                }
                else {
                    throw new IOException("Bad packet id " + Integer.toHexString(packet.messageID));
                }
                
                in.skip(in.remaining());
                                       
            } else {                
                throw new IOException("Bad packet id " + Integer.toHexString(packet.messageID));
            }           


        return packet;
    }
    
    private static boolean readPacketFields(IoBuffer in, Packet packet) {
        int fieldsCounter = 0;
        boolean isReversePacket = false;
        
        while (fieldsCounter < 10 ) {
            try {
                int fieldId = in.getInt();
                        
                if(hashMapPacketFields.containsKey(fieldId)){
                    switch(fieldId) {
                        case PacketFields.TIME:
                            setField(packet, (String) hashMapPacketFields.get(fieldId), in.getLong());
                                                        
                            if(fieldsCounter == 0) isReversePacket = true;                              
                            else {
                                return true;
                            }
                            
                            fieldsCounter++;
                            break; 
                        case PacketFields.VERSION:
                            setField(packet, (String) hashMapPacketFields.get(fieldId), in.getInt());
                            
                            if(isReversePacket) return true;
                            
                            fieldsCounter++;
                            break; 
                        case PacketFields.MESSAGE_ID:
                            setField(packet, (String) hashMapPacketFields.get(fieldId), in.getInt()& 0xFFFFFFFE);
                            
                            fieldsCounter++;
                            break;  
                        default: 
                            setField(packet, (String) hashMapPacketFields.get(fieldId), in.getInt());
                            
                            fieldsCounter++;
                    }
                }
                else {
                    return false;
                }
            }
            catch(Exception e) {
                return false;
            }     
        }
        
        return false;
    }
    
    private static void setField(Packet packet, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field;
            
        field = (packet.getClass()).getField(fieldName);

        field.set(packet, value);
    }
    
    private static void decrypt(IoBuffer buffer) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, 
            BadPaddingException {
        int size = buffer.limit();

        byte[] encrypted = new byte[size];

        System.arraycopy(buffer.array(), 0, encrypted, 0, size);

        Cipher rsa = Cipher.getInstance("RSA");

        rsa.init(Cipher.DECRYPT_MODE, RSAEncryptionManager.loadPrivateKey());

        buffer.sweep();

        byte[][] splitted = splitArrayToDecode(encrypted, RSAEncryptionManager.KEY_SIZE_DEFAULT/ 8);

        for(int i = 0; i < splitted.length; i++) {
            buffer.put(rsa.doFinal(splitted[i]));
        }

        //byte[] decrypted = rsa.doFinal(encrypted);
        //buffer.put(decrypted);

        buffer.flip(); 
    }

    private static byte[][] splitArrayToDecode(byte[] array, int maxSize) {
        int blocksNumber = (int) Math.ceil((double) array.length / maxSize);

        byte[][] splitted = new byte[blocksNumber][maxSize];

        for(int i = 0; i < array.length; i += maxSize){
            splitted[i/maxSize] = Arrays.copyOfRange(array, i, maxSize + i);
        }

        return splitted;
    }

    public int getPacketSize(DataInputStream datainputstream)  throws IOException {
        size = datainputstream.readInt();
        return size;
    }

    public static void sendPolicy(IoBuffer out)  throws IOException {
        DebugLog.debug("Policy sended");
        out.put(defaultPolicy.getBytes());
        out.put((byte)0);
    }

    public void savePacket(Packet packet, IoBuffer out)  throws IOException {
        int capacity = 44;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.setAutoExpand(true);

        buffer.putInt(PacketFields.VERSION);
        buffer.putInt(0x01);
        buffer.putInt(PacketFields.COMMUNICATION_SERVER_ID);
        buffer.putInt(0x00);
        buffer.putInt(PacketFields.SERVER_SERIAL);
        buffer.putInt(0x00);
        buffer.putInt(PacketFields.SERVER_RESERVATION_1);
        buffer.putInt(0x00);
        buffer.putInt(PacketFields.SERVER_RESERVATION_2);
        buffer.putInt(0x00);
        buffer.putInt(PacketFields.CLIENT_SERIAL);
        buffer.putInt(clientSerial);
        buffer.putInt(PacketFields.CLIENT_RESERVATION_1);
        buffer.putInt(clientReservations1);
        buffer.putInt(PacketFields.CLIENT_RESERVATION_2);
        buffer.putInt(clientReservations2);
        buffer.putInt(PacketFields.MESSAGE_ID);
        buffer.putInt(packet.getPacketId() | 0x00000001);
        buffer.putInt(PacketFields.TIME);
        buffer.putLong(System.currentTimeMillis());
        packet.writePacket(buffer);

        buffer.flip();

        out.putInt(buffer.remaining());
        out.put(buffer);
    }

    public static byte readByte(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        byte res = in.get();
        if (tmp != byteType) {
            throw new IllegalArgumentException("Uncorrect data type: " + tmp);
        }
        return res;
    }

    public static short readShort(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        short res = in.getShort();
        if (tmp != shortType) {
            throw new IllegalArgumentException("Uncorrect data type: " + tmp);
        }
        return res;
    }

    public static int readInt(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        int res = in.getInt();
        if (tmp != intType) {
            throw new IllegalArgumentException("Uncorrect data type: " + tmp);
        }
        return res;
    }

    public static long readLong(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        long res = in.getLong();
        if (tmp != longType) {
            throw new IllegalArgumentException("Uncorrect data type: " + tmp);
        }
        return res;
    }

    public static float readFloat(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        float res = in.getFloat();
        if (tmp != floatType) {
            throw new IllegalArgumentException("Uncorrect data type: " + tmp);
        }
        return res;
    }

    public static String readUTF(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        if (tmp != UTFType) {
            throw new IllegalArgumentException("Uncorrect data type: " + tmp);
        }
        int utflen = in.getShort();
        byte[] bytearr = null;
        char[] chararr = null;
        bytearr = new byte[utflen*2];
        chararr = new char[utflen*2];

        int c, char2, char3;
        int count = 0;
        int chararr_count=0;

//        DataInputStream.readUTF(null)
        in.get(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++]=(char)c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++]=(char)c;
                    break;
                case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                            "malformed input: partial character at end");
                    char2 = (int) bytearr[count-1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException(
                            "malformed input around byte " + count);
                    chararr[chararr_count++]=(char)(((c & 0x1F) << 6) |
                                                    (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException(
                            "malformed input: partial character at end");
                    char2 = (int) bytearr[count-2];
                    char3 = (int) bytearr[count-1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException(
                            "malformed input around byte " + (count-1));
                    chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
                                                    ((char2 & 0x3F) << 6)  |
                                                    ((char3 & 0x3F) << 0));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException(
                        "malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);
    }

    public static boolean packetEnd(IoBuffer in) throws IOException {
        byte[] buffer = in.array();

        return buffer[in.position()] == tailMarker;
    }

    public static void readHead(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        if (tmp != headMarker) {
            throw new IllegalArgumentException("Uncorrect header marker type: " + tmp);
        }
    }

    public static void readTail(IoBuffer in)  throws IOException {
        byte tmp = in.get();
        if (tmp != tailMarker) {
            throw new IllegalArgumentException("Uncorrect tail marker type: " + tmp);
        }
    }

    public static void writeByte(IoBuffer out, byte data)  throws IOException {
        out.put(byteType);
        out.put(data);
    }

    public static void writeShort(IoBuffer out, short data)  throws IOException {
        out.put(shortType);
        out.putShort(data);
    }

    public static void writeInt(IoBuffer out, int data)  throws IOException {
        out.put(intType);
        out.putInt(data);
    }

    public static void writeFloat(IoBuffer out, float data)  throws IOException {
        out.put(floatType);
        out.putFloat(data);
    }
    
    public static void writeLong(IoBuffer out, long data)  throws IOException {
        out.put(longType);
        out.putLong(data);
    }

    public static void writeUTF(IoBuffer out, String data)  throws IOException {
        int strlen;
        out.put(UTFType);
        if (data != null) {
            strlen = data.length();
        } else {
            strlen = 0;
        }
	int utflen = 0;
	int c, count = 0;

        /* use charAt instead of copying String to char array */
	for (int i = 0; i < strlen; i++) {
            c = data.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		utflen++;
	    } else if (c > 0x07FF) {
		utflen += 3;
	    } else {
		utflen += 2;
	    }
	}

	if (utflen > 65535)
	    throw new UTFDataFormatException(
                "encoded string too long: " + utflen + " bytes");

        byte[] bytearr = null;
        bytearr = new byte[(utflen*2) + 2];

	bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
	bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i=0;
        for (i=0; i<strlen; i++) {
           c = data.charAt(i);
           if (!((c >= 0x0001) && (c <= 0x007F))) break;
           bytearr[count++] = (byte) c;
        }

	for (;i < strlen; i++){
            c = data.charAt(i);
	    if ((c >= 0x0001) && (c <= 0x007F)) {
		bytearr[count++] = (byte) c;

	    } else if (c > 0x07FF) {
		bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
		bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
		bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
	    } else {
		bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
		bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
	    }
	}
        out.put(bytearr, 0, utflen+2);
//        return utflen + 2;
    }

    public static void writeHead(IoBuffer out)  throws IOException {
        out.put(headMarker);
    }

    public static void writeTail(IoBuffer out)  throws IOException {
        out.put(tailMarker);
    }

    public abstract void readPacket(IoBuffer in) throws IOException;

    public abstract void writePacket(IoBuffer out) throws IOException;

    public abstract int size();

    @Override
    public abstract String toString();

    static {
        addPacketFields();
        
        addPacketClass(ActionCommand.DIALOGMESSAGE, DialogMessage.class);
        addPacketClass(ActionCommand.PING, Ping.class);
        addPacketClass(ActionCommand.LOGIN, Login.class);
        addPacketClass(ActionCommand.LOGOUT, Logout.class);
        addPacketClass(ActionCommand.INFOMESSAGE, InfoMessage.class);

        c = new HashMap();
        packetsCount = 0;
    }
}
