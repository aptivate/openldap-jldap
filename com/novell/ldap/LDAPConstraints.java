/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

import java.util.Hashtable;
import com.novell.ldap.client.Debug;

/**
 * Defines options controlling LDAP operations on the directory.
 *
 * <p>An LDAPConstraints object is always associated with an LDAPConnection
 * object; its values can be changed with LDAPConnection.setConstraints, or
 * overridden by passing an LDAPConstraints object to an operation.</p>
 *
 * @see LDAPConnection#setConstraints(LDAPConstraints)
 */
public class LDAPConstraints implements Cloneable, java.io.Serializable {

    private int msLimit = 0;
    private int hopLimit = 10;
    private boolean doReferrals = false;
    private LDAPReferralHandler refHandler = null;
    private LDAPControl[] controls = null;
    private static Object nameLock = new Object();// protect agentNum
    private static int lConsNum = 0;              // Debug, LDAPConstraints num
    private String name;                          // String name for debug
    private Hashtable properties = null;          // Properties

    /**
     * Constructs a new LDAPConstraints object that specifies the default
     * set of constraints.
     */
    public LDAPConstraints()
    {
        // Get a unique constraints name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPConstraints(" + ++lConsNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name +
                    "Created, follow referrals = " + doReferrals);
        }
        return;
    }

    /**
     * Constructs a new LDAPConstraints object specifying constraints that
     * control wait time, and referral handling.
     *
     *  @param msLimit  The maximum time in milliseconds to wait for results.
     *                  The default is 0, which means that there is no
     *                  maximum time limit. This limit is enforced for an
     *                  operation by the API, not by the server.
     *                  The operation will be abandoned and terminated by the
     *                  API with a result code of LDAPException.LDAP_TIMEOUT
     *                  if the operation exceeds the time limit.
     *<br><br>
     * @param doReferrals Determines whether to automatically follow
     *                  referrals or not. Specify true to follow
     *                  referrals automatically, and false to throw
     *                  an LDAPReferralException if the server responds
     *                  with a referral. False is the default value.
     *                  The way referrals are followed automatically is
     *                  determined by the setting of the handler parameter.
     *                  It is ignored for asynchronous operations.
     *<br><br>
     * @param handler   The custom authentication handler called when
     *                  LDAPConnection needs to authenticate, typically on
     *                  following a referral.  A null may be specified to
     *                  indicate default authentication processing, i.e.
     *                  referrals are followed with anonymous authentication.
     *                  The handler object may be an implemention of either the
     *                  LDAPBindHandler or LDAPAuthHandler interface.
     *                  The implementation of these interfaces determines how
     *                  authentication is performed when following referrals.
     *                  It is ignored for asynchronous operations.
     *<br><br>
     * @param hop_limit The maximum number of referrals to follow in a
     *                  sequence during automatic referral following.
     *                  The default value is 10. A value of 0 means no limit.
     *                  The operation will be abandoned and terminated by the
     *                  API with a result code of
     *                  LDAPException.REFERRAL_LIMIT_EXCEEDED if the
     *                  number of referrals in a sequence exceeds the limit.
     *                  It is ignored for asynchronous operations.
     *
     * @see LDAPException#LDAP_TIMEOUT
     * @see LDAPException#REFERRAL_LIMIT_EXCEEDED
     * @see LDAPException#REFERRAL
     * @see LDAPReferralException
     * @see LDAPBindHandler
     * @see LDAPAuthHandler

     */
    public LDAPConstraints(int msLimit,
                      boolean doReferrals,
                      LDAPReferralHandler handler,
                      int hop_limit)
    {
        this.msLimit = msLimit;
        this.doReferrals = doReferrals;
        this.refHandler = handler;
        this.hopLimit = hop_limit;
        // Get a unique constraints name for debug
        if( Debug.LDAP_DEBUG) {
            synchronized( nameLock) {
                name = "LDAPConstraints(" + ++lConsNum + "): ";
            }
            Debug.trace( Debug.apiRequests, name +
                    "Created, follow referrals = " + doReferrals);
        }
        return;
    }

    /**
     * Returns the controls to be sent to the server.
     *
     * @return The controls to be sent to the server, or null if none.
     *
     * @see #setControls(LDAPControl)
     * @see #setControls(LDAPControl[])
     */
    public LDAPControl[] getControls()
    {
        return controls;
    }

    /**
     * Returns the maximum number of referrals to follow during automatic
     * referral following.  The operation will be abandoned and terminated by
     * the API with a result code of LDAPException.REFERRAL_LIMIT_EXCEEDED
     * if the number of referrals in a sequence exceeds the limit.
     * It is ignored for asynchronous operations.
     *
     * @return The maximum number of referrals to follow in sequence
     *
     * @see #setHopLimit(int)
     * @see LDAPException#REFERRAL_LIMIT_EXCEEDED
     */
    public int getHopLimit()
    {
        return hopLimit;
    }

    /**
     * Gets a property of the constraints object which has been
     * assigned with {@link #setProperty(String, Object)}.
     *
     * @param name   Name of the property to be returned.
     *
     * @return the object associated with the property,
     * or <code>null</code> if the property is not set.
     *
     * @see #setProperty(String, Object)
     * @see LDAPConnection#getProperty(String)
     */
    public Object getProperty(String name)
    {
        if( properties == null) {
            return null;            // Requested property not available.
        }
        return properties.get( name);
    }

    /**
     * Gets all the properties of the constraints object which has been
     * assigned with {@link #setProperty(String, Object)}.
     * A value of <code>null</code> is returned if no properties are defined.
     *
     * @see #setProperty(String, Object)
     * @see LDAPConnection#getProperty(String)
     */
    /* package */
    Hashtable getProperties()
    {
        return properties;
    }

    /**
     * Specified whether or not referrals are followed automatically.
     *
     * @return  True if referrals are followed automatically, or
     * false if referrals throw an LDAPReferralException.</p>
     */
    public boolean getReferralFollowing()
    {
        return doReferrals;
    }

    /**
     * Returns an object that can process authentication for automatic
     * referral handling.
     *
     * <p>It may be null.</p>
     *
     * @return An LDAPReferralHandler object that can process authentication.
     */
    /*package*/
    LDAPReferralHandler getReferralHandler()
    {
        return refHandler;
    }

    /**
     * Returns the maximum number of milliseconds to wait for any operation
     * under these constraints.
     *
     * <p>If the value is 0, there is no maximum time limit on waiting
     * for operation results. The actual granularity of the timeout depends
     * platform.  This limit is enforced the the API on an
     * operation, not by the server.
     * The operation will be abandoned and terminated by the
     * API with a result code of LDAPException.LDAP_TIMEOUT if the
     * operation exceeds the time limit.</p>
     *
     * @return The maximum number of milliseconds to wait for the operation.
     *
     * @see LDAPException#LDAP_TIMEOUT
     */
    public int getTimeLimit()
    {
        return msLimit;
    }

    /**
     * Sets a single control to be sent to the server.
     *
     * @param control     A single control to be sent to the server or
     *                    null if none.
     */
    public void setControls(LDAPControl control)
    {
        if( control == null) {
            this.controls = null;
            return;
        }
        this.controls = new LDAPControl[1];
        this.controls[0] = (LDAPControl)control.clone();
        return;
    }

    /**
     * Sets controls to be sent to the server.
     *
     * @param controls      An array of controls to be sent to the server or
     *                      null if none.
     */
    public void setControls(LDAPControl[] controls)
    {
        if( (controls == null) || (controls.length == 0)) {
            this.controls = null;
            return;
        }
        this.controls = new LDAPControl[controls.length];
        for( int i=0; i<controls.length; i++) {
            this.controls[i] = (LDAPControl)controls[i].clone();
        }
        return;
    }

    /**
     * Sets the maximum number of referrals to follow in sequence during
     * automatic referral following.
     *
     * @param hop_limit The maximum number of referrals to follow in a
     *                  sequence during automatic referral following.
     *                  The default value is 10. A value of 0 means no limit.
     *                  The operation will be abandoned and terminated by the
     *                  API with a result code of
     *                  LDAPException.REFERRAL_LIMIT_EXCEEDED if the
     *                  number of referrals in a sequence exceeds the limit.
     *                  It is ignored for asynchronous operations.
     *
     * @see LDAPException#REFERRAL_LIMIT_EXCEEDED
     */
    public void setHopLimit(int hop_limit)
    {
        this.hopLimit = hop_limit;
        return;
    }

    /**
     * Sets a property of the constraints object.
     *
     * <p>No property names have been defined at this time, but the
     * mechanism is in place in order to support revisional as well as
     * dynamic and proprietary extensions to operation modifiers.</p>
     *
     * @param name    Name of the property to set.
     *<br><br>
     * @param value   Value to assign to the property.
     *                 property is not supported.
     *
     * @throws NullPointerException if name or value are null
     *
     * @see #getProperty(String )
     * @see LDAPConnection#getProperty(String)
     */
    public void setProperty(String name, Object value)
        throws LDAPException
    {
        if( properties == null) {
            properties = new Hashtable();
        }
        properties.put( name, value);
        return;
    }

    /**
     * Sets all the properties of the constraints object.
     *
     * @param props the properties represented by the Hashtable object to set.
     */
    /* package */ void setProperties(Hashtable props)
    {
        properties = (Hashtable)props.clone();
        return;
    }

    /**
     * Specifies whether referrals are followed automatically or if
     * referrals throw an LDAPReferralException.
     *
     * <p>Referrals of any type other than to an LDAP server (for example, a
     *  referral URL other than ldap://something) are ignored on automatic
     *  referral following. </p>
     *
     * <p> The default is false.</p>
     *
     *  @param doReferrals    True to follow referrals automatically.
     *                        False to throw an LDAPReferralException if
     *                        the server returns a referral.
     */
    public void setReferralFollowing(boolean doReferrals)
    {
        this.doReferrals = doReferrals;
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, name +
                    "Follow referrals = " + doReferrals);
        }
        return;
    }

    /**
     * Specifies the object that will process authentication requests
     * during automatic referral following.
     *
     * <p>The default is null.</p>
     *
     *  @param handler    An object that implements LDAPBindHandler or
     *          LDAPAuthHandler
     *
     * @see LDAPAuthHandler
     * @see LDAPBindHandler
     */
    public void setReferralHandler(LDAPReferralHandler handler)
    {
        refHandler = handler;
        return;
    }

    /**
     * Sets the maximum number of milliseconds the client waits for
     * any operation under these constraints to complete.
     *
     * <p>If the value is 0, there is no maximum time limit enforced by the
     * API on waiting for the operation results. The actual granularity of
     * the timeout depends on the platform.
     * The operation will be abandoned and terminated by the
     * API with a result code of LDAPException.LDAP_TIMEOUT if the
     * operation exceeds the time limit.</p>
     *
     *  @param msLimit      The maximum milliseconds to wait.
     *
     * @see LDAPException#LDAP_TIMEOUT
     */
    public void setTimeLimit(int msLimit)
    {
        this.msLimit = msLimit;
        return;
    }

    /**
     * Clones an LDAPConstraints object.
     *
     * @return An LDAPConstraints object.
     */
    public Object clone()
    {
        try {
            Object newObj = super.clone();
            if( controls != null) {
                ((LDAPConstraints)newObj).controls = (LDAPControl[])controls.clone();
            }
            if( properties != null) {
                ((LDAPConstraints)newObj).properties = (Hashtable)properties.clone();
            }
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
    *  Writes the object state to a stream in standard Default Binary format
    *  This function wraps ObjectOutputStream' s defaultWriteObject() to write
    *  the non-static and non-transient fields of the current class to the stream
    *   
    *  @param objectOStrm  The OutputSteam where the Object need to be written
    */
    private void writeObject(java.io.ObjectOutputStream objectOStrm)
	    throws java.io.IOException {
		objectOStrm.defaultWriteObject();
    }
    
    /**
    *  Reads the serialized object from the underlying input stream.
    *  This function wraps ObjectInputStream's  defaultReadObject() function
    *
    *  @param objectIStrm  InputStream used to recover those objects previously serialized. 
    */
    private void readObject(java.io.ObjectInputStream objectIStrm)
         throws java.io.IOException, ClassNotFoundException
    {
	  objectIStrm.defaultReadObject();
    }


}
