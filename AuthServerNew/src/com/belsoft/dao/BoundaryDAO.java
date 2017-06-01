package com.belsoft.dao;

import java.util.Collection;

import com.belsoft.dao.factory.DaoFactory;
import com.belsoft.entity.*;
import com.belsoft.exceptions.CardFileException;

/**
 * The boundary class for interacting with the data in the database.
 * Uses DaoFactory which generates another DAO objects for interacting with
 * logic objects that are stored in the database. This is a singleton, you cannot 
 * create an instance of it, but you can obtain the object by calling 
 * "getInstance()". 
 * @author ag
 *
 */
public class BoundaryDAO {

    

    /**
     * Returns all the users from the library.
     * @return A collection with books.
     * @throws CardFileException
     */
    public Collection<User> findAllUsers() throws CardFileException {
        return DaoFactory.getInstance().getUserDAO().getAllElements();
    }
    
    /**
     * A default constructor. Protected. Can be used only internally.
     */
    protected BoundaryDAO() {
    }

    static BoundaryDAO instance = null;

    /**
     * Returns the instance of the class.
     * @return
     */
    static public synchronized BoundaryDAO getInstance() {
        if (instance == null) {
            instance = new BoundaryDAO();
        }

        return instance;
    }
}