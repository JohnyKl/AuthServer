package com.belsoft.config;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
import java.io.*;


/**
 * Main class for loading and parsing xml based configurations.
 * 
 * @author Andrew Romanenco
 *
 */
public class ConfigLoader extends DefaultHandler {

	private Node thisNode;
	private ArrayList<Node> queue;
	private Node top;
	
	private String tagContent;
	private String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
	
	/**
	 * create new object
	 */
	public ConfigLoader() {
		queue = new ArrayList<Node>();
	}
	
	/**
	 * parser - parser class name if other then xerces
	 * 
	 * @param parser
	 */
	public ConfigLoader(String parser) {
		this();
		DEFAULT_PARSER_NAME = parser;
	}
	
	/**
	 * Load configuration from given filename
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	public void LoadFromFile(String fileName) throws RuntimeException {
		try {
			InputSource in = new InputSource(
					new FileReader(new File(fileName))
					);
			doParse(in);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * Load configuration from String (xml)
	 * 
	 * @param xml
	 * @throws Exception
	 */
	public void LoadFromString(String xml) throws RuntimeException {
		try {
			InputSource in = new InputSource(
					new StringReader(xml)
					);
			doParse(in);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private void doParse(InputSource in) throws Exception {
		XMLReader saxParser = null;
		if (DEFAULT_PARSER_NAME.equals("")) {
			saxParser = XMLReaderFactory.createXMLReader();
		} else {
			saxParser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
		}
		saxParser.setContentHandler(this);
		saxParser.parse(in);
	}
	
	
	public void startElement(String namespaceURI,
            String sName, // simple name (localName)
            String qName, // qualified name
            Attributes attrs)
				throws SAXException {

		tagContent = "";
		Node node = new Node();
		node.parent = thisNode;
		thisNode = node;
		node.name = qName;
		queue.add(node);
		
		if (top == null) top = node;
		
		LinkedHashMap<String,String> hm = new LinkedHashMap<String,String>();
		for (int i = 0; i < attrs.getLength(); i++ ) {
			hm.put(attrs.getLocalName(i), attrs.getValue(i));
		}
		if (!hm.isEmpty()) {
			node.attributes = hm;
		}
	}

	public void endElement(String namespaceURI,
          String sName, // simple name
          String qName  // qualified name
         ) throws SAXException {
		queue.remove(thisNode);
		thisNode.value = tagContent;
		tagContent = "";
		if (thisNode.parent != null) {
			if (thisNode.parent.children == null) {
				thisNode.parent.children = new ArrayList<Node>();
			} 
			thisNode.parent.children.add(thisNode);
		}
		try {
			thisNode = queue.get(queue.size()-1);
		} catch (Exception e) {
			thisNode = null;
		}
	}
	public void characters(char buf[], int offset, int Len)
    			throws SAXException {
        tagContent += new String(buf, offset, Len);
    }
	
	//----- content readers -------
	

	/**
	 * Get content of tag
	 * 
	 * arg is target
	 * ("root.a.b") - use shortest path which is equals to
	 * ("root","0","a","0","b","0")
	 * 
	 * @param arg
	 * @return
	 */
	public String getTagValue(String... arg) {
		if ((top == null)||(arg.length == 0)) return null;
		
		if (arg.length == 1) {
			String[] tmp = new String[2];
			tmp[0] = arg[0];
			tmp[1] = "0";
			arg = tmp;
		} else if (arg.length % 2 == 1) {
			String tmp[] = new String[arg.length+1];
			for (int i = 0; i<arg.length; i++) {
				tmp[i] = arg[i];
			}
			tmp[arg.length] = "0";
			arg = tmp;
		}
		
		ArrayList<Node> children = new ArrayList<Node>();
		children.add(top);
		String value = null;
		
		try {
			Node node = null;		
			for (int i = 0; i<arg.length; i+=2) {
				children = getChildren(children, arg[i]);
				String p[] = arg[i].split("\\.");
				if (children == null) return null;
				int c = Integer.parseInt(arg[i+1]);
				node = null;
				for (Node n: children) {
					if ((c==0)&&(n.name.equals(p[p.length-1]))) {
						node = n;
						break;
					} else if (n.name.equals(p[p.length-1])) {
						c--;
					}
				}
				children = node.children;
			}
			value = node.value;
		} catch (Exception e) {
			//-- do nothing
		}
		
		return value;
	}
	
	private ArrayList<Node> getChildren(ArrayList<Node> ch, String name) {
		String[] p = name.split("\\.");
		EX:
		for (int i = 0; i<p.length - 1; i++){
			for (Node node: ch) {
				if (ch == null) return null;
				if (node.name.equals(p[i])) {
					ch = node.children;
					continue EX;
				}
			}
		}
		return ch;
	}
	
	/*
	 * to do
	 * get child name by number
	 * get child value by number
	 * get children names
	 * 
	 * getAttrCount
	 * getAttribOf
	 * get attributes names
	 */
	//---------------
	
	/**
	 * Get count of sub tags
	 * arg is target
	 * ("root.a.b") - use shortest path which is equals to
	 * ("root","0","a","0","b","0")
	 * 
	 * @param arg
	 * @return
	 */
	public int getTagChildrenCount(String... arg) {
		if ((top == null)||(arg.length == 0)) return 0;
		
		if (arg.length == 1) {
			String[] tmp = new String[2];
			tmp[0] = arg[0];
			tmp[1] = "0";
			arg = tmp;
		} else if (arg.length % 2 == 1) {
			String tmp[] = new String[arg.length+1];
			for (int i = 0; i<arg.length; i++) {
				tmp[i] = arg[i];
			}
			tmp[arg.length] = "0";
			arg = tmp;
		}
		
		ArrayList<Node> children = new ArrayList<Node>();
		children.add(top);
		int value = 0;
		
		try {
			Node node = null;		
			for (int i = 0; i<arg.length; i+=2) {
				children = getChildren(children, arg[i]);
				String p[] = arg[i].split("\\.");
				if (children == null) return 0;
				int c = Integer.parseInt(arg[i+1]);
				node = null;
				for (Node n: children) {
					if ((c==0)&&(n.name.equals(p[p.length-1]))) {
						node = n;
						break;
					} else if (n.name.equals(p[p.length-1])) {
						c--;
					}
				}
				children = node.children;
			}
			value = children.size();
		} catch (Exception e) {
			//-- do nothing
		}
		
		return value;
	}
	
	/**
	 * Get children count with node name
	 * arg is target
	 * ("root.a.b") - use shortest path which is equals to
	 * ("root","0","a","0","b","0")
	 * 
	 * @param name
	 * @param arg
	 * @return
	 */
	public int getTagChildrenCountWithName(String name, String... arg) {
		if ((top == null)||(arg.length == 0)) return 0;
		
		if (arg.length == 1) {
			String[] tmp = new String[2];
			tmp[0] = arg[0];
			tmp[1] = "0";
			arg = tmp;
		} else if (arg.length % 2 == 1) {
			String tmp[] = new String[arg.length+1];
			for (int i = 0; i<arg.length; i++) {
				tmp[i] = arg[i];
			}
			tmp[arg.length] = "0";
			arg = tmp;
		}
		
		ArrayList<Node> children = new ArrayList<Node>();
		children.add(top);
		int value = 0;
		
		try {
			Node node = null;		
			for (int i = 0; i<arg.length; i+=2) {
				children = getChildren(children, arg[i]);
				String p[] = arg[i].split("\\.");
				if (children == null) return 0;
				int c = Integer.parseInt(arg[i+1]);
				node = null;
				for (Node n: children) {
					if ((c==0)&&(n.name.equals(p[p.length-1]))) {
						node = n;
						break;
					} else if (n.name.equals(p[p.length-1])) {
						c--;
					}
				}
				children = node.children;
			}
			for (Node n: children) {
				if (n.name.equals(name)) value++;
			}
		} catch (Exception e) {
			//-- do nothing
		}
		
		return value;
	}

	public String getTagAttributeValue(String attrName, String attrDefault, String... arg) {
		if ((top == null)||(arg.length == 0)) return null;
		
		if (arg.length == 1) {
			String[] tmp = new String[2];
			tmp[0] = arg[0];
			tmp[1] = "0";
			arg = tmp;
		} else if (arg.length % 2 == 1) {
			String tmp[] = new String[arg.length+1];
			for (int i = 0; i<arg.length; i++) {
				tmp[i] = arg[i];
			}
			tmp[arg.length] = "0";
			arg = tmp;
		}
		
		ArrayList<Node> children = new ArrayList<Node>();
		children.add(top);
		String value = null;
		
		try {
			Node node = null;		
			for (int i = 0; i<arg.length; i+=2) {
				children = getChildren(children, arg[i]);
				String p[] = arg[i].split("\\.");
				if (children == null) return null;
				int c = Integer.parseInt(arg[i+1]);
				node = null;
				for (Node n: children) {
					if ((c==0)&&(n.name.equals(p[p.length-1]))) {
						node = n;
						break;
					} else if (n.name.equals(p[p.length-1])) {
						c--;
					}
				}
				children = node.children;
			}
			value = node.attributes.get(attrName);
		} catch (Exception e) {
			//-- do nothing
		}
		if (value == null) value = attrDefault;
		return value;
	}
	
	public ArrayList<String> getTagAttributeNames(String... arg) {
		if ((top == null)||(arg.length == 0)) return null;
		
		if (arg.length == 1) {
			String[] tmp = new String[2];
			tmp[0] = arg[0];
			tmp[1] = "0";
			arg = tmp;
		} else if (arg.length % 2 == 1) {
			String tmp[] = new String[arg.length+1];
			for (int i = 0; i<arg.length; i++) {
				tmp[i] = arg[i];
			}
			tmp[arg.length] = "0";
			arg = tmp;
		}
		
		ArrayList<Node> children = new ArrayList<Node>();
		children.add(top);
		ArrayList<String> values = null;
		
		try {
			Node node = null;		
			for (int i = 0; i<arg.length; i+=2) {
				children = getChildren(children, arg[i]);
				String p[] = arg[i].split("\\.");
				if (children == null) return null;
				int c = Integer.parseInt(arg[i+1]);
				node = null;
				for (Node n: children) {
					if ((c==0)&&(n.name.equals(p[p.length-1]))) {
						node = n;
						break;
					} else if (n.name.equals(p[p.length-1])) {
						c--;
					}
				}
				children = node.children;
			}
			values = new ArrayList<String>();
			for (Object o: node.attributes.keySet()) {
				values.add((String)o);
			}
		} catch (Exception e) {
			//-- do nothing
		}
		
		return values;
	}
	
	public static String str2xml(String s) {
		if (s == null) return "";
		s = s.trim();
		s = s.replaceAll("&","&amp;");
		s = s.replaceAll("<","&lt;");
		s = s.replaceAll(">","&gt;");
		s = s.replaceAll("'","&apos;");
		s = s.replaceAll("\"","&quot;");
		return s;
	}
	
	public static String xml2str(String s) {
		if (s == null) return "";
		s = s.replaceAll("&lt;","<");
		s = s.replaceAll("&gt;",">");
		s = s.replaceAll("&apos;","'");
		s = s.replaceAll("&quot;","\"");
		s = s.replaceAll("&amp;","&");
		return s;
	}
	
	
}
