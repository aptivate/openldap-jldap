
package com.novell.asn1;

import java.io.*;

/**
 * This class provides the means to manipulate ASN.1 Length's. It will
 * be used by ASN1Encoder's and ASN1Decoder's by composition.
 */
public class ASN1Length { 

   //*************************************************************************
   // Private variables
   //*************************************************************************

   private int length;
   private int encodedLength;

   //*************************************************************************
   // Constructors for ASN1Length
   //*************************************************************************

   /**
	 * Constructs an ASN1Length
    */
   public ASN1Length(int length)
   {
		this.length = length;
   }

   /**
    * Decode an ASN1Length directly from an InputStream. Save the
    * encoded length of the ASN1Length.
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
    * Returns the length of this ASN1Length.
    */
   public int getLength()
   {
      return length;
   }

   /**
    * Returns the encoded length of this ASN1Length.
    */
   public int getEncodedLength()
   {
      return encodedLength;
   }

}

