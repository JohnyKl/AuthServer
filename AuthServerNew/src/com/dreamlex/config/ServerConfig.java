package com.dreamlex.config;

import com.dreamlex.entity.ConfigProperty;
import com.dreamlex.entity.User;
import com.dreamlex.managers.RSAEncryptionManager;
import com.dreamlex.utils.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ServerConfig {
    public static final long PING_TIMEOUT_DEFAULT = 2 * 60 * 1000;
    public static final long PING_TIME_DEFAULT = 1 * 60 * 1000;
    public static final int CURRENT_CLIENT_VERSION = 1;
    
    public ServerConfig() {
        readProperties();
    }
    
    private void readProperties() {
        try {
            pingTimeout = Long.parseLong(getValue("ping_timeout"));
            pingTime = Long.parseLong(getValue("ping_time"));
            minClientVersion = Integer.parseInt(getValue("min_client_version"));
            rsaKeyLength = Integer.parseInt(getValue("rsa_key_size"));
        }
        catch (NumberFormatException e) {
            pingTimeout = PING_TIMEOUT_DEFAULT;
            pingTime = PING_TIME_DEFAULT;
            minClientVersion = CURRENT_CLIENT_VERSION;
            rsaKeyLength = RSAEncryptionManager.KEY_SIZE_DEFAULT;
        }
    }
    
    public long getPingTimeout() {
        return pingTimeout;
    }

    public int getRsaKeyLength() { return rsaKeyLength; }
    
    public long getPingTime() {
        return pingTime;
    }

    public int getMinClientVersion() {return minClientVersion; }
    
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
    private int minClientVersion;
    private int rsaKeyLength;
}
