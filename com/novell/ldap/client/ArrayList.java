/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/ArrayList.java,v 1.1 2001/01/30 21:21:16 vtag Exp $
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
		    "Index: "+index+", Size: "+size);
        return;
    }

    /**
     * Returns the number of items in this ArrayList.
     *
     * @return  the number of items in this ArrayList.
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
    public void ensureCapacity(int capacity) {
	    int origCapacity = items.length;
	    if( capacity > origCapacity) {
	        Object oldData[] = items;
    	    if( origCapacity < capacity) {
                // Allocate more plus a cushion
	            items = new Object[capacity + (capacity / 2) + 1];
	            System.arraycopy(oldData, 0, items, 0, size);
            }
	    }
        return;
    }

    /**
     * Appends the specified item to the end of this ArrayList.
     *
     * @param newItem the item to be added to the ArrayList.
     */
    public void add(Object newItem) {
	    ensureCapacity(size + 1);
	    items[size++] = newItem;
	    return;
    }

    /**
     * Inserts the specified item at the specified position in this ArrayList.
     * Moves the item currently at that position (if any) and any subsequent
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
}
