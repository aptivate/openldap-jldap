/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSchema.java,v 1.22 2001/04/23 21:09:31 cmorris Exp $
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

import java.util.Enumeration;

/**
 *  Represents the schema of a particular directory server.
 *
 * @see com.novell.ldap.LDAPSchema
 */
public class LDAPSchema
{

    private com.novell.ldap.LDAPSchema schema;

    /**
     * Constructs an empty LDAPSchema object.
     *
     * @see com.novell.ldap.LDAPSchema#LDAPSchema()
     */
    public LDAPSchema()
    {
        schema = new com.novell.ldap.LDAPSchema();
        return;
    }

    /**
     * Retrieves the entire schema from a directory server.
     *
     * @see com.novell.ldap.LDAPSchema#fetchSchema(LDAPConnection)
     */
    public void fetchSchema(LDAPConnection ld) throws LDAPException
    {
		try {
            if( ld == null) {
			    schema.fetchSchema((com.novell.ldap.LDAPConnection)null);
            } else {
			    schema.fetchSchema(ld.getWrappedObject());
            }
            return;
		}
		catch(com.novell.ldap.LDAPException e) {
			throw new LDAPException(e);
		}
    }

    /**
     * Retrieves the schema in effect at a particular entry in the directory
     * server.
     *
     * @see com.novell.ldap.LDAPSchema#fetchSchema(LDAPConnection,String)
     */
    public void fetchSchema(LDAPConnection ld,
                            String dn) throws LDAPException
    {
		try {
            if( ld == null) {
			    schema.fetchSchema((com.novell.ldap.LDAPConnection)null,dn);
            } else {
			    schema.fetchSchema(ld.getWrappedObject(),dn);
            }
		}
		catch(com.novell.ldap.LDAPException e) {
			throw new LDAPException(e);
		}
    }

    /**
     * Returns a particular attribute definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getAttribute(String)
     */
    public LDAPAttributeSchema getAttribute( String name )
    {
        com.novell.ldap.LDAPAttributeSchema attr = schema.getAttribute( name);
        return new LDAPAttributeSchema( attr);
    }

    /**
     * Returns a particular object class definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getObjectClass(String)
     */
    public LDAPObjectClassSchema getObjectClass( String name )
    {
        com.novell.ldap.LDAPObjectClassSchema obj = schema.getObjectClass(name);
        return new LDAPObjectClassSchema( obj);
    }

    /**
     * Returns a particular matching rule definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getMatchingRule(String)
     */
    public LDAPMatchingRuleSchema getMatchingRule( String name )
    {
        return new LDAPMatchingRuleSchema( schema.getMatchingRule(name));
    }

    /**
     * Returns a particular matching rule use definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getMatchingRuleUse(String)
     */
    public LDAPMatchingRuleUseSchema getMatchingRuleUse( String name )
    {
        return new LDAPMatchingRuleUseSchema( schema.getMatchingRuleUse(name));
    }

	/**
     * Returns a particular DIT structure rule definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getDITStructureRule(String)
     */
    public LDAPDITStructureRuleSchema getDITStructureRule( String name )
    {
        return new LDAPDITStructureRuleSchema(schema.getDITStructureRule(name));
    }

    /**
     * Returns a particular DIT structure rule definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getDITStructureRule(int)
     */
    public LDAPDITStructureRuleSchema getDITStructureRule( int id )
    {
        return new LDAPDITStructureRuleSchema( schema.getDITStructureRule(id));
    }

    /**
     * Returns a particular DIT content rule definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getDITContentRule(String)
     */
    public LDAPDITContentRuleSchema getDITContentRule( String name )
    {
        return new LDAPDITContentRuleSchema( schema.getDITContentRule(name));
    }

    /**
     * Returns a particular name form definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getNameForm(String)
     */
    public LDAPNameFormSchema getNameForm( String name )
    {
        return new LDAPNameFormSchema( schema.getNameForm(name));
    }

    /**
     * Returns a particular syntax definition, or null if not found.
     *
     * @see com.novell.ldap.LDAPSchema#getSyntax(String)
     */
    public LDAPSyntaxSchema getSyntax( String oid )
    {
        return new LDAPSyntaxSchema( schema.getSyntax(oid));
    }
 
    /**
     * Returns an enumeration of attribute definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getAttributes()
     */
    public Enumeration getAttributes()
    {
        return schema.getAttributes();
    }
 
    /**
     * Returns an enumeration of object class definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getObjectClasses()
     */
    public Enumeration getObjectClasses()
    {
        return schema.getObjectClasses();
    }
 
    /**
     * Returns an enumeration of matching rule definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getMatchingRules()
     */
    public Enumeration getMatchingRules()
    {
        return schema.getMatchingRules();
    }
 
    /**
     * Returns an enumeration of matching rule use definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getMatchingUseRules()
     */
    public Enumeration getMatchingUseRules()
    {
        return schema.getMatchingUseRules();
    }
 
    /**
     * Returns an enumeration of DIT structure rule definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getDITStructureRules()
     */
    public Enumeration getDITStructureRules()
    {
        return schema.getDITStructureRules();
    }
 
    /**
     * Returns an enumeration of DIT content rule definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getDITContentRules()
     */
    public Enumeration getDITContentRules()
    {
        return schema.getDITContentRules();
    }
 
    /**
     * Returns an enumeration of name form definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getNameForms()
     */
    public Enumeration getNameForms()
    {
        return schema.getNameForms();
    }
 
    /**
     * Returns an enumeration of syntax definitions.
     *
     * @see com.novell.ldap.LDAPSchema#getSyntaxes()
     */
    public Enumeration getSyntaxes()
    {
        return schema.getSyntaxes();
    }
 
    /**
     * Returns an enumeration of attribute names.
     *
     * @see com.novell.ldap.LDAPSchema#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
        return schema.getAttributeNames();
    }
 
    /**
     * Returns an enumeration of object class names.
     *
     * @see com.novell.ldap.LDAPSchema#getObjectClassNames()
     */
    public Enumeration getObjectClassNames()
    {
        return schema.getObjectClassNames();
    }
 
    /**
     * Returns an enumeration of matching rule names.
     *
     * @see com.novell.ldap.LDAPSchema#getMatchingRuleNames()
     */
    public Enumeration getMatchingRuleNames()
    {
        return schema.getMatchingRuleNames();
    }
 
    /**
   	 * Returns an enumeration of matching rule use names.
     *
     * @see com.novell.ldap.LDAPSchema#getMatchingRuleUseNames()
     */
    public Enumeration getMatchingRuleUseNames()
    {
        return schema.getMatchingRuleUseNames();
    }
 
    /**
   	 * Returns an enumeration of DIT structure rule names.
     *
     * @see com.novell.ldap.LDAPSchema#getDITStructureRuleNames()
     */
    public Enumeration getDITStructureRuleNames()
    {
        return schema.getDITStructureRuleNames();
    }
 
   /**
   	 * Returns an enumeration of DIT content rule names.
     *
     * @see com.novell.ldap.LDAPSchema#getDITContentRuleNames()
     */
    public Enumeration getDITContentRuleNames()
    {
        return schema.getDITContentRuleNames();
    }
 
   /**
   	 * Returns an enumeration of name form names.
     *
     * @see com.novell.ldap.LDAPSchema#getNameFormNames()
     */
    public Enumeration getNameFormNames()
    {
        return schema.getNameFormNames();
    }
 }
