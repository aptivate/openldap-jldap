
package com.novell.ldap.util;

import com.novell.ldap.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Writes LDAPMessages into a DOM structure for DSML batch requests and
 * batch responses.
 *
 * @see DOMReader
 * @see DSMLWriter
 */
public class DOMWriter implements LDAPWriter {
        /* document object used to create DOM nodes */
        private Document doc;
        /* root node of either batchRequest or batchResponse.*/
        private Element root;

    /**
     * Initializes the DOMWriter
     * @throws ParserConfigurationException
     *  Occurs if a parser could not be found or is misconfigured.
     */
    public DOMWriter() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.newDocument();
        return;
    }

    /**
     * Retrieves the version of DSML being written.
     * Currently only 2.0 is supported.
     *  @return Version of DSML being used.
     */
    public String getVersion() {
        return "2.0";
    }

    /**
     * Used to identify if the root node is a batchRequest or not.
     * @return true if the root node of the DOM is a batchRequest and false
     * otherwise.
     */
    public boolean isRequest() {
        return root.getNodeName().equals("batchRequest");
    }

    /**
     * Converts the message to a DOM element and adds the message to the DOM
     * structure.
     * @param message  LDAPMessage to write
     * @throws LDAPLocalException Occurs when a message is written out of
     * sequence, i.e. a response is written into a batchRequest.
     */
    public void writeMessage(LDAPMessage message)
            throws LDAPLocalException, IOException
    {
        if (root == null){
            if (message.isRequest()){
                root = doc.createElement("batchRequest");
            }else{
                root = doc.createElement("batchResponse");
            }
        }
        root.appendChild( message2Element(message) );
        return;
    }

    /**
     * This method is not implemented and throws an IOException.
     * @throws IOException Always.
     */
    public void writeComments(String comments) throws IOException {
        throw new IOException ( "DOMWriter: writeComment not implemented");
    }

    /**
     * Utility method to convert an LDAPMessage to a DSML DOM element .
     * @param message  An LDAPMessage to be converted to a DSML DOM element.
     * @return element A DOM element representing either a response or a
     * request in DSML.
     */
    public Element message2Element(LDAPMessage message)
    {
        Element e=null;
        if (message instanceof LDAPResponse){
            LDAPResponse r = (LDAPResponse) message;
            e = doc.createElement("delResponse");

            e.setAttribute("requestID", ""+message.getMessageID());
            Element resultCode = doc.createElement("resultCode");
            int result =r.getResultCode();
            resultCode.setAttribute("code",  result + "");
            resultCode.setAttribute("descr", LDAPException.resultCodeToString(result));

            e.appendChild(resultCode);
            /* Server Message: */
            String temp = r.getErrorMessage();
            if (temp != null && temp.length() > 0){
                Element err = doc.createElement("errorMessage");
                Text errorMessage = doc.createTextNode(temp);
                err.appendChild(errorMessage);
                e.appendChild(err);
            }

            /* MatchedDN*/
            temp = r.getMatchedDN();
            if (temp != null && temp.length() > 0){
                Element matched = doc.createElement("matchedDN");
                Text text = doc.createTextNode(temp);
                matched.appendChild(text);
                e.appendChild(matched);
            }
        }
        return e;
    }
}
