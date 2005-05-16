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

/**
 * Encapsulates parameters for selction of attribute names or object class names
 * for Effective Privileges Control
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/controls/EffectivePrivilegesControlSample.java.html">EffectivePrivilegesControlSample.java</p>
 */
public class LDAPEffectivePrivilegesSelection {

	private String selectionAttr = null;
	private String selectionClass = null;
		
	 /**
     * Default Constructor. Call setXXX() method after calling this constructor.
     * Call either setSelectionAttr(..) or setSelectionClass(..) to set the
     * Property but not both.
     */
	public LDAPEffectivePrivilegesSelection() {
		super();
	}

	 /**
     * Constructs a new LDAPEffectivePrivilegesSelection object using either
     * selectionAttr or selectionClass. selectionAttr and selectionClass
     * properties are both mutually exclusive (i.e., ASN1Choice type of BER data). 
     *
     * @param selectionAttr Name of the attribute in Effective Privileges 
     * Selections sequence
     * 
     * @param selectionClass Name of the object class in Effective Privileges 
     * Selections sequence
     * 
     */
	public LDAPEffectivePrivilegesSelection(String selectionAttr,
			String selectionClass) {
		super();
		this.selectionAttr = selectionAttr;
		this.selectionClass = selectionClass;
	}
	
	/**
     * Returns the selectionAttr.
     *
     * @return selectionAttr as String.
     */
	public String getSelectionAttr(){
		return selectionAttr;
	}
	
	/**
	 * This method set the selectionAttr property of this class to what is passed
	 * in argument
     *
     * @param strAttr Name of the attribute in Effective Privileges 
     * Selections sequence
     */
	public void setSelectionAttr(String strAttr){
		selectionAttr = strAttr;
	}
	
	/**
     * Returns the selectionClass.
     *
     * @return selectionClass as String.
     */
	public String getSelectionClass(){
		return selectionClass;
	}
	
	/**
	 * This method set the selectionClass property of this class to what is passed
	 * in argument
     *
     * @param strClass Name of the object class in Effective Privileges 
     * Selections sequence
     */
	public void setSelectionClass(String strClass){
		selectionClass = strClass;
	}
}
