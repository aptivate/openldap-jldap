/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPResponse.java,v 1.23 2001/01/03 18:46:21 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
 
package com.novell.ldap;

import java.io.IOException;
import java.util.Vector;

import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.client.*;

/**
 *  Represents the a message received from an LDAPServer
 *  in response to an asynchronous request.
 */
public class LDAPResponse extends LDAPMessage
{
    private LocalException exception = null;

    /**
     * Creates an LDAPMessage using an LDAPException
     *
     *  @param message  The exception
     */
    public LDAPResponse( LocalException ex)
    {
        exception = ex;
        return;
    }

    /**
     * Creates an LDAPMessage when receiving an asynchronous response from a
     * server.
     *
     *  @param message  The RfcLDAPMessage from a server.
     */
    /*package*/ LDAPResponse( RfcLDAPMessage message)
    {
        super(message);
        return;
    }

    /**
     * Returns any error message in the response.
     *
     * @return Any error message in the response.
     */
    public String getErrorMessage()
    {
        if( exception != null) {
            return( exception.getLDAPErrorMessage());
        }
        return ((RfcResponse)message.getProtocolOp()).getErrorMessage().getString();
    }

    /**
     * Returns the partially matched DN field from the server response, 
     * if the response contains one.
     *
     * @return The partially matched DN field, if the response contains one.
     *         
     */
    public String getMatchedDN()
    {
        if( exception != null) {
            return null;
        }
        return ((RfcResponse)message.getProtocolOp()).getMatchedDN().getString();
    }

    /**
     * Returns all referrals in a server response, if the response contains any.
     *
     * @return All the referrals in the server response.
     */
    public String[] getReferrals()
    {
        if( exception != null) {
            return null;
        }
        
        String[] referrals = null;
        RfcReferral ref = ((RfcResponse)message.getProtocolOp()).getReferral();
        
        if(ref != null) {
            // convert RFC 2251 Referral to String[]
            int size = ref.size();
            referrals = new String[size];
            for(int i=0; i<size; i++) {
                referrals[i] = new String(((ASN1OctetString)ref.get(i)).getContent());
            }
        }
        return referrals;
   }

    /**
     * Returns the result code in a server response.
     *
     * <p> For a list of result codes, see the LDAPException class. </p>
     *
     * @return The result code.
     */
    public int getResultCode()
    {
        if( exception != null) {
            return exception.getLDAPResultCode();
        }
        return ((RfcResponse)message.getProtocolOp()).getResultCode().getInt();
    }

