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
 *  Encapsulates a continuation reference from an asynchronous search operation.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSearchResultReference.html">
            com.novell.ldap.LDAPSearchResultReference</a>
 */
public class LDAPSearchResultReference extends LDAPMessage {

    private com.novell.ldap.LDAPSearchResultReference ref;
	/**
     * Constructs an LDAPSearchResultReference object.
     *
	 */
	/*package*/
	LDAPSearchResultReference( com.novell.ldap.LDAPSearchResultReference ref)
	{
        super(ref);
        this.ref = ref;
        return;
	}

    /**
     * Returns any URLs in the object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSearchResultReference.html#getReferrals()">
            com.novell.ldap.LDAPSearchResultReference.getReferrals()</a>
     */
    public String[] getReferrals()
    {
        return ref.getReferrals();
    }
}
