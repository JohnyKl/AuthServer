package com.belsoft.dao.general;

import java.util.Collection;

/**
 * The interface that provides basic operations for interacting with
 * the logic objects. This is a generic interface that means that any
 * logic class can be used, but it is mandatory to create a sub-class 
 * for each.  
 * @author ag
 *
 * @param <E> The logic class that represents the data from the base.
 */
public interface ElementDAO<E> {
	/**
	 * Adds the element into the base.
	 * @param el - the element
	 */
	public E addElement(E el); 
	
	/**
	 * Updates the element.
	 * @param el
	 */
	public void updateElement(E el); 
	
	/**
	 * Picks the element from the base by its id.
	 */
	public E getElementByID(int elId); 
	
	/**
	 * Returns the collection with all the elements from the base.
	 */
	public Collection<E> getAllElements(); 
	
	/**
	 * Removes the element from the base.
	 */
	public void deleteElement(E el);	
}