    /**
     * Checks the resultCode and throws the appropriate exception.
     */
    /* package */
    void chkResultCode() throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.messages, "LDAPResponse: message(" +
                getMessageID() + ") result code " + getResultCode());                
        }
        LDAPException ex = getResultException();
        if( ex == null)
            return;
        else
            throw ex;
    } 

    /**
     * Checks the resultCode and generates the appropriate exception or 
     * null if success.
     */
    /* package */
    LDAPException getResultException()
    {
        LDAPException ex = null;
        switch(getResultCode()) {
        case LDAPException.SUCCESS:
            break;
        case LDAPException.OPERATIONS_ERROR:
        case LDAPException.PROTOCOL_ERROR:
        case LDAPException.TIME_LIMIT_EXCEEDED:
        case LDAPException.SIZE_LIMIT_EXCEEDED:
        case LDAPException.AUTH_METHOD_NOT_SUPPORTED:
        case LDAPException.STRONG_AUTH_REQUIRED:
        case LDAPException.LDAP_PARTIAL_RESULTS:
        case LDAPException.ADMIN_LIMIT_EXCEEDED:
        case LDAPException.UNAVAILABLE_CRITICAL_EXTENSION:
        case LDAPException.CONFIDENTIALITY_REQUIRED:
        case LDAPException.SASL_BIND_IN_PROGRESS:
        case LDAPException.NO_SUCH_ATTRIBUTE:
        case LDAPException.UNDEFINED_ATTRIBUTE_TYPE:
        case LDAPException.INAPPROPRIATE_MATCHING:
        case LDAPException.CONSTRAINT_VIOLATION:
        case LDAPException.ATTRIBUTE_OR_VALUE_EXISTS:
        case LDAPException.INVALID_ATTRIBUTE_SYNTAX:
        case LDAPException.NO_SUCH_OBJECT:
        case LDAPException.ALIAS_PROBLEM:
        case LDAPException.INVALID_DN_SYNTAX:
        case LDAPException.IS_LEAF:
        case LDAPException.ALIAS_DEREFERENCING_PROBLEM:
        case LDAPException.INAPPROPRIATE_AUTHENTICATION:
        case LDAPException.INVALID_CREDENTIALS:
        case LDAPException.INSUFFICIENT_ACCESS_RIGHTS:
        case LDAPException.BUSY:
        case LDAPException.UNAVAILABLE:
        case LDAPException.UNWILLING_TO_PERFORM:
        case LDAPException.LOOP_DETECT:
        case LDAPException.NAMING_VIOLATION:
        case LDAPException.OBJECT_CLASS_VIOLATION:
        case LDAPException.NOT_ALLOWED_ON_NONLEAF:
        case LDAPException.NOT_ALLOWED_ON_RDN:
        case LDAPException.ENTRY_ALREADY_EXISTS:
        case LDAPException.OBJECT_CLASS_MODS_PROHIBITED:
        case LDAPException.AFFECTS_MULTIPLE_DSAS:
        case LDAPException.OTHER:
        case LDAPException.SERVER_DOWN:
        case LDAPException.LOCAL_ERROR:
        case LDAPException.ENCODING_ERROR:
        case LDAPException.DECODING_ERROR:
        case LDAPException.LDAP_TIMEOUT:
        case LDAPException.AUTH_UNKNOWN:
        case LDAPException.FILTER_ERROR:
        case LDAPException.USER_CANCELLED:
        case LDAPException.PARAM_ERROR:
        case LDAPException.NO_MEMORY:
        case LDAPException.CONNECT_ERROR:
        case LDAPException.LDAP_NOT_SUPPORTED:
        case LDAPException.CONTROL_NOT_FOUND:
        case LDAPException.NO_RESULTS_RETURNED:
        case LDAPException.MORE_RESULTS_TO_RETURN:
        case LDAPException.CLIENT_LOOP:
        case LDAPException.REFERRAL_LIMIT_EXCEEDED:
            ex = new LDAPException(getErrorMessage(),
                getResultCode(), getMatchedDN());
            break;
        case LDAPException.REFERRAL:
            // only throw this if automatic referral handling has not been
            // enabled.
            String[] refs = getReferrals();
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, "LDAPResponse: Generating RfcReferral Exception");
                for( int i = 0; i < refs.length; i++) {
                    Debug.trace( Debug.messages, "LDAPResponse: \t" + refs[i]);
                }
            }
            ex = new LDAPReferralException(null, LDAPException.REFERRAL, refs);
            break;
        default: // unknown
            ex = new LDAPException(getErrorMessage(),
                getResultCode(), getMatchedDN());
            break;
        }
        return ex;
    }
    
    /* Methods from LDAPMessage */

    /**
     * Returns any controls in the message.
     *
     * @see com.novell.ldap.LDAPMessage#getControls()
     */
    public LDAPControl[] getControls() {
        if( exception != null) {
            return null;
        }
        return super.getControls();
    }
    /**
     * Returns the message ID.
     *
     * @see com.novell.ldap.LDAPMessage#getMessageID()
     */
    public int getMessageID() {
        if( exception != null) {
            return exception.getMessageID();
       }
        return super.getMessageID();
    }
    
    /**
     * Returns the LDAP operation type of the message. 
     *
     * @return The operation type of the message.
     *
     * @see com.novell.ldap.LDAPMessage#getType()
     */
    public int getType()
	{
        if( exception != null) {
           return exception.getReplyType();
        }
		return super.getType();
    }
}
