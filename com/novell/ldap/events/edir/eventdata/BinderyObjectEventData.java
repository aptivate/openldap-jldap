/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999-2002 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.events.edir.eventdata;

import com.novell.ldap.asn1.ASN1Integer;
import com.novell.ldap.asn1.ASN1Object;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.LBERDecoder;
import com.novell.ldap.events.edir.EventResponseData;

import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * This class represents the data for Bindery Events. The event data has
 * the following encoding:-
 * 
 * <p>
 * BinderyObjectInfo [APPLICATION 5]::= <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;entry        LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;type         INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;emuObjFlags  INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;security     INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;name          LDAPString (48)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 */
public class BinderyObjectEventData implements EventResponseData {
    private final String entryDN;
    private final int type;
    private final int emuObjFlags;
    private final int security;
    private final String name;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public BinderyObjectEventData(final ASN1Object message)
        throws IOException {
        super();

        byte[] data = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];

        entryDN =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        type = ((ASN1Integer) decode.decode(in, length)).intValue();
        emuObjFlags = ((ASN1Integer) decode.decode(in, length)).intValue();
        security = ((ASN1Integer) decode.decode(in, length)).intValue();
        name = ((ASN1OctetString) decode.decode(in, length)).stringValue();
    }

    /**
     * Get the EmuObj Flag Property.
     *
     * @return Emu Flags as integer.
     */
    public int getEmuObjFlags() {
        return emuObjFlags;
    }

    /**
     * Gets the EntryDn as this event.
     *
     * @return Entry DN as String.
     */
    public String getEntryDN() {
        return entryDN;
    }

    /**
     * Returns the Name.
     *
     * @return Name as String.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the security Flags.
     *
     * @return Security as integer.
     */
    public int getSecurity() {
        return security;
    }

    /**
     * Returns the Event Type.
     *
     * @return event Type as integer.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[BinderyObjectEvent[EntryDn=" + getEntryDN() + "]");
        buf.append("[Type=" + getType() + "]");
        buf.append("[EnumOldFlags=" + getEmuObjFlags() + "]");
        buf.append("[Secuirty=" + getSecurity() + "]");
        buf.append("[Name=" + getName() + "]]");

        return buf.toString();
    }
}
