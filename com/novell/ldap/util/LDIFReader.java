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

/**
 * The class that is used to process the inputStream object.
 *
 * <p>The object of the class is used to read LDAPEntries from an LDIF content
 * file, or read LDAP operations from an LDIF change file. According to the
 * LDAP operations specified in the LDIF change file, either <tt>LDAPEntry</tt>
 * object, delete dn, <tt>ModInfo</tt> object, or <tt>LDAPModification</tt>
 * array object can be retrieved.</p>
 */
public class LDIFReader extends LDIF implements LDAPImport {

    private int       version;                   // LDIF file version
    private int       operationType;
    private String    dn;                        // entry dn
    private String    changeField;               // record change field
    private String    line;
    private String[]  rFields;                   // record fields
    private String[]  namePairs;                 // attr name and value pairs
    private String[]  cFields;                   // control fields
    private ArrayList rLines = new ArrayList();  // record lines
    private BufferedReader      bufReader;
    private LDAPControl[] controls = null;
    private LDAPEntry currentEntry = null;
    private LDAPOperation currentChange = null;


    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isContent,
     * InputStreamReader, and BufferedReader.
     *
     * <p>The constructor uses a default size value of 8,192 to create the
     * buffering character-input stream and suppose that the zise is big
     * enough to hold the dn field and the first line of the next field
     * of the first record in the LDIF file currently being read.</p>
     *
     * <p>The constructor uses '1' as default LDIF file version</p>
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
     * <p>The constructor uses a default size value of 8,192 to create the
     * buffering character-input stream and suppose that the zise is big
     * enough to hold the dn field and the first line of the next field
     * of the first record in the LDIF file currently being read.</p>
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
     * In order to determine if this is a LDIF content file or LDIF change file,
     * the lines of the first record in the file are red into memory.
     *
     * @param in The   Inputstream object to be processed by LDIFReader
     * @param version  The version currently used in the LDIF file
     * @param bufSize  The size used to create a buffering character-input
     *                 stream. The defaule value is 8,192.
     */
    public LDIFReader(InputStream in, int version, int bufSize) throws IOException {

        super();

        // check LDIF file version
        if ( version != 1 ) {
            throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "Should be version 1");
        }

        super.setVersion( version );
        InputStreamReader isr = new InputStreamReader(in, "UTF8");
        bufReader = new BufferedReader(isr);

        this.rLines.clear();

        // skip the leading empty and comment lines before version line
        while( (((this.line = bufReader.readLine())!= null)
               && (this.line.length() == 0))
               ||  (this.line != null && this.line.startsWith("#"))) {
        }

