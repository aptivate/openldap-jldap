/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2002 - 2003 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.util;

import java.io.IOException;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;

/**
 * Describes the interfaces used by applications to read
 * LDAP messages from an input source.
 *
 * @see LDAPWriter
 */
public interface LDAPReader
{
    /**
     * Returns version of data format being read from the source.
     *
     * @return the data version as a String value.
     */
    public String getVersion();

    /**
     * Returns the type of messages being read from the source,
     * true if request data or false if result data.
     *
     * @return  true if the data type is request data.
     */
    public boolean isRequest();

    /**
     * Reads an LDAPMessage from the data source.
     *
     * @return the LDAPMesssage read from the input source.
     */
     public LDAPMessage readMessage()
            throws LDAPException, IOException;
}
