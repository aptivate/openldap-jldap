/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2002 - 2003 Novell, Inc. All Rights Reserved.
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

import com.novell.ldap.*;

/**
 * Reads, parses and converts DSML into LDAPMessages.
 *
 * <p>Reads DSML, Directory Service Markup Language, from files, streams and
 * readers, and returns LDAPMessages.  Note that some XML applications will have
 * DSML pre-parsed into DOM objects, in which case DOMReader should be used.</p>
 *
 * @see DSMLWriter
 * @see DOMReader
 * @see LDAPMessage
 * @see com.novell.ldap.LDAPConnection#sendRequest
 */

public class DSMLReader implements LDAPReader {

    private int messageIndex = 0;
    private DSMLHandler handler = new DSMLHandler();
    private boolean            requestFile=true;          // request file=true
    private String version = "2.0";

    /**
     * Creates a reader that reads from a file containing XML with DSML tags.
     *
     * <p>All XML tags before and after batchRequests or batchResponses are
     * ignored.  All requests or response within batchRequests and
     * batchResponses are converted into LDAPMessages</p>
     *
     * @param dsmlFile  XML file with a DSML batchRequest or batchResponse
     * @throws LDAPLocalException Occurs when no batchRequest or batchResponse
     * is found, or the document is invalid DSML.
     * @throws FileNotFoundException Occurs when the specied file is not found
     */
    public DSMLReader (String dsmlFile)
                throws LDAPLocalException, FileNotFoundException
    {
        this( new java.io.FileReader(dsmlFile) );
        return;
    }

    /**
     * Creates a reader that reads from an inputStream containing xml with DSML
     * tags.
     * <p>All XML tags before and after batchRequests or batchResponses are
     * ignored.  All requests or response within batchRequests and
     * batchResponses are converted into LDAPMessages</p>

     * @param inputStream Stream of XML with a DSML batchRequest or
     * batchResponse
     * @throws LDAPLocalException Occurs when no batchRequest or batchResponse
     * is found, or the document is invalid DSML.
     */
    public DSMLReader (java.io.InputStream inputStream) throws LDAPLocalException, UnsupportedEncodingException
    {
        
    	this( new java.io.InputStreamReader(inputStream,"UTF8"));
        return;
    }

    /**
     * Creates a reader that reads from a reader containing xml with DSML
     * tags.
     * <p>All XML tags before and after batchRequests or batchResponses are
     * ignored.  All requests or response within batchRequests and
     * batchResponses are converted into LDAPMessages</p>
     *
     * @param reader Reader of XML with a DSML batchRequest or batchResponse
     * @throws LDAPLocalException Occurs when no batchRequest or batchResponse
     * is found, or the document is invalid DSML.
     */
    public DSMLReader (java.io.Reader reader) throws LDAPLocalException
    {
        // Create an XML Parser
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            //spf.setValidating(true);

            SAXParser saxParser = spf.newSAXParser();

            InputSource is = new InputSource(reader);

            // parse the document
            saxParser.parse(is, handler);

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
        } catch (SAXNotRecognizedException e){
            throw new LDAPLocalException(
                    null,
                    LDAPException.PROTOCOL_ERROR,
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

    /**
     * Retrieves the current LDAPMessage and advances to the next.
     *
     * <p>This method is used to iterate over all DSML tags parsed into
     * LDAPMessages<p>
     * @return LDAPMessage found in the DSML source specified in the
     * constructor.
     */
    public LDAPMessage readMessage()
    {
        if (this.messageIndex >= handler.getQueue().size())
            return null;
        return (LDAPMessage) handler.getQueue().get( this.messageIndex ++ );
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

    /**
     * Retrieves the optional requestID attribute on a BatchRequests.
     * @return requestID on a batchRequest or <tt>null</tt> if requestID is not
     * specified or the content is in a batchResponse.
     */
    public String getBatchRequestID(){
        return this.handler.getBatchRequestID();
    }

    /**
     * Indicates whether the requests in a batchRequest can be executed in
     * parallel.
     *
     * <p>This is determined by reading the "processing" attribute on the tag
     * batchRequest.  This attribute can take on the values of "sequential" or
     * "parallel" and defaults to "sequential" when the attribute is absent.<p>
     *
     * @return <tt>true</tt> if the content is a batchRequest with the attribute
     * <tt>processing</tt> equal to <tt>parallel</tt>; and false otherwise.
     *
     * <p>Other batchRequest properties:<p>
     * @see #getBatchRequestID
     * @see #isResponseUnordered
     * @see #isResumeOnError
     */
    public boolean isParallelProcessing(){
        return this.handler.isParallelProcessing();
    }

    /**
     * If requests in a batchRequest can be executed in parallel, this specifies
     * whether the responses can be written in any order.
     *
     * <p>This is determined by reading the "responseOrder" attribute on the tag
     * batchRequest.  This attribute can take on the values of "sequential" or
     * "unordered" and defaults to "sequential" when the attribute is absent.<p>
     *
     * @return <tt>true</tt> if the content is a batchRequest with the attribute
     * <tt>responseOrder</tt> equal to <tt>unordered</tt>; and false otherwise.
     *
     */
    public boolean isResponseUnordered(){
        return this.handler.isResponseUnordered();
    }

    /**
     * Indicates whether the execution of requests in a batchRequest should
     * resume or stop should an error occur.
     *
     * <p>This is determined by reading the "onError" attribute on the tag
     * batchRequest.  This attribute can take on the values of "resume" or
     * "exit" and defaults to "exit" when the attribute is absent.<p>
     *
     * @return <tt>true</tt> if the content is a batchRequest with the attribute
     * <tt>onError</tt> equal to <tt>resume</tt>; and false otherwise.
     *
     * <p>Other batchRequest properties:<p>
     * @see #getBatchRequestID
     * @see #isParallelProcessing
     * @see #isResponseUnordered
     */
    public boolean isResumeOnError(){
        return this.handler.isResumeOnError();
    }
    
    public ArrayList getErrors() {
    	return this.handler.getErrors();
    }
}
