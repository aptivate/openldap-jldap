/* **************************************************************************
 * $Novell$
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/
 
package com.novell.asn1.ldap;

import java.io.*;
import com.novell.asn1.*;

/**
 *       MessageID ::= INTEGER (0 .. maxInt)
 *
 *       maxInt INTEGER ::= 2147483647 -- (2^^31 - 1) --
 *
 * Note: The creation of a MessageID should be hidden within the creation of
 *       an LDAPMessage. The MessageID needs to be in sequence, and has an
 *       upper and lower limit. There is never a case when a user should be
 *       able to specify the MessageID for an LDAPMessage. The MessageID()
 *       class should be package protected. (So the MessageID value isn't
 *       arbitrarily run up.)
 */
class MessageID extends ASN1Integer {

   private static int messageID;

   /**
    * Creates a MessageID with an auto incremented ASN1Integer value.
	 *
	 * Bounds: (0 .. 2,147,483,647) (2^^31 - 1 or Integer.MAX_VALUE)
    */
   protected MessageID()
   {
		super((messageID < Integer.MAX_VALUE) ? ++messageID : (messageID = 0));
   }

   /**
    * Creates a MessageID with a specified int value.
    */
   protected MessageID(int i)
   {
      super(i);
   }

}

