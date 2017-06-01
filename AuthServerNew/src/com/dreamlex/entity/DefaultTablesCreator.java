package com.dreamlex.entity;

import com.dreamlex.DebugLog;
import com.dreamlex.dao.BoundaryDAO;
import com.dreamlex.exceptions.CardFileException;
import com.dreamlex.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Johny on 30.10.2015.
 */
public class DefaultTablesCreator {
    //private static final Logger log = LoggerFactory.getLogger(DefaultTablesCreator.class);

    public static void fillEmptyTablesByDefault() {

        ArrayList<Object> entities = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();

        try {
            if (checkIfTableIsEmpty(User.class)) {
                users.add(new User("user1@example.com","user1","password1"));
                users.add(new User("user2@example.com","user2","password2"));
                users.add(new User("user3@example.com","user3","password3"));
                users.add(new User("user4@example.com","user4","password4"));
                users.add(new User("user5@example.com","user5","password5"));

                entities.addAll(users);

                DebugLog.debug("Fill table 'users'");
            } else {
                users = (ArrayList<User>) BoundaryDAO.getInstance().findAllUsers();
            }

            if (checkIfTableIsEmpty(UserKey.class)) {
                for (User user : users) {
                    entities.add(new UserKey(user, 2, Timestamp.valueOf("2018-08-01 00:00:00"), 1));
                    entities.add(new UserKey(user, 7, Timestamp.valueOf("2014-08-01 00:00:00"), 1));
                }

                users.clear();

                DebugLog.debug("Fill table 'user_key'");
            }

            if (checkIfTableIsEmpty(ConfigProperty.class)) {
                entities.add(new ConfigProperty("ping_timeout", "60000"));
                entities.add(new ConfigProperty("ping_time", "30000"));
                entities.add(new ConfigProperty("min_client_version", "1"));
                entities.add(new ConfigProperty("rsa_key_size", "1024"));

                DebugLog.debug("Fill table 'config'");
            }

            if (checkIfTableIsEmpty(Offset.class)) {
                entities.add(new Offset(1, 1, null));
                entities.add(new Offset(1, 2, null));
                entities.add(new Offset(2, 1, null));

                DebugLog.debug("Fill table 'offset'");
            }

            saveObjects(entities);
        } catch (CardFileException e) {
            DebugLog.error("line 62: " + e.getMessage());
        }
    }

    private static void saveObjects(ArrayList<Object> entities) {
        if(entities.size() > 0) {
            Session session = HibernateUtil.getSessionFactory().openSession();

            session.beginTransaction();

            for(int i = 0; i < entities.size(); i++) {
                session.save(entities.get(i));
            }

            DebugLog.debug("Saving tables");

            session.getTransaction().commit();

            session.close();
        }
    }

    public static boolean checkIfTableIsEmpty(Class tableClass) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        return (Long) session.createCriteria(tableClass).setProjection(Projections.rowCount()).uniqueResult() == 0;
    }
}
