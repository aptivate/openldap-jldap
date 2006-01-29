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
import com.novell.ldap.rfc2251.RfcFilter;

import java.io.*;
import java.util.Iterator;

/**
 * Writes LDAPMessages into a Writer or outputStream as DSML batch requests and
 * batch responses.
 *
 * @see DSMLReader
 * @see DOMWriter
 * @see LDAPMessage
 */
public class DSMLWriter implements LDAPWriter {

    private static boolean fswitch = true;
    private Writer out = null;
    private int state = NEW_BATCH;
    private static final int NEW_BATCH = 0;
    private static final int REQUEST_BATCH = 1;
    private static final int RESPONSE_BATCH = 2;
    private static final int SEARCH_TAG = 3;
    private boolean indent = false;
    private String tabString = "    ";
    private String version = "2.0";
    private boolean useSOAP;
    private boolean resumeOnError;
    
    private static final String BATCH_REQUEST_START =
            "<batchRequest xmlns=\"urn:oasis:names:tc:DSML:2:0:core\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" onError=\"";
    private static final String BATCH_RESPONSE_START =
            "<batchResponse xmlns=\"urn:oasis:names:tc:DSML:2:0:core\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";

    
    public void setResumeOnError(boolean resumeOnError) {
    	this.resumeOnError = resumeOnError;
    }
    
    /**
     * Initializes this writer by opening the specified file to write DSML into.
     * @param file  File to write DSML
     * @throws FileNotFoundException occurs when the specified file could not
     * be opened or is not found.
     */
    public DSMLWriter(String file) throws FileNotFoundException
    {
        this( new FileOutputStream(file, true));
        return;
    }

    /**
     * Initializes this writer with the specified outputstream to write DSML
     * into.
     *
     * Note that the output characters will be UTF-8 encoded.
     * @param stream  Output stream to write DSML
     */
    public DSMLWriter(OutputStream stream)
    {
        try {
            out = new OutputStreamWriter(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding is not supported by JVM"+
                    e.toString());
        }
    }

    /**
     * Initializes this writer with the specified writer to write DSML into.
     * @param writer Writer to write DSML
     */
    public DSMLWriter(Writer writer)
    {
        out = writer;
        return;
    }

    /**
     * Any Exception can be written in DSML with this method, via the
     * <errorResponse> tag. In general LDAPExceptions should be written to the
     * errorResponse tag and other exception in a SOAP Fault.
     * @param e  LDAPException to be written in DSML.
     */
    public void writeError(Exception e)
            throws IOException
    {
        //check if we are in a response, if not set the state and write DSML tag
        try{
            checkState(true);
        } catch (LDAPLocalException lle){
            throw new IOException(lle.toString());
        }
        newLine(1);
        out.write("<errorResponse type=\"");
        if (e instanceof LDAPException){
            switch (((LDAPException)e).getResultCode()){
                case LDAPException.DECODING_ERROR:
                case LDAPException.PROTOCOL_ERROR:
                    out.write("malformedRequest\">");
                    break;
                case LDAPException.LOCAL_ERROR:
                    out.write("gatewayInternalError\">");
                    break;
                case LDAPException.INVALID_CREDENTIALS:
                    out.write("authenticationFailed\">");
                    break;
                default:
                    out.write("other\">");
            }
        } else {
            out.write("other\">");
        }
        newLine(2);
        out.write("<message>");
        newLine(3);
        if (e instanceof LDAPException) {
        	LDAPException le = (LDAPException) e;
        	String msg = Integer.toString(le.getResultCode()) + ":" + le.getMessage();
        	out.write(makeXMLSafe(msg));
        } else {
        	out.write(makeXMLSafe(e.getMessage()));
        }
        newLine(2);
        out.write("</message>");
        newLine(1);
        out.write("</errorResponse>");
        return;
    }

    /**
     * Writes closing tags for searchResponse, batchRequests, and batchResponse
     * depending on the current state.
     */
    public void finish() throws IOException
    {
        newLine(0);
        switch(state){
            case REQUEST_BATCH:
                out.write("</batchRequest>");
                break;
            case RESPONSE_BATCH:
                out.write("</batchResponse>");
                break;
            case SEARCH_TAG:
                if (indent)
                    out.write(tabString);
                out.write("</searchResponse>");
                newLine(0);
                out.write("</batchResponse>");
                break;
            case NEW_BATCH:
            	//if no message is send, we assume 
            	//batch response.
            	out.write("<batchResponse>");
            	newLine(0);
            	out.write("</batchResponse>");
            	break;                
        }
        newLine(0);
        out.flush();
        return;
    }
   
    /**
     * Writes the specified strings as XML comments.
     * @param lines Comments to be written
     */
    public void writeComments(String lines) throws IOException
    {
        newLine(1);
        out.write("<!-- ");
        out.write(lines);
        out.write(" -->");
        return;
    }

