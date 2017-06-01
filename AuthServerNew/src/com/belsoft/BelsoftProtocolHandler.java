/**
 * Copyright (C) 2011 - Alexey Kolenko
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.belsoft;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
//import org.apache.mina.common.TransportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import com.belsoft.classloader.*;
import java.text.DateFormat;
import java.util.Date;
import com.belsoft.packet.*;
import com.belsoft.protocol.*;
import com.belsoft.entity.User;
import com.belsoft.entity.UserSession;
import com.belsoft.operations.process.ProcessUsers;
import com.belsoft.utils.HibernateUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;
import javax.swing.Timer;
import org.hibernate.Session;

public class BelsoftProtocolHandler extends IoHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(BelsoftProtocolHandler.class);

    public Set<User> users = Collections.synchronizedSet(new HashSet<User>());
    
    public Set<Entry<Integer, Long>> sessionsKeys = Collections.synchronizedSet(new HashSet<Entry<Integer, Long>>());
    public Set<Entry<IoSession, Long>> sessions = Collections.synchronizedSet(new HashSet<Entry<IoSession, Long>>());
    //public Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
    private ICustomLogic objCustomLogicClass = null;

    public BelsoftProtocolHandler(long timeout) {
        this.pingTimeout = timeout;
        
        pingTimer = new Timer((int) pingTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pingUserSessions();                
            }
        });

        pingTimer.start();
    }   
    
    @Override
    public void sessionCreated(IoSession session) {
        log.debug("Session created. Remote IP address:" + session.getRemoteAddress());

        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, BelsoftServer.idleTimeBeforeDisconnect);

        try {
            if (BelsoftServer.runCustomLogic) {
                JarClassLoader jcl = new JarClassLoader("CustomLogic.jar");
                objCustomLogicClass = (ICustomLogic) jcl.loadClass("com.belsoft.CustomLogic").newInstance();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        try {
            log.debug("Session Idle procedure.");
            DateFormat df = DateFormat.getTimeInstance(DateFormat.FULL);
            String s = df.format(new Date());
            User user = (User) session.getAttribute(ATTRIBUTE_USER );
            if (user != null) {
                String name = user.getUserName();
                log.debug("User: " + name);
                /*                broadcast(new ChatMessage(1,
                 ChatManager.addNewChatMessage(
                 1,
                 1,
                 "Злая система",
                 0,
                 "",
                 (byte)0,
                 0xffffff,
                 "Нам придется растаться c " + name +"... Общаться не хочет.")));
                 */            }
            session.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        try {
            cause.printStackTrace();
            session.close();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private boolean connectionLimitHit(IoSession session) {
        if (getOnlineUserCount() > BelsoftServer.maximumConnections) {
            // sessionWrite(session, "CONNECTION_LIMIT_HIT");
            session.close();
            log.debug("Connection limit hit (Maximum connections allowed is: " + BelsoftServer.maximumConnections + ")");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        Packet incomingPacket;
        ErrorMessage err;

        try {
            long enterTime = System.currentTimeMillis();

            if (connectionLimitHit(session)) {
                return;
            }

            if ((BelsoftRequest) message == null) {
                session.close();
                return;
            }

            incomingPacket = ((BelsoftRequest) message).getPacket();

            if (incomingPacket.getSize() == 0x3C706F6C) {
                session.write(BelsoftServer.policyFile);
                session.close(false);
                return;
            }

            log.debug("Incoming Message:" + Integer.toHexString(incomingPacket.messageID));

            //long inTime = session.getLastIoTime();
            log.debug("Remote address:" + session.getRemoteAddress());

            User user = (User) session.getAttribute(ATTRIBUTE_USER );
            
            long i = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            int tempSessionKey = 0;

            log.debug(
                    "Total memory: " + Runtime.getRuntime().totalMemory() / 1024L + " kb. "
                    + "Free memory: " + Runtime.getRuntime().freeMemory() / 1024L + " kb. "
                    + "Max memory: " + Runtime.getRuntime().maxMemory() / 1024L + " kb. "
                    + "Memory use: " + i / 1024L + " kb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)");

            switch (incomingPacket.messageID) {
                case ActionCommand.LOGIN:
                    login(session, ((Login) incomingPacket.packetBody).login, ((Login) incomingPacket.packetBody).password);
                    user = (User) session.getAttribute(ATTRIBUTE_USER );
                    if (user != null) {
                        tempSessionKey = saveUserSession(session, user);
                        
                        synchronized(sessionsKeys){
                           sessionsKeys.add(new AbstractMap.SimpleEntry<Integer, Long>(tempSessionKey, System.currentTimeMillis())); 
                        }    
                        
                        sessionWrite(session, new Login(1, user, tempSessionKey));
                    }
                    else {
                        sessionWrite(session, new Login(0, null, -1));
                    }
                    break;
                case ActionCommand.LOGOUT:
                    logout(((Logout) incomingPacket.packetBody).userID, ((Logout) incomingPacket.packetBody).tempSessionKey, session);

                    break;
                    
                case ActionCommand.INFOMESSAGE:
                    tempSessionKey = ((InfoMessage) incomingPacket.packetBody).tempSessionKey;
                    
                    if(refreshPingTime(tempSessionKey)) {
                        sessionWrite(session, new InfoMessage(1));
                    }
                    else {
                        sessionWrite(session, new DialogMessage(0, "Bad InfoMessage packet"));
                    }                      
                    break;
                case ActionCommand.PING:
                    tempSessionKey = ((Ping) incomingPacket.packetBody).tempSessionKey;
                    
                    if(refreshPingTime(tempSessionKey)) {
                        sessionWrite(session, incomingPacket.packetBody);
                        
                        log.debug("Ping from session " + Integer.toString(tempSessionKey));
                    }
                    else {
                        log.error("Cannot refresh ping time of session " + Integer.toString(tempSessionKey));
                        
                        sessionWrite(session, new DialogMessage(0, "Current session was disconnected by timeout"));
                    }  

                    session.close(true);  
                    break;

                default:
                    log.error("Something is broken...");
                    
                    sessionWrite(session, new DialogMessage(1, "Packet ID:" + Integer.toHexString(incomingPacket.packetBody.messageID) + " is not implemented yet."));
                    log.error("Packet ID:" + Integer.toHexString(incomingPacket.packetBody.messageID) + " is not implemented yet.");
                    log.error("Remote address:" + session.getRemoteAddress());
                    log.error("DFrom user:" + user.getUserName());
                    session.close();
                    break;
                /*case ActionCommand.PREPAREAUTOLOGIN:
                 ProcessUsers.editUserKey(((PrepareAutoLogin)incomingPacket.packetBody).login,
                 ((PrepareAutoLogin)incomingPacket.packetBody).key);
                 PrepareAutoLogin prepareAutoLogin= new PrepareAutoLogin(1);
                 sessionWrite(session, prepareAutoLogin);
                 return;*/
            }

            /*if (user == null) {
                session.close(false);
                return;
            }*/

            

            log.debug("Work time: " + (int) (System.currentTimeMillis() - enterTime) + " msec.");

        } catch (IllegalArgumentException illarg) {
            log.error("Illegal argument");
            log.error(illarg.getMessage());
        } catch (Exception ex) {
            log.error("Exception");
            log.error(ex.getMessage());
        }
    }
    
    private boolean refreshPingTime(Integer sessionKey) {
        boolean isFound = false;
        
        synchronized (sessionsKeys) { 
            Iterator<Entry<Integer, Long>> iter = sessionsKeys.iterator();

            while (iter.hasNext() && !isFound) {
                Entry<Integer, Long> entry = (Entry<Integer, Long>) iter.next();

                if (sessionKey.equals(entry.getKey())) {
                    entry.setValue(System.currentTimeMillis());

                    isFound = true;
                }
            }
        }
        
        return isFound;
    }

    private int saveUserSession(IoSession ioSession, User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        UserSession userSession = new UserSession(user);
        ioSession.setAttribute("userSession", userSession);

        user.addSession(userSession);

        session.save(userSession);
        session.getTransaction().commit();

        session.close();
        
        return (int) userSession.getTempKey();
    }

    private void deleteUserSession(UserSession userSession, User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        if (userSession != null) {
            log.debug("Deleting user session " + Integer.toString(userSession.getTempKey()));
        
            session.delete(userSession);
            session.getTransaction().commit();
            
            user.removeUserSession(userSession);            
        }

        session.close();
    }
    
    /*private void deleteUserSession(IoSession ioSession) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        UserSession userSession = (UserSession) ioSession.getAttribute("userSession");

        if (userSession != null) {
            session.delete(userSession);
            session.getTransaction().commit();
        }

        session.close();
    }*/

    private void logout(int userId, int tempSessionKey, IoSession session) {
        Object[] us = getUserSessionByTempKey(tempSessionKey);
                
        if(us != null) {
            User user = (User) us[1];
            
            deleteUserSession((UserSession) us[0], user);
            
            users.remove(user);
                        
            sessionWrite(session, new Logout(1));
        } 
        else {
            sessionWrite(session, new DialogMessage(1, "Cannot log out! The user wasn`t logged in"));
        }
    }
    
    private void login(IoSession session, String email, String password) {
        try {
            if ((email.trim().length() <= 0) || (password.trim().length() <= 0)) {
                sessionWrite(session, new DialogMessage(1, "The login or password MUST be not empty!"));
                session.close();
                return;
            }

            User user = null;//ProcessUsers.getUserByEmail(email);

            if (checkIfUserIsLoggedIn(email, session)) {
                log.debug("user already connected - drop connection");
                // sessionWrite(session, "LOGIN_ERROR_USER_ALREADY_LOGGED_IN");
                //session.close();
                //return;

                user = (User) session.getAttribute(ATTRIBUTE_USER );

                if (!checkIsUserCanOpenSession(user)) {
                    session.setAttribute(ATTRIBUTE_USER , null);
                    sessionWrite(session, new DialogMessage(1, "Overflow limit of session"));
                    session.close(true);

                    return;
                }
            }

            if ((user == null)) {
                user = ProcessUsers.getUserByEmail(email);
            }

            if ((user == null) || (!((user.getPassword().equals(password))))) { //|| user.getCookie().equals(password)))) {
                sessionWrite(session, new DialogMessage(1, "Incorrect login or password"));
                session.close();
                return;
            }

            //String str = session.getRemoteAddress().toString().replaceAll("/", "");
            //user.setLastIp(new Integer(str));  
            //ProcessUsers.updateUser(user);
            session.setAttribute(ATTRIBUTE_USER , user);

            synchronized (users) {
                users.add(user);
                
                //sessions.add(new AbstractMap.SimpleEntry<IoSession, Long>(session, System.currentTimeMillis()));
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private boolean checkIsUserCanOpenSession(User currentUser) {
        return currentUser.getCurrentSessionsNumber() < currentUser.getMaxSession();
    }    

    /*private void removeSessionKey(int tempSessionKey) {
        synchronized (sessionsKeys) {            
            Iterator<Entry<Integer, Long>> iter = sessionsKeys.iterator();            
            
            while (iter.hasNext()) {
                Entry<Integer, Long> entry = (Entry<Integer, Long>) iter.next();
                
                if(((Integer)tempSessionKey).equals(entry.getKey())){
                    sessionsKeys.remove(entry);
                    
                    return;
                }
            }
        }
    }*/
    
    private boolean checkIfUserSessionValid(Integer sessionKey) {
        boolean valid = false;
        
        synchronized (sessionsKeys) {            
            Iterator<Entry<Integer, Long>> iter = sessionsKeys.iterator();            
            
            while (iter.hasNext() && !valid) {
                Entry<Integer, Long> entry = (Entry<Integer, Long>) iter.next();

                valid = entry.getKey().equals(sessionKey);                
            }
        }
        
        return valid;
    }
    
    private void pingUserSessions() {
        if (!sessionsKeys.isEmpty()) {
            long currentTime = System.currentTimeMillis();

            //synchronized (sessions) {
            Iterator<Entry<Integer, Long>> iter = sessionsKeys.iterator();

            while (iter.hasNext()) {
                Entry<Integer, Long> entry = (Entry<Integer, Long>) iter.next();

                synchronized (entry) {
                    if (currentTime - entry.getValue() > pingTimeout) {
                        Object[] us = getUserSessionByTempKey(entry.getKey());
                        
                        if(us != null) {
                            deleteUserSession((UserSession) us[0], (User) us[1]);
                        }
                        
                        sessionsKeys.remove(entry);
                    }
                }
            }
        }
    }
    
    private Object[] getUserSessionByTempKey(Integer tempKey) {
        //Iterator<Entry<Integer, Long>> iter = sessionsKeys.iterator();
        synchronized (users) {
            Iterator<User> iter = users.iterator();

            while (iter.hasNext()) {
                User user = (User) iter.next();

                UserSession us = user.getUserSessionByTempKey(tempKey);

                if (us != null) {
                    return new Object[]{us, user};
                }
            }
        }
        return null;
    }
    
    private boolean checkIfUserIsLoggedIn(String email, IoSession session) {
        synchronized (users) {
            Iterator<User> iter = users.iterator();

            while (iter.hasNext()) {
                User user = (User) iter.next();
                
                if (user.getEmail().equalsIgnoreCase(email)) {
                    session.setAttribute(ATTRIBUTE_USER , user);

                    return true;
                }
            }
        }
        
        /*synchronized (sessions) {
           Iterator<Entry<IoSession, Long>> iter = sessions.iterator();

            while (iter.hasNext()) {
                Entry<IoSession, Long> entry = (Entry<IoSession, Long>) iter.next();

                IoSession s = entry.getKey();

                if (s.isConnected()) {
                    User user = (User) s.getAttribute(ATTRIBUTE_USER );
                    if (user.getEmail().equalsIgnoreCase(email)) {
                        session.setAttribute(ATTRIBUTE_USER , user);

                        return true;
                    }
                }
            }
        }*/
        
        return false;
    }

    private void insertSessionProperty(IoSession session, String propertyName, String propertyValue) {
        try {
            session.setAttribute("CUSTOM_" + propertyName, propertyValue);
        } catch (Exception ex) {
        }
    }

    private void readSessionProperty(IoSession session, String propertyName) {
        try {
            String s = (String) session.getAttribute("CUSTOM_" + propertyName);
            // sessionWrite(session, "READ_SESSION_PROPERTY_RESULT~" + propertyName + "~" + s);
        } catch (Exception ex) {
        }
    }

    private void updateSessionProperty(IoSession session, String propertyName, String propertyValue) {
        try {
            session.setAttribute("CUSTOM_" + propertyName, propertyValue);
        } catch (Exception ex) {
        }
    }

    private void getUsersIP(IoSession session, String userName) {
        try {
            StringBuilder sb = new StringBuilder();
            char c;
            String str;

            synchronized (sessions) {
                Iterator<Entry<IoSession, Long>> iter = sessions.iterator();

                while (iter.hasNext()) {
                    Entry<IoSession, Long> entry = (Entry<IoSession, Long>) iter.next();

                    IoSession s = entry.getKey();

                    User user = (User) s.getAttribute(ATTRIBUTE_USER );

                    if ((s.isConnected()) && (user.getUserName().equalsIgnoreCase(userName))) {
                        str = user.getLastIp().toString();
                        str = str.toString().replaceAll("/", "");

                        for (int i = 0; i < str.length(); i++) {
                            c = str.charAt(i);
                            if (c == ':') {
                                break;
                            } else {
                                sb.append(c);
                            }
                        }
                        // sessionWrite(session, "INCOMING_USERS_IP~" + userName + "~" + sb.toString());
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    private void broadcast(Packet packet) {
        synchronized (sessions) {
            Iterator<Entry<IoSession, Long>> iter = sessions.iterator();

            while (iter.hasNext()) {
                Entry<IoSession, Long> entry = (Entry<IoSession, Long>) iter.next();

                IoSession s = entry.getKey();

                if (s.isConnected()) {
                    sessionWrite(s, packet);
                }
            }
        }
    }

    private void writePacketToUser(User user, Packet packet) {
        synchronized (sessions) {
            Iterator<Entry<IoSession, Long>> iter = sessions.iterator();

            while (iter.hasNext()) {
                Entry<IoSession, Long> entry = (Entry<IoSession, Long>) iter.next();

                IoSession s = entry.getKey();

                if (s.isConnected()) {
                    User targetUser = (User) s.getAttribute(ATTRIBUTE_USER );
                    if (user.getId().intValue() == targetUser.getId().intValue()) {
                        sessionWrite(s, packet);
                    }
                }
            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        /*try {
            User user = (User) session.getAttribute(ATTRIBUTE_USER );

            if (user != null) {
//                broadcastRoomMessage(user, "BROADCAST_USER_DISCONNECTED", room);

//                if (!(room.equalsIgnoreCase("lobby")))
//                {
//                    broadcastUserList("lobby");
//                }
//
//                broadcastUserList(room);
//                sendRoomList();
            }

//            synchronized (users)
//            {
//                users.remove(user);
//            }
            synchronized (sessions) {
                deleteUserSession(session);

                sessions.remove(session);
            }
        } catch (Exception ex) {
        }*/
    }

    private String getNumberOfUsers() {
        return String.valueOf(sessions.size());
    }

    private int getOnlineUserCount() {
        return sessions.size();
    }

    public void sessionWrite(IoSession session, Packet packet) {

        //log.info("Sending: " + message);
        // Run the custom logic
//        if(BelsoftServer.runCustomLogic)
//            message = objCustomLogicClass.customLogic(message);
        session.write(packet);
    }

    Timer pingTimer;
    public final long pingTimeout;  
    
    private static final String ATTRIBUTE_USER = "user";
}
