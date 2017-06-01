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
package com.dreamlex;

import com.dreamlex.entity.*;
import com.dreamlex.managers.PingManager;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dreamlex.classloader.*;
import com.dreamlex.packet.*;
import com.dreamlex.protocol.*;
import com.dreamlex.operations.process.ProcessUsers;
import com.dreamlex.utils.HibernateUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;
import java.sql.Timestamp;
import java.util.*;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.Timer;
import org.hibernate.Session;

public class DreamlexProtocolHandler extends IoHandlerAdapter {

    //private static final Logger log = LoggerFactory.getLogger(DreamlexProtocolHandler.class);

    //public Set<User> users = Collections.synchronizedSet(new HashSet());
    //public Set<Logs> logs = Collections.synchronizedSet(new HashSet<>());
    //public Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<IoSession>());
    //public Set<Entry<Integer, Long>> sessionsKeys = Collections.synchronizedSet(new HashSet<>());

    public Set<UserKey> userKeys = Collections.synchronizedSet(new HashSet());
    public Set<Entry<IoSession, Long>> sessions = Collections.synchronizedSet(new HashSet<>());
    private ICustomLogic objCustomLogicClass = null;

    public DreamlexProtocolHandler(long timeout, int minClientVersion) {
        this.pingTimeout = timeout;
        this.minClientVersion = minClientVersion;

        /*pingTimer = new Timer((int) pingTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pingUserSessions();
            }
        });

        pingTimer.start();*/
    }   
    
    @Override
    public void sessionCreated(IoSession session) {
        DebugLog.debug("Session created. Remote IP address:" + session.getRemoteAddress());

        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, DreamlexServer.idleTimeBeforeDisconnect);

