/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/controls/LDAPVirtualListResponse.java,v 1.4 2001/03/01 00:30:07 cmorris Exp $
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

package com.novell.ldap.controls;

import java.io.*;
import com.novell.ldap.*;
import com.novell.ldap.client.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 * 3.3 public class LDAPVirtualListResponse
 *                extends LDAPControl
 *   
 * LDAPVirtualListResponse is a Server Control returned by the server in 
 * response to a virtual list search request. 
 */
public class LDAPVirtualListResponse extends LDAPControl {

    public static final String OID = "2.16.840.1.113730.3.4.10";

	// Private values of returned data go here
	
	private int m_firstPosition;
	private int m_ContentCount;
	private int m_resultCode;
	private String m_context = null;

    /**
     * @deprecated For internal use only.  Should not be used by applications.
     *
     * This constructor is usually called by the SDK to instantiate an
     * a LDAPControl corresponding to the Server response to a LDAP
     * VLV Control request.  Application programmers should not have
     * any reason to call the constructor.  This constructor besides
     * constructing a LDAPControl object parses the contents of the response
     * control.
     * RFC 2891 defines this response control as follows:
     *
     * The controlValue is an OCTET STRING, whose
     * value is the BER encoding of a value of the following SEQUENCE:
	 *
     * VirtualListViewResponse ::= SEQUENCE {
	 *		targetPosition    INTEGER (0 .. maxInt),
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
     */
    public LDAPVirtualListResponse(RfcControl rfcCtl)
                    throws IOException
    {
        super(rfcCtl);

        // Get the control value
        byte [] tempCtlData = this.getValue();


        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");

		// We should get back an enumerated type
        ASN1Object asnObj = decoder.decode(tempCtlData);

        if ( (asnObj == null) || (!(asnObj instanceof ASN1Sequence)) )
            throw new IOException("Decoding error");

        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.controls, "LDAPVLVResponse Control Value =" + asnObj.toString());
        }

		// 1st element is an integer containing the targetPosition (firstPosition)
		ASN1Object asn1firstPosition = ((ASN1Sequence)asnObj).get(0);
        if ( (asn1firstPosition != null) && (asn1firstPosition instanceof ASN1Integer) )
             m_firstPosition =((ASN1Integer)asn1firstPosition).getInt();
		else
			throw new IOException("Decoding error");

		// 2nd element is an integer containing the current estimate of the contentCount
		ASN1Object asn1ContentCount = ((ASN1Sequence)asnObj).get(1);
        if ( (asn1ContentCount != null) && (asn1ContentCount instanceof ASN1Integer) )
             m_ContentCount =((ASN1Integer)asn1ContentCount).getInt();
		else
			throw new IOException("Decoding error");

		// 3rd element is an enum containing the errorcode
        ASN1Object asn1Enum = ((ASN1Sequence)asnObj).get(2);
        if ( (asn1Enum != null) && (asn1Enum instanceof ASN1Enumerated) )
             m_resultCode =((ASN1Enumerated)asn1Enum).getInt();
		else
			throw new IOException("Decoding error");

        // Optional 4th element could be the context string
        if ( ((ASN1Sequence)asnObj).size() > 3) {
            ASN1Object asn1String = ((ASN1Sequence)asnObj).get(3);
            if ( (asn1String != null) && (asn1String instanceof ASN1OctetString) )
                m_context  = ((ASN1OctetString)asn1String).getString();
        }
    }


    
	/** 3.2.1 getContentCount 
	 *
	 * Returns the size of the virtual search results list 
	 */
	 public int getContentCount () 
	 {
		return m_ContentCount;
	 }

    
    
	/** 3.2.2 getFirstPosition 
	 *
	 *    Returns the index of the first entry returned 
	 */    
	 public int getFirstPosition () 
	 {
	 	return m_firstPosition;

	 }
    


	/** 3.2.3 getResultCode 
	 *
	 * Returns the result code for the virtual list request 
	 */
	 public int getResultCode () 
	 {
		return m_resultCode;

	 }



	 /** 3.2.4 getContext 
	  *
	  * Returns the cookie used by some servers to optimize the processing of 
	  * virtual list requests. 
	  */
	  public String getContext() 
	  {
		return	m_context;
	  }



}

