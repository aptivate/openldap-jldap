/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.novell.ldap.client.EnumeratedIterator;


/**
 *
 * A set of {@link LDAPAttribute} objects.
 *
 * <p>An <tt>LDAPAttributeSet</tt> is a collection of <tt>LDAPAttribute</tt>
 * classes as returned from an <tt>LDAPEntry</tt> on a search or read
 * operation. <tt>LDAPAttributeSet</tt> may be also used to contruct an entry
 * to be added to a directory.  If the <tt>add()</tt> or <tt>addAll()</tt>
 * methods are called and one or more of the objects to be added is not
 * an <tt>LDAPAttribute, ClassCastException</tt> is thrown (as discussed in the
 * documentation for <tt>java.util.Collection</tt>).
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/AddEntry.java.html">AddEntry.java</p>
 *
 * @see LDAPAttribute
 * @see LDAPEntry
 */
public class LDAPAttributeSet
        extends java.util.AbstractSet
        implements Cloneable, java.util.Set {

    /**
     * This is the underlying data structure for this set.
     * <p>HashSet is similar to the functionality of this set.  The difference
     * is we use the name of an attribute as keys in the Map and LDAPAttributes
     * as the values.  We also do not declare the map as transient, making the
     * map serializable.</p>
     */
    private HashMap map;

    /**
     * Constructs an empty set of attributes.
     */
    public LDAPAttributeSet() {
        super();
        map = new HashMap();
    }

// ---  methods not defined in Set ---

    /**
     * Returns a deep copy of this attribute set.
     *
     * @return A deep copy of this attribute set.
     */
    public Object clone() {
        LDAPAttributeSet newAttrs = new LDAPAttributeSet();
        Iterator i = this.iterator();
        while (i.hasNext()){
            newAttrs.add( ((LDAPAttribute)i.next()).clone() );
        }
        return newAttrs;
    }

    /**
     * Returns the attribute matching the specified attrName.
     *
     * <p>For example:</p>
     * <ul>
     * <li><tt>getAttribute("cn")</tt>      returns only the "cn" attribute</li>
     * <li><tt>getAttribute("cn;lang-en")</tt> returns only the "cn;lang-en"
     *                                 attribute.</li>
     * </ul>
     * <p>In both cases, <tt>null</tt> is returned if there is no exact match to
     * the specified attrName.</p>
     *
     * <p>Note: Novell eDirectory does not currently support language subtypes.
     * It does support the "binary" subtype.</p>
     *
     * @param attrName   The name of an attribute to retrieve, with or without
     * subtype specifications. For example, "cn", "cn;phonetic", and
     * "cn;binary" are valid attribute names.
     *
     * @return The attribute matching the specified attrName, or <tt>null</tt>
     * if there is no exact match.
     */
    public LDAPAttribute getAttribute(String attrName) {
        return (LDAPAttribute)map.get(attrName.toUpperCase());
    }

    /**
     * Returns a single best-match attribute, or <tt>null</tt> if no match is
     * available in the entry.
     *
     * <p>LDAP version 3 allows adding a subtype specification to an attribute
     * name. For example, "cn;lang-ja" indicates a Japanese language
     * subtype of the "cn" attribute and "cn;lang-ja-JP-kanji" may be a subtype
     * of "cn;lang-ja". This feature may be used to provide multiple
     * localizations in the same directory. For attributes which do not vary
     * among localizations, only the base attribute may be stored, whereas
     * for others there may be varying degrees of specialization.</p>
     *
     * <p>For example, <tt>getAttribute(attrName,lang)</tt> returns the
     * <tt>LDAPAttribute</tt> that exactly matches attrName and that
     * best matches lang.</p>
     *
     * <p>If there are subtypes other than "lang" subtypes included
     * in attrName, for example, "cn;binary", only attributes with all of
     * those subtypes are returned. If lang is <tt>null</tt> or empty, the
     * method behaves as getAttribute(attrName). If there are no matching
     * attributes, <tt>null</tt> is returned. </p>
     *
     *
     * <p>Assume the entry contains only the following attributes:</p>
     *
     *  <ul>
     *  <li>cn;lang-en</li>
     *  <li>cn;lang-ja-JP-kanji</li>
     *  <li>sn</li>
     *  </ul>
     *
     *  <p>Examples:</p>
     *  <ul>
     *  <li><tt>getAttribute( "cn" )</tt>       returns <tt>null</tt>.</li>
     *  <li><tt>getAttribute( "sn" )</tt>       returns the "sn" attribute.</li>
     *  <li><tt>getAttribute( "cn", "lang-en-us" )</tt>
     *                              returns the "cn;lang-en" attribute.</li>
     *   <li><tt>getAttribute( "cn", "lang-en" )</tt>
     *                              returns the "cn;lang-en" attribute.</li>
     *   <li><tt>getAttribute( "cn", "lang-ja" )</tt>
     *                              returns <tt>null</tt>.</li>
     *   <li><tt>getAttribute( "sn", "lang-en" )</tt>
     *                              returns the "sn" attribute.</li>
     *  </ul>
     *
     * <p>Note: Novell eDirectory does not currently support language subtypes.
     * It does support the "binary" subtype.</p>
     *
     * @param attrName  The name of an attribute to retrieve, with or without
     * subtype specifications. For example, "cn", "cn;phonetic", and
     * cn;binary" are valid attribute names.
     *<br><br>
     * @param lang   A language specification with optional subtypes
     * appended using "-" as separator. For example, "lang-en", "lang-en-us",
     * "lang-ja", and "lang-ja-JP-kanji" are valid language specification.
     *
     * @return A single best-match <tt>LDAPAttribute</tt>, or <tt>null</tt>
     * if no match is found in the entry.
     *
     */
    public LDAPAttribute getAttribute(String attrName, String lang) {
        String key = attrName + ";" + lang;
        return (LDAPAttribute)map.get(key.toUpperCase());
    }

    /**
     * Creates a new attribute set containing only the attributes that have
     * the specified subtypes.
     *
     * <p>For example, suppose an attribute set contains the following
     * attributes:</p>
     *
     * <ul>
     * <li>    cn</li>
     * <li>    cn;lang-ja</li>
     * <li>    sn;phonetic;lang-ja</li>
     * <li>    sn;lang-us</li>
     * </ul>
     *
     * <p>Calling the <tt>getSubset</tt> method and passing lang-ja as the
     * argument, the method returns an attribute set containing the following
     * attributes:</p>
     *
     * <ul>
     *     <li>cn;lang-ja</li>
     *     <li>sn;phonetic;lang-ja</li>
     * </ul>
     *
     *  @param subtype    Semi-colon delimited list of subtypes to include. For
     *  example:
     * <ul>
     * <li> "lang-ja" specifies only Japanese language subtypes</li>
     * <li> "binary" specifies only binary subtypes</li>
     * <li> "binary;lang-ja" specifies only Japanese language subtypes
     *       which also are binary</li>
     * </ul>
     *
     * <p>Note: Novell eDirectory does not currently support language subtypes.
     * It does support the "binary" subtype.</p>
     *
     * @return An attribute set containing the attributes that match the
     *          specified subtype.
     */
    public LDAPAttributeSet getSubset(String subtype) {

        // Create a new tempAttributeSet
        LDAPAttributeSet tempAttributeSet = new LDAPAttributeSet();
        Iterator i = this.iterator();

        // Cycle throught this.attributeSet
        while (i.hasNext()){
            LDAPAttribute attr = (LDAPAttribute)i.next();

            // Does this attribute have the subtype we are looking for. If
            // yes then add it to our AttributeSet, else next attribute
            if (attr.hasSubtype(subtype))
                tempAttributeSet.add(attr.clone());
        }
        return tempAttributeSet;
    }

// --- methods defined in set ---

    /**
     * Returns an iterator over the attributes in this set.  The attributes
     * returned from this iterator are not in any particular order.
     *
     * @return iterator over the attributes in this set
     */
    public Iterator iterator(){
        return this.map.values().iterator();
    }

    /**
     * Returns the number of attributes in this set.
     *
     * @return number of attributes in this set.
     */
    public int size(){
        return this.map.size();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements
     *
     * @return <tt>true</tt> if this set contains no elements
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set contains an attribute of the same name
     * as the specified attribute.
     *
     * @param attr   Object of type <tt>LDAPAttribute</tt>
     *
     * @return <tt>true</tt> if this set contains the specified attribute
     *
     * @throw <tt>ClassCastException</tt> occurs the specified Object
     * is not of type <tt>LDAPAttribute</tt>.
     */
    public boolean contains(Object attr) {
        LDAPAttribute attribute = (LDAPAttribute) attr;
        return this.map.containsKey( attribute.getName().toUpperCase() );
    }

    /**
     * Adds the specified attribute to this set if it is not already present.
     * <p>If an attribute with the same name already exists in the set then the
     * specified attribute will not be added.</p>
     *
     * @param attr   Object of type <tt>LDAPAttribute</tt>
     *
     * @return <tt>true</tt> if the attribute was added.
     *
     * @throw <tt>ClassCastException</tt> occurs the specified Object
     * is not of type <tt>LDAPAttribute</tt>.
     */
    public boolean add(Object attr) {
        //We must enforce that attr is an LDAPAttribute
        LDAPAttribute attribute = (LDAPAttribute) attr;
        return (this.map.put(attribute.getName().toUpperCase(),
                             attribute) != null);
    }

    /**
     * Removes the specified object from this set if it is present.
     *
     * <p>If the specified object is of type <tt>LDAPAttribute</tt>, the
     * specified attribute will be removed.  If the specified object is of type
     * <tt>String</tt>, the attribute with a name that matches the string will
     * be removed.</p>
     *
     * @param <tt>LDAPAttribute</tt> to be removed or <tt>String</tt> naming
     * the attribute to be removed.
     *
     * @return <tt>true</tt> if the object was removed.
     *
     * @throw <tt>ClassCastException</tt> occurs the specified Object
     * is not of type <tt>LDAPAttribute</tt> or of type <tt>String</tt>.
     */
    public boolean remove(Object object) {
        String attributeName; //the name is the key to object in the HashMap
        if (object instanceof String){
            attributeName = (String)object;
        }
        else {
            attributeName = ((LDAPAttribute) object).getName();
        }
        if (attributeName == null){
            return false;
        }
        return (this.map.remove( attributeName.toUpperCase() ) != null );
    }

    /**
     * Removes all of the elements from this set.
     */
    public void clear(){
        this.map.clear();
    }

    /**
     * Adds all <tt>LDAPAttribute</tt> objects in the specified collection to
     * this collection.
     *
     * @param c  Collection of <tt>LDAPAttribute</tt> objects.
     *
     * @throw <tt>ClassCastException</tt> occurs when an element in the
     * collection is not of type <tt>LDAPAttribute</tt>.
     *
     * @return <tt>true</tt> if this set changed as a result of the call.
     */
    public boolean addAll(java.util.Collection c){
        boolean setChanged = false;
        Iterator i = c.iterator();

        while (i.hasNext()){
            // we must enforce that everything in c is an LDAPAttribute
            // add will return true if the attribute was added
            if (this.add( (LDAPAttribute) i.next() )){
                setChanged = true;
            }
        }
        return setChanged;
    }

// --- deprecated methods ---

    /**
     * This method is removed in the IETF draft 18 of the
     * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will
     * be removed from the LDAP Classes for Java API in the fall of 2003.
     * @deprecated replaced by {@link #iterator}.
     */
    public java.util.Enumeration getAttributes() {
        return new com.novell.ldap.client.EnumeratedIterator(
                this.iterator());
    }

    /**
     * This method is removed in the IETF draft 18 of the
     * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will
     * be removed from the LDAP Classes for Java API in the fall of 2003.
     * @deprecated replaced by {@link #iterator}.
     */
    public LDAPAttribute elementAt(int index)
            throws ArrayIndexOutOfBoundsException {
        return (LDAPAttribute) this.toArray()[index];
    }

    /**
     * This method is removed in the IETF draft 18 of the
     * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will
     * be removed from the LDAP Classes for Java API in the fall of 2003.
     * @deprecated replaced by {@link #remove}.
     */
    public void removeElementAt(int index)
            throws ArrayIndexOutOfBoundsException {
        this.remove(this.elementAt(index));
        return;
    }

    /**
     * Returns a string representation of this LDAPAttributeSet
     *
     * @return a string representation of this LDAPAttributeSet
     */ 
    public String toString()
    {
        StringBuffer retValue = new StringBuffer("LDAPAttributeSet: ");
        Iterator attrs = iterator();
        boolean first = true;
        while( attrs.hasNext()) {
            if( ! first) {
                retValue.append(" ");
            }
            first = false;
            LDAPAttribute attr = (LDAPAttribute)attrs.next();
            retValue.append(attr.toString());
        }    
        return retValue.toString();
    }
}
