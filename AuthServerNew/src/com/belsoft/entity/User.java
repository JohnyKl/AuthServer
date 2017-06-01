package com.belsoft.entity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
// import org.apache.mina.core.session.IoSession;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;

/*    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "clan_id")
    private Clan clan;
*/
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<UserSession> userSessions;

    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "time")
    private Long time;
    @Column(name = "user_key")
    private Integer userKey;
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
    private Integer lastLogin;
    @Column(name = "max_session")
    private Integer maxSession;
   
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
    public User(String email, String username, String password, int gender) {
        this.email = email;
        this.userName = username;
        this.password = password;         
        this.lastLogin = (int)(System.currentTimeMillis()/1000); 
        
        userSessions = new HashSet<UserSession>();
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
    
    public Integer getKey() {
        return userKey;
    }

    public Integer getMaxSession() {
        return maxSession;
    }
    
    public Integer getLastLogin() {
        return lastLogin;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
    
    public void addSession(UserSession session) {
        userSessions.add(session);
    }

    public int getCurrentSessionsNumber() {
        return userSessions.size();
    }            
    
    public UserSession getUserSessionByTempKey(Integer tempSessionKey) {
        Iterator<UserSession> iter = userSessions.iterator();

        while (iter.hasNext()) {
            UserSession us = (UserSession) iter.next();
            
            if (us.getTempKey().equals(tempSessionKey)) {
                return us;
            }
        }
        return null;
    }
    
    public void removeUserSession(UserSession session) {
        userSessions.remove(session);
    }
    
    public void setLastIp(Integer ip) {
       // this.lastIp = lastIp;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastLogin(Integer lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }        
}
