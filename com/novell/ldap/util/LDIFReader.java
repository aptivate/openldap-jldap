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
 * The class that takes the inputStream object as input. The inputStream may
 * be an LDIF file or other data source. It processes the inputStream till the
 * end of the stream.
 */

public class LDIFReader extends LDIF implements LDAPImport {

    private int       version, recordType, changeType;    
    private String    dn, changeField, changeOperation;
    private String[]  rFields;                          // record fields
    private String[]  namePairs;                 // attr name and value pairs
    private String[]  controlFields;
    private BufferedReader      bufReader;
    private LDAPControl[] controls = null;
    private LDAPEntry currentEntry = null;
    private LDAPOperation currentChange = null;

    /**
     * Constructs an LDIFReader object by initializing LDIF_VERSION, isContent,
     * InputStreamReader, and BufferedReader.
     *
     * In order to determine if this is a LDIF content file or LDIF change file,
     * the lines of the first record in the file are red into memory.  
     */
    public LDIFReader(InputStream in) throws IOException, LDAPException {
        
        super();
        super.setVersion( LDIF.LDIF_VERSION_1 );
        InputStreamReader isr = new InputStreamReader(in, "UTF8");
        bufReader = new BufferedReader(isr);

        String line;
        ArrayList FRLines = new ArrayList();

        // skip the leading empty and comment lines before version line,
        // and read the version line
        while( (((line = bufReader.readLine())!= null) && (line.length() == 0))
               || ( line != null && line.startsWith("#"))) {
        }

        // if this is the version line, check the version
        if (line.startsWith("version:")) {
            version = Integer.parseInt(
                line.substring("version:".length()).trim() );
            if ( version != 1 ) {
                throw( new IOException( "com.novell.ldap.ldif_dsml.LDIFReader" +
                                                  "Should be 'version: 1'" ) );
            }
        }

        // skip any empty and comment lines between the version line and
        // the first line fo the dn field in the first record of the LDIF
        // file, read the first line of the dn field of the first record
        do {
            // mark the first dn line, so we can later go back to here
            bufReader.mark(8192);
        } while((((line=bufReader.readLine())!= null) && (line.length()== 0))
               || line.startsWith("#"));

        FRLines.add(line);

        // read the rest lines of the record except comment lines
        while (((line=bufReader.readLine())!= null)&&(line.length()!=0)) {
            if ( !line.startsWith("#") )
                FRLines.add(line);
        }

        // we have red the lines of the first record, convert them to
        // the record field
        toRecordFields(FRLines);

        // go back to the beginning of the first record of the LDIF file
        bufReader.reset();

        // check the second recore field to see if it starts with 'changetype'
        // control. If it does, set super class isContent to 'false'
        if (   ((this.rFields[1]).startsWith("changetype:"))
            || ((this.rFields[1]).startsWith("control:"))) {
            setContent(false);
        }
    }


