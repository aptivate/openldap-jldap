/* **************************************************************************
 * $Novell: DSMLWriter.java,v 1.27 2002/11/27 20:36:31 $
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

import com.novell.ldap.*;

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

    private static LDAPMessage currentChange = null;
    private Writer out = null;
    private int state = NEW_BATCH;
    private static final int NEW_BATCH = 0;
    private static final int REQUEST_BATCH = 1;
    private static final int RESPONSE_BATCH = 2;
    private static final int SEARCH_TAG = 3;
    private boolean indent = false;
    private String tabString = "    ";
    private String version = "2.0";

    private static final String BATCH_REQUEST_START =
            "<batchRequest xmlns=\"urn:oasis:names:tc:DSML:2:0:core\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    private static final String BATCH_RESPONSE_START =
            "<batchResponse xmlns=\"urn:oasis:names:tc:DSML:2:0:core\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";

    /**
     * Initializes this writer by opening the specified file to write DSML into.
     * @param file  File to write DSML
     * @throws FileNotFoundException occurs when the specified file could not
     * be opened or is not found.
     */
    public DSMLWriter(String file) throws FileNotFoundException {
        this( new FileOutputStream(file, true));
    }

    /**
     * Initializes this writer with the specified outputstream to write DSML
     * into.
     * @param stream  Output stream to write DSML
     */
    public DSMLWriter(OutputStream stream){
        out = new OutputStreamWriter(stream);
    }

    /**
     * Initializes this writer with the specified writer to write DSML into.
     * @param writer Writer to write DSML
     */
    public DSMLWriter(Writer writer){
        out = writer;
    }

    /**
     * Any LDAPException can be written in DSML with this method, via the
     * <errorResponse> tag.
     * @param e  LDAPException to be written in DSML.
     */
    public void writeError(LDAPException e)
            throws IOException, LDAPLocalException
    {
        //check if we are in a response, if not set the state and write DSML tag
        checkState(true);
        newLine(1);
        out.write("<errorResponse type=\"");
        switch (e.getResultCode()){
            case LDAPException.DECODING_ERROR:
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
        newLine(2);
        out.write("<message>");
        newLine(3);
        out.write(e.toString());
        newLine(2);
        out.write("</message>");
        newLine(1);
        out.write("</errorResponse>");
    }

    /**
     * Writes closing tags for searchResponse, batchRequests, and batchResponse
     * depending on the current state.
     */
    public void finish() throws IOException {
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
        }
        newLine(0);
        //out.flush();
    }

    /**
     * Writes the specified strings as XML comments.
     * @param lines Comments to be written
     */
    public void writeComments(String lines) throws IOException {
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
            case LDAPMessage.SEARCH_REQUEST:
            case LDAPMessage.BIND_RESPONSE:
            case LDAPMessage.MODIFY_REQUEST:
            case LDAPMessage.ADD_REQUEST:
            case LDAPMessage.DEL_REQUEST:
            case LDAPMessage.MODIFY_RDN_REQUEST:
            case LDAPMessage.COMPARE_REQUEST:
            case LDAPMessage.ABANDON_REQUEST:
            case LDAPMessage.EXTENDED_REQUEST:
                throw new java.lang.UnsupportedOperationException(
                        "Writing of this message is not supported");

            //Responses:
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
                newLine(2);

                out.write("<responseName>");
                out.write(xResp.getID());
                out.write("</responseName>");

                byte[] value = xResp.getValue();
                if (value != null){
                    byte[] valueWithoutTagAndLength = new byte[value.length - 2];
                    System.arraycopy(value, 2,
                            valueWithoutTagAndLength, 0,
                            valueWithoutTagAndLength.length);

                    newLine(2);
                    if (Base64.isValidUTF8(valueWithoutTagAndLength, false)){
                        out.write("<response>");
                        out.write(new String(valueWithoutTagAndLength, "UTF8"));
                    } else {
                        out.write("<response xsi:type=\"xsd:base64Binary\">");
                        out.write(Base64.encode(valueWithoutTagAndLength));
                    }
                    out.write("</response>");
                }
                newLine(1);
                out.write("</extendedResponse>");
                break;
        }
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
                out.write(" matchedDN=\"" + matchedDN + "\"");
            }
        }
        out.write(">");
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
    private void writeResult(LDAPResponse result, int indent) throws IOException {
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
            out.write(temp);
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
    private void writeControls(LDAPControl[] controls, int indent) throws IOException {
        for(int i=0; i<controls.length; i++){
            newLine(indent);
            out.write("<control numericOID=\"");
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
            throws IOException
    {
        LDAPEntry entry = result.getEntry();
        newLine(2);
        out.write("<searchResultEntry dn=\"");
        out.write(entry.getDN());
        out.write("\" requestID=\"");
        out.write(""+DOMWriter.findRequestID(result));
        out.write("\">");
        LDAPAttributeSet set = entry.getAttributeSet();
        Iterator i = set.iterator();
        while (i.hasNext()){
            writeAttribute( (LDAPAttribute) i.next());
        }
        LDAPControl controls[] = result.getControls();
        if (controls !=null){
            writeControls(controls, 3);
        }
        newLine(2);
        out.write("</searchResultEntry>");

        return;
    }

    /**
     * Used to write an attribute and its values.
     * @param attr Attribute to be written.
     */
    private void writeAttribute(LDAPAttribute attr) throws IOException {
        newLine(3);
        out.write("<attr name=\"");
        out.write(attr.getName());
        out.write("\">");
        String values[] = attr.getStringValueArray();
        byte bytevalues[][] = attr.getByteValueArray();
        for(int i=0; i<values.length; i++){
            newLine(4);
            if (Base64.isValidUTF8(bytevalues[i], false)){
                out.write("<value>");
                out.write(values[i]);
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
                out.write(BATCH_REQUEST_START);
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
    private void newLine(int indentTabs) throws IOException {
        if (!indent)
            return;
        out.write("\n");
        for (int i=0; i< indentTabs; i++){
            out.write(tabString);
        }
        //out.flush();

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
    public void useIndent( boolean useIndent ){
        this.indent = useIndent;
    }

    /**
     * Sets the number of spaces for indentation of XML tags.
     * <p>This setting is ignored by default unless indentation is turned on via
     * the <tt>useIndent</tt> method. </p>
     *
     * @param spaces Number of spaces used in each indentation.
     * @see #useIndent
     */
    public void setIndent(int spaces){
        StringBuffer temp = new StringBuffer();
        for (int i=0; i< spaces; i++){
            temp.append(' ');
        }
        this.tabString = temp.toString();
    }
}
