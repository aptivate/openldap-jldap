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

package org.ietf.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>An <code>LDAPAttributeSet</code> is a collection of <code>LDAPAttribute</code>
 * classes as returned from an <code>LDAPEntry</code> on a search or read
 * operation. <code>LDAPAttributeSet</code> may be also used to contruct an entry
 * to be added to a directory.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html">
            com.novell.ldap.LDAPAttributeSet</a>
 */
public class LDAPAttributeSet implements java.lang.Cloneable,
                                         java.util.Set
{
    private com.novell.ldap.LDAPAttributeSet attrSet;

    /**
     * Constructs a new set of attributes, using an existing attribute set.
     */
    /* package */
    LDAPAttributeSet( com.novell.ldap.LDAPAttributeSet attrSet)
    {
        this.attrSet = attrSet;
        return;
    }

    /**
     * Constructs an empty set of attributes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#LDAPAttributeSet()">
            com.novell.ldap.LDAPAttributeSet.LDAPAttributeSet()</a>
     */
    public LDAPAttributeSet()
    {
        attrSet = new com.novell.ldap.LDAPAttributeSet();
        return;
    }

    /**
     * Gets the com.novell.ldap.LDAPAttributeSet object.
     */
    /* package */
    com.novell.ldap.LDAPAttributeSet getWrappedObject()
    {
        return attrSet;
    }

    /**
     * Returns a deep copy of this attribute set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#clone()">
            com.novell.ldap.LDAPAttributeSet.clone()</a>
     */
    public Object clone()
            throws CloneNotSupportedException
    {
        try {
            Object newObj = super.clone();
            ((LDAPAttributeSet)newObj).attrSet = (com.novell.ldap.LDAPAttributeSet)attrSet.clone();
            return newObj;
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
    }

    /**
     * Returns the attribute matching the specified attrName.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#getAttribute(java.lang.String)">
            com.novell.ldap.LDAPAttributeSet.getAttribute(String)</a>
     */
    public LDAPAttribute getAttribute(String attrName)
    {
        com.novell.ldap.LDAPAttribute attr;
        if( (attr = attrSet.getAttribute(attrName)) == null) {
            return null;
        }
        return new LDAPAttribute( attr);
    }

    /**
     * Returns a single best-match attribute, or null if no match is
     * available in the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#getAttribute(java.lang.String, java.lang.String)">
            com.novell.ldap.LDAPAttributeSet.getAttribute(String, String)</a>
     */
    public LDAPAttribute getAttribute(String attrName, String lang)
    {
        com.novell.ldap.LDAPAttribute attr;
        if( (attr = attrSet.getAttribute(attrName, lang)) == null) {
            return null;
        }
        return new LDAPAttribute( attr + ";" + lang);
    }

    /**
     * Creates a new attribute set containing only the attributes that have
     * the specified subtypes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#getSubset(java.lang.String)">
            com.novell.ldap.LDAPAttributeSet.getSubset(String)</a>
     */
    public LDAPAttributeSet getSubset(String subtype)
    {
        return new LDAPAttributeSet( attrSet.getSubset( subtype));
    }

// ################### Methods to implement Set ##########################

    /**
     * Adds the specified attribute to this attribute set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#add(java.lang.Object)">
            com.novell.ldap.LDAPAttributeSet.add(Object)</a>
     */
    public boolean add(Object attr)
    {
        com.novell.ldap.LDAPAttribute a;
        a = ((org.ietf.ldap.LDAPAttribute)attr).getWrappedObject();
        return attrSet.add( a);
    }

    /**
     * Unwraps the specified collection, returning a collection
     * containing com.novell.ldap.LDAPAttribute classes.
     */
    private Collection unwrapCollection( java.util.Collection attrs)
    {
        ArrayList c = new ArrayList( attrs.size());
        Iterator i = attrs.iterator();
        while( i.hasNext()) {
            LDAPAttribute a = (LDAPAttribute)i.next();
            c.add( a.getWrappedObject());
        }
        return c;
    }

    /**
     * Adds all the specified attributes to this attribute set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#addAll(java.util.Collection)">
            com.novell.ldap.LDAPAttributeSet.addAll(Collection)</a>
     */
    public boolean addAll(java.util.Collection attrs)
    {
        return attrSet.addAll(unwrapCollection(attrs));
    }

    /**
     * Removes all the attributes from this attribute set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#clear()">
            com.novell.ldap.LDAPAttributeSet.clear()</a>
     */
    public void clear()
    {
        attrSet.clear();
        return;
    }

    /**
     * Returns true if this AttributeSet contains the specified Attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#contains(java.lang.Object)">
            com.novell.ldap.LDAPAttributeSet.contains(Object)</a>
     */
    public boolean contains(Object attr)
    {
        com.novell.ldap.LDAPAttribute a;
        a = ((org.ietf.ldap.LDAPAttribute)attr).getWrappedObject();
        return attrSet.contains( a);
    }

    /**
     * Returns true if this Attribute set contains all the attributes
     * in the specified collection.
     */
    public boolean containsAll(java.util.Collection attrs)
    {
        return attrSet.containsAll(unwrapCollection(attrs));
    }

    /**
     * Compares the specified object with this set for equality.
     */
    public boolean equals(Object set)
    {
        com.novell.ldap.LDAPAttributeSet aset =
                    ((LDAPAttributeSet)set).getWrappedObject();
        return attrSet.equals( aset);
    }

    /**
     * Returns the hash code value for this set.
     */
    public int hashCode()
    {
        return attrSet.hashCode( );
    }

    /**
     * Returns true if there are no elements in this set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#isEmpty()">
            com.novell.ldap.LDAPAttributeSet.isEmpty()</a>
     */
    public boolean isEmpty()
    {
        return attrSet.isEmpty( );
    }

    /**
     * Returns an iterator over the elements of this set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#iterator()">
            com.novell.ldap.LDAPAttributeSet.iterator()</a>
     */
    public Iterator iterator() {
        return new WrappedIterator(this.attrSet.iterator());
    }

    /**
     * Removes the specified LDAPAttribute object from the set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#remove(java.lang.Object)">
            com.novell.ldap.LDAPAttributeSet.remove(Object)</a>
     */
    public boolean remove(Object obj)
    {
        com.novell.ldap.LDAPAttribute a;
        a = ((org.ietf.ldap.LDAPAttribute)obj).getWrappedObject();
        return attrSet.remove( a);
    }

    /**
     * Returns from this set all the elements that are contained
     * in the specified collection.
     */
    public boolean removeAll(java.util.Collection attrs)
    {
        return attrSet.removeAll(unwrapCollection(attrs));
    }


    /**
     * Retains only the elements that are contained
     * in the specified collection.
     */
    public boolean retainAll(java.util.Collection attrs)
    {
        return attrSet.retainAll(unwrapCollection(attrs));
    }

    /**
     * Returns the number of attributes in this set.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSet.html#size()">
            com.novell.ldap.LDAPAttributeSet.size()</a>
     */
    public int size()
    {
        return attrSet.size();
    }

    /**
     * Returns an array containing all the elements in this set.
     */
    public Object[] toArray()
    {
        LDAPAttribute[] attrs = new LDAPAttribute[attrSet.size()];
        return toArray(attrs);
    }

    /**
     * Returns an array containing all the elements in this set.
     *
     * <p>The runtime type of the returned array is that of the specified
     * </p>array.
     */
    public Object[] toArray( Object[] a)
    {
        // Throw ClassCastException if wrong type
        LDAPAttribute[] newAttrs = (LDAPAttribute[])a;

        com.novell.ldap.LDAPAttribute[] oldAttrs;
        oldAttrs = (com.novell.ldap.LDAPAttribute[])attrSet.toArray(
                new com.novell.ldap.LDAPAttribute[attrSet.size()]);

        int length = oldAttrs.length;

        if( newAttrs.length < length) {
            newAttrs = (LDAPAttribute[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), length);
        }

        int i = 0;
        for( i = 0; i < length; i++) {
            newAttrs[i] = new LDAPAttribute(oldAttrs[i]);
        }

        if( newAttrs.length > length) {
            newAttrs[i] = null;
        }
        return newAttrs;
    }
    
/**
 * An iterator that creates org.ietf.ldap.LDAPAttribute instances
 * from an iterator of com.novell.ldap.LDAPAttribute instances.
 */
private static final class WrappedIterator implements Iterator { 

    // Iterator containing com.novell.ldap.LDAPAttribute instances.
    private final Iterator iterator;

    WrappedIterator(final Iterator iterator) { 
       this.iterator = iterator;
    }

    public boolean hasNext() { 
      return this.iterator.hasNext();
}

    public Object next() { 
      com.novell.ldap.LDAPAttribute attr = 
        (com.novell.ldap.LDAPAttribute) this.iterator.next();
      return new LDAPAttribute(attr);
    }
    
    public void remove() {
    }
};

}



