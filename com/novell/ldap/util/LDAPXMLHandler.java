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

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class is used to handle the deserialization events emitted by 
 * SAXEventMultiplexer. It is a variation of SAXEvents Handler, simplied for
 * accessing specific required events only.
 * @see com.novell.ldap.util.SAXEventMultiplexer
 */
public class LDAPXMLHandler {

  private String elementName, value;
  private LDAPXMLHandler parenthandler;
  private HashMap m_handler_map = new HashMap();
  private int state = INIT;
  private final static int INIT = 0;
  private final static int START = 1;
  private final static int CHILDELEMENT = 2;
  private final static int END = 3;
  private Object resultantObject = null;

  /** Default Constructor with element name for this Handler and parent
   * LDAPXMLHandler to be processed. 
   * @param ElementName String name of the XML element to process.
   * @param parent LDAPXMLHandler , which is the parent of this Element.
   */
  public LDAPXMLHandler(String ElementName, LDAPXMLHandler parent) {
    elementName = ElementName;
    parenthandler = parent;
    state = INIT;
    initHandler();
  }
  /**
   * Returns the Parent LDAPXMLHandler.
   * @return LDAPHandler
   */
  protected LDAPXMLHandler getParent() {
    return parenthandler;
  }
  /**
   * This method is used to access the child element's LDAPXMLHandler.
   * The events for the child elements would be pipelined to returned
   * handler.
   * @param childelementName The element name for the xml element.
   * @return LDAPXMLHandler for the specific event.
   */
  LDAPXMLHandler nextHandler(String childelementName) {

    if ((state != START) && (state != CHILDELEMENT))
      throw new IllegalStateException("nextHandler()");
    state = CHILDELEMENT;
    Object ob = m_handler_map.get(childelementName);

    return (LDAPXMLHandler) ob;
  }

  /**
   * This method is called when a new element controlled by this 
   * handler is found.
   * @see #endElement() 
   */
  final void startElement() {
    state = START;
  }

  /**
   * This is used to handle the processing of the characters for the 
   * specific xml tags.
   * @param buf StringBuffer containing all the chars contained in this tag.
   */
  final void value(StringBuffer buf) {

    if ((state != START) && (state != CHILDELEMENT))
      throw new IllegalStateException("value()");
    //Empty Handler;
    value = buf.toString();
  }

  /**
   * This method returns the Name of the Element to be handled by this 
   * LDAPXMLHandler. 
   * @return String xml element Name.
   */
  protected final String getName() {
    return elementName;
  }
  /**
   * This method returns the Value stored in this element. 
   * @return String Value stored in this Element.
   */
  protected final String getValue() {
    return value;
  }
  /**
   * Sets the Handler for the Child LDAPXMLHandler, it expects only
   * one handler for each code base.
   * @param handler This handler for specific child element. 
   */
  protected final void setchildelement(LDAPXMLHandler handler) {
    if (state != INIT)
      throw new IllegalStateException("setchildelement");
    m_handler_map.put(handler.getName(), handler);
  }
  /**
   * This method defines the generic adapter to be used by child elements
   * to return the values of the processed Child Elements.
   * @param tag String XML tag.
   * @param value Object deserialized value for the specific tag.
   */
  protected void addValue(String tag, Object value) {
    //default empty implementation
  }
  /**
   * This method is used to initialize the Handler. It is expected 
   * that most of the implementation of this class would overide is method.
   */
  protected void initHandler() {
    //	default empty implementation
  }
  /**
   * This method is used to signal the end of Element. It is expected 
   * that most of the implementation of this class would overide is method.
   * and create the Objects from the deserialize data obtained so far.
   */
  protected void endElement() {
    //	default empty implementation
  }
  /**
   * This method is used to handle the Attributes associated with this Element.
   * It is expected that most of the implementation of this class would 
   * overide is method and do the required processing of the elements.  
   * @param attributes Attributes found with this element. 
   * @throws SAXException When the specific mandatory element is not found, so 
   * as to stop the processing.
   */
  protected void handleAttributes(Attributes attributes) throws SAXException {
    //	default empty implementation
  }
  /**
   * This method returns the deserialize Object generated by this class. 
   * @return Object Deserialize form of XML tag.
   */
  public final Object getObject() {
    return resultantObject;
  }
  /**
   * This method is used to set the Deserialized Object and it also calls
   * the parent LDAPXMLHandler (if not null), returning the value using 
   * addValue() 
   * @param data Object data.
   * @see #addValue(String, Object)
   */
  protected final void setObject(Object data) {

    if ((state != START) && (state != CHILDELEMENT))
      throw new IllegalStateException("setObject");
    resultantObject = data;
    if (getParent() != null) {
      getParent().addValue(getName(), data);
    }
  }
}
