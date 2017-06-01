package com.dreamlex.managers;

import com.dreamlex.DebugLog;
import com.dreamlex.DreamlexProtocolHandler;
import com.dreamlex.entity.UserSession;
import com.dreamlex.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

/**
 * Created by Johny on 27.11.2015.
 */
public class PingManager {
    private static Timer timer;

    //private static final Logger log = LoggerFactory.getLogger(PingManager.class);

    public static void start(final int pingTime) {
        final int pingTimeout = 2 * pingTime;

        timer = new Timer(pingTime, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEndedSessions(pingTimeout);
            }
        });

        timer.start();
    }

    public static void setDelay() {
        timer.setDelay(5 * 1000);
    }

    public static void checkTimer() {
        if(!timer.isRunning()) {
            timer.restart();
        }
    }

    private static void deleteEndedSessions(int pingTimeout) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            session.beginTransaction();
            Date expDate = new Date(System.currentTimeMillis() - pingTimeout);

            Criteria criteria = session.createCriteria(UserSession.class);
            criteria.add(Restrictions.le("time", expDate));

            List<UserSession> userSessionList = criteria.list();

            session.close();

            for (UserSession userSession : userSessionList) {
                DreamlexProtocolHandler.deleteUserSession(userSession, userSession.getUserKey());
            }
        }
        catch (Exception ex)  {
            DebugLog.error(ex.getMessage());
        }
    }
}
