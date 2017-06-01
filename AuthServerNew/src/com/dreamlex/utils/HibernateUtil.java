package com.dreamlex.utils;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dreamlex.entity.User;
import org.hibernate.*;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
//import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    private static Configuration configuration;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    static {
	Logger logger = Logger.getLogger("org.hibernate");
        logger.setLevel(Level.WARNING);
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            configuration = new Configuration();
            configuration.configure();
            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);            
//            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            log.error("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() throws HibernateException {
        Session sess = null;       
        try {         
            sess = sessionFactory.getCurrentSession();  
        } catch (org.hibernate.HibernateException he) {  
            sess = sessionFactory.openSession();     
        }             
        return sess;
    }

    public static Object findByFieldValue(Class entityClass, String fieldName, String fieldValue)
    {
        Session session = null;
        Object returnObject = null;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(entityClass);

            criteria.add(Restrictions.eq(fieldName, fieldValue));
            criteria.setMaxResults(1);

            @SuppressWarnings("unchecked")
            List<Object> queryResult = criteria.list();

            boolean hasResult = !queryResult.isEmpty();

            if (hasResult) {
                returnObject = queryResult.get(0);
            }

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
       return returnObject;
    }

    public static Collection<Object> findByCriteriaValue(Class entityClass, String fieldName, String fieldValue)
    {
        Session session = null;
        List<Object> returnObject = null;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(entityClass);

            if (!fieldName.isEmpty() && !fieldValue.isEmpty())
                criteria.add(Restrictions.eq(fieldName, fieldValue));

            returnObject = criteria.list();

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return returnObject;
    }

    public static  String getColumnName(Class entityClass, String name) {

        String column = ((Column)
                configuration.
                getClassMapping(entityClass.getName()).
                getProperty(name).
                getColumnIterator().next()).
                getName();

        return column;
    }
}
