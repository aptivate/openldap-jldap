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
 * @see com.novell.ldap.LDAPResponse
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
     * @see com.novell.ldap.LDAPResponse#getErrorMessage()
     */
    public String getErrorMessage()
    {
        return response.getErrorMessage();
    }

    /**
     * Returns the partially matched DN field from the server response,
     * if the response contains one.
     *
     * @see com.novell.ldap.LDAPResponse#getMatchedDN()
     */
    public String getMatchedDN()
    {
        return response.getMatchedDN();
    }

    /**
     * Returns all referrals in a server response, if the response contains any.
     *
     * @see com.novell.ldap.LDAPResponse#getReferrals()
     */
    public String[] getReferrals()
    {
        return response.getReferrals();
    }

    /**
     * Returns the result code in a server response.
     *
     * @see com.novell.ldap.LDAPResponse#getResultCode()
     */
    public int getResultCode()
    {
        return response.getResultCode();
    }

    /* Methods from LDAPMessage */

    /**
     * Returns any controls in the message.
     *
     * @see com.novell.ldap.LDAPResponse#getControls()
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
     * @see com.novell.ldap.LDAPResponse#getMessageID()
     */
    public int getMessageID() {
        return response.getMessageID();
    }

    /**
     * Returns the LDAP operation type of the message.
     *
     * @see com.novell.ldap.LDAPResponse#getMessageID()
     */
    public int getType()
	{
        return response.getType();
    }
}
