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

package com.novell.ldap.util;
import java.util.Vector;
import java.util.ArrayList;

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
    private ArrayList types;       //list of Type strings
    private ArrayList values;      //list of Value strings
    private String rawValue;       //the unnormalized value

    /**
     * Creates an RDN object from the DN component specified in the string RDN
     *
     * @param rdn the DN component
     */
    public RDN(String rdn){
        rawValue = rdn;
        DN dn = new DN(rdn);
        Vector rdns = dn.getRDNs();
        //there should only be one rdn
        if (rdns.size() != 1)
            throw new IllegalArgumentException("Invalid RDN: see API " +
                "documentation");
        RDN thisRDN   = (RDN)(rdns.elementAt(0));
        this.types    = thisRDN.types;
        this.values   = thisRDN.values;
        this.rawValue = thisRDN.rawValue;
        return;
    }

    public RDN(){
        types    = new ArrayList();
        values   = new ArrayList();
        rawValue = "";
        return;
    }

    /**
     * Compares the RDN to the rdn passed.  Note: If an there exist any
     * mulivalues in one RDN they must all be present in the other.
     *
     * @param rdn the RDN to compare to
     *
     * @throws IllegalArgumentException if the application compares a name
     * with an OID.
     */
    public boolean equals(RDN rdn){
        if (this.values.size() != rdn.values.size()){
            return false;
        }
        int j,i;
        for (i=0; i<this.values.size(); i++){
            //verify that the current value and type exists in the other list
            j=0;
            //May need a more intellegent compare
            while ( j<values.size() && (
                    !((String)this.values.get(i)).equalsIgnoreCase(
                      (String) rdn.values.get(j)) ||
                    !equalAttrType((String)this.types.get(i),
                                   (String) rdn.types.get(j)))){
                j++;
            }
            if (j >= rdn.values.size()) //couldn't find first value
                return false;
        }
        return true;
    }

    /**
     * Internal function used by equal to compare Attribute types.  Because
     * attribute types could either be an OID or a name.  There needs to be a
     * Translation mechanism.  This function will absract this functionality.
     *
     * Currently if types differ (Oid and number) then UnsupportedOperation is
     * thrown, either one or the other must used.  In the future an OID to name
     * translation can be used.
     *
     *
     */
    private boolean equalAttrType(String attr1, String attr2){
        if (java.lang.Character.isDigit(attr1.charAt(0))  ^ //XOR
            java.lang.Character.isDigit(attr2.charAt(0)) )
            //isDigit tests if it is an OID
            throw new IllegalArgumentException("OID numbers are not " +
                "currently compared to attribute names");

        return attr1.equalsIgnoreCase(attr2);
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
     * @param attrType Attribute type, could be an OID or String
     * @param attrValue Attribute Value, must be normalized and escaped
     * @param rawValue or text before normalization, can be Null
     */
    public void add(String attrType, String attrValue, String rawValue){
        types.add(attrType);
        values.add(attrValue);
        this.rawValue += rawValue;
    }

    /**
     * Creates a string that represents this RDN, according to RFC 2253
     *
     * @return An RDN string
     */
    public String toString(){
        return toString(false);
    }

    /**
     * Creates a string that represents this RDN.
     *
     * If noTypes is true then Atribute types will be ommited.
     *
     * @param noTypes true if attribute types will be omitted.
     *
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

    /**
     * Returns each multivalued name in the current RDN as an array of Strings.
     *
     * @param noTypes Specifies whether Attribute types are included. The attribute
     *  type names will be ommitted if the parameter noTypes is true.
     *
     * @return List of multivalued Attributes
     */
    public String[] explodeRDN( boolean noTypes ){
        int length=types.size();
        if (length < 1)
            return null;
        String[] toReturn = new String[types.size()];

        if (!noTypes)
            toReturn[0] = types.get(0) + "=";
        toReturn[0] += values.get(0);

        for(int i=1; i<length; i++){
            if (!noTypes)
                toReturn[i] += types.get(i) + "=";
            toReturn[i] += values.get(i);
        }

        return toReturn;
    }

    /**
     * Returns the type of this RDN.  This method assumes that only one value
     * is used, If multivalues attributes are used only the first Type is
     * returned.  Use GetTypes.
     * @return Type of attribute
     */
    public String getType(){
        return (String)types.get(0);
    }

    /**
     * Returns all the types for this RDN.
     * @return list of types
     */
     public String[] getTypes(){
        String[] toReturn = new String[types.size()];
        for(int i=0; i<types.size(); i++)
            toReturn[i] = (String)types.get(i);
        return toReturn;
     }

    /**
     * Returns the values of this RDN.  If multivalues attributes are used only
     * the first Type is returned.  Use GetTypes.
     *
     * @return Type of attribute
     */
    public String getValue(){
        return (String)values.get(0);
    }

    /**
     * Returns all the types for this RDN.
     * @return list of types
     */
     public String[] getValues(){
        String[] toReturn = new String[values.size()];
        for(int i=0; i<values.size(); i++)
            toReturn[i] = (String)values.get(i);
        return toReturn;
     }

     /**
      * Determines if this RDN is multivalued or not
      * @return true if this RDN is multivalued
      */
     public boolean isMultivalued(){
        return (values.size() > 1)? true: false;
     }

} //end class RDN
