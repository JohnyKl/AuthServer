package com.dreamlex.operations.process;

import java.util.Collection;

import com.dreamlex.dao.factory.DaoFactory;
import com.dreamlex.entity.ReportMessage;
import com.dreamlex.entity.User;
import com.dreamlex.exceptions.CardFileException;

/**
 * The class that interacts with used and asks for operations with books on the base.
 * Also class that contains methods for adding books from a text file such
 * as interaction with users, parsing the textfile and checking its lines for 
 * correctness (which is defined by a regular expression, loaded from the base) 
 * @author ag
 *
 */
public class ProcessReports {

    static public boolean addReportMessages(User owner, String subject, String text){
        if (owner == null)
            return false;
        int time = (int)(System.currentTimeMillis()/1000);
        ReportMessage reportMessage = new ReportMessage(owner, (byte)1, subject, text, time, false, 0);
        DaoFactory.getInstance().getReportMessageDAO().addElement(reportMessage);
        return true;
    }

    static public Collection<ReportMessage> getReportMessages(User user, int first, int last) throws CardFileException {
        return DaoFactory.getInstance().getReportMessageDAO().getReportMessages(user, first, last);
    }

    static public boolean deleteReportMessage(User user, int reportMessageId) throws CardFileException {
        ReportMessage reportMessage = getReportMessage(user, reportMessageId);
        if (reportMessage.getOwner().getId().intValue() == user.getId().intValue()) {
            reportMessage.setDeteled(true);
            return updateReportMessage(reportMessage);
        } else
            return false;
    }

    static public ReportMessage getReportMessage(User user, int reportMessageId) throws CardFileException {
        return DaoFactory.getInstance().getReportMessageDAO().getReportMessage(user, reportMessageId);
    }
    
    static public boolean updateReportMessage(ReportMessage reportMessage) throws CardFileException {
        DaoFactory.getInstance().getReportMessageDAO().updateElement(reportMessage);
        return true;
    }

    static public int getReportMessageUnreadCount(User user) throws CardFileException {
        return DaoFactory.getInstance().getReportMessageDAO().getReportMessageUnreadCount(user);
    }
    
    static public int getReportMessageCount(User user) throws CardFileException {
        return DaoFactory.getInstance().getReportMessageDAO().getReportMessageCount(user);
    }
    
}
