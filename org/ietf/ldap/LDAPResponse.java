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
 *  Represents the a message received from an LDAPServer
 *  in response to an asynchronous request.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html">
            com.novell.ldap.LDAPResponse</a>
 */
public class LDAPResponse extends LDAPMessage
{
    private com.novell.ldap.LDAPResponse response;

    /**
     * Creates an LDAPResponse from a com.novell.ldap.LDAPResponse object
     */
    public LDAPResponse( com.novell.ldap.LDAPResponse response)
    {
        super( response);
        this.response = response;
        return;
    }

    /**
     * Returns any error message in the response.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html#getErrorMessage()">
            com.novell.ldap.LDAPResponse.getErrorMessage()</a>
     */
    public String getErrorMessage()
    {
        return response.getErrorMessage();
    }

    /**
     * Returns the partially matched DN field from the server response,
     * if the response contains one.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html#getMatchedDN()">
            com.novell.ldap.LDAPResponse.getMatchedDN()</a>
     */
    public String getMatchedDN()
    {
        return response.getMatchedDN();
    }

    /**
     * Returns all referrals in a server response, if the response contains any.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html#getReferrals()">
            com.novell.ldap.LDAPResponse.getReferrals()</a>
     */
    public String[] getReferrals()
    {
        return response.getReferrals();
    }

    /**
     * Returns the result code in a server response.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html#getResultCode()">
            com.novell.ldap.LDAPResponse.getResultCode()</a>
     */
    public int getResultCode()
    {
        return response.getResultCode();
    }

    /* Methods from LDAPMessage */

    /**
     * Returns any controls in the message.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html#getControls()">
            com.novell.ldap.LDAPResponse.getControls()</a>
     */
    public LDAPControl[] getControls() {
		com.novell.ldap.LDAPControl[] controls = response.getControls();
        if( controls == null) {
            return null;
        }

        LDAPControl[] ietfControls = new LDAPControl[controls.length];

        for( int i=0; i < controls.length; i++) {
         ietfControls[i] = new LDAPControl( controls[i]);
        }
        return ietfControls;
    }

    /**
     * Returns the message ID.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html#getMessageID()">
            com.novell.ldap.LDAPResponse.getMessageID()</a>
     */
    public int getMessageID() {
        return response.getMessageID();
    }

    /**
     * Returns the LDAP operation type of the message.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPResponse.html#getType()">
            com.novell.ldap.LDAPResponse.getType()</a>
     */
    public int getType()
	{
        return response.getType();
    }
}
