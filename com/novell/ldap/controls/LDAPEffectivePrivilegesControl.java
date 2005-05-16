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

import java.util.ArrayList;
import java.util.Iterator;

import com.novell.ldap.LDAPControl;
import com.novell.ldap.asn1.ASN1Boolean;
import com.novell.ldap.asn1.ASN1Choice;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.asn1.ASN1Sequence;
import com.novell.ldap.asn1.LBEREncoder;

/**
 *  LDAPEffectivePrivilegesControl is a Server Control. This controls what is 
 *  contained in the attribute effectivePrivilges 2.16.840.1.113719.1.1.4.640.
 * 
 *  If includeallleagalattributes is passed and is true then regardless of what
 *  is on the object privileges, all legal attributes will be returned.
 *  
 *  If selection is passed then those attributes passed in the selection list 
 *  will be returned.
 * 
 *  If neither includeallleagalattributes or selection is passed then 
 *  the default behavior is assume IncludeAllLegalAttributes==false 
 *  and no selection list.
 *
 *  If the control was marked "critical", the whole search operation will fail
 *  if the control is not supported by server.
 *  
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/controls/EffectivePrivilegesControlSample.java.html">EffectivePrivilegesControlSample.java</p>
 */

public class LDAPEffectivePrivilegesControl extends LDAPControl {

	/**
	 * The requestOID of the control
	 */
	private static String requestOID = "2.16.840.1.113719.1.27.101.33";

	/**
     * Constructs a Effective Privileges control with includeAllLegalAttributes
     * and selectionList passed in the argument.
     *
     *  @param includeAllLegalAttributes	Boolean value which marks if all 
     * 							attributes are Legal in Effective Privileges 
     * 							control
     *  @param selectionList	An array of LDAPEffectivePrivilegesSelection
     * 							objects
     *  @param critical			True if the search operation is to fail if the
     *							server does not support this control.
     */
	public LDAPEffectivePrivilegesControl(boolean critical,
			boolean includeAllLegalAttributes, ArrayList selectionList) {
		super(requestOID, critical, null);
		
		ASN1Choice asn1_effectiveCtlChoice = null;

		/*
		 * Condition 1 => if includeallleagalattributes is passed and is true
		 * then regardless of what is on the object privileges, all legal
		 * attributes will be returned.
		 */
		if (includeAllLegalAttributes) {
			asn1_effectiveCtlChoice = new ASN1Choice(new ASN1Boolean(
					includeAllLegalAttributes));
		}
		/*
		 * Condition 2 => If selection is passed then those attributes passed in
		 * the selection list will be returned.
		 *  
		 */
		else if (!selectionList.isEmpty()) {
			ASN1Sequence asn1_selections = new ASN1Sequence();
			ASN1Choice selectionChoice = null;

			Iterator ISelectionList = selectionList.iterator();
			//Iterate through all the choices in the selections sequence
			while (ISelectionList.hasNext()) {
				LDAPEffectivePrivilegesSelection selection = 
					(LDAPEffectivePrivilegesSelection) ISelectionList.next();

				String tmp;
				if ((tmp = selection.getSelectionAttr()) != null) {
					selectionChoice = new ASN1Choice(new ASN1OctetString(tmp));
				} else if ((tmp = selection.getSelectionClass()) != null) {
					selectionChoice = new ASN1Choice(new ASN1OctetString(tmp));
				}
				asn1_selections.add(selectionChoice);
			}

			/*
			 * If selections sequence does not have a single selection choice
			 * make to Default behavior (Condition 3)
			 */
			if (asn1_selections.size() != 0) {
				asn1_effectiveCtlChoice = new ASN1Choice(asn1_selections);
			} else {
				asn1_effectiveCtlChoice = new ASN1Choice(new ASN1Boolean(false));
			}

		}
		/*
		 * Condition 3 => if neither element is passed then the default behavior
		 * is assume IncludeAllLegalAttributes==false and no selection list
		 */
		else {
			asn1_effectiveCtlChoice = new ASN1Choice(new ASN1Boolean(false));
		}

		setValue(asn1_effectiveCtlChoice.getEncoding(new LBEREncoder()));

	}
}