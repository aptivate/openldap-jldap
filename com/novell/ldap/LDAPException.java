/* **************************************************************************
 * $Novell: LDAPException.java,v 1.2 2000/03/14 18:17:27 smerrill Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/
 
package org.ietf.ldap;

import java.util.Locale;
 
/**
 * 4.13 public class LDAPException
 *                 extends Exception
 *
 *  Thrown to indicate that an error has occurred. An LDAPException can
 *  result from physical problems (such as network errors) as well as
 *  problems with LDAP operations (for example, if the LDAP add operation
 *  fails because of a duplicate entry).
 *
 *  Most errors that occur throw this type of exception.  The
 *  getLDAPResultCode() method returns the specific result code, which
 *  can be compared against standard LDAP result codes as defined in [6],
 *  section 4.
 */
public class LDAPException extends Exception {

   private int resultCode;
   private String errorMessage = null;
   private String matchedDN = null;

   /*
    * 4.13.1 Constructors
    */

   /**
    * Constructs a default exception with no specific error information.
    */
   public LDAPException() {
      super();
   }

   /**
    * Constructs an exception with an error code and a specified string as
    * additional information.
    *
    * Parameters are:
    *
    *  message        The additional error information.
    *
    *  resultCode     The result code returned
    */
   public LDAPException(String message, int resultCode) {
      super(message);
      this.resultCode = resultCode;
   }

   /**
    * this is not in the draft yet.
    */
   public LDAPException(String errorMessage, String matchedDN,
                        int resultCode) {
      super();
      this.errorMessage = errorMessage;
      this.matchedDN = matchedDN;
      this.resultCode = resultCode;
   }


   /*
    * 4.13.2 errorCodeToString
    */

   /**
    * Returns a String representing the internal error code, in the default
    * Locale.
    */
   public String errorCodeToString() {
		return errorStrings[resultCode];
   }

   /**
    * Returns a String representing an arbitrary error code, in the default
    * Locale, or null if there is no such code.
    */
   public static String errorCodeToString( int code ) {
      return null;
   }

   /**
    * Returns a String representing the internal error code, in the
    * specified Locale, or null if a String representation is not available
    * for the requested Locale.
    */
   public String errorCodeToString( Locale locale ) {
      return null;
   }

   /**
    * Returns a String representing an arbitrary error code, in the
    * specified Locale, or null if there is no such code or if a String
    * representation is not available for the requested Locale.
    *
    * Parameters are:
    *
    *  code           One of the error codes listed in "Error codes"
    *                  below.
    *
    *  locale         A Locale in which to render the error String.
    */
   public static String errorCodeToString( int code, Locale locale ) {
      return null;
   }

   /*
    * 4.13.3 getLDAPErrorMessage
    */

   /**
    * Returns the error message, if this message is available (that is, if
    * this message was set). If the message was not set, this method
    * returns null.
    */
   public String getLDAPErrorMessage() {
      return errorMessage;
   }

   /*
    * 4.13.4 getLDAPResultCode
    */

   /**
    * Returns the result code from the exception. The codes are defined as
    * public final static int members of this class. If the exception is a
    * result of error information returned from a directory operation, the
    * code will be one of those defined in [6]. Otherwise, a local error
    * code is returned (see "Error codes" below).
    */
   public int getLDAPResultCode() {
      return resultCode;
   }

   /*
    * 4.13.5 getMatchedDN
    */

   /**
    * Returns the part of a submitted distinguished name which could be
    * matched by the server. If the exception was caused by a local error,
    * such as no server available, the return value is null. If the
    * exception resulted from an operation being executed on a server, the
    * value is an empty String except when the result of the operation was
    * one of the following:
    *
    *  NO_SUCH_OBJECT
    *  ALIAS_PROBLEM
    *  INVALID_DN_SYNTAX
    *  ALIAS_DEREFERENCING_PROBLEM
    */
   public String getMatchedDN() {
      return matchedDN;
   }

   /*
    * 4.13.6 Error codes
    */

