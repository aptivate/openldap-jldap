/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2003 Novell, Inc. All Rights Reserved.
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

package com.novell.security.sasl;

/**
 * Performs SASL authentication as a client.An Class implementing this
 * interface can negotiate authentication as a client using one of the
 * IANA-registered mechanisms. 
 * @see Sasl
 * @see SaslClientFactory
 */
public interface SaslClient
{

    /**
     * Reports the IANA-registered name of the mechanism used by this
     * client, e.g. "NMAS_LOGIN" or "DIGEST-MD5".
     * @return A non-null string representing the IANA-registered mechanism name.
     */
    public abstract String getMechanismName();

    /**
     * Determines whether this mechanism has an optional initial response.
     * If true, caller should call evaluateChallenge() with an empty array
     * to get the initial response.
     * @return true if this mechanism has an initial response
     */
    public abstract boolean hasInitialResponse();

    /**
     * If a challenge is received from the server during the authentication
     * process, this method is called to prepare an appropriate next
     * response to submit to the server. 
     * @param challenge    The non-null challenge received from the server.
     *                      The challenge array may have zero length. 
     * @return The possibly null reponse to send to the server. It is null
     *            if the challenge accompanied a "SUCCESS" status and the
     *            challenge only contains data for the client to update its
     *            state and no response needs to be sent to the server.
     *            The response is a zero-length byte array if the client is to
     *            send a response with no data.
     * @exception   SaslException If an error occurred while processing the
     *                            challenge or generating a response.
     */
    public abstract byte[] evaluateChallenge(byte[] challenge)
    throws SaslException;

    /**
     * This method may be called at any time to determine if the authentication
     * process is finished. 
     * @return <ul> <li>true  - If the authentication exchange has completed
     *              <li> false -  otherwise</ul>
     */
    public abstract boolean isComplete();
    
    /**
     * Unwraps a byte array received from the server 
     * This method can be called only after the authentication process has
     * completed (i.e., when isComplete() returns true) and only if the 
     * authentication process has negotiated integrity and/or privacy as the
     * quality of protection.
     *
     * @param incoming   A non-null byte array containing the encoded bytes
     *                       from the server.
     * @param offset     The starting position at incoming of the bytes to use
     * @param len        The number of bytes from incoming to use. 
     *
     * @return  The corresponding decoded bytes in a byte array.
     *
     * @exception  SaslException If this method is called before
     *             the authentictaion process has completed.A SaslException
     *             is thrown also if incoming cannot be successfully unwrapped
     */
    public abstract byte[] unwrap(byte[] incoming,int offset, int len)
        throws SaslException;

    /**
     * Wraps a byte array to be sent to the server
     * This method can be called only after the authentication exchange has
     * completed (i.e., when isComplete() returns true) and only if the 
     * authentication exchange has negotiated integrity and/or privacy as the
     * quality of protection.
     *
     * @param outgoing  A non-null byte array containing the bytes to encode.
     * @param offset    The starting position at outgoing of the bytes to use.
     * @param len       The number of bytes from outgoing to use. 
     *
     * @return  The corresponding encoded bytes in a byte array.
     *
     * @exception  SaslException If this method is called before
     *             the authentictaion exchange has completed.A SaslException
     *             is thrown also if outgoing cannot be successfully wrapped.
     */ 
    public abstract byte[] wrap(byte[] outgoing,int offset, int len)
        throws SaslException;

    /**
     * This method Retrieves the negotiated property.
     * <p> This method can be called only after the authentication exchange
     * has completed (i.e., when isComplete() returns true)
     * <p> For example, this method may be used to obtained the negotiated raw
     * send buffer size, quality-of-protection, and cipher strength. 
     * 
     * @param propName     The non-null property name. 
     *
     * @return The value of the negotiated property. If null, the property was
     *          not negotiated or is not applicable to this mechanism.
     * @exception IllegalStateException   if this authentication exchange has
     *                                      not completed
     */
    public abstract Object getNegotiatedProperty(String propName)
                                       throws SaslException;

    /**
     * Disposes of any system resources or security-sensitive information the
     * SaslClient might be using. Invoking this method invalidates the 
     * SaslClient instance. This method is idempotent. 
     * @exception SaslException  If a problem was encountered while disposing
     * of the resources
     */
       public abstract void dispose() throws SaslException;

}

