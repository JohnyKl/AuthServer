package com.dreamlex.operations.process;

import com.dreamlex.dao.factory.DaoFactory;
import com.dreamlex.entity.LogTransaction;

/**
 * The class that interacts with used and asks for operations with books on the base.
 * Also class that contains methods for adding books from a text file such
 * as interaction with users, parsing the textfile and checking its lines for 
 * correctness (which is defined by a regular expression, loaded from the base) 
 * @author ag
 *
 */
public class ProcessLog {
    
    /**
     * offers a user to type a book's data
     */
    static public void addLog(int transactionId, String login, float amount, String details, boolean success) {
        LogTransaction logTransaction = null;
        logTransaction = new LogTransaction(transactionId, login, amount, details, success);
        DaoFactory.getInstance().getLogTransactionDAO().addElement(logTransaction);
    }
    
}
