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

    private int recordType;
    private String[] rLines;
    private ArrayList rFields = new ArrayList();
    private LDAPEntry entry;
    private LDAPOperation change;
    private BufferedWriter bufWriter;
    private LDAPEntry currentEntry = null;
    private LDAPOperation currentChange = null;

    /**
     * Constructs an LDIFWriter object by calling super constructor and
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
     * writeVresionLine is used to write the version line of LDIF
     * file into the OutputStream
     *
     */
    public void writeVersionLine () throws IOException {

        // we are currently using 'version 1' for LDIF file
        String versionLine = new String("version: " + LDIF_VERSION_1);
        bufWriter.write( versionLine, 0, versionLine.length());
        // start a new line and then an extra line to separate
        // the version line with the rest of the content
        bufWriter.newLine();
        bufWriter.newLine();
    }

    /**
     * writeLine is used to worite a single comment line into the OutputStream,
     * an '#' char is added in the front of the line to indicate that the line
     * is a coment line
     *
     * @param line The comment line to be written to the OutputStream
     */
    public void writeCommentLine ( String line ) throws IOException {

        String commentLine;
        String[] lines;

        if ( line != null) {
            commentLine = new String( "# " + line);
            lines = toCommentLines( line );
            if ( lines.length == 1) {
                bufWriter.write( commentLine, 0, commentLine.length());
                bufWriter.newLine();
            }
            else {
                writeRecordLines( lines );
            }
        }
    }

    /**
     * writeRecordLins is used to worite lines of a record into the OutputStream
     *
     * @param Lines to be written to the OutputStream
     */
    public void writeRecordLines ( String[] lines ) throws IOException {

        if ( lines != null ) {
            for ( int i = 0; i < lines.length; i++ ) {
                bufWriter.write( lines[i], 0, (lines[i]).length());
                // start a new line
                bufWriter.newLine();
            }
            // write a new line to sepatator records
            bufWriter.newLine();
        }
    }

    /**
     * Flush the stream
     */
    public void flushStream() throws IOException {
        // flush the stream
        bufWriter.flush();
    }


    /**
     * write a content record into LDIF content file
     *
     * @param entry LDAPEntry object
     * @param ctrls LDAPControl[] object
     *
     */
    public void writeContent(LDAPEntry entry, LDAPControl[] ctrls)
    throws IOException {

        String  rLines[];
        ArrayList rFields;

        this.recordType = LDIF.CONTENT_RECORD;

        if( ! isContent()) {
            throw new RuntimeException("Cannot write change to LDIF"
                                                  + " content file");
        }

        // to content record lines
        if ( ctrls != null ) {
            rLines = toRecordLines(entry, ctrls);
        }
        else {
            rLines = toRecordLines(entry, null);
        }

        // write the content line into LDIF file
        writeRecordLines( rLines );

    }

    /**
     * Write a number of content record into LDIF content file
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
     * @see LDAPADD
     * @see LDAPDelete
     * @see LDAPModDN
     * @see LDAPModify
     *
     * @param change LDAPOperation object
     */
    public void writeChange(LDAPOperation change)
    throws IOException  {

        String       dn = change.getDN();
        String[] rLines;
        LDAPControl[] ctrls = change.getControls();
        LDAPEntry entry;
        LDAPModification[] mods;
        ModInfo modInfo;

        if ( change instanceof LDAPAdd) {

            entry = ((LDAPAdd)change).getEntry();
            rLines = toRecordLines( entry, ctrls );

        }
        else if ( change instanceof LDAPDelete ) {

            dn = new String(((LDAPDelete)change).getDN());
            rLines = toRecordLines( dn, ctrls );

        }
        else if ( change instanceof LDAPModDN ) {

            modInfo = ((LDAPModDN)change).getModInfo();
            rLines = toRecordLines( dn, modInfo, ctrls );

        }
        else if ( change instanceof LDAPModify) {

            mods = ((LDAPModify)change).getModifications();
            rLines = toRecordLines( dn, mods, ctrls );

        }
        else {
            throw new RuntimeException("Not supported change type");
        }

        writeRecordLines( rLines );
    }

    /**
     * Write a number of LDAP change record into LDIF file. The change operation
     * may be LDAPAdd, LDAPDelete, LDAPN=ModDN, or LDAPModify.
     *
     * @see LDAPADD
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
     * Flush and close the output IO stream
     */
    public void closeStream() throws IOException {
        // flush and then close the stream
        bufWriter.close();
    }

    /**
     * Used to generate LDIF change/add record lines. Turn LDAPEntry object and
     * LDAPControl object into LDIF change/add record fields and then turn
     * record fields into record lines
     *
     * @param LDAPREntry object
     * @param LDAPControl object
     *
     * @return String array that contains entry dn, control info, and
     * attribute name and value pairs
     */
    public String[] toRecordLines( LDAPEntry entry, LDAPControl[] ctrls ) {

        int      i, len;
        String   attrName,  dn, temp, value;
        String   changeType = new String("add");
        String[] controlLines = null;
        LDAPAttribute attr;
        LDAPAttributeSet attrSet;
        Iterator allAttrs;
        Enumeration allValues;

        // save entry dn field
        dn = new String( entry.getDN() );
        this.rFields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( i = 0; i < ctrls.length; i++) {
                this.rFields.add("control: " + ctrls[i].toString());
            }
        }

        this.rFields.add("changetype: " + changeType);

        // save attribute fields
        attrSet = entry.getAttributeSet();
        allAttrs = attrSet.iterator();

        while(allAttrs.hasNext()) {
           attr = (LDAPAttribute)allAttrs.next();
           attrName = attr.getName();
           temp = new String ( attrName + ": ");

           allValues = attr.getStringValues();

           if( allValues != null) {
               while(allValues.hasMoreElements()) {
                   value = (String) allValues.nextElement();
                   temp = temp + value;
                   this.rFields.add( temp );
               }
           }
        }

        this.rLines = toArray( this.rFields );

        return toLines( this.rLines );
    }



    /**
     * Used to generate LDIF modify reocrd lines. Turn entry DN,
     * LDAPModification[], and change type into LDIF modify record
     * fields and then turn record fields into LDIF modify record lines
     *
     * @param String object representing entry DN
     * @param LDAPModification[] object
     * @param int Change type
     *
     * @return String array which contains the LDIF modify record lines
     *
     * @see LDAPModification
     */
    public String[] toRecordLines( String dn,
                                   LDAPModification[] mods,
                                   LDAPControl[] ctrls ) {

        int i, len, modOp;
        String attrName, attrValue;
        LDAPAttribute attr;


        // dave entry dn
        this.rFields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( i = 0; i < ctrls.length; i++) {
                this.rFields.add("control: " + ctrls[i].toString());
            }
        }

        // save change type
        this.rFields.add(new String("changetype: modify"));

        len = mods.length;

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
            this.rFields.add(new String( attrName + ": " + attrValue));
            // add separators between different modify operations
            this.rFields.add(new String("-"));
            // add an empty line between different modify operations
            this.rFields.add(new String(""));
        }

        this.rLines = toArray( this.rFields );

       return toLines( this.rLines );
    }

    /**
     * Used to generate LDIF moddn reocrd lines. Turn entry DN,
     * ModInfo, and change type into LDIF modify record
     * fields and then turn record fields into LDIF moddn record lines
     *
     * @param String object representing entry DN
     * @param ModInfo object
     @ @param int Change type
     *
     * @return String array which contains the LDIF mosify record lines
     *
     * @see ModInfo
     */
    public String[] toRecordLines( String dn,
                                   ModInfo modInfo,
                                   LDAPControl[] ctrls ) {

        int i, len, modOp;
        String attrName, attrValue;
        LDAPAttribute attr;


        // dave entry dn
        this.rFields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( i = 0; i < ctrls.length; i++) {
                this.rFields.add("control: " + ctrls[i].toString());
            }
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
     * Used to generate LDIF change/delete reocrd lines. Turn entry DN, controls
     * and change type into LDIF change/delete record fields and then turn
     * record fields into LDIF moddn record lines
     *
     * @param dn    String object representing entry DN
     * @param ctrls LDAPControl array object
     *
     * @return String array which contains the LDIF change/delete record lines
     *
     * @see LDAPControl
     */
    public String[] toRecordLines( String dn, LDAPControl[] ctrls ) {

        int i, len, modOp;
        String attrName, attrValue;
        LDAPAttribute attr;


        // dave entry dn
        this.rFields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( i = 0; i < ctrls.length; i++) {
                this.rFields.add("control: " + ctrls[i].toString());
            }
        }

        // save change type
        this.rFields.add(new String("changetype: delete"));

        this.rLines = toArray( this.rFields );

        return toLines( this.rLines );
    }



    /**
     * turn any input string into multiple continuation lines if it contains
     * more than 80 characters
     */
    public String[] toLines( String l ) {

        int i, len;
        ArrayList tempList = new ArrayList();
        String tempLine;

        if ( l.length() <= 80 ) {
            tempList.add( l );
        }
        else {
            // break the line if it contains more 80 characters
            while ( l.length() > 80 ) {
                // first line of a record has length of 80, the
                // substring begins at 0 and extends to 79
                tempList.add( l.substring(0, 80) );
                // any continuation line has length of
                // 80 and starts with a white space
                l = new String (" " + l.substring(80) );
            }
            // save the last part of the line
            tempList.add( l );
        }

        return toArray( tempList);

    }

    /**
     * turn the input comment string into multiple comment lines if it contains
     * more than 80 characters
     *
     */
    public String[] toCommentLines( String l ) {

        ArrayList tempList = new ArrayList();
        String tempLine;

        if ( l.length() <= 80 ) {
            tempList.add( l );
        }
        else {
            // break the comment line if it contains more than 80 characters
            while ( l.length() > 80 ) {
                // first comment line has length of 80, the
                // substring begins at 0 and extends to 79
                tempList.add( "# " + l.substring(0, 78) );
                // any continuation line has length of
                // 80 and starts with a white space
                l = new String ( l.substring(78) );
            }
            // save the last part of the comment line
            tempList.add( "# " + l);
        }

        return toArray( tempList );
    }

    /**
     * turn the input String array into multiple continuation lines if
     * it contzins more than 80 characters
     */
    public String[] toLines( String[] ls ) {

        ArrayList tempList = new ArrayList();
        String tempLine, lines[];

        // break any line that is longer than 80 chars.
        for ( int i = 0; i < ls.length; i++) {
            // field length equals or less than 80, save it
            if ( ls[i].length() <= 80 ) {
                tempList.add( ls[i] );
            }
            else {
                // field length is longer than 80, break it
                while ( ls[i].length() > 80 ) {
                    // first line of a record has length of 80, the
                    // substring begins at 0 and extends to 79
                    tempList.add( ls[i].substring(0, 80) );
                    // any continuation line has length of
                    // 80 and starts with a white space
                    ls[i] = new String (" " + ls[i].substring(80) );
                }
                // save the last part of the field
                tempList.add(ls[i]);
            }
        }

        return toArray( tempList );
    }
}
