package com.dreamlex.dao.special;

import com.dreamlex.dao.general.ElementDAOImpl;
import com.dreamlex.entity.User;
import com.dreamlex.entity.UserKey;
import com.dreamlex.exceptions.CardFileException;
import com.dreamlex.utils.HibernateUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class UserDAO extends ElementDAOImpl<User>{

	public UserDAO() {
		super(User.class);
	}

    /*
        ����� ����� � �������� ������������ � ���� ��������� ����������, ������������ � ����� ������� !!!
        ����� ����� ������� ����� ���� �����
    */

    public User getUserByEmail(String email) throws CardFileException {
        return (User)HibernateUtil.findByFieldValue(User.class, HibernateUtil.getColumnName(User.class, "email"), email);
    }

    public User getUserByName(String username) throws CardFileException {
        return (User)HibernateUtil.findByFieldValue(User.class, HibernateUtil.getColumnName(User.class, "userName"), username);
    }

    public Collection<User> getUsers() throws CardFileException {
        return (Collection<User>)(Object)HibernateUtil.findByCriteriaValue(User.class, "", "");
    }


}
