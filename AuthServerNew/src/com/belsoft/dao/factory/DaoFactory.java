package com.belsoft.dao.factory;

import com.belsoft.dao.special.UserDAO;
import com.belsoft.dao.special.LogTransactionDAO;
import com.belsoft.dao.special.ReportMessageDAO;

public class DaoFactory {

	// main entities
	private UserDAO userDAO = new UserDAO();
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


    public ReportMessageDAO getReportMessageDAO() {
        return reportMessageDAO;
    }

    public LogTransactionDAO getLogTransactionDAO() {
        return logTransactionDAO;
    }
}
