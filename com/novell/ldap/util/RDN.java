/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/util/RDN.java,v 1.1 2001/03/09 23:15:49 cmorris Exp $
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
 * For example of following DN, 'cn=admin,o=corporation',  cn=admin, and
 * o=corporation are the only object names.</P>
 *
 * <P>Multivalued attributes are encapsulated in this class.  For example the
 * following could be represented by RDN: 'cn=robert + cn=bob + l=US' Because
 * they all name one object</P>
 *
 * <P>Note: While a Relative Distinguished Name may be any name that doesn't
 * include extend to the Root, This class represents only one object name.
 * Likewise the DN class may contain multiple object name but does not
 * necessarily extend to the Root.</P>
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

    public String toString(){
        return toString(false);
    }
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
