/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/controls/LDAPPersistSearchControl.java,v 1.3 2001/03/30 01:44:59 javed Exp $
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

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.client.Debug;


/**
 *  LDAPPersistSearchControl is a Server Control that allows a client
 *  to receive notifications from the server of changes to entries within
 *  the searches result set. The client can be notified when an entry is
 *  added to the result set, when an entry is deleted from the result set,
 *  when a DN has been changed or when and attribute value has been changed.
 */
public class LDAPPersistSearchControl extends LDAPControl
{
    /* private data members */
    private static final int SEQUENCE_SIZE = 3;

    private static final int CHANGETYPES_INDEX = 0;
    private static final int CHANGESONLY_INDEX = 1;
    private static final int RETURNCONTROLS_INDEX = 2;

    private static final LBEREncoder s_encoder = new LBEREncoder();

    private int             m_changeTypes;
    private boolean         m_changesOnly;
    private boolean         m_returnControls;
    private ASN1Sequence    m_sequence;

    /**
     *  Change type specifying that you want to track additions of new entries
     *  to the directory.
     */
    public static final int ADD = 1;

    /**
     *  Change type specifying that you want to track removals of entries from
     *  the directory.
     */
    public static final int DELETE = 2;

    /**
     *  Change type specifying that you want to track modifications of entries
     *  in the directory.
     */
    public static final int MODIFY = 4;

    /**
     *  Change type specifying that you want to track modifications of the DNs
     *  of entries in the directory.
     */
    public static final int MODDN = 8;

    /**
     *  Change type specifying that you want to track any of the above
     *  modifications.
     */
    public static final int ANY = ADD | DELETE | MODIFY | MODDN;

    /**
     * The OID of the persistent search control
     */
    public static java.lang.String OID = "2.16.840.1.113730.3.4.3";

    /* public constructors */

    /**
     *  The default constructor. A control with changes equal to ANY,
     *  isCritical equal to true, changesOnly equal to true, and
     *  returnControls equal to true
     */
    public LDAPPersistSearchControl()
    {
        this(ANY, true, true, true);
    }

    /**
     *  Constructs an LDAPPersistSearchControl object according to the supplied
     *  parameters. The resulting control is used to specify a persistent search.
     *
     *  @param changeTypes  the change types to monitor. The bitwise OR of any
     *      of the following values: LDAPPersistSearchControl.ADD
     *                               LDAPPersistSearchControl.DELETE
     *                               LDAPPersistSearchControl.MODIFY
     *                               LDAPPersistSearchControl.MODDN
     *      To track all changes the value can be set to:
     *                               LDAPPersistSearchControl.ANY
     *  @param changesOnly  true if you do not want the server to return
     *      all existing entries in the directory that match the search
     *      criteria. (Use this if you just want the changed entries to be
     *      returned.)
     *  @param returnControls  true if you want the server to return entry
     *      change controls with each entry in the search results. You need to
     *      return entry change controls to discover what type of change
     *      and other additional information about the change.
     *  @param isCritical  true if this control is critical to the search
     *      operation. If true and the server does not support this control,
     *      the server will not perform the search at all.
     */
    public LDAPPersistSearchControl(
        int changeTypes,
        boolean changesOnly,
        boolean returnControls,
        boolean isCritical)
    {
        super(OID, isCritical, null);

        m_changeTypes = changeTypes;
        m_changesOnly = changesOnly;
        m_returnControls = returnControls;

        m_sequence = new ASN1Sequence(SEQUENCE_SIZE);

        m_sequence.add(new ASN1Integer(m_changeTypes));
        m_sequence.add(new ASN1Boolean(m_changesOnly));
        m_sequence.add(new ASN1Boolean(m_returnControls));

        setValue();
    }

    /**
     *  Returns the change types to be monitored as a logical OR of any or
     *  all of these values: ADD, DELETE, MODIFY, and/or MODDN.
     *
     *  @return  the change types to be monitored. The logical or of any of
     *      the following values: ADD, DELETE, MODIFY, and/or MODDN.
     */
    public int getChangeTypes()
    {
        return m_changeTypes;
    }

    /**
     *  Sets the change types to be monitored.
     *
     *  types  The change types to be monitored as a logical OR of any or all
     *      of these types: ADD, DELETE, MODIFY, and/or MODDN. Can also be set
     *      to the value ANY which is defined as the logical OR of all of the
     *      preceding values.
     */
    public void setChangeTypes(
        int changeTypes)
    {
        m_changeTypes = changeTypes;
        m_sequence.set(CHANGETYPES_INDEX, new ASN1Integer(m_changeTypes));
        setValue();
    }

    /**
     *  Returns true if entry change controls are to be returned with the
     *  search results.
     *
     *  @return  true if entry change controls are to be returned with the
     *      search results. Otherwise, false is returned
     */
    public boolean getReturnControls()
    {
        return m_returnControls;
    }

    /**
     *  When set to true, requests that entry change controls be returned with
     *  the search results.
     *
     *  @param returnControls   true to return entry change controls.
     */
    public void setReturnControls(
        boolean returnControls)
    {
        m_returnControls = returnControls;
        m_sequence.set(RETURNCONTROLS_INDEX, new ASN1Boolean(m_returnControls));
        setValue();
    }

    /**
     *  getChangesOnly returns true if only changes are to be returned.
     *  Results from the initial search are not returned.
     *
     *  @return  true of only changes are to be returned
     */
    public boolean getChangesOnly()
    {
        return m_changesOnly;
    }

    /**
     *  When set to true, requests that only changes be returned, results from
     *  the initial search are not returned.
     *  @param  changesOnly  true to skip results for the initial search
     */
    public void setChangesOnly(
        boolean changesOnly)
    {
        m_changesOnly = changesOnly;
        m_sequence.set(CHANGESONLY_INDEX, new ASN1Boolean(m_changesOnly));
        setValue();
    }

    public String toString()
    {
        byte[] data = m_sequence.getEncoding(s_encoder);

        StringBuffer buf = new StringBuffer(data.length);

        for (int i=0; i<data.length; i++)
        {
            buf.append(Byte.toString(data[i]));
            if (i<data.length-1)
                buf.append(",");
        }

        return buf.toString();
    }

    /**
     *  Sets the encoded value of the LDAPControlClass
     */
    private void setValue()
    {
        super.setValue(m_sequence.getEncoding(s_encoder));
    }
} // end class LDAPPersistentSearchControl