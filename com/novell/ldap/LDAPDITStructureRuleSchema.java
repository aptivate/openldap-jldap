package com.novell.ldap;

/** 
 * 4.10 public class LDAPDITStructureRuleSchema
 * extends LDAPSchemaElement
 */

/**
 * The LDAPDITStructureRuleSchema class represents the definition of a
 * DIT Structure Rule. It is used to discover or modify which object
 * classes a particular object class may be subordinate to in the DIT.
 * See RFC2252 for a description of DIT structure rule representation in
 * LDAP.
 */

public class LDAPDITStructureRuleSchema
                extends LDAPSchemaElement
{
    /**  Constructs a DIT structure rule for adding to or deleting from the
     * schema.
     *
     * @parameter name    Name of the structure rule.
     *
     * @param ruleID      Unique identifier of the structure rule. NOTE:
     *                    this is an integer, not a dotted numerical
     *                    identifier. Structure rules aren't identified
     *                    by OID.
     *
     * @param description Optional description of the structure rule.
     *
     * @param obsolete    true if the structure rule is obsolete.
     *
     * @param nameForm    Either the identifier or name of a name form.
     *                    This is used to indirectly refer to the object
     *                    class that this structure rule applies to.
     *
     * @param superiorIDs List of superior structure rules - specified
     *                    by their integer ID. The object class
     *                    specified by this structure rule (via the
     *                    nameForm parameter) may only be subordinate in
     *                    the DIT to object classes of those represented
     *                    by the structure rules here; may be null.
     *
     * @param aliases     Optional list of additional names by which the
     *                    structure rule may be known; null if there are
     *                    no aliases.
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
        throw new RuntimeException("Class LDAPDITStructureRuleSchema not implemented");
    }

    /** 
     * Constructs a DIT structure rule from the raw String value returned on
     * a schema query for "dITStructureRules".
     *
     * @param raw         The raw String value returned on a Directory
     *                    query for "dITStructureRules".
     */
    public LDAPDITStructureRuleSchema(String raw)
    {
        throw new RuntimeException("Class LDAPDITStructureRuleSchema not implemented");
    }

    /**
     * Returns the rule ID for this structure rule. Note that this returns
     * an integer rather than a dotted decimal OID. Objects of this class do
     * not have an OID, thus getID will return null.
     * public int getRuleID()
     *
     * @return the rule ID for this structure rule.
     */

    public int getRuleID()
    {
        throw new RuntimeException("Method LDAPDITStructureRuleSchema.getRuleID not implemented");
    }

    /**
     * Returns the NameForm that this structure rule controls. You can get
     * the actual object class that this structure rule controls by calling
     * getNameForm().getObjectClass().
     * public String getNameForm()
     *
     * @return the NameForm that this structure rule controls.
     */
    public String getNameForm()
    {
        throw new RuntimeException("Method LDAPDITStructureRuleSchema.getNameForm not implemented");
    }

    /**
     * Returns a list of all structure rules that are superior to this
     * structure rule. To resolve to an object class, you need to first
     * resolve the superior id to another structure rule, then call
     * getNameForm().getObjectClass() on that structure rule.
     *
     * @return list of all structure rules that are superior to this structure rule.
     */
     public String[] getSuperiors()
     {
        throw new RuntimeException("Method LDAPDITStructureRuleSchema.getSuperiors not implemented");
     }
 }
