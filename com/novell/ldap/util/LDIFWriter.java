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
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.ldif_dsml.LDAPOperation;
import com.novell.ldap.ldif_dsml.ModInfo;
import com.novell.ldap.util.DN;


public class LDIFWriter extends LDIF implements LDAPExport {

    private int            recordType;                    // record type
    private String         dn;                            // record dn
    private String[]       rLines;                        // record lines
    private String[]       cFields;                       // control fiields
    private ArrayList      rFields  = new ArrayList();    // record fields
    private ArrayList      tempList = new ArrayList();
    private BufferedWriter bufWriter;
    private LDAPControl[]  currentControls;
    private LDAPEntry      currentEntry = null;
    private LDAPOperation  currentChange = null;

    /**
     * Construct an LDIFWriter object by calling super constructor, and
     * initializing LDIF_VERSION, OutputStreamReader object, and BufferedWriter
     * object.
     */
    public LDIFWriter(OutputStream out) throws IOException  {
        super( );
        super.setVersion( LDIF.LDIF_VERSION_1 );
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
        bufWriter = new BufferedWriter( osw );
    }

    /**
     * Write the version line of LDIF file into the OutputStream.
     *
     * <p>Two extra lines will be written to separate version line with the rest
     * of lines in LDIF file</p>
     */
    public void writeVersionLine () throws IOException {

        // LDIF file is  currently using 'version 1'
        String versionLine = new String("version: " + LDIF_VERSION_1);
        bufWriter.write( versionLine, 0, versionLine.length());
        // start a new line and then an extra line to separate
        // the version line with the rest of the contents in LDIF file
        bufWriter.newLine();
        bufWriter.newLine();
    }

    /**
     * Write a single comment line into the OutputStream.
     *
     * <p> an '#' character is added in the front of the line to indicate that
     * the line is a coment line. If the line contains more than 80 characters,
     * it will be splited into multiple lines that start with '#' characters.</p>
     *
     * @param line The comment line to be written to the OutputStream
     */
    public void writeCommentLine ( String line ) throws IOException {

        String   commentLine;
        String[] lines;

        if ( line != null) {
            commentLine = new String( "# " + line);

            // berak the line if it contains more than 80 characters
            if ( (commentLine.length()) > 80 ) {
                lines = toCommentLines( commentLine );
                writeRecordLines( lines );
            }
            else {
                bufWriter.write( commentLine, 0, commentLine.length());
            }

            // write an empty line
            bufWriter.newLine();
        }
    }

    /**
     * Used to worite lines of a record into the OutputStream
     *
     * @param lines The lines to be written to the OutputStream
     */
    public void writeRecordLines ( String[] lines ) throws IOException {

        if ( lines != null ) {
            for ( int i = 0; i < lines.length; i++ ) {
                bufWriter.write( lines[i], 0, (lines[i]).length());
                // start a new line
                bufWriter.newLine();
            }
            // write a new line to sepatate records
            bufWriter.newLine();
        }
    }

    /**
     * Flush the output stream
     */
    public void flushStream() throws IOException {
        // flush the stream
        bufWriter.flush();
    }


    /**
     * Flush and close the output stream
     */
    public void closeStream() throws IOException {
        // flush and then close the stream
        bufWriter.close();
    }


    /**
     * Write a content record into LDIF content file
     *
     * @param entry LDAPEntry object
     * @param ctrls LDAPControl[] object
     *
     */
    public void writeContent(LDAPEntry entry, LDAPControl[] ctrls)
    throws IOException {

        this.recordType = LDIF.CONTENT_RECORD;

        if( ! isContent()) {
            throw new RuntimeException("Cannot write change to LDIF"
                                                  + " content file");
        }

        // to content record lines
        this.rLines = toRecordLines(entry, ctrls);

        // write the content line into LDIF file
        writeRecordLines( this.rLines );

    }

