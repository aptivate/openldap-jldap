/* $Id
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Vector;

import com.novell.ldap.*;
import com.novell.ldap.client.protocol.UnbindRequest;
import com.novell.ldap.client.protocol.lber.*;

/**
  * A thread that creates a connection to an LDAP server.
  * After the connection is made, a thread is created that reads from the
  * connection.
  */
public final class Connection implements Runnable {

	// The producer thread will multiplex response messages received from the
	// server to one of many queues. Each LDAPListener which registers with
	// this class will have its own message queue. That message queue may be
	// dedicated to a single LDAP operation, or may be shared among many LDAP
	// operations.
	//
	// The applications thread, using an LDAPListener, writes data directly
	// to the server using this class. The application thread will then query
	// the LDAPListener for a response.
	//
	// The producer thread reads data directly from the server, and writes
	// it to a message queue associated with either an LDAPResponseListener,
	// or an LDAPSearchListener. It uses the message ID from the response to
	// determine which listener is expecting the result. It does this by
	// getting a list of message id's from each listener, and comparing the
	// message ID from the message just received and adding the message to
	// that listeners queue.
	//
	// Note: the producer thread must not be a "selfish" thread, since some
	// operating systems do not time slice.
	//
   private Thread producer; // New thread that reads data from the server.
   private boolean v3 = true;

   private String host;
   private int port;

   private boolean bound = false;

   private InputStream inStream;
   private OutputStream outStream;
   private Socket socket;

   private int msgId = 0;

	private Vector ldapListeners;
	private LDAPMessageFactory messageFactory = new LDAPMessageFactory();

   // true means v3; false means v2
   void setV3(boolean v) {
      v3 = v;
		messageFactory.setV3(v);
   }

   // A BIND request has been successfully made on this connection
   // When cleaning up, remember to do an UNBIND
   public void setBound() {
      bound = true;
   }

   public boolean isBound() {
      return bound;
   }

   public void setInputStream(InputStream is) {
      inStream = is;
   }

   public void setOutputStream(OutputStream os) {
      outStream = os;
   }

   public InputStream getInputStream() {
      return inStream;
   }

   public OutputStream getOutputStream() {
      return outStream;
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

         inStream = new BufferedInputStream(socket.getInputStream());
         outStream = new BufferedOutputStream(socket.getOutputStream());
      }
      catch(IOException ioe) {
         throw new LDAPException("Unable to connect to server: " + host,
                                 LDAPException.CONNECT_ERROR);
      }

		ldapListeners = new Vector(5);

      producer = new Thread(this);
      producer.setDaemon(true); // If this is the last thread running, exit.
      producer.start();
   }


	//------------------------------------------------------------------------
	// Methods to manage IO to the LDAP server

	/**
	 *	Returns a unique message id for this connection.
	 */
   public synchronized int getMessageID() {
      return ++msgId;
   }

	/**
	 * Writes an lber encoded message to the LDAP server over a socket.
	 */
   public void writeMessage(LberEncoder lber)
		throws IOException
	{
      synchronized(this) {
         outStream.write(lber.getBuf(), 0, lber.getDataLen());
         outStream.flush();
      }
   }

	//------------------------------------------------------------------------
	// Methods to manage the LDAP Listeners

	public void addLDAPListener(LDAPListener listener) {
		ldapListeners.addElement(listener);
	}

	public void removeLDAPListener(LDAPListener listener) {
		ldapListeners.remove(listener);
	}

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

	//------------------------------------------------------------------------
   // Methods to unbind from the server and clean up resources when this
	// object is destroyed.

   private synchronized void ldapUnbind(LDAPControl[] reqCtls) {
      try {
			LDAPRequest req = new UnbindRequest(getMessageID(), reqCtls, v3);

         outStream.write(req.getLber().getBuf(), 0,
				             req.getLber().getDataLen());
         outStream.flush();
      }
		catch(LDAPException e) {
			// encoding errors
		}
      catch(IOException ex) {
			// communication errors
      }

      // An UnbindRequest will not return anything...
   }

	/**
	 *
	 */
   protected void finalize() {
      cleanup(null);
   }

	/**
	 *
	 */
   public synchronized void cleanup(LDAPControl[] reqCtls) {

      if(socket != null) {
         try {
            // abandonOutstandingReqs(reqCtls);
            if(bound) {
               ldapUnbind(reqCtls);
            }
         }
         finally {
            try {
               outStream.flush();
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


	//------------------------------------------------------------------------
   // The LDAPMessage producer thread. It does the demultiplexing of multiple
	// requests on the same TCP/IP connection.

   public void run() {
      byte inbuf[];
      int curMsgId = 0;

      try {
         while(true) {
            inbuf = new byte[2048];

            int bytesread;
            int offset = 0;
            int seqlen = 0;
            int seqlenlen = 0;

            // check that it is the beginning of a sequence
            bytesread = inStream.read(inbuf, offset, 1);
            if(bytesread < 0)
               break;
            if(inbuf[offset++] != (Lber.ASN_SEQUENCE | Lber.ASN_CONSTRUCTOR))
               continue; // loop until we get the start byte

            // get length of sequence
            bytesread = inStream.read(inbuf, offset, 1);
            if(bytesread < 0)
               break;
            seqlen = inbuf[offset++];

            // if high bit is on, length is encoded in the 
            // subsequent length bytes and the number of length bytes 
            // is equal to & 0x80 (i.e. length byte with high bit off).
            if((seqlen & 0x80) == 0x80) {
               seqlenlen = seqlen & 0x7f;  // number of length bytes

               bytesread = 0;

               boolean eos = false;
               int br;

               // Read all length bytes
               while(bytesread < seqlenlen) {
                  br = inStream.read(inbuf, offset+bytesread,      
                                     seqlenlen-bytesread);
                  if(br < 0) {
                     eos = true;
                     break;
                  }
                  bytesread += br;
               }

               // end-of-stream reached before length bytes are read
               if(eos)
                  break;

               // Add contents of length bytes to determine length
               seqlen = 0;
               for(int i = 0; i < seqlenlen; i++) {
                  seqlen = (seqlen << 8) + (inbuf[offset+i] & 0xff);
               }
               offset += bytesread;
            }

            // read in seqlen bytes
            int bytesleft = seqlen;
            if((offset + bytesleft) > inbuf.length) {
               byte nbuf[] = new byte[offset + bytesleft];
               System.arraycopy(inbuf, 0, nbuf, 0, offset);
               inbuf = nbuf;
            }
            while(bytesleft > 0) {
               bytesread = inStream.read(inbuf, offset, bytesleft);
               if(bytesread < 0)
                  break;
               offset += bytesread;
               bytesleft -= bytesread;
            }

            try {
               LberDecoder lber = new LberDecoder(inbuf, 0, offset);
					LDAPMessage message = messageFactory.createLDAPMessage(lber);

               if(message.getMessageID() == 0) {
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
									if(msgIDs[j] == message.getMessageID()) {
										ldapListener.addLDAPMessage(message); //notifies
										break findMsgId; // we're done, so bail out
									}
							}
               }
            }
            catch(Lber.DecodeException e) {
               //System.err.println("Cannot parse Ber");
            }
         } // while(true)
      }
      catch(java.io.IOException ex) {
      }
      finally {
         cleanup(null);
      }
   }

}
