/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPException.java,v 1.27 2001/04/23 21:05:34 vtag Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package org.ietf.ldap;

import java.util.Locale;
/**
 *  Thrown to indicate that an LDAP error has occurred. This is a general
 *  exception which includes an error message and an LDAP error code.
 *
 * @see com.novell.ldap.LDAPException
 */

public class LDAPException extends Exception
{
    private com.novell.ldap.LDAPException exception;

    /**
     *Indicates the requested client operation completed successfully.
     *
     * @see com.novell.ldap.LDAPException#SUCCESS
     */
    public final static int SUCCESS = 
			    com.novell.ldap.LDAPException.SUCCESS;

    /**
     * Indicates an internal error.
     *
     * @see com.novell.ldap.LDAPException#OPERATIONS_ERROR
     */
    public final static int OPERATIONS_ERROR = 
			    com.novell.ldap.LDAPException.OPERATIONS_ERROR;

    /**
     * Indicates that the server has received an invalid or malformed request
     * from the client.
     *
     * @see com.novell.ldap.LDAPException#PROTOCOL_ERROR
     */
    public final static int PROTOCOL_ERROR = 
			    com.novell.ldap.LDAPException.PROTOCOL_ERROR;

    /**
     * Indicates that the operation's time limit specified by either the
     * client or the server has been exceeded.
     *
     * @see com.novell.ldap.LDAPException#TIME_LIMIT_EXCEEDED
     */
    public final static int TIME_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.TIME_LIMIT_EXCEEDED;

    /**
     * Indicates that in a search operation, the size limit specified by
     * the client or the server has been exceeded.
     *
     * @see com.novell.ldap.LDAPException#SIZE_LIMIT_EXCEEDED
     */
    public final static int SIZE_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.SIZE_LIMIT_EXCEEDED;

    /**
     * Does not indicate an error condition. Indicates that the results of
     * a compare operation are false.
     *
     * @see com.novell.ldap.LDAPException#COMPARE_FALSE
     */
    public final static int COMPARE_FALSE = 
			    com.novell.ldap.LDAPException.COMPARE_FALSE;

    /**
     * Does not indicate an error condition. Indicates that the results of a
     * compare operation are true.
     *
     * @see com.novell.ldap.LDAPException#COMPARE_TRUE
     */
    public final static int COMPARE_TRUE = 
			    com.novell.ldap.LDAPException.COMPARE_TRUE;

    /**
     * Indicates that during a bind operation the client requested an
     * authentication method not supported by the LDAP server.
     *
     * @see com.novell.ldap.LDAPException#AUTH_METHOD_NOT_SUPPORTED
     */
    public final static int AUTH_METHOD_NOT_SUPPORTED = 
			    com.novell.ldap.LDAPException.AUTH_METHOD_NOT_SUPPORTED;

    /**
     *Indicates a problem with the level of authentication.
     *
     * @see com.novell.ldap.LDAPException#STRONG_AUTH_REQUIRED
     */
    public final static int STRONG_AUTH_REQUIRED = 
			    com.novell.ldap.LDAPException.STRONG_AUTH_REQUIRED;

    /**
     * Does not indicate an error condition. In LDAPv3, indicates that the
     * server does not hold the target entry of the request, but that the
     * servers in the referral field may.
     *
     * @see com.novell.ldap.LDAPException#REFERRAL
     */
    public final static int REFERRAL = 
			    com.novell.ldap.LDAPException.REFERRAL;

    /**
     * Indicates that an LDAP server limit set by an administrative authority
     * has been exceeded.
     *
     * @see com.novell.ldap.LDAPException#ADMIN_LIMIT_EXCEEDED
     */
    public final static int ADMIN_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.ADMIN_LIMIT_EXCEEDED;

    /**
     * Indicates that the LDAP server was unable to satisfy a request because
     * one or more critical extensions were not available.
     *
     * @see com.novell.ldap.LDAPException#UNAVAILABLE_CRITICAL_EXTENSION
     */
    public final static int UNAVAILABLE_CRITICAL_EXTENSION = 
			    com.novell.ldap.LDAPException.UNAVAILABLE_CRITICAL_EXTENSION;