   /**
    * See RFC 2251 for a discussion of the meanings of the codes.
    *
    *    ADMIN_LIMIT_EXCEEDED
    *    AFFECTS_MULTIPLE_DSAS
    *    ALIAS_DEREFERENCING_PROBLEM
    *    ALIAS_PROBLEM
    *    ATTRIBUTE_OR_VALUE_EXISTS
    *    AUTH_METHOD_NOT_SUPPORTED
    *    BUSY
    *    COMPARE_FALSE
    *    COMPARE_TRUE
    *    CONFIDENTIALITY_REQUIRED
    *    CONSTRAINT_VIOLATION
    *    ENTRY_ALREADY_EXISTS
    *    INAPPROPRIATE_AUTHENTICATION
    *    INAPPROPRIATE_MATCHING
    *    INSUFFICIENT_ACCESS_RIGHTS
    *    INVALID_ATTRIBUTE_SYNTAX
    *    INVALID_CREDENTIALS
    *    INVALID_DN_SYNTAX
    *    IS_LEAF
    *    LDAP_PARTIAL_RESULTS
    *    LOOP_DETECT
    *    NAMING_VIOLATION
    *    NO_SUCH_ATTRIBUTE
    *    NO_SUCH_OBJECT
    *    NOT_ALLOWED_ON_NONLEAF
    *    NOT_ALLOWED_ON_RDN
    *    OBJECT_CLASS_MODS_PROHIBITED
    *    OBJECT_CLASS_VIOLATION
    *    OPERATIONS_ERROR
    *    OTHER
    *    PROTOCOL_ERROR
    *    REFERRAL
    *    SASL_BIND_IN_PROGRESS
    *    SIZE_LIMIT_EXCEEDED
    *    STRONG_AUTH_REQUIRED
    *    SUCCESS
    *    TIME_LIMIT_EXCEEDED
    *    UNAVAILABLE
    *    UNAVAILABLE_CRITICAL_EXTENSION
    *    UNDEFINED_ATTRIBUTE_TYPE
    *    UNWILLING_TO_PERFORM
    *
    * Local errors, resulting from actions other than an operation on a
    * server, are among the following, listed in [ldap-c-api]:
    *
    *    AUTH_UNKNOWN
    *    CLIENT_LOOP
    *    CONNECT_ERROR
    *    CONTROL_NOT_FOUND
    *    DECODING_ERROR
    *    ENCODING_ERROR
    *    FILTER_ERROR
    *    LOCAL_ERROR
    *    LDAP_NOT_SUPPORTED
    *    LDAP_TIMEOUT
    *    MORE_RESULTS_TO_RETURN
    *    NO_MEMORY
    *    NO_RESULTS_RETURNED
    *    PARAM_ERROR
    *    REFERRAL_LIMIT_EXCEEDED
    *    SERVER_DOWN
    *    USER_CANCELLED
    */

   public final static int SUCCESS = 0;
   public final static int OPERATIONS_ERROR = 1;
   public final static int PROTOCOL_ERROR = 2;
   public final static int TIME_LIMIT_EXCEEDED = 3;
   public final static int SIZE_LIMIT_EXCEEDED = 4;
   public final static int COMPARE_FALSE = 5;
   public final static int COMPARE_TRUE = 6;
   public final static int AUTH_METHOD_NOT_SUPPORTED = 7;
   public final static int STRONG_AUTH_REQUIRED = 8;
	public final static int PARTIAL_RESULTS = 9;
   public final static int REFERRAL = 10;
   public final static int ADMIN_LIMIT_EXCEEDED = 11;
   public final static int UNAVAILABLE_CRITICAL_EXTENSION = 12;
   public final static int CONFIDENTIALITY_REQUIRED = 13;
   public final static int SASL_BIND_IN_PROGRESS = 14;
   public final static int NO_SUCH_ATTRIBUTE = 16;
   public final static int UNDEFINED_ATTRIBUTE_TYPE = 17;
   public final static int INAPPROPRIATE_MATCHING = 18;
   public final static int CONSTRAINT_VIOLATION = 19;
   public final static int ATTRIBUTE_OR_VALUE_EXISTS = 20;
   public final static int INVALID_ATTRIBUTE_SYNTAX = 21;
   public final static int NO_SUCH_OBJECT = 32;
   public final static int ALIAS_PROBLEM = 33;
   public final static int INVALID_DN_SYNTAX = 34;
   public final static int IS_LEAF = 35;
   public final static int ALIAS_DEREFERENCING_PROBLEM = 36;
   public final static int INAPPROPRIATE_AUTHENTICATION = 48;
   public final static int INVALID_CREDENTIALS = 49;
   public final static int INSUFFICIENT_ACCESS_RIGHTS = 50;
   public final static int BUSY = 51;
   public final static int UNAVAILABLE = 52;
   public final static int UNWILLING_TO_PERFORM = 53;
   public final static int LOOP_DETECT = 54;
   public final static int NAMING_VIOLATION = 64;
   public final static int OBJECT_CLASS_VIOLATION = 65;
   public final static int NOT_ALLOWED_ON_NONLEAF = 66;
   public final static int NOT_ALLOWED_ON_RDN = 67;
   public final static int ENTRY_ALREADY_EXISTS = 68;
   public final static int OBJECT_CLASS_MODS_PROHIBITED = 69;
   public final static int AFFECTS_MULTIPLE_DSAS = 71;
   public final static int OTHER = 80;
   public final static int SERVER_DOWN = 81;
   public final static int LOCAL_ERROR = 82;
   public final static int ENCODING_ERROR = 83;
   public final static int DECODING_ERROR = 84;
   public final static int LDAP_TIMEOUT = 85;
   public final static int AUTH_UNKNOWN = 86;
   public final static int FILTER_ERROR = 87;
   public final static int USER_CANCELLED = 88;
   public final static int PARAM_ERROR = 89;
   public final static int NO_MEMORY = 90;
   public final static int CONNECT_ERROR = 91;
   public final static int LDAP_NOT_SUPPORTED = 92;
   public final static int CONTROL_NOT_FOUND = 93;
   public final static int NO_RESULTS_RETURNED = 94;
   public final static int MORE_RESULTS_TO_RETURN = 95;
   public final static int CLIENT_LOOP = 96;
   public final static int REFERRAL_LIMIT_EXCEEDED = 97;
         
