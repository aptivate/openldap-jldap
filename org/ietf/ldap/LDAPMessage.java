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
 *  Represents the base class for LDAP request and response messages.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html">
            com.novell.ldap.LDAPMessage</a>
 */
public class LDAPMessage {

    /**
     * A request or response message for an asynchronous LDAP operation.
     */
    private com.novell.ldap.LDAPMessage message;

    /**
     * A bind request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#BIND_REQUEST">
            com.novell.ldap.LDAPMessage.BIND_REQUEST</a>
     */
    public final static int BIND_REQUEST =
				    com.novell.ldap.LDAPMessage.BIND_REQUEST;

    /**
     * A bind response operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#BIND_RESPONSE">
            com.novell.ldap.LDAPMessage.BIND_RESPONSE</a>
     */
    public final static int BIND_RESPONSE =
				    com.novell.ldap.LDAPMessage.BIND_RESPONSE;

    /**
     * An unbind request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#UNBIND_REQUEST">
            com.novell.ldap.LDAPMessage.UNBIND_REQUEST</a>
     */
    public final static int UNBIND_REQUEST =
				    com.novell.ldap.LDAPMessage.UNBIND_REQUEST;
    /**
     * A search request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#SEARCH_REQUEST">
            com.novell.ldap.LDAPMessage.SEARCH_REQUEST</a>
     */
    public final static int SEARCH_REQUEST =
				    com.novell.ldap.LDAPMessage.SEARCH_REQUEST;

    /**
     * A search response containing data.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#SEARCH_RESPONSE">
            com.novell.ldap.LDAPMessage.SEARCH_RESPONSE</a>
     */
    public final static int SEARCH_RESPONSE =
				    com.novell.ldap.LDAPMessage.SEARCH_RESPONSE;

    /**
     * A search result message - contains search status.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#SEARCH_RESULT">
            com.novell.ldap.LDAPMessage.SEARCH_REULT</a>
     */
    public final static int SEARCH_RESULT =
				    com.novell.ldap.LDAPMessage.SEARCH_RESULT;

    /**
     * A modify request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#MODIFY_REQUEST">
            com.novell.ldap.LDAPMessage.MODIFY_REQUEST</a>
     */
    public final static int MODIFY_REQUEST =
				    com.novell.ldap.LDAPMessage.MODIFY_REQUEST;

    /**
     * A modify response operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#MODIFY_RESPONSE">
            com.novell.ldap.LDAPMessage.MODIFY_RESPONSE</a>
     */
    public final static int MODIFY_RESPONSE =
				    com.novell.ldap.LDAPMessage.MODIFY_RESPONSE;

    /**
     * An add request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#ADD_REQUEST">
            com.novell.ldap.LDAPMessage.ADD_REQUEST</a>
     */
    public final static int ADD_REQUEST =
				    com.novell.ldap.LDAPMessage.ADD_REQUEST;

    /**
     * An add response operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#ADD_RESPONSE">
            com.novell.ldap.LDAPMessage.ADD_RESPONSE</a>
     */
    public final static int ADD_RESPONSE =
				    com.novell.ldap.LDAPMessage.ADD_RESPONSE;

    /**
     * A delete request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#DEL_REQUEST">
            com.novell.ldap.LDAPMessage.DEL_REQUEST</a>
     */
    public final static int DEL_REQUEST =
				    com.novell.ldap.LDAPMessage.DEL_REQUEST;

    /**
     * A delete response operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#DEL_RESPONSE">
            com.novell.ldap.LDAPMessage.DEL_RESPONSE</a>
     */
    public final static int DEL_RESPONSE =
				    com.novell.ldap.LDAPMessage.DEL_RESPONSE;

    /**
     * A modify RDN request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#MODIFY_RDN_REQUEST">
            com.novell.ldap.LDAPMessage.MODIFY_RDN_REQUEST</a>
     */
    public final static int MODIFY_RDN_REQUEST =
				    com.novell.ldap.LDAPMessage.MODIFY_RDN_REQUEST;

    /**
     * A modify RDN response operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#MODIFY_RDN_RESPONSE">
            com.novell.ldap.LDAPMessage.MODIFY_RDN_RESPONSE</a>
     */
    public final static int MODIFY_RDN_RESPONSE =
				    com.novell.ldap.LDAPMessage.MODIFY_RDN_RESPONSE;

    /**
     * A compare result operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#COMPARE_REQUEST">
            com.novell.ldap.LDAPMessage.COMPARE_REQUEST</a>
     */
    public final static int COMPARE_REQUEST =
				    com.novell.ldap.LDAPMessage.COMPARE_REQUEST;

    /**
     * A compare response operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#COMPARE_RESPONSE">
            com.novell.ldap.LDAPMessage.COMPARE_RESPONSE</a>
     */
    public final static int COMPARE_RESPONSE =
				    com.novell.ldap.LDAPMessage.COMPARE_RESPONSE;

    /**
     * An abandon request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#ABANDON_REQUEST">
            com.novell.ldap.LDAPMessage.ABANDON_REQUEST</a>
     */
    public final static int ABANDON_REQUEST =
				    com.novell.ldap.LDAPMessage.ABANDON_REQUEST;


    /**
     * A search result reference operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#SEARCH_RESULT_REFERENCE">
            com.novell.ldap.LDAPMessage.SEARCH_RESULT_REFERENCE</a>
     */
    public final static int SEARCH_RESULT_REFERENCE = 
				    com.novell.ldap.LDAPMessage.SEARCH_RESULT_REFERENCE;

    /**
     * An extended request operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#EXTENDED_REQUEST">
            com.novell.ldap.LDAPMessage.EXTENDED_REQUEST</a>
     */
    public final static int EXTENDED_REQUEST =
				    com.novell.ldap.LDAPMessage.EXTENDED_REQUEST;

    /**
     * An extended response operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#EXTENDED_RESPONSE">
            com.novell.ldap.LDAPMessage.EXTENDED_RESPONSE</a>
     */
    public final static int EXTENDED_RESPONSE =
				    com.novell.ldap.LDAPMessage.EXTENDED_RESPONSE;

    /**
     * Creates an LDAPMessage from a com.novell.ldap.LDAPMessage object
     */
    public LDAPMessage(com.novell.ldap.LDAPMessage message)
    {
        this.message = message;
    }

    /**
     * Returns any controls in the message.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#getControls()">
            com.novell.ldap.LDAPMessage.getControls()</a>
     */
    public LDAPControl[] getControls()
    {
		com.novell.ldap.LDAPControl[] controls = message.getControls();
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#getMessageID()">
            com.novell.ldap.LDAPMessage.getMessageID()</a>
     */
    public int getMessageID() {
        return message.getMessageID();
    }

    /**
     * Returns the LDAP operation type of the message.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPMessage.html#getType()">
            com.novell.ldap.LDAPMessage.getType()</a>
     */
    public int getType()
    {
        return message.getType();
    }
}
