/* **************************************************************************
 * $OpenLDAP:
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
 *  Represents a specific a name form in the directory schema.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPNameFormSchema.html">
            com.novell.ldap.LDAPNameFormSchema</a>
 */

public class LDAPNameFormSchema
                extends LDAPSchemaElement
{
	private com.novell.ldap.LDAPNameFormSchema schema;

    /**
     * Constructs LDAPAttributeSchema from com.novell.ldap.LDAPAttributeSchema
     */
    /* package */
    LDAPNameFormSchema( com.novell.ldap.LDAPNameFormSchema schema)
    {
        super( schema);
        this.schema = schema;
        return;
    }

    /**
     * Constructs a name form for adding to or deleting from the schema.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPNameFormSchema.html#LDAPNameFormSchema(java.lang.String[], java.lang.String,
            java.lang.String, boolean, java.lang.String, java.lang.String[],
            java.lang.String[])">
            com.novell.ldap.LDAPNameFormSchema.LDAPNameFormSchema(
            String,String,String,boolean,String,String[],String[])</a>
     */
    public LDAPNameFormSchema(String[] names,
                              String oid,
                              String description,
                              boolean obsolete,
                              String objectClass,
                              String[] required,
                              String[] optional)
    {
        super( new com.novell.ldap.LDAPNameFormSchema( names,
                                                       oid,
                                                       description,
                                                       obsolete,
                                                       objectClass,
                                                       required,
                                                       optional));
        schema = (com.novell.ldap.LDAPNameFormSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs a DIT content rule from the raw string value returned on a
     * schema query for nameForms.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPNameFormSchema.html#LDAPNameFormSchema(java.lang.String)">
            com.novell.ldap.LDAPNameFormSchema.LDAPNameFormSchema(String)</a>
     */
    public LDAPNameFormSchema(String raw)
    {
        super( new com.novell.ldap.LDAPNameFormSchema( raw));
        schema = (com.novell.ldap.LDAPNameFormSchema)getWrappedObject();
        return;
    }

    /**
     * Returns the name of the object class which this name form applies to.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPNameFormSchema.html#getObjectClass()">
            com.novell.ldap.LDAPNameFormSchema.getObjectClass()</a>
     */
    public String getObjectClass()
    {
        return schema.getObjectClass();
    }


    /**
     * Returns the list of required naming attributes for an entry
     * controlled by this name form.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPNameFormSchema.html#getRequiredNamingAttributes()">
            com.novell.ldap.LDAPNameFormSchema.getRequiredNamingAttributes()</a>
     */
    public String[]getRequiredNamingAttributes()
    {
        return schema.getRequiredNamingAttributes();
    }

    /**
     * Returns the list of optional naming attributes for an entry
     * controlled by this content rule.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPNameFormSchema.html#getOptionalNamingAttributes()">
            com.novell.ldap.LDAPNameFormSchema.getOptionalNamingAttributes()</a>
     */
    public String[]getOptionalNamingAttributes()
    {
        return schema.getOptionalNamingAttributes();
    }
}
