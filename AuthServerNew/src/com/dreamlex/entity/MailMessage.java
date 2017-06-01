package com.dreamlex.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "mail_messages")
public class MailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;

    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "time")
    private Integer time;
    @Column(name = "type")
    private Byte type;
    @Column(name = "subject")
    private String subject;
    @Column(name = "text")
    private String text;
    @Column(name = "readed")
    private Boolean readed;
    @Column(name = "read_time")
    private Integer read_time;
    @Column(name = "deleted")
    private Boolean deleted;

    public MailMessage() {
    }

    public MailMessage(User owner, User sender, User receiver, byte mailBox, String subject, String text, int time, boolean readed, int read_time) {
        this.owner = owner;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.subject = subject;
        this.text = text;
        this.type = mailBox;
        this.readed = readed;
        this.read_time = read_time;
        this.deleted = false;
    }

    public Boolean getDeteled() {
        return deleted;
    }

    public User getOwner() {
        return owner;
    }
    
    public Integer getId() {
        return Id;
    }

    public Integer getRead_time() {
        return read_time;
    }

    public Boolean getReaded() {
        return readed;
    }

    public User getReceiver() {
        return receiver;
    }

    public User getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public Integer getTime() {
        return time;
    }

    public Byte getType() {
        return type;
    }

    public void setDeteled(Boolean deteled) {
        this.deleted = deteled;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public void setRead_time(Integer read_time) {
        this.read_time = read_time;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setType(Byte type) {
        this.type = type;
    }
    
}