   static final String[] errorStrings = {
      "Success",                                // 0
      "Operations Error",                       // 1
      "Protocol Error",                         // 2
      "Timelimit Exceeded",                     // 3
      "Sizelimit Exceeded",                     // 4
      "Compare False",                          // 5
      "Compare True",                           // 6
      "Authentication Method Not Supported",    // 7
      "Strong Authentication Required",         // 8
      "Partial Results",                        // 9
      "Referral",                               // 10
      "Administrative Limit Exceeded",          // 11
      "Unavailable Critical Extension",         // 12
      "Confidentiality Required",               // 13
      "SASL Bind In Progress",                  // 14
      null,
      "No Such Attribute",                      // 16
      "Undefined Attribute Type",               // 17
      "Inappropriate Matching",                 // 18
      "Constraint Violation",                   // 19
      "Attribute Or Value Exists",              // 20
      "Invalid Attribute Syntax",               // 21
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "No Such Object",                         // 32
      "Alias Problem",                          // 33
      "Invalid DN Syntax",                      // 34
      "Is Leaf",                                // 35
      "Alias Dereferencing Problem",            // 36
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Inappropriate Authentication",           // 48
      "Invalid Credentials",                    // 49
      "Insufficient Access Rights",             // 50
      "Busy",                                   // 51
      "Unavailable",                            // 52
      "Unwilling To Perform",                   // 53
      "Loop Detect",                            // 54
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Naming Violation",                       // 64
      "Object Class Violation",                 // 65
      "Not Allowed On Non-leaf",                // 66
      "Not Allowed On RDN",                     // 67
      "Entry Already Exists",                   // 68
      "Object Class Modifications Prohibited",  // 69
      null,
      "Affects Multiple DSAs",                  // 71
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Other",                                  // 80
      "Server Down",                            // 81
      "Local Error",                            // 82
      "Encoding Error",                         // 83
      "Decoding Error",                         // 84
      "Ldap Timeout",                           // 85
      "Authentication Unknown",                 // 86
      "Filter Error",                           // 87
      "User Cancelled",                         // 88
      "Parameter Error",                        // 89
      "No Memory",                              // 90
		"Connect Error",                          // 91
		"Ldap Not Supported",                     // 92
		"Control Not Found",                      // 93
		"No Results Returned",                    // 94
		"More Results To Return",                 // 95
		"Client Loop",                            // 96
		"Referral Limit Exceeded"                 // 97
   };

	/**
	 *
	 */
	public String toString() {
		return super.toString() + ": (" + resultCode + ") " +
			    errorCodeToString();
	}
}