    /**
     * Write a number of content records into LDIF content file
     *
     * @param entries LDAPEntry array object
     * @param ctrls LDAPControl[] object
     */
    public void writeContents(LDAPEntry[] entries, LDAPControl[] ctrls)
    throws IOException  {

        for ( int i = 0; i < entries.length; i++ ) {
            writeContent( entries[i], ctrls );
        }
    }

    /**
     * Write a LDAP change record into LDIF file. The change operation may
     * be LDAPAdd, LDAPDelete, LDAPN=ModDN, or LDAPModify.
     *
     * @see LDAPAdd
     * @see LDAPDelete
     * @see LDAPModDN
     * @see LDAPModify
     *
     * @param change LDAPOperation object
     */
    public void writeChange(LDAPOperation change)
    throws IOException  {

        this.dn = change.getDN();
        this.currentControls = change.getControls();
        LDAPModification[] mods;
        ModInfo modInfo;

        if ( change instanceof LDAPAdd) {

            this.currentEntry = ((LDAPAdd)change).getEntry();
            this.rLines =toRecordLines(this.currentEntry, this.currentControls);

        }
        else if ( change instanceof LDAPDelete ) {

            rLines = toRecordLines( this.dn, this.currentControls );

        }
        else if ( change instanceof LDAPModDN ) {

            modInfo = ((LDAPModDN)change).getModInfo();
            this.rLines = toRecordLines( dn, modInfo, this.currentControls );

        }
        else if ( change instanceof LDAPModify) {

            mods = ((LDAPModify)change).getModifications();
            this.rLines = toRecordLines( dn, mods, this.currentControls );

        }
        else {
            throw new RuntimeException("Not supported change type");
        }

        writeRecordLines( this.rLines );
    }

    /**
     * Write a number of LDAP change record into LDIF file. The change operation
     * can be LDAPAdd, LDAPDelete, LDAPN=ModDN, or LDAPModify operation.
     *
     * @see LDAPAdd
     * @see LDAPDelete
     * @see LDAPModDN
     * @see LDAPModify
     *
     */
    public void writeChanges(LDAPOperation[] changes) throws IOException  {

        for ( int i = 0; i < changes.length; i++ ) {
            writeChange( changes[i] );
        }

    }

    /**
     * Used to generate LDIF content record or LDIF change/add record lines.
     *
     *<p>Turn LDAPEntry object and LDAPControl[] object into LDIF record fields
     * and then turn record fields into record lines</p>
     *
     * @param entry  LDAPREntry object
     * @param ctrls  LDAPControl object
     *
     * @return String array which contains the LDIF content or LDIF
     * change/add record lines
     */
    public String[] toRecordLines( LDAPEntry entry, LDAPControl[] ctrls )
    throws UnsupportedEncodingException {

        int      i, len;
        String   attrName,  dn, temp, value;
        LDAPAttribute attr;
        LDAPAttributeSet attrSet;
        Iterator allAttrs;
        Enumeration allValues;

        this.rFields.clear();

        // save entry dn field
        dn = new String( entry.getDN() );
        addDNToRecordFields(dn);

        // save controls if there any
        if ( ctrls != null ) {
            addControlToRecordFields( ctrls );
        }

        if ( !isContent() ) {
            this.rFields.add("changetype: " + new String("add"));
        }

        // save attribute fields
        attrSet = entry.getAttributeSet();
        allAttrs = attrSet.iterator();

        while(allAttrs.hasNext()) {
           attr = (LDAPAttribute)allAttrs.next();
           attrName = attr.getName();
           //temp = new String ( attrName + ": ");

           allValues = attr.getStringValues();

           if( allValues != null) {
               while(allValues.hasMoreElements()) {
                   value = (String) allValues.nextElement();

                   addAttrValueToRecordFields(attrName,value);
               }
           }
        }

        this.rLines = toArray( this.rFields );

        return toLines( this.rLines );
    }



