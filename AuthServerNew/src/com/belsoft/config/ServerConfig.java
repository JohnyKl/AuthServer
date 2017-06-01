package com.belsoft.config;

import com.belsoft.entity.ConfigProperty;
import com.belsoft.entity.User;
import com.belsoft.utils.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ServerConfig {
    public static final long PING_TIMEOUT_DEFAULT = 2 * 60 * 1000;
    public static final long PING_TIME_DEFAULT = 1 * 60 * 1000;
    
    public ServerConfig() {
        readProperties();
    }
    
    private void readProperties() {
        try {
            pingTimeout = Long.parseLong(getValue("ping_timeout"));
            pingTime = Long.parseLong(getValue("ping_time"));
        }
        catch (NumberFormatException e) {
            pingTimeout = PING_TIMEOUT_DEFAULT;
            pingTime = PING_TIME_DEFAULT;            
        }
    }
    
    public long getPingTimeout() {
        return pingTimeout;
    }
    
    public long getPingTime() {
        return pingTime;
    }
    
    public String getValue(String propertyName) {
        Session session = null;
        String value = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Criteria crit = session.createCriteria(ConfigProperty.class);
            crit.add(Restrictions.eq("property", propertyName));
            crit.setMaxResults(1);

            @SuppressWarnings("unchecked")
            List<ConfigProperty> properties = crit.list();

            boolean hasResult = !properties.isEmpty();

            if (hasResult) {
                value = properties.get(0).getPropertyValue();
            }

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return value;
    }     
    
    private long pingTimeout;
    private long pingTime;    
}
