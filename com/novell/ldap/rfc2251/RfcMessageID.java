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

package com.novell.ldap.rfc2251;

import com.novell.ldap.asn1.*;

/** 
 * Represents an LDAP Message ID.
 *
 *<pre>
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
 *</pre>
 */
class RfcMessageID extends ASN1Integer {

    private static int messageID = 0;
    private static Object lock = new Object();

    /**
     * Creates a MessageID with an auto incremented ASN1Integer value.
     *
     * Bounds: (0 .. 2,147,483,647) (2^^31 - 1 or Integer.MAX_VALUE)
     *
     * MessageID zero is never used in this implementation.  Always
     * start the messages with one.
     */
    protected RfcMessageID()
    {
        super(getMessageID());
    }

    /**
     * Creates a MessageID with a specified int value.
     */
    protected RfcMessageID(int i)
    {
        super(i);
    }

    /**
     * Increments the message number atomically
     *
     * @return the new message number
     */
    private final static int getMessageID() {
        synchronized(lock) {
            return (messageID < Integer.MAX_VALUE) ? ++messageID : (messageID = 1);
        }
    }
}

