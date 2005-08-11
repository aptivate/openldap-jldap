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
 ******************************************************************************/

package com.novell.ldap.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.LDAPAddRequest;
import com.novell.ldap.LDAPDeleteRequest;
import com.novell.ldap.LDAPModifyDNRequest;
import com.novell.ldap.LDAPModifyRequest;
import com.novell.ldap.util.Base64;

/**
 * The class to process the inputStream object to read an LDIF file.
 *
 * <p>This calss reads LDAP entries and LDAP Requests form an LDIF file</p>
 *
 * <p>The constructors uses a default size value of 8,192 to create the
 *    buffering character-input stream and assume that the size is big
 *    enough to hold the dn field and the first line of the next field
 *    of the first record in the LDIF file currently being read.</p>
 *
 * <p>The constructors uses '1' as default LDIF file version</p>
 */
public class LDIFReader implements LDAPReader {

    private boolean            requestFile=true;          // request file=true
    private String             version;                   // LDIF file version
    private int                reqType;                   // int rep. of name
    private int                lNumber;                   // line number
    private int                dnlNumber;                  // dn line number
    private int                fNumber = 0;               // number of fields
    private byte[]             bytes= new byte[0];        // for any byte value
    private boolean            control = false;            // is control field
    private String             entryDN;                   // entry dn
    private String[]           modInfo;                   // for moddn
    private ArrayList          rFields = new ArrayList(); // record fields
    private ArrayList          cList = new ArrayList();   // control list
    private BufferedReader     bufReader;
    private LDAPControl[]      controls = null;           // req controls
    private LDAPEntry          currentEntry = null;
    private LDAPModification[] mods;
    private LDAPMessage        currentRequest = null;

    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isRequest,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The InputStream object to be processed by LDIFReader
     */
    public LDIFReader( InputStream in )
                throws IOException, LDAPLocalException
    {
        this( in, 1, 8192 );
        return;
    }

    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isRequest,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     * @param version  The version currently used in the LDIF file
     */
    public LDIFReader( InputStream in, int version )
                throws IOException, LDAPLocalException
    {
        this( in, version, 8192 );
        return;
    }
    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isRequest,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     * @param version  The version currently used in the LDIF file
     * @param bufSize  The size used to create a buffering character-input
     *                 stream. The defaule value is 8,192.
     */
    public LDIFReader(InputStream in, int version, int bufSize)
                throws IOException, LDAPLocalException
    {

        super();

        String line = null;

        if ( version != 1 ) {  // check LDIF file version
            throw new RuntimeException("com.novell.ldap.ldif_dsml.LDIFReader:"
                              + "found: " + version + ", Should be: 1");
        }

        setVersion( version );
        InputStreamReader isr = new InputStreamReader(in, "US-ASCII");
        bufReader = new BufferedReader(isr);

        // In order to determine if it is a LDIF content file or LDIF change
        // file, the first line of dn field and the meaningful line next to
        // dn field are read into memory.

        // skip the leading empty and comment lines before version line
        while( (line = bufReader.readLine())!= null &&
               (line.length() == 0 || line.startsWith("#")) ) {
            this.lNumber++;
        }

        // already reaches the end of file
        if ( line == null ) {
            throw new LDAPLocalException(
                "com.novell.ldap.ldif_dsml.LDIFReader:"
                    + " The file contains no LDIF info",
                        LDAPException.LOCAL_ERROR);
        }

        // need to increase line number
        this.lNumber++;

        // the first effective line(the version line). check the version line
        if (line.startsWith("version:")) {
            this.version = line.substring("version:".length()).trim();
            if ( !this.version.equals( "1") ) {
                throw new LDAPLocalException(
                    "com.novell.ldap.ldif_dsml.LDIFReader: "
                        + "version: found '" + version + "' (on line "
                            + this.lNumber + " of the file), should be '1'",
                                LDAPException.LOCAL_ERROR);
            }
        }
        else { // first effective line is not a version line
            throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
                + " Version line must be the first meaningful line(on line " +
                    this.lNumber + " of the file)",
                        LDAPException.LOCAL_ERROR);
        }

        // skip any empty and comment lines between the version line and
        // the first line of the dn field in the first record of the LDIF
        // file, read the first line of the dn field of the first record
        do {
            // mark the first dn line, so we can later go back to here
            bufReader.mark( bufSize );
            line=bufReader.readLine();

            if ( line == null) {  // end of file
                throw new LDAPLocalException( "com.novell.ldap.ldif_dsml."
                    + "LDIFReader: the LDIF file only contains version line.",
                        LDAPException.LOCAL_ERROR);
            }
            this.lNumber++;
        } while((line.length()== 0) || line.startsWith("#"));

