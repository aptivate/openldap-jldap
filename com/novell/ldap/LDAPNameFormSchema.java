/* **************************************************************************
 * $Novell:
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ***************************************************************************/
package com.novell.ldap;

import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.client.AttributeQualifier;
import java.util.Enumeration;
import java.io.IOException;

/**
 *  Represents a specific a name form in the directory schema.
 *
 *  <p>The LDAPNameFormSchema class is used to discover or modify the allowed 
 *  naming attributes for a particular object class.</p>
 */

public class LDAPNameFormSchema
                extends LDAPSchemaElement
{
	private String objectClass;
	private String[] required;
    private String[] optional;

    /**
     * Constructs a name form for adding to or deleting from the schema.
     *
     * @param name        The name of the name form.</br></br>
     *
     * @param oid         The unique object identifier of the name form - in
     *                    dotted numerical format.</br></br>
     *
     * @param description An optional description of the name form.</br></br>
     *
     * @param obsolete    True if the name form is obsolete.</br></br>
     *
     * @param objectClass The object to which this name form applies.
     *                    This may be specified by either name or
     *                    numeric oid.</br></br>
     *
     * @param required    A list of the attributes that must be present
     *                    in the RDN of an entry that this name form
     *                    controls. These attributes may be specified by
     *                    either name or numeric oid.</br></br>
     *
     * @param optional    A list of the attributes that may be present
     *                    in the RDN of an entry that this name form
     *                    controls. These attributes may be specified by 
     *                    either name or numeric oid.</br></br>
     *
     * @param aliases     An optional list of additional names by which the
     *                    name form may be known; null if there are no
     *                    aliases.</br></br>
     */
    public LDAPNameFormSchema(String name,
                              String oid,
                              String description,
                              boolean obsolete,
                              String objectClass,
                              String[] required,
                              String[] optional,
                              String[] aliases)
    {
        super.name = name;
		super.oid = oid;
		super.description = description;
		super.obsolete = obsolete;
		this.objectClass = objectClass;
		this.required = required;
		this.optional = optional;
		super.aliases = aliases;
    }

    /**
     * Constructs a DIT content rule from the raw string value returned on a
     * schema query for nameForms.
     *
     * @param raw        The raw string value returned on a schema
     *                   query for nameForms.
     */
    public LDAPNameFormSchema(String raw)
    {
        super.obsolete = false;
        try{
		    SchemaParser parser = new SchemaParser( raw );

	        if( parser.getName() != null)
			    super.name = new String(parser.getName());
	        super.aliases = parser.getAliases();
	        if( parser.getID() != null)
	            super.oid = new String(parser.getID());
	        if( parser.getDescription() != null)
	            super.description = new String(parser.getDescription());
	        if( parser.getRequired() != null)
	            required = (String[])parser.getRequired().clone();
	        if( parser.getOptional() != null)
	            optional = (String[])parser.getOptional().clone();
			if( parser.getObjectClass() != null)
	            objectClass = parser.getObjectClass();
			super.obsolete = parser.getObsolete();
	        Enumeration qualifiers = parser.getQualifiers();
	        AttributeQualifier attrQualifier;
	        while(qualifiers.hasMoreElements()){
	            attrQualifier = (AttributeQualifier) qualifiers.nextElement();
	            setQualifier(attrQualifier.getName(), attrQualifier.getValues());
        	}
    	}
    	catch( IOException e){
    	}
    }

    /**
     * Returns the name of the object class which this name form applies to.
     *
     * @return The name of the object class.
     */
    public String getObjectClass()
    {
        return objectClass;
    }


    /**
     * Returns the list of required naming attributes for an entry
     * controlled by this name form.
     *
     * @return The list of required naming attributes.
     */
    public String[]getRequiredNamingAttributes()
    {
        return required;
    }

    /**
     * Returns the list of optional naming attributes for an entry
     * controlled by this content rule.
     *
     * @return The list of the optional naming attributes.
     */
    public String[]getOptionalNamingAttributes()
    {
        return optional;
    }
}
