/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/asn1/ASN1Null.java,v 1.7 2001/06/11 17:58:59 cmorris Exp $
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents the ASN.1 NULL type.
 */
public class ASN1Null extends ASN1Simple {

   /**
    * ASN.1 NULL tag definition.
    */
   public static final int TAG = 0x05;

   /**
    * ID is added for Optimization. id needs only be one Value for every instance
    * Thus we create it only once.
    */
    public static final ASN1Identifier ID =
        new ASN1Identifier(ASN1Identifier.UNIVERSAL, false, TAG);
   /* Constructor for ASN1Null
    */

   /**
    * Call this constructor to construct a new ASN1Null
    * object.
    *
    */
   public ASN1Null()
   {
        id = ID;
   }

   /* ASN1Object implementation
    */

   /**
    * Call this method to encode the current instance into the
    * specified output stream using the specified encoder object.
    *
    * @param enc Encoder object to use when encoding self.<br>
    *
    * @param out The output stream onto which the encoded byte
    * stream is written.
    */
   public void encode(ASN1Encoder enc, OutputStream out)
      throws IOException
   {
      enc.encode(this, out);
   }

   /* ASN1Null specific methods
    */

   /**
    * Return a String representation of this ASN1Null object.
    */
   public String toString()
   {
      return super.toString() + "NULL: \"\"";
   }

}