        // will check dn field later; now ignore the rest lines of the
        // dn field and read the effective line right after the dn field
        while ( (line = bufReader.readLine()) != null ) {

            // ! a part of dn field       ! a comment line
            if ( !line.startsWith(" ") && !line.startsWith("#") ) {
                 // to the end of the first record
                 if ( line.length() == 0 ) {
                    // an empty line; this record only has dn field
                    throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                        + "LDIFReader: the first record only has dn field(line "
                            + this.lNumber + " of the file)",
                                LDAPException.LOCAL_ERROR);
                 }
                 // the line just read should be the line that starts with
                 // either 'control', 'changetype', or an attribute name.
                break;
            }
        }

        if ( line == null) { // end of file
            throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                        + "LDIFReader: the first record only has dn field(line "
                            + this.lNumber + " of the file)",
                                LDAPException.LOCAL_ERROR);
        }

        if(line.startsWith("changetype")||line.startsWith("control")){
            setRequest(true);  // LDIF change file with LDAP operation requests
        }
        else {
            setRequest(false); // LDIF content file with LDAP entries
        }

        // go back to the beginning of the first record of the LDIF file so
        // later read can start from the first record
        bufReader.reset();

        //
        this.lNumber--;
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
     * Gets the version of the LDIF data associated with the input stream
     *
     * @param value the version number
     */
    private void setVersion(int value)
    {
        version = String.valueOf(value);
        return;
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
     * Sets the request type of the file being read, true if request data
     * or false if content data.
     *
     * @param type sets the type of file to content or request data.
     */
    private void setRequest( boolean type)
    {
        requestFile = type;
        return;
    }

    /**
     * Read LDAP Requests from the LDIF request (change) file or content file.
     *
     * @return LDAPMessage specified by the record
     */
    public LDAPMessage readMessage()
                throws IOException, LDAPException
    {
        readRecordFields();           // read record fields
        if ( this.rFields == null ) { // end of file
            return null;
        }
        toRecordProperties();         // set record properties

        if (!isRequest()) {
            return new LDAPSearchResult(currentEntry, null);
        }

        switch( this.reqType ) {
            case LDAPMessage.SEARCH_RESPONSE :
                this.currentRequest = new LDAPAddRequest(currentEntry, controls);
                break;
            case LDAPMessage.ADD_REQUEST :
                this.currentRequest = new LDAPAddRequest(currentEntry, controls);
                break;
            case LDAPMessage.DEL_REQUEST :
                this.currentRequest = new LDAPDeleteRequest(this.entryDN, controls);
                break;
            case LDAPMessage.MODIFY_RDN_REQUEST :
                boolean  delOldRdn;

                if ( Integer.parseInt(this.modInfo[1]) == 1 ) {
                    delOldRdn = true;
                } else {
                    delOldRdn = false;
                }

                if((modInfo[2].length())==0 ) {
                    this.currentRequest = new LDAPModifyDNRequest( this.entryDN,
                                     this.modInfo[0], null, delOldRdn, controls);
                } else {
                    this.currentRequest = new LDAPModifyDNRequest(this.entryDN,
                         this.modInfo[0], modInfo[2], delOldRdn, controls);
                }
                break;
            case LDAPMessage.MODIFY_REQUEST :
                this.currentRequest =
                          new LDAPModifyRequest(this.entryDN, mods, controls);
                break;
            default:
        }

        return this.currentRequest;
    }


    /**
     * Read all lines in the current record, convert record lines to
     * the record fields, and trim off extra spaces in record fields.
     */
    private void  readRecordFields()
                throws IOException, LDAPException
    {

        String line;
        StringBuffer bLine = new StringBuffer(80);

        // clean rFields
        this.rFields.clear();

        // skip empty and comment lines and read the first dn
        // line of the record
        while( (line = bufReader.readLine())!= null &&
               (line.length() == 0 || line.startsWith("#")) ) {
            this.lNumber++;
        }

        this.lNumber++;
        this.dnlNumber = this.lNumber;

        if (line == null) { // end of file
            this.rFields = null;
        }
        else {
            // check if dn line starts with 'dn:'
            if (!line.startsWith("dn:")) {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml." +
                    "LDIFReacer: Any record should start with 'dn:'(on line "
                        + this.lNumber + " of the file).",
                            LDAPException.LOCAL_ERROR);
            }

            // save the first dn line
            bLine.append(line);

            // read rest lines of the record except comment lines.
            // read stops at an empty line which is used to separate
            // the current record with the next
            while ((line = bufReader.readLine())!=null && line.length()!=0 ) {
                if ( !line.startsWith("#") ) {       // skip comment line
                    if ( line.startsWith(" ") ) {    // continuation line ?
                        // trim off leading ' ' and append it to previous line
                        bLine.append(line.substring(1, line.length()));
                    }
                    else { // a new line
                        // handle pewvious field
                        bLine = trimField(bLine);    // trime previous field
                        if(!this.control) {          // save it if it's not
                            this.rFields.add(bLine); // a control field
                        }
                        // handle new line
                        bLine = new StringBuffer(80);// create a new buffer
                        bLine.append(line);          // to hold new line
                    }
                }
                this.lNumber++;
            }
            // trim and save the last field
            bLine = trimField(bLine);
            this.rFields.add(bLine);

            this.lNumber++;                      // increase the line number
            this.fNumber = this.rFields.size();  // get number of fields
        }
        return;
    }


    /**
     * Set record properties.
     * <p>For LDIF content record, LDAPEntry specidfied by this record is
     * created</p>
     *
     * <p>For LDIF change record, depending on the request type, either
     * LDAPEntry, modInfo, or LDAPModifiction array along with the controls
     * associated with the request are created</p>
     */
    private void toRecordProperties()
                throws IOException, LDAPException
    {

        int index;
        String req;

        // set entry DN
        StringBuffer dnField = (StringBuffer)this.rFields.get(0);
        if (dnField.charAt(3) != ':') {
            // commom string value
            this.entryDN = dnField.substring( 3, dnField.length());
        }
        else {
            // base64 encoded
            this.bytes = Base64.decode(dnField, 4, dnField.length());
            try {
                this.entryDN = new String(this.bytes, "UTF-8");
            } catch( UnsupportedEncodingException ue) {
                throw new RuntimeException(
                    "UTF-8 String encoding not supported by JVM");
            }
        }

        if ( !isRequest() ) {  // is a content LDIF file
            toLDAPEntry();
        }
        else {  // is a change LDIF file
            index = 10; // length of 'changetype'
            // ctField - changetype field
            StringBuffer ctField = (StringBuffer)this.rFields.get(1);

            if(!ctField.substring(0, index).equalsIgnoreCase("changetype")) {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    +"LDIFReader: malformed changetype field in record starting"
                        + " on line " + this.dnlNumber + " of the file).",
                            LDAPException.LOCAL_ERROR);
            }
            // get request type, eg. 'add', 'delete',
            // 'moddn', 'modrdn', or 'modify'
            req = ctField.substring(index+1);

            // set request type
            if ( req.equalsIgnoreCase("add") ) {
                this.reqType = LDAPMessage.ADD_REQUEST;
                toLDAPEntry();
            }
            else if ( req.equalsIgnoreCase("delete") ) {
                this.reqType = LDAPMessage.DEL_REQUEST;
            }
            else if ( req.equalsIgnoreCase("modrdn") ) {
                this.reqType = LDAPMessage.MODIFY_RDN_REQUEST;
                toModInfo();
            }
            else if ( req.equalsIgnoreCase("moddn") ) {
                this.reqType = LDAPMessage.MODIFY_RDN_REQUEST;
                toModInfo();
            }
            else if ( req.equalsIgnoreCase("modify") ) {
                this.reqType = LDAPMessage.MODIFY_REQUEST;
                toLDAPModifications();
            }
            else {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: unsupported request type '" + req
                    + "' specified in changetype filed of the record starting "
                    + "on line " + this.dnlNumber + " of the file.",
                    LDAPException.LOCAL_ERROR);
            }


            if (this.cList.size() > 0) {
                this.controls = new LDAPControl[this.cList.size()];
            }
        }
        return;
    }


    /**
     * Process LDIF record fields to generate an LDAPEntry.
     */
    private void toLDAPEntry()
                throws LDAPLocalException
    {
        int i, index, fieldIndex;
        String attrName = null;
        StringBuffer currentField;
        LDAPAttributeSet attrSet = new LDAPAttributeSet();

        if ( !isRequest() ) { // skip dn field
            fieldIndex = 1;
        }
        else { // skip dn, control, and changetype fields
            fieldIndex = 2;
        }

        for (i=fieldIndex; i<this.fNumber; i++) {
            currentField = (StringBuffer)this.rFields.get(i);
            // ':' separates attr name and attr value
            index = IndexOf(currentField, ':');
            if (index == -1) { // ':' not found
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: missing ':' after attribute name in record "
                        + "starting on line " + this.dnlNumber +" of the file.",
                            LDAPException.LOCAL_ERROR);
            }

            // get attribute name
            attrName = currentField.substring(0,index);
            // if attrName not existing in attrSet, add it
            if ( attrSet.getAttribute(attrName) == null ) {
                // add it to attrSet with no value
                attrSet.add(new LDAPAttribute(attrName));
            }

            if(currentField.length() > index+1)
            {
            // add attr value to attr
                if (currentField.charAt(index+1)==':') {
                    // base64 encoded attribute value
                    attrSet.getAttribute(attrName).addBase64Value(currentField.
                                                           substring(index+2));
                }
                else if (currentField.charAt(index+1)=='<'){
                    // file URL attribute value
                    attrSet.getAttribute(attrName).addBase64Value(currentField.
                                                           substring(index+2));
                }
                else {
                    // string value
                    String vals=currentField.substring(index+1).trim();
                    attrSet.getAttribute(attrName).addValue(vals);
//                  attrSet.getAttribute(attrName).addValue(currentField.
//                                                           substring(index+1));
                }
            }
            else if(currentField.length() == index+1)
            {
                String vals=new String("");
                attrSet.getAttribute(attrName).addValue(vals);
            }

        }
        // construct the currentEntry
        this.currentEntry = new LDAPEntry(this.entryDN, attrSet);
        return;
    }


    /**
     * Build String array object that contains moddn information.
     */
    private void toModInfo()
                throws LDAPLocalException
    {

        int index = 6;      // length of "newrdn"
        int fieldIndex = 2; // reference newrdn field
        this.modInfo = new String[3];
        StringBuffer currentField = (StringBuffer)this.rFields.get(fieldIndex);



        if( ! currentField.substring(0, index+1).equalsIgnoreCase("newrdn:")) {
             throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
                 + " malformed newrdn field in record starting on line "
                 + this.dnlNumber + " of the file.", LDAPException.LOCAL_ERROR);
        }

        // get newrdn
        if ( currentField.charAt(index+1) != ':') {
            // common string value
            this.modInfo[0] = currentField.substring(index+1);
        }
        else {
            // decode newrdn
            this.bytes = Base64.decode( currentField, index+2,
                                                        currentField.length());
            try {
                this.modInfo[0] = new String(this.bytes, "UTF-8");
            } catch( UnsupportedEncodingException ue) {
                throw new RuntimeException(
                    "UTF-8 String encoding not supported by JVM");
            }
        }

        fieldIndex++;   // reference deleteOleRDN field
        index = 13;     // length of "deleteoldrdn"
        currentField = (StringBuffer)this.rFields.get(fieldIndex);

        if( ! currentField.substring(0, index).equalsIgnoreCase(
                                                           "deleteoldrdn:") ) {
            throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
                + " malformed deleteoldrdn field in record starting on line "
                + this.dnlNumber + " of the file.", LDAPException.LOCAL_ERROR);
        }

        char c = currentField.charAt(index);
        if (c == '1') {
            this.modInfo[1] = new String("1");
        }
        else if (c == '0'){
            this.modInfo[1] = new String("0");
        }
        else {
            throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
               + " value for deleteoldrdn field should '0' or '1', found '" + c
               + "' in the record starting on line " + this.dnlNumber
               + " of the file.", LDAPException.LOCAL_ERROR);
        }

        fieldIndex++;   // reference newsuperior field
        
        if (fieldIndex == this.fNumber) { // no newsuperior spefified
            this.modInfo[2] = new String("");
        }
        else { // there is a newsuperior
            currentField = (StringBuffer)this.rFields.get(fieldIndex);
            index = 12;   // length of "newsuperior:"
            if( ! currentField.substring(0, index).equalsIgnoreCase(
                                                             "newsuperior:")) {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: malformed newsuperior field in the record "
                    + "starting on line " + this.dnlNumber + " of the file.",
                    LDAPException.LOCAL_ERROR);
            }

            if ( currentField.charAt(index) != ':') {
                // commom string value
                this.modInfo[2] = currentField.substring(index);
            }
            else {
                // base64 encoded value
                this.bytes = Base64.decode( currentField, index+1,
                                                       currentField.length());
                this.modInfo[2] = new String(this.bytes);;
            }
        }
        return;
    }

    /**
     * Build LDAPModification array based on the content of LDIF modify record.
     */
    private void toLDAPModifications()
                throws LDAPLocalException
    {

        int        i, index;
        int        fieldIndex = 2;    // skip dn, control, and changetype field
        String     attrName, opName;
        LDAPAttribute attr = null;
        ArrayList modList = new ArrayList();

        if (!(this.rFields.get(this.fNumber-1)).toString().
                                                      equalsIgnoreCase("-") ) {
            throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                  + "LDIFReader: modify record not ends with '-' in the record"
                  + " starting on line " + this.dnlNumber + " of the file.",
                    LDAPException.LOCAL_ERROR);
        }

        // populate the LDAPModification array object
        for (i=fieldIndex; i<this.fNumber; i++) {
            // find ':' that separate mod operation and attr name
            index = IndexOf((StringBuffer)this.rFields.get(i), ':');
            if (index == -1) { // ':' not found
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                   + "LDIFReader: malformed opName:attrName field in the record"
                   + " starting on line " + this.dnlNumber + " of the file.",
                    LDAPException.LOCAL_ERROR);
            }

            StringBuffer nextField = (StringBuffer)this.rFields.get(i);
            opName= nextField.substring(0, index);
            attrName= nextField.substring(index+1);

            i++; // point to attrName:attrValue field
            nextField = (StringBuffer)this.rFields.get(i);

            // build each LDAPModification object and add it to modList
            if (nextField.charAt(0)!='-') {
                // there is at least one attrName:attrValue field
                for ( ; nextField.charAt(0)!='-';
                        i++, nextField = (StringBuffer)this.rFields.get(i)) {
                    // index separate attr name and attr value
                    if ((index=IndexOf(nextField, ':')) == -1) {
                        throw new LDAPLocalException("com.novell.ldap."
                            + "ldif_dsml.LDIFReader : no ':' found in attrName:"
                            + "attrValue field in the record starting on line "
                            + this.dnlNumber + " of the file.",
                            LDAPException.LOCAL_ERROR);
                    }
                    else {
                        // compare attr names from opName:attrName
                        // and attrName:attrValue fields
                        String aName = nextField.substring(0, index);
                        if (!aName.equalsIgnoreCase(attrName)) {
                            throw new LDAPLocalException("com.novell.ldap."
                            + "ldif_dsml.LDIFReader : found attribute name '"
                            + aName + "', should be '" + attrName
                            + "' in attrName:attrValue field in the record "
                            + "starting on line " + this.dnlNumber
                            + " of the file.", LDAPException.LOCAL_ERROR);
                        }

                        // create attr and add value to it
                        attr = new LDAPAttribute(attrName);
                        if (nextField.charAt(index+1)==':') {
                            // base64 encoded attribute value
                            attr.addBase64Value(nextField.substring(index+2));
                        }
                        else if (nextField.charAt(index+1)=='<'){
                            // file URL attribute value
                            attr.addBase64Value(nextField.substring(index+2));
                        }
                        else {
                            // string value
                            attr.addValue(nextField.substring(index+1));
                        }


                        if ( opName.equalsIgnoreCase("add") ) {
                            modList.add( new LDAPModification(
                                                  LDAPModification.ADD, attr));
                        }
                        else if ( opName.equalsIgnoreCase("delete") ) {
                            modList.add( new LDAPModification(
                                               LDAPModification.DELETE, attr));
                        }
                        else if ( opName.equalsIgnoreCase("replace") ) {
                            modList.add( new LDAPModification(
                                              LDAPModification.REPLACE, attr));
                        }
                        else {
                            throw new LDAPLocalException("com.novell.ldap."
                                + "ldif_dsml.LDIFReader : Not supported modify "
                                + " request (" + opName + ") specified in "
                                + "record starting on line " + this.dnlNumber
                                + " of the file.", LDAPException.LOCAL_ERROR);
                        }
                    }
                }
            }
            else {
                // there is no attribute value specified; this could be
                // true for 'delete' and 'replace' modify operation
                attr = new LDAPAttribute(attrName);

                if ( opName.equalsIgnoreCase("delete") ) {
                    modList.add( new LDAPModification(
                                               LDAPModification.DELETE, attr));
                }
                else if ( opName.equalsIgnoreCase("replace") ) {
                    modList.add( new LDAPModification(
                                              LDAPModification.REPLACE, attr));
                }
                else {
                    throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                        + "LDIFReader: For '" + opName + "', no value "
                        + "specified for atribute '" + attrName
                        + "' in the record starting on line "
                        + this.dnlNumber + " of the file.",
                        LDAPException.LOCAL_ERROR);
                }
            }
        }
        this.mods = new LDAPModification[modList.size()];
        this.mods = (LDAPModification[])modList.toArray(this.mods);
        return;
    }

    /**
     * Returns the index within this StringBuffer object
     * of the first occurence of the specified char.
     *
     * @param bl  The StringBuffer object
     * @param ch   The character to look for in the StringBuffer object
     *
     * @return The index of the first occurence of the character in the
     * StringBuffer object, or -1 if the character does not occur.
     */
    private int IndexOf(StringBuffer bl, int ch)
    {

        if (bl != null ) {
            for (int i=0;i<bl.length(); i++) {
                if(bl.charAt(i) == ch) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * <tt>trimField<tt> trims off extra spaces in a field. It also
     * trims confield and constructs control onjects.
     */
    private StringBuffer trimField( StringBuffer line)
                throws LDAPLocalException
    {
        int c, lastChar = 0, charIndex = 0;
        char t;
        char[] newChars;
        boolean isEncoded=false, isURL=false, criticality = false;
        String oid = null;

        if ((line == null)||((c=IndexOf(line,':'))==-1)) {
            // not all fields contain ':'
            return line;
        }

        // elminate any trailing spaces
        lastChar = line.length() - 1;
        while( line.charAt(lastChar) == ' ') {
            lastChar--;
        }

        // create newChars
        newChars = new char[lastChar+1];

        if( (c > 6) && (line.substring(0,c).equals("control"))) {
            // this is a control field
            this.control = true;
            c++;            // skip past ':'
            // eliminate any spaces after ':'
            while( (c <= lastChar) && (line.charAt(c) == ' ')) {
                c++;
            }
        }
        else {
            // not a control field. it's 'dn',
            //'changetype', or 'attrName' field
            this.control = false;

            // copy field name and ':', eg. 'dn:', 'changetype:', or 'attrName:'
            line.getChars(0, c+1, newChars, 0);
            // skip over copied chars
            charIndex += c + 1;
            // c points to char right after first ':'
            c++;
        }

        if(!this.control) {
            // // not a control field. check if '::' or ':<'
            if( c <= lastChar) {
                t = line.charAt(c);
                if( t == ':') {
                    newChars[charIndex++] = ':'; // save the ':' to
                    c++;                         // point to value
                }
                else if( t == '<') {
                    newChars[charIndex++] = '<'; // save the '<' to
                    c++;                         // point to value
                }
            }

			// for case like attr: <value>
			boolean nonfile=false;
		    String fredir= line.substring(c);            
		    if(fredir.length()>0 && fredir.charAt(0) != '<'){
			         String cstr=fredir.trim();
			         if(cstr.length()>0 && cstr.charAt(0) == '<'){
                          nonfile=true;
                     }
                }
                
            // eliminate any space(s) after ':' or '<'
            while( (c <= lastChar) && (line.charAt(c) == ' ')) {
                c++;
            }
            
			// for case like attr: <value>            
			if(nonfile==true){
			    c--;
			}
            

            if( c <= lastChar) {  // thers is a value specified
                // copy field value
                line.getChars(c, lastChar+1, newChars, charIndex);

                charIndex += lastChar - c + 1;
                // create a new StringBuffer object with capacity of lastChar
                StringBuffer newBuf = new StringBuffer(lastChar);
                // copy the filed represented by newChars
                newBuf.append( newChars, 0, charIndex);
                // return the trimed field
                return newBuf;
            }
            else if ( line.length() == c){
                StringBuffer newBuf= new StringBuffer();
                line.getChars(c, lastChar+1, newChars, charIndex);
                charIndex += lastChar - c + 1;
                newBuf.append( newChars, 0, charIndex);	
                return newBuf;
            }
            
            else {  // there is no value specified
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                        + "LDIFReader: a field contains no value after ':'. the "
                        + "field is in the record starting on line "
                        + this.dnlNumber + " of the file.",
                        LDAPException.LOCAL_ERROR);
            }
        }
        else {  // a control field
            // process values for control. a control field may looks like
            //    1. control: 1.2.3.4 true: control value
            //    2. control: 1.2.3.4: control value
            //    3. control: 1.2.3.4
            // extra spaces are possible between oid, criticality, and value.
            // oid is a must, while criticalitty and value can be absent.

            // get control oid
            int b = c;
            while(c <= lastChar) {
                // an oid consists of dots and digits
                t = line.charAt(c);
                if( (t == '.') || (Character.isDigit(t))) {
                    c++;
                    continue;
                }
                break;
            }

            if( b == c) {  // control with no oid
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: Control with no oid in the record "
                        + "starting on line " + this.dnlNumber
                        + " of the file.", LDAPException.LOCAL_ERROR);
            }
            else {  // control has iod, get local copy of oid
                char[] chars = new char[c-b];
                line.getChars(b, c, chars, 0);
                oid = new String(chars);
            }

            if ( c > lastChar) {
                // control only has an oid. create LDAPControl object
                // with oid, 'false' and empty byte array
                LDAPControl ctrl = new LDAPControl(oid, false, new byte[0]);
                // add it to cList
                this.cList.add(ctrl);
                return null;  // return value has no use
            }

            // get control criticality
            t = line.charAt(c);
            if( t == ' ') {
                // see a space, skip over any spaces
                while( (c <= lastChar) && (line.charAt(c) == ' ')) {
                    c++;
                }
            }
            // what we see now? 'true', 'false', or ':' ?
            if(((c + 3) <= lastChar)&&(line.substring(c,c+4).equals("true"))) {
                // found 'true'
                c += 4;
                criticality = true;
            }
            else if(((c+4)<=lastChar)&&(line.substring(c,c+5).equals("false"))){
                // found 'false'
                c += 5;
                criticality = false;
            }

            if (c > lastChar) {  // to the end of the control field
                // create LDAPControl object with oid,
                // criticality, and empty byte array
                LDAPControl ctrl=new LDAPControl(oid, criticality, new byte[0]);
                // add it to cList
                this.cList.add(ctrl);
                return null;
            }

            if ((t=line.charAt(c)) != ':') {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                        + "LDIFReader: Unexcepted char '" + t + "'. Expecting "
                        + "to see ':' in the record starting on line "
                        + this.dnlNumber + " of the file.",
                        LDAPException.LOCAL_ERROR);
            }

            // get control value
            c++;  // go to enst char after ':'
            if (c > lastChar) {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: No control value after ':' "
                    + "in the record starting on line "
                    + this.dnlNumber + " of the file.",
                    LDAPException.LOCAL_ERROR);
            }

            // positioned at the first char right after ':'
            // check if '::' or ':<'
            t = line.charAt(c);
            if( t == ':') {
                isEncoded = true;            // indicate encoded value
                c++;                         // point to value
                if (c > lastChar) {
                    throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                        + "LDIFReader: No control value after '::' "
                        + "in the record starting on line "
                        + this.dnlNumber + " of the file.",
                        LDAPException.LOCAL_ERROR);
                }
            }
            else if( t == '<') {
                isURL = true;                // indicate file URL value
                c++;                         // point to value
                if (c > lastChar) {
                    throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                        + "LDIFReader: No control value after ':<' "
                        + "in the record starting on line "
                        + this.dnlNumber + " of the file.",
                        LDAPException.LOCAL_ERROR);
                }
            }

            // eliminate any space(s) after ':', '::' or ':<'
            while((c <= lastChar) && (line.charAt(c) == ' ')) {
                c++;
            }

            if(c <= lastChar) {  // thers is a value spec specified
                char[] chars = new char[lastChar+1-c];
                line.getChars(c, lastChar+1, chars, 0);

                if (isEncoded) {
                    this.bytes = Base64.decode(chars);
                }
                else if (isURL) {
                    // if isURL, what to do?
                    this.bytes = (new String(chars)).getBytes();
                }
                else {
                    this.bytes = (new String(chars)).getBytes();
                }
            }
            // create LDAPControl object
            LDAPControl ctrl = new LDAPControl(oid, criticality, this.bytes);
            // add it to cList
            this.cList.add(ctrl);
        }
        return null;
    }
}
