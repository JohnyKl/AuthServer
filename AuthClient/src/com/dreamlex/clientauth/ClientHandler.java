/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dreamlex.clientauth;

import com.dreamlex.protocol.BelsoftRequest;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

public class ClientHandler extends IoHandlerAdapter {    

    public ClientHandler(Client client) { 
        userName = "";
        userId = 0;
        successLogin = 0;
        sessionTimeContinue = false;   
        
        this.client = client;
    } 
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception
    {
        log.error(Long.toString(session.getId()) +  cause.getMessage());
        
        session.close(false);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception { 
        Packet incomingPacket = ((BelsoftRequest) message).getPacket();
        
        switch (incomingPacket.messageID)
            {
                case ActionCommand.LOGIN:
                {
                    if(((Login) incomingPacket.packetBody).success == 1)
                    {
                        client.connected = true;
                        
                        client.id   = ((Login) incomingPacket.packetBody).userID;
                        client.name = ((Login) incomingPacket.packetBody).userName;   
                        client.tempSessionKey = ((Login) incomingPacket.packetBody).tempSessionKey;
                        
                        client.receivedResponse = true;
                        
                        if(checkServerMessage(((Login) incomingPacket.packetBody).serverMessageType, ((Login) incomingPacket.packetBody).serverMessage)){
                            client.startSendingPing();
                        
                            session.close(true);

                            log.info("User " + client.id + " was successfully login");                            
                        } else {
                           client.connected = false; 
                        }
                    }                     
                } break; 
                case ActionCommand.INFOMESSAGE:
                    session.close(true);
                    break;
                case ActionCommand.LOGOUT:
                {             
                    checkServerMessage(((Logout) incomingPacket.packetBody).serverMessageType, ((Logout) incomingPacket.packetBody).serverMessage);
                    
                    client.connected = false;
                    
                    client.stopSendingPing();
                    
                    session.close(true); 
                    
                    String success = ((Logout) incomingPacket.packetBody).success == 1? "successfully logout" : "logout with an error";
                    
                    log.info("User " + client.id + " was " + success);
                } break;    
                case ActionCommand.PING:
                {   
                    if(checkServerMessage(((Ping) incomingPacket.packetBody).serverMessageType, ((Ping) incomingPacket.packetBody).serverMessage)){
                        client.connected = true;

                        log.debug("Received a server response Ping");

                        sessionTimeContinue = true;
                    }                           
                    else {
                        client.connected = false;
                        
                        session.close(true);
                    }
                } break;   
                case ActionCommand.DIALOGMESSAGE:
                {
                    log.info(DIALOG_MESSAGE_TITLE + ((DialogMessage) incomingPacket.packetBody).message);
                    
                    client.connected = false;
                    
                    session.close(true);
                } break; 
        } 
    }     
    
    private boolean checkServerMessage(int messageType, String message) {
        switch(messageType){            
            case Packet.SERVER_MESSAGE_INFO: 
            case Packet.SERVER_MESSAGE_WARNING: 
                log.info(SERVER_MESSAGE_TITLE + message);
                break;
            case Packet.SERVER_MESSAGE_ERROR:
                log.error(SERVER_MESSAGE_TITLE + message);
                return false;
        }
        
        return true;
    }
    
    public String getUserName ()
    {        
        return userName;
    }
    
    public int getUserId ()
    {        
        return userId;
    }
    
    public boolean isSuccessLogin ()
    {        
        return successLogin == 1;
    }
    
    public boolean isSuccessLogout ()
    {        
        return successLogout == 1;
    }   
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ClientHandler.class);
    
    private Client client;
    private String userName;
    private int successLogin;
    private int successLogout;
    private int userId;
    
    private boolean sessionTimeContinue;  
    
    final static int port = 843;
    
    private static final String SERVER_MESSAGE_TITLE = "Message from server: ";
    private static final String DIALOG_MESSAGE_TITLE = "Message from server: ";
}
