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
 * Represents the schematic definition of a
 * particular attribute in a particular Directory Server.
 *
 * @see com.novell.ldap.LDAPAttributeSchema
 */
public class LDAPAttributeSchema extends LDAPSchemaElement
{

    private com.novell.ldap.LDAPAttributeSchema schema;
    /**
     * This attribute definition defines that the attribute usage
     * is applications.
     */
    public final static int USER_APPLICATIONS =
            com.novell.ldap.LDAPAttributeSchema.USER_APPLICATIONS;
    /**
     * This attribute definition defines the attribute usage
     * is directory operations.
     */
    public final static int DIRECTORY_OPERATION = 
            com.novell.ldap.LDAPAttributeSchema.DIRECTORY_OPERATION;
    /**
     * This attribute definition defines the attribute usage is shared dsa.
     */
    public final static int DISTRIBUTED_OPERATION = 
            com.novell.ldap.LDAPAttributeSchema.DISTRIBUTED_OPERATION;
    /**
     * This class definition defines the attribute usage is local dsa.
     */
    public final static int DSA_OPERATION = 
            com.novell.ldap.LDAPAttributeSchema.DSA_OPERATION;

    /**
     * Constructs LDAPAttributeSchema from com.novell.ldap.LDAPAttributeSchema
     */
    /* package */
    LDAPAttributeSchema( com.novell.ldap.LDAPAttributeSchema schema)
    {
        super( schema);
        this.schema = schema;
        return;
    }

    /**
     * Constructs an attribute definition for adding to or deleting from a
     * directory's schema.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#LDAPAttributeSchema(String,
             String,String,String, boolean,String,String[],boolean,
             String,String,String,boolean,boolean,int)
     */
    public LDAPAttributeSchema(String name, String oid, String description,
                  String syntaxString, boolean single,
                  String superior, String[] aliases,
                  boolean obsolete, String equality, String ordering,
                  String substring, boolean collective, boolean userMod,
                  int usage)
    {
        super( new com.novell.ldap.LDAPAttributeSchema(name, oid, description,
               syntaxString, single, superior, aliases, obsolete,
               equality, ordering, substring, collective, userMod, usage));
        schema = (com.novell.ldap.LDAPAttributeSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs an attribute definition from the raw string value returned
     * on a directory query for "attributetypes".
     *
     * @see com.novell.ldap.LDAPAttributeSchema#LDAPAttributeSchema(String)
     */
    public LDAPAttributeSchema(String raw)
    {
        super( new com.novell.ldap.LDAPAttributeSchema( raw));
        schema = (com.novell.ldap.LDAPAttributeSchema)getWrappedObject();
        return;
    }

    /**
     * Returns the object identifer of the syntax of the attribute, in
     * dotted numerical format.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#getSyntaxString()
     */
    public String getSyntaxString()
    {
        return schema.getSyntaxString();
    }

    /**
     * Returns the name of the attribute type which this attribute derives
     * from, or null if there is no superior attribute.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#getSuperior()
     */
    public String getSuperior()
    {
        return schema.getSuperior();
    }

    /**
     * Returns true if the attribute is single-valued.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#isSingleValued()
     */
    public boolean isSingleValued()
    {
        return schema.isSingleValued();
    }

    /**
     * Returns the matching rule for this attribute.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#getEqualityMatchingRule()
     */
    public String getEqualityMatchingRule()
    {
        return schema.getEqualityMatchingRule();
    }

    /**
     * Returns the ordering matching rule for this attribute.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#getOrderingMatchingRule()
     */

    public String getOrderingMatchingRule()
    {
        return schema.getOrderingMatchingRule();
    }

    /**
     * Returns the substring matching rule for this attribute.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#getSubstringMatchingRule()
     */

    public String getSubstringMatchingRule()
    {
        return schema.getSubstringMatchingRule();
    }

    /**
     * Returns true if the attribute is a collective attribute.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#isCollective()
     */

    public boolean isCollective()
    {
        return schema.isCollective();
    }

    /**
     * Returns false if the attribute is read-only.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#isUserModifiable()
     */

    public boolean isUserModifiable()
    {
        return schema.isUserModifiable();
    }

    /**
     * Returns the usage of the attribute.
     *
     * @see com.novell.ldap.LDAPAttributeSchema#getUsage()
     */

    public int getUsage()
    {
        return schema.getUsage();
    }
}
