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
package com.novell.ldap.resources;

/**
 * This class contains strings corresponding to LDAP Result Codes.
 * The resources are accessed by the String representation of the result code.
 */

public class ResultCodeMessages extends java.util.ListResourceBundle {
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
  //String index key are numbers - the LDAP error codes 0 to 97 -
  //this is why there are blanks
      {"0", "Success"},
      {"1", "Operations Error"},
      {"2", "Protocol Error"},
      {"3", "Timelimit Exceeded"},
      {"4", "Sizelimit Exceeded"},
      {"5", "Compare False"},
      {"6", "Compare True"},
      {"7", "Authentication Method Not Supported"},
      {"8", "Strong Authentication Required"},
      {"9", "Partial Results"},
      {"10", "Referral"},
      {"11", "Administrative Limit Exceeded"},
      {"12", "Unavailable Critical Extension"},
      {"13", "Confidentiality Required"},
      {"14", "SASL Bind In Progress"},
      //15 had no error code assigned this should remain blank
      {"16", "No Such Attribute"},
      {"17", "Undefined Attribute Type"},
      {"18", "Inappropriate Matching"},
      {"19", "Constraint Violation"},
      {"20", "Attribute Or Value Exists"},
      {"21", "Invalid Attribute Syntax"},
      // the following have no error code assigned, they should remain blank
      {"32", "No Such Object"},
      {"33", "Alias Problem"},
      {"34", "Invalid DN Syntax"},
      {"35", "Is Leaf"},
      {"36", "Alias Dereferencing Problem"},
      {"48", "Inappropriate Authentication"},
      {"49", "Invalid Credentials"},
      {"50", "Insufficient Access Rights"},
      {"51", "Busy"},
      {"52", "Unavailable"},
      {"53", "Unwilling To Perform"},
      {"54", "Loop Detect"},
      {"64", "Naming Violation"},
      {"65", "Object Class Violation"},
      {"66", "Not Allowed On Non-leaf"},
      {"67", "Not Allowed On RDN"},
      {"68", "Entry Already Exists"},
      {"69", "Object Class Modifications Prohibited"},
      {"71", "Affects Multiple DSAs"},
      {"80", "Other"},
      {"81", "Server Down"},
      {"82", "Local Error"},
      {"83", "Encoding Error"},
      {"84", "Decoding Error"},
      {"85", "LDAP Timeout"},
      {"86", "Authentication Unknown"},
      {"87", "Filter Error"},
      {"88", "User Cancelled"},
      {"89", "Parameter Error"},
      {"90", "No Memory"},
      {"91", "Connect Error"},
      {"92", "LDAP Not Supported"},
      {"93", "Control Not Found"},
      {"94", "No Results Returned"},
      {"95", "More Results To Return"},
      {"96", "Client Loop"},
      {"97", "Referral Limit Exceeded"},
      {"112", "TLS not supported"}
  // END OF MATERIAL TO LOCALIZE
  };
}//End ResultCodeMessages
