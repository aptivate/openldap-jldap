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
 ***************************************************************************/

package com.novell.ldap.client;

import com.novell.ldap.client.Debug;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This  class  extends the AbstractSet and Implements the Set
 * so that it can be used to maintain a list of currently
 * registered extended responses.
 */
public class RespExtensionSet extends java.util.AbstractSet
        implements java.util.Set
{

    private HashMap map;
    
    public RespExtensionSet()
    {
        super();
        map = new HashMap();        
        return;
    }


    /* Adds a responseExtension to the current list of registered responses.
     *
     */
    public final synchronized void registerResponseExtension(String oid, Class extClass)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.controls, "Registered Extension with OID " + oid
                    + " for class " + extClass.toString());
        }

       
        if( !this.map.containsKey(oid)){
            this.map.put(oid, (Class)extClass);
            }
        
    }


 	/**
     * Returns the number of extensions in this set.
     *
     * @return number of extensions in this set.
     */
    public int size(){
        return this.map.size();
    }
    
    /**
     * Returns an iterator over the responses in this set.  The responses
     * returned from this iterator are not in any particular order.
     *
     * @return iterator over the responses in this set
     */
    public Iterator iterator(){
        return this.map.values().iterator();
    }

    /* Searches the list of registered responses for a mathcing response.  We
     * search using the OID string.  If a match is found we return the
     * Class name that was provided to us on registration.
     */
    public final synchronized Class findResponseExtension(String searchOID)
                throws NoSuchFieldException
    {
    
        if( this.map.containsKey(searchOID))
        {
              return (Class)this.map.get(searchOID);
        }
        /* The requested extension does not have a registered response class */
		if( Debug.LDAP_DEBUG) {
			Debug.trace( Debug.controls, "Returned Extension did not match any registered extension.");
        }
        return null;
    }

 }
