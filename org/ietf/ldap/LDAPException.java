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

package org.ietf.ldap;

import java.util.Locale;
/**
 *  Thrown to indicate that an LDAP error has occurred. This is a general
 *  exception which includes an error message and an LDAP error code.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPException.html">
            com.novell.ldap.LDAPException</a>
 */

public class LDAPException extends Exception
{
    private com.novell.ldap.LDAPException exception;

    /**
     *Indicates the requested client operation completed successfully.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#SUCCESS">
            com.novell.ldap.LDAPException.SUCCESS</a>
     */
    public final static int SUCCESS = 
			    com.novell.ldap.LDAPException.SUCCESS;

    /**
     * Indicates an internal error.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#OPERATIONS_ERROR">
            com.novell.ldap.LDAPException.OPERATIONS_ERROR</a>
     */
    public final static int OPERATIONS_ERROR = 
			    com.novell.ldap.LDAPException.OPERATIONS_ERROR;

    /**
     * Indicates that the server has received an invalid or malformed request
     * from the client.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#PROTOCOL_ERROR">
            com.novell.ldap.LDAPException.PROTOCOL_ERROR</a>
     */
    public final static int PROTOCOL_ERROR = 
			    com.novell.ldap.LDAPException.PROTOCOL_ERROR;

    /**
     * Indicates that the operation's time limit specified by either the
     * client or the server has been exceeded.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#TIME_LIMIT_EXCEEDED">
            com.novell.ldap.LDAPException.TIME_LIMIT_EXCEEDED</a>
     */
    public final static int TIME_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.TIME_LIMIT_EXCEEDED;

    /**
     * Indicates that in a search operation, the size limit specified by
     * the client or the server has been exceeded.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#SIZE_LIMIT_EXCEEDED">
            com.novell.ldap.LDAPException.SIZE_LIMIT_EXCEEDED</a>
     */
    public final static int SIZE_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.SIZE_LIMIT_EXCEEDED;

    /**
     * Does not indicate an error condition. Indicates that the results of
     * a compare operation are false.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#COMPARE_FALSE">com.novell.ldap.LDAPException.COMPARE_FALSE</a>
     */
    public final static int COMPARE_FALSE = 
			    com.novell.ldap.LDAPException.COMPARE_FALSE;

    /**
     * Does not indicate an error condition. Indicates that the results of a
     * compare operation are true.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#COMPARE_TRUE">com.novell.ldap.LDAPException.COMPARE_TRUE</a>
     */
    public final static int COMPARE_TRUE = 
			    com.novell.ldap.LDAPException.COMPARE_TRUE;

    /**
     * Indicates that during a bind operation the client requested an
     * authentication method not supported by the LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#AUTH_METHOD_NOT_SUPPORTED">
            com.novell.ldap.LDAPException.AUTH_METHOD_NOT_SUPPORTED</a>
     */
    public final static int AUTH_METHOD_NOT_SUPPORTED = 
			    com.novell.ldap.LDAPException.AUTH_METHOD_NOT_SUPPORTED;

    /**
     *Indicates a problem with the level of authentication.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#STRONG_AUTH_REQUIRED">
            com.novell.ldap.LDAPException.STRONG_AUTH_REQUIRED</a>
     */
    public final static int STRONG_AUTH_REQUIRED = 
			    com.novell.ldap.LDAPException.STRONG_AUTH_REQUIRED;

    /**
     * Does not indicate an error condition. In LDAPv3, indicates that the
     * server does not hold the target entry of the request, but that the
     * servers in the referral field may.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#REFERRAL">com.novell.ldap.LDAPException.REFERRAL</a>
     */
    public final static int REFERRAL = 
			    com.novell.ldap.LDAPException.REFERRAL;

    /**
     * Indicates that an LDAP server limit set by an administrative authority
     * has been exceeded.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#ADMIN_LIMIT_EXCEEDED">
            com.novell.ldap.LDAPException.ADMIN_LIMIT_EXCEEDED</a>
     */
    public final static int ADMIN_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.ADMIN_LIMIT_EXCEEDED;

    /**
     * Indicates that the LDAP server was unable to satisfy a request because
     * one or more critical extensions were not available.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#UNAVAILABLE_CRITICAL_EXTENSION">
            com.novell.ldap.LDAPException.UNAVAILABLE_CRITICAL_EXTENSION</a>
     */
    public final static int UNAVAILABLE_CRITICAL_EXTENSION = 
			    com.novell.ldap.LDAPException.UNAVAILABLE_CRITICAL_EXTENSION;

