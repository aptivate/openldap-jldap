/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPDITStructureRuleSchema.java,v 1.9 2001/04/23 21:09:30 cmorris Exp $
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
 * Represents the definition of a specific DIT (Directory Information Tree)
 * structure rule in the directory schema.
 *
 * @see com.novell.ldap.LDAPDITStructureRuleSchema
 */

public class LDAPDITStructureRuleSchema
                extends LDAPSchemaElement
{
	private com.novell.ldap.LDAPDITStructureRuleSchema schema;

    /**
     * Constructs LDAPAttributeSchema from com.novell.ldap.LDAPAttributeSchema
     */
    /* package */
    LDAPDITStructureRuleSchema(
                            com.novell.ldap.LDAPDITStructureRuleSchema schema)
    {
        super( schema);
        this.schema = schema;
        return;
    }

    /**  Constructs a DIT structure rule for adding to or deleting from the
     *   schema.
     *
     *@see com.novell.ldap.LDAPDITStructureRuleSchema#LDAPDITStructureRuleSchema(
            String,int,String,boolean,String,String[],String[])
     *
     */
    public LDAPDITStructureRuleSchema(String name,
                                      int ruleID,
                                      String description,
                                      boolean obsolete,
                                      String nameForm,
                                      String[] superiorIDs,
                                      String[] aliases)
    {

        super( new com.novell.ldap.LDAPDITStructureRuleSchema( name,
                                                               ruleID,
                                                               description,
                                                               obsolete,
                                                               nameForm,
                                                               superiorIDs,
                                                               aliases));
        schema = (com.novell.ldap.LDAPDITStructureRuleSchema)getWrappedObject();
		return;
    }

    /**
     * Constructs a DIT structure rule from the raw string value returned from
     * a schema query for dITStructureRules.
     *
     *@see com.novell.ldap.LDAPDITStructureRuleSchema#LDAPDITStructureRuleSchema(
            String)
     */
    public LDAPDITStructureRuleSchema(String raw)
    {
        super( new com.novell.ldap.LDAPDITStructureRuleSchema( raw));
        schema = (com.novell.ldap.LDAPDITStructureRuleSchema)getWrappedObject();
        return;
    }

    /**
     * Returns the rule ID for this structure rule.
     *
     * @see com.novell.ldap.LDAPDITStructureRuleSchema#getRuleID()
     */

    public int getRuleID()
    {
        return schema.getRuleID();
    }

    /**
     * Returns the NameForm that this structure rule controls.
     *
     * @see com.novell.ldap.LDAPDITStructureRuleSchema#getNameForm()
     */
    public String getNameForm()
    {
        return schema.getNameForm();
    }

    /**
     * Returns a list of all structure rules that are superior to this
     * structure rule.
     *
     * @see com.novell.ldap.LDAPDITStructureRuleSchema#getSuperiors()
     */
    public String[] getSuperiors()
    {
        return schema.getSuperiors();
    }

}
