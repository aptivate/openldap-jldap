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
package com.novell.sasl.client;

import com.novell.security.sasl.*;
import java.util.*;

/**
 * Implements a ClientFactory class for all the saslClients in this package
 */
public class ClientFactory extends Object implements SaslClientFactory
{
    public ClientFactory()
    {
    }

    /**
     * Creates a SaslClient using the parameters supplied
     *
     * @param mechanisms  The non-null list of mechanism names to try. Each is
     *                    the IANA-registered name of a SASL mechanism (e.g. "GSSAPI", "CRAM-MD5")
     *
     * @param authorizationId  The possibly null protocol-dependent
     *                     identification to be used for authorization. If
     *                     null or empty, the server derives an authorization
     *                     ID from the client's authentication credentials.
     *                     When the SASL authentication completes
     *                     successfully, the specified entity is granted
     *                     access.
     *
     * @param protocol     The non-null string name of the protocol for which
     *                     the authentication is being performed (e.g. "ldap")
     *
     * @param serverName   The non-null fully qualified host name of the server
     *                     to authenticate to
     *
     * @param props        The possibly null set of properties used to select
     *                     the SASL mechanism and to configure the
     *                     authentication exchange of the selected mechanism.
     *                     See the Sasl class for a list of standard properties.
     *                     Other, possibly mechanism-specific, properties can
     *                     be included. Properties not relevant to the selected
     *                     mechanism are ignored.
     *
     * @param cbh          The possibly null callback handler to used by the
     *                     SASL mechanisms to get further information from the
     *                     application/library to complete the authentication.
     *                     For example, a SASL mechanism might require the
     *                     authentication ID, password and realm from the
     *                     caller. The authentication ID is requested by using
     *                     a NameCallback. The password is requested by using
     *                     a PasswordCallback. The realm is requested by using
     *                     a RealmChoiceCallback if there is a list of realms
     *                     to choose from, and by using a RealmCallback if the
     *                     realm must be entered.
     *
     * @return            A possibly null SaslClient created using the
     *                     parameters supplied. If null, this factory cannot
     *                     produce a SaslClient using the parameters supplied.
     *
     * @exception SaslException  If a SaslClient instance cannot be created
     *                     because of an error
     */
    public SaslClient createSaslClient(
        String[] mechanisms,
        String authorizationId,
        String protocol,
        String serverName,
        Map props,
        javax.security.auth.callback.CallbackHandler cbh)
    throws SaslException
    {
        SaslClient  client=null;
        int         i;

        if (props == null)
            props = new HashMap();

        if (props.get(Sasl.QOP) == null)
            props.put(Sasl.QOP, "auth");

        if (props.get(Sasl.STRENGTH) == null)
            props.put(Sasl.STRENGTH, "high,medium,low");

        if (props.get(Sasl.SERVER_AUTH) == null)
            props.put(Sasl.SERVER_AUTH, "false");

        for (i=0, client=null; (i<mechanisms.length) && (client==null); i++)
        {
            if ("DIGEST-MD5".equals(mechanisms[i]))
            {
                client = DigestMD5SaslClient.getClient(authorizationId,
                                                       protocol,
                                                       serverName, props,
                                                       cbh);
            }
            else if ("EXTERNAL".equals(mechanisms[i]))
            {
                client = ExternalSaslClient.getClient(authorizationId,
                                                      protocol,
                                                      serverName, props,
                                                      cbh);
            }
 
        }

        return client;
    }

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
     *
     *   QOP ("com.novell.security.sasl.qop")
     *
     *              A comma-separated, ordered list of quality-of-protection
     *              values that the client or server is willing to support. A
     *              qop value is one of
     *
     *              "auth"           authentication only
     *
     *              "auth-int"       authentication plus integrity protection
     *
     *              "auth-conf"      authentication plus integrity and
     *                               confidentiality protection
     *
     *
     *              The order of the list specifies the preference order of
     *              the client or server. If this property is absent, the
     *              default qop is "auth".
     *
     *   STRENGTH ("com.novell.security.sasl.strength")
     *
     *              A comma-separated, ordered list of cipher strength values
     *              that the client or server is willing to support. A
     *              strength value is one of
     *
     *              "low"
     *
     *              "medium"
     *
     *              "high"
     *
     *              The order of the list specifies the preference order of
     *              the client or server. An implementation SHOULD allow
     *              configuration of the meaning of these values.
     *
     *              An application MAY use the Java Cryptography Extension
     *              (JCE) with JCE-aware mechanisms to control the selection
     *              of cipher suites that match the strength values.
     *
     *              If this property is absent, the default strength is
     *              "high,medium,low".
     *
     *   SERVER_AUTH ("com.novell.security.sasl.server.authentication")
     *
     *              "true" if server must authenticate to client; default
     *              "false"
     *
     *   MAX_BUFFER ("com.novell.security.sasl.maxbuffer")
     *
     *              Maximum size of receive buffer in bytes of
     *              SaslClient/SaslServer; the default is defined by the
     *              mechanism. The property value is the string
     *              representation of an integer.
     *
     *   CLIENT_PKGS ("com.novell.security.sasl.client.pkgs")
     *
     *              A |-separated list of package names to use when locating
     *              a SaslClientFactory. Each package MUST contain a class
     *              named ClientFactory that implements the SaslClientFactory
     *              interface.
     *
     *   SERVER_PKGS ("com.novell.security.sasl.server.pkgs")
     *
     *              A |-separated list of package names to use when locating
     *              a SaslServerFactory. Each package MUST contain a class
     *              named ServerFactory that implements the SaslServerFactory
     *              interface.
     *
     *   RAW_SEND_SIZE ("com.novell.security.sasl.rawsendsize")
     *
     *              Maximum size of the raw send buffer in bytes of
     *              SaslClient/SaslServer. The property value is the string
     *              representation of an integer and is negotiated between
     *              the client and server during the authentication exchange.
     *
     *   The following properties are for defining a security policy for a
     *   server or client. Absence of the property is interpreted as "false".
     *
     *   POLICY_NOPLAINTEXT ("com.novell.security.sasl.policy.noplaintext")
     *
     *              "true"           if mechanisms susceptible to simple
     *                               plain passive attacks (e.g. "PLAIN") are
     *                               not permitted
     *
     *              "false"          if such mechanisms are permitted
     *
     *   POLICY_NOACTIVE ("com.novell.security.sasl.policy.noactive")
     *
     *              "true"           if mechanisms susceptible to active
     *                               (non-dictionary) attacks are not
     *                               permitted
     *
     *              "false"          if such mechanisms are permitted.
     *
     *   POLICY_NODICTIONARY ("com.novell.security.sasl.policy.nodictionary")
     *
     *              "true"           if mechanisms susceptible to passive
     *                               dictionary attacks are not permitted
     *
     *              "false"          if such mechanisms are permitted
     *
     *   POLICY_NOANONYMOUS ("com.novell.security.sasl.policy.noanonymous")
     *
     *              "true"           if mechanisms that accept anonymous
     *                               login are not permitted
     *
     *              "false"          if such mechanisms are permitted
     *
     *   POLICY_FORWARD_SECRECY ("com.novell.security.sasl.policy.forward")
     *
     *   Forward secrecy means that breaking into one session will not
     *   automatically provide information for breaking into future sessions.
     *
     *              "true"           if mechanisms that implement forward
     *                               secrecy between sessions are required
     *
     *              "false"          if such mechanisms are not required
     *
     *   POLICY_PASS_CREDENTIALS ("com.novell.security.sasl.policy.credentials")
     *
     *              "true"           if mechanisms that pass client
     *                               credentials are required
     *
     *              "false"          if such mechanisms are not required
     *
     * @return       A non-null array containing IANA-registered SASL mechanism
     *               names
     */

    public String[] getMechanismNames(
        Map props)
    {
        String[] mechanisms = {"DIGEST-MD5","EXTERNAL"};
        return mechanisms;
    }

}

