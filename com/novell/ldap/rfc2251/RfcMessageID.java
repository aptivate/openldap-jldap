/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcMessageID.java,v 1.8 2000/11/09 23:50:56 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 ***************************************************************************/
 
package com.novell.ldap.rfc2251;

import java.io.*;
import com.novell.ldap.asn1.*;

/**
 *       MessageID ::= INTEGER (0 .. maxInt)
 *
 *       maxInt INTEGER ::= 2147483647 -- (2^^31 - 1) --
 *
 * Note: The creation of a MessageID should be hidden within the creation of
 *       an RfcLDAPMessage. The MessageID needs to be in sequence, and has an
 *       upper and lower limit. There is never a case when a user should be
 *       able to specify the MessageID for an RfcLDAPMessage. The MessageID()
 *       class should be package protected. (So the MessageID value isn't
 *       arbitrarily run up.)
 */
class RfcMessageID extends ASN1Integer {

   private static int messageID;

   /**
    * Creates a MessageID with an auto incremented ASN1Integer value.
	 *
	 * Bounds: (0 .. 2,147,483,647) (2^^31 - 1 or Integer.MAX_VALUE)
    */
   protected RfcMessageID()
   {
		super((messageID < Integer.MAX_VALUE) ? ++messageID : (messageID = 0));
   }

   /**
    * Creates a MessageID with a specified int value.
    */
   protected RfcMessageID(int i)
   {
      super(i);
   }

}

