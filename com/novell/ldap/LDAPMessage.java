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

import com.novell.ldap.rfc2251.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.RespControlVector;
import com.novell.ldap.client.Debug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

/**
 * The base class for LDAP request and response messages.
 *
 * <p>Subclassed by response messages used in asynchronous operations.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/
 *jldap_sample/asynchronous/Searchas.java.html">Searchas.java</a></p>
 *
 */
public class LDAPMessage
{

    /**
     * A bind request operation.
     *
     *<p>BIND_REQUEST = 0</p>
     */
    public final static int BIND_REQUEST            = 0;

    /**
     * A bind response operation.
     *
     *<p>BIND_RESPONSE = 1</p>
     */
    public final static int BIND_RESPONSE           = 1;

    /**
     * An unbind request operation.
     *
     *<p>UNBIND_REQUEST = 2</p>
     */
    public final static int UNBIND_REQUEST          = 2;

    /**
     * A search request operation.
     *
     *<p>SEARCH_REQUEST = 3</p>
     */
    public final static int SEARCH_REQUEST          = 3;

    /**
     * A search response containing data.
     *
     *<p>SEARCH_RESPONSE = 4</p>
     */
    public final static int SEARCH_RESPONSE         = 4;

    /**
     * A search result message - contains search status.
     *
     *<p>SEARCH_RESULT = 5</p>
     */
    public final static int SEARCH_RESULT           = 5;

    /**
     * A modify request operation.
     *
     *<p>MODIFY_REQUEST = 6</p>
     */
    public final static int MODIFY_REQUEST          = 6;

    /**
     * A modify response operation.
     *
     *<p>MODIFY_RESPONSE = 7</p>
     */
    public final static int MODIFY_RESPONSE         = 7;

    /**
     * An add request operation.
     *
     *<p>ADD_REQUEST = 8</p>
     */
    public final static int ADD_REQUEST             = 8;

    /**
     * An add response operation.
     *
     *<p>ADD_RESONSE = 9</p>
     */
    public final static int ADD_RESPONSE            = 9;

    /**
     * A delete request operation.
     *
     *<p>DEL_REQUEST = 10</p>
     */
    public final static int DEL_REQUEST             = 10;

    /**
     * A delete response operation.
     *
     *<p>DEL_RESONSE = 11</p>
     */
    public final static int DEL_RESPONSE            = 11;

    /**
     * A modify RDN request operation.
     *
     *<p>MODIFY_RDN_REQUEST = 12</p>
     */
    public final static int MODIFY_RDN_REQUEST      = 12;

    /**
     * A modify RDN response operation.
     *
     *<p>MODIFY_RDN_RESPONSE = 13</p>
     */
    public final static int MODIFY_RDN_RESPONSE     = 13;

    /**
     * A compare result operation.
     *
     *<p>COMPARE_REQUEST = 14</p>
     */
    public final static int COMPARE_REQUEST         = 14;

    /**
     * A compare response operation.
     *
     *<p>COMPARE_RESPONSE = 15</p>
     */
    public final static int COMPARE_RESPONSE        = 15;

    /**
     * An abandon request operation.
     *
     *<p>ABANDON_REQUEST = 16</p>
     */
    public final static int ABANDON_REQUEST         = 16;

  
    /**
     * A search result reference operation.
     *
     *<p>SEARCH_RESULT_REFERENCE = 19</p>
     */
    public final static int SEARCH_RESULT_REFERENCE = 19;

    /**
     * An extended request operation.
     *
     *<p>EXTENDED_REQUEST = 23</p>
     */
    public final static int EXTENDED_REQUEST        = 23;

    /**
     * An extended response operation.
     *
     *<p>EXTENDED_RESONSE = 24</p>
     */
    public final static int EXTENDED_RESPONSE       = 24;
    
    /**
     * A request or response message for an asynchronous LDAP operation.
     */
	protected RfcLDAPMessage message;

    /**
     * Lock object to protect counter for message numbers
     */
    /* 
    private static Object msgLock = new Object();
    */
    
    /**
     * Counters used to construct request message #'s, unique for each request
     * Will be enabled after ASN.1 conversion
     */
    /* 
    private static int msgNum = 0; // LDAP Request counter
    */
    private int imsgNum = -1;     // This instance LDAPMessage number
    
    private int messageType = -1;
    private boolean ifRequest = false;
    
	/**
	 * Creates an LDAPMessage when sending a protocol operation.
     *
     * @param op The operation type of message.
     *
     * @deprecated For internal use only
     *
     * @see #getType
	 */
	public LDAPMessage(RfcRequest op)
	{
		this(-1, op, null);
	}

    /**
     * Dummy constuctor
     *
     * @deprecated For internal use only
     */
    /* protected */
    LDAPMessage()
    {
        return;
    }

