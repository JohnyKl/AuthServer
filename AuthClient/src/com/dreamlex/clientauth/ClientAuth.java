package com.dreamlex.clientauth;
 
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger; 
 
public class ClientAuth{    
      
    public static void main(String[] args) {        
        
            //RSAEncryptionManager.saveKeyPair(RSAEncryptionManager.generateKeyPair(RSAEncryptionManager.KEY_SIZE_DEFAULT));
            
            /*Thread clientThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
            Client c = new Client(1, login, password, true);//false);
            
            c.doWork();
            
            //new ClientConnector(new Client(login, password, false), serverAddress, serverPort, 1);
            }
            });
            Thread clientThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
            Client c = new Client(2, login, password, true);
            
            c.doWork();
            //new ClientConnector(new Client(login, password, true), serverAddress, serverPort, 2);
            }
            });
            Thread clientThread3 = new Thread(new Runnable() {
            @Override
            public void run() {
            Client c = new Client(3, login, password,true);
            
            c.doWork();
            ///new ClientConnector(new Client(login, password, true), serverAddress, serverPort, 3);
            }
            });
            Thread clientThread4 = new Thread(new Runnable() {
            @Override
            public void run() {
            Client c = new Client(4, "user2@example.com", password, true);
            
            c.doWork();
            //new ClientConnector(new Client("example2@mail.com", password ,true), serverAddress, serverPort, 4);
            }
            });
            
            clientThread1.start();
            clientThread2.start();
            clientThread3.start();
            clientThread4.start();
            try {
            clientThread1.join();
            clientThread2.join();
            clientThread3.join();
            clientThread4.join();
            } catch (InterruptedException ex) {
            Logger.getLogger(ClientAuth.class.getName()).log(Level.SEVERE, null, ex);
            }/**/
        try {    
            runThreads(2);
            joinThreads();
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientAuth.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    private static void joinThreads() throws InterruptedException{
        for(int i = 0; i < threads.size(); i++) {
            threads.get(i).join();
        }
    }
    
    private static void runThreads(int amount){
        for(int i = 0; i < amount; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //String login = clients.get(CLIENTS_NUMBER)[0];
                    //String password = clients.get(CLIENTS_NUMBER)[1];
                    
                    //CLIENTS_NUMBER+=2;
                    
                    Client c = new Client(CLIENTS_NUMBER, login + String.format("%03d", CLIENTS_NUMBER), password + CLIENTS_NUMBER, false);                    
                    
                    c.doWork();
                }
            });
            
            thread.start();
            
            threads.add(thread);
            //thread.join();
        }
    }
       
    
    public static int CLIENTS_NUMBER = 1;
    private final static ArrayList<Thread> threads = new ArrayList<Thread>();
    //private final static ArrayList<String[]> clients = new ArrayList<String[]>();
                                         
    private final static String login = "MsckjJXVc33zNV1ri";
    private final static String password = "password";
    
    public final static String serverAddress = "localhost";
    public final static int serverPort = 5549;
}