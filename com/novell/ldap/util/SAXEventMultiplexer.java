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

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class forms the base for deserialization(readDSML) of the DSML
 * Serialized classes.It pipes SAX events to LDAPXMLHandler which is 
 * implemented by each class that needs DSML serialization support.
 * 
 * @see com.novell.ldap.util.LDAPXMLHandler
 * @see org.xml.sax.helpers.DefaultHandler
 */
public class SAXEventMultiplexer extends DefaultHandler {

  private LDAPXMLHandler defaulthandler, currenthandler;
  private Stack Handlerstack = new Stack();

  private StringBuffer buffer = new StringBuffer();
  
  /**
   * This method is used to set the default LDAPXMLHandler, which
   * defines the root element for XML document being processed.
   * @param adefault LDAPXMLHandler for the root element of the XML Document.
   */
  public void setLDAPXMLHandler(LDAPXMLHandler adefault) {
    defaulthandler = adefault;
  }
  /** 
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  public void characters(char[] ch, int start, int length)
    throws SAXException {
    buffer.append(ch, start, length);
  }

  /**
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  public void endDocument() throws SAXException {

    //Kind of assertion
    if (currenthandler != null)
      throw new SAXException("Invalid System State,currenthandler not null in endDocument");

    super.endDocument();
  }

  /**
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  public void endElement(String uri, String localName, String qName)
    throws SAXException {

    //		Kind of assertion
    if (!localName.equals(currenthandler.getName())) {
      throw new SAXException("Invalid System State");
    }

    currenthandler.value(buffer);
    //	Reset StringBuffer.
    buffer.delete(0, buffer.length());
    currenthandler.endElement();

    //Pop the element.
    currenthandler = (LDAPXMLHandler) Handlerstack.pop();

    super.endElement(uri, localName, qName);
  }

  /**
   * @see org.xml.sax.ContentHandler#startDocument()
   */
  public void startDocument() throws SAXException {
    super.startDocument();
    if (defaulthandler == null) {
      throw new IllegalArgumentException("Illegal Argument , Default Handler not set");
    }
    currenthandler = null;
  }

  /**
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(
    String uri,
    String localName,
    String qName,
    Attributes attributes)
    throws SAXException {

    super.startElement(uri, localName, qName, attributes);

    Handlerstack.push(currenthandler);

    if (currenthandler == null) {
      currenthandler = defaulthandler;

    } else {

      currenthandler = currenthandler.nextHandler(localName);
    }
    //process Elements.

    if (!localName.equals(currenthandler.getName())) {
      throw new SAXException("Unknown tag:" + localName);
    }

    currenthandler.startElement();
    currenthandler.handleAttributes(attributes);

  }

  /**
   * This method is used to parse the XML Document and deserialize it using 
   * the default LDAPXMLHandler. 
   * @param inputStream The InputStream which contains the XML Document.
   * @return Resultant DeSerialized Object.
   * @throws IOException When parsing of Document fails or Deserialization 
   * fails.
   */
  public Object parseXML(InputStream inputStream) throws IOException {
    // Create an XML Parser
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      //spf.setValidating(true);

      SAXParser saxParser = spf.newSAXParser();

      InputSource is =
        new InputSource(new java.io.InputStreamReader(inputStream));

      // parse the document
      saxParser.parse(is, this);

    } catch (FactoryConfigurationError e) {

      throw new IOException(
        "The SAX parser factory is configured incorrectly:" + e);

    } catch (ParserConfigurationException e) {
      throw new IOException("The SAX parser is configured incorrectly:" + e);
    } catch (SAXNotRecognizedException e) {
      throw new IOException(
        "The XML cannot be parsed is configured incorrectly:" + e);
    } catch (SAXException e) {
      throw new IOException(
        "The XML cannot be parsed is configured incorrectly:" + e);
    }
    return defaulthandler.getObject();
  }
}