    /**
     * Indicates that the session is not protected by a protocol such as
     * Transport Layer Security (TLS), which provides session confidentiality.
     *
     * @see com.novell.ldap.LDAPException#CONFIDENTIALITY_REQUIRED
     */
    public final static int CONFIDENTIALITY_REQUIRED = 
			    com.novell.ldap.LDAPException.CONFIDENTIALITY_REQUIRED;

    /**
     * Does not indicate an error condition, but indicates that the server is
     * ready for the next step in the process.
     *
     * @see com.novell.ldap.LDAPException#SASL_BIND_IN_PROGRESS
     */
    public final static int SASL_BIND_IN_PROGRESS = 
			    com.novell.ldap.LDAPException.SASL_BIND_IN_PROGRESS;

    /**
     * Indicates that the attribute specified in the modify or compare
     * operation does not exist in the entry.
     *
     * @see com.novell.ldap.LDAPException#NO_SUCH_ATTRIBUTE
     */
    public final static int NO_SUCH_ATTRIBUTE = 
			    com.novell.ldap.LDAPException.NO_SUCH_ATTRIBUTE;

    /**
     * Indicates that the attribute specified in the modify or add operation
     * does not exist in the LDAP server's schema.
     *
     * @see com.novell.ldap.LDAPException#UNDEFINED_ATTRIBUTE_TYPE
     */
    public final static int UNDEFINED_ATTRIBUTE_TYPE = 
			    com.novell.ldap.LDAPException.UNDEFINED_ATTRIBUTE_TYPE;

    /**
     * Indicates that the matching rule specified in the search filter does
     * not match a rule defined for the attribute's syntax.
     *
     * @see com.novell.ldap.LDAPException#INAPPROPRIATE_MATCHING
     */
    public final static int INAPPROPRIATE_MATCHING = 
			    com.novell.ldap.LDAPException.INAPPROPRIATE_MATCHING;

    /**
     * Indicates that the attribute value specified in a modify, add, or
     * modify DN operation violates constraints placed on the attribute.
     *
     * @see com.novell.ldap.LDAPException#CONSTRAINT_VIOLATION
     */
    public final static int CONSTRAINT_VIOLATION = 
			    com.novell.ldap.LDAPException.CONSTRAINT_VIOLATION;

    /**
     * Indicates that the attribute value specified in a modify or add
     * operation already exists as a value for that attribute.
     *
     * @see com.novell.ldap.LDAPException#ATTRIBUTE_OR_VALUE_EXISTS
     */
    public final static int ATTRIBUTE_OR_VALUE_EXISTS = 
			    com.novell.ldap.LDAPException.ATTRIBUTE_OR_VALUE_EXISTS;

    /**
     * Indicates that the attribute value specified in an add, compare, or
     * modify operation is an unrecognized or invalid syntax for the attribute.
     *
     * @see com.novell.ldap.LDAPException#INVALID_ATTRIBUTE_SYNTAX
     */
    public final static int INVALID_ATTRIBUTE_SYNTAX = 
			    com.novell.ldap.LDAPException.INVALID_ATTRIBUTE_SYNTAX;

    /**
     * Indicates the target object cannot be found.
     *
     * @see com.novell.ldap.LDAPException#NO_SUCH_OBJECT
     */
    public final static int NO_SUCH_OBJECT = 
			    com.novell.ldap.LDAPException.NO_SUCH_OBJECT;

    /**
     * Indicates that an error occurred when an alias was dereferenced.
     *
     * @see com.novell.ldap.LDAPException#ALIAS_PROBLEM
     */
    public final static int ALIAS_PROBLEM = 
			    com.novell.ldap.LDAPException.ALIAS_PROBLEM;

    /**
     * Indicates that the syntax of the DN is incorrect.
     *
     * @see com.novell.ldap.LDAPException#INVALID_DN_SYNTAX
     */
    public final static int INVALID_DN_SYNTAX = 
			    com.novell.ldap.LDAPException.INVALID_DN_SYNTAX;

