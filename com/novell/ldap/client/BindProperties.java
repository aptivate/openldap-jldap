/* **************************************************************************
* $Novell: /ldap/src/jldap/com/novell/ldap/client/Message.java,v 1.3 2000/11/27 22:56:35 vtag Exp $
*
* Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
* 
* THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
* TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
* TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
* AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
* IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
* PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
* THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
***************************************************************************/
 
package com.novell.ldap.client;

import java.util.*;

/**
 * Encapsulates an LDAP Bind properties
 */
public class BindProperties
{
    
    private int version = 3;
    String dn = null;
    String passwd = null;
    String method = null;
    Hashtable bindProperties = null;
    Object bindCallbackHandler = null;

    public BindProperties(   int version,
                             String dn,
                             String passwd,
                             String method,
                             Hashtable bindProperties,
                             Object bindCallbackHandler)
    {
        this.version = version;
        this.dn = dn;
        this.passwd = passwd;
        this.method = method;
        this.bindProperties = bindProperties;
        this.bindCallbackHandler = bindCallbackHandler;
    }
    
    /**
     * gets the protocol version
     */
    public int getProtocolVersion()
    {
        return version;
    }

    /**
     * Gets the authentication password
     *
     * @return the authentication password for this connection
     */
    public String getAuthenticationPassword()
    {
        return passwd;
    }

    /**
     * Gets the authentication dn
     *
     * @return the authentication dn for this connection
     */
    public String getAuthenticationDN()
    {
        return dn;
    }

    /**
     * Gets the authentication method
     *
     * @return the authentication method for this connection
     */
    public String getAuthenticationMethod()
    {
        return method;
    }

    /**
     * Gets the SASL Bind properties
     *
     * @return the sasl bind properties for this connection
     */
    public Hashtable getSaslBindProperties()
    {
        return bindProperties;
    }

    /**
     * Gets the SASL callback handler
     *
     * @return the sasl callback handler for this connection
     */
    public Object /* javax.security.auth.callback.CallbackHandler */ getSaslCallbackHandler()
    {
        return bindCallbackHandler;
    }
}