        try {
            if (DreamlexServer.runCustomLogic) {
                JarClassLoader jcl = new JarClassLoader("CustomLogic.jar");
                objCustomLogicClass = (ICustomLogic) jcl.loadClass("com.dreamlex.CustomLogic").newInstance();
            }
        } catch (Exception ex) {
            DebugLog.error("lint 82: " + ex.getMessage());
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        try {
            DebugLog.debug("Session Idle procedure.");
            DateFormat df = DateFormat.getTimeInstance(DateFormat.FULL);
            String s = df.format(new Date());
            User user = (User) session.getAttribute(ATTRIBUTE_USER );
            if (user != null) {
                String name = user.getUserName();
                DebugLog.debug("User: " + name);
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
            session.close(true);
        } catch (Exception ex) {
            DebugLog.error("line 109" + ex.getMessage());
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        try {
            cause.printStackTrace();
            session.close(true);
        } catch (Exception ex) {
            DebugLog.error("line 119: " + ex.getMessage());
        }
    }

    private boolean connectionLimitHit(IoSession session) {
        if (getOnlineUserCount() > DreamlexServer.maximumConnections) {
            // sessionWrite(session, "CONNECTION_LIMIT_HIT");
            session.close(true);
            DebugLog.debug("Connection limit hit (Maximum connections allowed is: " + DreamlexServer.maximumConnections + ")");
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

            if ((DreamlexRequest) message == null) {
                session.close(true);
                return;
            }

            incomingPacket = ((DreamlexRequest) message).getPacket();

            if (incomingPacket.getSize() == 0x3C706F6C) {
                session.write(DreamlexServer.policyFile);
                session.close(false);
                return;
            }

            DebugLog.debug("Incoming Message:" + Integer.toHexString(incomingPacket.messageID));

            //long inTime = session.getLastIoTime();
            DebugLog.debug("Remote address:" + session.getRemoteAddress());

            User user = (User) session.getAttribute(ATTRIBUTE_USER );
            
            long i = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            int tempSessionKey = 0;

            DebugLog.debug(
                    "Total memory: " + Runtime.getRuntime().totalMemory() / 1024L + " kb. "
                    + "Free memory: " + Runtime.getRuntime().freeMemory() / 1024L + " kb. "
                    + "Max memory: " + Runtime.getRuntime().maxMemory() / 1024L + " kb. "
                    + "Memory use: " + i / 1024L + " kb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)");

            switch (incomingPacket.messageID) {
                case ActionCommand.LOGIN:
                    loginByUserKey(session, incomingPacket);

                    //loginByEmail(session, ((Login) incomingPacket.packetBody).login, ((Login) incomingPacket.packetBody).password);

                    break;
                case ActionCommand.LOGOUT:
                    logout(((Logout) incomingPacket.packetBody).tempSessionKey, session, incomingPacket);

                    break;
                    
                case ActionCommand.INFOMESSAGE:
                    tempSessionKey = ((InfoMessage) incomingPacket.packetBody).tempSessionKey;

                    PingManager.checkTimer();

                    if(refreshPingTime(tempSessionKey, System.currentTimeMillis())) {
                        sessionWrite(session, new InfoMessage(1));

                        Object[] obj = getUserSessionByTempKey(tempSessionKey);

                        if(obj != null) saveLogs(new Logs((UserSession) obj[0], (UserKey)obj[2], session.getRemoteAddress().toString(), "", incomingPacket.packetBody));
                    }
                    else {
                        sessionWrite(session, new DialogMessage(0, "Bad InfoMessage packet"));
                    }                      
                    break;
                case ActionCommand.PING:
                    tempSessionKey = ((Ping) incomingPacket.packetBody).tempSessionKey;

                    PingManager.checkTimer();

                    if(refreshPingTime(tempSessionKey, System.currentTimeMillis())) {
                        sessionWrite(session, new Ping(Packet.SERVER_MESSAGE_INFO, "Session was continue..."));
                        //sessionWrite(session, incomingPacket.packetBody);

                        Object[] obj = getUserSessionByTempKey(tempSessionKey);

                        if(obj != null) saveLogs(new Logs((UserSession) obj[0], (UserKey)obj[2], session.getRemoteAddress().toString(), "", incomingPacket.packetBody));

                        DebugLog.debug("Ping from session " + Integer.toString(tempSessionKey));
                    }
                    else {
                        DebugLog.debug("Cannot refresh ping time of session " + Integer.toString(tempSessionKey));

                        sessionWrite(session, new Ping(Packet.SERVER_MESSAGE_WARNING, "Current session was disconnected by timeout"));
                    }  

                    session.close(true);  
                    break;

                default:
                    DebugLog.error("line 248: " + "Something is broken...");
                    
                    sessionWrite(session, new DialogMessage(1, "Packet ID:" + Integer.toHexString(incomingPacket.packetBody.messageID) + " is not implemented yet."));
                    DebugLog.error("Packet ID:" + Integer.toHexString(incomingPacket.packetBody.messageID) + " is not implemented yet.");
                    DebugLog.error("Remote address:" + session.getRemoteAddress());
                    DebugLog.error("DFrom user:" + user.getUserName());
                    session.close(true);
                    break;
                /*case ActionCommand.PREPAREAUTOLOGIN:
                 ProcessUsers.editUserKey(((PrepareAutoLogin)incomingPacket.packetBody).loginByEmail,
                 ((PrepareAutoLogin)incomingPacket.packetBody).key);
                 PrepareAutoLogin prepareAutoLogin= new PrepareAutoLogin(1);
                 sessionWrite(session, prepareAutoLogin);
                 return;*/
            }

            /*if (user == null) {
                session.close(false);
                return;
            }*/

            

            DebugLog.debug("Work time: " + (int) (System.currentTimeMillis() - enterTime) + " msec.");

        } catch (IllegalArgumentException illarg) {
            DebugLog.error("line 274: Illegal argument");
            DebugLog.error(illarg.getMessage());
        } catch (Exception ex) {
            DebugLog.error("line 277: Exception");
            DebugLog.error(ex.getMessage());
        }
    }

    private boolean refreshPingTime(Integer sessionKey, long time) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        Criteria criteria = session.createCriteria(UserSession.class);
        criteria.add(Restrictions.eq("tempKey", sessionKey));

        List<UserSession> userSessionList = criteria.list();

        session.close();

        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        for (UserSession userSession : userSessionList) {
            DebugLog.debug("Update session timestamp");

            userSession.setTime(new Timestamp(time));

            session.saveOrUpdate(userSession);

            return true;
        }

        session.getTransaction().commit();

        session.close();

        /*synchronized (sessionsKeys) {
            Iterator<Entry<Integer, Long>> iter = sessionsKeys.iterator();

            while (iter.hasNext() && !isFound) {
                Entry<Integer, Long> entry =  iter.next();

                if (sessionKey.equals(entry.getKey())) {
                    entry.setValue(time);//System.currentTimeMillis());

                    isFound = true;
                }
            }
        }
        */
        return false;
    }

    private void saveLogs(Logs userLog) {
        //logs.add(userLog);

        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        session.save(userLog);
        session.getTransaction().commit();
        session.close();
    }

    private int saveUserSession(IoSession ioSession, UserKey userKey, Integer deviceHash) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        UserSession userSession = new UserSession(userKey);

        userSession.setDeviceHash(deviceHash);

        ioSession.setAttribute(ATTRIBUTE_USER_SESSION, userSession);

        userKey.addSession(userSession);

        session.save(userSession);
        session.getTransaction().commit();

        session.close();
        
        return userSession.getTempKey();
    }

