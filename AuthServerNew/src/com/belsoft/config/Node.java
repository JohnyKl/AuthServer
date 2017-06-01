package com.belsoft.config;

import java.util.*;

/**
 * Node representation. For internal use.
 * 
 * @author Andrew Romanenco
 *
 */
class Node {

	protected String name;
	protected String value;
	protected LinkedHashMap<String, String> attributes;
	protected ArrayList<Node> children;
	protected Node parent;
	
	Node() {
		attributes = new LinkedHashMap<String, String>();
		children = new ArrayList<Node>();
	}
	
}
