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
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *  LDAPEntryChangeControl is a Server Control returned at the request
 *  of the client in response to a persistent search request. It
 *  contains additional information about a change such as what type of
 *  change occurred.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/controls/SearchPersist.java.html">SearchPersist.java</p>
 */
public class LDAPEntryChangeControl extends LDAPControl
{
    private int     m_changeType;
    private String  m_previousDN;
    private boolean m_hasChangeNumber;
    private int     m_changeNumber;

    /**
     *  This constructor is called by the SDK to create an
     *  LDAPEntryChangeControl. This constructor should NOT be called by
     *  application developers. It must be public since it resides in a
     *  different package than the classes that call it.</P>
     *  <p>The Entry Change Control is defined as follows:</p>
     *          <p>EntryChangeNotification ::= SEQUENCE {
     *               <p>changeType ENUMERATED {</p>
     *                       <p>add             (1),</p>
     *                       <p>delete          (2),</p>
     *                       <p>modify          (4),</p>
     *                       <p>modDN           (8)</p>
     *               <p>},</p>
     *               <p>previousDN   LDAPDN OPTIONAL,     -- modifyDN ops. only</p>
     *               <p>changeNumber INTEGER OPTIONAL     -- if supported</p>
     *          <p>}</p>
     *
     *  @param oid     The OID of the control, as a dotted string.
     *<br><br>
     *  @param critical   True if the LDAP operation should be discarded if
     *                    the control is not supported. False if
     *                    the operation can be processed without the control.
     *<br><br>
     *  @param value     The control-specific data.
     */
    public LDAPEntryChangeControl( String oid, boolean critical, byte[] value)
        throws IOException
    {
        super(oid, critical, value);

        // Create a decoder objet
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error.");

        // We should get a sequence initially
        ASN1Object asnObj = decoder.decode(value);

        if ( (asnObj == null) || (!(asnObj instanceof ASN1Sequence)) )
            throw new IOException("Decoding error.");

        ASN1Sequence sequence = (ASN1Sequence)asnObj;

        if( Debug.LDAP_DEBUG)
        {
            Debug.trace( Debug.controls,
                         "LDAPEntryChangeControl controlvalue =" +
                         sequence.toString());
        }

		// The first element in the sequence should be an enumerated type
        ASN1Object asn1Obj = sequence.get(0);
        if ( (asn1Obj == null) || (!(asn1Obj instanceof ASN1Enumerated)) )
            throw new IOException("Decoding error.");

        m_changeType =((ASN1Enumerated)asn1Obj).intValue();

        //check for optional elements
        if ( (sequence.size() > 1) && (m_changeType == 8) ) //8 means modifyDN
        {
            // get the previous DN - it is encoded as an octet string
            asn1Obj = sequence.get(1);
            if ( (asn1Obj == null) || (!(asn1Obj instanceof ASN1OctetString)) )
                throw new IOException("Decoding error get previous DN");

            m_previousDN = ((ASN1OctetString)asn1Obj).stringValue();
        }
        else
        {
            m_previousDN = "";
        }

        //check for change number
        if (sequence.size() == 3)
        {
            asn1Obj = sequence.get(2);
            if ( (asn1Obj == null) || (!(asn1Obj instanceof ASN1Integer)) )
                throw new IOException("Decoding error getting change number");

            m_changeNumber = ((ASN1Integer)asn1Obj).intValue();
            m_hasChangeNumber = true;
        }
        else
            m_hasChangeNumber = false;
        return;
    }

    /**
     *  returns the record number of the change in the servers change log.
     *
     *  @return  the record number of the change in the server's change log.
     *      The server may not return a change number. In this case the return
     *      value is -1
     */

    public boolean getHasChangeNumber()
    {
        return m_hasChangeNumber;
    }

    /**
     *  returns the record number of the change in the servers change log.
     *
     *  @return  the record number of the change in the server's change log.
     *      The server may not return a change number. In this case the return
     *      value is -1
     */

    public int getChangeNumber()
    {
        return m_changeNumber;
    }

    /**
     *  Returns the type of change that occured
     *
     *  @return  returns one of the following values indicating the type of
     *      change that occurred:
     *          LDAPPersistSearchControl.ADD
     *          LDAPPersistSearchControl.DELETE
     *          LDAPPersistSearchControl.MODIFY
     *          LDAPPersistSearchControl.MODDN.
     */
    public int getChangeType()
    {
        return m_changeType;
    }

    /**
     *  Returns the previous DN of the entry, if it was renamed.
     *
     *  @return  the previous DN of the entry if the entry was renamed (ie. if the
     *      change type is LDAPersistSearchControl.MODDN.
     */
    public java.lang.String getPreviousDN()
    {
        return m_previousDN;
    }

    /**
     *  Returns a string representation of the control for debugging.
     */
    public java.lang.String toString()
    {
		StringBuffer result = new StringBuffer("LDAPEntryChangeControl: ");
		result.append("((oid="+getID()+"");
		result.append(",critical="+isCritical()+")");
		result.append("(value="+getValue()+"))");
		result.append("(changeType ="+getChangeTypeString(getChangeType())+")");
		result.append("(changeNumber ="+getChangeNumber()+")");
		result.append("(PreviousDN="+getPreviousDN()+"))");
		return result.toString();
        
    }
	/**
	 * Return a string indicating the type of change represented by the
	 * changeType parameter.
	 */
	private String getChangeTypeString(
		int changeType)
	{
		String changeTypeString;

		switch (changeType) {
			case LDAPPersistSearchControl.ADD:
				changeTypeString = "ADD";
				break;
			case LDAPPersistSearchControl.MODIFY:
				changeTypeString = "MODIFY";
				break;
			case LDAPPersistSearchControl.MODDN:
				changeTypeString = "MODDN";
				break;
			case LDAPPersistSearchControl.DELETE:
				changeTypeString = "DELETE";
				break;
			default:
				changeTypeString =
				 "Unknown change type: " + String.valueOf(changeType);
				break;
		}

		return changeTypeString;
	}
} //end class LDAPEntryChangeControl
