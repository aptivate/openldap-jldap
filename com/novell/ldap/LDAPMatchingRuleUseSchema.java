package com.novell.ldap;

import java.io.IOException;
import com.novell.ldap.client.SchemaParser;

/*  Represents the definition of a specific matching rule use in the
 *  directory schema.
 *
 * <p>The LDAPMatchingRuleUseSchema class is used to discover or modify which
 * attributes are suitable for use with an extensible matching rule. It contains
 * the name and identifier of a matching rule, and a list of attributes which
 * it applies to.</p>
 */


public class LDAPMatchingRuleUseSchema
                extends LDAPSchemaElement
{
    private String[] attributes;
    /**
     * Constructs a matching rule use definition for adding to or deleting
     * from the schema.
     *
     * @param name        The name of the matching rule.
     *</br></br>
     * @param oid         The unique object identifier of the matching rule
     *                    in dotted numerical format.
     *</br></br>
     * @param description An optional description of the matching rule use.
     *</br></br>
     * @param obsolete    True if the matching rule use is obsolete.
     *</br></br>
     * @param attributes  A list of attributes that this matching rule
     *                    applies to. These values may be either the
     *                    names or numeric oids of the attributes.
     *</br></br>
     * @param aliases     Optional list of additional names by which the
     *                    matching rule use may be known; null if there
     *                    are no aliases.
     */
    public LDAPMatchingRuleUseSchema(String name,
                                     String oid,
                                     String description,
                                     boolean obsolete,
                                     String[] attributes,
                                     String[] aliases)
    {
        super.name = name;
        super.oid = oid;
        super.description = description;
        super.obsolete = obsolete;
        super.aliases = aliases;
        this.attributes = attributes;
    }



    /**
     * Constructs a matching rule use definition from the raw string value
     * returned on a schema query for matchingRuleUse.
     *
     * @param raw        The raw string value returned on a schema
     *                   query for matchingRuleUse.
     */
    public LDAPMatchingRuleUseSchema(String raw)
    {
        try{
            SchemaParser matchParser = new SchemaParser(raw);
            super.name = matchParser.getName();
            super.oid = matchParser.getID();
            super.description = matchParser.getDescription();
            super.aliases = matchParser.getAliases();
            super.obsolete = matchParser.getObsolete();
            this.attributes = matchParser.getApplies();
        }
        catch( IOException e){
        }
    }

    /**
     * Returns an array of all the attributes which this matching rule
     * applies to.
     *
     * @return An array of all the attributes which this matching rule applies to.
     */
    public String[] getAttributes()
    {
        return attributes;
    }

	/**
    * Returns a string in a format suitable for directly adding to a
    * directory, as a value of the particular schema element attribute.
    *
    * @return A string representation of the attribute's definition.
    */
   public String getValue() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

      if( (token = getID()) != null){
        valueBuffer.append(token);
      }
      strArray = getAliases();
      if( (token = getName()) != null){
        valueBuffer.append(" NAME ");
        if(strArray != null){
          valueBuffer.append("( ");
        }
        valueBuffer.append("'" + token + "'");
        if(strArray != null){
          for( int i = 0; i < strArray.length; i++ ){
            valueBuffer.append(" '" + strArray[i] + "'");
          }
          valueBuffer.append(" )");
        }
      }
      if( (token = getDescription()) != null){
        valueBuffer.append(" DESC ");
        valueBuffer.append("'" + token + "'");
      }
      if( isObsolete()){
        valueBuffer.append(" OBSOLETE");
      }
      if( (strArray = getAttributes()) != null){
      	valueBuffer.append(" APPLIES ");
       	if( strArray.length > 1)
        	valueBuffer.append("( ");
        for( int i =0; i < strArray.length; i++){
        	if( i > 0)
         		valueBuffer.append(" $ ");
           	valueBuffer.append(strArray[i]);
        }
        if( strArray.length > 1)
        	valueBuffer.append(" )");
      }
      valueBuffer.append(" )");
      return valueBuffer.toString();
   }

}
