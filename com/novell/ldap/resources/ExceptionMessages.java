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
 * This class contains strings that may be associated with Exceptions generated
 * by the LDAP API libraries.
 * Two entries are made for each message, a String identifier, and the
 * actual error string.  Parameters are identified as {0}, {1}, etc.
 */
public class ExceptionMessages extends java.util.ListResourceBundle {
  public Object[][] getContents() {
      return contents;
  }
  //static strings to aide lookup and guarantee accuracy:
  //DO NOT include these strings in other Locales
  public final static String TOSTRING             = "TOSTRING";
  public final static String SERVER_MSG           = "SERVER_MSG";
  public final static String MATCHED_DN           = "MATCHED_DN";
  public final static String FAILED_REFERRAL      = "FAILED_REFERRAL";
  public final static String REFERRAL_ITEM        = "REFERRAL_ITEM";
  public final static String CONNECTION_ERROR     = "CONNECTION_ERROR";
  public final static String CONNECTION_IMPOSSIBLE= "CONNECTION_IMPOSSIBLE";
  public final static String CONNECTION_WAIT      = "CONNECTION_WAIT";
  public final static String CONNECTION_FINALIZED = "CONNECTION_FINALIZED";
  public final static String CONNECTION_CLOSED    = "CONNECTION_CLOSED";
  public final static String CONNECTION_READER    = "CONNECTION_READER";
  public final static String DUP_ERROR            = "DUP_ERROR";
  public final static String REFERRAL_ERROR       = "REFERRAL_ERROR";
  public final static String REFERRAL_LOCAL       = "REFERRAL_LOCAL";
  public final static String REFERENCE_ERROR      = "REFERENCE_ERROR";
  public final static String REFERRAL_SEND        = "REFERRAL_SEND";
  public final static String REFERENCE_NOFOLLOW   = "REFERENCE_NOFOLLOW";
  public final static String REFERRAL_BIND        = "REFERRAL_BIND";
  public final static String REFERRAL_BIND_MATCH  = "REFERRAL_BIND_MATCH";
  public final static String NO_DUP_REQUEST       = "NO_DUP_REQUEST";
  public final static String SERVER_CONNECT_ERROR = "SERVER_CONNECT_ERROR";
  public final static String NO_SUP_PROPERTY      = "NO_SUP_PROPERTY";
  public final static String ENTRY_PARAM_ERROR    = "ENTRY_PARAM_ERROR";
  public final static String DN_PARAM_ERROR       = "DN_PARAM_ERROR";
  public final static String RDN_PARAM_ERROR      = "RDN_PARAM_ERROR";
  public final static String OP_PARAM_ERROR       = "OP_PARAM_ERROR";
  public final static String PARAM_ERROR          = "PARAM_ERROR";
  public final static String DECODING_ERROR       = "DECODING_ERROR";
  public final static String ENCODING_ERROR       = "ENCODING_ERROR";
  public final static String IO_EXCEPTION         = "IO_EXCEPTION";
  public final static String INVALID_ESCAPE       = "INVALID_ESCAPE";
  public final static String SHORT_ESCAPE       = "SHORT_ESCAPE";
  public final static String INVALID_CHAR_IN_FILTER = "INVALID_CHAR_IN_FILTER";
  public final static String INVALID_CHAR_IN_DESCR = "INVALID_CHAR_IN_DESCR";
  public final static String INVALID_ESC_IN_DESCR = "INVALID_ESC_IN_DESCR";
  public final static String UNEXPECTED_END       = "UNEXPECTED_END";
  public final static String MISSING_LEFT_PAREN   = "MISSING_LEFT_PAREN";
  public final static String MISSING_RIGHT_PAREN  = "MISSING_RIGHT_PAREN";
  public final static String EXPECTING_RIGHT_PAREN  = "EXPECTING_RIGHT_PAREN";
  public final static String EXPECTING_LEFT_PAREN  = "EXPECTING_LEFT_PAREN";
  public final static String NO_OPTION            = "NO_OPTION";
  public final static String INVALID_FILTER_COMPARISON  = "INVALID_FILTER_COMPARISON";
  public final static String NO_MATCHING_RULE     = "NO_MATCHING_RULE";
  public final static String NO_ATTRIBUTE_NAME    = "NO_ATTRIBUTE_NAME";
  public final static String NO_DN_NOR_MATCHING_RULE = "NO_DN_NOR_MATCHING_RULE";
  public final static String NOT_AN_ATTRIBUTE     = "NOT_AN_ATTRIBUTE";
  public final static String UNEQUAL_LENGTHS      = "UNEQUAL_LENGTHS";
  public final static String IMPROPER_REFERRAL    = "IMPROPER_REFERRAL";
  public final static String NOT_IMPLEMENTED      = "NOT_IMPLEMENTED";
  public final static String NO_MEMORY            = "NO_MEMORY";
  public final static String SERVER_SHUTDOWN_REQ  = "SERVER_SHUTDOWN_REQ";
  public final static String INVALID_ADDRESS      = "INVALID_ADDRESS";
  public final static String UNKNOWN_RESULT       = "UNKNOWN_RESULT";
  public final static String OUTSTANDING_OPERATIONS = "OUTSTANDING_OPERATIONS";
  public final static String WRONG_FACTORY          = "WRONG_FACTORY";
  public final static String NO_TLS_FACTORY         = "NO_TLS_FACTORY";
  public final static String NO_STARTTLS            = "NO_STARTTLS";
  public final static String STOPTLS_ERROR          = "STOPTLS_ERROR";
  public final static String MULTIPLE_SCHEMA        = "MULTIPLE_SCHEMA";
  public final static String NO_SCHEMA              = "NO_SCHEMA";
  public final static String READ_MULTIPLE          = "READ_MULTIPLE";
  public final static String CANNOT_BIND            = "CANNOT_BIND";

