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

import java.util.Map;

/**
 * An object implementing this interface can provide a SaslClient.
 * <p> The implementation must be thread-safe and handle multiple
 * simultaneous requests.It must also have a public constructor that
 * accepts no argument. 
 */
    
public interface SaslClientFactory
{
    /**
     * Creates a SaslClient using the parameters supplied.
     * 
     * @param mechanisms      The non-null list of mechanism names to try. Each
     *                        is the IANA-registered name of a SASL mechanism 
     *                        (e.g. "GSSAPI", "DIGEST-MD5").
     * @param authorizationID The possibly null protocol-dependent
     *                        identification to be used for authorization. If 
     *                        null or empty, the server derives an authorization
     *                        ID from the client's authentication credentials.
     *                        When the SASL authentication complete successfully
     *                        , the specified entity is granted access.
     * @param protocol        The non-null string name of the protocol for which
     *                        the authentication is being performed, e.g "ldap".
     * @param serverName      The non-null fully qualified host name of the 
     *                        server to authenticate to.
     * @param props           The possibly null set of properties used to select
     *                        the SASL mechanism and to configure the 
     *                        authentication exchange of the selected mechanism.
     *                        Other, possibly mechanism-specific, properties can
     *                        be included. Properties not relevant to the 
     *                        selected mechanism are ignored.
     * @param cbh             The possibly null callback handler to be used by 
     *                        the SASL mechanisms to get further information 
     *                        from the application/library to complete the 
     *                        authentication. For example, a SASL mechanism 
     *                        might require the authentication ID, password and
     *                        realm from the caller. The authentication ID is 
     *                        requested by using a NameCallback. The password is
     *                        requested by using a PasswordCallback. The realm
     *                        is requested by using a RealmChoiceCallback if 
     *                        there is a list of realms to choose from, and by
     *                        using a RealmCallback if the realm must be enterd.
     *
     * @return             A possibly null SaslClient created using the
     *                     parameters supplied. If null, this factory cannot
     *                     produce a SaslClient using the parameters supplied.
     *
     * @exception SaslException  If a SaslClient instance cannot be created
     *                     because of an error
     */
    public abstract SaslClient  createSaslClient(String[] mechanisms,
                    String authorizationID,
                    String protocol,
                    String serverName,
                    Map props,
                    javax.security.auth.callback.CallbackHandler cbh)
                    throws SaslException;
    /**
     * Returns an array of names of mechanisms that match the specified
     * mechanism selection policies
     *
     * @param props  The possibly null set of properties used to specify the
     *               security policy of the SASL mechanisms. For example, if
     *               props contains the Sasl.POLICY_NOPLAINTEXT property with
     *               the value "true", then the factory must not return any
     *               SASL mechanisms that are susceptible to simple plain
     *               passive attacks. Non-policy related properties, if
     *               present in props, are ignored.
     * @return       A non-null array containing IANA-registered SASL mechanism
     *               names
     */     
    public abstract String[]    getMechanismNames(Map props);
}
