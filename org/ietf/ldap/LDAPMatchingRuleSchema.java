/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPMatchingRuleSchema.java,v 1.17 2001/04/23 21:09:30 cmorris Exp $
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

package org.ietf.ldap;

/**
 *  Represents the schematic definition of a particular matching rule
 *  in a particular Directory Server.
 *
 * @see com.novell.ldap.LDAPMatchingRuleSchema
 */
public class LDAPMatchingRuleSchema extends LDAPSchemaElement
{

    private com.novell.ldap.LDAPMatchingRuleSchema schema;

    /**
     * Constructs LDAPMatchingRuleSchema from com.novell.ldap
     */
    /* package */
    LDAPMatchingRuleSchema( com.novell.ldap.LDAPMatchingRuleSchema schema)
    {
        super( schema);
        this.schema = schema;
        return;
    }

    /**
     * Constructs a matching rule definition for adding to or deleting from
     * a directory.
     *
     * @see com.novell.ldap.LDAPMatchingRuleSchema#LDAPMatchingRuleSchema(
            String,String,String,String[],String,String[])
     */
    public LDAPMatchingRuleSchema(String name,
                                  String oid,
                                  String description,
                                  String[] attributes,
                                  String syntaxString,
                                  String[] aliases)
    {
        super( new com.novell.ldap.LDAPMatchingRuleSchema( name,
                                                           oid,
                                                           description,
                                                           attributes,
                                                           syntaxString,
                                                           aliases));
        schema = (com.novell.ldap.LDAPMatchingRuleSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs a matching rule definition for adding to or deleting from
     * a directory.
     *
     * @see com.novell.ldap.LDAPMatchingRuleSchema#LDAPMatchingRuleSchema(
            String,String,String,boolean,String,String[])
     */
    public LDAPMatchingRuleSchema(String name,
                                 String oid,
                                 String description,
                                 boolean obsolete,
                                 String syntaxString,
                                 String[] aliases)
    {
        super( new com.novell.ldap.LDAPMatchingRuleSchema( name,
                                                           oid,
                                                           description,
                                                           obsolete,
                                                           syntaxString,
                                                           aliases));
        schema = (com.novell.ldap.LDAPMatchingRuleSchema)getWrappedObject();
        return;
    }


    /**
     * Constructs a matching rule definition from the raw string values
     * returned from a schema query for "matchingRule" and for
     * "matchingRuleUse" for the same rule.
     *
     * @see com.novell.ldap.LDAPMatchingRuleSchema#LDAPMatchingRuleSchema(
            String,String)
     */
    public LDAPMatchingRuleSchema(String rawMatchingRule,
                                  String rawMatchingRuleUse)
    {
        super( new com.novell.ldap.LDAPMatchingRuleSchema( rawMatchingRule,
                                                           rawMatchingRuleUse));
        schema = (com.novell.ldap.LDAPMatchingRuleSchema)getWrappedObject();
        return;
    }

    /**
     * Returns the OIDs of the attributes to which this rule applies.
     *
     * @see com.novell.ldap.LDAPMatchingRuleSchema#getAttributes()
     */
    public String[] getAttributes() {
        return schema.getAttributes();
    }

    /**
     * Returns the OID of the syntax that this matching rule is valid for.
     *
     * @see com.novell.ldap.LDAPMatchingRuleSchema#getSyntaxString()
     */
    public String getSyntaxString() {
        return schema.getSyntaxString();
    }
}
