/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/MessageVector.java,v 1.2 2001/02/22 21:49:36 vtag Exp $
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

package com.novell.ldap.client;

import java.util.*;
import com.novell.ldap.client.*;

/**
 * The <code>MessageVector</code> class implements some JDK1.2 features
 * of vector needed by the Java LDAP SDK, which are not available
 * in JDK 1.1.x environments. Specifically, we need the atomic get/remove
 * object implemented by <code>Object remove(int index)</code>.
 */
public class MessageVector extends java.util.Vector
{
    public MessageVector( int cap, int incr)
    {
        super( cap, incr);
        return;
    }
    /**
     * Removes the element at the specified position in this Vector.
     * shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the Vector.
     * Overrides the jdk1.2 version of this command if running 1.2 or 1.3
     * and overloads the method if running version 1.1.x
     *
     * @exception ArrayIndexOutOfBoundsException index out of range (index
     * 		  &lt; 0 || index &gt;= size()).
     * @param index the index of the element to removed.
     *
     */
    public synchronized Object remove(int index) {
        // check for valid index
	    if (index >= elementCount)
	        throw new ArrayIndexOutOfBoundsException(index);
	    modCount++;
        // Save the object so we can return it
	    Object objectAtIndex = elementData[index];
        // Close up the array
	    int numToMove = elementCount - index - 1;
	    if (numToMove > 0) {
	        System.arraycopy(elementData, index+1, elementData, index, numToMove);
        }
        // adjust element count
        elementCount -= 1;
        // Clear the end of the array
	    elementData[elementCount] = null;
        // return the object that was a the index
	    return objectAtIndex;
    }

    /**
     * A method specific to Message objects.  It finds the
     * Message object with the given MsgID, and returns the Message
     * object. It finds the object and returns it in an atomic operation.
     *
     * @param msgId The msgId of the Message object to return
     *
     * @return The Message object corresponding to this MsgId.
     *
     * @throws NoSuchFieldException when no object with the corresponding
     * value for the MsgId field can be found.
     */
    public synchronized Message findMessageById( int msgId)
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
    public synchronized Object[] getObjectArray()
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
