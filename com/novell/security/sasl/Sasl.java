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

import java.util.Enumeration;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

/**
 *A static class for creating SASL clients  and servers. It transparently locates
 *and uses any available  SaslClientFactory/SaslServerFactory instances.
 *<p>Currently, only the client methods are available.
 */


public class Sasl
{

    private static SaslClientFactory cFactory = null;
    /**
     * The name of the property that specifies the SaslClientFactorys to use.
     *
     * A |-separated list of package names to use when locating a 
     * SaslClientFactory.Each package MUST contain a class named that 
     * implements the SaslClientFactory interface
     *
     * Value of this constant is "com.novell.security.sasl.client.pkgs"
     */
    public static final String CLIENTPKGS = "com.novell.security.sasl.client.pkgs";

    /**
     * The name of a property that specifies the quality-of-protection to use.
     *
     * A comma-separated, ordered list of quality-of-protection values 
     * that the client or server is willing to support A qop valie is one of
     *  <ul>
     *    <li>     auth      -     authentication only
     *    <li>     auth-int  -     authentication plus integrity protection
     *    <li>     auth-conf -     authentication plus integrity and 
     *                               confidentiality protection
     *  </ul>
     * The order of the list specifies the preference order of the client
     * or server.If this property is absent, the  default qop is "auth". 
     *
     * Value of this constant is "com.novell.security.sasl.qop"
     */
    public static final String QOP = "com.novell.security.sasl.qop";

    /**
     * The name of a property that specifies the cipher strength to use.
     *
     * A comma-separated, ordered list of cipher strength values that the
     * client or server is willing to support. A strength value is one of :
     *		<ul>
     *          <li>low
     *          <li>medium
     *          <li>high
     *      </ul>
     * The order of the list specifies the preference order of the client
     * or server. An implementation SHOULD allow configuration of the meaning
     * of these values. 
     *
     * An application MAY use the Java Cryptography Extension (JCE) with
     * JCE-aware mechanisms to control the selection of cipher suites that
     * match the strength values.If this property is absent, the default 
     * strength is "high,medium,low". 
     *
     * Value of this constant is "com.novell.security.sasl.strength"
     */ 
    public static final String STRENGTH = "com.novell.security.sasl.strength";

    /**
     * The name of a property that specifies whether the server must 
     * authenticate to the client.
     *
     * This property should be "true" if server must authenticate to client,
     * default is "false".
     *
     * Value of this constant is "com.novell.security.sasl.authentication"
     */
    public static final String SERVER_AUTH = 
                               "com.novell.security.sasl.server.authentication";

    /**
     * The name of a property that specifies the maximum size of the receive
     * buffer in bytes of SaslClient or SaslServer.
     *
     * The default size is defined by the mechanism.The property value is
     * the string representation of an integer.
     *
     * Value of this constant is "com.novell.security.sasl.maxbuffer"
     */
    public static final String MAX_BUFFER = "com.novell.security.sasl.maxbuffer";

    /**
     * The name of a property that specifies the maximum size of the raw send
     * buffer in bytes of SaslClient/SaslServer.
     *
     * The property value is the string representation of an integer and is
     * negotiated between the client and server during the authentication 
     * exchange. 
     *
     * Value of this constant is "com.novell.security.sasl.rawsendsize"
     */
    public static final String RAW_SEND_SIZE = 
                               "com.novell.security.sasl.rawsendsize";

    /**
     * The name of a property that specifies if mechanisms susceptible to simple
     * plain passive attacks are permitted or not
     *
     * The property can contain following values:
     *			<ul>
     *               <li> true  - If such mechansims are not permitted
     *               <li> false - If such mechanisms are permitted 
     *          </ul>
     * Absence of the property is interpreted as "false".
     *
     * Value of this constant is "com.novell.security.sasl.policy.noplaintext"
     */
    public static final String POLICY_NOPLAINTEXT = 
                               "com.novell.security.sasl.policy.noplaintext";

