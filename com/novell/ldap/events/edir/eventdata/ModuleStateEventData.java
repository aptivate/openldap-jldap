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
 * This class represents the data for Module State Events. The event data
 * has the following encoding:-
 * 
 * <p>
 * ModuleState ::= [APPLICATION7]<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;SEQUENCE {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;connectionDN    LDAPDN,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;flags           INTEGER,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;name            LDAPString ,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;description     LDAPString ,<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;source          LDAPString <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;}
 * </p>
 * 
 * <p>
 * <b>Note: </b>Please refer the JLDAP SDK Documentation at 
 * <a href="http://developer.novell.com/ndk/jldap.htm" target="_blank">
 * http://developer.novell.com/ndk/jldap.htm</a> for details of all
 * the properties. 
 * </p>
 */

public class ModuleStateEventData implements EventResponseData {
    private final String connectionDN;
    private final int flags;
    private final String name;
    private final String description;
    private final String source;

    /**
     * Default Constructor
     *
     * @param message ASN1Object containing the encoded data as String.
     *
     * @throws IOException When decoding of message fails.
     */
    public ModuleStateEventData(final ASN1Object message)
        throws IOException {
        super();

        byte[] data = ((ASN1OctetString) message).byteValue();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        LBERDecoder decode = new LBERDecoder();

        int[] length = new int[1];
        connectionDN =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        flags = ((ASN1Integer) decode.decode(in, length)).intValue();
        name = ((ASN1OctetString) decode.decode(in, length)).stringValue();
        description =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
        source =
            ((ASN1OctetString) decode.decode(in, length)).stringValue();
    }

    /**
     * Returns the ConnectionDN.
     *
     * @return Connection DN as String.
     */
    public String getConnectionDN() {
        return connectionDN;
    }

    /**
     * Returns the Description.
     *
     * @return Description as String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the Flags.
     *
     * @return flags as int.
     */
    public int getFlags() {
        return flags;
    }

    /**
     * Returns the Module Name.
     *
     * @return Name as String.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Source String.
     *
     * @return Source as String.
     */
    public String getSource() {
        return source;
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
        buf.append(
            "[ModuleStateEvent[connectionDN=" + getConnectionDN() + "]"
        );
        buf.append("[flags=" + getFlags() + "]");
        buf.append("[Name=" + getName() + "]");
        buf.append("[Description=" + getDescription() + "]");
        buf.append("[Source=" + getSource() + "]]");

        return buf.toString();
    }
}
