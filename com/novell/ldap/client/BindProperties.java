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

package com.novell.ldap.client;

import java.util.Hashtable;

/**
 * Encapsulates an LDAP Bind properties
 */
public class BindProperties
{

    private int version = 3;
    private String dn = null;
    private String method = null;
    private boolean anonymous;
    private Hashtable bindProperties = null;
    private Object bindCallbackHandler = null;

    public BindProperties(   int version,
                             String dn,
                             String method,
                             boolean anonymous,
                             Hashtable bindProperties,
                             Object bindCallbackHandler)
    {
        this.version = version;
        this.dn = dn;
        this.method = method;
        this.anonymous = anonymous;
        this.bindProperties = bindProperties;
        this.bindCallbackHandler = bindCallbackHandler;
    }

    /**
     * gets the protocol version
     */
    public final int getProtocolVersion()
    {
        return version;
    }

    /**
     * Gets the authentication dn
     *
     * @return the authentication dn for this connection
     */
    public final String getAuthenticationDN()
    {
        return dn;
    }

    /**
     * Gets the authentication method
     *
     * @return the authentication method for this connection
     */
    public final String getAuthenticationMethod()
    {
        return method;
    }

    /**
     * Gets the SASL Bind properties
     *
     * @return the sasl bind properties for this connection
     */
    public final Hashtable getSaslBindProperties()
    {
        return bindProperties;
    }

    /**
     * Gets the SASL callback handler
     *
     * @return the sasl callback handler for this connection
     */
    public final Object /* javax.security.auth.callback.CallbackHandler */ getSaslCallbackHandler()
    {
        return bindCallbackHandler;
    }

    /**
     * Indicates whether or not the bind properties specify an anonymous bind
     *
     * @return true if the bind properties specify an anonymous bind
     */
    public final boolean isAnonymous()
    {
        return anonymous;
    }
}
