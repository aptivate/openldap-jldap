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

package com.novell.ldap;

import com.novell.ldap.client.Debug;
import java.io.IOException;
import java.net.Socket;
import javax.net.SocketFactory;
import java.net.UnknownHostException;
import javax.net.ssl.*;

/**
 * Represents a socket factory that the method startTLS of LDAPConnection
 * can use to create secure TLS connections to LDAP servers using JSSE
 * technology.
 *
 * @see LDAPConnection#LDAPConnection(LDAPSocketFactory)
 * @see LDAPConnection#setSocketFactory
 */
public class LDAPJSSEStartTLSFactory
                implements LDAPTLSSocketFactory, org.ietf.ldap.LDAPSocketFactory
{
    private SSLSocketFactory factory;
    private boolean pauseForHandShake = false;

    /**
     * Constructs an LDAPJSSEStartTLSFactory object using the default settings
     * for a JSSE SSLSocketFactory.
     * <p>Setting the keystore for the default implementation is specific to the
     * implementation.  For Sun's JSSE implementation, the property
     * javax.net.ssl.truststore should be set to the path of a keystore that
     * holds the trusted root certificate of the directory server. </P>
     *
     * For information on creating keystores see the keytool documentation on
     * <a href="http://java.sun.com/j2se/1.4/docs/tooldocs/tools.html#security">
     * Java 2, security tools</a>
     */
    public LDAPJSSEStartTLSFactory()
    {
        factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        return;
    }

    /**
     * Constructs an LDAPSocketFactory using the SSLSocketFactory specified.
     *
     * </p>This SSLSocketFactory is used when startTLS is called.
     * For information on using the SSLSocketFactory see also
     * <a href="http://java.sun.com/j2se/1.4/docs/api/javax/net/ssl/SSLSocketFactory.html">
     * javax.net.ssl.SSLContext</a>.</p>
     */
    public LDAPJSSEStartTLSFactory(SSLSocketFactory factory)
    {
        this.factory = factory;
        return;
    }

    public java.net.Socket createSocket(String host, int port)
        throws IOException, UnknownHostException
    {
        return new java.net.Socket(host, port);
    }

    public Socket createSocket( Socket socket )
        throws IOException, UnknownHostException
    {
        SSLSocket tls = (SSLSocket)factory.createSocket(
            socket,
            socket.getInetAddress().getHostName(),
            socket.getPort(),
            false); /*  This flag allows us to close this socket without
                        closing the underlying Socket */

        tls.addHandshakeCompletedListener(new HandShakeFinished(this));
        // Handshake Finished will set pauseForHandShake to false

        this.pauseForHandShake = true;
        tls.startHandshake();

        try{
            while (this.pauseForHandShake)
                Thread.currentThread().sleep(5);
        }
        catch (java.lang.InterruptedException ie){
            throw new java.lang.RuntimeException("Internal error: Could not "+
              "pause main thread while waiting for TLS handshake to complete");
        }
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.TLS, "startTLS: TLS handshake complete");
            String suites[] = tls.getSupportedCipherSuites();
            for(int i=0; i< suites.length; i++){
                Debug.trace(Debug.TLS, "startTLS: suite["+i+"]="+ suites[i]);
            }
        }
        return tls;
    }

    private class HandShakeFinished implements
                        javax.net.ssl.HandshakeCompletedListener{
        private LDAPJSSEStartTLSFactory ssf;
        public HandShakeFinished (LDAPJSSEStartTLSFactory ssf){
            this.ssf = ssf;
            return;
        }
        public void handshakeCompleted(
            javax.net.ssl.HandshakeCompletedEvent event)
        {
            if( Debug.LDAP_DEBUG ) {
                Debug.trace(Debug.messages, "startTLS: Handshake is complete");
            }
            this.ssf.pauseForHandShake = false;
            return;
        }
    }
}
