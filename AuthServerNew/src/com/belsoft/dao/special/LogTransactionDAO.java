package com.belsoft.dao.special;

import com.belsoft.dao.general.ElementDAOImpl;
import com.belsoft.entity.LogTransaction;
import com.belsoft.entity.User;

public class LogTransactionDAO extends ElementDAOImpl<LogTransaction>{

	public LogTransactionDAO() {
		super(LogTransaction.class);
	}
        
}