    /**
     * Writes an LDAPMessage as DSML.
     * @param messageToWrite Message to be written as DSML
     *
     * @throws LDAPLocalException Occurs when a message is written out of
     * sequence, i.e. a response is written into a batchRequest.
     */
    public void writeMessage(LDAPMessage messageToWrite)
                throws IOException, LDAPLocalException
    {
        //check state and write batch tags if neccessary
        if ((messageToWrite instanceof LDAPResponse) ||
            (messageToWrite instanceof LDAPSearchResult) ||
            (messageToWrite instanceof LDAPSearchResultReference)){
            checkState(true);
        } else {//must be a request
            checkState(false);
        }

        //write the message tags
        switch (messageToWrite.getType()) {
            //requests:
            case LDAPMessage.BIND_REQUEST:
            case LDAPMessage.UNBIND_REQUEST:
            case LDAPMessage.BIND_RESPONSE:
//            case LDAPMessage.COMPARE_REQUEST:
            case LDAPMessage.ABANDON_REQUEST:
//            case LDAPMessage.EXTENDED_REQUEST:
                throw new java.lang.UnsupportedOperationException(
                        "Writing of this message is not supported");

            //Responses:
            case LDAPMessage.SEARCH_REQUEST:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("searchRequest", messageToWrite);
                    state = REQUEST_BATCH;
                }
                writeSearchRequest((LDAPSearchRequest) messageToWrite);
                break;

            case LDAPMessage.EXTENDED_REQUEST:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("extendedRequest", messageToWrite);
                    state = REQUEST_BATCH;
                }
                writeExtendedRequest((LDAPExtendedRequest) messageToWrite);
                break;