    /**
     * Indicates that the specified operation cannot be performed on a
     * leaf entry.
     *
     * @see com.novell.ldap.LDAPException#IS_LEAF
     */
    public final static int IS_LEAF = 
			    com.novell.ldap.LDAPException.IS_LEAF;

    /**
     * Indicates that during a search operation, either the client does not
     * have access rights to read the aliased object's name or dereferencing
     * is not allowed.
     *
     * @see com.novell.ldap.LDAPException#ALIAS_DEREFERENCING_PROBLEM
     */
    public final static int ALIAS_DEREFERENCING_PROBLEM = 
			    com.novell.ldap.LDAPException.ALIAS_DEREFERENCING_PROBLEM;

    /**
     * Indicates that during a bind operation, the client is attempting to use
     * an authentication method that the client cannot use correctly.
     *
     * @see com.novell.ldap.LDAPException#INAPPROPRIATE_AUTHENTICATION
     */
    public final static int INAPPROPRIATE_AUTHENTICATION = 
			    com.novell.ldap.LDAPException.INAPPROPRIATE_AUTHENTICATION;

    /**
     * Indicates that invalid information was passed during a bind operation.
     *
     * @see com.novell.ldap.LDAPException#INVALID_CREDENTIALS
     */
    public final static int INVALID_CREDENTIALS = 
			    com.novell.ldap.LDAPException.INVALID_CREDENTIALS;

    /**
     * Indicates that the caller does not have sufficient rights to perform
     * the requested operation.
     *
     * @see com.novell.ldap.LDAPException#INSUFFICIENT_ACCESS_RIGHTS
     */
    public final static int INSUFFICIENT_ACCESS_RIGHTS = 
			    com.novell.ldap.LDAPException.INSUFFICIENT_ACCESS_RIGHTS;

    /**
     * Indicates that the LDAP server is too busy to process the client request
     * at this time, but if the client waits and resubmits the request, the
     * server may be able to process it then.
     *
     * @see com.novell.ldap.LDAPException#BUSY
     */
    public final static int BUSY = 
			    com.novell.ldap.LDAPException.BUSY;

    /**
     * Indicates that the LDAP server cannot process the client's bind
     * request, usually because it is shutting down.
     *
     * @see com.novell.ldap.LDAPException#UNAVAILABLE
     */
    public final static int UNAVAILABLE = 
			    com.novell.ldap.LDAPException.UNAVAILABLE;

    /**
     * Indicates that the LDAP server cannot process the request because of
     * server-defined restrictions.
     *
     * @see com.novell.ldap.LDAPException#UNWILLING_TO_PERFORM
     */
    public final static int UNWILLING_TO_PERFORM = 
			    com.novell.ldap.LDAPException.UNWILLING_TO_PERFORM;

    /**
     * Indicates that the client discovered an alias or referral loop,
     * and is thus unable to complete this request.
     *
     * @see com.novell.ldap.LDAPException#LOOP_DETECT
     */
    public final static int LOOP_DETECT = 
			    com.novell.ldap.LDAPException.LOOP_DETECT;

    /**
     * Indicates that the add or modify DN operation violates the schema's
     * structure rules.
     *
     * @see com.novell.ldap.LDAPException#NAMING_VIOLATION
     */
    public final static int NAMING_VIOLATION = 
			    com.novell.ldap.LDAPException.NAMING_VIOLATION;

    /**
     * Indicates that the add, modify, or modify DN operation violates the
     * object class rules for the entry.
     *
     * @see com.novell.ldap.LDAPException#OBJECT_CLASS_VIOLATION
     */
    public final static int OBJECT_CLASS_VIOLATION = 
			    com.novell.ldap.LDAPException.OBJECT_CLASS_VIOLATION;

    /**
     * Indicates that the requested operation is permitted only on leaf entries.
     *
     * @see com.novell.ldap.LDAPException#NOT_ALLOWED_ON_NONLEAF
     */
    public final static int NOT_ALLOWED_ON_NONLEAF = 
			    com.novell.ldap.LDAPException.NOT_ALLOWED_ON_NONLEAF;

