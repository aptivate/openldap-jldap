/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2002 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.util;

import java.io.UnsupportedEncodingException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class provides the implementation of the LDAPXMLHandler for the 
 * <value> tag of DSML.
 * @see com.novell.ldap.util.LDAPXMLHandler
 */
public class ValueXMLhandler extends LDAPXMLHandler {
  private boolean isBase64;

  /**
   * This constructor creates the ValueXMLHandler with the tagname as 'value'. 
   * @param parent Parent LDAPXMLHandler. 
   */
  public ValueXMLhandler(LDAPXMLHandler parent) {
    super("value", parent);
  }

  /**
   * This constructor makes no assumption about the tagname and allows the
   * user to specify the same. Used specifically by LDAPControl 
   * @param parent Parent LDAPXMLHandler.
   * @param tagname String name of the XML element to process.
   */
  public ValueXMLhandler(String tagname, LDAPXMLHandler parent) {
    super(tagname, parent);
  }
  protected void endElement() {
    try {
      byte[] temp;
      String name = getName();
      String value = getValue();
      if (this.isBase64) {
        temp = Base64.decode(value);
      } else {
        temp = value.getBytes("UTF-8");
      }
      //getParent().addValue("value", temp);
	  getParent().addValue(name, temp);
    } catch (UnsupportedEncodingException e) {
    }
  }
  protected void handleAttributes(Attributes attributes) throws SAXException {
    String temp = attributes.getValue("xsi:type");
    if (temp != null && temp.equals("xsd:base64Binary")) {
      isBase64 = true;
    } else {
      isBase64 = false;
    }
  }
  
   /**
   * This mehod separates whitespaces in non-text nodes during XML de-serialization.
   * The whitespaces are added in non-text nodes because of XML doc written during
   * Serialization needs to be stored with proper indentation. 
   * @param whole The XML String with whitespaces in non-text nodes read from stream
   * during de-serialization
   * @param buffer StringBuffer which holds the resulting string after removal of 
   * unwanted whitespaces in non-text nodes
   */
   public static void parseInput(String whole, StringBuffer buffer){		 
		String token, part;
		int start, end;
		start = whole.indexOf('<');
		if(start != -1)
		{
			//if Index of start is not '<'
			if(start > 0)
				  start = 0;
			end = whole.indexOf('>');
			token = whole.substring(start, end + 1);
			buffer.append(token);
			part = whole.substring(end + 1).trim();
			parseInput(part, buffer);  
		}
		return;
	 }
   
	 /**
	 * This mehod supports for adding indentation to the XML document written 
	 * during XML serialization.
	 * @param indentTabs The integer specifying the number of indentation tabs
	 */
	 public static String newLine(int indentTabs){
		  String tabString = "    "; 
		  String result = "\n";   
		
		  for (int i=0; i< indentTabs; i++){
			  result += tabString;
		  }
		  return result;
	 }

}
