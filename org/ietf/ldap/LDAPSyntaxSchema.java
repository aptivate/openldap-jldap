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
 * Represents a specific syntax definition in the directory schema.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSyntaxSchema.html">
            com.novell.ldap.LDAPSyntaxSchema</a>
 */

public class LDAPSyntaxSchema
                extends LDAPSchemaElement
{
    com.novell.ldap.LDAPSyntaxSchema schema;

    /**
     * Constructs LDAPAttributeSchema from com.novell.ldap.LDAPAttributeSchema
     */
    /* package */
    LDAPSyntaxSchema( com.novell.ldap.LDAPSyntaxSchema schema)
    {
        super( schema);
        this.schema = schema;
        return;
    }

    /**
     * Constructs a syntax for adding to or deleting from the schema.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSyntaxSchema.html#LDAPSyntaxSchema(java.lang.String, java.lang.String)">
            com.novell.ldap.LDAPSyntaxSchema.LDAPSyntaxSchema(String,
            String)</a>
     */
    public LDAPSyntaxSchema(String oid,
                            String description)
    {
        super( new com.novell.ldap.LDAPSyntaxSchema( oid, description));
        schema = (com.novell.ldap.LDAPSyntaxSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs a syntax from the raw string value returned on a schema
     * query for LDAPSyntaxes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSyntaxSchema.html#LDAPSyntaxSchema(java.lang.String)">
            com.novell.ldap.LDAPSyntaxSchema.LDAPSyntaxSchema(String)</a>
     */
    public LDAPSyntaxSchema(String raw)
    {
        super( new com.novell.ldap.LDAPSyntaxSchema( raw));
        schema = (com.novell.ldap.LDAPSyntaxSchema)getWrappedObject();
        return;
    }
}
