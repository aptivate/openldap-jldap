/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPReferralException.java,v 1.12 2001/03/01 00:29:53 cmorris Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package org.ietf.ldap;

/**
 *  Thrown when a server returns a referral and when a referral has not
 *  been followed.
 *
 * @see com.novell.ldap.LDAPReferralException
 */
public class LDAPReferralException extends LDAPException
{
    private com.novell.ldap.LDAPReferralException exception;
    /**
     * Constructs a referral exception from a
     * com.novell.ldap.LDAPREferralException
     *
     */
    public LDAPReferralException( com.novell.ldap.LDAPReferralException ex)
    {
        super(ex);
        exception = ex;
        return;
    }

    /**
     * Constructs a default exception with no specific error information.
     *
     * @see com.novell.ldap.LDAPReferralException#LDAPReferralException()
     */
    public LDAPReferralException()
    {
        super( new com.novell.ldap.LDAPReferralException());
        exception = 
        exception = (com.novell.ldap.LDAPReferralException)
                                                       super.getWrappedObject();
        return;
    }

    /**
     * Constructs a default exception with a specified string as additional
     * information.
     *
     * @see com.novell.ldap.LDAPReferralException#LDAPReferralException(String)
     */
    public LDAPReferralException(String message)
    {
        super( new com.novell.ldap.LDAPReferralException(message));
        exception = (com.novell.ldap.LDAPReferralException)
                                                       super.getWrappedObject();
        return;
    }

    /**
     * Constructs a default exception with a specified string as additional
     * information and an exception that indicates a failure to follow a
     * referral.
     *
     * @see com.novell.ldap.LDAPReferralException#LDAPReferralException(
                String, Throwable)
     */
    public LDAPReferralException(String message,
            Throwable rootException)
    {
        super(new com.novell.ldap.LDAPReferralException(message,rootException));
        exception = (com.novell.ldap.LDAPReferralException)
                                                       super.getWrappedObject();
        return;
    }

    /**
     *
     * Constructs an exception with a specified error string, result code, and
     * an error message from the server.
     *
     * @see com.novell.ldap.LDAPReferralException#LDAPReferralException(
                    String, int, String)
     */
    public LDAPReferralException(String message,
            int resultCode,
            String serverMessage)
    {
        super(new com.novell.ldap.LDAPReferralException( message,
                                                         resultCode,
                                                         serverMessage));
        exception = (com.novell.ldap.LDAPReferralException)
                                                       super.getWrappedObject();
        return;
    }

    /**
     *
     * Constructs an exception with a specified error string, result code,
     * an error message from the server, and an exception that indicates
     * a failure to follow a referral.
     *
     * @see com.novell.ldap.LDAPReferralException#LDAPReferralException(
                String, int, String, Throwable)
     */
    public LDAPReferralException(String message,
            int resultCode,
            String serverMessage,
            Throwable rootException)
    {
        super( new com.novell.ldap.LDAPReferralException( message,
                                                          resultCode,
                                                          serverMessage,
                                                          rootException));
        exception = (com.novell.ldap.LDAPReferralException)
                                                       super.getWrappedObject();
        return;
    }

    /** Gets the referral that could not be processed.
     *
     * @see com.novell.ldap.LDAPReferralException#getFailedReferral()
     */
    public String getFailedReferral()
    {
        return exception.getFailedReferral();
    }

    /**
     * Gets the list of referrals (LDAP URLs to other servers) returned by
     * the LDAP server.
     *
     * @see com.novell.ldap.LDAPReferralException#getReferrals()
     */
    public String[] getReferrals()
    {
        return exception.getReferrals();
    }

    /**
     * Sets a referral that could not be processed
     *
     * @see com.novell.ldap.LDAPReferralException#setFailedReferral(String)
     */
    public void setFailedReferral( String url)
    {
        exception.setFailedReferral( url);
        return;
    }
}