    /**
     * Indicates that the session is not protected by a protocol such as
     * Transport Layer Security (TLS), which provides session confidentiality.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#CONFIDENTIALITY_REQUIRED">
            com.novell.ldap.LDAPException.CONFIDENTIALITY_REQUIRED</a>
     */
    public final static int CONFIDENTIALITY_REQUIRED = 
			    com.novell.ldap.LDAPException.CONFIDENTIALITY_REQUIRED;

    /**
     * Does not indicate an error condition, but indicates that the server is
     * ready for the next step in the process.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#SASL_BIND_IN_PROGRESS">
            com.novell.ldap.LDAPException.SASL_BIND_IN_PROGRESS</a>
     */
    public final static int SASL_BIND_IN_PROGRESS = 
			    com.novell.ldap.LDAPException.SASL_BIND_IN_PROGRESS;

    /**
     * Indicates that the attribute specified in the modify or compare
     * operation does not exist in the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#NO_SUCH_ATTRIBUTE">
            com.novell.ldap.LDAPException.NO_SUCH_ATTRIBUTE</a>
     */
    public final static int NO_SUCH_ATTRIBUTE = 
			    com.novell.ldap.LDAPException.NO_SUCH_ATTRIBUTE;

    /**
     * Indicates that the attribute specified in the modify or add operation
     * does not exist in the LDAP server's schema.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#UNDEFINED_ATTRIBUTE_TYPE">
            com.novell.ldap.LDAPException.UNDEFINED_ATTRIBUTE_TYPE</a>
     */
    public final static int UNDEFINED_ATTRIBUTE_TYPE = 
			    com.novell.ldap.LDAPException.UNDEFINED_ATTRIBUTE_TYPE;

    /**
     * Indicates that the matching rule specified in the search filter does
     * not match a rule defined for the attribute's syntax.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#INAPPROPRIATE_MATCHING">
            com.novell.ldap.LDAPException.INAPPROPRIATE_MATCHING</a>
     */
    public final static int INAPPROPRIATE_MATCHING = 
			    com.novell.ldap.LDAPException.INAPPROPRIATE_MATCHING;

    /**
     * Indicates that the attribute value specified in a modify, add, or
     * modify DN operation violates constraints placed on the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#CONSTRAINT_VIOLATION">
            com.novell.ldap.LDAPException.CONSTRAINT_VIOLATION</a>
     */
    public final static int CONSTRAINT_VIOLATION = 
			    com.novell.ldap.LDAPException.CONSTRAINT_VIOLATION;

    /**
     * Indicates that the attribute value specified in a modify or add
     * operation already exists as a value for that attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#ATTRIBUTE_OR_VALUE_EXISTS">
            com.novell.ldap.LDAPException.ATTRIBUTE_OR_VALUE_EXISTS</a>
     */
    public final static int ATTRIBUTE_OR_VALUE_EXISTS = 
			    com.novell.ldap.LDAPException.ATTRIBUTE_OR_VALUE_EXISTS;

    /**
     * Indicates that the attribute value specified in an add, compare, or
     * modify operation is an unrecognized or invalid syntax for the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#INVALID_ATTRIBUTE_SYNTAX">
            com.novell.ldap.LDAPException.INVALID_ATTRIBUTE_SYNTAX</a>
     */
    public final static int INVALID_ATTRIBUTE_SYNTAX = 
			    com.novell.ldap.LDAPException.INVALID_ATTRIBUTE_SYNTAX;

    /**
     * Indicates the target object cannot be found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#NO_SUCH_OBJECT">
            com.novell.ldap.LDAPException.NO_SUCH_OBJECT</a>
     */
    public final static int NO_SUCH_OBJECT = 
			    com.novell.ldap.LDAPException.NO_SUCH_OBJECT;

    /**
     * Indicates that an error occurred when an alias was dereferenced.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#ALIAS_PROBLEM">
            com.novell.ldap.LDAPException.ALIAS_PROBLEM</a>
     */
    public final static int ALIAS_PROBLEM = 
			    com.novell.ldap.LDAPException.ALIAS_PROBLEM;

