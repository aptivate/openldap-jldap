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

package com.novell.ldap;

import java.io.IOException;
import java.net.MalformedURLException;

import com.novell.ldap.asn1.ASN1Enumerated;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.client.RespExtensionSet;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcExtendedResponse;
import com.novell.ldap.rfc2251.RfcLDAPDN;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcLDAPOID;
import com.novell.ldap.rfc2251.RfcLDAPString;
import com.novell.ldap.rfc2251.RfcReferral;


/**
 *
 *  Encapsulates the response returned by an LDAP server on an
 *  asynchronous extended operation request.  It extends LDAPResponse.
 *
 *  The response can contain the OID of the extension, an octet string
 *  with the operation's data, both, or neither.
 */
public class LDAPExtendedResponse extends LDAPResponse {

    private static RespExtensionSet registeredResponses =
                                                    new RespExtensionSet();

	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPExtendedResponse()
	{
		super(LDAPMessage.EXTENDED_RESPONSE);
	}
  
  /**
   * Creates the Extended Response Object passing the individual parameters.
   *  
   * @param resultCode  The result code as defined in LDAPException.
   *
   * @param matchedDN   The name of the lowest entry that was matched
   *                    for some error result codes, an empty string
   *                    or <code>null</code> if none.
   *
   * @param serverMessage  A diagnostic message returned by the server,
   *                       an empty string or <code>null</code> if none.
   *
   * @param referrals   The referral URLs returned for a REFERRAL result
   *                    code or <code>null</code> if none.
   *
   * @param controls    Any controls returned by the server or
   *                    <code>null</code> if none.
   * @param extendedid The LDAPOID for this extended operation.
   * @param extendedvalue The Value (Data) for this extended operation.  
   * @throws MalformedURLException When the referral URL are malformed.
   * 
   */
  public LDAPExtendedResponse(
    int resultCode,
    String matchedDN,
    String serverMessage,
    String[] referrals,
    LDAPControl[] controls,
    String extendedid,
    byte[] extendedvalue)
    throws MalformedURLException {
	this(
      new RfcLDAPMessage(
        new RfcExtendedResponse(
          new ASN1Enumerated(resultCode),
          (matchedDN != null) ? new RfcLDAPDN(matchedDN) : new RfcLDAPDN(""),
          (serverMessage != null)
            ? new RfcLDAPString(serverMessage)
            : new RfcLDAPString(""),
          (referrals != null) ? new RfcReferral(referrals) : null,
          (extendedid != null) ? new RfcLDAPOID(extendedid) : null,
          (extendedvalue != null)
            ? new ASN1OctetString(extendedvalue)
            : null)));
  }

    /**
     * Creates an LDAPExtendedResponse object which encapsulates
     * a server response to an asynchronous extended operation request.
     *
     * @param message  The RfcLDAPMessage to convert to an
     *                 LDAPExtendedResponse object.
     */
    public LDAPExtendedResponse(RfcLDAPMessage message)
    {
        super(message);
    }

    /**
     * Returns the message identifier of the response.
     *
     * @return OID of the response.
     */
    public String getID()
    {
        RfcLDAPOID respOID =
            ((RfcExtendedResponse)message.getResponse()).getResponseName();
        if (respOID == null)
            return null;
        return respOID.stringValue();
    }

    /**
     * Returns the value part of the response in raw bytes.
     *
     * @return The value of the response.
     */
    public byte[] getValue()
    {
		ASN1OctetString tempString =
                ((RfcExtendedResponse)message.getResponse()).getResponse();
		if (tempString == null)
			return null;
		else
			return(tempString.byteValue());
    }
    
    /**
     * Registers a class to be instantiated on receipt of a extendedresponse
     * with the given OID.
     *
     * <p>Any previous registration for the OID is overridden. The 
     *  extendedResponseClass object MUST be an extension of 
     *  LDAPExtendedResponse. </p>
     *
     * @param oid            The object identifier of the control.
     * <br><br>
     * @param extendedResponseClass  A class which can instantiate an 
     *                                LDAPExtendedResponse.
     */
    public static void register(String oid, Class extendedResponseClass) 
    {
        registeredResponses.registerResponseExtension(oid, extendedResponseClass);
        return;
    }
    
    /* package */
    public static RespExtensionSet getRegisteredResponses()
    {
        return registeredResponses;
    }
	protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
		   throws IOException, ClassNotFoundException {
//		  Check if it is the correct message type
		  if(!(readObject instanceof LDAPExtendedResponse))
			throw new ClassNotFoundException("Error occured while deserializing " +
				"LDAPExtendedResponse object");
			
			LDAPExtendedResponse tmp = (LDAPExtendedResponse)readObject;
	
			//Parent class properties
			int type = tmp.getType();
			int resultCode = tmp.getResultCode();
			String matchedDN = tmp.getMatchedDN();
			String serverMessage = tmp.getErrorMessage();
			String[] referrals = tmp.getReferrals();
			
			//This class specific properties
			String extendedid = tmp.getID();
			byte[] extendedvalue = tmp.getValue();
			tmp = null; //remove reference after getting properties

			message = new RfcLDAPMessage(
			new RfcExtendedResponse(
			  new ASN1Enumerated(resultCode),
			  (matchedDN != null) ? new RfcLDAPDN(matchedDN) : new RfcLDAPDN(""),
			  (serverMessage != null)
				? new RfcLDAPString(serverMessage)
				: new RfcLDAPString(""),
			  (referrals != null) ? new RfcReferral(referrals) : null,
			  (extendedid != null) ? new RfcLDAPOID(extendedid) : null,
			  (extendedvalue != null)
				? new ASN1OctetString(extendedvalue)
				: null)); 
//			Garbage collect the readObject from readDSML()..	
			readObject = null;
	   }           
}
