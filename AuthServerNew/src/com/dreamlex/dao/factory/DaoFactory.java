package com.dreamlex.dao.factory;

import com.dreamlex.dao.special.UserDAO;
import com.dreamlex.dao.special.LogTransactionDAO;
import com.dreamlex.dao.special.ReportMessageDAO;
import com.dreamlex.dao.special.UserKeyDAO;

public class DaoFactory {

	// main entities
	private UserDAO userDAO = new UserDAO();
    private UserKeyDAO userKeyDAO = new UserKeyDAO();
	private ReportMessageDAO reportMessageDAO = new ReportMessageDAO();	
	private LogTransactionDAO logTransactionDAO = new LogTransactionDAO();

        private static DaoFactory instance = null;
	
	public static synchronized DaoFactory getInstance() {
		if (instance == null) {
			instance = new DaoFactory();
		}
		
		return instance;
	}   

    public UserDAO getUserDAO() {
        return userDAO;
    }
    public UserKeyDAO getUserKeyDAO() {
        return userKeyDAO;
    }

    public ReportMessageDAO getReportMessageDAO() {
        return reportMessageDAO;
    }

    public LogTransactionDAO getLogTransactionDAO() {
        return logTransactionDAO;
    }
}
