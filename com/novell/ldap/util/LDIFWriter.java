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

    public void writeChange(LDAPOperation change) throws IOException  {
        return;
    }

    public void writeChanges(LDAPOperation[] changes) throws IOException  {
        return;
    }

    public void writeContent(LDAPEntry entry) throws IOException  {
        ArrayList fields;
        String[]  recordFields;
        recordFields = toRecordLines( entry );
    }

    public void writeContents(LDAPEntry[] entries) throws IOException  {
        return;
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
     * writeLine is used to worite a single line into the OutputStream
     *
     * @param line to be written to the OutputStream
     */
    public void writeLine ( String line ) throws IOException {
        
        if ( line != null) {
            bufWriter.write( line, 0, line.length());
            bufWriter.newLine();
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
     * writeContent is used to worite lines of a record into the OutputStream
     *
     * @param Lines to be written to the OutputStream
     */
    public void writeContent ( String[] lines ) throws IOException {
        for ( int i = 0; i < lines.length; i++ ) {
            bufWriter.write( lines[i], 0, (lines[i]).length());
            // start a new line
            bufWriter.newLine();
        }
        // write a new line to sepatator records
        bufWriter.newLine();
    }

    /**
     * Flush the stream
     */
    public void flushStream() throws IOException {
        // flush the stream
        bufWriter.flush();
    }
    
    /**
     * Flush and close the stream
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

        String   attrName,  dn, temp, value;
        String   changeType = new String("add");
        String[] controlLines = null;
        String[] entryLines = null;                      
        ArrayList fields = new ArrayList();
        LDAPAttribute attr;
        LDAPAttributeSet attrSet;
        Enumeration allAttrs, allValues;

        // save entry dn field
        dn = new String( entry.getDN() );
        fields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( int i = 0; i < ctrls.length; i++) {
                fields.add("control: " + ctrls[i].toString());
            }
        }
        
        fields.add("changetype: " + changeType);        

        // save attribute fields
        attrSet = entry.getAttributeSet();
        allAttrs = attrSet.getAttributes();

        while(allAttrs.hasMoreElements()) {
           attr = (LDAPAttribute)allAttrs.nextElement();
           attrName = attr.getName();
           temp = new String ( attrName + ": ");

           allValues = attr.getStringValues();

           if( allValues != null) {
               while(allValues.hasMoreElements()) {
                   value = (String) allValues.nextElement();
                   temp = temp + value;
                   fields.add( temp );
               }
           }
        }
        return toLines( fields );
    }
    
    /**
     * Turn LDAPEntry object into LDIF content record fields and then
     * turn record fields into record lines
     *
     * @param LDAPREntry object
     *
     * @return String array that contains entry dn and attribute name
     * and value pairs
     *
     * @see LDAPEntry
     */
    public String[] toRecordLines( LDAPEntry entry ) {

        String dn, attrName, value, temp, lines[];
        ArrayList fields = new ArrayList();
        LDAPAttribute attr;
        LDAPAttributeSet attrSet;
        Enumeration allAttrs;
        Enumeration allValues;

        // get entry dn
        dn = new String( entry.getDN() );
        fields.add( new String("dn: " + dn ));

        // get attributes of that entry
        attrSet = entry.getAttributeSet();
        allAttrs = attrSet.getAttributes();

        while(allAttrs.hasMoreElements()) {
           attr = (LDAPAttribute)allAttrs.nextElement();
           attrName = attr.getName();
           temp = new String ( attrName + ": ");

           allValues = attr.getStringValues();

           if( allValues != null) {
               while(allValues.hasMoreElements()) {
                   value = (String) allValues.nextElement();
                   temp = temp + value;
                   fields.add( temp );
               }
           }
        }
        return toLines( fields );
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
     * @return String array which contains the LDIF mosify record lines
     *
     * @see LDAPModification
     */
    public String[] toRecordLines( String dn, LDAPModification[] mods, LDAPControl[] ctrls ) {

        int i, len, modOp;
        String attrName, attrValue;
        ArrayList fields = new ArrayList();
        LDAPAttribute attr;
        

        // dave entry dn
        fields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( i = 0; i < ctrls.length; i++) {
                fields.add("control: " + ctrls[i].toString());
            }
        }

        // save change type
        fields.add(new String("changetype: modify"));

        len = mods.length;

        for ( i = 0; i < len; i++ ) {
            
            modOp = mods[i].getOp();
            attr =  mods[i].getAttribute();
            attrName = attr.getName();
            attrValue = attr.getStringValue();
            switch ( modOp )  {
                case LDAPModification.ADD:                    
                    fields.add(new String("add: "+ attrName));
                    //fields.add(new String( attrName + ": " + attrValue));
                    break;
                case LDAPModification.DELETE:
                    fields.add(new String("delete: "+ attrName));
                    break;
                case LDAPModification.REPLACE:
                    fields.add(new String("replace: "+ attrName));
                    break;
                default:
            }
            fields.add(new String( attrName + ": " + attrValue));
            // add separators between different modify operations
            fields.add(new String("-"));
            // add an empty line between different modify operations
            fields.add(new String(""));
        }

        return toLines( fields );
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
    public String[] toRecordLines( String dn, ModInfo modInfo, LDAPControl[] ctrls ) {

        int i, len, modOp;
        String attrName, attrValue;
        ArrayList fields = new ArrayList();
        LDAPAttribute attr;
        

        // dave entry dn
        fields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( i = 0; i < ctrls.length; i++) {
                fields.add("control: " + ctrls[i].toString());
            }
        }

        // save change type
        fields.add(new String("changetype: moddn"));

        // save moddn info
        fields.add(new String("newrdn:" + modInfo.newRDN));
        fields.add(new String("deleteoldrdn:" + modInfo.deleteOldRDN));
        if ( ((modInfo.newSuperior).length()) != 0) {
            fields.add(new String("newsuperior:" + modInfo.newSuperior));
        }

        return toLines( fields );
    }

    /**
     * Used to generate LDIF change/delete reocrd lines. Turn entry DN, controls 
     * and change type into LDIF change/delete record fields and then turn 
     * record fields into LDIF moddn record lines
     *
     * @param dn object representing entry DN
     * @param   LDAPControl array object
     *
     * @return String array which contains the LDIF change/delete record lines
     *
     * @see DN
     */
    public String[] toRecordLines( DN dn, LDAPControl[] ctrls ) {

        int i, len, modOp;
        String attrName, attrValue;
        ArrayList fields = new ArrayList();
        LDAPAttribute attr;
        

        // dave entry dn
        fields.add( new String("dn: " + dn ));

        // save control fields
        if ( ctrls != null ) {

            for ( i = 0; i < ctrls.length; i++) {
                fields.add("control: " + ctrls[i].toString());
            }
        }

        // save change type
        fields.add(new String("changetype: delete"));       

        return toLines( fields );
    }

    /**
     * Turn record fields into record lines
     *
     * @param ArrayList object that holds the reocrd fields
     *
     * @return String array that contains the record lines
     */
    public String[] toLines( ArrayList al ) {

        int i, j, len;
        ArrayList tempList = new ArrayList();
        String tempLine, fields[], lines[];

        len = al.size();
        fields = new String[ len ];

        // turn input ArrayList object into String array
        for ( i = 0; i < len; i++) {
            fields[i] = (String)al.get(i);
        }

        // break any field that is longer than 80 chars.
        for (i=0;i<len;i++) {
            // field length equals or less than 80, save it
            if ( fields[i].length() <= 80 ) {
                tempList.add( fields[i] );
            }
            else {
                // field length is longer than 80, break it
                while ( fields[i].length() > 80 ) {
                    // first line of a record has length of 80, the
                    // substring begins at 0 and extends to 79
                    tempList.add( fields[i].substring(0, 80) );
                    // any continuation line has length of
                    // 80 and starts with a white space
                    fields[i] = new String (" " + fields[i].substring(80) );
                }
                // save the last part of the field
                tempList.add(fields[i]);
            }
        }

        len = tempList.size();
        lines = new String[len];

        // turn ArrayList object into String array
        for ( i = 0; i < len; i++) {
            lines[i] = (String)tempList.get(i);
        }

        return lines;
    }
}