    /**
     * Indicates that the syntax of the DN is incorrect.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#INVALID_DN_SYNTAX">
            com.novell.ldap.LDAPException.INVALID_DN_SYNTAX</a>
     */
    public final static int INVALID_DN_SYNTAX = 
			    com.novell.ldap.LDAPException.INVALID_DN_SYNTAX;

    /**
     * Indicates that the specified operation cannot be performed on a
     * leaf entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#IS_LEAF">
            com.novell.ldap.LDAPException.IS_LEAF</a>
     */
    public final static int IS_LEAF = 
			    com.novell.ldap.LDAPException.IS_LEAF;

    /**
     * Indicates that during a search operation, either the client does not
     * have access rights to read the aliased object's name or dereferencing
     * is not allowed.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#ALIAS_DEREFERENCING_PROBLEM">
            com.novell.ldap.LDAPException.ALIAS_DEREFERENCING_PROBLEM</a>
     */
    public final static int ALIAS_DEREFERENCING_PROBLEM = 
			    com.novell.ldap.LDAPException.ALIAS_DEREFERENCING_PROBLEM;

    /**
     * Indicates that during a bind operation, the client is attempting to use
     * an authentication method that the client cannot use correctly.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#INAPPROPRIATE_AUTHENTICATION">
            com.novell.ldap.LDAPException.INAPPROPRIATE_AUTHENTICATION</a>
     */
    public final static int INAPPROPRIATE_AUTHENTICATION = 
			    com.novell.ldap.LDAPException.INAPPROPRIATE_AUTHENTICATION;

    /**
     * Indicates that invalid information was passed during a bind operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#INVALID_CREDENTIALS">
            com.novell.ldap.LDAPException.INVALID_CREDENTIALS</a>
     */
    public final static int INVALID_CREDENTIALS = 
			    com.novell.ldap.LDAPException.INVALID_CREDENTIALS;

    /**
     * Indicates that the caller does not have sufficient rights to perform
     * the requested operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#INSUFFICIENT_ACCESS_RIGHTS">
            com.novell.ldap.LDAPException.INSUFFICIENT_ACCESS_RIGHTS</a>
     */
    public final static int INSUFFICIENT_ACCESS_RIGHTS = 
			    com.novell.ldap.LDAPException.INSUFFICIENT_ACCESS_RIGHTS;

    /**
     * Indicates that the LDAP server is too busy to process the client request
     * at this time, but if the client waits and resubmits the request, the
     * server may be able to process it then.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#BUSY">
            com.novell.ldap.LDAPException.BUSY</a>
     */
    public final static int BUSY = 
			    com.novell.ldap.LDAPException.BUSY;

    /**
     * Indicates that the LDAP server cannot process the client's bind
     * request, usually because it is shutting down.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#UNAVAILABLE">
            com.novell.ldap.LDAPException.UNAVAILABLE</a>
     */
    public final static int UNAVAILABLE = 
			    com.novell.ldap.LDAPException.UNAVAILABLE;

    /**
     * Indicates that the LDAP server cannot process the request because of
     * server-defined restrictions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#UNWILLING_TO_PERFORM">
            com.novell.ldap.LDAPException.UNWILLING_TO_PERFORM</a>
     */
    public final static int UNWILLING_TO_PERFORM = 
			    com.novell.ldap.LDAPException.UNWILLING_TO_PERFORM;

    /**
     * Indicates that the client discovered an alias or referral loop,
     * and is thus unable to complete this request.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LOOP_DETECT">
            com.novell.ldap.LDAPException.LOOP_DETECT</a>
     */
    public final static int LOOP_DETECT = 
			    com.novell.ldap.LDAPException.LOOP_DETECT;

    /**
     * Indicates that the add or modify DN operation violates the schema's
     * structure rules.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#NAMING_VIOLATION">
            com.novell.ldap.LDAPException.NAMING_VIOLATION</a>
     */
    public final static int NAMING_VIOLATION = 
			    com.novell.ldap.LDAPException.NAMING_VIOLATION;

    /**
     * Indicates that the add, modify, or modify DN operation violates the
     * object class rules for the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#OBJECT_CLASS_VIOLATION">
            com.novell.ldap.LDAPException.OBJECT_CLASS_VIOLATION</a>
     */
    public final static int OBJECT_CLASS_VIOLATION = 
			    com.novell.ldap.LDAPException.OBJECT_CLASS_VIOLATION;

