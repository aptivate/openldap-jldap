/**************************************************************************
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
 *  Thrown when a server returns a referral and when a referral has not
 *  been followed.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html">
            com.novell.ldap.LDAPReferralException</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#LDAPReferralException()">
            com.novell.ldap.LDAPReferralException.LDAPReferralException()</a>
     */
    public LDAPReferralException()
    {
        super( new com.novell.ldap.LDAPReferralException());
        exception = (com.novell.ldap.LDAPReferralException)
                                                       super.getWrappedObject();
        return;
    }

    /**
     * Constructs a default exception with a specified string as additional
     * information.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#LDAPReferralException(java.lang.String)">
            com.novell.ldap.LDAPReferralException.LDAPReferralException(
            String)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#LDAPReferralException(java.lang.String, java.lang.Throwable)">
            com.novell.ldap.LDAPReferralException.LDAPReferralException(
            String, Throwable)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#LDAPReferralException(java.lang.String, int, java.lang.String)">
            com.novell.ldap.LDAPReferralException.LDAPReferralException(
            String, int, String)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#LDAPReferralException(java.lang.String, int, java.lang.String, 
            java.lang.Throwable)">
            com.novell.ldap.LDAPReferralException.LDAPReferralException(
            String, int, String, Throwable)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#getFailedReferral()">
            com.novell.ldap.LDAPReferralException.getFailedReferral()</a>
     */
    public String getFailedReferral()
    {
        return exception.getFailedReferral();
    }

    /**
     * Gets the list of referrals (LDAP URLs to other servers) returned by
     * the LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#getReferrals()">
            com.novell.ldap.LDAPReferralException.getReferrals()</a>
     */
    public String[] getReferrals()
    {
        return exception.getReferrals();
    }

    /**
     * Sets a referral that could not be processed
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPReferralException.html#setFailedReferral(java.lang.String)">
            com.novell.ldap.LDAPReferralException.setFailedReferral(String)</a>
     */
    public void setFailedReferral( String url)
    {
        exception.setFailedReferral( url);
        return;
    }
}