        if ( line == null ) {
             throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                                     + "There is an empty file" ) ;
        }

        // the version line, check the version
        if (this.line.startsWith("version:")) {
            this.version = Integer.parseInt(
                this.line.substring("version:".length()).trim() );
            if ( this.version != 1 ) {
                throw new RuntimeException(
                    "com.novell.ldap.ldif_dsml.LDIFReader: "
                                     + "version: found '" + this.version + "', should be '1'" ) ;
            }
        }
        else {
             throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDIFReader: "
                                     + "There is no version line" ) ;
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

        // this is the first line of the dn field
        if ( !this.line.startsWith("dn")) {
                throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "a record should starts with dn");
        }
        else {
            this.rLines.add(this.line);
        }

        // ignore the rest lines of the dn field and read the
        // line right after the dn field
        while ( (this.line = bufReader.readLine()) != null ) {

            if (    !this.line.startsWith(" ")    // ! a part of dn field
                 && !this.line.startsWith("#") ){ // ! a comment line

                 // it is still needed to check if this is an empty line
                 if ( this.line.length() == 0 ) {
                    // an empty line, this record only has dn field
                    throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "the record only has dn field");
                 }

                 // this should be the line that starts with either
                 // 'control', 'changetype', or an attribute name.
                 this.rLines.add(this.line);
                 break;
            }
        }

        if ( this.line == null) {
                throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "the LDIF file is not well formated");
        }

        // turn record lines to record fields
        toRecordFields();

        // check and set the type of the current LDIF file
        if (   ((this.rFields[1]).startsWith("changetype"))
            || ((this.rFields[1]).startsWith("control:"))) {
            setContent(false);
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


        if( ! isContent()) {
            throw new RuntimeException(
                "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "Cannot read content from LDIF change file");
        }

        // read record lines
        this.rLines.clear();
        readRecordLines();

        if ( this.rLines == null ) {
            // end of file
            return null;
        }

        // convert content record lines to the record fields
        toRecordFields();

        // set dn, controls, and attribute nemes and values
        toRecordProperties();

        // create LDAPentry object
        currentEntry = toLDAPEntry();

        return currentEntry;
    }


    /**
     * Read the LDAP operations from the LDIF change file.
     *
     * @see LDAPOperation
     */
    public LDAPOperation readOperation()
    throws UnsupportedEncodingException, IOException {

        if( isContent()) {
            throw new RuntimeException(
                "Cannot read changes from LDIF content file");
        }

        // read record lines
        this.rLines.clear();
        readRecordLines();

        if ( this.rLines == null ) {
            // end of file
            return null;
        }

        // convert record lines to record fields
        toRecordFields();

        // set up dn, controls, attribute names and values
        toRecordProperties();

        switch( operationType ) {
                    // construct a specific change record object and let the
                    // super class object reference it
                    case LDAPOperation.LDAP_ADD :
                        currentEntry = toLDAPEntry();
                        if (controls == null)
                            currentChange = new LDAPAdd(currentEntry);
                        else
                            currentChange = new LDAPAdd(currentEntry, controls);
                        break;

                    case LDAPOperation.LDAP_DELETE :
                        if (controls == null)
                            currentChange = new LDAPDelete(this.dn);
                        else
                            currentChange = new LDAPDelete(this.dn, controls);
                        break;

                    case LDAPOperation.LDAP_MODDN :
                        ModInfo modInfo = toModInfo();
                        String sup = modInfo.getNewSuperior();
                        if (controls == null) {
                            if( sup.length() == 0 ) {
                                currentChange = new LDAPModDN(
                                                    this.dn,
                                                    modInfo.getNewRDN(),
                                                    modInfo.getDeleteOldRDN());
                            }
                            else {
                                currentChange = new LDAPModDN(
                                                    this.dn,
                                                    modInfo.getNewRDN(),
                                                    modInfo.getDeleteOldRDN(),
                                                    modInfo.getNewSuperior());
                            }
                        }
                        else {
                            if((sup.length())==0 ) {
                                currentChange = new LDAPModDN(
                                                    this.dn,
                                                    modInfo.getNewRDN(),
                                                    modInfo.getDeleteOldRDN(),
                                                    controls);
                            }
                            else {
                                currentChange = new LDAPModDN(this.dn,
                                                    modInfo.getNewRDN(),
                                                    modInfo.getDeleteOldRDN(),
                                                    modInfo.getNewSuperior(),
                                                    controls);
                            }

                        }
                        break;

                    case LDAPOperation.LDAP_MODIFY :

                        LDAPModification[] mods = toLDAPModifications();
                        if (controls == null) {
                            currentChange = new LDAPModify(this.dn, mods);
                        }
                        else {
                            currentChange=new LDAPModify(this.dn,mods,controls);
                        }
                        break;

                    default:
                        // unknown change type
                        throw new IOException("com.novell.ldap.ldif_dsml."
                                        + "LDIFReader: Unknown change type");
                }

        return currentChange;
    }


    /**
     * Read the record lines by skipping any empty and comment lines and
     * checking if the first line starts with 'dn".
     *
     * @throws IOException.
     */
    public void  readRecordLines() throws IOException {

        this.rLines.clear();

        // skip empty and comment lines and read the the first dn
        // line of the record
        while( ( ((this.line = bufReader.readLine()) != null)
                 && (this.line.length() == 0))
                 || ( this.line != null && this.line.startsWith("#"))) {
        }

        if ( this.line != null ) {

            // check if the first dn line starts with 'dn:'
            if ( !this.line.startsWith("dn:") ) {
                throw new IOException("com.novell.ldap.ldif_dsml." +
                                      "Any record should start with 'dn:'");
            }

            this.rLines.add(this.line);

            // read the rest lines of the record except comment lines
            while ( ((this.line = bufReader.readLine()) != null)
                    && (this.line.length()!=0)) {
                if ( !this.line.startsWith("#") )
                    this.rLines.add(this.line);
            }
        }
        else {
            this.rLines = null;
        }
    }

    /**
     * Turn record lines into record fields and decode any Base64 encoded fields
     *
     * @param lines  The LDIF record lines
     */
    public void toRecordFields()
    throws UnsupportedEncodingException, IOException {

        int i, firstColon, len = this.rLines.size();
        String tempString;
        ArrayList tempList = new ArrayList();


        for ( i = 0; i < len; i++ ) {
            tempString = (String)(this.rLines.get(i));

            while( (i < len-1)
                   && (((String)this.rLines.get(i+1))).startsWith(" ")) {
                // find a continuationvline,concatenate it to the previous line
                tempString += ((String)this.rLines.get(i+1)).substring(1);
                i++;
            }

            tempList.add(tempString);
        }

        // get all record fields
        this.rFields = new String[tempList.size()];
        this.rFields = (String[])tempList.toArray(this.rFields);

        // find and decode any Base64 encoded fields
        decodeRecordFields();
    }


    /**
     * Find and decode any base64 encoded fields.
     *
     */
    private void decodeRecordFields()
                            throws UnsupportedEncodingException, IOException {

        int i, firstColon, len = this.rFields.length, fromIndex=0;
        String tempString;
        Base64Decoder base64Decoder = new Base64Decoder();

        // decode record fields if there is one that is base64 encoded
        for (i=0; i<len; i++) {

            // go through the field to see if there is any base64 encoded
            // value. a base64 encoded value has a leading "::" part
            while( true ) {
                // see a colon ?
                firstColon = (this.rFields[i]).indexOf((int)':', fromIndex);

                // no, then it's done
                if (firstColon == -1) {
                    fromIndex = 0;
                    break;
                }

                // yes, what's the next ?
                if ((this.rFields[i]).charAt(firstColon+1)==(int)':') {
                    // another colon, it's base64 encoded, get it
                    tempString =
                            ((this.rFields[i]).substring(firstColon+2)).trim();
                    // decode it and
                    tempString = base64Decoder.decoder(tempString);
                    // put it back to the field
                    this.rFields[i] =
                       (this.rFields[i]).substring(0,firstColon+1) + tempString;
                }
                // new start index used to search for "::"
                fromIndex = firstColon + 1;
            }
        }
    }


    /**
     * set up record dn, attribute names and values, and controls
     *
     */
    private void toRecordProperties() throws IOException {

        int i, index, len = this.rFields.length;
        boolean criticality;
        byte[] controlValue;
        String controlOID, tempString, booleanString;

        // set dn
        index = this.rFields[0].indexOf((int)':');
        this.dn = (this.rFields[0]).substring(index+1).trim();

        if ( isContent() ) {

           namePairs = new String[len-1];

           // is there any attribute name and value specified
           if ( len == 1 ) {
               // no any attribute name and value
               throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "the reocrd only contains dn field");
           }

           // save the attribute name(s) and value(s)
           for ( i = 0; i < len-1; i++ ) {
               namePairs[i] = this.rFields[i+1];
           }

        }
        else {

            if ( (this.rFields[1]).startsWith("control:") ) {
                // a change record with one or more controls

                ArrayList controlList = new ArrayList();
                i = 1;

                // save the controls fields
                while ((this.rFields[i]).startsWith("control:")) {
                    controlList.add(this.rFields[i]);
                    i++;
                }

                // now i reference the change field
                int changeIndex = i;

                // set changeField
                this.changeField = this.rFields[changeIndex];

                // set operationType
                setOperationType();

                // is there any attribute name and value
                // specified for the change operation ?
                if (    (len == changeIndex)
                     && this.operationType != LDAPOperation.LDAP_DELETE ) {

                    // no any attribute name and value
                    throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                + "no attribute specified for this operation");
                }

                // save attribute name(s) and value(s)
                int contentLen = len - changeIndex;
                this.namePairs = new String[contentLen];
                for ( i = 0; i < contentLen; i++ ) {
                    this.namePairs[i] = this.rFields[i+changeIndex];
                }

                // get array of control fields
                this.cFields = new String[controlList.size()];
                this.cFields = (String[])controlList.toArray(this.cFields);

                // get the number of controls related with this change
                int controlNumber = controlList.size();
                // initializing control array
                controls = new LDAPControl[controlNumber];

                // control field has th format of
                //     control: 1.2.3.4 true: byte values
                // or  control: 1.2.3.4 true:: base64 encoded byte values
                for ( i = 0; i < controlNumber; i++ ) {
                    index = this.cFields[i].indexOf((int)':');
                    tempString = (this.cFields[i]).substring(index+1).trim();

                    // get control OID
                    index = tempString.indexOf((int)' ');
                    controlOID = tempString.substring( 0, index );
                    tempString = tempString.substring(index).trim();

                    // get control criticality
                    index = tempString.indexOf((int)':');

                    if ( index != -1) {
                        booleanString = tempString.substring( 0, index ).trim();
                        tempString = tempString.substring( index+1 ).trim();

                    }
                    else {
                        booleanString = tempString;
                    }

                    if ( booleanString.equalsIgnoreCase("true") ) {
                        criticality = true;
                    }
                    else {
                        criticality = false;
                    }

                    // get control value
                    if ( index != -1) {
                        controlValue = tempString.getBytes();
                    }
                    else {
                        controlValue = null;
                    }

                    // build each control object
                    this.controls[i] = new LDAPControl( controlOID,
                                                        criticality,
                                                        controlValue);
                }

            }
            else {
                // a change record with no controls:

                // set changeField
                this.changeField = this.rFields[1];

                // set operationType
                setOperationType();

                // is there any attribute name and value
                // specified for the change operation ?
                if (    (len == 2)
                     && this.operationType != LDAPOperation.LDAP_DELETE ) {

                    // no any attribute name and value
                    throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                + "no attribute specified for this operation");
                }

                // save attribute name(s) and value(s)
                this.namePairs = new String[len-2];

                for (i=0; i<len-2; i++ ) {
                    this.namePairs[i] = this.rFields[i+2];
                }
            }
        }
    }


    /**
     *
     */
    private void setOperationType( ) {

        int index;
        String changeOperation;

        index = (this.changeField).indexOf((int)':');

        changeOperation = (this.changeField).substring(index+1).trim();

        // set operation type
        if ( changeOperation.equalsIgnoreCase("add") ) {
            this.operationType = LDAPOperation.LDAP_ADD;
        }
        else if ( changeOperation.equalsIgnoreCase("delete") ) {
            this.operationType = LDAPOperation.LDAP_DELETE;
        }
        else if ( changeOperation.equalsIgnoreCase("modrdn") ) {
            this.operationType = LDAPOperation.LDAP_MODDN;
        }
        else if ( changeOperation.equalsIgnoreCase("moddn") ) {
            this.operationType = LDAPOperation.LDAP_MODDN;
        }
        else if ( changeOperation.equalsIgnoreCase("modify") ) {
            this.operationType = LDAPOperation.LDAP_MODIFY;
        }
        else
            throw new RuntimeException(
                  "com.novell.ldap.ldif_dsml.LDIFReader" +
                                      "not supported change operation");
    }


    /**
     * Process LDIF record fields to get record dn and attributes,construct
     * and return LDAPEntry object.
     *
     * @return LDAPEntry object.
     */
    private LDAPEntry toLDAPEntry() {

        int              i, j, index, len;
        ArrayList        tl  = new ArrayList();
        String           attrName;
        String           attrValue;
        String[]         attrValues = null;
        LDAPAttribute    attr;
        LDAPAttributeSet attrSet = new LDAPAttributeSet();

        len = namePairs.length;;

        // go through the namePairs to get attribute names and values.
        for ( i = 0; i < len; i++ ) {
            index = (namePairs[i]).indexOf((int)':');
            attrName  = (namePairs[i]).substring(0, index).trim();
            attrValue = (namePairs[i]).substring(index + 1).trim();
            tl.add(attrValue);

            // look up the rest of the namePairs to see
            // if this is a multi-valued attribute
            for ( j = i + 1; j < len; j++) {
                index = (namePairs[j]).indexOf((int)':');

                if ( attrName.equalsIgnoreCase((namePairs[j]).
                                                  substring( 0, index )) ) {
                    // found one, save it
                    tl.add((namePairs[j]).substring( index + 1 ).trim());
                    // increase i to point to next attribute name/value pair
                    i = j;
                }
            }

            if ( tl.size() == 1 ) {
                // single-valued attribute
                attr = new LDAPAttribute(attrName, attrValue);
            }
            else {
                // multi-valued attribute
                attrValues = new String[tl.size()];
                attrValues = (String[])tl.toArray(attrValues);

                attr = new LDAPAttribute(attrName, attrValues);
            }

            // clean to reuse it
            tl.clear();
            // add to LDAPAttributeSet object
            attrSet.add(attr);
        }

        return new LDAPEntry(this.dn, attrSet);
    }


    /**
     * Build ModInfo object based on the content of LDIF modDN reocrd
     *
     * @return mods ModInfo object that holds newRDN, deleteOldRDN,
     * and newSuperior
     */
    public ModInfo toModInfo() {

        int index;
        boolean delOldRDN;
        String nRDN, sDelOldRDN, nSup;
        ModInfo mi = null;

        index = namePairs[0].indexOf((int)':') + 1;
        nRDN  = namePairs[0].substring(index).trim();
        index = namePairs[1].indexOf((int)':')+1;
        sDelOldRDN = namePairs[1].substring(index).trim();

        if ( Integer.parseInt(sDelOldRDN) == 1 ) {
            delOldRDN = true;
        }
        else {
            delOldRDN = false;
        }

        if(this.namePairs.length == 2) {
            // new superior is not specified in the record
            mi = new ModInfo( nRDN, delOldRDN, new String(""));
        }
        else {
            // new superior is specified in the record
            nSup = namePairs[02].
                          substring(namePairs[2].indexOf((int)':')+1).trim();
            mi = new ModInfo( nRDN, delOldRDN, nSup) ;
        }

        return mi;
    }

    /**
     * Build LDAPModification array based on the content of LDIF modify record
     *
     * @return LDAPModification array.
     */
    public LDAPModification[] toLDAPModifications () throws IOException {

        int        i, j, k, changeNumber = 0, modType, len = namePairs.length;
        String     temp, attrName, attrValue, modOp;
        ArrayList[] modifyOperations;
        LDAPAttribute attr = null;
        LDAPModification mod = null;
        LDAPModification[] mods;

        // an LDIF modify record may specify a number of LDAP modify oprations
        for (i=0; i<len; i++) {
            // get the number of LDAP modify operations
            if ( (namePairs[i]).startsWith("-") ) {
                changeNumber++;
            }
        }

        // construct LDAPModification array
        mods = new LDAPModification[changeNumber];

        modifyOperations = new ArrayList[changeNumber];

        // go through the content, populate modifyOperations. Each
        // change group specify a specific LDAP modify operation
        for ( i = 0, j = 0; i < changeNumber; i++, j++) {
            modifyOperations[i] = new ArrayList();
            while ( !namePairs[j].startsWith("-") ) {
                (modifyOperations[i]).add(namePairs[j]);
                j++;
            }
        }

        // go through each modify operation to get modop, to set modType to
        // LDAPOperation.MODIFY_ADD, LDAPOperation.MODIFY_DELETE,
        // or LDAPOperation.MODIFY_REPLACE, to get modify attribute, and
        // to construct an LDAPModification object
        for ( i=0, j=1; i<changeNumber; i++, j=1 ) {
            // first field consists of 'modifyOperation: attrDescription'
            // modifyOperation may be 'add', 'delete', or 'replace'
            temp = (String)(modifyOperations[i]).get(0);
            modOp = temp.substring(0, temp.indexOf((int)':'));

            while( j < ( modifyOperations[i]).size() ) {
                // the consecutive fields consists of attrName: attrValue pairs
                temp = (String)(modifyOperations[i]).get(j);
                attrName  = temp.substring(0, temp.indexOf((int)':'));
                attrValue = temp.substring( temp.lastIndexOf((int)':') + 1 );
                attr = new LDAPAttribute(attrName, attrValue);

                if ( modOp.equalsIgnoreCase("add") ) {
                    modType = LDAPOperation.MODIFY_ADD;
                    mod = new LDAPModification(LDAPModification.ADD, attr);
                }
                else if ( modOp.equalsIgnoreCase("delete") ) {
                    modType = LDAPOperation.MODIFY_DELETE;
                    mod = new LDAPModification(LDAPModification.DELETE, attr);
                }
                else if ( modOp.equalsIgnoreCase("replace") ) {
                    modType = LDAPOperation.MODIFY_REPLACE;
                    mod = new LDAPModification(LDAPModification.REPLACE, attr);
                }
                else {
                    throw new IOException(
                            "com.novell.ldap.ldif_dsml.LDIFReader:"
                                        + "Not supported modify operation");
                }

                j++;
            }
            mods[i] = mod;
        }

        return mods;
    }
}
