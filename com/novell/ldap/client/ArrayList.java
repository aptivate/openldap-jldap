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

package com.novell.ldap.client;

import java.lang.reflect.Array;

/**
 * Resizable array implementation.  This is a subset of java.util.ArrayList
 * providing only those functions needed for the LDAP implementation.
 * When NetWare moves to a 1.2 JDK, we can eliminate this class.
 * <br><br>
 * <strong>Note that this implementation is not synchronized.</strong>
 */

public class ArrayList
{
    /**
     * The array buffer which holds ArrayList items.  Its capacity
     * is the length of this array buffer.
     */
    private Object items[];

    /**
     * The size of the ArrayList (the number of items in it).
     */
    private int size;

    /**
     * Constructs an empty ArrayList with the specified initial capacity.
     *
     * @param   initialCapacity   the initial capacity of the ArrayList.
     */
    public ArrayList(int initialCapacity) {
	    this.items = new Object[initialCapacity];
        return;
    }

    /**
     * Constructs an empty ArrayList.
     */
    public ArrayList() {
	    this(10);
        return;
    }

    /**
     * Check if the given index is in range.  If not, throw an appropriate
     * runtime exception.
     */
    private void checkIndex(int index) {
	    if (index >= size || index < 0)
	        throw new IndexOutOfBoundsException(
		            "Index: " + index + ", Size: " + size);
        return;
    }

    /**
     * Returns the number of items added to this ArrayList.
     *
     * @return  the number of items added to this ArrayList.
     */
    public int size() {
	    return size;
    }

    /**
     * Tests if this ArrayList has no items.
     *
     * @return  <tt>true</tt> if this ArrayList has no items;
     *          <tt>false</tt> otherwise.
     */
    public boolean isEmpty() {
	    return size == 0;
    }

    /**
     * Increases the size of this <tt>ArrayList</tt>, if necessary,
     * to be sure it can hold the number of items specified by
     * the capacity argument.
     *
     * @param   capacity   the desired capacity.
     */
    public void ensureCapacity(int desiredCapacity) {
	    int origCapacity = items.length;
	    if( desiredCapacity > origCapacity) {
	        Object oldData[] = items;
            // Allocate more plus a cushion
	        items = new Object[desiredCapacity + (desiredCapacity / 2) + 1];
	        System.arraycopy(oldData, 0, items, 0, size);
	    }
        return;
    }

    /**
     * Appends the specified item to the end of this ArrayList.
     *
     * @param newItem the item to be added to the ArrayList.
     * 
     * @returns true if the object was added
     */
    public boolean add(Object newItem) {
	    ensureCapacity(size + 1);
	    items[size++] = newItem;
	    return true;
    }

    /**
     * Inserts the specified item at the specified position in this ArrayList.
     * Moves the item currently at that position and any subsequent
     * items up in the ArrayList, i.e. it adds one to their index.
     *
     * @param index index at which the specified item is to be inserted.
     * <br><br>
     * @param item item to be inserted.
     *
     * @throws    IndexOutOfBoundsException if the index is out of range
     */
    public void add(int index, Object item) {
        checkIndex( index);
	    ensureCapacity(size+1); // We are adding one item
	    System.arraycopy(items, index, items, index + 1, size - index);
	    items[index] = item;
	    size++;
        return;
    }

    /**
     * Returns the item at the specified index in this ArrayList.
     *
     * @param  index index of item to return.
     *
     * @return the item at the specified position in this ArrayList.
     *
     * @throws    IndexOutOfBoundsException if index is out of range
     */
    public Object get(int index) {
	    checkIndex(index);
	    return items[index];
    }

    /**
     * Replaces the item at the specified position in this ArrayList with
     * the specified item.
     *
     * @param index index of item to replace.
     * <br><br>
     * @param newItem the item to be stored at the specified position.
     *
     * @return the item previously at the specified position.
     *
     * @throws    IndexOutOfBoundsException if index out is of range
     */
    public Object set(int index, Object newItem) {
	    checkIndex(index);

	    Object oldItem = items[index];
	    items[index] = newItem;
	    return oldItem;
    }

    /**
     * Removes the item at the specified position in this ArrayList.
     * Moves any subsequent items down, i.e. subtracts one from their index.
     *
     * @param index the index of the item to removed.
     *
     * @return the item that was removed from the ArrayList.
     *
     * @throws    IndexOutOfBoundsException if the index is out of range.
     */
    public Object remove(int index) {
	    checkIndex(index);

	    Object oldItem = items[index];

	    int numMoved = size - index - 1;
	    if (numMoved > 0)
	        System.arraycopy(items, index+1, items, index, numMoved);
	    items[--size] = null;
	    return oldItem;
    }

    /**
     * Returns an array containing all of the elements in this ArrayList.
     * Overrides the jdk1.2 version of this command if running 1.2 or 1.3
     * and overloads the method if running version 1.1.x
     *
     * @return the elements of the vector in the correct order.
     */
    public synchronized Object[] toArray()
    {
	    Object[] results = new Object[size];
	    System.arraycopy( items, 0, results, 0, size);
	    return results;
    }

    /**
     * Returns an array containing all of the elements in this ArrayList.
     * The runtime type of the returned array is that of the specified array.
     * If the ArrayList list fits in the specified array, it is returned
     * in the specified array.  If not, a new array is allocated with the
     * same type as the specified array and the size of this ArrayList.<p>
     *
     * If the copied array fits in the specified array and at least one extra
     * element remains, then the the element in the array immediately following
     * the the last element is set to to null.  This can be used to determe
     * the length of the list if the array does not contain any null
     * elements.<p>
     *
     * Overrides the jdk1.2 version of this command if running 1.2 or 1.3
     * and overloads the method if running version 1.1.x<p>

     * @param array the array into which the elements of the ArrayList are to
     *       		be copied, if the array is large enough; if not,
     *              a new array of the same runtime type as the specified array
     *              is allocated for this purpose.
     * @return      the array containing the elements of the ArrayList.
     * @throws      ArrayStoreException if the runtime type of a is not a
     *              supertype of the runtime type of every element in this list.
     */
    public Object[] toArray(Object array[])
    {
        // If application array is too small, get one that is big enough
        if (array.length < size) {
            array = (Object[])Array.newInstance(
                    array.getClass().getComponentType(), size);
        }

        // Copy the data into application array
	    System.arraycopy(items, 0, array, 0, size);

        // Set end to null if large enough
        if (array.length > size) {
            array[size] = null;
        }

        return array;
    }
}
