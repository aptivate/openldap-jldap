/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1Sequence.java,v 1.7 2001/04/16 17:47:14 javed Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.asn1;

import java.io.*;
import com.novell.ldap.client.ArrayList;

/**
 * The ASN1Sequence class can hold an ordered collection of components with
 * distinct type. This class inherits from the ASN1Structured class which
 * provides functionality to hold multiple ASN1 components.
 */
public class ASN1Sequence extends ASN1Structured
{

   /**
    * ASN.1 SEQUENCE tag definition.
    */
   public static final int TAG = 0x10;

   /**
    * ID is added for Optimization. id needs only be one Value for every instance
    * Thus we create it only once.
    */
   private static final ASN1Identifier ID =
        new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, TAG);
   /* Constructors for ASN1Sequence
    */

   /**
    * Constructs an ASN1Sequence object with no actual
    * ASN1Objects in it. Assumes a default size of 5 elements.
    */
   public ASN1Sequence()
   {
      this(5);
      return;
   }

   /**
    * Constructs an ASN1Sequence object with the specified
    * number of placeholders for ASN1Objects. However there
    * are no actual ASN1Objects in this SequenceOf object.
    *
    * @param size Specifies the initial size of the collection.
    */
   public ASN1Sequence(int size)
   {
      id = ID;
      content = new ArrayList(size);
      return;
   }

    /**
    * Constructs an ASN1Sequence object by decoding data from an
    * input stream.
    *
    * @param dec The decoder object to use when decoding the
    * input stream.  Sometimes a developer might want to pass
    * in his/her own decoder object<br>
    *
    * @param in A byte stream that contains the encoded ASN.1
    *
    */
   public ASN1Sequence(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      id = ID;
      decodeStructured(dec, in, len);
      return;
   }

   /* ASN1Sequence specific methods
    */

   /**
    * Return a String representation of this ASN1Sequence.
    */
   public String toString()
   {
      return super.toString("SEQUENCE: { ");
   }
}
