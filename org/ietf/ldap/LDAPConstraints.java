/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPConstraints.java,v 1.19 2001/04/18 15:59:26 vtag Exp $
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

package org.ietf.ldap;

/**
 *  Represents a set of options to control an LDAP operation.
 *
 * @see com.novell.ldap.LDAPConstraints
 */
public class LDAPConstraints
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
     * Constructs an LDAPConstraints object, using the default
     * option values.
     *
     * @see com.novell.ldap.LDAPConstraints#LDAPConstraints()
     */
    public LDAPConstraints()
    {
        cons = new com.novell.ldap.LDAPSearchConstraints();
        return;
    }

    /**
     * Constructs a new LDAPConstraints object, using the specified
     * operational constraints for waiting, referrals, LDAPBind
     * object, and hop limit.
     *
     * @see com.novell.ldap.LDAPConstraints#LDAPConstraints(
                int,boolean,LDAPReferralHandler,int)
     */
    public LDAPConstraints(int msLimit,
                      boolean doReferrals,
                      LDAPReferralHandler binder,
                      int hop_limit)
    {
        // Set defaults for search parameters
        this.cons = new com.novell.ldap.LDAPSearchConstraints();
        // Set values for base constraints
        this.cons.setTimeLimit( msLimit);
        this.cons.setReferralFollowing( doReferrals);
        this.cons.setHopLimit( hop_limit);
        setReferralHandler( binder);
        return;
    }

    /**
     * Wrapper object for LDAPBind LDAPReferralHandler
     */
    private class BindImpl implements com.novell.ldap.LDAPBind
    {
        LDAPBind ref;

        private BindImpl( LDAPBind ref)
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
     * Wrapper object for LDAPReBind LDAPReferralHandler
     */
    private class RebindImpl implements com.novell.ldap.LDAPRebind
    {
        LDAPRebind ref;

        private RebindImpl( LDAPRebind ref)
        {
            this.ref = ref;
            return;
        }

        public com.novell.ldap.LDAPRebindAuth getRebindAuthentication(
                                                        String host,
                                                        int port)
        {
            LDAPRebindAuth auth;
            auth =  ref.getRebindAuthentication(host,port);
            if( auth == null) {
                return (com.novell.ldap.LDAPRebindAuth)null;
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
     * @see com.novell.ldap.LDAPConstraints#getHopLimit()
     */
    public int getHopLimit()
    {
        return cons.getHopLimit();
    }

    /**
     * Returns true if referrals are to be followed automatically.
     *
     * @see com.novell.ldap.LDAPConstraints#getHopLimit()
     */
    public boolean getReferralFollowing()
    {
        return cons.getReferralFollowing();
    }

    /**
     * Returns the maximum number of milliseconds to wait for any operation
     * under these constraints.
     *
     * @see com.novell.ldap.LDAPConstraints#getTimeLimit()
     */
    public int getTimeLimit()
    {
        return cons.getTimeLimit();
    }

    /**
     * Sets the maximum number of hops to follow in sequence during
     * automatic referral following.
     *
     * @see com.novell.ldap.LDAPConstraints#setHopLimit(int)
     */
    public void setHopLimit(int hop_limit)
    {
        cons.setHopLimit(hop_limit);
        return;
    }

    /**
     * Specifies the object that will process authentication requests.
     *
     * @see com.novell.ldap.LDAPConstraints#setReferralHandler(
                    LDAPReferralHandler)
     */
    public void setReferralHandler(LDAPReferralHandler binder)
    {
        if( binder == null) {
            cons.setReferralHandler((com.novell.ldap.LDAPReferralHandler)null);
            refHandler = null;
        } else
        if( binder instanceof LDAPBind) {
            refHandler = new BindImpl( (LDAPBind)binder);
            cons.setReferralHandler( refHandler);
        } else
        if( binder instanceof LDAPRebind) {
            refHandler = new RebindImpl( (LDAPRebind)binder);
            cons.setReferralHandler( refHandler);
        } else {
            throw new IllegalArgumentException(
                "LDAPReferralHandler must be either LDAPBind or LDAPRebind");
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
     * @see com.novell.ldap.LDAPConstraints#setReferralFollowing(boolean)
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
     * @see com.novell.ldap.LDAPConstraints#setTimeLimit(int)
     */
    public void setTimeLimit(int msLimit)
    {
        cons.setTimeLimit(msLimit);
        return;
    }

    /**
     * Returns the client controls to be used by the interface.
     *
     * @see com.novell.ldap.LDAPConstraints#getClientControls()
     */
    public LDAPControl[] getClientControls()
    {
        com.novell.ldap.LDAPControl[] controls = cons.getClientControls();
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
     * Returns the server controls to be sent to the server.
     *
     * @see com.novell.ldap.LDAPConstraints#getServerControls()
     */
    public LDAPControl[] getServerControls()
    {
        com.novell.ldap.LDAPControl[] controls = cons.getServerControls();
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
     * Sets a client control for use by the interface.
     *
     * @see com.novell.ldap.LDAPConstraints#setClientControls(LDAPControl)
     */
    public void setClientControls(LDAPControl control)
    {
        if( control == null) {
            cons.setClientControls( (com.novell.ldap.LDAPControl[])null);
        }
        cons.setClientControls( control.getWrappedObject());
        return;
    }

    /**
     * Sets an array of client controls for use by the interface.
     *
     * @see com.novell.ldap.LDAPConstraints#setClientControls(LDAPControl[])
     */
    public void setClientControls(LDAPControl[] controls)
    {
        if( controls == null) {
            cons.setClientControls( (com.novell.ldap.LDAPControl[])null);
            return;
        }
        com.novell.ldap.LDAPControl[] novellControls = 
                    new com.novell.ldap.LDAPControl[controls.length];
        for( int i=0; i < controls.length; i++) {
            novellControls[i] = controls[i].getWrappedObject();
        }
        cons.setClientControls( novellControls);
        return;
    }

    /**
     * Sets a server control to be sent to the server.
     *
     * @see com.novell.ldap.LDAPConstraints#setServerControls(LDAPControl)
     */
    public void setServerControls(LDAPControl control)
    {
        if( control == null) {
            cons.setClientControls( (com.novell.ldap.LDAPControl[])null);
        }
        cons.setServerControls( control.getWrappedObject());
        return;
    }

    /**
     * Sets an array of server controls to be sent to the server.
     *
     * @see com.novell.ldap.LDAPConstraints#setServerControls(LDAPControl[])
     */
    public void setServerControls(LDAPControl[] controls)
    {
        if( controls == null) {
            cons.setServerControls( (com.novell.ldap.LDAPControl[])null);
            return;
        }
        com.novell.ldap.LDAPControl[] novellControls = 
                    new com.novell.ldap.LDAPControl[controls.length];
        for( int i=0; i < controls.length; i++) {
            novellControls[i] = controls[i].getWrappedObject();
        }
        cons.setServerControls( novellControls);
        return;
    }
}