    /**
     * Indicates that the modify operation attempted to remove an attribute
     * value that forms the entry's relative distinguished name.
     *
     * @see com.novell.ldap.LDAPException#NOT_ALLOWED_ON_RDN
     */
    public final static int NOT_ALLOWED_ON_RDN = 
			    com.novell.ldap.LDAPException.NOT_ALLOWED_ON_RDN;

    /**
     * Indicates that the add operation attempted to add an entry that already
     * exists, or that the modify operation attempted to rename an entry to the
     * name of an entry that already exists.
     *
     * @see com.novell.ldap.LDAPException#ENTRY_ALREADY_EXISTS
     */
    public final static int ENTRY_ALREADY_EXISTS = 
			    com.novell.ldap.LDAPException.ENTRY_ALREADY_EXISTS;

    /**
     * Indicates that the modify operation attempted to modify the structure
     * rules of an object class.
     *
     * @see com.novell.ldap.LDAPException#OBJECT_CLASS_MODS_PROHIBITED
     */
    public final static int OBJECT_CLASS_MODS_PROHIBITED = 
			    com.novell.ldap.LDAPException.OBJECT_CLASS_MODS_PROHIBITED;

    /**
     * Indicates that the modify DN operation moves the entry from one LDAP
     * server to another and thus requires more than one LDAP server.
     *
     * @see com.novell.ldap.LDAPException#AFFECTS_MULTIPLE_DSAS
     */
    public final static int AFFECTS_MULTIPLE_DSAS = 
			    com.novell.ldap.LDAPException.AFFECTS_MULTIPLE_DSAS;

    /**
     * Indicates an unknown error condition.
     *
     * @see com.novell.ldap.LDAPException#OTHER
     */
    public final static int OTHER = 
			    com.novell.ldap.LDAPException.OTHER;

    /**
     * Indicates that the LDAP libraries cannot establish an initial connection
     * with the LDAP server. Either the LDAP server is down or the specified
     * host name or port number is incorrect.
     *
     * @see com.novell.ldap.LDAPException#SERVER_DOWN
     */
    public final static int SERVER_DOWN = 
			    com.novell.ldap.LDAPException.SERVER_DOWN;

    /**
     * Indicates that the LDAP client has an error. This is usually a failed
     * dynamic memory allocation error.
     *
     * @see com.novell.ldap.LDAPException#LOCAL_ERROR
     */
    public final static int LOCAL_ERROR = 
			    com.novell.ldap.LDAPException.LOCAL_ERROR;

    /**
     * Indicates that the LDAP client encountered errors when encoding an
     * LDAP request intended for the LDAP server.
     *
     * @see com.novell.ldap.LDAPException#ENCODING_ERROR
     */
    public final static int ENCODING_ERROR = 
			    com.novell.ldap.LDAPException.ENCODING_ERROR;

    /**
     * Indicates that the LDAP client encountered errors when decoding an
     * LDAP response from the LDAP server.
     *
     * @see com.novell.ldap.LDAPException#DECODING_ERROR
     */
    public final static int DECODING_ERROR = 
			    com.novell.ldap.LDAPException.DECODING_ERROR;

    /**
     * Indicates that the time limit of the LDAP client was exceeded while
     * waiting for a result.
     *
     * @see com.novell.ldap.LDAPException#LDAP_TIMEOUT
     */
    public final static int LDAP_TIMEOUT = 
			    com.novell.ldap.LDAPException.LDAP_TIMEOUT;

    /**
     * Indicates that a bind method was called with an unknown
     * authentication method.
     *
     * @see com.novell.ldap.LDAPException#AUTH_UNKNOWN
     */
    public final static int AUTH_UNKNOWN = 
			    com.novell.ldap.LDAPException.AUTH_UNKNOWN;

    /**
     * Indicates that the search method was called with an invalid
     * search filter.
     *
     * @see com.novell.ldap.LDAPException#FILTER_ERROR
     */
    public final static int FILTER_ERROR = 
			    com.novell.ldap.LDAPException.FILTER_ERROR;

    /**
     * Indicates that the user cancelled the LDAP operation.
     *
     * @see com.novell.ldap.LDAPException#USER_CANCELLED
     */
    public final static int USER_CANCELLED = 
			    com.novell.ldap.LDAPException.USER_CANCELLED;

