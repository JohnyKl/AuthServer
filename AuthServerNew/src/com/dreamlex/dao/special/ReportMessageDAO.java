package com.dreamlex.dao.special;

import com.dreamlex.dao.general.ElementDAOImpl;
import com.dreamlex.entity.ReportMessage;
import com.dreamlex.entity.User;
import com.dreamlex.exceptions.CardFileException;
import com.dreamlex.utils.HibernateUtil;
import java.util.Collection;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ReportMessageDAO extends ElementDAOImpl<ReportMessage>{

	public ReportMessageDAO() {
		super(ReportMessage.class);
	}
       
    @SuppressWarnings("unchecked")
    public Collection<ReportMessage> getReportMessages(User user, int first, int last) throws CardFileException {
        List<ReportMessage> mailMessages = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            // creating a query		
            Criteria crit = session.createCriteria(ReportMessage.class);
            crit.add(Restrictions.eq("owner", user));
            crit.addOrder(Order.desc("time"));
            crit.add(Restrictions.eq("deleted", false));
            crit.setFirstResult(first);
            crit.setMaxResults(last);

            mailMessages = crit.list();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return mailMessages;
    }

    @SuppressWarnings("unchecked")
    public ReportMessage getReportMessage(User user, int messageId) throws CardFileException {
        List<ReportMessage> mailMessages = null;
        ReportMessage mailMessage = null;
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            // creating a query		
            Criteria crit = session.createCriteria(ReportMessage.class);
            crit.add(Restrictions.eq("owner", user));
            crit.add(Restrictions.eq("id", messageId));
            crit.add(Restrictions.eq("deleted", false));
            crit.addOrder(Order.desc("time"));

            mailMessages = crit.list();
            if (mailMessages.size()>0) {
                mailMessage = mailMessages.get(0);
            }
                    
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return mailMessage;
    }

    @SuppressWarnings("unchecked")
    public int getReportMessageCount(User user) throws CardFileException {
        int mailCount = 0;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            // creating a query		
            Criteria crit = session.createCriteria(ReportMessage.class);
//            Criterion sender = Restrictions.eq("owner", user);
            crit.add(Restrictions.eq("deleted", false));
            crit.add(Restrictions.eq("owner", user));
            crit.setProjection(Projections.rowCount());
            mailCount = ((Integer)crit.list().get(0)).intValue();
        
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return mailCount;
    }

    @SuppressWarnings("unchecked")
    public int getReportMessageUnreadCount(User user) throws CardFileException {
        int mailCount = 0;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Criteria crit = session.createCriteria(ReportMessage.class);
            crit.add(Restrictions.eq("deleted", false));
            crit.add(Restrictions.eq("readed", false));
            crit.add(Restrictions.eq("owner", user));
            crit.setProjection(Projections.rowCount());
            mailCount = ((Integer)crit.list().get(0)).intValue();
        
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return mailCount;
    }
        
}
