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
 * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html">
            com.novell.ldap.LDAPAttributeSchema</a>
 */
public class LDAPAttributeSchema extends LDAPSchemaElement
{

    private com.novell.ldap.LDAPAttributeSchema schema;
    /**
     * Indicates that the attribute usage is for ordinary application
     * or user data.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#USER_APPLICATIONS">
            com.novell.ldap.LDAPAttributeSchema.USER_APPLICATIONS</a>
     */
    public final static int USER_APPLICATIONS =
            com.novell.ldap.LDAPAttributeSchema.USER_APPLICATIONS;
    /**
     * Indicates that the attribute usage is for directory operations.
     * Values are vendor specific.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#DIRECTORY_OPERATION">
            com.novell.ldap.LDAPAttributeSchema.DIRECTORY_OPERATION</a>
     */
    public final static int DIRECTORY_OPERATION =
            com.novell.ldap.LDAPAttributeSchema.DIRECTORY_OPERATION;
    /**
     * Indicates that the attribute usage is for distributed operational
     * attributes. These hold server (DSA) information that is shared among
     * servers holding replicas of the entry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#DISTRIBUTED_OPERATION">
            com.novell.ldap.LDAPAttributeSchema.DISTRIBUTED_OPERATION</a>
     */
    public final static int DISTRIBUTED_OPERATION =
            com.novell.ldap.LDAPAttributeSchema.DISTRIBUTED_OPERATION;
    /**
     * Indicates that the attribute usage is for local operational attributes.
     * These hold server (DSA) information that is local to a server.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#DSA_OPERATION">
            com.novell.ldap.LDAPAttributeSchema.DSA_OPERATION</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#LDAPAttributeSchema(java.lang.string[], java.lang.String,
            java.lang.String, java.lang.String, boolean, java.lang.String,
            boolean, java.lang.String, java.lang.String,
            java.lang.String, boolean, boolean, int)">
            com.novell.ldap.LDAPAttributeSchema.LDAPAttributeSchema(
                  String[], String, String, String, boolean, String
                  boolean, String, String, String, boolean, boolean, int)</a>
     */
    public LDAPAttributeSchema(String[] names, String oid, String description,
                  String syntaxString, boolean single,
                  String superior,
                  boolean obsolete, String equality, String ordering,
                  String substring, boolean collective, boolean userMod,
                  int usage)
    {
        super( new com.novell.ldap.LDAPAttributeSchema(names, oid, description,
               syntaxString, single, superior, obsolete,
               equality, ordering, substring, collective, userMod, usage));
        schema = (com.novell.ldap.LDAPAttributeSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs an attribute definition from the raw string value returned
     * on a directory query for "attributetypes".
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#LDAPAttributeSchema(java.lang.string)">
            com.novell.ldap.LDAPAttributeSchema.LDAPAttributeSchema(String)</a>
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
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#getSyntaxString()">
            com.novell.ldap.LDAPAttributeSchema.getSyntaxString()</a>
     */
    public String getSyntaxString()
    {
        return schema.getSyntaxString();
    }

    /**
     * Returns the name of the attribute type which this attribute derives
     * from, or null if there is no superior attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#getSuperior()">
            com.novell.ldap.LDAPAttributeSchema.getSuperior()</a>
     */
    public String getSuperior()
    {
        return schema.getSuperior();
    }

    /**
     * Returns true if the attribute is single-valued.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#isSingleValued()">
            com.novell.ldap.LDAPAttributeSchema.isSingleValued()</a>
     */
    public boolean isSingleValued()
    {
        return schema.isSingleValued();
    }

    /**
     * Returns the matching rule for this attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#getEqualityMatchingRule()">
            com.novell.ldap.LDAPAttributeSchema.getEqualityMatchingRule()</a>
     */
    public String getEqualityMatchingRule()
    {
        return schema.getEqualityMatchingRule();
    }

    /**
     * Returns the ordering matching rule for this attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#getOrderingMatchingRule()">
            com.novell.ldap.LDAPAttributeSchema.getOrderingMatchingRule()</a>
     */
    public String getOrderingMatchingRule()
    {
        return schema.getOrderingMatchingRule();
    }

    /**
     * Returns the substring matching rule for this attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#getSubstringMatchingRule()">
            com.novell.ldap.LDAPAttributeSchema.getSubstringMatchingRule()</a>
     */
    public String getSubstringMatchingRule()
    {
        return schema.getSubstringMatchingRule();
    }

    /**
     * Returns true if the attribute is a collective attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#isCollective()">
            com.novell.ldap.LDAPAttributeSchema.isCollective()</a>
     */
    public boolean isCollective()
    {
        return schema.isCollective();
    }

    /**
     * Returns false if the attribute is read-only.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#isUserModifiable()">
            com.novell.ldap.LDAPAttributeSchema.isUserModifiable()</a>
     */
    public boolean isUserModifiable()
    {
        return schema.isUserModifiable();
    }

    /**
     * Returns the usage of the attribute.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPAttributeSchema.html#getUsage()">
            com.novell.ldap.LDAPAttributeSchema.getUsage()</a>
     */
    public int getUsage()
    {
        return schema.getUsage();
    }
}
