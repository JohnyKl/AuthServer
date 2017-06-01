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

package com.dreamlex;


import com.dreamlex.config.ServerConfig;
import com.dreamlex.entity.DefaultTablesCreator;
import com.dreamlex.entity.UserSession;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.*;
import java.net.*;

import com.dreamlex.protocol.*;
import com.dreamlex.managers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DreamlexServer
{
    //private static final Logger log = LoggerFactory.getLogger(DreamlexServer.class);

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

    public static DreamlexProtocolHandler protocolHandler;
    
    private static int port = 5549;
    private static IoAcceptor acceptor = null;

//    private static Logger log = null;

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
            DebugLog.error(ex.getMessage());
            DebugLog.error("There was an error reading the command line arguments");
            return;            
        }
                
        try
        {            
            ServerProperties propertyReader = new ServerProperties();
            
            policyFile = propertyReader.LoadCrossDomainPolicy();            
        }
        catch (Exception ex)
        {
            DebugLog.error(ex.getMessage());
            DebugLog.error("There was an error reading the server.xml file. Dreamlex Server will be shutdown");
            return;
        }

        try
        {
            config.readXMLDocument("sdsdsdsd");
            DebugLog.info("Dreamlex Server Version: " + version);
            DebugLog.info("Cross Domain Policy: " + policyFile);
            DebugLog.info("Dreamlex Server is allowed to manage accounts: " + manageAccounts);
            DebugLog.info("Idle time before disconnect: " + idleTimeBeforeDisconnect);
            
            // Custom Class Loader
            if(checkIfCustomLogicClassFileExists())
            {
                runCustomLogic = true;
                DebugLog.info("CustomLogic.jar file was found");
            }

            acceptor = new NioSocketAcceptor();

            acceptor.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new DreamlexCodecFactory(false)));
            acceptor.getFilterChain().addLast("logging", new LoggingFilter());

            DefaultTablesCreator.fillEmptyTablesByDefault();

            ServerConfig serverConfig = new ServerConfig();

            if(args.length > 0 && args[0].equals("-gen_key"))
            {
                RSAEncryptionManager.saveKeyPair(RSAEncryptionManager.generateKeyPair(serverConfig.getRsaKeyLength()));
            }

            PingManager.start((int)serverConfig.getPingTimeout());

            protocolHandler = new DreamlexProtocolHandler(serverConfig.getPingTimeout(), serverConfig.getMinClientVersion());
            acceptor.setHandler(protocolHandler);
            acceptor.bind(new InetSocketAddress(port));

            DebugLog.info("Listening on port: " + port);

            (new TimeManager("TimeManager thread")).start();
            DebugLog.info("TimeManager started.");
        }
        catch (Exception ex)
        {
            if(acceptor != null)
                acceptor.unbind();
            DebugLog.error(ex.getMessage());
        }
    }

    public static void shutdownServer()
    {
        try
        {
            acceptor.unbind();

            DebugLog.info("Dreamlex Server has shutdown");
            System.exit(0);
        }
        catch (Exception ex)
        {
            DebugLog.error(ex.getMessage());
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
}