    /**
     * Read the record lines in the LDIF change file, turn the lines into lDIF
     * record fields, process the fileld information, construct and return
     * LDAPOperation object.
     *
     * @return The LDAPOperation object represented by the LDIF change record.
     */
    public LDAPOperation readChange()
    throws UnsupportedEncodingException, IOException {

        ArrayList cLines = new ArrayList(); // change record lines

        if( isContent()) {
            throw new RuntimeException("Cannot read changes from LDIF content file");
        }

        if ( (cLines = readRecordLines()) == null) {
            // end of file
            return null;
        }

        // convert record lines to record fields
        toRecordFields(cLines);

        //
        toRecordProperties();

        switch( changeType ) {
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
     * Read the record lines in the LDIF content file, turn the lines into lDIF
     * record fields, process the fileld information, construct and return
     * LDAPEntry object.
     *
     * @return The LDAPEntry object represented by the LDIF content record.
     */
    public LDAPEntry readContent()
    throws UnsupportedEncodingException, LDAPException, IOException {

        this.recordType = LDIF.CONTENT_RECORD;
        ArrayList cLines = new ArrayList();    // content record lines

        if( ! isContent()) {
            throw new RuntimeException("Cannot read content from LDIF change file");
        }

        // read record lines
        if ( (cLines = readRecordLines()) == null ) {
            // end of file
            return null;
        }

        // convert content record lines to the record fields
        toRecordFields(cLines);

        //
        toRecordProperties();

        currentEntry = toLDAPEntry();

        return currentEntry;
    }


    /**
     * find and decode the Base64 encoded fields.
     *
     * @param The LDIF record fields.
     */
    private void decodeRecordFields(String[] fields)
                            throws UnsupportedEncodingException, IOException {

        int i, firstColon, len = fields.length;;
        String tempString;
        Base64Decoder base64Decoder = new Base64Decoder();

        // decode record fields if there is one that is base64 encoded
        for (i=0; i<len; i++) {
            firstColon = (fields[i]).indexOf((int)':');
            // is there a Base64 encoded field?
            if ((fields[i]).charAt(firstColon+1)==(int)':') {
                // yes, get the spec of this field
                tempString = ((fields[i]).substring(firstColon+2)).trim();
                // decode the spec, and
                tempString = base64Decoder.decoder(tempString);
                // put it back to the field
                this.rFields[i] = (fields[i]).substring(0,firstColon+1)
                                                           + tempString;
            }
        }
    }


    /**
     * Process LDIF record fields to get record dn and attributes,construct
     * and return LDAPEntry object.
     *
     * @return LDAPEntry object.
     */
    private LDAPEntry toLDAPEntry() {
        int i, j, index, len;                      
        ArrayList tl  = new ArrayList(); 
        String attrName;                 
        String attrValue;                
        String[] values = null;          
        LDAPAttribute attr;
        LDAPAttributeSet attrSet = new LDAPAttributeSet();

        len = namePairs.length;;

        // go through the namePairs to get attribute names and values.
        for ( i=0; i<len; i++ ) {
            index = (namePairs[i]).indexOf((int)':');
            attrName  = (namePairs[i]).substring(0, index).trim();
            attrValue = (namePairs[i]).substring(index + 1).trim();
            tl.add(attrValue);

            // look up the rest of the namePairs to see
            // if this is a multi-valued attribute
            for ( j = i + 1; j < len; j++) {
                index = (namePairs[j]).indexOf((int)':');

                if ( attrName.equalsIgnoreCase((namePairs[j]).substring( 0, index )) ) {
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
                values = toArray( tl );

                attr = new LDAPAttribute(attrName, values);
            }

            // clean to reuse it
            tl.clear();
            // add to LDAPAttributeSet object
            attrSet.add(attr);
        }

        return new LDAPEntry(this.dn, attrSet);
    }

    /**
     * Read the record lines by skipping any empty and comment lines and
     * checking if the first line starts with 'dn".
     *
     * @throws IOException.
     */
    public ArrayList readRecordLines() throws IOException {

        String line;
        ArrayList RLines = new ArrayList(); // any record lines

        // skip empty and comment lines and read the the first dn
        // line of the record
        while( (((line = bufReader.readLine())!= null) && (line.length() == 0))
                || ( line != null && line.startsWith("#"))) {
        }

        if (line == null) {
            return null;
        }

        // check if the first dn line starts with 'dn:'
        if ( !line.startsWith("dn:") ) {
            throw new IOException("com.novell.ldap.ldif_dsml." + 
                                  "Any record should start with 'dn:'");
        }

        RLines.add(line);

        // read the rest lines of the record except comment lines
        while (((line=bufReader.readLine())!= null)&&(line.length()!=0)) {
            if ( !line.startsWith("#") )
                RLines.add(line);
        }

        return RLines;
    }

    /**
     * Turn record lines into record fields and decode any Base64 encoded fields
     *
     * @param lines  The LDIF record lines
     */
    public void toRecordFields(ArrayList lines)
    throws UnsupportedEncodingException, IOException {

        int i, firstColon, len = lines.size();
        String tempString;
        ArrayList tempList = new ArrayList();

        
        for ( i = 0; i < len; i++ ) {
            tempString = (String)(lines.get(i));

            while( (i < len-1) && (((String)lines.get(i+1))).startsWith(" ")) {
                // find a continuationvline,concatenate it to the previous line
                tempString += ((String)lines.get(i+1)).substring(1);
                i++;
            }

            tempList.add(tempString);
        }

        // get all record fields        
        this.rFields = toArray( tempList );        

        // find and decode any Base64 encoded fields
        decodeRecordFields(this.rFields);
    }

    /**
     * set up record dn, attribute names and values, and controls
     *
     */    
    private void toRecordProperties() throws IOException {

        int i, index, len = this.rFields.length;

        // set dn
        index = this.rFields[0].indexOf((int)':');
        this.dn = (this.rFields[0]).substring(index+1).trim();

        if ( isContent() ) {
            // this is a content record
           this.recordType = CONTENT_RECORD;

           namePairs = new String[len-1];

           for ( i = 0; i < len-1; i++ ) {
               namePairs[i] = this.rFields[i+1];
           }

        }
        else {
            // this is a change record
            this.recordType = CHANGE_RECORD;

            if ( (this.rFields[1]).startsWith("control:") ) {
                // a change record with one or more controls
                ArrayList controlList = new ArrayList();
                i = 1;
                while ((this.rFields[i]).startsWith("control:")) {
                    controlList.add(this.rFields[i]);
                    i++;
                }

                // now i reference the change field
                int changeIndex = i;
                this.changeField = this.rFields[changeIndex];

                // all other fields
                int contentLen = len - changeIndex;
                this.namePairs = new String[contentLen];
                for (i=0; i<contentLen; i++ ) {
                    this.namePairs[i] = this.rFields[i+changeIndex];
                }

                // get string array of control list
                int controlNumber = controlList.size();
                controlFields = new String[controlNumber];
                for( i=0; i<controlNumber; i++) {
                    controls[i] = (LDAPControl)controlList.get(i);
                }
            }
            else {
                // a change record with no controls
                this.changeField = this.rFields[1];

                // all other fields
                this.namePairs = new String[len-2];

                for (i=0; i<len-2; i++ ) {
                    this.namePairs[i] = this.rFields[i+2];
                }
            }
            
            index = (this.changeField).indexOf((int)':');
            this.changeOperation = (this.changeField).substring(index+1).trim();

            // set change type
            if ( changeOperation.equalsIgnoreCase("add") ) {
                this.changeType = LDAPOperation.LDAP_ADD;
            }
            else if ( changeOperation.equalsIgnoreCase("delete") ) {
                this.changeType = LDAPOperation.LDAP_DELETE;
            }
            else if ( changeOperation.equalsIgnoreCase("modrdn") ) {
                this.changeType = LDAPOperation.LDAP_MODDN;
            }
            else if ( changeOperation.equalsIgnoreCase("moddn") ) {
                this.changeType = LDAPOperation.LDAP_MODDN;
            }
            else if ( changeOperation.equalsIgnoreCase("modify") ) {
                this.changeType = LDAPOperation.LDAP_MODIFY;
            }
            else
                throw new IOException("com.novell.ldap.ldif_dsml." +
                                      "not supported change operation");

        }
    }

    /**
     * Build an ModInfo object based on the content of LDIF modDN reocrd
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
                    throw new IOException("com.novell.ldap.ldif_dsml.LDIFReader:" 
                                            + "Not supported modify operation");
                }

                j++;
            }
            mods[i] = mod;
        }

        return mods;
    }   
}
