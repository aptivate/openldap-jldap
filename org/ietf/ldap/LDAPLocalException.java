/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
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

package org.ietf.ldap;

/**
 *  Represents an LDAP exception that is not a result of a server response.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPException.html">
            com.novell.ldap.LDAPException</a>
 */

public class LDAPLocalException extends LDAPException
{
    /**
     * Constructs a default local exception with no specific error information.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPLocalException.html#LDAPLocalException()">
            com.novell.ldap.LDAPException.LDAPLocalException()</a>
     */
    public LDAPLocalException()
    {
        super();
        return;
    }

    /**
     * Constructs an LDAPException class using com.novell.ldap.LDAPLocalException
     */
    /* package */
    LDAPLocalException( com.novell.ldap.LDAPLocalException ex)
    {
        super(ex);
        return;
    }

    /**
     * Constructs an exception with a detailed message
     * String and the result code.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPLocalException.html#LDAPLocalException(java.lang.String, int)">
            com.novell.ldap.LDAPLocalException.LDAPLocalException(String, int)</a>
     */
    public LDAPLocalException( String message,
                               int resultCode)
    {
        super(message, resultCode, null);
        return;
    }

    /**
     * Constructs an exception with a detailed message String, the
     * result code, and the root exception.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPLocalException.html#LDAPLocalException(java.lang.String, int, java.lang.Throwable)">
            com.novell.ldap.LDAPLocalException.LDAPLocalException(String, int,
            Throwable)</a>
     */
    public LDAPLocalException( String message,
                               int resultCode,
                               Throwable rootException)
    {
        super(message, resultCode, null, rootException);
        return;
    }
}
