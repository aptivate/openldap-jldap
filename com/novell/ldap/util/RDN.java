/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/util/RDN.java,v 1.2 2001/03/14 19:25:59 cmorris Exp $
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

package com.novell.ldap.util;
import java.util.Vector;

/**
 * <P>A RDN encapsulates a single object's name of a Distinguished Name(DN).
 * The object name represented by this class contains no context.  Thus a
 * Relative Distinguished Name (RDN) could be relative to anywhere in the
 * Directories tree.</P>
 *
 * <P>For example, of following DN, 'cn=admin, ou=marketing, o=corporation', all
 * possible RDNs are 'cn=admin', 'ou=marketing', and 'o=corporation'.</P>
 *
 * <P>Multivalued attributes are encapsulated in this class.  For example the
 * following could be represented by an RDN: 'cn=john + l=US', or
 * 'cn=juan + l=ES' </P>
 *
 * @see DN
 */


public class RDN extends Object
{
    private Vector types;       //list of Type strings
    private Vector values;      //list of Value strings
    private String rawValue;    //the unnormalized value

    /**
     * Creates an RDN object from the DN component specified in the string RDN
     *
     * @param the DN component
     */
    public RDN(String RDN){
        throw new RuntimeException("RDN.equals not Implemented yet");
        //rawValue = RDN;
    }

    public RDN(){
        types    = new Vector();
        values   = new Vector();
        rawValue = "";
    }

    /**
     * Compares the RDN to rdn passed in.
     *
     * @param the RDN to compare to
     */
    public boolean equals(RDN rdn){
        throw new RuntimeException("RDN.equals not Implemented yet");
    }
    /**
     * Returns the actually Raw String before Normalization
     *
     * @return The raw string
     */
    protected String getRawValue()
    {
        return rawValue;
    }

    /**
     * Adds another value to the RDN.  Only one attribute type is allowed for
     * the RDN.
     * @param Attribute type, could be an OID or String
     * @param Attribute Value, must be normalized and escaped
     * @param rawValue or text before normalization, can be Null
     */
    public void add(String attrType, String attrValue, String rawValue){
        types.addElement(attrType);
        values.addElement(attrValue);
        this.rawValue += rawValue;
    }

    /**
     * Creates a string that represents this RDN, according to RFC 2253
     * @returns An RDN string
     */
    public String toString(){
        return toString(false);
    }

    /**
     * Creates a string that represents this RDN.  If noTypes if true then
     * Atribute types will be ommited.
     * @noTypes
     * @return An RDN string
     */
    public String toString(boolean noTypes){
        int length=types.size();
        String toReturn = "";
        if (length < 1)
            return null;
        if (!noTypes)
            toReturn = types.get(0) + "=";
        toReturn += values.get(0);

        for(int i=1; i<length; i++){
            toReturn += "+";
            if (!noTypes)
                toReturn += types.get(i) + "=";
            toReturn += values.get(i);
        }
        return toReturn;
    }
} //end class RDN
