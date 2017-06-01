package com.dreamlex.clientauth;

import com.dreamlex.protocol.BelsoftCodecFactory;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import static org.apache.mina.example.imagine.step1.client.ImageClient.CONNECT_TIMEOUT;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class ClientConnector {

    public ClientConnector(Client client, int clientId) {
        //this.client = client;
        
        createConnector(clientId, client);
        //createConnectFuture(serverAddress, serverPort);  
    }
    
    public void dispose() {
        connector.dispose();
    }  
    
    public boolean sendPacketToServer(final Packet packetToSend){
        boolean success = true;
        
        ConnectFuture connFuture = connector.connect(new InetSocketAddress(ClientAuth.serverAddress, ClientAuth.serverPort));   

        connFuture.awaitUninterruptibly(); 
        
        try {
            connFuture.addListener(new IoFutureListener<ConnectFuture>() {

            @Override
            public void operationComplete(ConnectFuture future) {
                if (future.isConnected()) {                    
                    //try {
                        future.getSession().write(packetToSend);
                    //}  
                    //catch (Exception e) {                        
                        //future.cancel();
                        
                        //throw e;
                    //}
                }
                else {
                    future.cancel();
                }
            }});      
        /*connFuture.addListener(new IoFutureListener<CloseFuture>() {

            @Override
            public void operationComplete(CloseFuture f) {
                f.getSession().close(true);
                
                System.out.println("Session is closing now, send packet class: " + packetToSend./*packetBody.getClass().getName());
            }
            
        });*/
        } catch (Exception e) {
            success = false;
        }
        
        return success;
    }
    
    private void createConnector(int id, Client client) {
        connector = new NioSocketConnector();
                
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        
        connector.setHandler(new ClientHandler(client));
        connector.getFilterChain().addLast("protocol " + Integer.toString(id), new ProtocolCodecFilter(new BelsoftCodecFactory(false)));            
        connector.getFilterChain().addLast("logger-client "+ Integer.toString(id), new LoggingFilter());
    }    
        
    /*private void createConnectFuture( String serverAddress, int serverPort) {
        ConnectFuture connFuture = connector.connect(new InetSocketAddress(serverAddress, serverPort));   

        connFuture.awaitUninterruptibly(); 
        
        connFuture.addListener(new IoFutureListener<ConnectFuture>() {

            @Override
            public void operationComplete(ConnectFuture future) {
                if (future.isConnected()) {                    
                    client.session = future.getSession();
                    
                    client.login(future.getSession());                    
                    
                    try {
                        int workTime = 0;
                        int sleepTime = 2500;
                        
                        do {
                            if(client.session.isConnected()) {
                                Thread.sleep(sleepTime);
                                
                                workTime += sleepTime;
                                
                                System.out.println("Total time: " + Integer.toString(workTime));
                            }
                        } while (workTime < 2 * 60 * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientAuth.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    client.logout(future.getSession()); 
                }
                else 
                {
                    future.cancel();
                }
            }            
        }); 
    }*/
    
    //private final int serverPort;
    //private final String serverAddress;
    //private final Client client;
    private NioSocketConnector connector;
}
