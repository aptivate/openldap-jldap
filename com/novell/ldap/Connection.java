/* **************************************************************************
 * $Novell: /ldap/src/jldap/src/com/novell/ldap/client/Connection.java,v 1.14 2000/09/03 19:55:55 smerrill Exp $
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
 
package com.novell.ldap.client;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.protocol.UnbindRequest;

/**
  * A thread that creates a connection to an LDAP server. After the
  * connection is made, another thread is created that reads data from the
  * connection.
  */
public final class Connection implements Runnable {

   // The listener thread will multiplex response messages received from the
   // server to one of many queues. Each LDAPListener which registers with
   // this class will have its own message queue. That message queue may be
   // dedicated to a single LDAP operation, or may be shared among many LDAP
   // operations.
   //
   // The applications thread, using an LDAPListener, writes data directly
   // to the server using this class. The application thread will then query
   // the LDAPListener for a response.
   //
   // The listener thread reads data directly from the server as it decodes
   // an LDAPMessage and writes it to a message queue associated with either
   // an LDAPResponseListener, or an LDAPSearchListener. It uses the message
   // ID from the response to determine which listener is expecting the
   // result. It does this by getting a list of message ID's from each
   // listener, and comparing the message ID from the message just received
   // and adding the message to that listeners queue.
   //
   // Note: the listener thread must not be a "selfish" thread, since some
   // operating systems do not time slice.
   //
   private Thread listener; // New thread that reads data from the server.
   private boolean v3 = true;

   private LBEREncoder encoder = new LBEREncoder();
   private LBERDecoder decoder = new LBERDecoder();

   private String host;
   private int port;

   private boolean bound = false;

   private InputStream in;
   private OutputStream out;
   private Socket socket;

   private Vector ldapListeners;

   // true means v3; false means v2
   void setV3(boolean v) {
      v3 = v;
   }

   // A BIND request has been successfully made on this connection
   public void setBound() {
      bound = true;
   }

   public boolean isBound() {
      return bound;
   }

   public void setInputStream(InputStream is) {
      in = is;
   }

   public void setOutputStream(OutputStream os) {
      out = os;
   }

   public InputStream getInputStream() {
      return in;
   }

   public OutputStream getOutputStream() {
      return out;
   }

   public String getHost() {
      return host;
   }

   public int getPort() {
      return port;
   }

   /**
    * Constructs a TCP/IP connection to a server specified in host and port.
    * The socketFactory parameter produces SSL sockets.
    */
   public Connection(String host, int port, LDAPSocketFactory socketFactory)
      throws LDAPException
   {
      this.host = host;
      this.port = port;

      // Make socket connection to specified host and port
      try {
         if(socketFactory != null) {
            socket = socketFactory.makeSocket(host, port);
         }
         else {
            socket = new Socket(host, port);
         }

         in = new BufferedInputStream(socket.getInputStream());
         out = new BufferedOutputStream(socket.getOutputStream());
      }
      catch(IOException ioe) {
         throw new LDAPException("Unable to connect to server: " + host,
                                 LDAPException.CONNECT_ERROR);
      }

      ldapListeners = new Vector(5);

      listener = new Thread(this);
      listener.setDaemon(true); // If this is the last thread running, exit.
      listener.start();
   }

   /**
    * Writes an LDAPMessage to the LDAP server over a socket.
    */
   public void writeMessage(com.novell.ldap.LDAPMessage msg)
      throws IOException
   {
      byte[] ber = msg.getASN1Object().getEncoding(encoder);
      synchronized(this) {
         out.write(ber, 0, ber.length);
         out.flush();
      }
   }

   //------------------------------------------------------------------------
   // Methods to manage the LDAP Listeners
   //------------------------------------------------------------------------

   /**
    *
    */
   public void addLDAPListener(LDAPListener listener) {
      ldapListeners.addElement(listener);
   }

   /**
    *
    */
   public void removeLDAPListener(LDAPListener listener) {
      ldapListeners.remove(listener);
   }

   /**
    *
    */
   public void abandon(int msgId) {
      // Find the message queue which owns this request.
      int cnt = ldapListeners.size();
      findMsgId:
         for(int i=0; i<cnt; i++) {
            LDAPListener ldapListener =
                (LDAPListener)ldapListeners.elementAt(i);
            int[] msgIDs = ldapListener.getMessageIDs();
            for(int j=0; j<msgIDs.length; j++)
               if(msgIDs[j] == msgId) {
                  ldapListener.removeResponses(msgId); // responses already received
                  ldapListener.removeMessageIDAndNotify(msgId); // notifies
                  break findMsgId; // we're done, so bail out
               }
         }
   }

   /**
    *
    */
   protected void finalize() {
      shutdown(null);
   }

   /**
    * This method may be called by finalize() for the connection, or it may
    * be called by LDAPConnection.disconnect().
    */
   public synchronized void shutdown(LDAPControl[] reqCtls) {

      if(socket != null) {
         try {
            // abandonOutstandingReqs(reqCtls);
            if(bound) {
               writeMessage(new LDAPMessage(new UnbindRequest(), reqCtls));
            }
         }
         catch(IOException ioe) {

         }
         finally {
            try {
               out.flush();
               socket.close();
            }
            catch(java.io.IOException ie) {
               // problem closing socket
            }

            // make sure no thread is left doing a wait()!!!
            // notify each object that may be waiting....

            socket = null;
         } 
      }
   }


   /**
    * The thread that decodes and processes LDAPMessage's from the server.
    *
    * Note: This thread needs a graceful shutdown implementation.
    */
   public void run() {
      try {
         for(;;) {
            // ------------------------------------------------------------
            // Decode an LDAPMessage directly from the socket.
            // ------------------------------------------------------------
            ASN1Identifier asn1ID = new ASN1Identifier(in);
            if(asn1ID.getTag() != ASN1Sequence.TAG)
               continue; // loop looking for an LDAPMessage identifier

            ASN1Length asn1Len = new ASN1Length(in);

            com.novell.ldap.protocol.LDAPMessage msg =
                new com.novell.ldap.protocol.LDAPMessage(
                   decoder, in, asn1Len.getLength());

            // ------------------------------------------------------------
            // Process the decoded LDAPMessage.
            // ------------------------------------------------------------
            int msgId = msg.getMessageID();

            if(msgId == 0) {
               // Process Unsolicited Notification
            }
            else {
               // Find the message queue which requested this response.
               // It is possible to receive a response for a request which
               // has been abandoned. If abandoned, do nothing.
               int cnt = ldapListeners.size();
               findMsgId:
                  for(int i=0; i<cnt; i++) {
                     LDAPListener ldapListener =
                         (LDAPListener)ldapListeners.elementAt(i);
                     int[] msgIDs = ldapListener.getMessageIDs();
                     for(int j=0; j<msgIDs.length; j++)
                        if(msgIDs[j] == msgId) {
                           ldapListener.addLDAPMessage(msg); //notifies
                           break findMsgId; // we're done, so bail out
                        }
                  }
            }
         }
      }
      catch(IOException ioe) {
//       ioe.printStackTrace();
      }
      finally {
         shutdown(null);
      }
   }

}

