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
 *  Represents a set of options to control an LDAP operation.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html">
            com.novell.ldap.Constraints</a>
 */
public class LDAPConstraints implements Cloneable
{
    private com.novell.ldap.LDAPConstraints cons;
    private com.novell.ldap.LDAPReferralHandler refHandler = null;

    /**
     * Constructs LDAPConstraints from a com.novell.ldap.LDAPConstraints
     */
    /* package */
    LDAPConstraints( com.novell.ldap.LDAPConstraints cons)
    {
        // Sets Constraints parameters to default
        this.cons = cons;
        return;
    }


    /**
     * Constructs a com.novell.ldap.LDAPConstraints object from an
     * LDAPConstraints object
     */
    /* package */
    LDAPConstraints( LDAPConstraints cons)
    {
        // Set base constraints with defaults for search parameters
        this( cons.getTimeLimit(),
              cons.getReferralFollowing(),
              null,
              cons.getHopLimit());
        refHandler = cons.getReferralHandler();

        if( cons instanceof LDAPSearchConstraints) {
            // Set search constraints values, if present
            LDAPSearchConstraints inCons = (LDAPSearchConstraints)cons;
            com.novell.ldap.LDAPSearchConstraints outCons =
                            (com.novell.ldap.LDAPSearchConstraints)this.cons;
            outCons.setServerTimeLimit(inCons.getServerTimeLimit());
            outCons.setDereference(inCons.getDereference());
            outCons.setMaxResults(inCons.getMaxResults());
            outCons.setBatchSize(inCons.getBatchSize());
        }
        return;
    }

