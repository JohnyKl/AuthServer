package com.dreamlex.entity;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import javax.persistence.*;

@Entity
@Table(name = "sessions")
public class UserSession {
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_key_id")
    private UserKey userKeyId;

    @Id
    @GenericGenerator(name="hilo" , strategy="increment")
    @GeneratedValue(generator="hilo")
    @Column(name = "temp_key", nullable = false, unique = true)
    private Integer tempKey;

    @Column(name = "device_hash", unique = false, nullable = true)
    private Integer deviceHash;

    @Column(name = "time", nullable = false)
    private Timestamp time;
    @Column(name = "ip")
    private String ip;

    public UserSession() {
        
    }

    public UserSession(UserKey userKey){
        this.userKeyId = userKey;
        this.time = new Timestamp(System.currentTimeMillis());
        //this.logs = new HashSet<>();
        this.ip   = userKey.getUser().getIp();

        generateTempKey();
    }

    public Integer getTempKey() {
        return tempKey;
    }
    
    private void generateTempKey() {
        Random randomGenerator = new Random();
        
        boolean found = false;
        
        while (!found) {
            int value = randomGenerator.nextInt(KEY_MAX_VALUE) + 1;

            if (!tempKeys.contains(value)) {
                found = true;

                tempKeys.add(value);
                tempKey = value;
            }
        }
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
    public void setDeviceHash(Integer hash) {
        this.deviceHash = hash;
    }

    public Integer getDeviceHash() { return this.deviceHash; }

    public UserKey getUserKey() { return userKeyId; }

    @Transient
    private static final int KEY_MAX_VALUE = 65000;

    @Transient
    private static ArrayList<Integer> tempKeys = new ArrayList<>();
}
