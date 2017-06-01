package com.dreamlex.clientauth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.swing.Timer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

public class Client {
    public String login;
    public String password;
    public String name;
    public int tempSessionKey;
    public int id;
    public int maxSessionNumber;
    //public ArrayList<IoSession> sessions;
    public IoSession session; 
    
    public boolean receivedResponse;
    public boolean connected;
    
    public Client() {
        this(0, "", "", true);
    }
            
    public Client (int clientId, String login, String password, boolean sendPing)
    {
        this.login    = login;
        this.password = password;
        this.name     = "";
        this.id       = clientId;
        this.maxSessionNumber = 1;
        this.sendPing = sendPing;
        this.tempSessionKey = 0;
        
        connected = false;
        receivedResponse = false;
        
        connector = new ClientConnector(this, clientId);
        initTimer();
    }
    
    private void initTimer() {
        timer = new Timer(TIMER_TICK, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(connected){ 
                    if(sendPing){
                        if(!connector.sendPacketToServer(new Ping(tempSessionKey))) {
                            stop();
                        } else {
                            log.debug("PING");
                        }   
                    }
                }
                else {
                    stop();
                }
            }
        });        
    }
    
    private byte[] getDeviceHash() {
        byte[] hash = null;
        
        try {
            String str =  "hash";//Long.toString(rng.nextLong() / System.currentTimeMillis());
            
            hash = MessageDigest.getInstance("MD5").digest(str.getBytes());
            
            log.debug("Length of device hash" + hash.length);
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
        
        return new byte[] {hash[0],hash[1],hash[2],hash[3]};
    }
    
    public void doWork() {        
        if(!connector.sendPacketToServer(new Login(login, password, CLIENT_VERSION, CLIENT_VERSION, getDeviceHash(), GAME_ID, GAME_VERSION))) {
            stop();
        } else {
            try {
                //connected = true;
                
                int workTime = 0;
                int sleepTime = TIMER_TICK / 2;
                
                while(!receivedResponse){
                    log.info("Connecting to server...");
                    
                    Thread.sleep(sleepTime);                    
                }
                
                while (workTime < TIMER_TICK * 4) {
                    if(connected) {
                        Thread.sleep(sleepTime);

                        workTime += sleepTime;
                    }
                    else {
                        log.info("Work is stopping now");
                        
                        stop();
                        
                        return;
                    }
                }
                
                if(connected) {
                    connector.sendPacketToServer(new Logout(id, tempSessionKey));                
                
                    log.info("Sent Logout packet");
                    
                    Thread.sleep(sleepTime*3);                  
                }
                
                
                
                stop();
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
            }
        }
    }
    
    public void startSendingPing() {
        timer.start();
        
        log.info("Client started sending a ping");
    } 
        
    public void stopSendingPing() {
        timer.stop();
        
        stop();
        
        log.info("Client finished sending a ping");
    }
    
    /*public void login(IoSession session){
        session.write(new Login(login, password));        
    }
    
    public void logout(IoSession session){
        session.write(new Logout(id)); 
        
        connector.dispose();
    }  */
    
    public void setPingTime(int time) {
        timer.setDelay(time);
    }
    
    public void stop() {
        timer.stop();
                        
        connector.dispose();
        
        log.info("Client finished his work");
    }
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Client.class);
    
    private ClientConnector connector;
    private boolean sendPing;
    private Timer timer;
    
    private static final Random rng = new Random();
    
    private static final int CLIENT_VERSION = 1;
    private static final int GAME_ID = 1;    
    private static final int GAME_VERSION = 2;
    
    private static final int TIMER_TICK = 30000;//1000 * 60 * 2;
}
