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

import com.novell.ldap.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * Writes LDAPMessages into a DOM structure as DSML batch requests and
 * batch responses.
 *
 * @see DOMReader
 * @see DSMLWriter
 * @see LDAPMessage
 */
public class DOMWriter implements LDAPWriter
 {
    /* Document object used to create DOM nodes */
    private Document doc;
    /* Root node of either batchRequest or batchResponse.*/
    private Element root;
    private int state = NEW_BATCH;
    private static final int NEW_BATCH = 0;
    private static final int REQUEST_BATCH = 1;
    private static final int RESPONSE_BATCH = 2;
    private static final int SEARCH_RESPONSE = 3;
    private Element searchNode;

    /**
     * Initializes the DOMWriter.
     * @throws ParserConfigurationException
     *  Occurs if a parser could not be found or is misconfigured.
     */
    public DOMWriter() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.newDocument();
        return;
    }

    /**
     * Retrieves the version of DSML being written, currently only 2.0 is
     * supported.
     *  @return Version of DSML being used.
     */
    public String getVersion()
    {
        return "2.0";
    }

    /**
     * Used to identify if the root node is a batchRequest or not.
     * @return true if the root node of the DOM is a batchRequest and false
     * otherwise.
     */
    public boolean isRequest()
    {
        return root.getNodeName().equals("batchRequest");
    }

    /**
     * This method is not implemented and is silently ignored.
     */
    public void writeComments(String comments) throws IOException
    {
        return;
    }

    /**
     * Writes the LDAPMessage into the DOMStructure.
     *
     * @param message  LDAPMessage to write
     * @throws LDAPLocalException Occurs when a message is written out of
     * sequence, i.e. a response is written into a batchRequest.
     */
    public void writeMessage(LDAPMessage message)
            throws LDAPLocalException, IOException
    {
        checkState(message);
        if (message.getType() == LDAPMessage.SEARCH_RESPONSE ||
            message.getType() == LDAPMessage.SEARCH_RESULT ||
            message.getType() == LDAPMessage.SEARCH_RESULT_REFERENCE)
        {
            searchNode.appendChild(message2Element(message));
        } else {
            root.appendChild( message2Element(message) );
        }
        if (message.getType()== LDAPMessage.SEARCH_RESULT){
            state = RESPONSE_BATCH;
            searchNode = null;
        }
        return;
    }

    /**
     * Write an LDAP record into LDIF file as LDAPContent data.
     * An LDAPEntry is written as a DSML SearchResultEntry record.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param entry LDAPEntry object
     *
     * @throws LDAPLocalException if data and content are mixed.
     *
     * @throws LDAPLocalException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPEntry
     */
    public void writeEntry( LDAPEntry entry)
            throws LDAPLocalException
    {
        checkState( entry);
        myWriteEntry( entry, null);
        return;
    }

    // Javadoc from interface
    /**
     * Write an LDAP record into LDIF file as LDAPContent data.
     * An LDAPEntry is written as a DSML SearchResultEntry record.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param entry LDAPEntry object
     *
     * @param controls Controls that were returned with this entry
     *
     * @throws LDAPLocalException if data and content are mixed.
     *
     * @see com.novell.ldap.LDAPEntry
     */
    public void writeEntry( LDAPEntry entry, LDAPControl[] controls)
            throws LDAPLocalException
    {
        checkState( entry);
        myWriteEntry( entry, controls);
        return;
    }

    /**
     * Write an LDAP record into LDIF file as LDAPContent data.
     * An LDAPEntry is written as a DSML SearchResultEntry record.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param entry object
     *
     * @param controls Controls that were returned with this entry
     *
     * @param requestID the String that associates this response with the request
     *
     * @throws LDAPLocalException if data and content are mixed.
     *
     * @see com.novell.ldap.LDAPEntry
     */
    public void writeEntry( LDAPEntry entry,
                            LDAPControl[] controls,
                            String requestID)
            throws LDAPLocalException
    {
        checkState( entry);
        Element e = myWriteEntry( entry, controls);
        if( (requestID != null) && (requestID.length() != 0)) {
            e.setAttribute("requestID", requestID);
        }
        return;
    }

    // Javadoc from interface
    private Element myWriteEntry( LDAPEntry entry,
                                  LDAPControl[] controls )
    {
        Element e = doc.createElement("searchResultEntry");
        e.setAttribute("dn", entry.getDN());

        LDAPAttributeSet set = entry.getAttributeSet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            LDAPAttribute attr = (LDAPAttribute)iterator.next();
            Element attribute = doc.createElement("attr");
            writeAttribute(attribute, attr);
            e.appendChild(attribute);
        }
        if (controls != null) {
            writeControls(e, controls);
        }
        searchNode.appendChild( e);
        return e;
    }

    private void writeAttribute(Element attribute, LDAPAttribute attr)
    {
         attribute.setAttribute("name", attr.getName());

         String values[] = attr.getStringValueArray();
         byte bytevalues[][] = attr.getByteValueArray();
         for(int i=0; i<values.length; i++){
             Element value = doc.createElement("value");
             if (Base64.isValidUTF8(bytevalues[i], false)){
                 value.appendChild(doc.createTextNode(values[i]));
             } else {
                 value.setAttribute("xsi:type", "base64Binary");
                 value.appendChild(doc.createTextNode(
                         Base64.encode(bytevalues[i])));
             }
             attribute.appendChild(value);
         }
     }

    /**
     * Utility method to convert an LDAPMessage to a DSML DOM element.
     * @param message  An LDAPMessage to be converted to a DSML DOM element.
     *
     * @return element A DOM element representing either a response or a
     * request in DSML.
     */
    public Element message2Element(LDAPMessage message)
    {
        Element e=null;
        switch (message.getType()) {
            //requests:
            case LDAPMessage.BIND_REQUEST:
                e = doc.createElement("authRequest");
                e.setAttribute("principle", ((LDAPBindRequest)message).getAuthenticationDN());
                break;
            case LDAPMessage.UNBIND_REQUEST:
                throw new java.lang.UnsupportedOperationException(
                    "Writing of an unbind request is not currently supported");
            case LDAPMessage.SEARCH_REQUEST:{
                e = doc.createElement("searchRequest");
                LDAPSearchRequest sr = (LDAPSearchRequest) message;
                e.setAttribute("dn", sr.getDN());

                //filter
                Element filter = doc.createElement("filter");
                writeFilter(filter, sr.getSearchFilter());
                e.appendChild(filter);

                //attributes
                Element attributes = doc.createElement("attributes");
                String attrNames[] = sr.getAttributes();
                for (int i=0; i< attrNames.length; i++){
                    Element attribute = doc.createElement("attribute");
                    attribute.setAttribute("name", attrNames[i]);
                    attributes.appendChild(attribute);
                }
                e.appendChild(attributes);

                //scope
                int temp = sr.getScope();
                if (temp==LDAPConnection.SCOPE_BASE){
                    e.setAttribute("scope", "baseObject");
                } else if (temp==LDAPConnection.SCOPE_ONE){
                    e.setAttribute("scope", "singleLevel");
                } else if (temp==LDAPConnection.SCOPE_SUB){
                    e.setAttribute("scope", "wholeSubtree");
                }

                //dereference aliases
                temp = sr.getDereference();
                if (temp==LDAPSearchConstraints.DEREF_NEVER){
                    e.setAttribute("derefAliases", "neverDerefAliases");
                } else if (temp==LDAPSearchConstraints.DEREF_SEARCHING){
                    e.setAttribute("derefAliases", "derefInSearching");
                } else if (temp==LDAPSearchConstraints.DEREF_FINDING){
                    e.setAttribute("derefAliases", "derefFindingBaseObj");
                } else if (temp==LDAPSearchConstraints.DEREF_ALWAYS){
                    e.setAttribute("derefAliases", "derefAlways");
                }

                //sizeLimit 0 is default
                temp = sr.getMaxResults();
                if (temp != 0){
                    e.setAttribute("sizeLimit", Integer.toString(temp));
                }

                //timeLimit 0 is default
                temp = sr.getServerTimeLimit();
                if (temp != 0){
                    e.setAttribute("timeLimit", Integer.toString(temp));
                }

                //typesOnly false is default
                if ( sr.isTypesOnly() )
                {
                    e.setAttribute("typesOnly", "true");
                }
                break;
            }
            case LDAPMessage.MODIFY_REQUEST:{
                e = doc.createElement("modifyRequest");
                LDAPModifyRequest modreq= (LDAPModifyRequest)message;
                e.setAttribute("dn", modreq.getDN());

                LDAPModification[] mods = modreq.getModifications();
                for(int i=0;i<mods.length; i++){
                    Element m = doc.createElement("modification");
                    LDAPAttribute attr = mods[i].getAttribute();
                    if (mods[i].getOp() == LDAPModification.ADD){
                        m.setAttribute("operation", "add");
                    } else if (mods[i].getOp() == LDAPModification.DELETE){
                        m.setAttribute("operation", "delete");
                    } else if (mods[i].getOp() == LDAPModification.REPLACE){
                        m.setAttribute("operation", "replace");
                    }
                    writeAttribute(m, attr);
                    e.appendChild(m);
                }
                break;
            }
            case LDAPMessage.ADD_REQUEST:{
                e = doc.createElement("addRequest");
                LDAPAddRequest add = (LDAPAddRequest)message;
                LDAPEntry entry = add.getEntry();
                e.setAttribute("dn", entry.getDN());
                Iterator attrs = entry.getAttributeSet().iterator();
                while(attrs.hasNext()){
                    Element a = doc.createElement("attr");
                    writeAttribute(a, (LDAPAttribute)attrs.next());
                    e.appendChild(a);
                }
                break;
            }
            case LDAPMessage.DEL_REQUEST:
                e = doc.createElement("delRequest");
                e.setAttribute("dn", ((LDAPDeleteRequest)message).getDN());
                break;
            case LDAPMessage.MODIFY_RDN_REQUEST:{
                e = doc.createElement("modDNRequest");
                LDAPModifyDNRequest moddn = (LDAPModifyDNRequest)message;
                e.setAttribute("dn", moddn.getDN());
                e.setAttribute("newrdn", moddn.getNewRDN());
                e.setAttribute("deleteoldrdn", moddn.getDeleteOldRDN()+"");
                String temp = moddn.getParentDN();
                if (temp != null && temp.length() >0){
                    e.setAttribute("newSuperior", temp);
                }
                break;
            }
            case LDAPMessage.COMPARE_REQUEST: {
                e = doc.createElement("compareRequest");
                LDAPCompareRequest comp = (LDAPCompareRequest) message;
                e.setAttribute("dn", comp.getDN());

                Element assertion = doc.createElement("assertion");
                assertion.setAttribute("name", comp.getAttributeDescription());
                e.appendChild(assertion);

                Element value = doc.createElement("value");
                assertion.appendChild(value);

                byte[] compareValue = comp.getAssertionValue();
                if (Base64.isValidUTF8(compareValue, false)){
                    try {
                        value.appendChild(doc.createTextNode(
                                new String(compareValue, "UTF-8")));
                    } catch (UnsupportedEncodingException uee) {
                        throw new RuntimeException("UTF-8 not supported by JVM:"
                                + uee);
                    }
                } else {
                    value.setNodeValue(Base64.encode(compareValue));
                    //value.appendChild(doc.createTextNode(
                    //        Base64.encode(compareValue)));
                }
                break;
            }
            case LDAPMessage.ABANDON_REQUEST:
                e = doc.createElement("abandonRequest");
                e.setAttribute("abandonID",findRequestID(message));
                break;
            case LDAPMessage.EXTENDED_REQUEST:{
                e = doc.createElement("extendedRequest");
                LDAPExtendedOperation xreq =
                        ((LDAPExtendedRequest)message).getExtendedOperation();
                Element reqName = doc.createElement("requestName");
                reqName.appendChild(doc.createTextNode(xreq.getID()));
                e.appendChild(reqName);

                byte value[] = xreq.getValue();
                if (value != null){
                    Element reqValue = doc.createElement("requestValue");
                    reqValue.setAttribute("xsi:type", "xsd:base64Binary");
                    reqValue.appendChild(
                            doc.createTextNode(Base64.encode(xreq.getValue())));
                    e.appendChild(reqValue);
                }
                break;
            }
            //Responses:
            case LDAPMessage.SEARCH_RESPONSE:
                e = myWriteEntry( ((LDAPSearchResult)message).getEntry(),
                                  message.getControls());
                break;
            case LDAPMessage.SEARCH_RESULT_REFERENCE:
                e = doc.createElement("searchResultReference");
                String[] refs = ((LDAPSearchResultReference)
                        message).getReferrals();

                for(int i=0; i< refs.length; i++){
                    Element ref = doc.createElement("ref");
                    Text value = doc.createTextNode(refs[i]);
                    ref.appendChild(value);
                    e.appendChild(ref);
                }
                break;
            case LDAPMessage.SEARCH_RESULT:  //final search done message
                e = doc.createElement("searchResultDone");
                writeResult(e, (LDAPResponse)message);
                break;
            case LDAPMessage.MODIFY_RESPONSE:
                e = doc.createElement("modifyResponse");
                writeResult(e, (LDAPResponse)message);
                break;
            case LDAPMessage.ADD_RESPONSE:
                e = doc.createElement("addResponse");
                writeResult(e, (LDAPResponse)message);
                break;
            case LDAPMessage.DEL_RESPONSE:
                e = doc.createElement("delResponse");
                writeResult(e, (LDAPResponse)message);
                break;
            case LDAPMessage.MODIFY_RDN_RESPONSE:
                e = doc.createElement("modDNResponse");
                writeResult(e, (LDAPResponse)message);
                break;
            case LDAPMessage.COMPARE_RESPONSE:
                e = doc.createElement("compareResponse");
                writeResult(e, (LDAPResponse)message);
                break;
            case LDAPMessage.EXTENDED_RESPONSE:
                LDAPExtendedResponse xResp = (LDAPExtendedResponse) message;
                e = doc.createElement("extendedResponse");
                writeResult(e, (LDAPResponse)message);
                Element resp = doc.createElement("responseName");
                Text text = doc.createTextNode(xResp.getID());
                resp.appendChild(text);
                e.appendChild(resp);

                byte[] value = xResp.getValue();
                if (value != null){
                    resp = doc.createElement("response");
                    resp.setAttribute("xsi:type", "base64Binary");
                    resp.appendChild(doc.createTextNode(Base64.encode(value)));
                    e.appendChild(resp);
                }
                break;
        }
        //if valid tag && write requestIDs is set.
        String id =  findRequestID(message);
        if( (id != null) && (id.length() != 0)) {
            e.setAttribute("requestID", id);
        }
        return e;
    }

    /**
     * Common code for =, >=, <=, and ~=.
     */
    private void writeMatching( Element newElement, Iterator itr)
    {
        newElement.setAttribute("name", (String)itr.next());
        Element valueNode = doc.createElement("value");
        newElement.appendChild(valueNode);
        byte[] value = (byte[])itr.next();
        String text = byte2String(value);
        valueNode.appendChild(doc.createTextNode(text));
        return;
    }

    /**
     *
     * @param e element to add a DSML search filter component (filter or a
     * AND, OR or NOT )
     * @param itr
     */
    private void writeFilter(Element e, Iterator itr)
    {
        int op=-1;
        Element newElement = null;
        while (itr.hasNext()){
            Object filterpart = itr.next();
            if (filterpart instanceof Integer) {
                op = ((Integer)filterpart).intValue();
                switch (op){
                    case LDAPSearchRequest.AND:
                        newElement = doc.createElement("and");
                        break;

                    case LDAPSearchRequest.OR:
                        newElement = doc.createElement("or");
                        break;

                    case LDAPSearchRequest.NOT:
                        newElement = doc.createElement("not");
                        break;
                        
                    case LDAPSearchRequest.EQUALITY_MATCH:
                        newElement = doc.createElement("equalityMatch");
                        writeMatching( newElement, itr);
                        break;

                    case LDAPSearchRequest.GREATER_OR_EQUAL:
                        newElement = doc.createElement("greaterOrEqual");
                        writeMatching( newElement, itr);
                        break;

                    case LDAPSearchRequest.LESS_OR_EQUAL:
                        newElement = doc.createElement("lessOrEqual");
                        writeMatching( newElement, itr);
                        break;

                    case LDAPSearchRequest.APPROX_MATCH:
                        newElement = doc.createElement("approxMatch");
                        writeMatching( newElement, itr);
                        break;

                    case LDAPSearchRequest.PRESENT:
                        newElement = doc.createElement("present");
                        newElement.setAttribute("name", (String)itr.next());
                        break;

                    case LDAPSearchRequest.EXTENSIBLE_MATCH:{
                        newElement = doc.createElement("extensibleMatch");
                        newElement.setAttribute("matchingRule",
                                (String)itr.next());
                        newElement.setAttribute("name",
                                (String)itr.next());

                        Element value = doc.createElement("value");
                        value.appendChild(doc.createTextNode((String)itr.next()));
                        newElement.appendChild(value);

                        //TODO DN matching
                        break;
                    }

                    case LDAPSearchRequest.SUBSTRINGS:{
                        newElement = doc.createElement("substrings");
                        newElement.setAttribute("name", (String)itr.next());

                        //loop through all substrings
                        while (itr.hasNext()){
                            op = ((Integer)itr.next()).intValue();
                            Element nextSubString = null;
                            switch(op){
                                case LDAPSearchRequest.INITIAL:
                                    nextSubString = doc.createElement("initial");
                                    break;
                                case LDAPSearchRequest.ANY:
                                    nextSubString = doc.createElement("any");
                                    break;
                                case LDAPSearchRequest.FINAL:
                                    nextSubString = doc.createElement("final");
                                    break;
                            }
                            String value = (String)itr.next();
                            nextSubString.appendChild(
                                    doc.createTextNode(value));
                            newElement.appendChild(nextSubString);
                        }
                        break;
                    }
                }
            } else if (filterpart instanceof Iterator){
                //This case will occur after AND, OR and NOT
                writeFilter(newElement, (Iterator)filterpart);
            }
        }
        e.appendChild(newElement);
        return;
    }

    private String byte2String(byte[] value)
    {
        String text = null;
        if (Base64.isValidUTF8(value, false)){
            try {
                text = new String(value, "UTF-8");
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException(
                        "UTF-8 not supported by JVM" + uee);
            }
        } else {
            text = Base64.encode(value);
        }
        return text;
    }

    /**
     * Writes the specified LDAPResponse into the specified element.
     * <p>Possible information written to the element is a Result code with a
     * description, a server response, and a matched DN.  Controls and referrals
     * should also be written - and will be in the future.<p>
     * @param e  Element to insert response info into.
     * @param response  Response message to write.
     */
    private void writeResult(Element e, LDAPResponse response)
    {

        /* controls: */
        LDAPControl controls[] = response.getControls();
        if (controls != null){
            writeControls(e, controls);
        }

        /* Referral */
        String urls[] = response.getReferrals();
        if (urls != null){
            for (int i=0; i<urls.length; i++){
                Element referral = doc.createElement("referral");
                Text text = doc.createTextNode(urls[i]);
                referral.appendChild(text);
                e.appendChild(referral);
            }
        }

        /* result code: */
        Element resultCode = doc.createElement("resultCode");
        int result = response.getResultCode();
        resultCode.setAttribute("code",  result + "");
        resultCode.setAttribute("descr", LDAPException.resultCodeToString(result));

        e.appendChild(resultCode);
        /* Server Message: */
        String temp = response.getErrorMessage();
        if (temp != null && temp.length() > 0){
            Element err = doc.createElement("errorMessage");
            Text errorMessage = doc.createTextNode(temp);
            err.appendChild(errorMessage);
            e.appendChild(err);
        }

        /* MatchedDN */
        temp = response.getMatchedDN();
        if (temp != null && temp.length() > 0){
            e.setAttribute("matchedDN", temp);
        }

    }

    private void writeControls(Element e, LDAPControl[] controls)
    {
        for (int i=0; i< controls.length; i++){
            Element el = doc.createElement("control");
            el.setAttribute("NumericOID", controls[i].getID());
            el.setAttribute("criticality", ""+controls[i].isCritical());

            byte byteValue[] = controls[i].getValue();
            if (byteValue!= null){
                Element value = doc.createElement("controlValue");
                value.setAttribute("xsi:type", "base64Binary");
                Text text = doc.createTextNode( Base64.encode(byteValue));
                value.appendChild(text);
                el.appendChild(value);
            }
            e.appendChild( el );
        }
    }

    /**
     * Any Exception can be written in DSML with this method, via the
     * <errorResponse> tag. In general LDAPExceptions should be written to the
     * errorResponse tag and other exception in a SOAP Fault.
     * @param e  LDAPException to be written in DSML.
     */
    public void writeError(Exception e) throws IOException
    {
        //check if we are in a response, if not set the state and write DSML tag

        Element error = doc.createElement("errorResponse");

        if (e instanceof LDAPException){
            switch (((LDAPException)e).getResultCode()){
                case LDAPException.DECODING_ERROR:
                    error.setAttribute("type", "malformedRequest");
                    break;
                case LDAPException.LOCAL_ERROR:
                    error.setAttribute("type", "gatewayInternalError");
                    break;
                case LDAPException.INVALID_CREDENTIALS:
                    error.setAttribute("type", "authenticationFailed");
                    break;
                default:
                    error.setAttribute("type", "other");
            }
        } else {
            error.setAttribute("type", "other");
        }
        Element message = doc.createElement("message");
        Text messageValue = doc.createTextNode(e.toString());
        message.appendChild(messageValue);
        error.appendChild(messageValue);
        root.appendChild(error);
        return;
    }

    /**
     * Tests the current state with a new message that is either a response or
     * request.
     * <p>If the state is NEW_BATCH, check_state will create the
     * appropriate batch element and set it as root.  If the state is
     * SEARCH_RESPONSE then the new message is verified to be a search result,
     * search response or search reference.<p>
     *
     * @param message Message to be written
     * @throws LDAPLocalException
     */
    private void checkState(LDAPMessage message)
            throws LDAPLocalException
    {
        boolean isResponse = !message.isRequest();
        if (state == NEW_BATCH) {
            if (isResponse) {
                root = doc.createElement("batchResponse");
                root.setAttribute("xmlns", "urn:oasis:names:tc:DSML:2:0:core");
                root.setAttribute("xmlns:xsd","http://www.w3.org/2001/XMLSchema");
                root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
                state = RESPONSE_BATCH;
            }
            else{
                root = doc.createElement("batchRequest");
                root.setAttribute("xmlns", "urn:oasis:names:tc:DSML:2:0:core");
                root.setAttribute("xmlns:xsd","http://www.w3.org/2001/XMLSchema");
                root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
                state = REQUEST_BATCH;
            }
        }

        if (state != SEARCH_RESPONSE &&
            (message.getType()==LDAPMessage.SEARCH_RESPONSE ||
             message.getType()==LDAPMessage.SEARCH_RESULT ||
             message.getType()==LDAPMessage.SEARCH_RESULT_REFERENCE))
        {
            searchNode = doc.createElement("searchResponse");
            searchNode.setAttribute("requestID", ""+ findRequestID(message));
            root.appendChild(searchNode);
            state = SEARCH_RESPONSE;
        }

        else if ((state == REQUEST_BATCH) && (isResponse)) {
            throw new LDAPLocalException(
                "Attempted insertion of a response message in a request batch",
                LDAPException.ENCODING_ERROR);
        } else if ((state == RESPONSE_BATCH || state == SEARCH_RESPONSE)
                        && (!isResponse))
        {
            throw new LDAPLocalException(
                "Attempted insertion of a request message in a response batch",
                LDAPException.ENCODING_ERROR);
        } else if ( state == SEARCH_RESPONSE &&
                (message.getType()!=LDAPMessage.SEARCH_RESPONSE &&
                 message.getType()!=LDAPMessage.SEARCH_RESULT &&
                 message.getType()!=LDAPMessage.SEARCH_RESULT_REFERENCE))
        {
            throw new LDAPLocalException(
             "Attempted insertion of a non-search result into a searchResponse",
             LDAPException.ENCODING_ERROR);
        }
        return;
    }

    /**
     * Tests the current state with a new message that is either a response or
     * request.
     * <p>If the state is NEW_BATCH, check_state will create the
     * appropriate batch element and set it as root.  If the state is
     * SEARCH_RESPONSE then the new message is verified to be a search result,
     * search response or search reference.<p>
     *
     * @param entry Message to be written
     * @throws LDAPLocalException
     */
    private void checkState(LDAPEntry entry)
            throws LDAPLocalException
    {
        boolean isResponse = true;
        if (state == NEW_BATCH) {
            root = doc.createElement("batchResponse");
            root.setAttribute("xmlns", "urn:oasis:names:tc:DSML:2:0:core");
            root.setAttribute("xmlns:xsd","http://www.w3.org/2001/XMLSchema");
            root.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            state = RESPONSE_BATCH;
        }

        if (state != SEARCH_RESPONSE) {
            searchNode = doc.createElement("searchResponse");
            root.appendChild(searchNode);
            state = SEARCH_RESPONSE;
        }
        else if ((state == REQUEST_BATCH) && (isResponse)) {
            throw new LDAPLocalException(
                "Attempted insertion of a response message in a request batch",
                LDAPException.ENCODING_ERROR);
        }
        return;
    }

    static String findRequestID(LDAPMessage message)
    {
        String tag = message.getTag();
        if (tag == null){
            tag = message.getMessageID() + "";
        }
        return tag;
    }

    /**
     * Retrieves the batchRequest or batchResponse element populated with this
     * writer.
     * @return A batchRequest or batchResponse element.
     */
    public Element getRootElement()
    {
        return root;
    }

    public void finish() throws IOException
    {
        doc.appendChild(root);
        return;
    }
}
