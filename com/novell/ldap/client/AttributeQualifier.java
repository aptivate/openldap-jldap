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

import java.util.ArrayList;

/**
 * Encapsulates a qualifier in a Schema definition.  Definitions that are not
 * in rfc2252.  Begins with 'X-'
 */
public class AttributeQualifier implements java.io.Serializable {
    String name;
    ArrayList values;

    public AttributeQualifier( String name, String[] value )
    {
        if ( name == null || value == null ) {
            throw new java.lang.IllegalArgumentException(
                "A null name or value " +
                "was passed in for a schema definition qualifier");
        }
        this.name = name;
        values = new ArrayList(5);
        for( int i=0; i < value.length; i++) {
            values.add( value[i] );
        }
        return;
    }
    /*
    public void addValue( String value )
    {
        values.add( value );
        return;
    }
    */
    public final String getName()
    {
        return name;
    }
    public final String[] getValues()
    {
        String[] strValues = null;
        if( values.size() > 0 ) {
            strValues = new String[values.size()];
            for(int i = 0; i < values.size(); i++ ){
                strValues[i] = (String) values.get(i);
            }
        }
        return strValues;
    }

    /**
    *  Writes the object state to a stream in standard Default Binary format
    *  This function wraps ObjectOutputStream' s defaultWriteObject() to write
    *  the non-static and non-transient fields of the current class to the stream
    *   
    *  @param objectOStrm  The OutputSteam where the Object need to be written
    */
    private void writeObject(java.io.ObjectOutputStream objectOStrm)
	    throws java.io.IOException {
		objectOStrm.defaultWriteObject();
    }
    
    /**
    *  Reads the serialized object from the underlying input stream.
    *  This function wraps ObjectInputStream's  defaultReadObject() function
    *
    *  @param objectIStrm  InputStream used to recover those objects previously serialized. 
    */
    private void readObject(java.io.ObjectInputStream objectIStrm)
         throws java.io.IOException, ClassNotFoundException
    {
	  objectIStrm.defaultReadObject();
    }
}