    /**
     * Indicates that the requested operation is permitted only on leaf entries.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#NOT_ALLOWED_ON_NONLEAF">
            com.novell.ldap.LDAPException.NOT_ALLOWED_ON_NONLEAF</a>
     */
    public final static int NOT_ALLOWED_ON_NONLEAF = 
			    com.novell.ldap.LDAPException.NOT_ALLOWED_ON_NONLEAF;

    /**
     * Indicates that the modify operation attempted to remove an attribute
     * value that forms the entry's relative distinguished name.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#NOT_ALLOWED_ON_RDN">
            com.novell.ldap.LDAPException.NOT_ALLOWED_ON_RDN</a>
     */
    public final static int NOT_ALLOWED_ON_RDN = 
			    com.novell.ldap.LDAPException.NOT_ALLOWED_ON_RDN;

    /**
     * Indicates that the add operation attempted to add an entry that already
     * exists, or that the modify operation attempted to rename an entry to the
     * name of an entry that already exists.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#ENTRY_ALREADY_EXISTS">
            com.novell.ldap.LDAPException.ENTRY_ALREADY_EXISTS</a>
     */
    public final static int ENTRY_ALREADY_EXISTS = 
			    com.novell.ldap.LDAPException.ENTRY_ALREADY_EXISTS;

    /**
     * Indicates that the modify operation attempted to modify the structure
     * rules of an object class.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#OBJECT_CLASS_MODS_PROHIBITED">
            com.novell.ldap.LDAPException.OBJECT_CLASS_MODS_PROHIBITED</a>
     */
    public final static int OBJECT_CLASS_MODS_PROHIBITED = 
			    com.novell.ldap.LDAPException.OBJECT_CLASS_MODS_PROHIBITED;

    /**
     * Indicates that the modify DN operation moves the entry from one LDAP
     * server to another and thus requires more than one LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#AFFECTS_MULTIPLE_DSAS">
            com.novell.ldap.LDAPException.AFFECTS_MULTIPLE_DSAS</a>
     */
    public final static int AFFECTS_MULTIPLE_DSAS = 
			    com.novell.ldap.LDAPException.AFFECTS_MULTIPLE_DSAS;

    /**
     * Indicates an unknown error condition.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#OTHER">
            com.novell.ldap.LDAPException.OTHER</a>
     */
    public final static int OTHER = 
			    com.novell.ldap.LDAPException.OTHER;

    /**
     * Indicates that the LDAP libraries cannot establish an initial connection
     * with the LDAP server. Either the LDAP server is down or the specified
     * host name or port number is incorrect.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#SERVER_DOWN">
            com.novell.ldap.LDAPException.SERVER_DOWN</a>
     */
    public final static int SERVER_DOWN = 
			    com.novell.ldap.LDAPException.SERVER_DOWN;

    /**
     * Indicates that the LDAP client has an error. This is usually a failed
     * dynamic memory allocation error.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LOCAL_ERROR">
            com.novell.ldap.LDAPException.LOCAL_ERROR</a>
     */
    public final static int LOCAL_ERROR = 
			    com.novell.ldap.LDAPException.LOCAL_ERROR;

    /**
     * Indicates that the LDAP client encountered errors when encoding an
     * LDAP request intended for the LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#ENCODING_ERROR">
            com.novell.ldap.LDAPException.ENCODING_ERROR</a>
     */
    public final static int ENCODING_ERROR = 
			    com.novell.ldap.LDAPException.ENCODING_ERROR;

    /**
     * Indicates that the LDAP client encountered errors when decoding an
     * LDAP response from the LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#DECODING_ERROR">
            com.novell.ldap.LDAPException.DECODING_ERROR</a>
     */
    public final static int DECODING_ERROR = 
			    com.novell.ldap.LDAPException.DECODING_ERROR;

    /**
     * Indicates that the time limit of the LDAP client was exceeded while
     * waiting for a result.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LDAP_TIMEOUT">
            com.novell.ldap.LDAPException.LDAP_TIMEOUT</a>
     */
    public final static int LDAP_TIMEOUT = 
			    com.novell.ldap.LDAPException.LDAP_TIMEOUT;

