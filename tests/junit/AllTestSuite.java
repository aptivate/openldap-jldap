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
import com.novell.ldap.LDAPUrlTest;
import com.novell.ldap.util.DSMLReaderTest;
import com.novell.ldap.util.DSMLWriterTest;
import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * This class is the Junit Test Suite of all the junit tests.
 */
public class AllTestSuite {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTestSuite.suite());
	}
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for default package");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(LDAPUrlTest.class));
		suite.addTest(new TestSuite(DSMLReaderTest.class));
		suite.addTest(new TestSuite(DSMLWriterTest.class));
		//$JUnit-END$
		return suite;
	}
}