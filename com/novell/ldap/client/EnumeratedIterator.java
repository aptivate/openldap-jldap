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
 /**
  * wrappers a class of type Iterator and makes it act as an Enumerator.  This
  * is used when the API requires enumerations be used but we may be using
  * JDK1.2 collections, which return iterators instead of enumerators.  Used by
  * LDAPSchema and LDAPSchemaElement
  *
  * @see com.novell.ldap.LDAPSchema#getAttributeSchemas
  * @see com.novell.ldap.LDAPSchemaElement#getQualifierNames
  */

public class EnumeratedIterator implements java.util.Enumeration
{
    private java.util.Iterator i;

    public EnumeratedIterator( java.util.Iterator iterator){
        i=iterator;
        return;
    }

    /**
     * Enumeration method that maps to Iterator.hasNext()
     */
    public final boolean hasMoreElements(){
        return i.hasNext();
    }

    /**
     * Enumeration method that maps to Iterator.next()
     */
    public final Object nextElement(){
        return i.next();
    }
}