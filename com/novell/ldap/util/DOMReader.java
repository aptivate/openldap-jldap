
package com.novell.ldap.util;

import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;
import com.novell.ldap.client.DSMLHandler;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class DOMReader implements LDAPReader{
    DSMLHandler handler;
    Element root;
    private int messageIndex=0;

    /**
     * Creates a reader that will convert the nodes inside of the specified
     * element to LDAPMessages.
     * <p>Requests or responses must be inside of the batchRequest or
     * batchResponse specified</p>
     * @param root  Element with a name of batchRequest or batchResponse.
     */
    public DOMReader(Element root) throws LDAPLocalException {
        this.root = root;
        handler = new DSMLHandler();
        String name = root.getNodeName();
        if (!name.equals("batchRequest") &&
            !name.equals("batchResponse") )
        {
            throw new IllegalArgumentException (
                    "DOMReader: specified root element " +
                    "must be a batchRequest or a batchResponse");
        }
        processNodes(root.getParentNode());
        return;
    }

    private void processNodes(Node node) throws LDAPLocalException {
        Node curChild = node.getFirstChild();
        DomAttributesWrapper wrapper = new DomAttributesWrapper();
        try {
            while(curChild!=null){
                String simpleName = curChild.getNodeName();
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
        if (this.messageIndex >= handler.queue.size())
            return null;
        return (LDAPMessage) handler.queue.get( this.messageIndex ++ );
    }

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

    }
}
