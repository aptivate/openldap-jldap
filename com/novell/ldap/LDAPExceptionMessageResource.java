package com.novell.ldap;

public class LDAPExceptionMessageResource extends java.util.ListResourceBundle {
  public Object[][] getContents() {
      return contents;
  }
  //static strings to aide lookup and guarentee accuracy:
  //DO NOT include these strings in other Locales
  public final static String CONNECT_ERROR        = "CONNECT_ERROR";
  public final static String DECODE_ERROR         = "DECODE_ERROR";
  public final static String PARAMETER_ERROR      = "PARAMETER_ERROR";
  public final static String ENCODE_ERROR         = "ENCODE_ERROR";
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
      {"CONNECT_ERROR", "Unable to connect to server: {0}"},
      {"DECODE_ERROR", "Error Decoding responseValue"},
      {"PARAMETER_ERROR", "Invalid parameter"},
      {"ENCODE_ERROR","Encoding Error"},
      {"INVALID_ESCAPE", "Invalid escape value" },
      {"UNEXPECTED_END", "Unexpected end of filter"},
      {"MISSING_LEFT_PAREN", "Missing left paren"},
      {"MISSING_RIGHT_PAREN", "Missing right paren"},
      {"INVALID_FILTER", "Invalid filter type"},
      {"NOT_AN_ATTRIBUTE", "Schema element is not an LDAPAttributeSchema object"},
      {"UNEQUAL_LENGTHS","Length of attribute Name array does not equal length of Flags array"},
      {"COMMUNICATE_ERROR","Communication error: {0}"},
      {"MAXHOPS_EXCEEDED","Max hops exceeded"},
      {"IMPROPER_REFERRAL","Referral doesn't make sense for command"},
      {"NOT_A_RULESCHEMA","Schema element is not an LDAPMatchingRuleSchema object"},
      {"NOT_A_RULEUSESHCEMA","Schema element is not an LDAPMatchingRuleUseSchema object"}
  // END OF MATERIAL TO LOCALIZE
  };
}//End LDAPExceptionMessageResource
