/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/Vector2.java,v 1.1 2001/03/23 19:13:34 vtag Exp $
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

import java.util.Vector;
import com.novell.ldap.client.*;

/**
 * The <code>Vector2</code> class implements some JDK1.2 features
 * of vector needed by the Java LDAP SDK, which are not available
 * in JDK 1.1.x environments. Specifically, we need the atomic get/remove
 * object implemented by <code>Object remove(int index)</code>.
 */
public class Vector2 extends java.util.Vector
{
    public Vector2( int cap, int incr)
    {
        super( cap, incr);
        return;
    }

    /**
     * This implements ensureCapacity in an unsynchronized way.
     * Synchronized methods in this class call this method to
     * ensuring capacity without the cost of an extra synchronization.
     * Need to implement this because Vectors implementation is also private.
     */ 
    private void ensureCapacityMethod(int minCapacity)
    {
	    int origCapacity = elementData.length;
	    if (minCapacity > origCapacity) {
            // We need more space, increase capacity
	        Object origData[] = elementData;
            // Compute needed size
	        int newCapacity = (capacityIncrement > 0) ?
		    (origCapacity + capacityIncrement) : (origCapacity * 2);
    	    if (newCapacity < minCapacity) {
		        newCapacity = minCapacity;
	        }
            // Get a new Object array with new capacity.
	        elementData = new Object[newCapacity];
	        System.arraycopy(origData, 0, elementData, 0, elementCount);
	    }
        return;    
    }

    /**
     * Appends the specified object to the end of this MessageVector.
     * Overrides the jdk1.2 version of this command if running 1.2 or 1.3
     * and overloads the method if running version 1.1.x
     *
     * @param obj element to be appended to this Vector.
     *
     * @return true
     */
    public synchronized boolean add(Object obj)
    {
	    ensureCapacityMethod(elementCount + 1);
	    elementData[elementCount++] = obj;
        return true;
    }
    
    /**
     * Returns the object at the specified position in this MessageVector.
     * Overrides the jdk1.2 version of this command if running 1.2 or 1.3
     * and overloads the method if running version 1.1.x
     *
     * @param index index of element to return.
     *
     * @return the object at the specified position
     *
     * @throws    IndexOutOfBoundsException if index is out of range
     */
    public synchronized Object get(int index)
    {
	    if (index >= elementCount) {
	        throw new ArrayIndexOutOfBoundsException(index);
        }
	    return elementData[index];
    }
    
    /**
     * Removes the element at the specified position in this Vector.
     * shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the Vector.
     * Overrides the jdk1.2 version of this command if running 1.2 or 1.3
     * and overloads the method if running version 1.1.x
     *
     * @param index the index of the element to removed.
     *
     * @return the object a the specified index
     *
     * @throws    IndexOutOfBoundsException if index is out of range
     */
    public synchronized Object remove(int index)
    {
        // check for valid index
	    if (index >= elementCount)
	        throw new ArrayIndexOutOfBoundsException(index);
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
     * Returns an array containing all of the elements in this
     * MessageVector.
     * Overrides the jdk1.2 version of this command if running 1.2 or 1.3
     * and overloads the method if running version 1.1.x
     *
     * @return the elements of the vector in the correct order.
     */
    public synchronized Object[] toArray()
    {
	    Object[] results = new Object[elementCount];
	    System.arraycopy( elementData, 0, results, 0, elementCount);
	    return results;
    }
}
