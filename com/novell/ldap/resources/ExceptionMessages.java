// $Novell: /ldap/src/jldap/com/novell/ldap/LDAPExceptionMessageResource.java,v 1.6 2001/02/27 22:53:14 vtag Exp $
package com.novell.ldap;

public class LDAPExceptionMessageResource extends java.util.ListResourceBundle {
  public Object[][] getContents() {
      return contents;
  }
  //static strings to aide lookup and guarentee accuracy:
  //DO NOT include these strings in other Locales
  public final static String TOSTRING             = "TOSTRING";
  public final static String CONNECTION_ERROR     = "CONNECTION_ERROR";
  public final static String CONNECTION_WAIT      = "CONNECTION_WAIT";
  public final static String CONNECTION_FINALIZED = "CONNECTION_FINALIZED";
  public final static String CONNECTION_CLOSED    = "CONNECTION_CLOSED";
  public final static String DUP_ERROR            = "DUP_ERROR";
  public final static String NO_CONNECT           = "NO_CONNECT";
  public final static String REFERRAL_ERROR       = "REFERRAL_ERROR";
  public final static String REFERRAL_LOCAL       = "REFERRAL_LOCAL";
  public final static String REFERENCE_ERROR      = "REFERENCE_ERROR";
  public final static String REFERRAL_SEND        = "REFERRAL_SEND";
  public final static String REFERRAL_NOFOLLOW    = "REFERRAL_NOFOLLOW";
  public final static String REFERENCE_NOFOLLOW   = "REFERENCE_NOFOLLOW";
  public final static String REFERRAL_INTERNAL    = "REFERRAL_INTERNAL";
  public final static String NO_DUP_REQUEST       = "NO_DUP_REQUEST";
  public final static String SERVER_CONNECT_ERROR = "SERVER_CONNECT_ERROR";
  public final static String NO_PROPERTY          = "NO_PROPERTY";
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
  public final static String UNEXPECTED_END       = "UNEXPECTED_END";
  public final static String MISSING_LEFT_PAREN   = "MISSING_LEFT_PAREN";
  public final static String MISSING_RIGHT_PAREN  = "MISSING_RIGHT_PAREN";
  public final static String INVALID_FILTER       = "INVALID_FILTER";
  public final static String NOT_AN_ATTRIBUTE     = "NOT_AN_ATTRIBUTE";
  public final static String UNEQUAL_LENGTHS      = "UNEQUAL_LENGTHS";
  public final static String COMMUNICATE_ERROR    = "COMMUNICATE_ERROR";
  public final static String MAXHOPS_EXCEEDED     = "MAXHOPS_EXCEEDED";
  public final static String IMPROPER_REFERRAL    = "IMPROPER_REFERRAL";
  public final static String NOT_A_RULESCHEMA     = "NOT_A_RULESCHEMA";
  public final static String NOT_A_RULEUSESHCEMA  = "NOT_A_RULEUSESHCEMA";
  //End constants

  static final Object[][] contents = {
  // LOCALIZE THIS
      {"TOSTRING", "{0} : ({1}) {2}"},
      {"CONNECTION_WAIT", "Connection lost waiting for results from {0}:{1}"},
      {"CONNECTION_FINALIZED", "Connection closed by the application finalizing the object"},
      {"CONNECTION_CLOSED", "Connection closed by the application disconnecting"},
      {"DUP_ERROR", "RfcLDAPMessage: Cannot duplicate message built from the input stream"},
      {"REFERENCE_ERROR","Error attempting to follow a search continuation reference"},
      {"REFERRAL_ERROR","Error attempting to follow a referral"},
      {"REFERRAL_LOCAL", "LDAPSearchResults.{0}(): No entry found & request is not complete"},
      {"NO_CONNECT", "Could not create any connection to follow referral"},
      {"REFERRAL_SEND", "Error sending request to referred server"},
      {"REFERRAL_NOFOLLOW", "Referral received, and referral following is off"},
      {"REFERENCE_NOFOLLOW", "Search result reference received, and referral following is off"},
      {"REFERRAL_INTERNAL", "LDAPCOnnection: checkForReferral: internal error"},
      {"NO_DUP_REQUEST", "Cannot duplicate message to follow referral for {0} request, not allowed"},
      {"SERVER_CONNECT_ERROR","Error connecting to server {0} while attempting to follow a referral"},
      {"NO_PROPERTY","Requested property is not available."},
      {"NO_SUP_PROPERTY","Requested property is not supported."},
      {"ENTRY_PARAM_ERROR", "Invalid Entry parameter"},
      {"DN_PARAM_ERROR", "Invalid DN parameter"},
      {"RDN_PARAM_ERROR", "Invalid DN or RDN parameter"},
      {"OP_PARAM_ERROR", "Invalid extended operation parameter, no OID specified"},
      {"PARAM_ERROR", "Invalid parameter"},
      {"DECODING_ERROR", "Error Decoding responseValue"},
      {"ENCODING_ERROR","Encoding Error"},
      {"IO_EXCEPTION", "I/O Exception on {0}:{1}"},
      {"INVALID_ESCAPE", "Invalid escape value" },
      {"UNEXPECTED_END", "Unexpected end of filter"},
      {"MISSING_LEFT_PAREN", "Missing left paren"},
      {"MISSING_RIGHT_PAREN", "Missing right paren"},
      {"INVALID_FILTER", "Invalid filter type"},
      {"NOT_AN_ATTRIBUTE", "Schema element is not an LDAPAttributeSchema object"},
      {"UNEQUAL_LENGTHS","Length of attribute Name array does not equal length of Flags array"},
      {"COMMUNICATE_ERROR","Communication error: {0}"},
      {"MAXHOPS_EXCEEDED","Max hops exceeded"},
      {"IMPROPER_REFERRAL","Referral not supported for command {0}"},
      {"NOT_A_RULESCHEMA","Schema element is not an LDAPMatchingRuleSchema object"},
      {"NOT_A_RULEUSESHCEMA","Schema element is not an LDAPMatchingRuleUseSchema object"}
  // END OF MATERIAL TO LOCALIZE
  };
}//End LDAPExceptionMessageResource
