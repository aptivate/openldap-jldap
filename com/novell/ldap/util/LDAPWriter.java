/* **************************************************************************
 * $Novell: LDAPWriter.java,v 1.5 2003/01/22 19:32:49 $
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
package com.novell.ldap.util;

import java.io.IOException;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;

/**
 * Describes the interfaces used by applications to write
 * LDAP messages to an output stream.
 *
 * @see LDAPReader
 */
public interface LDAPWriter
{
    /**
     * Returns version of data to write to the stream.
     *
     * @return the data version as a String value.
     */
    public String getVersion();

    /**
     * Returns the type of data to write to the stream,
     * true if request data, or false if result data.
     *
     * @return  true if the data type is request data.
     */
    public boolean isRequest();


    /**
     * Writes an LDAPMessage to the data stream.
     *
     * @param message the LDAPMesssage to write.
     *
     * @throws IOException if an I/O error occurs.
     *
     * @throws LDAPException for exceptions from LDAP
     */
     public void writeMessage( LDAPMessage message)
            throws IOException, LDAPException;

    /**
     * Writes an LDAPEntry to the data stream.
     *
     * @param entry the LDAPEntry to write.
     */
     public void writeEntry( LDAPEntry entry)
            throws IOException, LDAPException;

    /**
     * Writes an LDAPEntry to the data stream.
     *
     * @param entry the LDAPEntry to write.
     *
     * @param controls any controls associated with the entry
     *
     * @throws IOException if an I/O error occurs.
     *
     * @throws LDAPException for exceptions from LDAP
     */
     public void writeEntry( LDAPEntry entry, LDAPControl[] controls)
            throws IOException, LDAPException;

    /**
     * Writes an comments to the data stream.
     *
     * @param comments The comments to write
     */
     public void writeComments( String comments)
            throws IOException;

    /**
     * Writes an Exception to the data stream.
     *
     * @param e  Exception to be written.
     */
    public void writeError(Exception e) throws IOException;


    /**
     * Writes any remaining data to the output stream.
     */
     public void finish()
            throws IOException;
}
