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

package com.novell.ldap;

import com.novell.ldap.client.*;

/**
 * The <code>MessageVector</code> class implements additional semantics
 * to Vector needed for handling messages.
 */
/* package */
class MessageVector extends java.util.Vector
{
    /* package */
    MessageVector( int cap, int incr)
    {
        super( cap, incr);
        return;
    }

    /**
     * Finds the Message object with the given MsgID, and returns the Message
     * object. It finds the object and returns it in an atomic operation.
     *
     * @param msgId The msgId of the Message object to return
     *
     * @return The Message object corresponding to this MsgId.
     *
     * @throws NoSuchFieldException when no object with the corresponding
     * value for the MsgId field can be found.
     */
    /* package */
    final synchronized Message findMessageById( int msgId)
                throws NoSuchFieldException
    {
        Message msg = null;
        for( int i = 0; i < elementCount; i++) {
            if( (msg = (Message)elementData[i]) == null) {
                throw new NoSuchFieldException();
            }
            if( msg.getMessageID() == msgId) {
                return msg;
            }
        }
        throw new NoSuchFieldException();
    }

    /** Returns an array containing all of the elements in this MessageVector.
     * The elements returned are in the same order in the array as in the
     * Vector.  The contents of the vector are cleared.
     *
     * @return the array containing all of the elements.
     */
    /* package */
    final synchronized Object[] getObjectArray()
    {
        Object[] results = new Object[elementCount];
        System.arraycopy( elementData, 0, results, 0, elementCount);
        for( int i = 0; i < elementCount; i++) {
            elementData[i] = null;
        }
        elementCount = 0;
        return results;
    }
}