  //End constants

  static final Object[][] contents = {
  // LOCALIZE THIS
      {"TOSTRING", "{0}: {1} ({2}) {3}"},
      {"SERVER_MSG", "{0}: Server Message: {1}"},
      {"MATCHED_DN", "{0}: Matched DN: {1}"},
      {"FAILED_REFERRAL", "{0}: Failed Referral: {1}"},
      {"REFERRAL_ITEM", "{0}: Referral: {1}"},
      {"CONNECTION_ERROR", "Unable to connect to server {0}:{1}"},
      {"CONNECTION_IMPOSSIBLE", "Unable to reconnect to server, application has never called connect()"},
      {"CONNECTION_WAIT", "Connection lost waiting for results from {0}:{1}"},
      {"CONNECTION_FINALIZED", "Connection closed by the application finalizing the object"},
      {"CONNECTION_CLOSED", "Connection closed by the application disconnecting"},
      {"CONNECTION_READER", "Reader thread terminated"},
      {"DUP_ERROR", "RfcLDAPMessage: Cannot duplicate message built from the input stream"},
      {"REFERENCE_ERROR","Error attempting to follow a search continuation reference"},
      {"REFERRAL_ERROR","Error attempting to follow a referral"},
      {"REFERRAL_LOCAL", "LDAPSearchResults.{0}(): No entry found & request is not complete"},
      {"REFERRAL_SEND", "Error sending request to referred server"},
      {"REFERENCE_NOFOLLOW", "Search result reference received, and referral following is off"},
      {"REFERRAL_BIND", "LDAPBind.bind() function returned null"},
      {"REFERRAL_BIND_MATCH", "Could not match LDAPBind.bind() connection with Server Referral URL list"},
      {"NO_DUP_REQUEST", "Cannot duplicate message to follow referral for {0} request, not allowed"},
      {"SERVER_CONNECT_ERROR","Error connecting to server {0} while attempting to follow a referral"},
      {"NO_SUP_PROPERTY","Requested property is not supported."},
      {"ENTRY_PARAM_ERROR", "Invalid Entry parameter"},
      {"DN_PARAM_ERROR", "Invalid DN parameter"},
      {"RDN_PARAM_ERROR", "Invalid DN or RDN parameter"},
      {"OP_PARAM_ERROR", "Invalid extended operation parameter, no OID specified"},
      {"PARAM_ERROR", "Invalid parameter"},
      {"DECODING_ERROR", "Error Decoding responseValue"},
      {"ENCODING_ERROR","Encoding Error"},
      {"IO_EXCEPTION", "I/O Exception on host {0}, port {1}"},
      {"INVALID_ESCAPE", "Invalid value in escape sequence \"{0}\"" },
      {"SHORT_ESCAPE", "Incomplete escape sequence"},
      {"UNEXPECTED_END", "Unexpected end of filter"},
      {"MISSING_LEFT_PAREN", "Unmatched parentheses, left parenthesis missing"},
      {"NO_OPTION", "Semicolon present, but no option specified"},
      {"MISSING_RIGHT_PAREN", "Unmatched parentheses, right parenthesis missing"},
      {"EXPECTING_RIGHT_PAREN", "Expecting right parenthesis, found \"{0}\""},
      {"EXPECTING_LEFT_PAREN", "Expecting left parenthesis, found \"{0}\""},
      {"NO_ATTRIBUTE_NAME", "Missing attribute description"},
      {"NO_DN_NOR_MATCHING_RULE", "DN and matching rule not specified"},
      {"NO_MATCHING_RULE", "Missing matching rule"},
      {"INVALID_FILTER_COMPARISON", "Invalid comparison operator"},
      {"INVALID_CHAR_IN_FILTER", "The invalid character \"{0}\" needs to be escaped as \"{1}\""},
      {"INVALID_ESC_IN_DESCR", "Escape sequence not allowed in attribute description"},
      {"INVALID_CHAR_IN_DESCR", "Invalid character \"{0}\" in attribute description"},
      {"NOT_AN_ATTRIBUTE", "Schema element is not an LDAPAttributeSchema object"},
      {"UNEQUAL_LENGTHS","Length of attribute Name array does not equal length of Flags array"},
      {"IMPROPER_REFERRAL","Referral not supported for command {0}"},
      {"NOT_IMPLEMENTED","Method LDAPConnection.startTLS not implemented"},
      {"NO_MEMORY","All results could not be stored in memory, sort failed"},
	  {"SERVER_SHUTDOWN_REQ","Received unsolicited notification from server {0}:{1} to shutdown"},
      {"INVALID_ADDRESS","Invalid syntax for address with port; {0}"},
      {"UNKNOWN_RESULT","Unknown LDAP result code {0}"},
      {"OUTSTANDING_OPERATIONS", "Cannot start or stop TLS because outstanding LDAP operations exist on this connection" },
      {"WRONG_FACTORY", "StartTLS cannot use the set socket factory because it does not implement LDAPTLSSocketFactory"},
      {"NO_TLS_FACTORY", "StartTLS failed because no LDAPTLSSocketFactory has been set for this Connection" },
      {"NO_STARTTLS", "An attempt to stopTLS on a connection where startTLS had not been called"},
      {"STOPTLS_ERROR", "Error stopping TLS: Error getting input & output streams from the original socket"},
      {"MULTIPLE_SCHEMA", "Multiple schema found when reading the subschemaSubentry for {0}"}, //the 0th parameter is a String DN
      {"NO_SCHEMA", "No schema found when reading the subschemaSubentry for {0}"}, //the 0th parameter is a String DN
      {"READ_MULTIPLE", "Read response is ambiguous, multiple entries returned"},
      {"CANNOT_BIND", "Cannot bind. Use PoolManager.getBoundConnection()"}
  // END OF MATERIAL TO LOCALIZE
  };
}//End ExceptionMessages
