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

/**
 *  Represents the schematic definition of a particular object class in
 *  a particular directory server.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html">
            com.novell.ldap.LDAPObjectClassSchema</a>
 */
public class LDAPObjectClassSchema extends LDAPSchemaElement
{
    private com.novell.ldap.LDAPObjectClassSchema schema;

    /**
     * This class definition defines an abstract schema class.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#ABSTRACT">
            com.novell.ldap.LDAPObjectClassSchema.ABSTRACT</a>
     */
     public final static int ABSTRACT =
                com.novell.ldap.LDAPObjectClassSchema.ABSTRACT;

    /**
     * This class definition defines a structural schema class.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#STRUCTURAL">
            com.novell.ldap.LDAPObjectClassSchema.STRUCTURAL</a>
     */
     public final static int STRUCTURAL =
                com.novell.ldap.LDAPObjectClassSchema.STRUCTURAL;

    /**
     * This class definition defines an auxiliary schema class.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#AUXILIARY">
            com.novell.ldap.LDAPObjectClassSchema.AUXILIARY</a>
     */
     public final static int AUXILIARY =
                com.novell.ldap.LDAPObjectClassSchema.AUXILIARY;

    /**
     * Constructs an object class definition for adding to or deleting from
     * a directory's schema.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#LDAPObjectClassSchema(java.lang.String[], java.lang.String,
            java.lang.String[], java.lang.String, java.lang.String[],
            java.lang.String[], int, boolean)">
            com.novell.ldap.LDAPObjectClassSchema.LDAPObjectClassSchema(
            String, String, String[], String, String[], String[], int, boolean)
            </a>
     */
    public LDAPObjectClassSchema(String[] names,
                                 String oid,
                                 String[] superiors,
                                 String description,
                                 String[] required,
                                 String[] optional,
                                 int type,
                                 boolean obsolete)
    {
        super( new com.novell.ldap.LDAPObjectClassSchema( names,
                                                          oid,
                                                          superiors,
                                                          description,
                                                          required,
                                                          optional,
                                                          type,
                                                          obsolete));
        schema = (com.novell.ldap.LDAPObjectClassSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs an object class definition from the raw string value
     * returned from a directory query for "objectClasses".
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#LDAPObjectClassSchema(java.lang.String)">
            com.novell.ldap.LDAPObjectClassSchema.LDAPObjectClassSchema(
            String)</a>
     */
    public LDAPObjectClassSchema(String raw)
    {
        super( new com.novell.ldap.LDAPObjectClassSchema( raw));
        schema = (com.novell.ldap.LDAPObjectClassSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs from com.novell.ldap.LDAPObjectClassSchema
     */
    /* package */
    LDAPObjectClassSchema( com.novell.ldap.LDAPObjectClassSchema schema)
    {
        super(schema);
        this.schema = schema;
        return;
    }

    /**
     * Returns the object classes from which this one derives.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#getSuperiors()">
            com.novell.ldap.LDAPObjectClassSchema.getSuperiors()</a>
     */
    public String[] getSuperiors()
    {
        return schema.getSuperiors();
    }

    /**
     * Returns a list of attributes required for an entry with this object
     * class.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#getRequiredAttributes()">
            com.novell.ldap.LDAPObjectClassSchema.getRequiredAttributes()</a>
     */
    public String[] getRequiredAttributes()
    {
        return schema.getRequiredAttributes();
    }

    /**
     * Returns a list of optional attributes but not required of an entry
     * with this object class.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#getOptionalAttributes()">
            com.novell.ldap.LDAPObjectClassSchema.getOptionalAttributes()</a>
     */
    public String[] getOptionalAttributes()
    {
        return schema.getOptionalAttributes();
    }

    /**
     * Returns the type of object class.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPObjectClassSchema.html#getType()">
            com.novell.ldap.LDAPObjectClassSchema.getType()</a>
     */
    public int getType()
    {
        return schema.getType();
    }
}
