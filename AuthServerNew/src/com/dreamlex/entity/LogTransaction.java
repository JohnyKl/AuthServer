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
@Table(name = "log_transactions")
public class LogTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;

    @Column(name = "transaction_id")
    private Integer transactionId;
    @Column(name = "time")
    private Integer time;
    @Column(name = "login")
    private String login;
    @Column(name = "amount")
    private Float amount;
    @Column(name = "details")
    private String details;
    @Column(name = "success")
    private Boolean success;
    
    public LogTransaction() {
    }

    public LogTransaction(int transactionId, String login, float amount, String details, boolean success) {
        this.transactionId = transactionId;
        this.time = (int)(System.currentTimeMillis()/1000);
        this.login = login;
        this.amount = amount;
        this.details = details;
        this.success = success;
     }

    public Integer getId() {
        return Id;
    }

    public Float getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }

    public String getLogin() {
        return login;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
  
}