	/**
	 * Creates an LDAPMessage when sending a protocol operation and sends
	 * some optional controls with the message.
     *
     * @param op The operation type of message.
     *<br><br>
     * @param controls The controls to use with the operation.
     *
     * @deprecated For internal use only
     *
     * @see #getType
	 */
	public LDAPMessage( int type,
                        RfcRequest op,
                        LDAPControl[] controls)
	{
        
        // Get a unique number for this request message
        /* Turn on after ASN.1 conversion
        synchronized( msgLock) {
             msgNum += 1;
             imsgNum = msgNum;
        }    
        */
    
        messageType = type;
        RfcControls asn1Ctrls = null;
		if(controls != null) {
			// Move LDAPControls into an RFC 2251 Controls object.
			asn1Ctrls = new RfcControls();
			for(int i=0; i<controls.length; i++) {
				asn1Ctrls.add(controls[i].getASN1Object());
			}
		}

		// create RFC 2251 LDAPMessage
		message = new RfcLDAPMessage(op, asn1Ctrls);
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "Creating " + toString());
        }
        return;
	}

	/**
	 * Creates an Rfc 2251 LDAPMessage when the libraries receive a response
	 * from a command.
     *
     * @param message A response message.
     *
     * @deprecated For internal use only
	 */
	public LDAPMessage(RfcLDAPMessage message)
	{
		this.message = message;
        return;
	}

    /**
     * Returns any controls in the message.
     */
    public LDAPControl[] getControls()
    {

        LDAPControl[] controls = null;
        RfcControls asn1Ctrls = message.getControls();

        // convert from RFC 2251 Controls to LDAPControl[].
	    if(asn1Ctrls != null) {
		    controls = new LDAPControl[asn1Ctrls.size()];
		    for(int i=0; i<asn1Ctrls.size(); i++) {

                /*
		         * At this point we have an RfcControl which needs to be
		         * converted to the appropriate Response Control.  This requires
		         * calling the constructor of a class that extends LDAPControl.
                 * The controlFactory method searches the list of registered
		         * controls and if a match is found calls the constructor
		         * for that child LDAPControl. Otherwise, it returns a regular
		         * LDAPControl object.
                 *
		         * Question: Why did we not call the controlFactory method when
		         * we were parsing the control. Answer: By the time the
		         * code realizes that we have a control it is already too late.
                 */
		        RfcControl rfcCtl = (RfcControl)asn1Ctrls.get(i);
                String oid = rfcCtl.getControlType().stringValue();
                byte[] value = rfcCtl.getControlValue().byteValue();
                boolean critical = rfcCtl.getCriticality().booleanValue();

		        /* Return from this call should return either an LDAPControl
		         * or a class extending LDAPControl that implements the
		         * appropriate registered response control
                 */
		        controls[i] = controlFactory(oid, critical, value);
		    }
	    }
	    return controls;
    }
    
    /**
     * Instantiates an LDAPControl.  We search through our list of
     * registered controls.  If we find a matchiing OID we instantiate
     * that control by calling its contructor.  Otherwise we default to
     * returning a regular LDAPControl object
     *
     * @deprecated  Not to be used by application programs.
     *
     * @param data A RfcControl object that encodes the returned control.
     */
    private final LDAPControl controlFactory(String oid,boolean critical,byte[] value)
    {
        RespControlVector regControls = LDAPControl.getRegisteredControls();
        try {
            /*
             * search through the registered extension list to find the
             * response control class
             */
            Class respCtlClass = regControls.findResponseControl(oid);

            // Did not find a match so return default LDAPControl
            if ( respCtlClass == null)
                return new LDAPControl(oid, critical, value);

            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                 "For oid " + oid + ", found class " + respCtlClass.toString());

            }

            /* If found, get LDAPControl constructor */
            Class[] argsClass = { String.class, boolean.class, byte[].class };
            Object[] args = new Object[] {oid, new Boolean(critical), value};
            Exception ex = null;
            try {
                Constructor ctlConstructor =
                                         respCtlClass.getConstructor(argsClass);

                try {
                    /* Call the control constructor for a registered Class*/
                    Object ctl = null;
                    ctl = ctlConstructor.newInstance(args);
                    return (LDAPControl)ctl;
                } catch (InstantiationException e) {
                    // Could not create the ResponseControl object
                    // All possible exceptions are ignored. We fall through
                    // and create a default LDAPControl object
                    ex = e;
                } catch (IllegalAccessException e) {
                    ex = e;
                } catch (InvocationTargetException e) {
                    ex = e;
                }
            } catch (NoSuchMethodException e) {
                // bad class was specified, fall through and return a
                // default LDAPControl object
                ex = e;
            }
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                      "Unable to create new instance of child LDAPControl");
                Debug.trace( Debug.controls,
                   ex.toString());
            }
        } catch (NoSuchFieldException e) {
            // No match with the OID
            // Do nothing. Fall through and construct a default LDAPControl object.
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                      "Oid " + oid + " not registered");
            }
        }
        // If we get here we did not have a registered response control
        // for this oid.  Return a default LDAPControl object.
        return new LDAPControl( oid, critical, value);
    }

    /**
     * Returns the message ID.  The message ID is an integer value
     * identifying the LDAP request and its response.
     */
    public int getMessageID()
    {
        if( imsgNum == -1) {
            imsgNum = message.getMessageID();
        }
        return imsgNum;
    }

    /**
     * Returns the LDAP operation type of the message.
     *
     * <p>The type is one of the following:</p>
     * <ul>
     *   <li>BIND_REQUEST            = 0;</li>
     *   <li>BIND_RESPONSE           = 1;</li>
     *   <li>UNBIND_REQUEST          = 2;</li>
     *   <li>SEARCH_REQUEST          = 3;</li>
     *   <li>SEARCH_RESPONSE         = 4;</li>
     *   <li>SEARCH_RESULT           = 5;</li>
     *   <li>MODIFY_REQUEST          = 6;</li>
     *   <li>MODIFY_RESPONSE         = 7;</li>
     *   <li>ADD_REQUEST             = 8;</li>
     *   <li>ADD_RESPONSE            = 9;</li>
     *   <li>DEL_REQUEST             = 10;</li>
     *   <li>DEL_RESPONSE            = 11;</li>
     *   <li>MODIFY_RDN_REQUEST      = 12;</li>
     *   <li>MODIFY_RDN_RESPONSE     = 13;</li>
     *   <li>COMPARE_REQUEST         = 14;</li>
     *   <li>COMPARE_RESPONSE        = 15;</li>
     *   <li>ABANDON_REQUEST         = 16;</li>
     *   <li>SEARCH_RESULT_REFERENCE = 19;</li>
     *   <li>EXTENDED_REQUEST        = 23;</li>
     *   <li>EXTENDED_RESPONSE       = 24;</li>
     * </ul>
     *
     * @return The operation type of the message.
     */
    public int getType()
	{
        if( messageType == -1) {
		    messageType = message.getProtocolOp().getIdentifier().getTag();
        }
        return messageType;
    }

    /**
     * Indicates whether the message is a request or a response
     *
     * @return true if the message is a request, false if it is a response,
     * a search result, or a search result reference.
     */
    public boolean isRequest()
	{
        if( messageType == -1) {
		    messageType = message.getProtocolOp().getIdentifier().getTag();
            getName();
        }
        return ifRequest;
    }
    /**
	 * Returns the RFC 2251 LDAPMessage composed in this object.
     *
     * @deprecated For internal use only
	 */
	final public RfcLDAPMessage getASN1Object()
	{
		return message;
	}

    /**
     * Creates a String representation of this object
     *
     * @return a String representation for this LDAPMessage
     */
    public String toString()
    {
        return getName() + "(" + getMessageID() + "): " + message.toString();
    }
    
    private final
    String getName()
    {
        switch(getType()) {
            case SEARCH_RESULT:
                return "LDAPSearchResult";
            case SEARCH_REQUEST:
                ifRequest = true;
                return "LDAPSearchRequest";
            case SEARCH_RESPONSE:
                return "LDAPSearchResponse";
            case MODIFY_REQUEST:
                ifRequest = true;
                return "LDAPModifyRequest";
            case MODIFY_RESPONSE:
                return "LDAPModifyResponse";
            case ADD_REQUEST:
                ifRequest = true;
                return "LDAPAddRequest";
            case ADD_RESPONSE:
                return "LDAPAddResponse";
            case DEL_REQUEST:
                ifRequest = true;
                return "LDAPDelRequest";
            case DEL_RESPONSE:
                return "LDAPDelResponse";
            case MODIFY_RDN_REQUEST:
                ifRequest = true;
                return "LDAPModifyRDNRequest";
            case MODIFY_RDN_RESPONSE:
                return "LDAPModifyRDNResponse";
            case COMPARE_REQUEST:
                ifRequest = true;
                return "LDAPCompareRequest";
            case COMPARE_RESPONSE:
                return "LDAPCompareResponse";
            case BIND_REQUEST:
                ifRequest = true;
                return "LDAPBindRequest";
            case BIND_RESPONSE:
                return "LDAPBindResponse";
            case UNBIND_REQUEST:
                ifRequest = true;
                return "LDAPUnbindRequest";
            case ABANDON_REQUEST:
                ifRequest = true;
                return "LDAPAbandonRequest";
            case SEARCH_RESULT_REFERENCE:
                return "LDAPSearchResultReference";
            case EXTENDED_REQUEST:
                ifRequest = true;
                return "LDAPExtendedRequest";
            case EXTENDED_RESPONSE:
                return "LDAPExtendedResponse";
        }
        return "Message type: " + getType();
    }
}
