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
package com.novell.ldap.extensions;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;
import java.io.IOException;

/**
 *  This object represent the filter returned fom a GetReplicationFilterRequest.
 *
 *  <p>An object in this class is generated from an ExtendedResponse object
 *  using the ExtendedResponseFactory class.</p>
 *
 *  <p>The GetReplicationFilterResponse extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.38</p>
 *
 */
public class GetReplicationFilterResponse extends LDAPExtendedResponse {



	// Replication filter returned by the server goes here
	String returnedFilter[][];

   /**
    * Constructs an object from the responseValue which contains the replication
    * filter.
    *
    *  <p>The constructor parses the responseValue which has the following
    *  format:<br>
    *  responseValue ::=<br>
	*  &nbsp;&nbsp;&nbsp;&nbsp; SEQUENCE of SEQUENCE {</p>
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; classname&nbsp;&nbsp;&nbsp;  OCTET STRING</p>
	*  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; SEQUENCE of ATTRIBUTES</p>
	*  &nbsp;&nbsp;&nbsp;&nbsp;}</p>
	*  &nbsp;&nbsp;&nbsp;&nbsp;where</p>
	*  &nbsp;&nbsp;&nbsp;&nbsp;ATTRIBUTES:: OCTET STRING</p>
    *
    * @exception IOException The responseValue could not be decoded.
    */
   public GetReplicationFilterResponse (RfcLDAPMessage rfcMessage)
         throws IOException {

        super(rfcMessage);

        if (getResultCode() != LDAPException.SUCCESS)
        {
            returnedFilter = new String[0][0];
        }
        else
        {
            // parse the contents of the reply
            byte [] returnedValue = this.getValue();
            if (returnedValue == null)
                throw new IOException("No returned value");

            // Create a decoder object
            LBERDecoder decoder = new LBERDecoder();
            if (decoder == null)
                throw new IOException("Decoding error");

		    // We should get back a sequence
            ASN1Sequence returnedSequence = (ASN1Sequence)decoder.decode(returnedValue);

            if (returnedSequence == null)
                throw new IOException("Decoding error");

            // How many sequences in this list
            int numberOfSequences = returnedSequence.size();
		    returnedFilter = new String[numberOfSequences][];

            // Parse each returned sequence object
		    for(int classNumber = 0; classNumber < numberOfSequences; classNumber++) {

		      // Get the next ASN1Sequence
              ASN1Sequence asn1_innerSequence = (ASN1Sequence)returnedSequence.get(classNumber);
		      if (asn1_innerSequence == null)
                throw new IOException("Decoding error");

		      // Get the asn1 encoded classname
              ASN1OctetString asn1_className = (ASN1OctetString)asn1_innerSequence.get(0);
              if (asn1_className == null)
                    return;

		     // Get the attribute List
		     ASN1Sequence asn1_attributeList = (ASN1Sequence)asn1_innerSequence.get(1);
		     if (asn1_attributeList == null)
			     throw new IOException("Decoding error");

		     int numberOfAttributes = asn1_attributeList.size();
		     returnedFilter[classNumber] = new String[numberOfAttributes+1];

             // Get the classname
             returnedFilter[classNumber][0] = asn1_className.stringValue();
             if (returnedFilter[classNumber][0] == null)
                    throw new IOException("Decoding error");

		     for (int attributeNumber = 0; attributeNumber < numberOfAttributes; attributeNumber++) {

			    // Get the asn1 encoded attribute name
			    ASN1OctetString asn1_attributeName = (ASN1OctetString)asn1_attributeList.get(attributeNumber);
			    if (asn1_attributeName == null)
                    throw new IOException("Decoding error");

			    // Get attributename string
			    returnedFilter[classNumber][attributeNumber+1] = asn1_attributeName.stringValue();
			    if (returnedFilter[classNumber][attributeNumber+1] == null)
                    throw new IOException("Decoding error");

			    }

		    }
        }

   }

   /**
    * Returns the replicationFilter as an array of classname-attribute name pairs
    *
    * @return String array contining a two dimensional array of strings.  The first
	* element of each array is the class name the others are the attribute names
    */
   public String[][] getReplicationFilter() {
        return returnedFilter;
   }

}