    public static void deleteUserSession(UserSession userSession, UserKey userKey) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        if (userSession != null) {
            DebugLog.debug("Deleting user session " + Integer.toString(userSession.getTempKey()));
        
            session.delete(userSession);
            session.getTransaction().commit();
            
            userKey.removeUserSession(userSession);
        }

        session.close();
    }

    private void logout(int tempSessionKey, IoSession session, Packet incomingPacket) {
        Object[] us = getUserSessionByTempKey(tempSessionKey);

        if (us != null) {
            refreshPingTime(tempSessionKey, System.currentTimeMillis() - pingTimeout * 2);

            saveLogs(new Logs((UserSession) us[0], (UserKey) us[2], session.getRemoteAddress().toString(), "", incomingPacket.packetBody));

            sessionWrite(session, new Logout(1, Packet.SERVER_MESSAGE_INFO, ""));
        }
        else {
            sessionWrite(session, new Logout(1, Packet.SERVER_MESSAGE_ERROR, "Cannot log out! The user wasn`t logged in"));
        }
    }

    private void loginByUserKey(IoSession session, /*String userKey, String password, int clientVersion, Integer deviceHash, int gameID, */Packet incomingPacket) {
        try {
            String login         = ((Login) incomingPacket.packetBody).login;
            String password      = ((Login) incomingPacket.packetBody).password;
            int    clientVersion = ((Login) incomingPacket.packetBody).clientVersion;
            int    deviceHash    = ((Login) incomingPacket.packetBody).deviceHash;
            int    gameID        = ((Login) incomingPacket.packetBody).gameID;
            int    gameVersion   = ((Login) incomingPacket.packetBody).gameVersion;

            DebugLog.info("User with key '" + login + "' try to login");

            if ((login.trim().length() <= 0) || (password.trim().length() <= 0)) {
                sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "The login or password MUST be not empty!", new byte[0]));
                session.close(true);
                return;
            }

            User user = null;
            UserKey userKey = null;

            if (checkIfUserIsLoggedIn(login, session)) {
                user = (User) session.getAttribute(ATTRIBUTE_USER );
                userKey = (UserKey) session.getAttribute(ATTRIBUTE_USER_KEY );

                DebugLog.info("User " + user.getId().toString() + "already connected, user key - " + login);

                if (!checkIsUserCanOpenSession(userKey)) {
                    session.setAttribute(ATTRIBUTE_USER, null);
                    session.setAttribute(ATTRIBUTE_USER_KEY, null);

                    DebugLog.info("User " + user.getId().toString() + " overflowed limit of sessions");

                    sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "Overflow limit of session", new byte[0]));
                    session.close(true);

                    return;
                }
            }

            if (user == null || userKey == null) {
                userKey = ProcessUsers.getUserKeyByValue(login);
                user    = userKey.getUser();

                DebugLog.info("User " + user.getId().toString() + " with key - " + login + " found in DB");
            }

            if (user == null || userKey == null || !checkIsUserKeyIsValid(userKey)) {
                DebugLog.info("User " + user.getId().toString() + " has the incorrect key - " + login);

                sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "The user key is incorrect", new byte[0]));
                session.close(true);
                return;
            }

            DebugLog.info(Integer.toString(deviceHash));
            if(checkIsOtherDeviceUseKey(deviceHash, userKey)) {
                DebugLog.info("User key " + userKey.getLogin() + " using on other computer now. Login denied.");

                sessionWrite(session, new Login(1, null, -1, Packet.SERVER_MESSAGE_ERROR, "Other computer using this key", new byte[0]));
                session.close(true);
                return;
            }

            if(clientVersion < minClientVersion) {
                DebugLog.info("User " + user.getId().toString() + "has old client version - " + clientVersion + ". Login denied.");

                sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "Old client version", new byte[0]));
                session.close(true);
                return;
            }

            if(gameID != userKey.getGameID()) {
                DebugLog.info("User " + user.getId().toString() + "has client version " + gameID + " made for other game. Login denied.");

                sessionWrite(session, new Login(1, null, -1, Packet.SERVER_MESSAGE_ERROR, "Bad client game ID", new byte[0]));
                session.close(true);
                return;
            }

            synchronized (userKeys){
                userKeys.add(userKey);

                user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            }

            byte[] offsetData = getOffsetData(gameID, gameVersion);

            int tempSessionKey = saveUserSession(session, userKey, ((Login) incomingPacket.packetBody).deviceHash);

            UserSession userSession = (UserSession) session.getAttribute(ATTRIBUTE_USER_SESSION);

            /*synchronized(sessionsKeys){
                sessionsKeys.add(new AbstractMap.SimpleEntry<>(tempSessionKey, System.currentTimeMillis()));
            }*/
            saveLogs(new Logs(userSession, userKey, session.getRemoteAddress().toString(), "", incomingPacket.packetBody));

            sessionWrite(session, new Login(1, user, tempSessionKey, Packet.SERVER_MESSAGE_INFO, "Login successful", offsetData));

            DebugLog.info("User " + user.getId().toString() + "successfully login by key - " + login);
        } catch (Exception ex) {
            DebugLog.error(ex.getMessage());

            sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "Cannot login, something is broken", new byte[0]));
        }
    }

    private byte[] getOffsetData(int gameID, int gameVersion) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        Criteria criteria = session.createCriteria(Offset.class);
        criteria.add(Restrictions.eq("gameID", gameID));

        List<Offset> offsetsList = criteria.list();

        session.close();

        for(Offset offset : offsetsList) {
            if(offset.getGameVersion() == gameVersion) {
                return offset.getData();
            }
        }

        return new byte[0];
    }

    private boolean checkIsOtherDeviceUseKey(int deviceHash, UserKey key) {
        return !key.checkDeviceHashBelongThis(deviceHash);
    }

    private boolean checkIsUserKeyIsValid(UserKey key) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        if(key != null && key.getEndDate().compareTo(ts) == 1 ) {
            return true;
        }

        DebugLog.debug("key date is not valid");
        return false;
    }

    private boolean checkIsUserCanOpenSession(UserKey userKey) {
        return userKey.getCurrentSessionsNumber() < userKey.getMaxSessionsNumber();
    }


    private void deleteEndedSessions() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        Date expDate = new Date(System.currentTimeMillis() - pingTimeout);

        Criteria criteria = session.createCriteria(UserSession.class);
        criteria.add(Restrictions.le("time", expDate));

        List<UserSession> userSessionList = criteria.list();

        session.close();

        for (UserSession userSession : userSessionList) {
            deleteUserSession(userSession, userSession.getUserKey());
        }

    }

    private void pingUserSessions() {
        deleteEndedSessions();

    }

    private Object[] getUserSessionByTempKey(Integer tempKey) {
        //login by EMAIL
        /*synchronized (users) {
            Iterator<User> iter = users.iterator();

            while (iter.hasNext()) {
                User user = (User) iter.next();

                UserSession us = user.getUserSessionByTempKey(tempKey);

                if (us != null) {
                    return new Object[]{us, user, user.getUserKey()};
                }
            }
        }*/
        //LOGIN BY KEY
        /*synchronized (userKeys) {
            Iterator<UserKey> iter = userKeys.iterator();

            while (iter.hasNext()) {
                UserKey     userKey = iter.next();
                User        user    = userKey.getUser();
                UserSession us      = userKey.getUserSessionByTempKey(tempKey);

                if (us != null) {
                    return new Object[]{us, user, userKey};
                }
            }
        }*/

        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        Criteria criteria = session.createCriteria(UserSession.class);
        criteria.add(Restrictions.eq("tempKey", tempKey));

        List<UserSession> userSessionList = criteria.list();

        session.close();

        for (UserSession userSession : userSessionList) {
            return new Object[]{userSession, userSession.getUserKey().getUser(), userSession.getUserKey()};
        }

        return null;
    }

    private boolean checkIfUserIsLoggedIn(String userKey, IoSession session) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        s.beginTransaction();

        List<UserSession> userSessionList = s.createCriteria(UserSession.class).list();

        s.close();

        for (UserSession userSession : userSessionList) {
            UserKey uk = userSession.getUserKey();
            if(uk.getLogin().equals(userKey)) {
                session.setAttribute(ATTRIBUTE_USER , uk.getUser());
                session.setAttribute(ATTRIBUTE_USER_KEY, uk);

                return true;
            }
        }
        /*synchronized (userKeys) {
            Iterator<UserKey> iter = userKeys.iterator();

            while (iter.hasNext()) {
                UserKey uk = iter.next();

                if(uk.getUserKey().equals(userKey)) {
                    User user = uk.getUser();

                }
            }
        }
*/
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


    //login by EMAIL
    /*private boolean checkIfUserIsLoggedIn(String email, IoSession session) {
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

        return false;
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
*/
    /*private void broadcast(Packet packet) {
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
    }*/


    /*private void deleteUserSession(IoSession ioSession) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();

        UserSession userSession = (UserSession) ioSession.getAttribute(ATTRIBUTE_USER_SESSION);

        if (userSession != null) {
            session.delete(userSession);
            session.getTransaction().commit();
        }

        session.close();
    }

    private void loginByEmail(IoSession session, String email, String password) {
        try {
            if ((email.trim().length() <= 0) || (password.trim().length() <= 0)) {
                sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "The login or password MUST be not empty!"));
                //sessionWrite(session, new DialogMessage(1, "The login or password MUST be not empty!"));
                session.close(true);
                return;
            }

            User user = null;//ProcessUsers.getUserByEmail(email);

            if (checkIfUserIsLoggedIn(email, session)) {
                DebugLog.debug("user already connected - drop connection");
                // sessionWrite(session, "LOGIN_ERROR_USER_ALREADY_LOGGED_IN");
                //session.close();
                //return;

                user = (User) session.getAttribute(ATTRIBUTE_USER );

                if (!checkIsUserCanOpenSession(user)) {
                    session.setAttribute(ATTRIBUTE_USER , null);
                    sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "Overflow limit of session"));
                    //sessionWrite(session, new DialogMessage(1, "Overflow limit of session"));
                    session.close(true);

                    return;
                }
            }

            if ((user == null)) {
                user = ProcessUsers.getUserByEmail(email);
            }

            if ((user == null) || (!((user.getPassword().equals(password))))) { //|| user.getCookie().equals(password)))) {
                sessionWrite(session, new Login(1,null,-1, Packet.SERVER_MESSAGE_ERROR, "Incorrect login or password"));
                //sessionWrite(session, new DialogMessage(1, "Incorrect login or password"));
                session.close();
                return;
            }

            //String str = session.getRemoteAddress().toString().replaceAll("/", "");
            //user.setLastIp(new Integer(str));
            //ProcessUsers.updateUser(user);
            session.setAttribute(ATTRIBUTE_USER , user);
            session.setAttribute(ATTRIBUTE_USER_KEY , user.getUserKey());

            synchronized (users) {
                users.add(user);

                //sessions.add(new AbstractMap.SimpleEntry<IoSession, Long>(session, System.currentTimeMillis()));
            }

        } catch (Exception ex) {
            DebugLog.error(ex.getMessage());
        }
    }


    private boolean checkIsUserCanOpenSession(User currentUser) {
        return true;
    }

    private void removeSessionKey(int tempSessionKey) {
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
    }

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
            //synchronized (sessionsKeys) {
                long currentTime = System.currentTimeMillis();

                synchronized (sessionsKeys) {
                    try {
                Iterator<Entry<Integer, Long>> iter = sessionsKeys.iterator();

                while (iter.hasNext()) {
                       Entry<Integer, Long> entry = iter.next();

                       if (currentTime - entry.getValue() > pingTimeout) {
                           Object[] objs = getUserSessionByTempKey(entry.getKey());

                           if (objs != null) {
                               deleteUserSession((UserSession) objs[0], (UserKey) objs[2]);
                           }

                           synchronized (userKeys) {
                               userKeys.remove(objs[2]);
                           }

                           //synchronized (sessionsKeys) {
                           sessionsKeys.remove(entry);
                           //}

                       }
                }
                    } catch (Exception ex) {
                        DebugLog.error("detele session error: " + ex.getMessage());
                    }
            }
        }
*/

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

    /*private String getNumberOfUsers() {
        return String.valueOf(sessions.size());
    }*/

    private int getOnlineUserCount() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        return ( (Long) session.createCriteria(UserSession.class).setProjection(Projections.rowCount()).uniqueResult()).intValue();
        //return sessions.size();
    }

    public void sessionWrite(IoSession session, Packet packet) {

        //DebugLog.info("Sending: " + message);
        // Run the custom logic
//        if(DreamlexServer.runCustomLogic)
//            message = objCustomLogicClass.customLogic(message);
        session.write(packet);
    }

    Timer pingTimer;
    public  final long pingTimeout;
    private final int minClientVersion;

    private static final String ATTRIBUTE_USER_SESSION = "userSession";
    private static final String ATTRIBUTE_USER_KEY = "user_key";
    private static final String ATTRIBUTE_USER = "user";
}