    /**
     * The name of a property that specifies if mechanisms susceptible to active
     * (non-dictionary) attacks are permitted or not.
     *
     * The property can contain following values:
     *			<ul> 
     *               <li> true  - If such mechansims are not permitted
     *               <li> false - If such mechanisms are permitted 
     *          </ul>
     * Absence of the property is interpreted as "false".
     *
     * Value of this constant is "com.novell.security.sasl.policy.noactive"
     */
    public static final String POLICY_NOACTIVE = 
                               "com.novell.security.sasl.policy.noactive";

    /**
     * The name of a property that specifies if mechanisms susceptible to
     * passive dictionary attacks are permitted or not.
     *
     * The property can contain following values:
     *			<ul> 
     *               <li> true  - If such mechansims are not permitted
     *               <li> false - If such mechanisms are permitted
     *          </ul>
     * Absence of the property is interpreted as "false".
     *
     * Value of this constant is "com.novell.security.sasl.policy.nodictionary"
     */
    public static final String POLICY_NODICTIONARY = 
                               "com.novell.security.sasl.policy.nodictionary";

    /**
     * The name of a property that specifies if mechanisms that accept anonymous
     * login are permitted or not.
     *
     * The property can contain following values:
     *			<ul>
     *               <li> true  - If such mechansims are not permitted
     *               <li> false - If such mechanisms are permitted 
     *          </ul>
     * Absence of the property is interpreted as "false".
     *
     * Value of this constant is "com.novell.security.sasl.policy.noanonymous"
     */
    public static final String POLICY_NOANONYMOUS = 
                               "com.novell.security.sasl.policy.noanonymous";

    /**
     * The name of a property that specifies if mechanisms that implement
     * forward secrecy between sessions are required or not
     *
     * The property can contain following values:
     *			<ul> 
     *               <li> true  - If such mechansims are required
     *               <li> false - If such mechanisms not required
     *          </ul>
     * Absence of the property is interpreted as "false".
     *
     * Value of this constant is "com.novell.security.sasl.policy.forward"
     */
    public static final String POLICY_FORWARD_SECRECY = 
                               "com.novell.security.sasl.policy.forward";

    /**
     * The name of a property that specifies if mechanisms that pass client
     * credentials are required or not
     *
     * The property can contain following values:
     *			<ul> 
     *               <li> true  - If such mechansims are required
     *               <li> false - If such mechanisms are not required
     *          </ul>
     * Absence of the property is interpreted as "false".
     *
     * Value of this constant is "com.novell.security.sasl.policy.credentials"
     */
    public static final String POLICY_PASS_CREDENTIALS = 
                               "com.novell.security.sasl.policy.credentials";
    

     private Sasl() {
     }

