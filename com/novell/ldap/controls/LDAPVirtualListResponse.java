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

package com.novell.ldap.controls;

import java.io.IOException;
import com.novell.ldap.*;
import com.novell.ldap.client.Debug;
import com.novell.ldap.asn1.*;

/**
 *
 * LDAPVirtualListResponse is a Server Control returned by the server in
 * response to a virtual list search request.
 
 * <br><br>
 * <p>In response to a VLV Search request the server returns an error code
 * and if the search was successful returns the following information:<br>
 * <li> an index into the search results from where the returned list begins
 * <li> an estimate of the total number of elements in the search result
 * <li> an optional context field to be returned to the server with
 * subsequent VLV request.
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/controls/VLVControl.java.html">VLVControl.java</p>
 */
public class LDAPVirtualListResponse extends LDAPControl
{
    /* The parsed fields are stored in these private variables */
    private int m_firstPosition;
    private int m_ContentCount;
    private int m_resultCode;

    /* The context field if one was returned by the server */
    private String m_context = null;

    /**
     * This constructor is usually called by the SDK to instantiate an
     * a LDAPControl corresponding to the Server response to a LDAP
     * VLV Control request.  Application programmers should not have
     * any reason to call the constructor.  This constructor besides
     * constructing a LDAPVirtualListResponse control object also
     * parses the contents of the response into local variables.
     *
     * <p>RFC 2891 defines this response control as follows:
     *
     * The controlValue is an OCTET STRING, whose value is the BER
     * encoding of a value of the following ASN.1:<br><br>
     *
     * VirtualListViewResponse ::= SEQUENCE {
     *        targetPosition    INTEGER (0 .. maxInt),
     *      contentCount     INTEGER (0 .. maxInt),
     *      virtualListViewResult ENUMERATED {
     *      success (0),
     *      operationsError (1),
     *      unwillingToPerform (53),
     *      insufficientAccessRights (50),
     *      busy (51),
     *      timeLimitExceeded (3),
     *      adminLimitExceeded (11),
     *      sortControlMissing (60),
     *      offsetRangeError (61),
     *      other (80) },
     *      contextID     OCTET STRING OPTIONAL }
     *
     *
     *  @param oid     The OID of the control, as a dotted string.
     *<br><br>
     *  @param critical   True if the LDAP operation should be discarded if
     *                    the control is not supported. False if
     *                    the operation can be processed without the control.
     *<br><br>
     *  @param values     The control-specific data.
     */
    public LDAPVirtualListResponse(String oid, boolean critical, byte[] values)
                    throws IOException
    {
        super(oid, critical, values);

        /* Create a decoder object */
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");

        /* We should get back an ASN.1 Sequence object */
        ASN1Object asnObj = decoder.decode(values);
        if ( (asnObj == null) || (!(asnObj instanceof ASN1Sequence)) )
            throw new IOException("Decoding error");

        /* Else we got back a ASN.1 sequence - print it if running debug code */
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.controls, "LDAPVLVResponse Control Value =" + asnObj.toString());
        }

        /* Get the 1st element which should be an integer containing the
         * targetPosition (firstPosition)
         */
        ASN1Object asn1firstPosition = ((ASN1Sequence)asnObj).get(0);
        if ( (asn1firstPosition != null) && (asn1firstPosition instanceof ASN1Integer) )
             m_firstPosition =((ASN1Integer)asn1firstPosition).intValue();
        else
            throw new IOException("Decoding error");

        /* Get the 2nd element which should be an integer containing the
         * current estimate of the contentCount
         */
        ASN1Object asn1ContentCount = ((ASN1Sequence)asnObj).get(1);
        if ( (asn1ContentCount != null) && (asn1ContentCount instanceof ASN1Integer) )
             m_ContentCount =((ASN1Integer)asn1ContentCount).intValue();
        else
            throw new IOException("Decoding error");

        /* The 3rd element is an enumer containing the errorcode */
        ASN1Object asn1Enum = ((ASN1Sequence)asnObj).get(2);
        if ( (asn1Enum != null) && (asn1Enum instanceof ASN1Enumerated) )
             m_resultCode =((ASN1Enumerated)asn1Enum).intValue();
        else
            throw new IOException("Decoding error");

        /* Optional 4th element could be the context string that the server
         * wants the client to send back with each subsequent VLV request
         */
        if ( ((ASN1Sequence)asnObj).size() > 3) {
            ASN1Object asn1String = ((ASN1Sequence)asnObj).get(3);
            if ( (asn1String != null) && (asn1String instanceof ASN1OctetString) )
                m_context  = ((ASN1OctetString)asn1String).stringValue();
        }
        return;
    }

    /**
     * Returns the size of the virtual search results list.  This integer as
     * the servers current estimate of what the search result size.
     */
     public int getContentCount ()
     {
        return m_ContentCount;
     }

    /**
     * Returns the index of the first entry in the returned list.  The server uses
     * the clients request information in conjunction with its current search result
     * list to estimate what list of entries the client is requesting.  This integer
     * is the index into the search results that is returned to the client.
     */
     public int getFirstPosition ()
     {
         return m_firstPosition;

     }

    /**
     * Returns the result code for the virtual list search request.
     */
     public int getResultCode ()
     {
        return m_resultCode;

     }

     /**
      * Returns the cookie used by some servers to optimize the processing of
      * virtual list requests. Subsequent VLV requests to the same server
      * should return this String to the server.
      */
      public String getContext()
      {
        return    m_context;
      }
}
