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

package com.novell.ldap;

import java.io.IOException;
import java.util.Iterator;

import com.novell.ldap.asn1.ASN1Object;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.ASN1Set;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcSearchResultEntry;
/**
 *  Encapsulates a single search result that is in response to an asynchronous
 *  search operation.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/asynchronous/Searchas.java.html">Searchas.java</p>
 *
 * @see LDAPConnection#search
 */
public class LDAPSearchResult extends LDAPMessage
{

    private LDAPEntry entry = null;
    
	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPSearchResult()
	{
		super();
	}
    
    /**
     * Constructs an LDAPSearchResult object.
     *
     * @param message The RfcLDAPMessage with a search result.
     */
    /*package*/
    LDAPSearchResult(RfcLDAPMessage message)
    {
        super(message);
        return;
    }

    /**
     * Constructs an LDAPSearchResult object from an LDAPEntry.
     *
     * @param entry the LDAPEntry represented by this search result.
     * <br><br>
     * @param cont controls associated with the search result
     */
    public LDAPSearchResult(LDAPEntry entry, LDAPControl[] cont)
    {
        super(	
        		new RfcLDAPMessage(
				new RfcSearchResultEntry(
					new ASN1OctetString(entry.getDN()),
					getEntrySequence(entry)),
				RfcControlFactory(cont)));

        this.entry = entry;
        return;
    }
	/** Converts a LDAPControl array to an RfcControl Structure.
   * @param controls array of LDAPControl
   * @return RfcControls Structure representation of controls arrray.
   */
  private static RfcControls RfcControlFactory(LDAPControl[] controls) {
		RfcControls rfcs = new RfcControls();

		if (controls != null) {

			for (int i = 0; i < controls.length; i++) {
				rfcs.add(controls[i].getASN1Object());
			}
			return rfcs;
		} else
			return null;

	}
	/**
   * Creates a ASN Encoded Sequence for the specified entry.
   * @param entry The LDAPEntry to be encoded.
   * @return ASN Encoded representation of the entry.
   */
	private static ASN1Sequence getEntrySequence(LDAPEntry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Argument \"entry\" cannot be null");
		}
		
		Iterator entryiterator = entry.getAttributeSet().iterator();
		ASN1Sequence attributelistsequence = new ASN1Sequence();
		while (entryiterator.hasNext()) {
			ASN1Sequence attributesequence = new ASN1Sequence();
			LDAPAttribute attribute = (LDAPAttribute) entryiterator.next();
			attributesequence.add(new ASN1OctetString(attribute.getName()));
			ASN1Set valueset = new ASN1Set();
			String[] valueArray = attribute.getStringValueArray();
			if (valueArray != null)
				for (int i = 0; i < valueArray.length; i++) {
					valueset.add(new ASN1OctetString(valueArray[i]));
				}
			attributesequence.add(valueset);
			attributelistsequence.add(attributesequence);
		}
		return attributelistsequence;
	}
    /**
     * Returns the entry of a server's search response.
     *
     * @return The LDAPEntry associated with this LDAPSearchResult
     */
    public LDAPEntry getEntry()
    {
        if( entry == null) {
            LDAPAttributeSet attrs = new LDAPAttributeSet();

            ASN1Sequence attrList =
                ((RfcSearchResultEntry)message.getResponse()).getAttributes();

            ASN1Object[] seqArray = attrList.toArray();
            for(int i = 0; i < seqArray.length; i++) {
                ASN1Sequence seq = (ASN1Sequence)seqArray[i];
                LDAPAttribute attr =
                    new LDAPAttribute(((ASN1OctetString)seq.get(0)).stringValue());

                ASN1Set set = (ASN1Set)seq.get(1);
                Object[] setArray = set.toArray();
                for(int j = 0; j < setArray.length; j++) {
                    attr.addValue(((ASN1OctetString)setArray[j]).byteValue());
                }
                attrs.add(attr);
            }

            entry = new LDAPEntry(
                ((RfcSearchResultEntry)message.getResponse()).getObjectName().stringValue(),
                attrs);
        }            
        return entry;
    }

    /**
     * Return a String representation of this object.
     *
     * @return a String representing this object.
     */
    public String toString()
    {
        String str;
        if( entry == null) {
            str = super.toString();
        } else {
            str = entry.toString();
        }
        return str;
    }
    
	protected void setDeserializedValues(LDAPMessage readObject, RfcControls asn1Ctrls)
		   throws IOException, ClassNotFoundException {
//			Check if it is the correct message type	
		  if(!(readObject instanceof LDAPSearchResult))
			throw new ClassNotFoundException("Error occured while deserializing " +
				"LDAPSearchResult object");

			LDAPSearchResult tmp = (LDAPSearchResult)readObject;
			  LDAPEntry entry = tmp.getEntry();
			  tmp = null; //remove reference after getting properties

			  message = new RfcLDAPMessage(
							  new RfcSearchResultEntry(
								  new ASN1OctetString(entry.getDN()),
								  LDAPSearchResult.getEntrySequence(entry)),
								  asn1Ctrls);
//			Garbage collect the readObject from readDSML()..	
			readObject = null;
	   }       
        
}
