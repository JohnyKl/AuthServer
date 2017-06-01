package com.dreamlex.entity;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
// import org.apache.mina.core.session.IoSession;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<UserKey> userKeys;

    /*@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<UserSession> userSessions;*/

    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "time")
    private Timestamp time;
    @Column(name = "ip")
    private String ip;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "comments")
    private String comments;
    @Column(name = "ban")
    private boolean ban;    
    @Column(name = "last_login")
    private Timestamp lastLogin;
   
//    @Transient
//    private String raceName;

    public User() {
    }
/*
    public String getAvatar() {
        return avatar;
    }

    public Integer getGender() {
        return gender;
    }
*/
    public User(String email, String username, String password) {
        this.email     = email;
        this.userName  = username;
        this.password  = password;
        this.lastName  = "";
        this.comments  = "";
        this.ip        = "";
        this.firstName = "";
        this.lastLogin = new Timestamp(System.currentTimeMillis());
        this.userKeys  = new HashSet<>();
        //this.userSessions = new HashSet<>();
        this.time       = new Timestamp(System.currentTimeMillis());
        //this.userLogs = new HashSet<>();
    }
   
    public Integer getLastIp() {
        //return lastIp;
        return null;
    }
    
    public String getIp(){
        return ip;
    }            

    public Integer getId() {
        return Id;
    }

    public String getEmail() {
        return email;
    }
    
    /*public Integer getKey() {
        return userKey;
    }*/

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() { return userName; }

    public UserKey getUserKey() {
        return userKeys.iterator().next();
    }

    public UserKey getUserKeyByKeyValue(String key) {
        Iterator<UserKey> iter = userKeys.iterator();

        while (iter.hasNext()) {
            UserKey uk = iter.next();

            if (uk.getLogin().equals(key)) {
                return uk;
            }
        }
        return null;
    }

    /*public boolean isKeyBelongsUser(String key) {
        Iterator<UserKey> iter = userKeys.iterator();

        while (iter.hasNext()) {
            UserKey uk = iter.next();

            if (uk.getUserKey().equals(key)) {
                return true;
            }
        }
        return false;
    }*/

    public void addKey(UserKey key) { userKeys.add(key); }

    /*public void addSession(UserSession session) {
        userSessions.add(session);
    }

    public int getCurrentSessionsNumber() {
        return userSessions.size();
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
    }*/
    
    public void setLastIp(Integer ip) {
       // this.lastIp = lastIp;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }        
}