    /**
     * Creates a SaslClient using the parameters supplied.
     * The algorithm for selection is as follows: 
     * <ul>
     * <li> If a factory has been installed via setSaslClientFactory(), 
     * invoke createSaslClient() on it. If the method invocation returns 
     * a non-null SaslClient instance, return the SaslClient instance; 
     * otherwise continue. 
     * <li> Create a list of fully qualified class names using the package 
     * names listed in the CLIENTPKGS ("com.novell.security.sasl.client.pkgs")
     * property in props and the class name ClientFactory. Each class name
     * in this list identifies a SaslClientFactory implementation. Starting
     * with the first class on the list, create an instance of SaslClientFactory
     * using the class' public no-argument constructor and invoke
     * createSaslClient() on it. If the method invocation returns a non-null
     * SaslClient instance, return it; otherwise repeat using the next class
     * on the list until a non-null SaslClient is produced or the list is
     * exhausted. 
     * <li> Repeat the previous step using the CLIENTPKGS ("com.novell.security.sasl
     * .client.pkgs") System property instead of the property in props.
     * <li> As per the Java 2 Standard Edition version 1.3 service provider 
     * guidelines, check for the existence of one of more files named 
     * META-INF/services/com.novell.security.sasl.SaslClientFactory in the 
     * classpath and installed JAR files. Each file lists the fully 
     * qualified class names of the factories (i.e. implementations of 
     * SaslClientFactory) found in the JAR files or classpath. Construct 
     * a merged list of class names using these files and repeat Step 2 
     * using this list. If there are more than one of these files, the 
     * order in which they are processed is undefined. If no non-null 
     * SaslClient instance is produced, return null. 
     *
     * @param mechanisms       The non-null list of mechanism names to try.Each
     * is the IANA-registered name of a SASL mechanism. (e.g. "Digest-MD5", 
     * "NMAS_LOGIN").
     *
     * @param authorizationId  The possibly null protocol-dependent 
     * identification to be used for authorization.When the SASL authentication
     * completes successfully,the specified entity is granted access.
     *
     * @param protocol         The non-null string name of the protocol for
     * which the authentication is being performed (e.g., "LDAP").
     *
     * @param serverName       The non-null fully qualified host name of the
     * server to authenticate to.
     *
     * @param props            The possibly null set of properties used to
     * select the SASL mechanisms and  to configure the authentication exchange
     * of teh selected mechanism. For example, "if props includes the
     * Sasl.POLICY_NOPLAINTEXT property with the value "true", then the selected
     * SASL mechanism must not be susceptible to simple plain passive attacks.
     *
     * @param cbh              The possibly null callback handler to used by
     * the SASL mechanisms to get further information from the application/
     * library to complete the authentication. For example, a SASL mechanism
     * might require the authentication ID and password from the caller.The 
     * authentication ID is requested by using a NameCallback. The password is
     * requested by using a PasswordCallback. The realm is requested by using
     * a RealmChoiceCallback if there is a list of realms to choose from, and
     * by using a RealmCallback if the realm must be entered.
     *
     * @return  A non-null SaslClient using teh parameters supplied. It returns
     * null if no SaslClient can be created using the parameters supplied.
     *
     * @exception SaslException if it cannot create a SaslClient because
     * of an error.
     */

     public static SaslClient createSaslClient(String[] mechanisms,
                                            String authorizationId,
                                            String protocol,
                                            String serverName,
                                            Map props,
                                            CallbackHandler cbh)
      throws SaslException {

      SaslClient mclient = null;

    // If a factory has already been installed invoke createSaslClient() on it

       if (cFactory != null) {
            mclient = cFactory.createSaslClient(mechanisms,
                                                authorizationId,
                                                protocol, 
                                                serverName, 
                                                props, 
                                                cbh);
       }

       if (mclient == null)    {
           SaslClientFactory factory;
           for(Enumeration saslFactories = getSaslClientFactories(props);
            mclient == null && saslFactories.hasMoreElements();
            mclient = factory.createSaslClient(mechanisms, 
                                               authorizationId,
                                               protocol, 
                                               serverName, 
                                               props, 
                                               cbh))

           factory = (SaslClientFactory)saslFactories.nextElement();
           }

       return mclient;

   }
    /**
     * Gets an enumeration of known factories for producing SaslClient.
     * <p> This method uses the same sources for locating factories as
     * createSaslClient(). 
     * @param props   A possibly null set of properties that may contain policy
     * properties and the property CLIENTPKGS("com.novell.security.sasl.client.pkgs") 
     * for specifying a list of SaslClientFactory implementation package names.
     *
     * @return  An enumeration of known SaslClientfactories for producing
     * SaslClient.
     */
    public static Enumeration getSaslClientFactories(Map props)
        {
                   return new SaslEnumFactory("SaslClientFactory",
                                               props,
                                               CLIENTPKGS);
        }

    /**
     * Sets the default SaslClientFactory to use.
     * <p> This method sets fac to be the default factory. It can only be called
     * with a non-null value once per VM. 
     * @exception  IllegalStateException If a factory has been set already
     */
    public static void setSaslClientFactory(SaslClientFactory fac) {

        if (cFactory != null) {
            throw new IllegalStateException (
                "SaslClientFactory already defined");
        }
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkSetFactory();
        }
        synchronized (cFactory){
            cFactory = fac;
        }
    }
}
