/* **************************************************************************
 * $Novell: DSMLReader.java,v 1.11 2002/10/22 22:15:46 $
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
 */

package com.novell.ldap.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import com.novell.ldap.*;


public class DSMLReader implements LDAPReader {

    private int messageIndex = 0;
    private DSMLHandler handler = new DSMLHandler();
    private boolean            requestFile=true;          // request file=true
    private String version = "2.1";

    public DSMLReader (String dsmlFile)
                throws LDAPLocalException, FileNotFoundException
    {
        this( new java.io.FileReader(dsmlFile) );
        return;
    }

    public DSMLReader (java.io.InputStream inputStream) throws LDAPLocalException
    {
        this( new java.io.InputStreamReader(inputStream));
        return;
    }


    public DSMLReader (java.io.Reader reader) throws LDAPLocalException
    {
        // Create an XML Parser
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            //spf.setValidating(true);

            XMLReader parser = null;
            SAXParser saxParser = spf.newSAXParser();
            parser = saxParser.getXMLReader();
            // assign the handler to the parser
            parser.setContentHandler(handler);
            // parse the document

            InputSource is = new InputSource(reader);
            parser.parse(is);
        } catch (FactoryConfigurationError e) {
            throw new LDAPLocalException(
                    "The SAX parser factory is configured incorrectly:" + e,
                    LDAPException.LOCAL_ERROR,
                    e);
        } catch (ParserConfigurationException e) {
            throw new LDAPLocalException(
                    "The SAX parser is configured incorrectly:" + e,
                    LDAPException.LOCAL_ERROR,
                    e);
        } catch (SAXException e) {
            throw new LDAPLocalException(
                    "The following error occured while parsing DSML: " + e,
                    LDAPException.DECODING_ERROR,
                    e);
        } catch (IOException e) {
            throw new LDAPLocalException(
                    "The following error occured while reading DSML: " + e,
                    LDAPException.LOCAL_ERROR,
                    e);
        }
        return;
    }

    public LDAPMessage readMessage()
    {
        if (this.messageIndex >= handler.queue.size())
            return null;
        return (LDAPMessage) handler.queue.get( this.messageIndex ++ );
    }
    
    /**
     * Gets the version of the LDIF data associated with the input stream
     *
     * @return the version number
     */
    public String getVersion()
    {
        return version;
    }
    
    /**
     * Returns true if request data ist associated with the input stream,
     * or false if content data.
     *
     * @return true if input stream contains request data.
     */
    public boolean isRequest()
    {
        return requestFile;
    }
}
