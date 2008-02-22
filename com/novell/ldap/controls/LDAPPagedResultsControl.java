/* **************************************************************************
 *
 * Copyright (C) 2008 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap.controls;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *  LDAPPagedResultsControl is a Server Control to specify how search results
 *  are to be returned in pages of specified size by the server. If the control
 *  was marked "critical", the whole search operation will fail if the paged results
 *  control is not supported.
 *
 *  This control is specified in RFC2696
 */
public class LDAPPagedResultsControl extends LDAPControl {

    /**
     * The requestOID of the paged results control
     */
    private static String requestOID = "1.2.840.113556.1.4.319";

    /**
     * The responseOID of the paged results control
     */
    private static String responseOID = "1.2.840.113556.1.4.319";

    /**
     * Empty cookie
     */
    private static final byte EMPTY_COOKIE[] = new byte[0];

    private int pageSize;
    private byte Cookie[];

    /*
     * This is where we register the control responses
     */
    static
    {
		/*
         * Register the Server Paged Results Control class which is returned by the
		 * server in response to a Paged Results Request
		 */
        try {
            LDAPControl.register(responseOID,
                    Class.forName("com.novell.ldap.controls.LDAPPagedResultsResponse"));
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                  "Registered Paged Results Control Response Class");
            }
        } catch (ClassNotFoundException e) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                  "Could not register Paged Results Control Response - Class not found");
            }
        }
    }

    /**
     * Constructs a paged results control with requested page size and cookie
     *
     *  @param pageSize  Requested page size.
     *
     *  @param Cookie    Paged Results cookie.
     *
     *  @param critical	True if the search operation is to fail if the
     *					server does not support this control.
     */
    public LDAPPagedResultsControl(int pagesize, byte cookie[], boolean critical)
    {
        super(requestOID, critical, null);
        pageSize = pagesize;
        Cookie = cookie;

        setEncodedValue();
    }

    private void setEncodedValue()
    {

        /* Create a new ASN1Sequence object */
        ASN1Sequence m_prRequest = new ASN1Sequence();

        m_prRequest.add(new ASN1Integer(pageSize));
        m_prRequest.add(new ASN1OctetString((Cookie == null) ? EMPTY_COOKIE : Cookie));

        /* Set the request data field in the in the parent LDAPControl to
         * the ASN.1 encoded value of this control.  This encoding will be
         * appended to the search request when the control is sent.
         */
        setValue (m_prRequest.getEncoding(new LBEREncoder()));

    }

    /**
     * Constructs a paged results control with requested page size
     *
     *  @param pageSize  Requested page size.
     *
     *  @param critical	True if the search operation is to fail if the
     *					server does not support this control.
     */
    public LDAPPagedResultsControl(int pageSize, boolean critical)
    {
        this(pageSize, null, critical);
        return;
    }

    /**
     * Returns the current page size value
     */
    public int getPageSize()
    {
        return pageSize;
    }

    /**
     * Sets the request page size value
     *
     * @param pageSize new page size value
     * 
     */
    public void setPageSize(int pagesize)
    {
        pageSize = pagesize;
        setEncodedValue();
    }

    /**
     * Returns the current cookie
     */
    public byte[] getCookie()
    {
        return Cookie;
    }

    /**
     * Sets new cookie value
     *
     * @param cookie new cookie value
     *
     */
    public void setCookie(byte cookie[]) {
        Cookie = cookie;
        setEncodedValue();
    }

}
