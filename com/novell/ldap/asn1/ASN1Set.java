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

import java.io.IOException;
import java.io.InputStream;

/**
 * The ASN1Set class can hold an unordered collection of components with
 * distinct type. This class inherits from the ASN1Structured class
 * which already provides functionality to hold multiple ASN1 components.
 */
public class ASN1Set extends ASN1Structured {

   /**
    * ASN.1 SET tag definition.
    */
   public static final int TAG = 0x11;

   /**
    * ID is added for Optimization.
    
    * <p>ID needs only be one Value for every instance,
    * thus we create it only once.</p>
    */
    public static final ASN1Identifier ID =
        new ASN1Identifier(ASN1Identifier.UNIVERSAL, true, TAG);
   /* Constructors for ASN1Set
    */

   /**
    * Constructs an ASN1Set object with no actual
    * ASN1Objects in it. Assumes a default size of 5 elements.
    */
   public ASN1Set()
   {
      super(ID);
      return;
   }


   /**
    * Constructs an ASN1Set object with the specified
    * number of placeholders for ASN1Objects. However there
    * are no actual ASN1Objects in this SequenceOf object.
    *
    * @param size Specifies the initial size of the collection.
    */
   public ASN1Set(int size)
   {
      super(ID, size);
      return;
   }

   /**
    * Constructs an ASN1Set object by decoding data from an
    * input stream.
    *
    * @param dec The decoder object to use when decoding the
    * input stream.  Sometimes a developer might want to pass
    * in his/her own decoder object<br>
    *
    * @param in A byte stream that contains the encoded ASN.1
    *
    */
   public ASN1Set(ASN1Decoder dec, InputStream in, int len)
      throws IOException
   {
      super(ID);
      decodeStructured(dec, in, len);
      return;
   }

   /* ASN1Set specific methods
    */

   /**
    * Returns a String representation of this ASN1Set.
    */
   public String toString()
   {
      return super.toString("SET: { ");
   }
}
