package com.dreamlex.entity;

import com.dreamlex.packet.Packet;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "logs")
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer logId;
    /*@ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")*/
    @Column(name = "user_id")
    private Integer user;
    @Column(name = "user_key")
    private String key;

    /*@ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "temp_key")
    private UserSession sessions;*/
    @Column(name = "temp_key")
    private Integer tempKey;

    @Column(name = "time")
    private Timestamp time;
    @Column(name = "ip")
    private String userIP;
    @Column(name = "ip_info")
    private String userIPInfo;
    @Column(name = "last_login")
    private Timestamp lastLogin;
    @Column(name = "packet_info")
    private String packetInfo;

    public Logs() { }

    public Logs(UserSession userSession, UserKey key, String userIP, String userIPInfo, Packet packet) {
        this.key         = key.getLogin();
        this.user        = key.getUser().getId();
        //this.user        = userSession.getUser().getId();
        this.time        = new Timestamp(System.currentTimeMillis());
        this.userIP      = userIP;
        this.tempKey     = userSession.getTempKey();
        this.lastLogin   = key.getUser().getLastLogin();
        this.packetInfo  = packet.toString();
        this.userIPInfo  = userIPInfo;

        //userSession.addLog(this);
    }

    public String    getKey()           { return key; }
    public String    getUserIP()        { return userIP; }
    public String    getUserIPInfo()    { return userIPInfo; }
    public String    getPacketInfo()    { return packetInfo; }
    public Integer   getUserId()        { return user; }
    public Timestamp getTime()          { return time; }
    public Timestamp getLastLogin()     { return lastLogin; }
    public Integer   getTempKey()       { return tempKey; }

    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    public void setUserId(User user) { this.user = user.getId(); }
    public void setKey(String key) { this.key = key; }
    public void setUserIP(String ip) { this.userIP = ip; }
    public void setTime(Timestamp time) { this.time = time; }

    /*public void setSessions(UserSession sessions) {
        this.tempKey = sessions.getTempKey();
        setUserId(sessions.getUser());
        setLastLogin(sessions.getUser().getLastLogin());
        setUserIP(sessions.getUser().getIp());
    }*/

    public void setUserIPInfo(String ipInfo) { this.userIPInfo = ipInfo; }
    public void setPacketInfo(String packetInfo) { this.packetInfo = packetInfo; }

}
