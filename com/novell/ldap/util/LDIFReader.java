/* **************************************************************************
 * $Novell: LDIFReader.java,v 1.26 2002/10/15 18:16:18 $
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
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;
import com.novell.ldap.ldif_dsml.LDAPAdd;
import com.novell.ldap.ldif_dsml.LDAPDelete;
import com.novell.ldap.ldif_dsml.LDAPModDN;
import com.novell.ldap.ldif_dsml.LDAPModify;

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
    private LDAPRequest        currentRequest = null;

    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isRequest,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The InputStream object to be processed by LDIFReader
     */
    public LDIFReader( InputStream in ) throws IOException, LDAPLocalException {
        this( in, 1 );
    }

    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isRequest,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     * @param version  The version currently used in the LDIF file
     */
    public LDIFReader( InputStream in, int version )
    throws IOException, LDAPLocalException {
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
    throws IOException, LDAPLocalException {

        super();

        String line = null;

        if ( version != 1 ) {  // check LDIF file version
            throw new RuntimeException("com.novell.ldap.ldif_dsml.LDIFReader:"
                              + "found: " + version + ", Should be: 1");
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
            this.lNumber++;
        }

        // already reaches the end of file
        if ( line == null ) {
            //throw new RuntimeException( "com.novell.ldap.ldif_dsml.LDIFReader:"
            //          + " The file contains no LDIF info" ) ;
            throw new LDAPLocalException(
                "com.novell.ldap.ldif_dsml.LDIFReader:"
                    + " The file contains no LDIF info",
                        LDAPException.LOCAL_ERROR);
        }

        // need to increase line number
        this.lNumber++;

        // the first effective line(the version line). check the version line
        if (line.startsWith("version:")) {
            this.version = Integer.parseInt(
                line.substring("version:".length()).trim() );
            if ( this.version != 1 ) {
                throw new LDAPLocalException(
                    "com.novell.ldap.ldif_dsml.LDIFReader: "
                        + "version: found '" + this.version + "' (on line "
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
            if (    !line.startsWith(" ")    // ! a part of dn field
                 && !line.startsWith("#") ){ // ! a comment line
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
    }


    /**
     * Read the records from the LDIF content file.
     *
     * @return The LDAPEntry object represented by the LDIF content record.
     */
    public LDAPEntry readNextEntry()
    throws UnsupportedEncodingException, IOException, LDAPLocalException {

        if( isRequest()) {
            throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
                              + " Cannot read entry from LDIF change file",
                                  LDAPException.LOCAL_ERROR);
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
    throws UnsupportedEncodingException, IOException, LDAPLocalException {

        if( !isRequest()) {
            throw new LDAPLocalException("Cannot read requests from LDIF"
                + " content file", LDAPException.LOCAL_ERROR);
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
    private void  readRecordFields() throws IOException, LDAPLocalException {

        String line;
        StringBuffer bLine = new StringBuffer();
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

            // read the rest lines of the record except comment lines.
            // read stops at an empty line which is used to separate
            // the current record with the next
            while ((line = bufReader.readLine())!=null && line.length()!=0 ) {
                if ( !line.startsWith("#") ) { // skip comment line
                    if ( line.startsWith(" ") ) { // continuation line
                        // trim off leading ' ' and append it to previous line
                        bLine.append(line.substring(1, line.length()));
                    }
                    else { // a new line:
                        // handle previous field
                        bLine = trimField(bLine);  // trime the previous field
                        if(!this.control) {
                            this.rFields.add(bLine); // save previous field
                        }
                        // handle new line
                        bLine = new StringBuffer();
                        bLine.append(line);
                    }
                }
                this.lNumber++;
            }
            // save the last field (assuming it's not a control field)
            bLine = trimField(bLine);                             // trim the last field
            this.rFields.add(bLine);            // save the last field
            this.fNumber = this.rFields.size();  // get number of fields
            this.lNumber++;
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
    private void toRecordProperties() throws IOException, LDAPLocalException {

        int index;
        String req;

        // set entry DN
        if ( (((StringBuffer)this.rFields.get(0)).charAt(3)) != ':') {
            // commom string value
            this.entryDN = ((StringBuffer)this.rFields.get(0)).substring( 3,
                                 ((StringBuffer)this.rFields.get(0)).length());
        }
        else {
            // base64 encoded
            this.bytes = Base64.decode((StringBuffer)this.rFields.get(0),
                              4, ((StringBuffer)this.rFields.get(0)).length());
            this.entryDN = new String(this.bytes, "UTF-8");
        }

        if ( !isRequest() ) {
            toLDAPEntry();
        }
        else {
            index = 10; // length of 'changetype'
            // 1 references to changetype field
            if( ! ((StringBuffer)this.rFields.get(1))
                        .substring(0, index).equalsIgnoreCase("changetype")){
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    +"LDIFReader: malformed changetype field in record starting"
                        + " on line " + this.dnlNumber + " of the file).",
                            LDAPException.LOCAL_ERROR);
            }

            req = ((StringBuffer)this.rFields.get(1)).substring(index+1);

            // set request type
            if ( req.equalsIgnoreCase("add") ) {
                this.reqType = LDAPRequest.LDAP_ADD;
                toLDAPEntry();
            }
            else if ( req.equalsIgnoreCase("delete") ) {
                this.reqType = LDAPRequest.LDAP_DELETE;
            }
            else if ( req.equalsIgnoreCase("modrdn") ) {
                this.reqType = LDAPRequest.LDAP_MODDN;
                toModInfo();
            }
            else if ( req.equalsIgnoreCase("moddn") ) {
                this.reqType = LDAPRequest.LDAP_MODDN;
                toModInfo();
            }
            else if ( req.equalsIgnoreCase("modify") ) {
                this.reqType = LDAPRequest.LDAP_MODIFY;
                toLDAPModifications();
            }
            else {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: unsupported change operation specified in "
                        + "changetype filed of the record starting on line "
                            + this.dnlNumber + " of the file.",
                                LDAPException.LOCAL_ERROR);
            }


            if (this.cList.size() > 0) {
                this.controls = new LDAPControl[this.cList.size()];
            }
        }
    }


    /**
     * Process LDIF record fields to generate LDAPEntry.
     */
    private void toLDAPEntry() throws LDAPLocalException {

        int i, index;
        int startIndex;
        boolean isEncoded = false;
        String attrName = null, attrValue = null;
        LDAPAttributeSet attrSet = new LDAPAttributeSet();

        if ( !isRequest() ) { // skip dn field
            startIndex = 1;
        }
        else { // skip dn, control, and changetype fields
            startIndex = 2;
        }

        for ( i=startIndex; i<this.fNumber; i++) {
            // ':' separates attr name and attr value
            index = IndexOf((StringBuffer)this.rFields.get(i), ':');
            if (index == -1) { // ':' not found
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: malformed attribute field in record "
                        + "starting on line " + this.dnlNumber + " of the file.",
                            LDAPException.LOCAL_ERROR);
            }
            else {
                // get attribute name
                attrName=((StringBuffer)this.rFields.get(i)).substring(0,index);
                // get attribute value
                if ((((StringBuffer)this.rFields.get(i)).charAt(index+1)!=
                     ':') && (((StringBuffer)this.rFields.get(i)).
                                                   charAt(index+1)!='<')){
                    // common string value
                    attrValue = ((StringBuffer)this.rFields.get(i)).substring(
                        index+1, ((StringBuffer)this.rFields.get(i)).length());
                }
                else if (((StringBuffer)this.rFields.get(i)).charAt(index+1)
                                                                   =='<'){
                    // a file URL, we are not ready to handle this yet
                    attrValue = ((StringBuffer)this.rFields.get(i)).substring(
                        index+2, ((StringBuffer)this.rFields.get(i)).length());
                }
                else {  // base64 encoded value
                    isEncoded = true;
                    // decode the value
                    this.bytes = Base64.decode((StringBuffer)this.rFields.
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
     */
    public void toModInfo()
    throws UnsupportedEncodingException, LDAPLocalException {

        int index;
        int fieldIndex; // points to ModDN info
        this.modInfo = new String[3];

        fieldIndex = 2; // reference newrdn field
        index = 6;      // length of "newrdn"

        if( ! ((StringBuffer)this.rFields.get(fieldIndex)).substring(0, index+1).
                                                  equalsIgnoreCase("newrdn:")) {
             throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
                 + " malformed newrdn field in record starting on line "
                     + this.dnlNumber + " of the file.",
                         LDAPException.LOCAL_ERROR);
        }

        // get newrdn
        if ( (((StringBuffer)this.rFields.get(fieldIndex)).charAt(index+1))
                                                                 != ':') {
            // common string value
            this.modInfo[0] = ((StringBuffer)this.rFields.get(fieldIndex))
                                                            .substring(index+1);
        }
        else {
            // decode newrdn
            this.bytes = Base64.decode(
                         (StringBuffer)this.rFields.get(fieldIndex),
                         index+2,
                         ((StringBuffer)this.rFields.get(fieldIndex)).length());
            this.modInfo[0] = new String(this.bytes);
        }

        fieldIndex++;   // reference deleteOleRDN field
        index = 12;     // length of "deleteoldrdn"

        if( ! ((StringBuffer)this.rFields.get(fieldIndex)).substring(0, index+1).
                                           equalsIgnoreCase("deleteoldrdn:") ) {
            throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
                + " malformed deleteoldrdn field in record starting on line "
                    + this.dnlNumber + " of the file.",
                         LDAPException.LOCAL_ERROR);
        }

        if ( ((StringBuffer)this.rFields.get(fieldIndex)).charAt(index+1)
                                                                == '1' ) {
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
                         substring(0, index+1).equalsIgnoreCase("newsuperior:")) {
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml.LDIFReader:"
                + " malformed newsuperior field in record starting on line "
                    + this.dnlNumber + " of the file.",
                         LDAPException.LOCAL_ERROR);
            }

            if ( (((StringBuffer)this.rFields.get(fieldIndex)).charAt(index+1))
                                                                 != ':') {
                // commom string value
                this.modInfo[2] = ((StringBuffer)this.rFields.get(fieldIndex))
                                                           .substring(index+1);
            }
            else {
                // base64 encoded value
                this.bytes = Base64.decode(
                        (StringBuffer)this.rFields.get(fieldIndex),
                        index+2,
                        ((StringBuffer)this.rFields.get(fieldIndex)).length());
                this.modInfo[2] = new String(this.bytes);;
            }
        }
    }

    /**
     * Build LDAPModification array based on the content of LDIF modify record.
     */
    public void toLDAPModifications () throws IOException, LDAPLocalException {

        int        i, index;
        int        j;                       // number of attrs for an Request
        int        startIndex;              // where to find mod Requests
        String     attrName, opName;
        LDAPAttribute attr = null;
        ArrayList modList = new ArrayList();

        // skip dn, control, and changetype field
        startIndex = 2;

        // populate the LDAPModification array object
        for (i=startIndex; i<this.fNumber; i+=j+1) {
            // fined ':' that separate mod operation and attr anme
            index = IndexOf((StringBuffer)this.rFields.get(i), ':');
            if (index == -1) { // ':' not found
                throw new RuntimeException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: Mal-formatted attribute field");
            }
            else {
                j=1;
                opName=((StringBuffer)this.rFields.get(i)).substring(0, index);
                attrName=((StringBuffer)this.rFields.get(i)).substring(index+1);

                // build each LDAPModification object and add it to modList
                if (((StringBuffer)this.rFields.get(i+1)).charAt(0)!='-') {
                    // there is at least one attribute value specified
                    while (((StringBuffer)this.rFields.get(i+j)).charAt(0) !=
                                                                    '-') {
                        // index separate attr name and attr value
                        index=IndexOf((StringBuffer)this.rFields.get(i+j),
                                                                     ':');
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
                            throw new LDAPLocalException("com.novell.ldap."
                                + "ldif_dsml.LDIFReader : Not supported modify "
                                    + " request (" + opName + ") specified in record starting "
                                        + "on line " + this.dnlNumber + " of the file.",
                                            LDAPException.LOCAL_ERROR);
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
                        throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                            + "LDIFReader: For '" + opName + "', no value "
                                + "specified for atribute '" + attrName
                                    + "' in the record starting on line "
                                        + this.dnlNumber + " of the file.",
                                             LDAPException.LOCAL_ERROR);
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
     * @param ch   The character to look for in the StringBuffer object
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
     * <tt>trimField<tt> trims off extra spaces in a field. It also
     * trims confield and constructs control onjects.
     */
    public StringBuffer trimField( StringBuffer line)
    throws UnsupportedEncodingException, LDAPLocalException  {
        int c, lastChar = 0, charIndex = 0;
        char[] newChars;
        boolean isEncoded=false, isURL=false;
        char t;
        String oid = null;
        boolean criticality = false;

        if ((line == null)||((c=IndexOf(line,':'))==-1)) {
            // some fields contain no ':'
            return line;
        }

        // elminate any trailing spaces
        lastChar = line.length() - 1;
        newChars = new char[lastChar + 1 + 7]; // allow room for criticality
        while( line.charAt(lastChar) == ' ') {
            lastChar--;
        }

        if( (c > 6) && (line.substring(0,c).equals("control"))) {
            this.control = true;
            c++;            // skip past ':'
            // eliminate any spaces after :
            while( (c <= lastChar) && (line.charAt(c) == ' ')) {
                c++;
            }
        }
        else {
            this.control = false;
            // copy attrname and ':'
            line.getChars(0, c+1, newChars, 0);
            charIndex += c + 1;
            c++;
        }

        if( this.control) {
            // Process special values for controls
            // determine length of the oid
            int b = c;
            while( b <= lastChar) {
                // an oid consists of '.'s and digits
                t = line.charAt(c);
                if( (t == '.') || (Character.isDigit(t))) {
                    c++;
                    continue;
                }
                break;
            }

            if( b == c) {
                // control with no oid
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: Control with no oid in the record "
                        + "starting on line " + this.dnlNumber
                        + " of the file.", LDAPException.LOCAL_ERROR);
            }

            // has oid, copy it
            line.getChars(b, c, newChars, charIndex); // not used after return
            // get local copy of oid
            char[] chars = new char[c-b];
            line.getChars(b, c, chars, 0);
            oid = new String(chars);

            // increase charIndex for next copy
            charIndex += c - b;

            // check if character after oid is end, space, or ':'
            if( c <= lastChar) {
                t = line.charAt(c);
                if( t == ' ') {
                    // skip over spaces
                    while( (c <= lastChar) && (line.charAt(c) == ' ')) {
                        c++;
                    }
                }
                if(((c + 3) <= lastChar) && (line.substring(c,c+4).
                                                             equals("true"))) {
                    // found 'true', copy to buffer
                    line.getChars(c-1, c+4, newChars, charIndex);
                    c += 4;
                    charIndex += 5;
                    criticality = true;
                }
                else if(((c + 4) <= lastChar) && (line.substring(c,c+5).
                                                            equals("false"))) {
                    // found 'false', copy to buffer
                    line.getChars(c-1, c+5, newChars, charIndex);
                    c += 5;
                    charIndex += 6;
                    criticality = false;
                }
                else {
                    if( (t = line.charAt(c)) == ':') {
                        // found colon(no criticality),
                        // copy default of 'false' to buffer
                        " false".getChars(0, 6, newChars, charIndex);
                        charIndex += 6;
                        criticality = false;
                    }
                    else {
                        // see control value with no leading ':'
                        throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                            + "LDIFReader: Control value with no leading colon "
                                + "in the record starting on line "
                                    + this.dnlNumber + " of the file.",
                                        LDAPException.LOCAL_ERROR);
                    }
                }

                // If we have a ':', we must include it
                if( (c <= lastChar) && (line.charAt(c) == ':')) {
                    // found colon add to buffer
                    newChars[charIndex++] = ':';
                    c++;
                }
            }
            else {
                // OID only, add false
                " false".getChars(0, 6, newChars, charIndex);
                charIndex += 6;
                criticality = false;
            }
        }

        // Positioned at value
        // Check if :: or :<
        if( c <= lastChar) {
            t = line.charAt(c);
            if( t == ':') {
                newChars[charIndex++] = ':';
                isEncoded = true;
                c++;
            }
            else if( t == '<') {
                newChars[charIndex++] = '<';
                isURL = true;
                c++;
            }
        }

        // eliminate any spaces after : or <
        while( (c <= lastChar) && (line.charAt(c) == ' ')) {
            c++;
        }

        if( c <= lastChar) {  // there is a value spec specified
            line.getChars(c, lastChar+1, newChars, charIndex);
            charIndex += lastChar - c + 1;

            if(this.control) {
                char[] chars = new char[lastChar+1-c];
                line.getChars(c, lastChar+1, chars, 0);
                //charIndex += lastChar - c + 1;

                //if(this.control) {
                if (isEncoded) {
                    this.bytes = Base64.decode(chars);
                }
                else {
                    // if isURL, what to do?
                    this.bytes = (new String(chars)).getBytes();
                }
            }
        }
        else {  // there is no value spec specified
            if (this.control) {  // for control field, it's ok
                this.bytes = new byte[0];
            }
            else {  // for other field, it's not ok
                throw new LDAPLocalException("com.novell.ldap.ldif_dsml."
                    + "LDIFReader: a field contains no value after ':'. the "
                        + "field is in the record starting on line "
                            + this.dnlNumber + " of the file.",
                                        LDAPException.LOCAL_ERROR);
            }

        }

        if (this.control) {
            LDAPControl ctrl = new LDAPControl(oid, criticality, this.bytes);
                    this.cList.add(ctrl);
        }
        StringBuffer newBuf = new StringBuffer( lastChar + 1);
        newBuf.append( newChars, 0, charIndex);
        return newBuf;
    }
}
