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

import java.io.FileNotFoundException;
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
import com.novell.ldap.ldif_dsml.Base64Encoder;

/**
 * The class that is used to write LDIF content records or LDIF change records
 * to the OutputStream object.
 *
 * <p>The object of the class is used to generate LDIF content record or LDIF
 * change record lines. toRecordLines() methods have different signatures to
 * convert <tt> LDAPEntry</tt> objects, delete dn, <tt>ModInfo</tt> object, or
 * <tt>LDAPModification</tt> array objects into LDIF record lines.</p>
 */


public class LDIFWriter extends LDIF implements LDAPExport {

    private int            recordType;                    // record type
    private String         dn;                            // record dn
    private String[]       rLines;                        // record lines
    private ArrayList      rFields  = new ArrayList();    // record fields
    private ArrayList      tempList = new ArrayList();
    private Base64Encoder  base64Encoder = new Base64Encoder();
    private BufferedWriter bufWriter;
    private LDAPControl[]  currentControls;
    private LDAPEntry      currentEntry = null;
    private LDAPOperation  currentChange = null;


    /**
     * Constructs an LDIFWriter object by calling super constructor, and
     * OutputStreamReader object, and BufferedWriter object.
     *
     * <p>The default version 1 is used for the LDIF file</p>
     *
     * @param out     The OutputStream object
     *
     * @throws IOException
     */
    public LDIFWriter(OutputStream out)
    throws UnsupportedEncodingException, IOException {

        this( out, 1 );
    }


    /**
     * Constructs an LDIFWriter object by calling super constructor, and
     * initializing version, OutputStreamReader object, and BufferedWriter
     * object.
     *
     * @param out     The OutputStream object
     * @param version The version currently used by the LDIF file
     */
    public LDIFWriter(OutputStream out, int version)
    throws UnsupportedEncodingException, IOException {
        super( );

        // check LDIF file version
        if ( version != 1 ) {
            throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFReader: "
                                 + "Should be version 1");
        }

