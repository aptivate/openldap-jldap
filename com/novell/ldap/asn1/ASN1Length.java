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
package com.novell.ldap.asn1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides a means to manipulate ASN.1 Length's. It will
 * be used by ASN1Encoder's and ASN1Decoder's by composition.
 */
public class ASN1Length {

   /* Private variables
    */

   private int length;
   private int encodedLength;

   /* Constructors for ASN1Length
    */

   /**
    * Constructs an empty ASN1Length.  Values are added by calling reset
    */
   public ASN1Length(){}
   /**
	 * Constructs an ASN1Length
    */
   public ASN1Length(int length)
   {
		this.length = length;
   }

   /**
    * Constructs an ASN1Length object by decoding data from an
    * input stream.
    *
    * @param in A byte stream that contains the encoded ASN.1
    *
    */
   public ASN1Length(InputStream in)
      throws IOException
   {
		int r = in.read();
		encodedLength++;
		if(r == 0x80)
			length = -1;
		else if(r < 0x80)
			length = r;
		else {
			length = 0;
			for(r = r & 0x7F; r > 0; r--) {
				int part = in.read();
				encodedLength++;
				if(part < 0)
					throw new EOFException("BERDecoder: decode: EOF in ASN1Length");
				length = (length << 8) + part;
			}
		}

   }

   /**
    * Resets an ASN1Length object by decoding data from an
    * input stream.
    *
    * Note: this was added for optimization of ASN1.LBERdecoder.decode()
    *
    * @param in A byte stream that contains the encoded ASN.1
    *
    */
   public final void reset(InputStream in)
      throws IOException
   {
        encodedLength = 0;
		int r = in.read();
		encodedLength++;
		if(r == 0x80)
			length = -1;
		else if(r < 0x80)
			length = r;
		else {
			length = 0;
			for(r = r & 0x7F; r > 0; r--) {
				int part = in.read();
				encodedLength++;
				if(part < 0)
					throw new EOFException("BERDecoder: decode: EOF in ASN1Length");
				length = (length << 8) + part;
			}
		}

   }

   /**
    * Returns the length of this ASN1Length.
    */
   public final int getLength()
   {
      return length;
   }

   /**
    * Returns the encoded length of this ASN1Length.
    */
   public final int getEncodedLength()
   {
      return encodedLength;
   }
}
