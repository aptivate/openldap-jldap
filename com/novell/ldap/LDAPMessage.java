/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPMessage.java,v 1.23 2001/06/13 17:51:06 jhammons Exp $
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
 */
public class LDAPMessage {

  /**
   * A request or response message for an asynchronous LDAP operation.
   */
	protected RfcLDAPMessage message;

  /**
   * A bind request operation.
   */
   public final static int BIND_REQUEST            = 0;

  /**
   * A bind response operation.
   */
   public final static int BIND_RESPONSE           = 1;

  /**
   * An unbind request operation.
   */
   public final static int UNBIND_REQUEST          = 2;

  /**
   * A search request operation.
   */
   public final static int SEARCH_REQUEST          = 3;

  /**
   * A search response containing data.
   */
   public final static int SEARCH_RESPONSE         = 4;

  /**
   * A search result message - contains search status.
   */
   public final static int SEARCH_RESULT           = 5;

  /**
   * A modify request operation.
   */
   public final static int MODIFY_REQUEST          = 6;

  /**
   * A modify response operation.
   */
   public final static int MODIFY_RESPONSE         = 7;

  /**
   * An add request operation.
   */
   public final static int ADD_REQUEST             = 8;

  /**
   * An add response operation.
   */
   public final static int ADD_RESPONSE            = 9;

  /**
   * A delete request operation.
   */
   public final static int DEL_REQUEST             = 10;

  /**
   * A delete response operation.
   */
   public final static int DEL_RESPONSE            = 11;

  /**
   * A modify RDN request operation.
   */
   public final static int MODIFY_RDN_REQUEST      = 12;

  /**
   * A modify RDN response operation.
   */
   public final static int MODIFY_RDN_RESPONSE     = 13;

  /**
   * A compare result operation.
   */
   public final static int COMPARE_REQUEST         = 14;

  /**
   * A compare response operation.
   */
   public final static int COMPARE_RESPONSE        = 15;

  /**
   * An abandon request operation.
   */
   public final static int ABANDON_REQUEST         = 16;


  /**
   * A search result reference operation.
   */
   public final static int SEARCH_RESULT_REFERENCE = 19;

  /**
   * An extended request operation.
   */
   public final static int EXTENDED_REQUEST        = 23;

  /**
   * An extended response operation.
   */
   public final static int EXTENDED_RESPONSE       = 24;

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
		this(op, null);
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
	public LDAPMessage(RfcRequest op, LDAPControl[] controls)
	{
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
	}

/*
	protected LDAPMessage(int messageID, LDAPControl[] controls, int type)
	{
		this.messageID = messageID;
		if(controls != null && controls.length > 0) {
			this.controls = new Vector(controls.length);
			for(int i=0; i<controls.length; i++) {
				this.controls.addElement(controls[i]);
			}
		}
		this.type = type;
	}
*/

   /**
    * Returns any controls in the message.
    */
   public LDAPControl[] getControls() {

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
                String oid = rfcCtl.getControlType().getString();
                byte[] value = rfcCtl.getControlValue().getContent();
                boolean critical = rfcCtl.getCriticality().getContent();

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
     * @depreacted.  Not to be used by application programs.
     *
     * @param data A RfcControl object that encodes the returned control.
     */
    private LDAPControl controlFactory(String oid,boolean critical,byte[] value)
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
    * Returns the message ID.
    */
   public int getMessageID() {
      return message.getMessageID();
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
    *@return The operation type of the message.
    */
   public int getType()
	{
		return message.getProtocolOp().getIdentifier().getTag();
   }

	/**
	 * Returns the RFC 2251 LDAPMessage composed in this object.
     *
     * @deprecated For internal use only
	 */
	public RfcLDAPMessage getASN1Object()
	{
		return message;
	}

}
