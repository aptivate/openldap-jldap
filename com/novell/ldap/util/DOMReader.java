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

import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Reads pre-parsed DSML as LDAPMessages.
 *
 * <p>Some applications have access to DSML(Directory Services Markup Language)
 * pre-parsed into DOM(Document Object Model) objects.  This utility class will
 * read a DOM structure and translate it into LDAPMessages. If DSML comes from
 * a stream or file then DSMLReader may also be used.
 * </p>
 *
 * @see DOMWriter
 * @see DSMLReader
 * @see LDAPMessage
 * @see com.novell.ldap.LDAPConnection#sendRequest
 */
public class DOMReader implements LDAPReader{
    DSMLHandler handler;
    Element root;
    private int messageIndex=0;

    /**
     * @deprecated use #DOMReader(Element)
     * Creates a reader that reads a DOM document and translates it into
     * LDAPMessages.
     * <p>The first batchRequest or batchResponse is located and all nodes
     * within it will be read and translated into LDAPMessages</p>
     *
     * @param dsmlDoc  Document with a DSML batchRequest or batchResponse
     * @throws LDAPLocalException Occurs when no batchRequest or batchResponse
     * is found, or the document is invalid DSML.
     */
    public DOMReader(Document dsmlDoc) throws LDAPLocalException {
        this.root = (Element)
                dsmlDoc.getElementsByTagName("batchRequest").item(0);
        if (this.root == null){
            this.root = (Element)
                    dsmlDoc.getElementsByTagNameNS("*", "batchResponse").item(0);
        }
        if (this.root == null){
            throw new IllegalArgumentException(
                 "DOMReader: could not locate a batchRequest or batchResponse");

        }
        handler = new DSMLHandler();
        processNodes(root.getParentNode());
    }

    /**
     * Creates a reader that read a DOM element and translate it into
     * LDAPMessages.
     * <p>Requests or responses must be inside of the batchRequest or
     * batchResponse specified</p>
     * @param root  Element with a name of batchRequest or batchResponse.
     * @throws LDAPLocalException Occurs when no batchRequest or batchResponse
     * is found, or the Element is invalid DSML.
     */
    public DOMReader(Element root) throws LDAPLocalException {
        this.root = root;
        String name = root.getLocalName();
        if (name == null){
            name = root.getNodeName();
        }
        if (!name.equals("batchRequest") &&
            !name.equals("batchResponse") )
        {
            throw new IllegalArgumentException (
                    "DOMReader: specified root element " +
                    "must be a batchRequest or a batchResponse");
        }
        handler = new DSMLHandler();

        processNodes(root.getParentNode());
        return;
    }

    /**
     * Recursively processes DOM nodes by pulling out the tag name, attributes
     * and text values and calling the DSMLHandler methods to construct
     * LDAPMessages.
     * @param node DOM Node to process
     * @throws LDAPLocalException thrown if DSMLHandler finds an error in DSML
     */
    private void processNodes(Node node) throws LDAPLocalException {
        Node curChild = node.getFirstChild();
        DomAttributesWrapper wrapper = new DomAttributesWrapper();
        try {
            while(curChild!=null){
                String simpleName = curChild.getLocalName();
                if (simpleName == null){
                    simpleName = curChild.getNodeName();
                }
                if (curChild instanceof Element){
                    wrapper.setAttrs(curChild.getAttributes());
                    handler.startElement(
                            "", simpleName, simpleName, wrapper);
                }else if (curChild instanceof Text){
                    String value = curChild.getNodeValue();
                    handler.characters(value.toCharArray(), 0, value.length());
                }
                //recurse
                processNodes(curChild);

                if (curChild instanceof Element){
                    handler.endElement("", simpleName, simpleName);
                }
                curChild = curChild.getNextSibling();
            }
        } catch (SAXException e) {
            throw new LDAPLocalException(
                    "DOMReader error while traversing DOM:",
                    LDAPException.LOCAL_ERROR, e);
        }
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
     * Reads the current element and returns an LDAPMessage.
     * @return LDAPMessage found in the DSML DOM structure specified in the
     * constructor.
     */
    public LDAPMessage readMessage()
    {
        if (this.messageIndex >= handler.getQueue().size())
            return null;
        return (LDAPMessage) handler.getQueue().get( this.messageIndex ++ );
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

    /**
     * This wrapper class takes an existing set of DOM attributes, NamedNodeMap,
     * and provides an interface for SAX attributes.
     */
    static private class DomAttributesWrapper implements org.xml.sax.Attributes {
        NamedNodeMap nnm;
        public void setAttrs(NamedNodeMap domAttributes){
            nnm = domAttributes;
        }
        public int getLength() {
            return nnm.getLength();
        }

        public String getURI(int index) {
            return nnm.item(index).getNamespaceURI();
        }

        public String getLocalName(int index) {
            return nnm.item(index).getLocalName();
        }

        public String getQName(int index) {
            Node node = nnm.item(index);
            return node.getNamespaceURI() +"."+ node.getLocalName();
        }

        public String getType(int index) {
            /*CDATA is the default value returned*/
            return "CDATA";
        }

        public String getValue(int index) {
            return nnm.item(index).getNodeValue();
        }

        public int getIndex(String uri, String localPart) {
            return -1;
        }

        public int getIndex(String qName) {
            return -1;
        }

        public String getType(String uri, String localName) {
            /*CDATA is the default value returned*/
            return "CDATA";
        }

        public String getType(String qName) {
            /*CDATA is the default value returned*/
            return "CDATA";
        }

        public String getValue(String uri, String localName) {
            return nnm.getNamedItemNS(uri, localName).getNodeValue();
        }

        public String getValue(String qName) {
            Node node = nnm.getNamedItem(qName);
            if (node == null){
                return null;
            }
            String value = node.getNodeValue();
            return value;
            //return nnm.getNamedItem(qName).getNodeValue();
        }
    }//DomAttributesWrapper
}//DOMReader
