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

package com.novell.ldap;

import java.io.IOException;
import java.util.Enumeration;
import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.resources.*;

/**  Represents the definition of a specific matching rule use in the
 *  directory schema.
 *
 * <p>The LDAPMatchingRuleUseSchema class represents the definition of a
 * matching rule use.  It is used to discover or modify which attributes are
 * suitable for use with an extensible matching rule. It contains the name and
 * identifier of a matching rule, and a list of attributes which
 * it applies to.</p>
 *
 * @see LDAPAttributeSchema
 * @see LDAPSchemaElement
 * @see LDAPSchema
 */
public class LDAPMatchingRuleUseSchema
                extends LDAPSchemaElement
{
    private String[] attributes;

    /**
     * Constructs a matching rule use definition for adding to or deleting
     * from the schema.
     *
     * @param names       Name(s) of the matching rule.
     *</br></br>
     * @param oid         Object Identifier of the the matching rule
     *                    in dotted-decimal format.
     *</br></br>
     * @param description Optional description of the matching rule use.
     *</br></br>
     * @param obsolete    True if the matching rule use is obsolete.
     *</br></br>
     * @param attributes  List of attributes that this matching rule
     *                    applies to. These values may be either the
     *                    names or numeric oids of the attributes.
     */
    public LDAPMatchingRuleUseSchema(String names[],
                                     String oid,
                                     String description,
                                     boolean obsolete,
                                     String[] attributes)
    {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING_USE]);
        super.names = (String[]) names.clone();
        super.oid = oid;
        super.description = description;
        super.obsolete = obsolete;
        this.attributes = (String[]) attributes.clone();
        super.setValue(formatString());
        return;
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
        super(LDAPSchema.schemaTypeNames[LDAPSchema.MATCHING_USE]);    
        try{
            SchemaParser matchParser = new SchemaParser(raw);
            super.names = (String[])matchParser.getNames().clone();
            super.oid = matchParser.getID();
            super.description = matchParser.getDescription();
            super.obsolete = matchParser.getObsolete();
            this.attributes = matchParser.getApplies();
            super.setValue(formatString());
        }
        catch( IOException e){
        }
        return;
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
   protected String formatString() {

      StringBuffer valueBuffer = new StringBuffer("( ");
      String token;
      String[] strArray;

      if( (token = getID()) != null){
        valueBuffer.append(token);
      }
      strArray = getNames();
      if( strArray != null){
        valueBuffer.append(" NAME ");
        if (strArray.length == 1){
            valueBuffer.append("'" + strArray[0] + "'");
        }
        else {
           valueBuffer.append("( ");

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