    /**
     * Used to generate LDIF change/modify reocrd lines.
     *
     * <p>Turn entry DN, LDAPModification[] object, and LDAPControl[] object
     * into LDIF LDIF record fields and then turn record fields into LDIF
     * change/modify record lines</p>
     *
     * @param dn    String object representing entry DN
     * @param mods  LDAPModification array object
     * @param ctrls LDAPControl array object
     *
     * @return String array which contains the LDIF change/modify record lines
     *
     * @see LDAPModification
     * @see LDAPControl
     */
    public String[] toRecordLines( String dn, LDAPModification[] mods,
    LDAPControl[] ctrls ) throws UnsupportedEncodingException {

        int i, modOp, len = mods.length;
        String attrName, attrValue;
        LDAPAttribute attr;


        // save entry dn
        addDNToRecordFields(dn);


        // save controls if there is any
        if ( ctrls != null ) {
            addControlToRecordFields( ctrls );
        }

        // save change type
        this.rFields.add(new String("changetype: modify"));

        // save attribute names and values
        for ( i = 0; i < len; i++ ) {

            modOp = mods[i].getOp();
            attr =  mods[i].getAttribute();
            attrName = attr.getName();
            attrValue = attr.getStringValue();

            switch ( modOp )  {
                case LDAPModification.ADD:
                    this.rFields.add(new String("add: "+ attrName));
                    //fields.add(new String( attrName + ": " + attrValue));
                    break;
                case LDAPModification.DELETE:
                    this.rFields.add(new String("delete: "+ attrName));
                    break;
                case LDAPModification.REPLACE:
                    this.rFields.add(new String("replace: "+ attrName));
                    break;
                default:
            }
            //this.rFields.add(new String( attrName + ": " + attrValue));
            addAttrValueToRecordFields(attrName, attrValue);
            // add separators between different modify operations
            this.rFields.add(new String("-"));
            // add an empty line between different modify operations
            this.rFields.add(new String(""));
        }

        this.rLines = toArray( this.rFields );

        return toLines( this.rLines );
    }

    /**
     * Used to generate LDIF change/moddn reocrd lines.
     *
     * <p>Turn entry DN and ModInfo into LDIF change/modify record
     * fields and then turn record fields into LDIF change/moddn record lines</p>
     *
     * @param dn      String object representing entry DN
     * @param modInfo ModInfo object
     * @param ctrls   LDAPControl array object
     *
     * @return String array which contains the LDIF change/modify record lines
     *
     * @see ModInfo
     */
    public String[] toRecordLines( String dn, ModInfo modInfo,
    LDAPControl[] ctrls ) throws UnsupportedEncodingException {

        // save entry dn
        addDNToRecordFields(dn);

        if ( ctrls != null ) {
            addControlToRecordFields( ctrls );
        }

        // save change type
        this.rFields.add(new String("changetype: moddn"));

        // save moddn info
        this.rFields.add(new String("newrdn:" + modInfo.newRDN));
        this.rFields.add(new String("deleteoldrdn:" + modInfo.deleteOldRDN));
        if ( ((modInfo.newSuperior).length()) != 0) {
            this.rFields.add(new String("newsuperior:" + modInfo.newSuperior));
        }

        this.rLines = toArray( this.rFields);

        return toLines( this.rLines );
    }

    /**
     * Used to generate LDIF change/delete reocrd lines.
     *
     * <p>Turn entry DN, controls
     * and change type into LDIF change/delete record fields and then turn
     * record fields into LDIF moddn record lines</p>
     *
     * @param dn    String object representing entry DN
     * @param ctrls LDAPControl array object
     *
     * @return String array which contains the LDIF change/delete record lines
     *
     * @see LDAPControl
     */
    public String[] toRecordLines( String dn, LDAPControl[] ctrls )
    throws UnsupportedEncodingException {

        // save entry dn
        addDNToRecordFields(dn);

        // save controls if there is any
        if ( ctrls != null ) {
            addControlToRecordFields( ctrls );
        }

        // save change type
        this.rFields.add(new String("changetype: delete"));

        this.rLines = toArray( this.rFields );

        return toLines( this.rLines );
    }