    /**
     * Indicates that a bind method was called with an unknown
     * authentication method.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#AUTH_UNKNOWN">
            com.novell.ldap.LDAPException.AUTH_UNKNOWN</a>
     */
    public final static int AUTH_UNKNOWN = 
			    com.novell.ldap.LDAPException.AUTH_UNKNOWN;

    /**
     * Indicates that the search method was called with an invalid
     * search filter.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#FILTER_ERROR">
            com.novell.ldap.LDAPException.FILTER_ERROR</a>
     */
    public final static int FILTER_ERROR = 
			    com.novell.ldap.LDAPException.FILTER_ERROR;

    /**
     * Indicates that the user cancelled the LDAP operation.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#USER_CANCELLED">
            com.novell.ldap.LDAPException.USER_CANCELLED</a>
     */
    public final static int USER_CANCELLED = 
			    com.novell.ldap.LDAPException.USER_CANCELLED;

    /**
     * Indicates that a dynamic memory allocation method failed when calling
     * an LDAP method.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#NO_MEMORY">
            com.novell.ldap.LDAPException.NO_MEMORY</a>
     */
    public final static int NO_MEMORY = 
			    com.novell.ldap.LDAPException.NO_MEMORY;

    /**
     * Indicates that the LDAP client has lost either its connection or
     * cannot establish a connection to the LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#CONNECT_ERROR">
            com.novell.ldap.LDAPException.CONNECT_ERROR</a>
     */
    public final static int CONNECT_ERROR = 
			    com.novell.ldap.LDAPException.CONNECT_ERROR;

    /**
     * Indicates that the requested functionality is not supported by the
     * client.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LDAP_NOT_SUPPORTED">
            com.novell.ldap.LDAPException.LDAP_NOT_SUPPORTED</a>
     */
    public final static int LDAP_NOT_SUPPORTED = 
			    com.novell.ldap.LDAPException.LDAP_NOT_SUPPORTED;

    /**
     * Indicates that the client requested a control that the libraries
     * cannot find in the list of supported controls sent by the LDAP server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#CONTROL_NOT_FOUND">
            com.novell.ldap.LDAPException.CONTROL_NOT_FOUND</a>
     */
    public final static int CONTROL_NOT_FOUND = 
			    com.novell.ldap.LDAPException.CONTROL_NOT_FOUND;

    /**
     * Indicates that the LDAP server sent no results.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#NO_RESULTS_RETURNED">
            com.novell.ldap.LDAPException.NO_RESULTS_RETURNED</a>
     */
    public final static int NO_RESULTS_RETURNED = 
			    com.novell.ldap.LDAPException.NO_RESULTS_RETURNED;

    /**
     * Indicates that more results are chained in the result message.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#MORE_RESULTS_TO_RETURN">
            com.novell.ldap.LDAPException.MORE_RESULTS_TO_RETURN</a>
     */
    public final static int MORE_RESULTS_TO_RETURN = 
			    com.novell.ldap.LDAPException.MORE_RESULTS_TO_RETURN;

    /**
     * Indicates the LDAP libraries detected a loop. Usually this happens
     * when following referrals.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#CLIENT_LOOP">
            com.novell.ldap.LDAPException.CLIENT_LOOP</a>
     */
    public final static int CLIENT_LOOP = 
			    com.novell.ldap.LDAPException.CLIENT_LOOP;

    /**
     * Indicates that the referral exceeds the hop limit. The default hop
     * limit is ten.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#REFERRAL_LIMIT_EXCEEDED">
            com.novell.ldap.LDAPException.REFERRAL_LIMIT_EXCEEDED</a>
     */
    public final static int REFERRAL_LIMIT_EXCEEDED = 
			    com.novell.ldap.LDAPException.REFERRAL_LIMIT_EXCEEDED;

    /**
     * Indicates that the server response to a request is invalid
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#INVALID_RESPONSE">
            com.novell.ldap.LDAPException.INVALID_RESPONSE</a>
     */
   public final static int INVALID_RESPONSE = 
			    com.novell.ldap.LDAPException.INVALID_RESPONSE;

    /**
     * Indicates that the server response to a request is ambiguous
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#AMBIGUOUS_RESPONSE">
            com.novell.ldap.LDAPException.AMBIGUOUS_RESPONSE</a>
     */
   public final static int AMBIGUOUS_RESPONSE = 
			    com.novell.ldap.LDAPException.AMBIGUOUS_RESPONSE;

