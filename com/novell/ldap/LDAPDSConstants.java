/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2001 Novell, Inc. All Rights Reserved.
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

/**
 * LDAPDSConstants.java contains bit values for [Entry Rights], [All attribute
 * Rights], attribute rights, and entry flags in Novell eDirectory 
 */
public interface LDAPDSConstants {

	///////////////////////////////////////////////////////////////////////////
	// bit values for [Entry Rights] of access control in Novell eDirecroty
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Browse right.
	 *
	 * <p>Allows a trustee to discover objects in the Novell eDirectory tree. 
	 * </p>
	 */
	public static final long LDAP_DS_ENTRY_BROWSE = 0x00000001L;

	/**
	 * Creation right .
	 *
	 * <p>Allows a trustee to create child objects (new objects that are
	 * subordinate to the object in the Novell eDirectory tree). </p>
	 */
	public static final long LDAP_DS_ENTRY_ADD = 0x00000002L;

	/**
	 * Delete right.
	 *
	 * <p>Allows a trustee to delete an object. This right does not allow a 
	 * trustee to delete a container object that has subordinate objects. </p>
	 */
	public static final long LDAP_DS_ENTRY_DELETE = 0x00000004L;

	/**
	 * Rename right.
	 *
	 * <p>Allows a trustee to rename the object.</p>
	 */
	public static final long LDAP_DS_ENTRY_RENAME = 0x00000008L;

	/**
	 * Supercisor rights.
	 *
	 * <p>Gives a trustee all rights to an object and its attributes.</p>
	 */
	public static final long LDAP_DS_ENTRY_SUPERVISOR = 0x00000010L;

	/**
	 * Inherit ACL.
	 *
	 * <p>Allows a trustee to inherit the rights granted in the ACL
	 * and exercise them on subordinate objects.</p>
	 */
	public static final long LDAP_DS_ENTRY_INHERIT_CTL = 0x00000040L;

	///////////////////////////////////////////////////////////////////////////
	// bit values for [Attribute Rights] and attribute rights of access control
	// in Novell eDirecroty
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Attribute compare.
	 *
	 * <p>Allows a trustee to compare a value with an attribute's value. This 
	 * allows the trustee to see if the attribute contains the value without 
	 * having rights to see the value.</p>
	 */
	public static final long LDAP_DS_ATTR_COMPARE    = 0x00000001L;

	/**
	 * Attribute read.
	 *
	 * <p>Allows a trustee to read an attribute value. This right confers
	 * the Compare right.</p>
	 */
	public static final long LDAP_DS_ATTR_READ       = 0x00000002L;

	/**
	 * Attribute write.
	 *
	 * <p>Allows a trustee to add, delete, or modify an attribute value. This 
	 * right also gives the trustee the Self (Add or Delete Self) right.</p>
	 */
	public static final long LDAP_DS_ATTR_WRITE      = 0x00000004L;

	/**
	 * Self rights.
	 *
	 * <p>Allows a trustee to add or delete its name as an attribute value on 
	 * those attributes that take object names as their values.</p>
	 */
	public static final long LDAP_DS_ATTR_SELF       = 0x00000008L;

	/**
	 * All attribute rights.
	 *
	 * <p>Gives a trustee all rights to the object's attributes.</p>
	 */
	public static final long LDAP_DS_ATTR_SUPERVISOR = 0x00000020L;

	/**
	 * inherit the ACL rights.
	 *
	 * <p>Allows a trustee to inherit the rights granted in the ACL and 
	 * exercise these attribute rights on subordinate objects.</p>
	 */
	public static final long LDAP_DS_ATTR_INHERIT_CTL= 0x00000040L;

	/**
	 * dynamic ACL.
	 *
	 * <p>This bit will be set if the trustee in the ACL is a dynamic group 
	 * and its dynamic members should be considered for ACL rights 
	 * calculation purposes. If this bit is reset, the trustee's static 
	 * members alone will be considered for rights calculation purposes.</p>
	 */
	public static final long LDAP_DS_DYNAMIC_ACL     = 0x40000000L;

	///////////////////////////////////////////////////////////////////////////
	// bit values of entry flag in Novell eDirectory
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Alias object.
	 *
	 * <p>Indicates that the entry is an alias object.</p>
	 */
	public static final int LDAP_DS_ALIAS_ENTRY         = 0x0001;

	/**
	 * Partition root.
	 *
	 * <p>Indicates that the entry is the root partition.</p>
	 */
	public static final int LDAP_DS_PARTITION_ROOT      = 0x0002;

	/**
	 *Container entry.
	 *
	 * <p>Indicates that the entry is a container object and not a container
	 * alias.</p>
	 */
	public static final int LDAP_DS_CONTAINER_ENTRY     = 0x0004;

	/**
	 * Container alias.
	 *
	 * <p>Indicates that the entry is a container alias.</p>
	 */
	public static final int LDAP_DS_CONTAINER_ALIAS     = 0x0008;

	/**
	 * Matches the list.
	 *
	 * <p>Indicates that the entry matches the List filter.</p>
	 */
	public static final int LDAP_DS_MATCHES_LIST_FILTER = 0x0010;
       
	/**
	 * Reference entry.
	 *
	 * <p>Indicates that the entry has been created as a reference rather than 
	 * an entry. The synchronization process is still running and has not 
	 * created an entry for the object on this replica.</p>
	 */
	public static final int LDAP_DS_REFERENCE_ENTRY     = 0x0020;

	/**
	 * 4.0x reference entry.
	 *
	 * <p>Indicates that the entry is a reference rather than the object. The
	 * reference is in the older 4.0x form and appears only when upgrading </p>
	 */
	public static final int LDAP_DS_40X_REFERENCE_ENTRY = 0x0040;

	/**
	 * New entry.
	 *
	 * <p>Indicates that the entry is being back linked.</p>
	 */
	public static final int LDAP_DS_BACKLINKED = 0x0080;

	/**
	 * Temporary reference.
	 *
	 * <p>Indicates that the entry is new and replicas are still being updated.
	 * </p>
	 */
	public static final int LDAP_DS_NEW_ENTRY = 0x0100;

	/**
	 * Temporary reference.
	 *
	 * <p>Indicates that an external reference has been temporarily created for
	 * authentication; when the object logs out, the temporary reference is 
	 * deleted.</p>
	 */
	public static final int LDAP_DS_TEMPORARY_REFERENCE = 0x0200;

	/**
	 * Audited.
	 *
	 * <p>Indicates that the entry is being audited.</p>
	 */
	public static final int LDAP_DS_AUDITED = 0x0400;

	/**
	 * Entry not present.
	 *
	 * <p>Indicates that the state of the entry is not present.</p>
	 */
	public static final int LDAP_DS_ENTRY_NOT_PRESENT = 0x0800;

	/**
	 * Verify entry creation timestamp.
	 *
	 * <p>Indicates the entry's creation timestamp needs to be verified. Novell 
	 * eDirectory sets this flag when a replica is removed or upgraded from 
	 * NetWare 4.11 to NetWare 5.</p>
	 */
	public static final int LDAP_DS_ENTRY_VERIFY_CTS = 0x1000;

	/**
	 * entry damaged.
	 *
	 * <p>Indicates that the entry's information does not conform to the 
	 * standard format and is therefore damaged.</p>
	 */
	public static final int LDAP_DS_ENTRY_DAMAGED = 0x2000;

}
