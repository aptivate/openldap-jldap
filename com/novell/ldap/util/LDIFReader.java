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
import com.novell.ldap.LDAPException;
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
 * <p>This calss reads LDAP entries and LDAP operations form an LDIF file</p>
 *
 * <p>The constructors uses a default size value of 8,192 to create the
 *    buffering character-input stream and assume that the size is big
 *    enough to hold the dn field and the first line of the next field
 *    of the first record in the LDIF file currently being read.</p>
 *
 * <p>The constructors uses '1' as default LDIF file version</p>
 */
public class LDIFReader extends LDIF implements LDAPImport {

    private int                version;                   // LDIF file version
    private int                operationType;             //
    private int                controlNumber = 0;         // number of controls
    private int                fieldNumber;               // number of fields
    private String             entryDN;                   // entry dn
    private String             deleteDN;                  // delete dn
    private String             changeField;               // record change field
    private String             changeOperation;           //
    private String             line;                      // single line read
    private String[]           modInfo;
    private StringBuffer       bLine = null;              // buffer lines erad
    private byte[]             byteValue = new byte[0];   // for any bytes
    private boolean            hasControls = false;       //
    private boolean            isBase64Encoded;           //
    private boolean            criticality;               // control criticaliry
    private ArrayList          rFields = new ArrayList(); // record lines/fields
    private BufferedReader     bufReader;
    private LDAPControl[]      controls = null;           // controls of the op
    private LDAPAttributeSet   attrSet = null;
    private LDAPEntry          currentEntry = null;
    private LDAPModification[] mods;
    private LDAPRequest        currentRequest = null;
    private Base64Decoder      base64Decoder = new Base64Decoder();


    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isContent,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     */
    public LDIFReader( InputStream in ) throws IOException {
        this( in, 1 );
    }

    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isContent,
     * InputStreamReader, and BufferedReader.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     * @param version  The version currently used in the LDIF file
     */
    public LDIFReader( InputStream in, int version ) throws IOException {
        this(in, version, 8192 );
    }
    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isContent,
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

