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
package com.novell.ldap.util;
import java.io.ByteArrayOutputStream;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.util.DSMLWriter;
import junit.framework.TestCase;
/**
 * This Class contains some testcases for DSMLWriter primarily based on patches
 * and other bugs found.
 */
public class DSMLWriterTest extends TestCase {
	private DSMLWriter writer = null;
	private ByteArrayOutputStream stream = null;
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		stream = new ByteArrayOutputStream();
		writer = new DSMLWriter(stream);
		writer.useIndent(true);
	}
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		writer.finish();
		stream.close();
		stream = null;
	}
	/**
	 * This test cases is based on the ITS#3102. The actual issue is JLDAP
	 * DSMLWriter replacing special characters.
	 * 
	 * The JLDAP library should handle special characters. Current patch is only
	 * for SearchResult.
	 */
	public void testPredefinedEntities() throws Exception {
		String values[] = {"somevalue", "ampersand&", "greaterthan<",
				"lessthan>", "apos'", "quot\""};
		LDAPAttribute a = new LDAPAttribute("test", values);
		LDAPAttributeSet attributeset = new LDAPAttributeSet();
		attributeset.add(a);
		LDAPSearchResult result = new LDAPSearchResult(new LDAPEntry(
				"o=novell", attributeset), null);
		writer.writeMessage(result);
		writer.finish();
		stream.flush();
		String data = stream.toString();
		System.out.println("xml = " + data);
		assertTrue("The xml should contain ampersand&amp", data
				.indexOf("ampersand&amp") != -1);
		for (int i = 2; i < values.length; i++)
			assertTrue("The xml should not contain" + values[i], data
					.indexOf(values[i]) == -1);
	}
	/**
	 * If the batchrequest contains a empty set then a batch response of empty
	 * set must be returned.
	 * 
	 * @throws Exception
	 */
	public void testEmptyBatchresponse() throws Exception {
		writer.finish();
		stream.flush();
		String data = stream.toString();
		System.out.println("xml = " + data);
		assertTrue("Should contain batchresponse", data
				.indexOf("batchResponse") != -1);
	}
}