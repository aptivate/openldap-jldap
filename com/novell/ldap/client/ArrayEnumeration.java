/**
 * Class to return elements of an array as an Enumeration
 */
package com.novell.ldap.client;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class ArrayEnumeration implements Enumeration
{
    private Object[] eArray; 
    private int index = 0;
    /**
     * Constructor to create the Enumeration
     *
     * @param array the array to use for the Enumeration
     */
    public ArrayEnumeration( Object[] eArray)
    {
        this.eArray = eArray;
    }

    public boolean hasMoreElements()
    {
        return (index < eArray.length);
    }

    public Object nextElement() throws NoSuchElementException
    {
        if( index >= eArray.length) {
            throw new NoSuchElementException();
        }
        return eArray[index++];
    }
}