        super.setVersion( version );
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
        bufWriter = new BufferedWriter( osw );
        writeVersionLine();
    }

    /**
     * Write the version line of LDIF file into the OutputStream.
     *
     * <p>Two extra lines will be written to separate version line
     * with the rest of lines in LDIF file</p>
     *
     * @throws IOException if an I/O error occurs.
     */
    protected void writeVersionLine () throws IOException {

        // LDIF file is  currently using 'version 1'
        String versionLine = new String("version: " + getVersion());
        bufWriter.write( versionLine, 0, versionLine.length());
        // start a new line and then an extra line to separate
        // the version line with the rest of the contents in LDIF file
        bufWriter.newLine();
        bufWriter.newLine();
    }

    /**
     * Write a single comment line into the OutputStream.
     *
     * <p> an '#' charis added to the front of the line to indicate that
     * the line is a coment line. If the line contains more than 80
     * characters, it will be splited into multiple lines that start
     * with '#' characters.</p>
     *
     * @param line The comment line to be written to the OutputStream
     *
     * @throws IOException if an I/O error occurs.
     */
    public void writeCommentLine ( String line ) throws IOException {

        String   commentLine;

        if ( line != null) {
            commentLine = new String( "# " + line);

            // berak the line if it contains more than 80 characters
            if ( (commentLine.length()) > 80 ) {
                // borrow rLines to save comments lines
                this.rLines = toCommentLines( commentLine );
                writeRecordLines();
            }
            else {
                bufWriter.write( commentLine, 0, commentLine.length());
            }

            // write an empty line
            bufWriter.newLine();
        }
    }

    /**
     * Used to write lines of a record into the OutputStream
     *
     * @throws IOException if an I/O error occurs.
     */
    protected void writeRecordLines () throws IOException {

        if ( this.rLines != null ) {
            for ( int i = 0; i < this.rLines.length; i++ ) {
                bufWriter.write( this.rLines[i], 0, (this.rLines[i]).length());
                // start a new line
                bufWriter.newLine();
            }
            // write a new line to separate records
            bufWriter.newLine();
        }
    }

    /**
     * Flush the output stream
     *
     * @throws IOException if an I/O error occurs.
     */
    public void flushStream() throws IOException {
        // flush the stream
        bufWriter.flush();
    }


    /**
     * Flush and close the output stream
     *
     * @throws IOException if an I/O error occurs.
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
     * @throws IOException if an I/O error occurs.
     */
    public void writeContent(LDAPEntry entry, LDAPControl[] ctrls)
    throws IOException {

        if( ! isContent()) {
            throw new RuntimeException(
                 "com.novell.ldap.ldif_dsml.LDAIWriter: "
                      + "Cannot write change to LDIF content file");
        }

        // to content record lines
        toRecordLines(entry, ctrls);

        // write the content line into LDIF file
        writeRecordLines();

    }

    /**
     * Write a number of content records into LDIF content file
     *
     * @param entries LDAPEntry array object
     * @param ctrls LDAPControl[] object
     *
     * @throws IOException if an I/O error occurs.
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
     *
     * @throws IOException if an I/O error occurs.
     */
    public void writeChange(LDAPOperation change) throws IOException  {

        this.dn = change.getDN();
        this.currentControls = change.getControls();
        LDAPModification[] mods;
        ModInfo modInfo;

        if ( change instanceof LDAPAdd) {

            this.currentEntry = ((LDAPAdd)change).getEntry();
            toRecordLines(this.currentEntry, this.currentControls);

        }
        else if ( change instanceof LDAPDelete ) {

            toRecordLines( this.dn, this.currentControls );

        }
        else if ( change instanceof LDAPModDN ) {

            modInfo = ((LDAPModDN)change).getModInfo();
            toRecordLines( dn, modInfo, this.currentControls );

        }
        else if ( change instanceof LDAPModify) {

            mods = ((LDAPModify)change).getModifications();
            toRecordLines( dn, mods, this.currentControls );

        }
        else {
            throw new RuntimeException("Not supported change type");
        }

        writeRecordLines();
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
     *
     * @throws IOException if an I/O error occurs.
     */
    public void writeChanges(LDAPOperation[] changes) throws IOException  {

        for ( int i = 0; i < changes.length; i++ ) {
            writeChange( changes[i] );
        }

    }


    /**
     * Used to generate LDIF content record or LDIF change/add record lines.
     *
     * <p>Turn LDAPEntry object into LDIF record</p>
     *
     * @param entry  LDAPREntry object
     *
     * @throws UnsupportedEncodingException
     */
    public void toRecordLines( LDAPEntry entry )
    throws UnsupportedEncodingException {
        toRecordLines(entry, null);
    }


    /**
     * Used to generate LDIF content record or LDIF change/add record lines.
     *
     * <p>Turn LDAPEntry object and LDAPControl[] object into LDIF record
     * lines</p>
     *
     * @param entry  LDAPREntry object
     * @param ctrls  LDAPControl object
     *
     * @throws UnsupportedEncodingException
     */
    public void toRecordLines( LDAPEntry entry, LDAPControl[] ctrls )
    throws UnsupportedEncodingException {

        int      i, len;
        boolean  safeString;
        String   attrName, temp, value;
        LDAPAttribute attr;
        LDAPAttributeSet attrSet;
        Iterator allAttrs;
        Enumeration allValues;

        this.rFields.clear();

        // get dn from the entry
        this.dn = new String( entry.getDN() );

        // add dn to record fileds
        addDNToRecordFields();

        // save controls if there any
        if ( ctrls != null ) {
            addControlsToRecordFields( ctrls );
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

           allValues = attr.getStringValues();

           if( allValues != null) {
               while(allValues.hasMoreElements()) {
                   value = (String) allValues.nextElement();

                   addAttrValueToRecordFields(attrName,value);
               }
           }
        }

        // to record lines
        this.rLines = new String[this.rFields.size()];
        this.rLines = (String[])this.rFields.toArray(this.rLines);

        // to record lines each of which has no more than 80 characters
        to80charLines();

    }


    /**
     * Used to generate LDIF change/modify reocrd lines.
     *
     * <p>Turn entry DN, LDAPModification[] object into LDIF LDIF record
     * fields and then turn record fields into LDIF  change/modify record
     * lines</p>
     *
     * @param dn    String object representing entry DN
     * @param mods  LDAPModification array object
     *
     * @see LDAPModification
     * @see LDAPControl
     */
    public void toRecordLines( String dn, LDAPModification[] mods )
    throws UnsupportedEncodingException {

        toRecordLines( dn, mods, null );
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
     * @see LDAPModification
     * @see LDAPControl
     */
    public void toRecordLines( String dn, LDAPModification[] mods,
    LDAPControl[] ctrls ) throws UnsupportedEncodingException {

        int i, modOp, len = mods.length;
        String attrName, attrValue;
        LDAPAttribute attr;

        // add dn to record fileds
        addDNToRecordFields();

        // save controls if there is any
        if ( ctrls != null ) {
            addControlsToRecordFields( ctrls );
        }

        // save change type
        this.rFields.add(new String("changetype: modify"));

        // save attribute names and values
        for ( i = 0; i < len; i++ ) {

            modOp = mods[i].getOp();
            attr =  mods[i].getAttribute();
            attrName = attr.getName().trim();
            attrValue = attr.getStringValue().trim();

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

            // add attribute names and values to record fields
            addAttrValueToRecordFields(attrName, attrValue);

            // add separators between different modify operations
            this.rFields.add(new String("-"));

            // add an empty line between different modify operations
            this.rFields.add(new String(""));
        }

        // to record lines
        this.rLines = new String[this.rFields.size()];
        this.rLines = (String[])this.rFields.toArray(this.rLines);

        // to record lines each of which has no more than 80 characters
        to80charLines();
    }


    /**
     * Used to generate LDIF change/moddn reocrd lines.
     *
     * <p>Turn entry DN and ModInfo into LDIF change/modify record lines</p>
     *
     * @param dn      String object representing entry DN
     * @param modInfo ModInfo object
     *
     * @see ModInfo
     */
    public void toRecordLines( String dn, ModInfo modInfo )
    throws UnsupportedEncodingException {

        toRecordLines( dn, modInfo, null );
    }


    /**
     * Used to generate LDIF change/moddn reocrd lines.
     *
     * <p>Turn entry DN and ModInfo into LDIF change/modify record lines</p>
     *
     * @param dn      String object representing entry DN
     * @param modInfo ModInfo object
     * @param ctrls   LDAPControl array object
     *
     * @see ModInfo
     */
    public void toRecordLines( String dn, ModInfo modInfo,
    LDAPControl[] ctrls ) throws UnsupportedEncodingException {

        String tempString;

        // save entry dn
        addDNToRecordFields();

        if ( ctrls != null ) {
            addControlsToRecordFields( ctrls );
        }

        // save change type
        this.rFields.add(new String("changetype: moddn"));

        // save new RDN
        if ( isSafeString(modInfo.newRDN) ) {
            this.rFields.add(new String("newrdn:" + modInfo.newRDN));
        }
        else {
            // base64 encod newRDN
            tempString = base64Encoder.encoder(modInfo.newRDN);
            // put newRDN into record fields with a trailing space
            this.rFields.add(new String("newrdn:" + tempString + " "));
        }

        // save deleteOldRDN
        this.rFields.add(new String("deleteoldrdn:" + modInfo.deleteOldRDN));

        // save newSuperior
        if ( ((modInfo.newSuperior).length()) != 0) {

            if ( isSafeString(modInfo.newSuperior) ) {
                this.rFields.add(new String("newsuperior:" + modInfo.newSuperior));
            }
            else {
                // base64 encod newRDN
                tempString = base64Encoder.encoder(modInfo.newSuperior);
                // put newSuperior into record fields with a trailing space
                this.rFields.add(new String("newsuperior:" + tempString));
            }
        }

        // to record lines
        this.rLines = new String[this.rFields.size()];
        this.rLines = (String[])this.rFields.toArray(this.rLines);

        // to record lines each of which has no more than 80 characters
        to80charLines();
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
    public void toRecordLines( String dn, LDAPControl[] ctrls )
    throws UnsupportedEncodingException {

        // save entry dn
        addDNToRecordFields();

        // save controls if there is any
        if ( ctrls != null ) {
            addControlsToRecordFields( ctrls );
        }

        // save change type
        this.rFields.add(new String("changetype: delete"));

        // to record lines
        this.rLines = new String[this.rFields.size()];
        this.rLines = (String[])this.rFields.toArray( this.rLines );

        // to record lines each of which has no more than 80 characters
        to80charLines();
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

        this.rLines = new String[tempList.size()];
        return (String[])this.tempList.toArray(this.rLines);
    }

    /**
     * Turn any string in input String array into multiple continuation
     * lines if it contains more than 80 characters
     *
     * @param ls  The input String array object
     */
    public void to80charLines() {

        this.tempList.clear();

        // break any line that is longer than 80 chars.
        for ( int i = 0; i < this.rLines.length; i++) {
            // field length equals or less than 80, save it
            if ( this.rLines[i].length() <= 80 ) {
                this.tempList.add( this.rLines[i] );
            }
            else {
                // field length is longer than 80, break it
                while ( this.rLines[i].length() > 80 ) {
                    // first line of a record has length of 80, the
                    // substring begins at 0 and extends to 79
                    this.tempList.add( this.rLines[i].substring(0, 80) );
                    // any continuation line has length of
                    // 80 and starts with a white space
                    this.rLines[i] = new String (" "
                                               + this.rLines[i].substring(80) );
                }
                // save the last part of the field
                this.tempList.add(this.rLines[i]);
            }
        }

        this.rLines = new String[tempList.size()];
        this.rLines =  (String[])this.tempList.toArray(this.rLines);
    }



    /**
     * Add record dn into record fields.
     *
     * <p>Check if dn is base64 encoded and use either 'dn:: dn spec ' or
     * 'dn: dn spec' format</p>
     *
     * @throws UnsupportedEncodingException
     */
    public void addDNToRecordFields() throws UnsupportedEncodingException {

        Base64Encoder base64Encoder = new Base64Encoder();

        if ( !isSafeString( this.dn ) ) {
            // IF dn contains NON-SAFE-INIT-CHAR or NON-SAFE-CHAR,
            // it has to be base64 encoded
            this.dn = base64Encoder.encoder( this.dn );
            // base64 encoded dn ended with white spavce
            this.rFields.add( new String("dn:: " + dn + " "));
        }
        else {
            // add dn to record fileds
            this.rFields.add( new String("dn: " + dn ));
        }

    }


    /**
     * Convert LDAPControl array object to control fields in LDIF format
     *
     * @param ctrls LDAPControl array object
     *
     */
    public void addControlsToRecordFields(LDAPControl[] ctrls)
    throws UnsupportedEncodingException {

        boolean criticality;
        byte[]  byteValue = null;
        String  controlOID, controlValue;


        for ( int i = 0; i < ctrls.length; i++ ) {

            controlOID = ctrls[i].getID();
            criticality = ctrls[i].isCritical();
            byteValue = ctrls[i].getValue();

            if ( byteValue.length > 0 ) {
                //controlValue = new String( byteValue, "UTF8");

                // always encode control value(s) ?
                byteValue = base64Encoder.encoder( byteValue );

                controlValue = new String(byteValue, "UTF8");

                // a trailing space is add to the end of base64 encoded value
                this.rFields.add( "control: " + controlOID + " " + criticality
                              + ":: " + controlValue + " " );
            }
            else {
                this.rFields.add("control: " + controlOID + " " + criticality);
            }
        }
    }



    /**
     * Add record attribute name and value into record fields.
     *
     * <p>Check if attribute value contains NON-SAFE-INIT-CHAR or
     * NON-SAFE_CHAR. if it does, encode it</p>
     */
    public void addAttrValueToRecordFields(String attrName, String attrSpec)
    throws UnsupportedEncodingException {

        if ( !isSafeString(attrSpec) ) {
            // IF attrSpec contains NON-SAFE-INIT-CHAR or NON-SAFE-CHAR,
            // it has to be base64 encoded
            attrSpec = base64Encoder.encoder(attrSpec);
            // base64 encoded attribute spec ended with white spavce
            this.rFields.add( new String(attrName + ":: " + attrSpec + " " ));
        }
        else {
            this.rFields.add( new String(attrName + ": " + attrSpec ));
        }

    }


    /**
     * Check if the input String object is a SAFE-STRING
     *
     * @param boolean object to incidate that the string is safe or not
     */
    private boolean isSafeString( String value ) {

        int i, len = value.length();
        boolean isSafe = true;

        // is there any NON-SAFE-INIT-CHAR
        if (   (value.charAt(0) == 0x00)     // NUL
            || (value.charAt(0) == 0x0A)     // linefeeder
            || (value.charAt(0) == 0x0D)     // carrage return
            || (value.charAt(0) == 0x20)     // space(' ')
            || (value.charAt(0) == 0x3A)     // colon(':')
            || (value.charAt(0) == 0x3C)) {  // less-than('<')

            isSafe = false;

            return isSafe;
        }

        // is there any NON-SAFE-CHAR
        for ( i = 1; i < len; i++ ) {
            if (   (value.charAt(i) == 0x00)    // NUL
                || (value.charAt(i) == 0x0A)    // linefeeder
                || (value.charAt(i) == 0x0D)) { // carrage return

                isSafe = false;

                return isSafe;
            }
        }

        return isSafe;
    }

}