    /**
     * Turn the input comment string into multiple comment lines if it contains
     * more than 80 characters
     *
     * @param line String object representing a comment line in LDIF file.
     *
     * @trturn String array object that contain one or more lines
     */
    public String[] toCommentLines( String line ) {

        this.tempList.clear();

        if ( line.length() <= 80 ) {
            // no need to break
            this.tempList.add( line );
        }
        else {
            // break the comment line
            while ( line.length() > 80 ) {
                // any continuation line has length of
                // 80 and starts with a white space
                this.tempList.add( "# " + line.substring(0, 78) );
                line = new String ( line.substring(78) );
            }
            // save the last part of the comment line
            this.tempList.add( "# " + line);
        }

        return toArray( this.tempList );
    }

    /**
     * Turn any string in input String array into multiple continuation lines if
     * it contains more than 80 characters
     *
     * @param ls  The input String array object
     *
     * @return String array object
     */
    public String[] toLines( String[] ls ) {

        this.tempList.clear();

        // break any line that is longer than 80 chars.
        for ( int i = 0; i < ls.length; i++) {
            // field length equals or less than 80, save it
            if ( ls[i].length() <= 80 ) {
                this.tempList.add( ls[i] );
            }
            else {
                // field length is longer than 80, break it
                while ( ls[i].length() > 80 ) {
                    // first line of a record has length of 80, the
                    // substring begins at 0 and extends to 79
                    this.tempList.add( ls[i].substring(0, 80) );
                    // any continuation line has length of
                    // 80 and starts with a white space
                    ls[i] = new String (" " + ls[i].substring(80) );
                }
                // save the last part of the field
                this.tempList.add(ls[i]);
            }
        }

        return toArray( tempList);
    }


    /**
     * Convert LDAPControl array object to control fields in LDIF format
     *
     * @param ctrls LDAPControl array object
     *     
     */
    public void addControlToRecordFields(LDAPControl[] ctrls)
    throws UnsupportedEncodingException {

        boolean criticality;
        byte[]  byteValue = null;
        String  controlOID, controlValue;


        for ( int i = 0; i < ctrls.length; i++ ) {

            controlOID = ctrls[i].getID();
            criticality = ctrls[i].isCritical();
            byteValue = ctrls[i].getValue();

            if ( (byteValue.length) > 0 ) {
                controlValue = new String( byteValue, "UTF-8");
                this.rFields.add( "control: " + controlOID + " " + criticality
                              + ": " + controlValue );
            }
            else {
                this.rFields.add("control: " + controlOID + " " + criticality);
            }
        }
    }

    /**
     * Add record dn into record fields.
     *
     * <p>Check if dn is base64 encoded and use either 'dn:: dn spec' or
     * 'dn: dn spec' format</p>    
     */    
    public void addDNToRecordFields(String dn) {
        
        int index;
        
        index = dn.lastIndexOf((int)' '); 
        
        if ( (index != -1) && (index == dn.length() - 1) ) {
            // base64 encoded
            this.rFields.add( new String("dn:: " + dn ));
        }
        else {
            // UTF8 string
            this.rFields.add( new String("dn: " + dn ));
        }

    }

    /**
     * Add record attribute name and value into record fields.
     *
     * <p>Check if attribute value is base64 encoded and use either 
     * 'attrName:: attrSpec' or 'attrName: attrSpec' format</p>    
     */    
    public void addAttrValueToRecordFields(String attrName, String attrSpec) {
        
        int index;
        
        index = attrSpec.lastIndexOf((int)' '); 
        
        if ( (index != -1) && (index == attrSpec.length() - 1) ) {
            // base64 encoded
            this.rFields.add( new String(attrName + ":: " + attrSpec ));
        }
        else {
            // UTF8 string
            this.rFields.add( new String(attrName + ": " + attrSpec ));
        }

    }
}
