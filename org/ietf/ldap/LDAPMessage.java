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
 * @see com.novell.ldap.LDAPMessage
 */
public class LDAPMessage {

    /**
     * A request or response message for an asynchronous LDAP operation.
     */
    private com.novell.ldap.LDAPMessage message;

    /**
     * A bind request operation.
     *
     * @see com.novell.ldap.LDAPMessage#BIND_REQUEST
     */
    public final static int BIND_REQUEST =
				    com.novell.ldap.LDAPMessage.BIND_REQUEST;

    /**
     * A bind response operation.
     *
     * @see com.novell.ldap.LDAPMessage#BIND_RESPONSE
     */
    public final static int BIND_RESPONSE =
				    com.novell.ldap.LDAPMessage.BIND_RESPONSE;

    /**
     * An unbind request operation.
     *
     * @see com.novell.ldap.LDAPMessage#UNBIND_REQUEST
     */
    public final static int UNBIND_REQUEST =
				    com.novell.ldap.LDAPMessage.UNBIND_REQUEST;
    /**
     *
     * @see com.novell.ldap.LDAPMessage#SEARCH_REQUEST
     * A search request operation.
     */
    public final static int SEARCH_REQUEST =
				    com.novell.ldap.LDAPMessage.SEARCH_REQUEST;

    /**
     *
     * @see com.novell.ldap.LDAPMessage#SEARCH_RESPONSE
     * A search response containing data.
     */
    public final static int SEARCH_RESPONSE =
				    com.novell.ldap.LDAPMessage.SEARCH_RESPONSE;

    /**
     * A search result message - contains search status.
     *
     * @see com.novell.ldap.LDAPMessage#SEARCH_RESULT
     */
    public final static int SEARCH_RESULT =
				    com.novell.ldap.LDAPMessage.SEARCH_RESULT;

    /**
     * A modify request operation.
     *
     * @see com.novell.ldap.LDAPMessage#MODIFY_REQUEST
     */
    public final static int MODIFY_REQUEST =
				    com.novell.ldap.LDAPMessage.MODIFY_REQUEST;

    /**
     * A modify response operation.
     *
     * @see com.novell.ldap.LDAPMessage#MODIFY_RESPONSE
     */
    public final static int MODIFY_RESPONSE =
				    com.novell.ldap.LDAPMessage.MODIFY_RESPONSE;

    /**
     * An add request operation.
     *
     * @see com.novell.ldap.LDAPMessage#ADD_REQUEST
     */
    public final static int ADD_REQUEST =
				    com.novell.ldap.LDAPMessage.ADD_REQUEST;

    /**
     * An add response operation.
     *
     * @see com.novell.ldap.LDAPMessage#ADD_RESPONSE
     */
    public final static int ADD_RESPONSE =
				    com.novell.ldap.LDAPMessage.ADD_RESPONSE;

    /**
     * A delete request operation.
     *
     * @see com.novell.ldap.LDAPMessage#DEL_REQUEST
     */
    public final static int DEL_REQUEST =
				    com.novell.ldap.LDAPMessage.DEL_REQUEST;

    /**
     * A delete response operation.
     *
     * @see com.novell.ldap.LDAPMessage#DEL_RESPONSE
     */
    public final static int DEL_RESPONSE =
				    com.novell.ldap.LDAPMessage.DEL_RESPONSE;

    /**
     * A modify RDN request operation.
     *
     * @see com.novell.ldap.LDAPMessage#MODIFY_RDN_REQUEST
     */
    public final static int MODIFY_RDN_REQUEST =
				    com.novell.ldap.LDAPMessage.MODIFY_RDN_REQUEST;

    /**
     * A modify RDN response operation.
     *
     * @see com.novell.ldap.LDAPMessage#MODIFY_RDN_RESPONSE
     */
    public final static int MODIFY_RDN_RESPONSE =
				    com.novell.ldap.LDAPMessage.MODIFY_RDN_RESPONSE;

    /**
     * A compare result operation.
     *
     * @see com.novell.ldap.LDAPMessage#COMPARE_REQUEST
     */
    public final static int COMPARE_REQUEST =
				    com.novell.ldap.LDAPMessage.COMPARE_REQUEST;

    /**
     * A compare response operation.
     *
     * @see com.novell.ldap.LDAPMessage#COMPARE_RESPONSE
     */
    public final static int COMPARE_RESPONSE =
				    com.novell.ldap.LDAPMessage.COMPARE_RESPONSE;

    /**
     * An abandon request operation.
     *
     * @see com.novell.ldap.LDAPMessage#ABANDON_REQUEST
     */
    public final static int ABANDON_REQUEST =
				    com.novell.ldap.LDAPMessage.ABANDON_REQUEST;


    /**
     * A search result reference operation.
     *
     * @see com.novell.ldap.LDAPMessage#SEARCH_RESULT_REFERENCE
     */
    public final static int SEARCH_RESULT_REFERENCE = 
				    com.novell.ldap.LDAPMessage.SEARCH_RESULT_REFERENCE;

    /**
     * An extended request operation.
     *
     * @see com.novell.ldap.LDAPMessage#EXTENDED_REQUEST
     */
    public final static int EXTENDED_REQUEST =
				    com.novell.ldap.LDAPMessage.EXTENDED_REQUEST;

    /**
     * An extended response operation.
     *
     * @see com.novell.ldap.LDAPMessage#EXTENDED_RESPONSE
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
     * @see com.novell.ldap.LDAPMessage#getControls()
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
     * @see com.novell.ldap.LDAPMessage#getMessageID()
     */
    public int getMessageID() {
        return message.getMessageID();
    }

    /**
     * Returns the LDAP operation type of the message.
     *
     * @see com.novell.ldap.LDAPMessage#getType()
     */
    public int getType()
    {
        return message.getType();
    }
}
