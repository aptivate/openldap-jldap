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

package com.novell.ldap;

/**
 *  Represents an LDAP exception that is not a result of a server response.
 */
public class LDAPLocalException extends LDAPException
{
    /**
     * Constructs a default exception with no specific error information.
     */
    public LDAPLocalException()
    {
        super();
        return;
    }

    /**
     * Constructs a local exception with a detailed message obtained from the
     * specified <code>MessageOrKey</code> String and the result code.
     * <br>
     * The String is used either as a message key to obtain a localized
     * messsage from ExceptionMessages, or if there is no key in the
     * resource matching the text, it is used as the detailed message itself.
     *
     *  @param messageOrKey  Key to addition result information, a key into
     *                       ExceptionMessages, or the information
     *                       itself if the key doesn't exist.
     *<br><br>
     *  @param resultCode    The result code returned.
     */
    public LDAPLocalException(String messageOrKey, int resultCode)
    {
        super( messageOrKey, resultCode, (String)null);
        return;
    }

    /**
     * Constructs a local exception with a detailed message obtained from the
     * specified <code>MessageOrKey</code> String and modifying arguments.
     * Additional parameters specify the result code.
     * <br>
     * The String is used either as a message key to obtain a localized
     * messsage from ExceptionMessages, or if there is no key in the
     * resource matching the text, it is used as the detailed message itself.
     * <br>
     * The message in the default locale is built with the supplied arguments,
     * which are saved to be used for building messages for other locales.
     *
     *  @param messageOrKey  Key to addition result information, a key into
     *                       ExceptionMessages, or the information
     *                       itself if the key doesn't exist.
     *<br><br>
     *  @param arguments    The modifying arguments to be included in the
     *                       message string.
     *<br><br>
     *  @param resultCode    The result code returned.
     */
    public LDAPLocalException( String messageOrKey,
                               Object[] arguments,
                               int resultCode)
    {
      super( messageOrKey, arguments, resultCode, (String)null) ;
      return;
    }

    /**
     * Constructs a local exception with a detailed message obtained from the
     * specified <code>MessageOrKey</code> String.
     * Additional parameters specify the result code and a rootException which
     * is the underlying cause of an error on the client.
     * <br>
     * The String is used either as a message key to obtain a localized
     * messsage from ExceptionMessages, or if there is no key in the
     * resource matching the text, it is used as the detailed message itself.
     *
     *  @param messageOrKey  Key to addition result information, a key into
     *                       ExceptionMessages, or the information
     *                       itself if the key doesn't exist.
     *<br><br>
     *  @param resultCode    The result code returned.
     *<br><br>
     *  @param rootException  A throwable which is the underlying cause
     *                        of the LDAPException.
     */
    public LDAPLocalException( String messageOrKey,
                               int resultCode,
                               Throwable rootException)
    {
      super( messageOrKey, resultCode, null, rootException);
      return;
    }

    /**
     * Constructs a local exception with a detailed message obtained from the
     * specified <code>MessageOrKey</code> String and modifying arguments.
     * Additional parameters specify the result code
     * and a rootException which is the underlying cause of an error
     * on the client.
     * <br>
     * The String is used either as a message key to obtain a localized
     * messsage from ExceptionMessages, or if there is no key in the
     * resource matching the text, it is used as the detailed message itself.
     * <br>
     * The message in the default locale is built with the supplied arguments,
     * which are saved to be used for building messages for other locales.
     *
     *  @param messageOrKey  Key to addition result information, a key into
     *                       ExceptionMessages, or the information
     *                       itself if the key doesn't exist.
     *<br><br>
     *  @param arguments    The modifying arguments to be included in the
     *                       message string.
     *<br><br>
     *  @param resultCode    The result code returned.
     *<br><br>
     *  @param rootException  A throwable which is the underlying cause
     *                        of the LDAPException.
     */
    public LDAPLocalException( String messageOrKey,
                               Object[] arguments,
                               int resultCode,
                               Throwable rootException)
    {
        super(messageOrKey, arguments, resultCode, null, rootException);
        return;
    }

    /**
     * returns a string of information about the exception and the
     * the nested exceptions, if any.
     */
    public String toString()
    {
        // Format the basic exception information
        return getExceptionString("LDAPLocalException");
    }
}
