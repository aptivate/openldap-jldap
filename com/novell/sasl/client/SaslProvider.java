/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C)  2003 Novell, Inc. All Rights Reserved.
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

import java.security.AccessController;
import java.security.Provider;

/**
 * Implements a "provider" for the Java Security API.Constructs a provider with
 * the specified name, version number, and information.
 */
public final class SaslProvider extends Provider {

    /**
     * A human-readable description of the provider and its services.
     */
    private static String info = "Open SASL Provider ";
                   

    public SaslProvider() {

        super("SaslClientFactory", 1.0, "*****This a  provider*************");

        AccessController.doPrivileged(new java.security.PrivilegedAction() {
            public Object run() {

                /*
                 * Cipher engines
                 */
                try {
                    put("SaslClientFactory.DIGEST-MD5",
                        "com.novell.sasl.client.ClientFactory");
                    
                    put("SaslClientFactory.EXTERNAL",
                        "com.novell.sasl.client.ClientFactory");

                }catch(SecurityException e)            {
                    System.out.println("***exception putting" + 
                                       "a provider property****");
                }
                return null;
            }
      });
    }
}
