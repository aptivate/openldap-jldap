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
 *  Represents ia specific DIT (Directory Information Tree) content rule
 *  in the directory schema.
 *
 * @see <a href="../../../../api/com/novell/ldap/LDAPDITContentRuleSchema.html">
            com.novell.ldap.LDAPDITContentRuleSchema</a>
 */
public class LDAPDITContentRuleSchema
                extends LDAPSchemaElement
{
    private com.novell.ldap.LDAPDITContentRuleSchema schema;

    /**
     * Constructs LDAPAttributeSchema from com.novell.ldap.LDAPAttributeSchema
     */
    /* package */
    LDAPDITContentRuleSchema( com.novell.ldap.LDAPDITContentRuleSchema schema)
    {
        super( schema);
        this.schema = schema;
        return;
    }

    /**
     * Constructs a DIT content rule for adding to or deleting from the
     * schema.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPDITContentRuleSchema.html#LDAPDITContentRuleSchema(java.lang.String[], java.lang.String,
            java.lang.String, boolean, java.lang.String[], java.lang.String[],
            java.lang.String[], java.lang.String[])">
            com.novell.ldap.LDAPDITContentRuleSchema.LDAPDITContentRuleSchema(
            String[], boolean, String, boolean, String[], String[], String[],
            String[])</a>
     */
    public LDAPDITContentRuleSchema(String[] names,
                                    String oid,
                                    String description,
                                    boolean obsolete,
                                    String[] auxiliary,
                                    String[] required,
                                    String[] optional,
                                    String[] precluded)
    {
        super( new com.novell.ldap.LDAPDITContentRuleSchema( names,
                                                          oid,
                                                          description,
                                                          obsolete,
                                                          auxiliary,
                                                          required,
                                                          optional,
                                                          precluded));
        schema = (com.novell.ldap.LDAPDITContentRuleSchema)getWrappedObject();
        return;
    }

    /**
     * Constructs a DIT content rule from the raw string value returned from a
     * schema query for DITContentRules.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPDITContentRuleSchema.html#LDAPDITContentRuleSchema(java.lang.String)">
            com.novell.ldap.LDAPDITContentRuleSchema.LDAPDITContentRuleSchema(
            String)</a>
     */
    public LDAPDITContentRuleSchema(String raw)
    {
        super( new com.novell.ldap.LDAPDITContentRuleSchema( raw));
        schema = (com.novell.ldap.LDAPDITContentRuleSchema)getWrappedObject();
        return;
    }

    /**
     * Returns the list of allowed auxiliary classes.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPDITContentRuleSchema.html#getAuxiliaryClasses()">
            com.novell.ldap.LDAPDITContentRuleSchema.getAuxiliaryClasses()</a>
     */
    public String[] getAuxiliaryClasses()
    {
        return schema.getAuxiliaryClasses();
    }

    /**
     * Returns the list of additional required attributes for an entry
     * controlled by this content rule.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPDITContentRuleSchema.html#getRequiredAttributes()">
            com.novell.ldap.LDAPDITContentRuleSchema.getRequiredAttributes()</a>
     */
    public String[] getRequiredAttributes()
    {
        return schema.getRequiredAttributes();
    }

    /**
     * Returns the list of additional optional attributes for an entry
     * controlled by this content rule.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPDITContentRuleSchema.html#getOptionalAttributes()">
            com.novell.ldap.LDAPDITContentRuleSchema.getOptionalAttributes()</a>
     */
    public String[] getOptionalAttributes()
    {
        return schema.getOptionalAttributes();
    }

    /**
     * Returns the list of precluded attributes for an entry controlled by
     * this content rule.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPDITContentRuleSchema.html#getPrecludedAttributes()">
            com.novell.ldap.LDAPDITContentRuleSchema.getPrecludedAttributes()</a>
     */
    public String[] getPrecludedAttributes()
    {
        return schema.getPrecludedAttributes();
    }
}