    /**
     * Indicates that TLS is not supported on the server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#TLS_NOT_SUPPORTED">
            com.novell.ldap.LDAPException.TLS_NOT_SUPPORTED</a>
     */
    public final static int TLS_NOT_SUPPORTED = 
			    com.novell.ldap.LDAPException.TLS_NOT_SUPPORTED;

    /**
     * Constructs a default exception with no specific error information.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LDAPException()">
            com.novell.ldap.LDAPException.LDAPException()</a>
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
     * Constructs an exception with a detailed message 
     * String and the result code.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LDAPException(java.lang.String, int, java.lang.String)">
            com.novell.ldap.LDAPException.LDAPException(String, int, String)</a>
     */
    public LDAPException( String message,
                          int resultCode,
                          String serverMessage)
    {
        super(message);
        exception = new com.novell.ldap.LDAPException( message,
                                                       resultCode, 
                                                       serverMessage);
        return;
    }

    /**
     * Constructs an exception with a detailed message String, the
     * result code, and the root exception.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LDAPException(java.lang.String, int, java.lang.String, java.lang.Throwable)">
            com.novell.ldap.LDAPException.LDAPException(String, int, String,
            Throwable)</a>
     */
    public LDAPException( String message,
                          int resultCode,
                          String serverMessage,
                          Throwable rootException)
    {
        super(message);
        exception = new com.novell.ldap.LDAPException(
                        message,resultCode,serverMessage,rootException);
        return;
    }

    /**
     * Constructs an exception with a detailed message String, result code,
     * and a matchedDN returned from the server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#LDAPException(java.lang.String, int, java.lang.String, java.lang.String)">
            com.novell.ldap.LDAPException.LDAPException(String, int, String,
            String)</a>
     */
    public LDAPException( String message,
                          int resultCode,
                          String serverMessage,
                          String matchedDN)
    {
        super(message);
        exception = new com.novell.ldap.LDAPException(
                message, resultCode, serverMessage, matchedDN);
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
     * Returns the lower level Exception which caused the failure, if any.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#getCause()">
            com.novell.ldap.LDAPException.getCause()</a>
     */
    public Throwable getCause()
    {
        return exception.getCause( );
    }

    /**
     * Returns the error message from the LDAP server, if this message is
     * available (that is, if this message was set).
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#getLDAPErrorMessage()">
            com.novell.ldap.LDAPException.getLDAPErrorMessage()</a>
     */
    public String getLDAPErrorMessage()
    {
        return exception.getLDAPErrorMessage( );
    }

    /**
     * Returns the result code from the exception.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#getResultCode()">
            com.novell.ldap.LDAPException.getResultCode()</a>
     */
    public int getResultCode()
    {
        return exception.getResultCode( );
    }

    /**
     * Returns the part of a submitted distinguished name which could be
     * matched by the server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#getMatchedDN()">
            com.novell.ldap.LDAPException.getMatchedDN()</a>
     */
    public String getMatchedDN()
    {
        return exception.getMatchedDN( );
    }
    /**
     * Returns a string representing the result code in the default
     * locale.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#resultCodeToString()">
            com.novell.ldap.LDAPException.resultCodeToString()</a>
     */
    public String resultCodeToString()
    {
        return exception.resultCodeToString();
    }

    /**
     * Returns a string representing the specified result code in the default
     * locale.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#resultCodeToString(int)">
            com.novell.ldap.LDAPException.resultCodeToString(int)</a>
     */
    public static String resultCodeToString( int code )
    {
        return com.novell.ldap.LDAPException.resultCodeToString( code);
    }

    /**
     * Returns a string representing the result code in the
     * specified locale.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#resultCodeToString(java.util.Locale)">
            com.novell.ldap.LDAPException.resultCodeToString(Locale)</a>
     */
    public String resultCodeToString( Locale locale )
    {
        return exception.resultCodeToString( locale);
    }

    /**
     * Returns a string representing the specified result code in the
     * specified locale.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#resultCodeToString(int, java.util.Locale)">
            com.novell.ldap.LDAPException.resultCodeToString(int, Locale)</a>
     */
    public static String resultCodeToString( int code, Locale locale )
    {
        return com.novell.ldap.LDAPException.resultCodeToString( code, locale);
    }


    /**
     * Converts the integer error value to a string, in the default locale.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPException.html#toString()">
            com.novell.ldap.LDAPException.toString()</a>
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