    /**
     * Creates a clone of this object
     *
     * @return a clone of this object
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#clone()">com.novell.ldap.LDAPConstraints.clone()</a>
     */
    public Object clone()
    {
        try {
            Object newObj = super.clone();
            ((LDAPConstraints)newObj).cons = (com.novell.ldap.LDAPConstraints)this.cons.clone();
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }
    /**
     * Constructs an LDAPConstraints object, using the default
     * option values.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#LDAPConstraints()">
            com.novell.ldap.LDAPConstraints.LDAPConstraints()</a>
     */
    public LDAPConstraints()
    {
        cons = new com.novell.ldap.LDAPSearchConstraints();
        return;
    }

    /**
     * Constructs a new LDAPConstraints object, using the specified
     * operational constraints for waiting, referrals, LDAPBindHandler
     * object, and hop limit.
     *
     * @see com.novell.ldap.LDAPConstraints#LDAPConstraints(
                int,boolean,LDAPReferralHandler,int)
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#LDAPConstraints(int, boolean,
            com.novell.ldap.LDAPReferralHandler, int)">
            com.novell.ldap.LDAPConstraints.LDAPConstraints(int, boolean,
            LDAPReferralHandler, int)</a>
     */
    public LDAPConstraints(int msLimit,
                           boolean doReferrals,
                           LDAPReferralHandler handler,
                           int hop_limit)
    {
        // Set defaults for search parameters
        this.cons = new com.novell.ldap.LDAPSearchConstraints();
        // Set values for base constraints
        this.cons.setTimeLimit( msLimit);
        this.cons.setReferralFollowing( doReferrals);
        this.cons.setHopLimit( hop_limit);
        setReferralHandler( handler);
        return;
    }

    /**
     * Wrapper object for LDAPBindHandler LDAPReferralHandler
     */
    private class BindHandlerImpl implements com.novell.ldap.LDAPBindHandler
    {
        LDAPBindHandler ref;

        private BindHandlerImpl( LDAPBindHandler ref)
        {
            this.ref = ref;
            return;
        }

        public com.novell.ldap.LDAPConnection bind( String[] ldapurl,
                                    com.novell.ldap.LDAPConnection conn)
                throws com.novell.ldap.LDAPReferralException
        {
            LDAPConnection newconn;
            try {
                newconn = ref.bind( ldapurl, new LDAPConnection(conn));

                if( newconn == null) {
                    return (com.novell.ldap.LDAPConnection)null;
                }
                return newconn.getWrappedObject();
            } catch( LDAPReferralException rex) {
                throw (com.novell.ldap.LDAPReferralException)
                                                         rex.getWrappedObject();
            } catch( Throwable ex) {
                throw new com.novell.ldap.LDAPReferralException( ex.toString(),
                                                                 ex);
            }
        }
    }

    /**
     * Wrapper object for LDAPAuthHandler LDAPReferralHandler
     */
    private class AuthHandlerImpl implements com.novell.ldap.LDAPAuthHandler
    {
        LDAPAuthHandler ref;

        private AuthHandlerImpl( LDAPAuthHandler ref)
        {
            this.ref = ref;
            return;
        }

        public com.novell.ldap.LDAPAuthProvider getAuthProvider(
                                                        String host,
                                                        int port)
        {
            LDAPAuthProvider auth;
            auth = ref.getAuthProvider(host,port);
            if( auth == null) {
                return (com.novell.ldap.LDAPAuthProvider)null;
            }
            return auth.getWrappedObject();
        }
    }

    /**
     * Returns the Constraints object
     */
    /* package */
    com.novell.ldap.LDAPConstraints getWrappedObject()
    {
        return cons;
    }

    /**
     * Returns the maximum number of hops to follow during automatic
     * referral following.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#getHopLimit()">
            com.novell.ldap.LDAPConstraints.getHopLimit()</a>
     */
    public int getHopLimit()
    {
        return cons.getHopLimit();
    }

    /**
     * Returns a properties constraints objects which has been assigned with
     * set property.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#getProperty(java.lang.String)">
            com.novell.ldap.LDAPConstraints.getProperty(String)</a>
     */
    public Object getProperty(String name)
    {
        return cons.getProperty(name);
    }

    /**
     * Returns true if referrals are to be followed automatically.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#getReferralFollowing()">
            com.novell.ldap.LDAPConstraints.getReferralFollowing()</a>
     */
    public boolean getReferralFollowing()
    {
        return cons.getReferralFollowing();
    }

    /**
     * Returns the maximum number of milliseconds to wait for any operation
     * under these constraints.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#getTimeLimit()">
            com.novell.ldap.LDAPConstraints.getTimeLimit()</a>
     */
    public int getTimeLimit()
    {
        return cons.getTimeLimit();
    }

    /**
     * Sets the maximum number of hops to follow in sequence during
     * automatic referral following.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#setHopLimit(int)">
            com.novell.ldap.LDAPConstraints.setHopLimit(int)</a>
     */
    public void setHopLimit(int hop_limit)
    {
        cons.setHopLimit(hop_limit);
        return;
    }

    /**
     * Specifies the object that will process authentication requests.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#setReferralHandler(com.novell.ldap.LDAPReferralHandler)">
            com.novell.ldap.LDAPConstraints.setReferralHandler(
            LDAPReferralHandler)</a>
     */
    public void setReferralHandler(LDAPReferralHandler handler)
    {
        if( handler == null) {
            cons.setReferralHandler((com.novell.ldap.LDAPReferralHandler)null);
            refHandler = null;
        } else
        if( handler instanceof LDAPBindHandler) {
            refHandler = new BindHandlerImpl( (LDAPBindHandler)handler);
            cons.setReferralHandler( refHandler);
        } else
        if( handler instanceof LDAPAuthHandler) {
            refHandler = new AuthHandlerImpl( (LDAPAuthHandler)handler);
            cons.setReferralHandler( refHandler);
        } else {
            throw new IllegalArgumentException(
                "LDAPReferralHandler object must be either LDAPAuthHandler or LDAPBindHandler");
        }
        return;
    }

    /**
     * Gets the referral handler
     */
    /* package */
    com.novell.ldap.LDAPReferralHandler getReferralHandler()
    {
        return refHandler;
    }

    /**
     * Specifies whether referrals are followed automatically or whether
     * referrals throw an LDAPReferralException.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#setReferralFollowing(boolean)">
            com.novell.ldap.LDAPConstraints.setReferralFollowing(boolean)</a>
     */
    public void setReferralFollowing(boolean doReferrals)
    {
        cons.setReferralFollowing(doReferrals);
        return;
    }

    /**
     * Sets the maximum number of milliseconds the client waits for
     * any operation under these search constraints to complete.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#setTimeLimit(int)">
            com.novell.ldap.LDAPConstraints.setTimeLimit(int)</a>
     */
    public void setTimeLimit(int msLimit)
    {
        cons.setTimeLimit(msLimit);
        return;
    }

    /**
     * Returns the controls to be sent to the server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#getControls()">
            com.novell.ldap.LDAPConstraints.getControls()</a>
     */
    public LDAPControl[] getControls()
    {
        com.novell.ldap.LDAPControl[] controls = cons.getControls();
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
     * Sets a control to be sent to the server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#setControls(com.novell.ldap.LDAPControl)">
            com.novell.ldap.LDAPConstraints.setControls(LDAPControl)</a>
     */
    public void setControls(LDAPControl control)
    {
        cons.setControls( control);
        return;
    }

    /**
     * Sets an array of controls to be sent to the server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#setControls(com.novell.ldap.LDAPControl[])">
            com.novell.ldap.LDAPConstraints.setControls(LDAPControl[])</a>
     */
    public void setControls(LDAPControl[] controls)
    {
        cons.setControls( controls);
        return;
    }

    /**
     * Sets a property of the constraints object.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPConstraints.html#setProperty(java.lang.String, java.lang.Object)">
            com.novell.ldap.LDAPConstraints.setProperty(String, Object)</a>
     */
    public void setProperty( String name, Object value)
                throws LDAPException
    {
        try {
            cons.setProperty( name, value);
        } catch( com.novell.ldap.LDAPException rex) {
            throw new LDAPException( rex);
        }
        return;
    }
}
