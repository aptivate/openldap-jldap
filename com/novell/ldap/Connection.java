/*
 */
package com.novell.ldap.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Vector;

import com.novell.ldap.*;

/**
  * A thread that creates a connection to an LDAP server.
  * After the connection is made, a thread is created that reads from the
  * connection.
  */
public final class Connection implements Runnable {

	// The worker thread will multiplex response messages received from the
	// server to one of many queues. Each LDAPListener which registers with
	// this class will have its own message queue. That message queue may be
	// dedicated to a single LDAP operation, or may be shared among many LDAP
	// operations.
	//
	// The applications thread, using an LDAPListener, writes data directly
	// to the server using this class. The application thread will then query
	// the LDAPListener for a response.
	//
	// The worker thread reads data directly from the server, and writes
	// it to a message queue associated with either an LDAPResponseListener,
	// or an LDAPSearchListener. It uses the message ID from the response to
	// determine which listener is expecting the result. It does this by
	// getting a list of message id's from each listener, and comparing the
	// message ID from the message just received and adding the message to
	// that listeners queue.
	//
   private Thread worker; // New thread that reads data from the server.
   private boolean v3 = true;

   String host;   // used by LDAPClient for generating exception messages
   int port;      // used by LDAPClient for generating exception messages

   private boolean bound = false;

   public InputStream inStream;
   public OutputStream outStream;
   public Socket socket;

   // For processing "disconnect" unsolicited notification
   private LDAPClient parent = null; 

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
   void setBound() {
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

   /**
    * for jldap
    */
   Connection(LDAPClient parent, String host, int port,
		        LDAPSocketFactory socketFactory)
      throws LDAPException {

      this.host = host;
      this.port = port;
      this.parent = parent;

      // Make connection to specified server
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

      worker = new Thread(this);
      worker.setDaemon(true); // If this is the last thread running, exit.
      worker.start();
   }


	//------------------------------------------------------------------------
	// Methods to manage IO to the LDAP server

   synchronized int getMsgId() {
      return ++msgId;
   }

	/**
	 * Writes a ber encoded message to the LDAP server over a socket
	 */
   public void writeMessage(BerEncoder ber) throws IOException {
      synchronized(this) {
         outStream.write(ber.getBuf(), 0, ber.getDataLen());
         outStream.flush();
      }
   }

	//---------------------------------------------------------------
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

	//---------------------------------------------------------------

/*
   // save this code as an example how to abandon a request
	//
	//
   synchronized void abandonRequest(int mid, LDAPControl[] reqCtls) {

      LDAPRequest ldr = findRequest(mid);

      if(ldr == null) {
         return;
      }

      BerEncoder ber = new BerEncoder(256);
      int abandonMsgId = getMsgId();

      //
      // build the abandon request.
      //

      try {

         ber.beginSeq(Ber.ASN_SEQUENCE | Ber.ASN_CONSTRUCTOR);
         ber.encodeInt(abandonMsgId);
         ber.encodeInt(mid, LDAPClient.LDAP_REQ_ABANDON);

         if(v3) {
            LDAPClient.encodeControls(ber, reqCtls);
         }
         ber.endSeq();

         outStream.write(ber.getBuf(), 0, ber.getDataLen());
         outStream.flush();
         removeRequest(mid);

      }
      catch(IOException ex) {
         //System.err.println("ldap.abandon: " + ex);
      }

      // Dont expect any response for the abandon request.
   }

   synchronized void abandonOutstandingReqs(LDAPControl[] reqCtls) {

      LDAPRequest ldr = pendingRequests;
      LDAPRequest nextLdr = null;

      while(ldr != null) {
         abandonRequest(ldr.msgId, reqCtls);
         pendingRequests = ldr = ldr.next;
      }

   }
*/	

   ////////////////////////////////////////////////////////////////////////////
   //
   // Methods to unbind from server and clear up resources when object is
   // destroyed.
   //
   ////////////////////////////////////////////////////////////////////////////

   private synchronized void ldapUnbind(LDAPControl[] reqCtls) {

      BerEncoder ber = new BerEncoder(256);
      int unbindMsgId = getMsgId();

      // build the unbind request.
      try {

         ber.beginSeq(Ber.ASN_SEQUENCE | Ber.ASN_CONSTRUCTOR);
         ber.encodeInt(unbindMsgId);
         // IMPLICIT TAGS
         ber.encodeByte(LDAPClient.LDAP_REQ_UNBIND);
         ber.encodeByte(0);

         if(v3) {
            LDAPClient.encodeControls(ber, reqCtls);
         }
         ber.endSeq();

         outStream.write(ber.getBuf(), 0, ber.getDataLen());
         outStream.flush();
      }
      catch(IOException ex) {
         //System.err.println("ldap.unbind: " + ex);
      }

      // Dont expect any response for the unbind request.
   }

   protected void finalize() {
      cleanup(null);
   }

   synchronized void cleanup(LDAPControl[] reqCtls) {

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


   ////////////////////////////////////////////////////////////////////////////
   //
   // The LDAP Binding thread. It does the demultiplexing of multiple requests
   // on the same TCP connection.
   //
   ////////////////////////////////////////////////////////////////////////////


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
            if(inbuf[offset++] != (Ber.ASN_SEQUENCE | Ber.ASN_CONSTRUCTOR))
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
               BerDecoder retBer = new BerDecoder(inbuf, 0, offset);
					LDAPMessage message = messageFactory.createLDAPMessage(retBer);

					// get the msgId
               //retBer.parseSeq(null);
               //curMsgId = retBer.parseInt();
               //retBer.reset();   // reset offset

               if(message.getMessageID() == 0) {
                  // Unsolicited Notification
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
            catch(Ber.DecodeException e) {
               //System.err.println("Cannot parse Ber");
            }
         } // while(true)
      }
      catch(java.io.IOException ex) {
      }
      finally {
         cleanup(null); // cleanup
      }
   }

}
