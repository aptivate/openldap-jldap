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
 * Represents the definition of a specific matching rule use in the
 * directory schema.
 *
 * @see com.novell.ldap.LDAPMatchingRuleUseSchema
 */

public class LDAPMatchingRuleUseSchema
                extends LDAPSchemaElement
{
    private com.novell.ldap.LDAPMatchingRuleUseSchema schema;

    /**
     * Constructs LDAPMatchingRuleUseSchema from com.novell.ldap
     */
    /* package */
    LDAPMatchingRuleUseSchema( com.novell.ldap.LDAPMatchingRuleUseSchema schema)
    {
        super( schema);
        this.schema = schema;
        return;
    }

    /**
     * Constructs a matching rule use definition for adding to or deleting
     * from the schema.
     *
     * @see com.novell.ldap.LDAPMatchingRuleUseSchema#LDAPMatchingRuleUseSchema(
     *      String,String,String,boolean,String[],String[])
     */
    public LDAPMatchingRuleUseSchema(String name,
                                     String oid,
                                     String description,
                                     boolean obsolete,
                                     String[] attributes,
                                     String[] aliases)
    {
        super( new com.novell.ldap.LDAPMatchingRuleUseSchema( name,
                                                              oid,
                                                              description,
                                                              obsolete,
                                                              attributes,
                                                              aliases));
        schema = (com.novell.ldap.LDAPMatchingRuleUseSchema)getWrappedObject();
        return;
    }



    /**
     * Constructs a matching rule use definition from the raw string value
     * returned on a schema query for matchingRuleUse.
     *
     * @see com.novell.ldap.LDAPMatchingRuleUseSchema#LDAPMatchingRuleUseSchema(
            String)
     */
    public LDAPMatchingRuleUseSchema(String raw)
    {
        super( new com.novell.ldap.LDAPMatchingRuleUseSchema( raw));
        schema = (com.novell.ldap.LDAPMatchingRuleUseSchema)getWrappedObject();
        return;
    }

    /**
     * Returns an array of all the attributes which this matching rule
     * applies to.
     *
     * @see com.novell.ldap.LDAPMatchingRuleUseSchema#getAttributes()
     */
    public String[] getAttributes()
    {
        return schema.getAttributes();
    }
}