        // check LDIF file version
        if ( version != 1 ) {
            throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "Should be version 1");
        }

        super.setVersion( version );
        InputStreamReader isr = new InputStreamReader(in, "US-ASCII");
        bufReader = new BufferedReader(isr);

        // In order to determine if it is a LDIF content file or LDIF change
        // file, the first line of dn field and the meaningful line next to
        // dn field are read into memory.

        // skip the leading empty and comment lines before version line
        while( (this.line = bufReader.readLine())!= null &&
               (this.line.length() == 0 || this.line.startsWith("#")) ) {
        }

        // already reaches the end of file
        if ( line == null ) {
             throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                       + "The file contains no LDIF info" ) ;
        }

        // the first effective line(the version line). check the version line
        if (this.line.startsWith("version:")) {
            this.version = Integer.parseInt(
                this.line.substring("version:".length()).trim() );
            if ( this.version != 1 ) {
                throw new RuntimeException(
                   "com.novell.ldap.ldif_dsml.LDIFReader: "
                     + "version: found '" + this.version + "', should be '1'");
            }
        }
        else {
            // first effective line is not a version line
            throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                       + "Version line must be the first meaningful line" );
        }

        // skip any empty and comment lines between the version line and
        // the first line of the dn field in the first record of the LDIF
        // file, read the first line of the dn field of the first record
        do {
            // mark the first dn line, so we can later go back to here
            bufReader.mark( bufSize );
            this.line=bufReader.readLine();

            // end of file ?
            if ( this.line == null) {
                throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "the LDIF file only contains version line");
            }

        } while((this.line.length()== 0) || this.line.startsWith("#"));

        // this is the first line of the dn field. it should starts with 'dn'
        if ( !this.line.startsWith("dn")) {
                throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "a record should starts with dn");
        }

        // ignore the rest lines of the dn field and read the
        // effective line right after the dn field
        while ( (this.line = bufReader.readLine()) != null ) {

            if (    !this.line.startsWith(" ")    // ! a part of dn field
                 && !this.line.startsWith("#") ){ // ! a comment line

                 // to the end of the first record
                 if ( this.line.length() == 0 ) {
                    // an empty line; this record only has dn field
                    throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "the record only has dn field");
                 }

                 // the line just read should be the line that starts with
                 // either 'control', 'changetype', or an attribute name.
                 break;
            }
        }

        // end of file ?
        if ( this.line == null) {
                throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "the LDIF file is not well formated");
        }

        // check and set the type of the current LDIF file
        if (   this.line.startsWith("changetype")
            || this.line.startsWith("control") ) {
            setRequest(true);
        }
        else {
            setRequest(false);
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
    public LDAPEntry readContent()
    throws UnsupportedEncodingException, LDAPException, IOException {

        if( isRequest()) {
            throw new RuntimeException(
                "com.novell.ldap.ldif_dsml.LDIFReader: "
                       + "Cannot read content from LDIF change file");
        }

        // read record fields
        this.rFields.clear();
        readRecordFields();

        // end of file ?
        if ( this.rFields == null ) {
            return null;
        }

        // set dn, controls, attributes and ...
        toRecordProperties();

        return this.currentEntry;
    }


    /**
     * Read the LDAP operations from the LDIF change file.
     *
     * @see LDAPOperation
     */
    public LDAPRequest readOperation()
    throws UnsupportedEncodingException, IOException {

        if( !isRequest()) {
            throw new RuntimeException(
                "Cannot read changes from LDIF content file");
        }

        // read record fields
        this.rFields.clear();
        readRecordFields();

        if ( this.rFields == null ) {
            // end of file
            return null;
        }

        toRecordProperties();

        switch( operationType ) {
            // construct a specific change record object and let the
            // super class object reference it
            case LDAPRequest.LDAP_ADD :
                if (this.controls == null)
                    this.currentRequest = new LDAPAdd(currentEntry);
                else
                    this.currentRequest = new LDAPAdd(currentEntry, controls);
                break;

            case LDAPRequest.LDAP_DELETE :
                if (this.controls == null)
                    this.currentRequest = new LDAPDelete(this.deleteDN);
                else
                    this.currentRequest =
                          new LDAPDelete(this.deleteDN, controls);
                break;

            case LDAPRequest.LDAP_MODDN :
                //String[] modInfo = toModInfo();
                boolean  delOldRdn;

                if ( Integer.parseInt(this.modInfo[1]) == 1 ) {
                    delOldRdn = true;
                }
                else {
                    delOldRdn = false;
                }

                String sup = modInfo[2];
                if (this.controls == null) {
                    if( sup.length() == 0 ) {
                        this.currentRequest = new LDAPModDN(
                                            this.entryDN,
                                            this.modInfo[0],
                                            delOldRdn);
                    }
                    else {
                        this.currentRequest = new LDAPModDN(
                                            this.entryDN,
                                            this.modInfo[0],
                                            delOldRdn,
                                            modInfo[2]);
                    }
                }
                else {
                    if((sup.length())==0 ) {
                        this.currentRequest = new LDAPModDN(
                                            this.entryDN,
                                            this.modInfo[0],
                                            delOldRdn,
                                            controls);
                    }
                    else {
                        this.currentRequest = new LDAPModDN(this.entryDN,
                                            this.modInfo[0],
                                            delOldRdn,
                                            modInfo[2],
                                            controls);
                    }

                }
                break;

            case LDAPRequest.LDAP_MODIFY :

                toLDAPModifications();

                if (this.controls == null) {
                    this.currentRequest = new LDAPModify(this.entryDN, mods);
                }
                else {
                    this.currentRequest =
                          new LDAPModify(this.entryDN, mods, controls);
                }
                break;

            default:
                // unknown change type
                throw new IOException("com.novell.ldap.ldif_dsml."
                                  + "LDIFReader: Unknown change type");
                }

        return this.currentRequest;
    }


    /**
     * Reads all lines in the current record, convert record lines to
     * the record fields, and trim off extra spaces in record fields.
     *
     * @throws IOException.
     */
    private void  readRecordFields() throws IOException {

        int i, j, index, len, fLen;

        //len = this.rFields.size();
        this.rFields.clear();

        // skip empty and comment lines and read the first dn
        // line of the record
        while( (this.line = bufReader.readLine())!= null &&
               (this.line.length() == 0 || this.line.startsWith("#")) ) {
        }

        if (this.line == null) {
            // reaches the end of the LDIF file
            this.rFields = null;
        }
        else {

            // check if the first dn line starts with 'dn:'
            if ( !this.line.startsWith("dn:") ) {
                throw new IOException("com.novell.ldap.ldif_dsml." +
                                      "Any record should start with 'dn:'");
            }

            // add the first line of dn field to this.rFields
            this.bLine = new StringBuffer();
            this.bLine.append(this.line);
            //this.rLines.add(this.pLine);

            // read the rest lines of the record except comment lines.
            // read stops at an empty line which is used to separate
            // the current record with the next one
            while ( ((this.line = bufReader.readLine()) != null)
                    && ( this.line.length() != 0) ) {

                // skip if it's a comment line
                if ( !this.line.startsWith("#") ) {

                    if ( !this.line.startsWith(" ") ) {
                        // not a continuation line:
                        //     1. save bLine
                        //     2. create a cLine
                        this.rFields.add(this.bLine);

                        this.bLine = new StringBuffer();
                        this.bLine.append(this.line);

                         // get controlNumber
                         if (this.line.startsWith("control:")) {
                            this.hasControls = true;
                            this.controlNumber++;
                         }
                    }
                    else {
                        // a continuation line:
                        // trim off the leading ' ' and append it to bLine
                        this.bLine.append(
                                  this.line.substring(1, this.line.length()));
                    }
                }
            }
            // save the last line
            this.rFields.add(this.bLine);

            len = this.rFields.size();

            for ( i=0; i<len; i++ ) {

                // clean the leading spaces between ':' and value, '::'
                // and value, or ':<' and value.
                //
                // find the index for the first colon
                for( j=0; j<((StringBuffer)this.rFields.get(i)).length(); j++) {
                    if( (((StringBuffer)this.rFields.get(i)).charAt(j) ==
                                                                   (int)':')) {
                        break;
                    }
                }

                // j points to the first colon
                if (((((StringBuffer)this.rFields.get(i)).
                                                     charAt(j+1))==(int)':')
                  ||((((StringBuffer)this.rFields.get(i)).
                                                     charAt(j+1))==(int)'<')) {
                    // an base64 encoded or URL value;
                    // remove any spaces between '::' and base64
                    // encoded value or spaces between ':<' and URL
                    while( ((StringBuffer)this.rFields.get(i)).charAt(j+2)
                                                                == (int)' ' ) {
                        ((StringBuffer)this.rFields.get(i)).delete(j+2, j+3);
                    }

                    // if there is a trailing ' ', remove it
                    if ( ((StringBuffer)this.rFields.get(i)).charAt(
                        ((StringBuffer)this.rFields.get(i)).length()-1)
                                                                  ==(int)' ') {
                        ((StringBuffer)this.rFields.get(i)).delete(
                            ((StringBuffer)this.rFields.get(i)).length()-1,
                            ((StringBuffer)this.rFields.get(i)).length() );
                    }
                }
                else {
                    // mormal string value;
                    // remove any spaces between ':' and value
                    while( ((StringBuffer)this.rFields.get(i)).charAt(j+1)
                                                               == (int)' ' ) {
                        ((StringBuffer)this.rFields.get(i)).delete(j+1, j+2);
                    }
                }

                // remove any trailing spaces
                fLen = ((StringBuffer)this.rFields.get(i)).length();
                while(((StringBuffer)this.rFields.get(i)).charAt(fLen-1)
                                                                == (int)' ') {
                    ((StringBuffer)this.rFields.get(i)).delete(fLen-1, fLen);
                    fLen = ((StringBuffer)this.rFields.get(i)).length();
                }
            }

            // get the number of fields in the current record
            this.fieldNumber = this.rFields.size();
        }
    }



    /**
     * set up record dn, attribute names and values, and controls
     *
     */
    private void toRecordProperties() throws IOException {

        int i, j, len, index;
        //boolean criticality;
        String attrName, attrValue=null, controlOID;
        LDAPAttribute attr;

        len = ((StringBuffer)this.rFields.get(0)).length();

        // set entry DN
        if ( (((StringBuffer)this.rFields.get(0)).charAt(3)) != (int)':') {
            // not a base64 encoded entry dn
            this.entryDN = ((StringBuffer)this.rFields.get(0)).substring( 3,
                                 ((StringBuffer)this.rFields.get(0)).length());
        }
        else {
            // a base64 encoded entry dn, tailing space was already removed
            this.byteValue = this.base64Decoder.decoder(
                                 (StringBuffer)this.rFields.get(0),
                                 4,
                                 ((StringBuffer)this.rFields.get(0)).length());
            this.entryDN = new String(this.byteValue, "UTF8");
        }

        if ( !isRequest() ) {

            toLDAPEntry();

        }
        else {
            // a change record with controls
            if ( this.hasControls ) {

                // initialize control array
                this.controls = new LDAPControl[this.controlNumber];

                // change record that has controls
                for (i=1; i<=this.controlNumber; i++) {

                    // there is no leading space in front of contro OID
                    // the loop breaks if it sees a space or a colon
                    for( j=8; // length of 'control:'
                         j < ((StringBuffer)this.rFields.get(i)).length();
                         j++) {
                        if ((((StringBuffer)this.rFields.get(i)).charAt(j)
                                                                == (int)' ')
                        || (((StringBuffer)this.rFields.get(i)).charAt(j)
                                                                == (int)':') ) {
                            break;
                        }
                    }

                    // j points to the end of control OID; get control OID
                    controlOID = ((StringBuffer)this.rFields.get(i)).
                                                               substring(8, j);

                    // skip any spaces or reach the end of the control field
                    while(
                        (j<((StringBuffer)this.rFields.get(i)).length())
                        &&(((StringBuffer)this.rFields.get(i)).charAt(j)
                                                                == (int)' ')) {
                        j++;
                    }

                    if ( j==((StringBuffer)this.rFields.get(i)).length()) {
                        // reach the end of control field, there is no
                        // criticality or value specified. construct a
                        // LDAPControl object with the OID, false, and empty
                        // byte array
                        this.criticality = false;
                        this.byteValue = new byte[0];
                        this.controls[i-1] = new LDAPControl( controlOID,
                                                              this.criticality,
                                                              this.byteValue);
                    }
                    else {
                        // at least criticality or value is specified,
                        // j points to the beginning of cirticality or
                        // value, or beginning of both of them
                        index = j;

                        for ( ; j<((StringBuffer)this.rFields.get(i)).length();
                                                                        j++ ) {
                            if ( ((StringBuffer)this.rFields.get(i)).charAt(j)
                                                                == (int)':' ) {
                                break;
                            }
                        }

                        if ( index == j) {
                        // only value is specified
                        // use false as default ciriticality
                            this.criticality = false;
                            // is the value base64 encoded
                            if ( ((StringBuffer)this.rFields.get(i)).charAt(j+1)
                                                                 == (int)':') {
                                // base64 encoded value, j point to value
                                j+=2;
                                // remove any leading spaces in value
                                while( ((StringBuffer)this.rFields.get(i)).
                                                      charAt(j) == (int)' ' ) {
                                     ((StringBuffer)this.rFields.get(i)).
                                                             delete(j,j+1);
                                }
                                // get base64 encoded value
                                this.byteValue = this.base64Decoder.decoder(
                                  (StringBuffer)this.rFields.get(i),
                                  j,
                                  ((StringBuffer)this.rFields.get(i)).length());

                                this.controls[i-1] = new LDAPControl(
                                                            controlOID,
                                                            this.criticality,
                                                            this.byteValue);
                            }
                            else {
                                // commom value
                                j++;
                                // remove any leading spaces in value
                                while( ((StringBuffer)this.rFields.get(i)).
                                                      charAt(j) == (int)' ' ) {
                                     ((StringBuffer)this.rFields.
                                                         get(i)).delete(j,j+1);
                                }
                                // get control value
                                this.byteValue = ((StringBuffer)this.rFields.
                                             get(i)).substring(j+1).getBytes();
                                this.controls[i-1] =
                                           new LDAPControl( controlOID,
                                                            this.criticality,
                                                            this.byteValue);
                            }
                        }
                        else {
                        // both criticality and value specified. now j
                        // points to ':' before value, no trailing space
                        // between criticality and ':'
                        // get criticality
                            if (((StringBuffer)this.rFields.get(i)).
                                  substring(index,j).equalsIgnoreCase("true")){
                                this.criticality = true;
                            }
                            else if (((StringBuffer)this.rFields.get(i)).
                                substring(index,j).equalsIgnoreCase("false")) {
                                this.criticality = false;
                            }
                            else {
                                throw new RuntimeException(
                                    "com.novell.ldap.ldif_dsml.LDIFReader: "
                                        + "wrong value for criticality");
                            }

                            // get value
                            // is the value base64 encoded
                            if ( ((StringBuffer)this.rFields.get(i)).charAt(j+1)
                                                                 == (int)':') {
                                // base64 encoded value, j point to value
                                j+=2;
                                // remove any leading spaces in value
                                while( ((StringBuffer)this.rFields.get(i)).
                                                      charAt(j) == (int)' ' ) {
                                     ((StringBuffer)this.rFields.get(i)).
                                                                delete(j,j+1);
                                }
                                // get the value
                                this.byteValue = this.base64Decoder.decoder(
                                  (StringBuffer)this.rFields.get(i),
                                  j,
                                  ((StringBuffer)this.rFields.get(i)).length());
                                this.controls[i-1] = new LDAPControl(
                                                               controlOID,
                                                               criticality,
                                                               this.byteValue);

                            }
                            else {
                                // commom value
                                j++;
                                // remove any leading spaces in value
                                while( ((StringBuffer)this.rFields.get(i)).
                                                      charAt(j) == (int)' ' ) {
                                     ((StringBuffer)this.rFields.get(i)).
                                                                delete(j,j+1);
                                }
                                // get the value
                                this.byteValue= ((StringBuffer)this.rFields.
                                               get(i)).substring(j).getBytes();
                                this.controls[i-1] = new LDAPControl(
                                                            controlOID,
                                                            this.criticality,
                                                            this.byteValue);

                            }
                        }
                    }
                }
            }

            setOperationType();

            switch(this.operationType) {

                case(LDAPRequest.LDAP_ADD):
                    toLDAPEntry();
                    break;
                case(LDAPRequest.LDAP_DELETE):
                    toDeleteDN();
                    break;
                case(LDAPRequest.LDAP_MODDN):
                    toModInfo();
                    break;
                case(LDAPRequest.LDAP_MODIFY):
                    toLDAPModifications();
                    break;
                default:
                    throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "unsupported LDAP operation");
            }
        }
    }


    /**
     *
     */
    private void setOperationType( ) {

        int i,index;

        // index points to the first char of change value
        index = 11; // length of 'changetype:'

        // controlNumber+1 points to changetype field
        if(((StringBuffer)this.rFields.get(this.controlNumber+1))
                                                 .charAt(index-1) != (int)':' ){
             throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                     + "malformated controltype field: "
                            + "field " + this.controlNumber+1);
        }

        this.changeOperation = ((StringBuffer)this.rFields.
                                   get(this.controlNumber+1)).substring(index);

        // set operation type
        if ( this.changeOperation.equalsIgnoreCase("add") ) {
            this.operationType = LDAPRequest.LDAP_ADD;
        }
        else if ( this.changeOperation.equalsIgnoreCase("delete") ) {
            this.operationType = LDAPRequest.LDAP_DELETE;
        }
        else if ( this.changeOperation.equalsIgnoreCase("modrdn") ) {
            this.operationType = LDAPRequest.LDAP_MODDN;
        }
        else if ( this.changeOperation.equalsIgnoreCase("moddn") ) {
            this.operationType = LDAPRequest.LDAP_MODDN;
        }
        else if ( this.changeOperation.equalsIgnoreCase("modify") ) {
            this.operationType = LDAPRequest.LDAP_MODIFY;
        }
        else
            throw new RuntimeException(
                  "com.novell.ldap.ldif_dsml.LDIFReader" +
                                      "not supported change operation");
    }


    /**
     * Process LDIF record fields and return LDAPEntry object.
     *
     * @return LDAPEntry object.
     */
    private void toLDAPEntry() {

        int i, len;
        int colon;            // index to first colon in a field
        int startIndex;       // index points to the first attribute field
        String attrName = null, attrValue = null;

        len = this.fieldNumber;
        this.attrSet = new LDAPAttributeSet();

        if ( !isRequest() ) {
            startIndex = 1;
        }
        else {
            // skip dn, control, and changetype fields
            startIndex = this.controlNumber+2;
        }

        for ( i=startIndex; i<len; i++) {
            // fined the index of ':' that saparate attr name and attr value
            for( colon=0;
                 colon<((StringBuffer)this.rFields.get(i)).length();
                 colon++) {

                if((((StringBuffer)this.rFields.get(i)).charAt(colon)
                                                                == (int)':')) {
                    // found index of ':', done
                    break;
                }
            }

            attrName = ((StringBuffer)this.rFields.get(i)).substring(0, colon);

            if (((((StringBuffer)this.rFields.get(i)).
                                         charAt(colon+1)) != (int)':')
              &&((((StringBuffer)this.rFields.get(i)).
                                         charAt(colon+1))!=(int)'<')){

                // common attribute value
                attrValue = ((StringBuffer)this.rFields.get(i)).substring(
                             colon+1,
                             ((StringBuffer)this.rFields.get(i)).length());
            }
            else if ( (((StringBuffer)this.rFields.get(i)).charAt(colon+1))
                                                                   ==(int)'<'){
                // a file URL, we are not ready to handle this
                attrValue = ((StringBuffer)this.rFields.get(i)).substring(
                             colon+2,
                             ((StringBuffer)this.rFields.get(i)).length());
            }
            else {
                // a base64 encoded attribute value
                this.isBase64Encoded = true;
                // decode the value
                this.byteValue = this.base64Decoder.decoder(
                             (StringBuffer)this.rFields.get(i),
                             colon+2,
                             ((StringBuffer)this.rFields.get(i)).length());
            }

            // add the new atribute to the attribute set or add the
            // value to an existing attribute itn the attribute set
            if ( this.attrSet.getAttribute(attrName) == null ) {

                // this is a new attribute, add it to attribute set
                if ( this.isBase64Encoded ) {
                    this.attrSet.add(new LDAPAttribute(attrName, 
                                                             this.byteValue));
                    this.isBase64Encoded = false;
                }
                else {
                    this.attrSet.add(new LDAPAttribute(attrName, attrValue));
                }
            }
            else {

                // add the value to an existing attribute in the set
                if ( this.isBase64Encoded ) {
                    this.attrSet.getAttribute(attrName).addValue(this.byteValue);
                    this.isBase64Encoded = false;
                }
                else {
                    this.attrSet.getAttribute(attrName).addValue(attrValue);
                }
            }
        }

        // construct the currentEntry
        this.currentEntry = new LDAPEntry(this.entryDN, this.attrSet);
    }

    /**
     *
     */
    private void toDeleteDN() {

        this.deleteDN = this.entryDN;
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

        fieldIndex = this.controlNumber + 2; // points to newrdn field
        index = 7;                           // length of "newrdn:"

        if(((StringBuffer)this.rFields.get(fieldIndex))
                                                 .charAt(index-1) != (int)':' ){
             throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                     + "malformated newrdn field");
        }

        // get newrdn
        if ( (((StringBuffer)this.rFields.get(fieldIndex)).charAt(index))
                                                                 != (int)':') {
            // get newrdn
            this.modInfo[0] = ((StringBuffer)this.rFields.get(fieldIndex))
                                                             .substring(index);
        }
        else {
            // base64 encoded value
            index+= 2;

            // remove trailing space if there is any
            len = ((StringBuffer)this.rFields.get(fieldIndex)).length();
            if ( ((StringBuffer)this.rFields.get(fieldIndex)).charAt(len-1)
                                                                 == (int)' ') {
                ((StringBuffer)this.rFields.get(fieldIndex)).delete(len-1, len);
            }

            // decode newrdn
            this.byteValue = this.base64Decoder.decoder(
                         (StringBuffer)this.rFields.get(fieldIndex),
                         index,
                         ((StringBuffer)this.rFields.get(fieldIndex)).length());

            this.modInfo[0] = new String(this.byteValue, "UTF8");
        }

        fieldIndex++;   // points to deleteOleRDN
        index = 13;      // points to '*' after "deleteoldrdn:"

        if(((StringBuffer)this.rFields.get(fieldIndex))
                                                 .charAt(index-1) != (int)':' ){
             throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                     + "malformated deleteoldrdn field");
        }

        if ( ((StringBuffer)this.rFields.get(fieldIndex)).charAt(index)
                                                                == (int)'1' ) {
            this.modInfo[1] = new String("1");
        }
        else {
            this.modInfo[1] = new String("0");
        }

        fieldIndex++;   // points to deleteOleRDN
        if (fieldIndex<=this.rFields.size()) {
            // there is a new superior
            index = 12;   // length of "newsuperior:"

            if(((StringBuffer)this.rFields.get(fieldIndex))
                                                 .charAt(index-1) != (int)':' ){
             throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                     + "malformated newsuperior field");
            }

            if ( (((StringBuffer)this.rFields.get(fieldIndex)).charAt(index))
                                                                 != (int)':') {
            // get newrsuperior
            this.modInfo[2] = ((StringBuffer)this.rFields.get(fieldIndex))
                                                             .substring(index);
            }
            else {
                // base64 encoded value
                index++;
                // remove trailing space if there is any
                len = ((StringBuffer)this.rFields.get(fieldIndex)).length();
                if ( ((StringBuffer)this.rFields.get(fieldIndex)).
                                                   charAt(len-1) == (int)' ') {
                    ((StringBuffer)this.rFields.get(fieldIndex)).
                                                            delete(len-1, len);
                }

                // decode newsuperior
                this.byteValue = this.base64Decoder.decoder(
                         (StringBuffer)this.rFields.get(fieldIndex),
                         index,
                         ((StringBuffer)this.rFields.get(fieldIndex)).length());

                this.modInfo[2] = new String(this.byteValue, "UTF8");;
            }

        }
    }

    /**
     * Build LDAPModification array based on the content of LDIF modify record
     *
     * @return LDAPModification array.
     */
    public void toLDAPModifications () throws IOException {

        int        i, index;
        int        j;                       // number of attrs for an operation
        int        modNumber = 0;           // number of mod operations
        int        startIndex;              // where to find mod operations
        String     attrName, opName;
        LDAPAttribute attr = null;
        ArrayList modList = new ArrayList();

        // used to skip dn, control, and changetype field
        startIndex = this.controlNumber + 2;

        // an LDIF modify record may specify a number of LDAP modify
        // oprations. '-'s are used to separate the opetrations
        for (i=startIndex; i<this.fieldNumber; i++) {
            // get the number of LDAP modify operations
            if ( ((StringBuffer)this.rFields.get(i)).charAt(0) == (int)'-' ) {
                modNumber++;
            }
        }

        // populate the LDAPModification array object
        for (i=startIndex; i<this.fieldNumber; i+=j+1) {

            // find mod operation name and attr name.
            for ( index=0;
                  index<((StringBuffer)this.rFields.get(i)).length();
                  index++ ) {
                if (((StringBuffer)this.rFields.get(i)).
                                                    charAt(index)==(int)':'){
                    break;
                }
            }

            // index points to ':' between mod operation name and attr name
            opName = ((StringBuffer)this.rFields.get(i)).substring(0, index);
            // already removed leading space; no trailing space
            attrName = ((StringBuffer)this.rFields.get(i)).substring(index+1);

            // build each LDAPModification object and add it ot modList
            j = 1;
            if (((StringBuffer)this.rFields.get(i+1)).charAt(0) != (int)'-') {
                // there is at least one attribute value specified
                for ( ;
                      ((StringBuffer)this.rFields.get(i+j)).charAt(0)!=(int)'-';
                      j++) {

                    // index is used to get attribute value
                    for ( index=0;
                          index<((StringBuffer)this.rFields.get(i+j)).length();
                          index++ ) {
                        if (((StringBuffer)this.rFields.get(i+j)).charAt(index)
                                                                 == (int)':') {
                            break;
                        }
                    }

                    attr = new LDAPAttribute( attrName,
                         ((StringBuffer)this.rFields.get(i+j))
                                                          .substring(index+1));

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
                        throw new IOException(
                            "com.novell.ldap.ldif_dsml.LDIFReader:"
                                        + "Not supported modify operation");
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
                    throw new IOException(
                        "com.novell.ldap.ldif_dsml.LDIFReader:"
                                    + "No attribute value specified");
                }
            }
        }

        this.mods = new LDAPModification[modList.size()];

        this.mods = (LDAPModification[])modList.toArray(this.mods);
    }
}
