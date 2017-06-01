package com.belsoft.dao.special;

import com.belsoft.dao.general.ElementDAOImpl;
import com.belsoft.entity.User;
import com.belsoft.exceptions.CardFileException;
import com.belsoft.utils.HibernateUtil;
import java.util.Collection;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class UserDAO extends ElementDAOImpl<User>{

	public UserDAO() {
		super(User.class);
	}
        
    /*@SuppressWarnings("unchecked")
    public Collection<User> getUsers(Clan clan) throws CardFileException {
        List<User> userList = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            // creating a query		
            Criteria crit = session.createCriteria(User.class);
            crit.add(Restrictions.eq("clan", clan));

            userList = crit.list();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return userList;
    }*/

    public User getUserByEmail(String email) throws CardFileException {
        Session session = null;
        User user = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Criteria crit = session.createCriteria(User.class);
            crit.add(Restrictions.eq("email", email));
            crit.setMaxResults(1);

            @SuppressWarnings("unchecked")
            List<User> users = crit.list();

            boolean hasResult = !users.isEmpty();

            if (hasResult) {
                user = users.get(0);
            }

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return user;
    }

    public User getUserByName(String username) throws CardFileException {
        Session session = null;
        User user = null;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria crit = session.createCriteria(User.class);
            crit.add(Restrictions.eq("userName", username));
            crit.setMaxResults(1);

            @SuppressWarnings("unchecked")
            List<User> users = crit.list();

            boolean hasResult = !users.isEmpty();

            if (hasResult) {
                user = users.get(0);
            }

        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return user;
    }
    
}
