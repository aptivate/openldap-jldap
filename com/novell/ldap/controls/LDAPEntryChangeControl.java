/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/controls/LDAPEntryChangeControl.java,v 1.4 2001/07/25 23:42:03 vtag Exp $
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

import java.io.IOException;
import com.novell.ldap.*;
import com.novell.ldap.client.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 *  LDAPEntryChangeControl is a Server Control returned at the request
 *  of the client in response to a persistent search request. It
 *  contains additional information about a change such as what type of
 *  change occurred.
 */
public class LDAPEntryChangeControl extends LDAPControl
{
    private int     m_changeType;
    private String  m_previousDN;
    private boolean m_hasChangeNumber;
    private int     m_changeNumber;

    /**
     *  @deprecated For internal use only.  Should not be used by applications.
     *
     *  <p>This constructor is called by the SDK to create an
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
     */
    public LDAPEntryChangeControl(
        RfcControl rfcCtl)  throws IOException
    {
        super(rfcCtl);

        // Get the control value
        byte [] tempCtlData = this.getValue();

        // Create a decoder objet
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error.");

        // We should get a sequence initially
        ASN1Object asnObj = decoder.decode(tempCtlData);

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

        m_changeType =((ASN1Enumerated)asn1Obj).getInt();

        //check for optional elements
        if ( (sequence.size() > 1) && (m_changeType == 8) ) //8 means modifyDN
        {
            // get the previous DN - it is encoded as an octet string
            asn1Obj = sequence.get(1);
            if ( (asn1Obj == null) || (!(asn1Obj instanceof ASN1OctetString)) )
                throw new IOException("Decoding error get previous DN");

            m_previousDN = ((ASN1OctetString)asn1Obj).getString();
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

            m_changeNumber = ((ASN1Integer)asn1Obj).getInt();
            m_hasChangeNumber = true;
        }
        else
            m_hasChangeNumber = false;
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
        return super.toString();
    }

} //end class LDAPEntryChangeControl