    /**
     * Indicates that a dynamic memory allocation method failed when calling
     * an LDAP method.
     *
     * @see com.novell.ldap.LDAPException#NO_MEMORY
     */
    public final static int NO_MEMORY = 
			    com.novell.ldap.LDAPException.NO_MEMORY;

    /**
     * Indicates that the LDAP client has lost either its connection or
     * cannot establish a connection to the LDAP server.
     *
     * @see com.novell.ldap.LDAPException#CONNECT_ERROR
     */
    public final static int CONNECT_ERROR = 
			    com.novell.ldap.LDAPException.CONNECT_ERROR;

    /**
     * Indicates that the requested functionality is not supported by the
     * client.
     *
     * @see com.novell.ldap.LDAPException#LDAP_NOT_SUPPORTED
     */
    public final static int LDAP_NOT_SUPPORTED = 
			    com.novell.ldap.LDAPException.LDAP_NOT_SUPPORTED;

    /**
     * Indicates that the client requested a control that the libraries
     * cannot find in the list of supported controls sent by the LDAP server.
     *
     * @see com.novell.ldap.LDAPException#CONTROL_NOT_FOUND
     */
    public final static int CONTROL_NOT_FOUND = 
			    com.novell.ldap.LDAPException.CONTROL_NOT_FOUND;

    /**
     * Indicates that the LDAP server sent no results.
     *
     * @see com.novell.ldap.LDAPException#NO_RESULTS_RETURNED
     */
    public final static int NO_RESULTS_RETURNED = 
			    com.novell.ldap.LDAPException.NO_RESULTS_RETURNED;

    /**
     * Indicates that more results are chained in the result message.
     *
     * @see com.novell.ldap.LDAPException#MORE_RESULTS_TO_RETURN
     */
    public final static int MORE_RESULTS_TO_RETURN = 
			    com.novell.ldap.LDAPException.MORE_RESULTS_TO_RETURN;

    /**
     * Indicates the LDAP libraries detected a loop. Usually this happens
     * when following referrals.
     *
     * @see com.novell.ldap.LDAPException#CLIENT_LOOP
     */
    public final static int CLIENT_LOOP = 
			    com.novell.ldap.LDAPException.CLIENT_LOOP;

    /**
     * Indicates that the referral exceeds the hop limit. The default hop
     * limit is ten.
     *
     * @see com.novell.ldap.LDAPException#REFERRAL_LIMIT_EXCEEDED
     */
    public final static int REFERRAL_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.REFERRAL_LIMIT_EXCEEDED;

   /**
    * Indicates that the server response to a request is invalid
    *
    * <p>INVALID_RESPONSE = 100</p>
    */
   public final static int INVALID_RESPONSE = 
			    com.novell.ldap.LDAPException.INVALID_RESPONSE;

   /**
    * Indicates that the server response to a request is ambiguous
    *
    * <p>AMBIGUOUS_RESPONSE = 101</p>
    */
   public final static int AMBIGUOUS_RESPONSE = 
			    com.novell.ldap.LDAPException.AMBIGUOUS_RESPONSE;

    /**
     * Indicates that TLS is not supported on the server.
     *
     * @see com.novell.ldap.LDAPException#TLS_NOT_SUPPORTED
     */
    public final static int TLS_NOT_SUPPORTED = 
			    com.novell.ldap.LDAPException.TLS_NOT_SUPPORTED;

    /*
     * Constructs a default exception with no specific error information.
     *
     * @see com.novell.ldap.LDAPException#LDAPException()
     */
    public LDAPException()
    {
        super();
        exception = new com.novell.ldap.LDAPException();
        return;
    }

    /**
     * Constructs an LDAPException class using com.novell.ldap.LDAPException
     */
    /* package */
    LDAPException( com.novell.ldap.LDAPException ex)
    {
        super();
        exception = ex;
        return;
    }

    /**
     * Constructs an exception with with a detail message 
     * String and the result code.
     *
     * @see com.novell.ldap.LDAPException#LDAPException(String, int)
     */
    public LDAPException(String message, int resultCode)
    {
        super(message);
        exception = new com.novell.ldap.LDAPException( message, resultCode);
        return;
    }

