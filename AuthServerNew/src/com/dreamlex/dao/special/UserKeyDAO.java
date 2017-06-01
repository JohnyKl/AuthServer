package com.dreamlex.dao.special;

import com.dreamlex.dao.general.ElementDAOImpl;
import com.dreamlex.entity.UserKey;
import com.dreamlex.exceptions.CardFileException;
import com.dreamlex.utils.HibernateUtil;

/**
 * Created by Johny on 09.11.2015.
 */
public class UserKeyDAO extends ElementDAOImpl<UserKey> {
    public UserKeyDAO() { super(UserKey.class); }

    public UserKey getUserKeyByValue(String userkey) throws CardFileException {
        return (UserKey) HibernateUtil.findByFieldValue(UserKey.class, HibernateUtil.getColumnName(UserKey.class, "login"), userkey);
    }
}
