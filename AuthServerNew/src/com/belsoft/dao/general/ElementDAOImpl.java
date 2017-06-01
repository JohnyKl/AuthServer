package com.belsoft.dao.general;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.belsoft.utils.HibernateUtil;

/**
 * 
 * The class that provides basic operations for interacting with
 * the logic objects. This is a generic class that means that any
 * logic class can be used, but it is mandatory to create a sub-class 
 * for each.  
 * @author ag
 *
 * @param <E> The logic class that represents the data from the base.
 */

public class ElementDAOImpl<E> implements ElementDAO<E> {
	private Class<E> elementClass;

	/**
	 * This class have to be created with element.class parameter
	 * because when finding the data in the database, the class is 
	 * used. This parameter must be defined in all subclasses this way:
	 * <br/>
	 * super(Author.class);
	 * @param elementClass
	 */
	public ElementDAOImpl(Class<E> elementClass){
		this.elementClass = elementClass;
	}
	
	/**
	 * Adds the element into the base.
	 * @param el - the element
	 */	
	@Override
	public E addElement(E el) {
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			Transaction transaction = session.beginTransaction();
			session.save(el);
			transaction.commit();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
                return el;
	}

	/**
	 * Updates the element.
	 * @param el
	 */
	@Override
	public void updateElement(E el) {
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			Transaction transaction = session.beginTransaction();
			session.update(el);
			transaction.commit();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
	
	/**
	 * Picks the element from the base by its id.
	 */
	@SuppressWarnings("unchecked") // cannot check a generic class
	@Override
	public E getElementByID(int elId) {
		Session session = null;
		E el = null;
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// this doesn't work as I need - this returns only a set with IDs
			//el = (E) session.load(elementClass, elId);
			
			el = (E) session.get(elementClass, elId);

		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		
		return el;
	}

	/**
	 * Returns the collection with all the elements from the base.
	 */
	@SuppressWarnings("unchecked") // cannot check a generic class
	@Override
	public List<E> getAllElements() {
		Session session = null;
		List<E> els = new ArrayList<E>();
		
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			els = session.createCriteria(elementClass).list();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		
		return els;
	}

	/**
	 * Removes the element from the base.
	 */
	@Override
	public void deleteElement(E el) {
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			Transaction transaction = session.beginTransaction();
			session.delete(el);
			transaction.commit();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

}


