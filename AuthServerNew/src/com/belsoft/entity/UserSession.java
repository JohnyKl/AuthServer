package com.belsoft.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer sessionId;   
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;   
    @GenericGenerator(name="hilo" , strategy="increment")
    @GeneratedValue(generator="hilo")
    @Column(name = "temp_key", nullable = false, unique = true)
    private Integer tempKey;
    @Column(name = "time")
    private Long time;
    @Column(name = "ip")
    private String ip;            
    
    //@PrePersist
    //private void generateSecret(){
        
    //}
    
    public UserSession() {
        
    }
    
    public UserSession(User user){
        //userId = user.getId();
        this.user = user;
        //tempKey = user.getKey();
        time = System.currentTimeMillis();
        ip = user.getIp();
        
        generateTempKey();
    } 
    
    public Integer getTempKey() {
        return tempKey;
    }
    
    private void generateTempKey() {
        Random randomGenerator = new Random();
        
        boolean found = false;
        
        while (!found) {
            int value = randomGenerator.nextInt(65000) + 58600;
        
            if(!tempKeys.contains(value)) {
                found = true;
                
                tempKeys.add(value);
                tempKey = (Integer) value;
            }
        }   
    }
    
    @Transient
    public static ArrayList<Integer> tempKeys = new ArrayList<Integer>();
}
