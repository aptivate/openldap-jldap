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
package com.novell.ldap.rfc2251;


import com.novell.ldap.asn1.*;
import com.novell.ldap.*;

/**
 * Represents and LDAP Bind Request.
 *<pre>
 *       BindRequest ::= [APPLICATION 0] SEQUENCE {
 *               version                 INTEGER (1 .. 127),
 *               name                    LDAPDN,
 *               authentication          AuthenticationChoice }
 *</pre>
 */
public class RfcBindRequest extends ASN1Sequence implements RfcRequest {

    /**
     * ID is added for Optimization.
     *
     * <p>ID needs only be one Value for every instance,
     * thus we create it only once.<p>
     */
    private static final ASN1Identifier ID =
        new ASN1Identifier(ASN1Identifier.APPLICATION, true, LDAPMessage.BIND_REQUEST);


    //*************************************************************************
    // Constructors for BindRequest
    //*************************************************************************

    /**
     *
     */
    public RfcBindRequest(ASN1Integer version, RfcLDAPDN name,
                        RfcAuthenticationChoice auth)
    {
        super(3);
        add(version);
        add(name);
        add(auth);
        return;
    }

    public RfcBindRequest(
        int     version,
        String  dn,
        String  mechanism,
        byte[]  credentials)
    {
        this(new ASN1Integer(version), new RfcLDAPDN(dn),
         new RfcAuthenticationChoice(mechanism, credentials));
    
    }
    
    /**
     * Constructs a new Bind Request copying the original data from
     * an existing request.
     */
    /* package */
    RfcBindRequest(   ASN1Object[] origRequest,
                      String base)
            throws LDAPException
    {
        super(origRequest, origRequest.length);
        // Replace the dn if specified, otherwise keep original base
        if( base != null) {
            set( 1, new RfcLDAPDN(base));
        }
        return;
    }
    
    //*************************************************************************
    // Mutators
    //*************************************************************************

    /**
     * Sets the protocol version
     */
    public final void setVersion(ASN1Integer version)
    {
        set(0, version);
        return;
    }

    /**
     * 
     */
    public final void setName(RfcLDAPDN name)
    {
        set(1, name);
        return;
    }

    /**
     *
     */
    public final void setAuthenticationChoice(RfcAuthenticationChoice auth)
    {
        set(2, auth);
        return;    
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     *
     */
    public final ASN1Integer getVersion()
    {
        return (ASN1Integer)get(0);
    }

    /**
     *
     */
    public final RfcLDAPDN getName()
    {
        return (RfcLDAPDN)get(1);
    }

    /**
     *
     */
    public final RfcAuthenticationChoice getAuthenticationChoice()
    {
        return (RfcAuthenticationChoice)get(2);
    }

    /**
     * Override getIdentifier to return an application-wide id.
     *
     *<pre>
     * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 0. (0x60)
     *</pre>
     */
    public final ASN1Identifier getIdentifier()
    {
        return ID;
    }

    public final RfcRequest dupRequest(String base, String filter, boolean request)
            throws LDAPException
    {
        return new RfcBindRequest( toArray(), base);
    }
    public final String getRequestDN()
    {
        return ((RfcLDAPDN)get(1)).stringValue();
    }
}