            case LDAPMessage.COMPARE_REQUEST:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("compareRequest", messageToWrite);
                    state = REQUEST_BATCH;
                }
                writeCompareRequest((LDAPCompareRequest) messageToWrite);
                break;

            case LDAPMessage.ADD_REQUEST:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("addRequest", messageToWrite);
                    state = REQUEST_BATCH;
                }
                writeAddRequest((LDAPAddRequest) messageToWrite);
                break;

            case LDAPMessage.MODIFY_REQUEST:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("modifyRequest", messageToWrite);
                    state = REQUEST_BATCH;
                }
                writeModifyRequest((LDAPModifyRequest) messageToWrite);
                break;

            case LDAPMessage.MODIFY_RDN_REQUEST:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("modDNRequest", messageToWrite);
                    state = REQUEST_BATCH;
                }
                writeModifyDNRequest((LDAPModifyDNRequest) messageToWrite);
                break;
                
            case LDAPMessage.DEL_REQUEST:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("delRequest", messageToWrite);
                    state = REQUEST_BATCH;
                }
                writeDeleteRequest((LDAPDeleteRequest) messageToWrite);
                break;

                        
            case LDAPMessage.SEARCH_RESPONSE:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("searchResponse", messageToWrite);
                    state = SEARCH_TAG;
                }
                writeSearchResponse((LDAPSearchResult) messageToWrite);
                break;
            case LDAPMessage.SEARCH_RESULT_REFERENCE:
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("searchResponse", messageToWrite);
                    state = SEARCH_TAG;
                }
                writeSearchResultReference(
                        (LDAPSearchResultReference) messageToWrite);
                break;
            case LDAPMessage.SEARCH_RESULT:  //final search done message (or standard referral)
                if (state != SEARCH_TAG){
                    newLine(1);
                    writeTagWithID("searchResponse", messageToWrite);
                }
                newLine(2);
                writeTagWithID("searchResultDone", messageToWrite);
                    writeResult((LDAPResponse)messageToWrite, 3);
                newLine(2);
                out.write("</searchResultDone>");

                newLine(1);
                out.write("</searchResponse>");
                state = RESPONSE_BATCH;
                break;

            case LDAPMessage.MODIFY_RESPONSE:
                newLine(1);
                writeTagWithID("modifyResponse", messageToWrite);
                writeResult((LDAPResponse)messageToWrite, 2);
                newLine(1);
                out.write("</modifyResponse>");
                break;

            case LDAPMessage.ADD_RESPONSE:
                newLine(1);
                writeTagWithID("addResponse", messageToWrite);
                writeResult((LDAPResponse)messageToWrite, 2);
                newLine(1);
                out.write("</addResponse>");
                break;

            case LDAPMessage.DEL_RESPONSE:
                newLine(1);
                writeTagWithID("delResponse", messageToWrite);
                writeResult((LDAPResponse)messageToWrite, 2);
                newLine(1);
                out.write("</delResponse>");
                break;

            case LDAPMessage.MODIFY_RDN_RESPONSE:
                newLine(1);
                writeTagWithID("modDNResponse", messageToWrite);
                writeResult((LDAPResponse)messageToWrite, 2);
                newLine(1);
                out.write("</modDNResponse>");
                break;
            case LDAPMessage.COMPARE_RESPONSE:
                newLine(1);
                writeTagWithID("compareResponse", messageToWrite);
                writeResult((LDAPResponse)messageToWrite, 2);
                newLine(1);
                out.write("</compareResponse>");
                break;
            case LDAPMessage.EXTENDED_RESPONSE:
                newLine(1);
                LDAPExtendedResponse xResp =
                        (LDAPExtendedResponse) messageToWrite;
                writeTagWithID("extendedResponse", messageToWrite);
                writeResult((LDAPResponse)messageToWrite, 2);
                newLine(2);

                out.write("<responseName>");
                out.write(xResp.getID());
                out.write("</responseName>");

                byte[] value = xResp.getValue();
                if (value != null){
                    /*  temporary
                    byte[] valueWithoutTagAndLength = new byte[value.length - 2];
                    System.arraycopy(value, 2,
                            valueWithoutTagAndLength, 0,
                            valueWithoutTagAndLength.length);
                            */
                    newLine(2);
                    /*
                    if (Base64.isValidUTF8(valueWithoutTagAndLength, false)){
                        out.write("<response>");
                        out.write(new String(valueWithoutTagAndLength, "UTF8"));
                    } else {*/
                    out.write("<response xsi:type=\"xsd:base64Binary\">");
                    out.write(Base64.encode(value));
                    //}
                    out.write("</response>");
                }
                newLine(1);
                out.write("</extendedResponse>");
                break;
        }
        return;
    }

    /**
     * Write an LDAP entry into LDIF file as LDAPContent data.
     * An LDAPEntry is written as a SearchResultEntry record.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param entry LDAPEntry object
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPEntry
     */
    public void writeEntry( LDAPEntry entry)
            throws IOException, LDAPLocalException
    {
        checkState(true);
        writeEntry( entry, null, null);
        return;
    }

    /**
     * Write an LDAP entry into LDIF file as LDAPContent data.
     * An LDAPEntry is written as a SearchResultEntry record.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param entry LDAPEntry object
     *
     * @param controls Controls that were returned with this entry
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPEntry
     */
    public void writeEntry( LDAPEntry entry, LDAPControl[] controls)
            throws IOException, LDAPLocalException
    {
        checkState(true);
        writeEntry( entry, controls, null);
        return;
    }

    /**
     * Write an LDAP entry into LDIF file as LDAPContent data.
     * An LDAPEntry is written as a SearchResultEntry record.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param entry LDAPEntry object
     *
     * @param controls Controls that were returned with this entry
     *
     * @param requestID the String that associates this response with the request
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPEntry
     */
    public void writeEntry( LDAPEntry entry,
                            LDAPControl[] controls,
                            String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(true);
        newLine(2);
        out.write("<searchResultEntry dn=\"");
        out.write(this.makeAttributeSafe(entry.getDN()));
        if( requestID != null) {
            out.write("\" requestID=\"" + requestID);
        }
        out.write("\">");
        LDAPAttributeSet set = entry.getAttributeSet();
        Iterator i = set.iterator();
        while (i.hasNext()){
            writeAttribute( (LDAPAttribute) i.next());
        }
        if( (controls !=null) && (controls.length != 0)) {
            writeControls(controls, 3);
        }
        newLine(2);
        out.write("</searchResultEntry>");

        return;
    }

    /**
     * Convert a UTF8 encoded string, or binary data, into a String encoded for
     * a string filter.
     */
    private static String byteString(byte[] value) {
        String toReturn = null;
        if (com.novell.ldap.util.Base64.isValidUTF8(value, true)) {
            try {
                toReturn = new String(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "Default JVM does not support UTF-8 encoding" + e);
            }
        } else {
            StringBuffer binary = new StringBuffer();
            for (int i=0; i<value.length; i++){
                //TODO repair binary output
                //Every octet needs to be escaped
                if (value[i] >=0) {
                    //one character hex string
                    binary.append("\\0");
                    binary.append(Integer.toHexString(value[i]));
                } else {
                    //negative (eight character) hex string
                    binary.append("\\"+
                            Integer.toHexString(value[i]).substring(6));
                }
            }
            toReturn = binary.toString();
        }
        return toReturn;
    }

    /*
     * Writes  LDAP Search Filter as a XML document 
     *
     * @param itr Iterator object representing RfcFilter object
     *
     * @see com.novell.ldap.rfc2251.RfcFilter
     */
	private void writeFilter ( Iterator itr )
                throws IOException, LDAPLocalException	
	{
        int op=-1;
				
        while (itr.hasNext()){
            Object filterpart = itr.next();
            if (filterpart instanceof Integer){
                op = ((Integer)filterpart).intValue();
                switch (op){
                    case RfcFilter.AND:
                        newLine(3);
                        out.write("<and>");
                        break;
                    case RfcFilter.OR:
                        newLine(3);
                        out.write("<or>");
                        break;
                    case RfcFilter.NOT:
                        newLine(3);
                        out.write("<not>");
                        break;
                    case RfcFilter.EQUALITY_MATCH:{
                         newLine(4);
                         out.write("<equalityMatch name=\"");
                         out.write((String)itr.next());
                         out.write("\">");
                         byte[] value = (byte[])itr.next();
                         newLine(5);                         
                         out.write("<value>");
                         out.write(byteString(value));
                         out.write("</value>");
                         newLine(4);                         
                         out.write("</equalityMatch>");                         
                         break;
                    }
                    case RfcFilter.GREATER_OR_EQUAL:{                    
                         newLine(4);
                         out.write("<greaterOrEqual name=\"");
                         out.write((String)itr.next());
                         out.write("\">");
                         byte[] value = (byte[])itr.next();
                         newLine(5);                         
                         out.write("<value>");
                         out.write(byteString(value));
                         out.write("</value>");
                         newLine(4);                         
                         out.write("</greaterOrEqual>");                         
                         break;
                    }
                    
                    case RfcFilter.LESS_OR_EQUAL:{                    
                         newLine(4);
                         out.write("<lessOrEqual name=\"");
                         out.write((String)itr.next());
                         out.write("\">");
                         byte[] value = (byte[])itr.next();
                         newLine(5);                         
                         out.write("<value>");
                         out.write(byteString(value));
                         out.write("</value>");
                         newLine(4);                         
                         out.write("</lessOrEqual>");                         
                         break;
                    }
                    
                    case RfcFilter.PRESENT:{                    
                         newLine(4);
                         out.write("<present name=\"");
                         out.write((String)itr.next());
                         out.write("\">");
                         newLine(4);                         
                         out.write("</present>");                         
                         break;
                    }
                    
                    case RfcFilter.APPROX_MATCH:{                    
                         newLine(4);
                         out.write("<approxMatch name=\"");
                         out.write((String)itr.next());
                         out.write("\">");
                         byte[] value = (byte[])itr.next();
                         newLine(5);                         
                         out.write("<value>");
                         out.write(byteString(value));
                         out.write("</value>");
                         newLine(4);                         
                         out.write("</approxMatch>");                         
                         break;
                    }
                    
                    case RfcFilter.SUBSTRINGS:{                    
                         newLine(4);
                         out.write("<substrings name=\"");
                         out.write((String)itr.next());
                         out.write("\">");
                         boolean noStarLast = false;                         
                         while (itr.hasNext()){
                            op = ((Integer)itr.next()).intValue();
                            switch(op){
                                case RfcFilter.INITIAL:
                                    newLine(5);                         
                                    out.write("<initial>");
                                    out.write((String)itr.next());
                                    out.write("</initial>");
                                    noStarLast = false;
                                    break;
                                case RfcFilter.ANY:
                                    newLine(5);                         
                                    out.write("<any>");
                                    out.write((String)itr.next());
                                    out.write("</any>");
                                    noStarLast = false;
                                    break;
                                case RfcFilter.FINAL:
                                    newLine(5);                         
                                    out.write("<final>");
                                    out.write((String)itr.next());
                                    out.write("</final>");
                                    break;
                            }
                        }
                         newLine(4);
                         out.write("</substrings>");
                         break;
                    }
                    
                    
                }
            } else if( filterpart instanceof Iterator ) {
                writeFilter( (Iterator)filterpart);
                switch (op) {
                    case RfcFilter.AND:
                        if(fswitch){
                            fswitch=false;
                        }  else{
                            newLine(3);
                            out.write("</and>");
                            fswitch=true;
                        }
                        break;

                    case RfcFilter.OR:
                        if(fswitch){
                            fswitch=false;
                        }  else {
                            newLine(3);
                            out.write("</or>");
                            fswitch = true;
                        }
                        break;
                
                    case RfcFilter.NOT:
                        newLine(3);                    
                        out.write("</not>");
                        break;
                
                    default:

                }
            }
                    
        }
            

    }

    /*
     * Writes  LDAP ModifyDN Request as a XML document 
     *
     * @param request LDAPModifyDNRequest object
	 * @param controls Controls that were returned with this entry
     * @param requestID the String representing a Request ID
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPModifyDNRequest
     */
    private void writeModifyDNRequestEntry( LDAPModifyDNRequest request,
                            	   LDAPControl[] controls,
                            	   String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(false);
        
//		  added this fix for OCT' 04 NDK
		  if( (controls !=null) && (controls.length != 0)) {
			  writeControls(controls, 3);
		  }
        newLine(1);
        out.write("</modDNRequest>");
        return;
    }
        
    /*
     * Writes  LDAP Modify Request as a XML document 
     *
     * @param request LDAPModifyRequest object
	 * @param controls Controls that were returned with this entry
     * @param requestID the String representing a Request ID
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPModifyRequest
     */
    private void writeModifyRequestEntry( LDAPModifyRequest request,
                            	   LDAPControl[] controls,
                            	   String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(false);
        LDAPModification[] modList=request.getModifications();
        for(int i=0; i< modList.length; i++){
            LDAPAttribute attr=modList[i].getAttribute();
            newLine(2);
            out.write("<modification name=\"");
            out.write(attr.getName());
            out.write("\" operation=\"");
            switch(modList[i].getOp())
            {
                case LDAPModification.ADD:
                    out.write("add");
                    break;

                case LDAPModification.DELETE:
                    out.write("delete");
                    break;

                case LDAPModification.REPLACE:
                    out.write("replace");
                    break;
            }
                    
            out.write("\">");
        
            String values[] = attr.getStringValueArray();
            byte bytevalues[][] = attr.getByteValueArray();
            for(int j=0; j<values.length; j++){
                newLine(3);
                if (Base64.isValidUTF8(bytevalues[j], false)){
                    out.write("<value>");
                	String xmlvalue = makeAttributeSafe(values[j]);
                	out.write(xmlvalue);
                    out.write("</value>");
                } else {
                    out.write("<value xsi:type=\"xsd:base64Binary\">");
                    out.write(Base64.encode(bytevalues[j]));
                    out.write("</value>");
                }

            }
            newLine(2);
            out.write("</modification>");
        }
        
//		added this fix for OCT' 04 NDK
		  if( (controls !=null) && (controls.length != 0)) {
			  writeControls(controls, 3);
		  }
        
        newLine(1);
        out.write("</modifyRequest>");
        return;
    }

    /*
     * Writes  LDAP Delete Request as a XML document 
     *
     * @param request LDAPDeleteRequest object
	 * @param controls Controls that were returned with this entry
     * @param requestID the String representing a Request ID
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPDeleteRequest
     */
    private void writeDeleteRequestEntry( LDAPDeleteRequest request,
                            	   LDAPControl[] controls,
                            	   String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(false);
//		added this fix for OCT' 04 NDK
		  if( (controls !=null) && (controls.length != 0)) {
			  writeControls(controls, 3);
		  }
        
        newLine(1);
        out.write("</delRequest>");
        return;
    }    

    /*
     * Writes  LDAP Extended Request as a XML document 
     *
     * @param request LDAPExtendedRequest object
	 * @param controls Controls that were returned with this entry
     * @param requestID the String representing a Request ID
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPExtendedRequest
     */
    private void writeExtendedRequestEntry( LDAPExtendedRequest request,
                            	   LDAPControl[] controls,
                            	   String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(false);
        
        newLine(2);
        out.write("<requestName>");
        out.write(request.getExtendedOperation().getID());
        out.write("</requestName>");

        byte[] vals=request.getExtendedOperation().getValue();
        if( vals != null)
        {
            newLine(2);
            out.write("<requestValue xsi:type=\"xsd:base64Binary\">");
            out.write(Base64.encode(vals));
            out.write("</requestValue>");
        }
        
		//		  added this fix for OCT' 04 NDK
		if( (controls !=null) && (controls.length != 0)) {
			writeControls(controls, 3);
		}
				
        newLine(1);
        out.write("</extendedRequest>");
        return;
    }    
    
    /*
     * Writes  LDAP Compare Request as a XML document 
     *
     * @param request LDAPCompareRequest object
	 * @param controls Controls that were returned with this entry
     * @param requestID the String representing a Request ID
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPCompareRequest
     */
    private void writeCompareRequestEntry( LDAPCompareRequest request,
                            	   LDAPControl[] controls,
                            	   String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(false);
        newLine(2);
        out.write("<assertion name=\"" + request.getAttributeDescription());
        out.write("\">");
        newLine(3);
        out.write("<value>");
        byte[] vals=request.getAssertionValue();
        if (Base64.isLDIFSafe(vals)) 
            out.write(new String(vals,"UTF-8"));
        else
            out.write(Base64.encode(vals));
        out.write("</value>");
        newLine(2);
        out.write("</assertion>");
        
//		added this fix for OCT' 04 NDK
		  if( (controls !=null) && (controls.length != 0)) {
			  writeControls(controls, 3);
		  }
        
        newLine(1);
        out.write("</compareRequest>");
    }
    /*
     * Writes  LDAP Add Request as a XML document 
     *
     * @param request LDAPAddRequest object
	 * @param controls Controls that were returned with this entry
     * @param requestID the String representing a Request ID
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPAddRequest
     */
    private void writeAddRequestEntry( LDAPAddRequest request,
                            	   LDAPControl[] controls,
                            	   String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(false);
        LDAPEntry entry=request.getEntry();
        
        LDAPAttributeSet set = entry.getAttributeSet();
        Iterator i = set.iterator();
        while (i.hasNext()){
            writeAttribute( (LDAPAttribute) i.next());
        }
     
        if( (controls !=null) && (controls.length != 0)) {
            writeControls(controls, 3);
        }
        newLine(1);
        out.write("</addRequest>");
        return;        
    }
    
    /*
     * Writes  LDAP Search Request as a XML document 
     *
     * @param request LDAPSearchRequest object
	 * @param controls Controls that were returned with this entry
     * @param requestID the String representing a Request ID
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPSearchRequest
     */
    private void writeSearchRequestEntry( LDAPSearchRequest request,
                            	   LDAPControl[] controls,
                            	   String requestID)
            throws IOException, LDAPLocalException
    {
        checkState(false);
        
        newLine(2);
 		out.write("<attributes>");
        String[] attrs = request.getAttributes();
        for(int i=0; i< attrs.length; i++){
		newLine(3);        
        out.write("<attribute name=\"");
        out.write(attrs[i]);
        out.write("\"/>");
        }
		newLine(2);                
        out.write("</attributes>");

		newLine(2);                
        out.write("<filter>");
        Iterator itr=request.getSearchFilter();
        writeFilter(itr);
        newLine(2);
		out.write("</filter>");                
		
        if( (controls !=null) && (controls.length != 0)) {
            writeControls(controls, 3);
        }
        newLine(1);
        out.write("</searchRequest>");

        return;
    }

    /**
     * Writes an XML tag with it's requestID and possibly a matchedDN.
     * @param tag XML tag name to be written
     * @param message LDAPMessage containing a requestID and possibly matchedDN
     * values to be written with the tag
     */
    private void writeTagWithID(String tag, LDAPMessage message)
            throws IOException
    {
        out.write("<" + tag + " requestID=\""+ DOMWriter.findRequestID(message) +"\"");
        if (message instanceof LDAPResponse){
            String matchedDN = ((LDAPResponse) message).getMatchedDN();
            if (matchedDN != null && !matchedDN.equals("")){
                out.write(" matchedDN=\"" + this.makeAttributeSafe(matchedDN) + "\"");
            }
        }

        if( message instanceof LDAPCompareRequest) {
            String dn = ((LDAPCompareRequest) message).getDN();
            out.write(" dn=\"" + this.makeAttributeSafe(dn) + "\"");
        }

        if( message instanceof LDAPExtendedRequest) {
    
        }
        
        if( message instanceof LDAPDeleteRequest) {
            String dn = ((LDAPDeleteRequest) message).getDN();
			out.write(" dn=\"" + this.makeAttributeSafe(dn) + "\"");                        
        }
        
        if( message instanceof LDAPAddRequest) {
            String dn = ((LDAPAddRequest) message).getEntry().getDN();
			out.write(" dn=\"" + this.makeAttributeSafe(dn) + "\"");                        
        }
        
        if( message instanceof LDAPModifyRequest) {
            String dn = ((LDAPModifyRequest) message).getDN();
			out.write(" dn=\"" + this.makeAttributeSafe(dn) + "\"");                        
        }

        if( message instanceof LDAPModifyDNRequest) {
            String dn = ((LDAPModifyDNRequest) message).getDN();
			out.write(" dn=\"" + this.makeAttributeSafe(dn) + "\"");
			out.write(" newrdn=\"");
			out.write(((LDAPModifyDNRequest) message).getNewRDN() + "\" deleteoldrdn=\"");
			out.write(((LDAPModifyDNRequest) message).getDeleteOldRDN() + "\" newSuperior=\"");
			out.write(((LDAPModifyDNRequest) message).getParentDN() + "\"");
        }
        
        if( message instanceof LDAPSearchRequest) {
            String searchScope="";
            String searchDeRef="";
                    
            String dn = ((LDAPSearchRequest) message).getDN();
            int scope = ((LDAPSearchRequest) message).getScope();
            int deref = ((LDAPSearchRequest) message).getDereference();
            
            int slimit =  ((LDAPSearchRequest) message).getMaxResults();
            int tlimit =  ((LDAPSearchRequest) message).getServerTimeLimit();
            boolean isTypesonly =  ((LDAPSearchRequest) message).isTypesOnly();
            
            
            
			out.write(" dn=\"" + this.makeAttributeSafe(dn) + "\"");            
            switch(scope)   {
                case LDAPConnection.SCOPE_BASE:
                    searchScope = "baseObject";
                    break;
                    
                case LDAPConnection.SCOPE_ONE:
                    searchScope = "singleLevel";
                    break;
                
                default:
                    searchScope = "wholeSubtree";
                    break;
            }
			out.write(" scope=\"" + searchScope + "\"");
			
            switch(deref)   {
                case LDAPSearchConstraints.DEREF_SEARCHING:
                    searchDeRef = "derefInSearching";
                    break;
                    
                case LDAPSearchConstraints.DEREF_FINDING:
                    searchDeRef = "derefFindingBaseObj";
                    break;

                case LDAPSearchConstraints.DEREF_ALWAYS:
                    searchDeRef = "derefAlways";
                    break;
                
                default:
                    searchDeRef = "neverDerefAliases";
                    break;
            }
            out.write(" derefAliases=\"" + searchDeRef + "\"");
			out.write(" sizeLimit=\"" + slimit + "\"");
			out.write(" timeLimit=\"" + tlimit + "\"");
			out.write(" typesOnly=\"" + isTypesonly + "\"");
        }
        out.write(">");
        return;
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
        return true;
    }

    /**
     * Writes all information associated with a result such as controls,
     * referrals, result code with description, and any server message.
     * @param result result to be written
     * @param indent number of indentation this result should use for writing
     */
    private void writeResult(LDAPResponse result, int indent) throws IOException
    {
        /* controls: */
        LDAPControl[] controls = result.getControls();
        if (controls != null){
            writeControls(controls, indent);
        }

        /* referal: */
        String referrals[] = result.getReferrals();
        if (referrals != null){
            for(int i=0; i<referrals.length; i++){
                newLine(indent);
                out.write("<referral>"+referrals[i]+"</referral>");
            }
        }

        /* result code: */
        newLine(indent);
        out.write("<resultCode code=\"");
        out.write(new Integer(result.getResultCode()).toString());
        out.write("\" descr=\"");
        out.write(LDAPException.resultCodeToString(result.getResultCode()));
        out.write( "\" />");

        /* Server Message: */
        String temp = result.getErrorMessage();
        if (temp != null && temp.length() > 0){
            newLine(indent);
            out.write("<errorMessage>");
            out.write(makeXMLSafe(temp));
            out.write("</errorMessage>");
        }
       return;
    }

    /**
     * Writes a control in DSML.
     * <p>Used by writeResult and by the searchResponse case in writeMessage.
     * @param controls Controls to be written
     * @param indent  Size of indentation for writting.
     */
    private void writeControls(LDAPControl[] controls, int indent)
            throws IOException
    {
        for(int i=0; i<controls.length; i++){
            newLine(indent);
            out.write("<control type=\"");
            out.write(controls[i].getID());
            out.write("\" criticality=\""+controls[i].isCritical()+ "\"");

            byte value[] = controls[i].getValue();
            if (value == null){
                out.write(" / >");
            } else {
                out.write(">");
                newLine(indent+1);
                out.write("<controlValue xsi:type=\"xsd:base64Binary\">");
                out.write(Base64.encode(value));
                out.write("</controlValue>");
                newLine(indent);
                out.write("</control>");
            }
        }
        return;
    }

    /**
     * Writes referrences that are returned from a search.
     * @param ref search reference
     */
    private void writeSearchResultReference(LDAPSearchResultReference ref)
            throws IOException
    {
        String[] refs = ref.getReferrals();
        newLine(2);
        writeTagWithID("searchResultReference", ref);
        //out.write("<searchResultReference>");
        for(int i=0; i< refs.length; i++){
            newLine(3);
            out.write("<ref>");
            out.write(refs[i]);
            out.write("</ref>");
        }
        newLine(2);
        out.write("</searchResultReference>");
    }

    /**
     * Writes the entries returned within search responses.
     * @param result a search result entry
     */
    private void writeSearchResponse(LDAPSearchResult result)
            throws IOException, LDAPLocalException
    {
        writeEntry( result.getEntry(),
                    result.getControls(),
                    DOMWriter.findRequestID(result));
        return;
    }

    /**
     * Writes the Search request requested within a LDAP search 
     * request
     * @param request a search request entry
     */
    private void writeSearchRequest(LDAPSearchRequest request)
            throws IOException, LDAPLocalException
    {
        writeSearchRequestEntry( request,
                           request.getControls(),
                           DOMWriter.findRequestID(request));
        return;
    }

    /**
     * Writes the Add request requested within a LDAP Add 
     * request
     * @param request a search request entry
     */
    private void writeAddRequest(LDAPAddRequest request)
            throws IOException, LDAPLocalException
    {
        writeAddRequestEntry( request,
                           request.getControls(),
                           DOMWriter.findRequestID(request));
        return;
    }

    /**
     * Writes the Compare request requested within a LDAP Compare 
     * request
     * @param request a search request entry
     */
    private void writeCompareRequest(LDAPCompareRequest request)
            throws IOException, LDAPLocalException
    {
        writeCompareRequestEntry( request,
                           request.getControls(),
                           DOMWriter.findRequestID(request));
        return;
    }

    /**
     * Writes the Modify request requested within a LDAP Modify 
     * request
     * @param request a search request entry
     */
    private void writeModifyRequest(LDAPModifyRequest request)
            throws IOException, LDAPLocalException
    {
        writeModifyRequestEntry( request,
                           request.getControls(),
                           DOMWriter.findRequestID(request));
        return;
    }


    /**
     * Writes the Modify request requested within a LDAP Modify 
     * request
     * @param request a search request entry
     */
    private void writeModifyDNRequest(LDAPModifyDNRequest request)
            throws IOException, LDAPLocalException
    {
        writeModifyDNRequestEntry( request,
                           request.getControls(),
                           DOMWriter.findRequestID(request));
        return;
    }

    /**
     * Writes the Delete request requested within a LDAP Delete 
     * request
     * @param request a search request entry
     */
    private void writeDeleteRequest(LDAPDeleteRequest request)
            throws IOException, LDAPLocalException
    {
        writeDeleteRequestEntry( request,
                           request.getControls(),
                           DOMWriter.findRequestID(request));
        return;
    }

    /**
     * Writes the Extended request requested within a LDAP Extended 
     * request
     * @param request a Extended request entry
     */
    private void writeExtendedRequest(LDAPExtendedRequest request)
            throws IOException, LDAPLocalException
    {
        writeExtendedRequestEntry( request,
                                   request.getControls(),
                                   DOMWriter.findRequestID(request));
        return;
    }

    /**
     * Used to write an attribute and its values.
     * @param attr Attribute to be written.
     */
    private void writeAttribute(LDAPAttribute attr) throws IOException
    {
        newLine(3);
        out.write("<attr name=\"");
        out.write(attr.getName());
        out.write("\">");
        String values[] = attr.getStringValueArray();
        byte bytevalues[][] = attr.getByteValueArray();
        for(int i=0; i<values.length; i++){
            newLine(4);
            
            if (Base64.isValidUTF8(bytevalues[i],true) && this.isXMLSafe(bytevalues[i])){
                out.write("<value>");
                String xmlvalue = makeAttributeSafe(values[i]);
                out.write(xmlvalue);
                out.write("</value>");
            } else {
                out.write("<value xsi:type=\"xsd:base64Binary\">");
                out.write(Base64.encode(bytevalues[i]));
                out.write("</value>");
            }

        }
        newLine(3);
        out.write("</attr>");
        return;
    }

    /**
     * Tests the current state with a new message that is either a response or
     * request.  If the state is NEW_BATCH, check_state will print an
     * appropriate batch tag.
     *
     * @param isResponse    Indicates if the message to be written is a response
     *                      or not.
     * @throws IOException
     * @throws LDAPLocalException
     */
    private void checkState(boolean isResponse)
            throws IOException, LDAPLocalException
    {
        if (state == NEW_BATCH) {
            if (isResponse) {
                out.write(BATCH_RESPONSE_START);
                state = RESPONSE_BATCH;
            }
            else{
                out.write(BATCH_REQUEST_START + (resumeOnError ? "resume" : "exit") + "\">");
                state = REQUEST_BATCH;
            }
        }
        else if ((state == REQUEST_BATCH) && (isResponse)) {
            throw new LDAPLocalException(
                "Attempted insertion of a response message in a request batch",
                LDAPException.ENCODING_ERROR);
        } else if (state == RESPONSE_BATCH && (!isResponse)) {
                throw new LDAPLocalException(
                "Attempted insertion of a request message in a response batch",
                LDAPException.ENCODING_ERROR);
        }
        return;
    }

    /**
     * Writes a new line and then the specified number of indentTabs to indent
     * the next characters to be written.
     *
     * <p>Allows the writer to 'pretty-print' XML output according to the
     * number of tabs.  The size of a tab is determined by tabString which is
     * created by the method setIndent.  No pretty-printing will occur if indent
     * is set to <tt>false</tt>.
     * @param indentTabs number of tabs to indent.
     */
    private void newLine(int indentTabs) throws IOException
    {
        if (!indent)
            return;
        out.write("\n");
        for (int i=0; i< indentTabs; i++){
            out.write(tabString);
        }
        return;
    }

    /**
     * Turns on or off 'pretty-printing' of XML with newlines and indentation to
     * make output more readable.
     *
     * <p>For efficiency, useIndent is set to false by default.  The size of an
     * indentation can be set using the method <tt>setIndent</tt>.
     * @param useIndent Indicates whether indentation and newlines should be
     * written to make the output DSML more readable.
     * @see #setIndent
     */
    public void useIndent( boolean useIndent )
    {
        this.indent = useIndent;
        return;
    }

    /**
     * Sets the number of spaces for indentation of XML tags.
     * <p>This setting is ignored by default unless indentation is turned on via
     * the <tt>useIndent</tt> method. </p>
     *
     * @param spaces Number of spaces used in each indentation.
     * @see #useIndent
     */
    public void setIndent(int spaces)
    {
        StringBuffer temp = new StringBuffer();
        for (int i=0; i< spaces; i++){
            temp.append(' ');
        }
        this.tabString = temp.toString();
        return;
    }
    
    private boolean isXMLSafe(byte[] val) {
    	boolean safe = true;
    	
    	for (int i=0,m=val.length;i<m;i++) {
    		if (val[i] >= 0 && val[i] <= 31) {
    			safe = false;
    			break;
    		}
    	}
    	
    	
    	return safe;
    }
    
    private String makeXMLSafe(String msg) {
    	if (msg == null) {
    		return "";
    	}
    	byte[] val = msg.getBytes();
    	boolean safe = true;
    	
    	for (int i=0,m=val.length;i<m;i++) {
    		if (val[i] >= 0 && val[i] <= 31) {
    			val[i] = (byte) ' ';
    		}
    	}
    	
    	return new String(val);
    	
    }
    
    private String makeAttributeSafe(String attrib) {
    	
    	String ret = attrib;
    	ret = ret.replaceAll("&", "&amp;");
		ret = ret.replaceAll("<", "&lt;");
		ret = ret.replaceAll(">", "&gt;");
		ret = ret.replaceAll("'", "&apos;");
		ret = ret.replaceAll("\"", "&quot;");
    	return ret;
    }
    
	/**
	 * @return Returns the useSOAP.
	 */
	public boolean isUseSOAP() {
		return useSOAP;
	}
	/**
	 * @param useSOAP The useSOAP to set.
	 */
	public void setUseSOAP(boolean useSOAP) {
		this.useSOAP = useSOAP;
	}
}

