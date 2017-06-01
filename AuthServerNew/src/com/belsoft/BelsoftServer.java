/**
 * Copyright (C) 2011 - Alexey Kolenko
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package com.belsoft;


import com.belsoft.config.ServerConfig;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
//import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
//import org.apache.mina.transport.socket.SocketAcceptorConfig;

import java.io.*;
import java.net.*;

//import com.belsoft.classloader.*;
import com.belsoft.protocol.*;
import com.belsoft.managers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BelsoftServer
{
    private static final Logger log = LoggerFactory.getLogger(BelsoftServer.class);

    public static Configuration config = new Configuration();
    public static String version = "0.0.0.1";    
    public static String policyFile = "";
    public static int idleTimeBeforeDisconnect = 120;
    public static int maximumConnections = 1000;
    public static ICustomLogic objCustomLogicClass = null;
    public static boolean runCustomLogic = false;
    public static String mysqlConnectionString = "";
    public static String mysqlUsername = "";
    public static String mysqlPassword = "";
    public static boolean manageAccounts = true;

    public static BelsoftProtocolHandler protocolHandler;
    
    private static int port = 843;
    private static IoAcceptor acceptor = null;
    

    public static void main(String[] args) throws Exception
    {
        try
        {
            for(int i = 0; i < args.length; i++)
            {
                if(args[i].contains("-mysql=")) { mysqlConnectionString = args[i].replace("-mysql=", "").trim(); }
                if(args[i].contains("-port="))  { port = Integer.parseInt(args[i].replace("-port=", "").trim()); }
                if(args[i].contains("-mysqlusername=")) { mysqlUsername = args[i].replace("-mysqlusername=", "").trim(); }
                if(args[i].contains("-mysqlpassword=")) { mysqlPassword = args[i].replace("-mysqlpassword=", "").trim(); }
                if(args[i].contains("-idletimeout=")) { idleTimeBeforeDisconnect = Integer.parseInt(args[i].replace("-idletimeout=", "").trim()); }
                if(args[i].contains("-manageaccounts=")) {manageAccounts = Boolean.parseBoolean(args[i].replace("-manageaccounts=", "").trim());}
            }            
        }
        catch(Exception ex)
        {
            log.error(ex.getMessage());
            log.error("There was an error reading the command line arguments");
            return;            
        }
                
        try
        {            
            ServerProperties propertyReader = new ServerProperties();
            
            policyFile = propertyReader.LoadCrossDomainPolicy();            
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
            log.error("There was an error reading the server.xml file. Belsoft Server will be shutdown");
            return;
        }

        try
        {
            config.readXMLDocument("sdsdsdsd");
            log.info("Belsoft Server Version: " + version);
            log.info("Cross Domain Policy: " + policyFile);
            log.info("Belsoft Server is allowed to manage accounts: " + manageAccounts);
            log.info("Idle time before disconnect: " + idleTimeBeforeDisconnect);
            
            // Custom Class Loader
            if(checkIfCustomLogicClassFileExists())
            {
                runCustomLogic = true;
                System.out.println("CustomLogic.jar file was found");
            }

            acceptor = new NioSocketAcceptor();

            acceptor.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new BelsoftCodecFactory(false)));
            acceptor.getFilterChain().addLast("logging", new LoggingFilter());

            ServerConfig serverConfig = new ServerConfig();
            
            protocolHandler = new BelsoftProtocolHandler(serverConfig.getPingTimeout());
            acceptor.setHandler(protocolHandler);
            acceptor.bind(new InetSocketAddress(port));

            log.info("Listening on port: " + port);

            (new TimeManager("TimeManager thread")).start();
            log.info("TimeManager started.");
        }
        catch (Exception ex)
        {
            if(acceptor != null)
                acceptor.unbind();
            log.error(ex.getMessage());
        }
    }

    public static void shutdownServer()
    {
        try
        {
            acceptor.unbind();

            log.info("Belsoft Server has shutdown");
            System.exit(0);
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
    }

    private static void addLogger(DefaultIoFilterChainBuilder chain) throws Exception
    {
        chain.addLast("logger", new LoggingFilter());
    }
    
    
    private static boolean checkIfCustomLogicClassFileExists()
    {
        boolean exists = (new File("CustomLogic.jar")).exists();
        
        return exists;                    
    }    

    /*Test changes*/
}
