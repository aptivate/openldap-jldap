package com.novell.ldap;

/**
 * Represents the definition of a specific DIT (Directory Information Tree)
 * structure rule in the directory schema.
 *  
 * <p>The LDAPDITStructureRuleSchema class is used to discover or modify which 
 * object classes a particular object class may be subordinate to in the DIT.</p>
 * 
 */

public class LDAPDITStructureRuleSchema
                extends LDAPSchemaElement
{
    /**  Constructs a DIT structure rule for adding to or deleting from the
     *   schema.
     *
     * @parameter name    The name of the structure rule.
     *<br><br>
     * @param ruleID      The unique identifier of the structure rule. NOTE:
     *                    this is an integer, not a dotted numerical
     *                    identifier. Structure rules aren't identified
     *                    by OID.
     *<br><br>
     * @param description An optional description of the structure rule.
     *<br><br>
     * @param obsolete    True if the structure rule is obsolete.
     *<br><br>
     * @param nameForm    Either the identifier or name of a name form.
     *                    This is used to indirectly refer to the object
     *                    class that this structure rule applies to.
     *<br><br>
     * @param superiorIDs A list of superior structure rules - specified
     *                    by their integer ID. The object class
     *                    specified by this structure rule (via the
     *                    nameForm parameter) may only be subordinate in
     *                    the DIT to object classes of those represented
     *                    by the structure rules here; it may be null.
     *<br><br>
     * @param aliases     An optional list of additional names by which the
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
     * Constructs a DIT structure rule from the raw string value returned from
     * a schema query for dITStructureRules.
     *
     * @param raw         The raw string value returned from a schema
     *                    query for dITStructureRules.
     */
    public LDAPDITStructureRuleSchema(String raw)
    {
        throw new RuntimeException("Class LDAPDITStructureRuleSchema not implemented");
    }

    /**
     * Returns the rule ID for this structure rule. 
     *
     * <p>The getRuleID method returns an integer rather than a dotted
     * decimal OID. Objects of this class do not have an OID,
     * thus getID can return null. </p>
     * 
     *
     * @return The rule ID for this structure rule.
     */

    public int getRuleID()
    {
        throw new RuntimeException("Method LDAPDITStructureRuleSchema.getRuleID not implemented");
    }

    /**
     * Returns the NameForm that this structure rule controls. 
     *
     * <p>You can get the actual object class that this structure rule controls
     *  by calling the getNameForm.getObjectClass method.</p>
     *
     * @return The NameForm that this structure rule controls.
     */
    public String getNameForm()
    {
        throw new RuntimeException("Method LDAPDITStructureRuleSchema.getNameForm not implemented");
    }

    /**
     * Returns a list of all structure rules that are superior to this
     * structure rule. 
     *
     * <p>To resolve to an object class, you need to first
     * resolve the superior ID to another structure rule, then call
     * the getNameForm.getObjectClass method on that structure rule.</p>
     *
     * @return A list of all structure rules that are superior to this structure rule.
     */
     public String[] getSuperiors()
     {
        throw new RuntimeException("Method LDAPDITStructureRuleSchema.getSuperiors not implemented");
     }
 }
