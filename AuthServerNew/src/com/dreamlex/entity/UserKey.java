package com.dreamlex.entity;

import javax.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


@Entity
@Table(name = "user_keys")
public class UserKey {
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = false, nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userKeyId")
    private Set<UserSession> userSessions;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, length = 20)
    private Integer id;

    @Column(name = "login", unique = true, nullable = false, length = 20)
    private String login;

    @Column(name = "end_date", unique = false, nullable = false)
    private Timestamp endDate;

    @Column(name = "max_sessions", unique = false, nullable = false)
    private Integer maxSessionsNumber;

    @Column(name = "game_id", unique = false, nullable = false)
    private Integer gameID;

    public UserKey() { }

    public UserKey(User user, Integer maxSessionNumber, Timestamp endDate, Integer gameID) {
        this.login = generateKey();
        this.user    = user;
        this.endDate = endDate;
        this.maxSessionsNumber = maxSessionNumber;
        this.gameID = gameID;
        userSessions = new HashSet<>();
    }

    private String generateKey() {
        GEN_KEY_NUMBER++;

        return "MsckjJXVc33zNV1ri" + String.format("%03d", GEN_KEY_NUMBER) ;
    }

    /*private String generateKey() {
        return RandomStringUtils.randomAlphanumeric(KEY_LENGTH);
    }*/

    public Integer getID() { return id; }
    public String getLogin() { return login; }
    public User getUser() { return user; }
    public Integer getMaxSessionsNumber() { return  maxSessionsNumber; }
    public Timestamp getEndDate() { return endDate; }
    public Integer getGameID() { return gameID; }

    public void addSession(UserSession session) {
        userSessions.add(session);
    }

    public int getCurrentSessionsNumber() {
        return userSessions.size();
    }

    public boolean checkDeviceHashBelongThis(int hash) {
        boolean deviceHashOwn = true;

        //System.out.println(userSessions.size());

        if(userSessions.size() > 0) {
            Iterator<UserSession> iter = userSessions.iterator();

            while (iter.hasNext() && deviceHashOwn) {
                UserSession us = iter.next();

                if (us.getDeviceHash() != hash) {
                    deviceHashOwn = false;
                }
            }
        }

        return deviceHashOwn;
    }

    public UserSession getUserSessionByTempKey(Integer tempSessionKey) {
        Iterator<UserSession> iter = userSessions.iterator();

        while (iter.hasNext()) {
            UserSession us = iter.next();

            if (us.getTempKey().equals(tempSessionKey)) {
                return us;
            }
        }
        return null;
    }

    public void removeUserSession(UserSession session) {
        userSessions.remove(session);
    }

    public void setUser(User user) { this.user = user; }
    public void setMaxSessionsNumber(Integer maxSessionNumber) { this.maxSessionsNumber = maxSessionNumber; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }
    public void setLogin(String login) { this.login = login; }
    public void setGameID(Integer gameID) { this.gameID = gameID; }

    private static final int KEY_LENGTH = 20;

    private static int GEN_KEY_NUMBER = 0;
}
