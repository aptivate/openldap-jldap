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

package com.novell.ldap.ldif_dsml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.ldif_dsml.LDAPAdd;
import com.novell.ldap.ldif_dsml.LDAPDelete;
import com.novell.ldap.ldif_dsml.LDAPModDN;
import com.novell.ldap.ldif_dsml.LDAPModify;
import com.novell.ldap.ldif_dsml.Base64Decoder;

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
public class LDIFReader extends LDIF implements LDAPReader {

    private int                version;                   // LDIF file version
    private int                reqType;                   // int rep. of name
    private int                cNumber = 0;               // number of controls
    private int                fNumber;                   // number of fields
    private byte[]             bytes= new byte[0];        // for any byte value
    private boolean            hasControls = false;       // indicate req ctrls
    private String             entryDN;                   // entry dn
    private String[]           modInfo;                   // for moddn
    private StringBuffer       bLine = null;
    private ArrayList          rFields = new ArrayList(); // record fields
    private BufferedReader     bufReader;
    private LDAPControl[]      controls = null;           // req controls
    private LDAPEntry          currentEntry = null;
    private LDAPModification[] mods;
    private LDAPRequest        currentRequest = null;
    private Base64             base64 = new Base64();


    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isRequest,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     */
    public LDIFReader( InputStream in ) throws IOException {
        this( in, 1 );
    }

    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isRequest,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     * @param version  The version currently used in the LDIF file
     */
    public LDIFReader( InputStream in, int version ) throws IOException {
        this(in, version, 8192 );
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
    throws IOException {

        super();

        String line = null;

        if ( version != 1 ) {  // check LDIF file version
            throw new RuntimeException("com.novell.ldap.ldif_dsml.LDIFReader:"
                              + "found " + version + ", Should be version 1");
        }

        super.setVersion( version );
        InputStreamReader isr = new InputStreamReader(in, "US-ASCII");
        bufReader = new BufferedReader(isr);

        // In order to determine if it is a LDIF content file or LDIF change
        // file, the first line of dn field and the meaningful line next to
        // dn field are read into memory.

        // skip the leading empty and comment lines before version line
        while( (line = bufReader.readLine())!= null &&
               (line.length() == 0 || line.startsWith("#")) ) {
        }

        // already reaches the end of file
        if ( line == null ) {
             throw new RuntimeException( "com.novell.ldap.ldif_dsml.LDIFReader:"
                       + " The file contains no LDIF info" ) ;
        }

        // the first effective line(the version line). check the version line
        if (line.startsWith("version:")) {
            this.version = Integer.parseInt(
                line.substring("version:".length()).trim() );
            if ( this.version != 1 ) {
                throw new RuntimeException(
                   "com.novell.ldap.ldif_dsml.LDIFReader: "
                     + "version: found '" + this.version + "', should be '1'");
            }
        }
        else { // first effective line is not a version line
            throw new RuntimeException("com.novell.ldap.ldif_dsml.LDIFReader:"
                         + " Version line must be the first meaningful line");
        }

        // skip any empty and comment lines between the version line and
        // the first line of the dn field in the first record of the LDIF
        // file, read the first line of the dn field of the first record
        do {
            // mark the first dn line, so we can later go back to here
            bufReader.mark( bufSize );
            line=bufReader.readLine();

            if ( line == null) {  // end of file
                throw new RuntimeException( "com.novell.ldap.ldif_dsml."
                    + "LDIFReader: the LDIF file only contains version line");
            }

        } while((line.length()== 0) || line.startsWith("#"));

        // will check dn field later; now ignore the rest lines of the
        // dn field and read the effective line right after the dn field
        while ( (line = bufReader.readLine()) != null ) {
            if (    !line.startsWith(" ")    // ! a part of dn field
                 && !line.startsWith("#") ){ // ! a comment line
                 // to the end of the first record
                 if ( line.length() == 0 ) {
                    // an empty line; this record only has dn field
                    throw new RuntimeException("com.novell.ldap.ldif_dsml."
                           + "LDIFReader: the first record only has dn field");
                 }
                 // the line just read should be the line that starts with
                 // either 'control', 'changetype', or an attribute name.
                 break;
            }
        }

        if ( line == null) { // end of file
            throw new RuntimeException("com.novell.ldap.ldif_dsml."
                          + "LDIFReader: the first record only has dn field");
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
    }


    /**
     * Read the records from the LDIF content file.
     *
     * @return The LDAPEntry object represented by the LDIF content record.
     */
    public LDAPEntry readNextEntry()
    throws UnsupportedEncodingException, IOException {

        if( isRequest()) {
            throw new RuntimeException("com.novell.ldap.ldif_dsml.LDIFReader:"
                              + " Cannot read entry from LDIF change file");
        }

        readRecordFields(); // read record fields
        if ( this.rFields == null ) { // end of file
            return null;
        }
        toRecordProperties();  // set record properties

        return this.currentEntry;
    }


    /**
     * Read the LDAP Requests from the LDIF change file.
     *
     * @return LDAPRequest specified by the record
     */
    public LDAPRequest readNextRequest()
    throws UnsupportedEncodingException, IOException {

        if( !isRequest()) {
            throw new RuntimeException("Cannot read requests from LDIF"
                                                           + " content file");
        }

        readRecordFields();  // read record fields
        if ( this.rFields == null ) { // end of file
            return null;
        }
        toRecordProperties();  // set record properties

        switch( this.reqType ) {
            case LDAPRequest.LDAP_ADD :
                if (this.controls == null)
                    this.currentRequest = new LDAPAdd(currentEntry);
                else
                    this.currentRequest = new LDAPAdd(currentEntry, controls);
                break;
            case LDAPRequest.LDAP_DELETE :
                if (this.controls == null)
                    this.currentRequest = new LDAPDelete(this.entryDN);
                else
                    this.currentRequest =
                          new LDAPDelete(this.entryDN, controls);
                break;
            case LDAPRequest.LDAP_MODDN :
                boolean  delOldRdn;

                if ( Integer.parseInt(this.modInfo[1]) == 1 ) {
                    delOldRdn = true;
                }
                else {
                    delOldRdn = false;
                }

                if (this.controls == null) {
                    if( modInfo[2].length() == 0 ) {
                        this.currentRequest = new LDAPModDN( this.entryDN,
                                                   this.modInfo[0], delOldRdn);
                    }
                    else {
                        this.currentRequest = new LDAPModDN( this.entryDN,
                                       this.modInfo[0], delOldRdn, modInfo[2]);
                    }
                }
                else {
                    if((modInfo[2].length())==0 ) {
                        this.currentRequest = new LDAPModDN( this.entryDN,
                                         this.modInfo[0], delOldRdn, controls);
                    }
                    else {
                        this.currentRequest = new LDAPModDN(this.entryDN,
                             this.modInfo[0], delOldRdn, modInfo[2], controls);
                    }
                }
                break;
            case LDAPRequest.LDAP_MODIFY :
                if (this.controls == null) {
                    this.currentRequest = new LDAPModify(this.entryDN, mods);
                }
                else {
                    this.currentRequest =
                          new LDAPModify(this.entryDN, mods, controls);
                }
                break;
            default: // unknown request type
                throw new IOException("com.novell.ldap.ldif_dsml."
                                  + "LDIFReader: Unknown request type");
        }
        return this.currentRequest;
    }


    /**
     * Reads all lines in the current record, convert record lines to
     * the record fields, and trim off extra spaces in record fields.
     */
    private void  readRecordFields() throws IOException {

        String line;
        this.rFields.clear();

        // skip empty and comment lines and read the first dn
        // line of the record
        while( (line = bufReader.readLine())!= null &&
               (line.length() == 0 || line.startsWith("#")) ) {
        }

        if (line == null) { // end of file
            this.rFields = null;
        }
        else {
            // check if dn line starts with 'dn:'
            if (!line.startsWith("dn:")) {
                throw new RuntimeException("com.novell.ldap.ldif_dsml." +
                                      "Any record should start with 'dn:'");
            }

            // save the first dn line
            this.bLine = new StringBuffer();
            this.bLine.append(line);

            // read the rest lines of the record except comment lines.
            // read stops at an empty line which is used to separate
            // the current record with the next
            while ((line = bufReader.readLine())!=null && line.length()!=0 ) {
                if ( !line.startsWith("#") ) { // skip comment line
                    if ( line.startsWith(" ") ) { // continuation line
                        // trim off leading ' ' and append it to previous line
                        this.bLine.append(line.substring(1, line.length()));
                    }
                    else { // a new line:
                        trimField();  // trime the previous field
                        if ((this.bLine.length() > 7) && (this.bLine.
                                substring(0,7)).equalsIgnoreCase("control")){
                            // control field, need more triming
                            trimControlField();   // trim control field
                            this.cNumber++;       // increase control number
                            this.hasControls = true;
                        }
                        this.rFields.add(this.bLine); // save previous line
                        // handle new line
                        this.bLine = new StringBuffer();
                        this.bLine.append(line);
                    }
                }
            }
            // save the last field (assuming it's not a control field)
            trimField();                             // trim the last field
            this.rFields.add(this.bLine);            // save the last field
            this.fNumber = this.rFields.size();  // get number of fields
        }
    }


    /**
     * Set record properties.
     * <p>For LDIF content record, LDAPEntry specidied by this record is
     * created</p>
     *
     * <p>For LDIF change reocrd, depending on the request type, either
     * LDAPEntry, modInfo, or LDAPModifiction array along with the controls
     * associated with the request are created</p>
     */
    private void toRecordProperties() throws IOException {

        int i, index;
        String req;
        boolean criticality;

        // set entry DN
        if ( (((StringBuffer)this.rFields.get(0)).charAt(3)) != (int)':') {
            // commom string value
            this.entryDN = ((StringBuffer)this.rFields.get(0)).substring( 3,
                                 ((StringBuffer)this.rFields.get(0)).length());
        }
        else {
            // base64 encoded
            this.bytes = this.base64.decoder((StringBuffer)this.rFields.get(0),
                              4, ((StringBuffer)this.rFields.get(0)).length());
            this.entryDN = new String(this.bytes, "UTF-8");
        }

        if ( !isRequest() ) {
            toLDAPEntry();
        }
        else {
            index = 10; // length of 'changetype'
            // cNumber+1 references to changetype field
            if( ! ((StringBuffer)this.rFields.get(this.cNumber+1))
                        .substring(0, index).equalsIgnoreCase("changetype")){
                throw new RuntimeException("com.novell.ldap.ldif_dsml."
                                       + "LDIFReader: mal-formatted record.");
            }

            req=((StringBuffer)this.rFields.get(this.cNumber+1)).
                                                            substring(index+1);

            // set request type
            if ( req.equalsIgnoreCase("add") ) {
                this.reqType = LDAPRequest.LDAP_ADD;
            }
            else if ( req.equalsIgnoreCase("delete") ) {
                this.reqType = LDAPRequest.LDAP_DELETE;
            }
            else if ( req.equalsIgnoreCase("modrdn") ) {
                this.reqType = LDAPRequest.LDAP_MODDN;
            }
            else if ( req.equalsIgnoreCase("moddn") ) {
                this.reqType = LDAPRequest.LDAP_MODDN;
            }
            else if ( req.equalsIgnoreCase("modify") ) {
                this.reqType = LDAPRequest.LDAP_MODIFY;
            }
            else {
                throw new RuntimeException("com.novell.ldap.ldif_dsml."
                                + "LDIFReader: unsupported change operation");
            }

            switch(this.reqType) {
                case(LDAPRequest.LDAP_ADD):
                    toLDAPEntry();
                    break;
                case(LDAPRequest.LDAP_DELETE):
                    break;
                case(LDAPRequest.LDAP_MODDN):
                    toModInfo();
                    break;
                case(LDAPRequest.LDAP_MODIFY):
                    toLDAPModifications();
                    break;
                default:
                    throw new RuntimeException("com.novell.ldap.ldif_dsml."
                                    + "LDIFReader: unsupported LDAP Request");
            }

            if ( this.hasControls ) { // a request with controls
                this.controls = new LDAPControl[this.cNumber];

                // loop to parse control fields and build control objects
                for (i=1; i<=this.cNumber; i++) {
                    // get control value
                    index = IndexOf((StringBuffer)this.rFields.get(i),(int)':');
                    if (index != -1) {  // has control value
                        if ( ((StringBuffer)this.rFields.get(i)).
                                                 charAt(index+1) == (int)':') {
                            // base64 encoded
                            this.bytes = this.base64.decoder(
                                (StringBuffer)(this.rFields.get(i)), index+2,
                                  ((StringBuffer)this.rFields.get(i)).length());
                            // trim the control field
                            ((StringBuffer)this.rFields.get(i)).delete(index,
                                ((StringBuffer)this.rFields.get(i)).length());
                        }
                        else {  // commom value
                            this.bytes = ((StringBuffer)this.rFields.
                                        get(i)).substring(index+1).getBytes();
                            // trim the control field
                            ((StringBuffer)this.rFields.get(i)).delete(index,
                                ((StringBuffer)this.rFields.get(i)).length());
                        }
                    }
                    else {  // no control value
                        this.bytes = new byte[0];
                    }

                    // get criticality
                    index = IndexOf((StringBuffer)this.rFields.get(i),(int)' ');
                    if (index != -1) {  // has criticality
                        if((((StringBuffer)this.rFields.get(i)).
                                substring(index+1)).equalsIgnoreCase("true")) {
                            criticality = true;
                        }
                        else if(((StringBuffer)this.rFields.get(i)).
                                 substring(index+1).equalsIgnoreCase("false")){
                            criticality = false;
                        }
                        else {
                            throw new RuntimeException("com.novell.ldap."
                                + "ldif_dsml.LDIFReader: not a boolean value");
                        }
                        // trim control field
                        ((StringBuffer)this.rFields.get(i)).delete(index,
                                ((StringBuffer)this.rFields.get(i)).length());
                    }
                    else {  // no criticality
                        criticality = false;
                    }

                    // initialize the control object
                    this.controls[i-1] = new LDAPControl(
                        ((StringBuffer)this.rFields.get(i)).toString(),  // OID
                                                 criticality,  this.bytes);
                }
            }
        }
    }


    /**
     * Process LDIF record fields to generate LDAPEntry.
     */
    private void toLDAPEntry() {

        int i, index;
        int startIndex;
        boolean isEncoded = false;
        String attrName = null, attrValue = null;
        LDAPAttributeSet attrSet = new LDAPAttributeSet();

        if ( !isRequest() ) { // skip dn field
            startIndex = 1;
        }
        else { // skip dn, control, and changetype fields
            startIndex = this.cNumber+2;
        }

        for ( i=startIndex; i<this.fNumber; i++) {
            // ':' separates attr name and attr value
            index = IndexOf((StringBuffer)this.rFields.get(i), (int)':');
            if (index == -1) { // ':' not found
                throw new RuntimeException("com.novell.ldap.ldif_dsml."
                               + "LDIFReader: mal-formatted attribute field");
            }
            else {
                // get attribute name
                attrName=((StringBuffer)this.rFields.get(i)).substring(0,index);
                // get attribute value
                if ((((StringBuffer)this.rFields.get(i)).charAt(index+1)!=
                     (int)':') && (((StringBuffer)this.rFields.get(i)).
                                                   charAt(index+1)!=(int)'<')){
                    // common string value
                    attrValue = ((StringBuffer)this.rFields.get(i)).substring(
                        index+1, ((StringBuffer)this.rFields.get(i)).length());
                }
                else if (((StringBuffer)this.rFields.get(i)).charAt(index+1)
                                                                   ==(int)'<'){
                    // a file URL, we are not ready to handle this yet
                    attrValue = ((StringBuffer)this.rFields.get(i)).substring(
                        index+2, ((StringBuffer)this.rFields.get(i)).length());
                }
                else {  // base64 encoded value
                    isEncoded = true;
                    // decode the value
                    this.bytes = this.base64.decoder((StringBuffer)this.rFields.
                                 get(i), index+2,
                                 ((StringBuffer)this.rFields.get(i)).length());
                }

                if ( attrSet.getAttribute(attrName) == null ){ // new attr
                    if ( isEncoded ) {  // add it with decoded bytes
                        attrSet.add(new LDAPAttribute(attrName,this.bytes));
                    }
                    else { // add it with string value
                        attrSet.add(new LDAPAttribute(attrName, attrValue));
                    }
                }
                else {
                    // an existing attr, add value to it
                    if ( isEncoded ) {
                        attrSet.getAttribute(attrName).addValue(this.bytes);
                    }
                    else {
                        attrSet.getAttribute(attrName).addValue(attrValue);
                    }
                }
            }
            isEncoded = false;
            // construct the currentEntry
            this.currentEntry = new LDAPEntry(this.entryDN, attrSet);
        }
    }


    /**
     * Build String array object that contains moddn information.
     *
     * @return mods String array object that holds newRDN, deleteOldRDN,
     * and newSuperior
     */
    public void toModInfo() throws UnsupportedEncodingException {

        int index, len;
        int fieldIndex; // points to ModDN info
        boolean delOldRDN;
        this.modInfo = new String[3];

        fieldIndex = this.cNumber + 2; // reference newrdn field
        index = 6;                           // length of "newrdn"

        if( ! ((StringBuffer)this.rFields.get(fieldIndex)).
                            substring(0, index).equalsIgnoreCase("newrdn")) {
             throw new RuntimeException("com.novell.ldap.ldif_dsml.LDIFReader:"
                                              + " mal-formatted newrdn field");
        }

        // get newrdn
        if ( (((StringBuffer)this.rFields.get(fieldIndex)).charAt(index+1))
                                                                 != (int)':') {
            // common string value
            this.modInfo[0] = ((StringBuffer)this.rFields.get(fieldIndex))
                                                             .substring(index+1);
        }
        else {
            // decode newrdn
            this.bytes = this.base64.decoder(
                         (StringBuffer)this.rFields.get(fieldIndex),
                         index+2,
                         ((StringBuffer)this.rFields.get(fieldIndex)).length());
            this.modInfo[0] = new String(this.bytes);
        }

        fieldIndex++;   // reference deleteOleRDN field
        index = 12;     // length of "deleteoldrdn"

        if( ! ((StringBuffer)this.rFields.get(fieldIndex)).substring(0, index).
                                           equalsIgnoreCase("deleteoldrdn") ) {
             throw new RuntimeException("com.novell.ldap.ldif_dsml.LDIFReader:"
                                        + " mal-formatted deleteoldrdn field");
        }

        if ( ((StringBuffer)this.rFields.get(fieldIndex)).charAt(index+1)
                                                                == (int)'1' ) {
            this.modInfo[1] = new String("1");
        }
        else {
            this.modInfo[1] = new String("0");
        }

        fieldIndex++;   // reference newsuperior field
        if (fieldIndex == this.fNumber) { // no newsuperior spefified
            this.modInfo[2] = new String("");
        }
        else { // there is a newsuperior
            index = 11;   // length of "newsuperior"
            if( ! ((StringBuffer)this.rFields.get(fieldIndex)).
                         substring(0, index).equalsIgnoreCase("newsuperior")) {
                throw new RuntimeException("com.novell.ldap.ldif_dsml."
                                + "LDIFReader: malformated newsuperior field");
            }

            if ( (((StringBuffer)this.rFields.get(fieldIndex)).charAt(index+1))
                                                                 != (int)':') {
                // commom string value
                this.modInfo[2] = ((StringBuffer)this.rFields.get(fieldIndex))
                                                           .substring(index+1);
            }
            else {
                // base64 encoded value
                this.bytes = this.base64.decoder(
                        (StringBuffer)this.rFields.get(fieldIndex),
                        index+2,
                        ((StringBuffer)this.rFields.get(fieldIndex)).length());
                this.modInfo[2] = new String(this.bytes);;
            }
        }
    }

    /**
     * Build LDAPModification array based on the content of LDIF modify record.
     *
     * @return LDAPModification array.
     */
    public void toLDAPModifications () throws IOException {

        int        i, index;
        int        j;                       // number of attrs for an Request
        int        startIndex;              // where to find mod Requests
        String     attrName, opName;
        LDAPAttribute attr = null;
        ArrayList modList = new ArrayList();

        // skip dn, control, and changetype field
        startIndex = this.cNumber + 2;

        // populate the LDAPModification array object
        for (i=startIndex; i<this.fNumber; i+=j+1) {
            // fined ':' that separate mod operation and attr anme
            index = IndexOf((StringBuffer)this.rFields.get(i), (int)':');
            if (index == -1) { // ':' not found
                throw new RuntimeException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: Mal-formatted attribute field");
            }
            else {
                j=1;
                opName = ((StringBuffer)this.rFields.get(i)).substring(0, index);
                attrName = ((StringBuffer)this.rFields.get(i)).substring(index+1);

                // build each LDAPModification object and add it to modList
                if (((StringBuffer)this.rFields.get(i+1)).charAt(0)!=(int)'-') {
                    // there is at least one attribute value specified
                    while (((StringBuffer)this.rFields.get(i+j)).charAt(0) !=
                                                                    (int)'-') {
                        // index separate attr name and attr value
                        index=IndexOf((StringBuffer)this.rFields.get(i+j),
                                                                     (int)':');
                        attr = new LDAPAttribute( attrName, ((StringBuffer)this.
                                        rFields.get(i+j)).substring(index+1));

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
                            throw new RuntimeException("com.novell.ldap."
                                   + "ldif_dsml.LDIFReader :"
                                        + "Not supported modify request");
                        }
                        j++;
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
                        throw new IOException("com.novell.ldap.ldif_dsml."
                            + "LDIFReader: No attribute value specified");
                    }
                }
            }
        }

        this.mods = new LDAPModification[modList.size()];
        this.mods = (LDAPModification[])modList.toArray(this.mods);
    }

    /**
     * Returns the index within this StringBuffer object
     * of the first occurence of the specified char.
     *
     * @param bl  The StringBuffer object
     * @param c   The character to look for in the StringBuffer object
     *
     * @return The index of the first occurence of the character in the
     * StringBuffer object, or -1 if the character does not occur.
     */
    private int IndexOf(StringBuffer bl, int ch) {

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
     * Returns the index within this StringBuffer object
     * of the first occurence of the specified character,
     * starting at tje specified index.
     *
     * @param bl  The StringBuffer object
     * @param ch  The character to look for in the StringBuffer object
     *
     * @return The index of the first occurence of the character in the
     * StringBuffer object, starting at the specified idex.
     * -1 is returned if the character does not occur.
     */
    private int IndexOf(StringBuffer bl, int ch, int si) {
        if (bl != null && si<bl.length()) {
            for (int i=si;i<bl.length(); i++) {
                if(bl.charAt(i) == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index within this StringBuffer object
     * of the last occurence of the specified char.
     *
     * @param bl  The StringBuffer object
     * @param ch  The character to look for in the StringBuffer object
     *
     * @return The index of the last occurence of the character in the
     * StringBuffer object, or -1 if the character does not occur.
     */
    private int LastIndexOf(StringBuffer bl, int ch ) {
        if (bl != null) {
            for (int i=bl.length()-1; i>=0; i--) {
                if(bl.charAt(i) == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * <tt>trimField<tt> trims off extra spaces in a field.
     *
     * <p><tt>trimField<tt> trims off the extra spaces between
     * colon(s) and value spec and trailing spaces. It
     * does not trim off the other spaces in control
     * fields.</p>
     */
    private void trimField() {

        int index;

        if (this.bLine != null) {
            if ( (index = IndexOf(this.bLine, (int)':')) != -1) {
                if (this.bLine.charAt(index+1) == (int)':') {
                    index+=2;
                }
                else {
                    index++;
                }
                // remove any spaces bebetween colos(s) and value
                while( this.bLine.charAt(index) == (int)' ' ) {
                    this.bLine.delete(index, index+1);
                }
            }

            // remove any trailing spaces
            int len = bLine.length();
            while(bLine.charAt(len-1) == (int)' ') {
                bLine.delete(len-1, len);
                len = bLine.length();
            }
        }
    }

    /**
     * <tt>trimControlField<tt> trims off the extra spaces in a control field.
     *
     * <p><tt>trimControlField<tt> trims off the spaces between colon(s)
     * and value and the spaces between OID and criticality.</p>
     */
    private void trimControlField() {

        int index;

        if (this.bLine != null) {
            // remove 'control' from control field
            this.bLine.delete(0, 8);

            // remove extra spaces between colon(s) and control value
            if ( (index = LastIndexOf(this.bLine, (int)':')) != -1) {
                while( this.bLine.charAt(index+1) == (int)' ' ) {
                    this.bLine.delete(index+1, index+2);
                }
            }

            // remove extra spaces between contorl OID and criticality
            if ( (index = IndexOf(this.bLine, (int)' ')) != -1) {
                while( this.bLine.charAt(index+1) == (int)' ' ) {
                    this.bLine.delete(index+1, index+2);
                }
            }
            // if no cirticality, remove the remaining space before colon
            if (this.bLine.charAt(index+1) == (int)':') {
                this.bLine.delete(index, index+1);
            }
        }
    }
}
