package com.belsoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "config")
public class ConfigProperty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;
    @Column(name = "property")
    private String property;
    @Column(name = "value")
    private String propertyValue;
    
    public ConfigProperty() {
        
    }   
    
    public String getPropertyName () {
        return property;
    }
    
    public String getPropertyValue () {
        return propertyValue;
    }
    
}
