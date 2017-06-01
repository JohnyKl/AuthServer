package com.dreamlex.dao.special;

import com.dreamlex.dao.general.ElementDAOImpl;
import com.dreamlex.entity.LogTransaction;
import com.dreamlex.entity.User;

public class LogTransactionDAO extends ElementDAOImpl<LogTransaction>{

	public LogTransactionDAO() {
		super(LogTransaction.class);
	}
        
}
