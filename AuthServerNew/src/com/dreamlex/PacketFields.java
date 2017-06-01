/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dreamlex;

public class PacketFields {
    public static final int SUCCESS = 0x10001001;
    public static final int LOGIN = 0x10001002;
    public static final int PASSWORD = 0x10001003;
    public static final int USER_ID = 0x10001004;
    public static final int USER_NAME = 0x10001005;
    public static final int MESSAGE = 0x10001006;
    public static final int TEMP_SESSION_KEY = 0x10001007;
    public static final int SERVER_MESSAGE = 0x10001008;
    public static final int SERVER_MESSAGE_TYPE = 0x10001009;
    public static final int CLIENT_VERSION = 0x10001010;
    public static final int FLOATING_KEY = 0x10001011;
    public static final int DEVICE_HASH = 0x10001012;
    public static final int CLIENT_CURRENT_VERSION = 0x10001013;
    public static final int GAME_ID = 0x10001014;
    public static final int GAME_VERSION = 0x10001015;
    public static final int OFFSET_DATA = 0x10001016;
    public static final int OFFSET_DATA_LENGTH = 0x10001017;
    
    public static final int VERSION = 0x10000001;
    public static final int ROLE_ID = 0x10000002;
    public static final int COMMUNICATION_SERVER_ID = 0x10000003;
    public static final int SERVER_SERIAL = 0x10000004;
    public static final int SERVER_RESERVATION_1 = 0x10000005;
    public static final int SERVER_RESERVATION_2 = 0x10000006;
    public static final int CLIENT_SERIAL = 0x10000007;
    public static final int CLIENT_RESERVATION_1 = 0x10000008;
    public static final int CLIENT_RESERVATION_2 = 0x10000009;
    public static final int MESSAGE_ID = 0x1000000A;
    public static final int TIME = 0x1000000B;    
    public static final int PLAYER_ID = 0x1000000C;    
    public static final int NAME = 0x1000000D;    
    public static final int REALM = 0x1000000E;    
    public static final int BALANCE = 0x1000000F;    
    public static final int LAST_UPDATE = 0x10000010;    
    public static final int LEVEL = 0x10000011;    
    public static final int MAX_LEVEL = 0x10000012;    
    public static final int PLAYER_CLASS = 0x10000013;    
    public static final int COMP_NAME = 0x10000014;
    public static final int CONTROL_SUM = 0x10000015;

}
