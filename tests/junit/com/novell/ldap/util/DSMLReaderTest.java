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
import java.io.ByteArrayInputStream;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.LDAPSearchRequest;
import junit.framework.TestCase;
/**
 * This Class contains some testcases for DSMLReader primarily based on patches
 * and other bugs found.
 */
public class DSMLReaderTest extends TestCase {
	private DSMLReader reader = null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {

		super.setUp();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {

		super.tearDown();
	}
	/**
	 * This testcases checks whether the dsml can handle namespace for the dsml
	 * request.
	 * 
	 * @throws Exception
	 */
	public void testnameprefixtest() throws Exception {
		String xml = "<Envelope><Body><dsml:batchRequest xmlns=\"urn:oasis:names:tc:DSML:2:0:core\""
				+ " xmlns:dsml=\"urn:oasis:names:tc:DSML:2:0:core\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >"
				+ "<dsml:searchRequest derefAliases=\"derefAlways\""
				+ " dn=\"ou=peons,dc=idrs,dc=com\" requestID=\"1\" scope=\"singleLevel\">"
				+ "<dsml:filter>"
				+ "<dsml:present name=\"objectclass\"/>"
				+ "</dsml:filter>"
				+ "</dsml:searchRequest>"
				+ "</dsml:batchRequest></Body></Envelope>";
		reader = new DSMLReader(new ByteArrayInputStream(xml.getBytes()));
		LDAPSearchRequest request = (LDAPSearchRequest) reader.readMessage();
		assertEquals(request.getDN(), "ou=peons,dc=idrs,dc=com");
		assertEquals(request.getStringFilter(), "(objectclass=*)");
	}
	/**
	 * This tests checks that the extendedresponse is decoded correctly.
	 * 
	 * @throws Exception
	 */
	public void testreadExtendedResponse() throws Exception {
		String xml = "<batchResponse xmlns=\"urn:oasis:names:tc:DSML:2:0:core\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<extendedResponse requestID=\"1\"> "
				+ "<resultCode code=\"34\" descr=\"Invalid DN Syntax\"/>"
				+ "<errorMessage>Could not convert to NDS Name</errorMessage>"
				+ "<responseName>2.16.840.1.113719.1.27.100.38</responseName>"
				+ "<response>hello</response>"
				+ "</extendedResponse>"
				+ "</batchResponse>";
		reader = new DSMLReader(new ByteArrayInputStream(xml.getBytes()));
		LDAPExtendedResponse response = (LDAPExtendedResponse) reader
				.readMessage();
		assertEquals(response.getResultCode(), 34);
		assertEquals(response.getErrorMessage(),
				"Could not convert to NDS Name");
		assertEquals(response.getID(), "2.16.840.1.113719.1.27.100.38");
		byte arr1[] = response.getValue();
		byte arr2[] = new String("hello").getBytes("UTF-8");
		for (int i = 0; i < arr1.length; i++)
			assertEquals(arr1[i], arr2[i]);
	}
}