    /**
     * Constructs an exception with with a detail message String, the
     * result code, and the root exception.
     *
     * @see com.novell.ldap.LDAPException#LDAPException(String, int, Throwable)
     */
    public LDAPException(
                String message, int resultCode, Throwable rootException)
    {
        super(message);
        exception = new com.novell.ldap.LDAPException(
                        message,resultCode,rootException);
        return;
    }

    /**
     * Constructs an exception with with a detail message String, result code,
     * and a matchedDN returned from the server.
     *
     * @see com.novell.ldap.LDAPException#LDAPException(String, int, String)
     */
    public LDAPException( String message, int resultCode, String matchedDN)
    {
        super(message);
        exception = new com.novell.ldap.LDAPException(
                message, resultCode, matchedDN);
        return;
    }

    /**
     * gets the com.novell.ldap.LDAPException object
     */
    /* package */
    com.novell.ldap.LDAPException getWrappedObject()
    {
        return exception;
    }

    /**
     * Returns a string representing the internal error code, in the default
     * locale.
     *
     * @see com.novell.ldap.LDAPException#errorCodeToString()
     */
    public String errorCodeToString()
    {
        return exception.errorCodeToString();
    }

    /**
     * Returns a string representing an arbitrary error code, in the default
     * locale, or null if there is no such code.
     *
     * @see com.novell.ldap.LDAPException#errorCodeToString(int)
     */
    public static String errorCodeToString( int code )
    {
        return com.novell.ldap.LDAPException.errorCodeToString( code);
    }

    /**
     * Returns a string representing the internal error code, in the
     * specified locale, or null if a string representation is not available
     * for the requested locale.
     *
     * @see com.novell.ldap.LDAPException#errorCodeToString(Locale)
     */
    public String errorCodeToString( Locale locale )
    {
        return exception.errorCodeToString( locale);
    }

    /**
     * Returns a string representing an arbitrary error code, in the
     * specified locale, or null if there is no such code or if a string
     * representation is not available for the requested Locale.
     *
     * @see com.novell.ldap.LDAPException#errorCodeToString(int, Locale)
     */
    public static String errorCodeToString( int code, Locale locale )
    {
        return com.novell.ldap.LDAPException.errorCodeToString( code, locale);
    }

    /**
     * Returns the error message from the LDAP server, if this message is
     * available (that is, if this message was set).
     *
     * @see com.novell.ldap.LDAPException#errorCodeToString(int, Locale)
     */
    public String getLDAPErrorMessage()
    {
        return exception.getLDAPErrorMessage( );
    }

    /**
     * Returns the lower level Exception which caused the failure, if any.
     *
     * @see com.novell.ldap.LDAPException#getCause()
     */
    public Throwable getCause()
    {
        return exception.getCause( );
    }

    /**
     * Returns the result code from the exception.
     *
     * @see com.novell.ldap.LDAPException#getLDAPResultCode()
     */
    public int getLDAPResultCode()
    {
        return exception.getLDAPResultCode( );
    }

    /**
     * Returns the part of a submitted distinguished name which could be
     * matched by the server.
     *
     * @see com.novell.ldap.LDAPException#getMatchedDN()
     */
    public String getMatchedDN()
    {
        return exception.getMatchedDN( );
    }

    /**
     * Converts the integer error value to a string, in the default locale.
     *
     * @return the String value of the current error
     */
    public String toString()
    {
        return exception.toString( );
    }

    public void printStackTrace()
    {
        exception.printStackTrace();
        return;
    }

    public void printStackTrace(java.io.PrintStream stream)
    {
        exception.printStackTrace(stream);
        return;
    }

    public void printStackTrace(java.io.PrintWriter writer)
    {
        exception.printStackTrace(writer);
        return;
    }

    public Throwable fillInStackTrace()
    {
        if( exception == null) {
            // Called during class initialization
            return super.fillInStackTrace();
        } else {
            return exception.fillInStackTrace();
        }
    }

    public String getLocalizedMessage()
    {
        return exception.getLocalizedMessage();
    }

    public String getMessage()
    {
        return exception.getMessage();
    }
}
