/*******************************************************************************
 * $OpenLDAP: pkg/jldap/com/novell/ldap/util/DSMLWriter.java,v 1.42 2004/01/23
 * 10:12:51 sunilk Exp $
 * 
 * Copyright (C) 2002 - 2003 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.
 * USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT TO VERSION
 * 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS AVAILABLE AT
 * HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE" IN THE
 * TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION OF THIS WORK
 * OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, OR
 * OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 */
package com.novell.ldap;
import junit.framework.TestCase;
/**
 * This Class contains some testcases for LDAPUrl primarily based on patches and
 * other bugs found.
 */
public class LDAPUrlTest extends TestCase {
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	/**
	 * This testcase is for the resolving the following: <b>LDAPUrl fails to
	 * parse attribute correctly (ITS#3049) </b>
	 * 
	 * @throws Exception
	 *             When error occurs.
	 */
	public void testparseAttribute() throws Exception {
		//Check normal url.
		checkurl("ldap://hostname:389");
		checkurl("ldap://hostname:389/dc=examples,dc=com?uid");
		//Use the second data.
		checkurl("ldap://hostname:389/dc=examples");
	}
	private void checkurl(String urlstring) throws Exception {
		LDAPUrl url = new LDAPUrl(urlstring);
		assertTrue("LDAPURL fails for " + urlstring + " ldapurl="
				+ url.toString(), urlstring.equals(url.toString()));
	}
}