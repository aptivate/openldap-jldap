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
import com.novell.ldap.client.SchemaParser;
import com.novell.ldap.resources.*;
import java.util.Enumeration;
import java.io.IOException;
/**
 *  The schematic definition of a particular matching rule
 *  in a particular Directory Server.
 *
 *  <p>The LDAPMatchingRuleSchema class represents the definition of a mathcing
 *  rule.  It is used to query matching rule syntax, and to add or delete a
 *  matching rule definition in a directory.
 *
 * <p>Novell eDirectory does not currently allow matching rules to be added
 * or deleted from the schema.</p>
 *
 * @see LDAPAttributeSchema
 * @see LDAPSchemaElement
 * @see LDAPSchema
 */
public class LDAPMatchingRuleSchema extends LDAPSchemaElement
{
    private String syntaxString;
    private String[] attributes;
   /**
    * Constructs a matching rule definition for adding to or deleting from
    * a directory.
    *
    *  @param names       The names of the attribute.
    *<br><br>
    *  @param oid         Object Identifier of the attribute - in
    *                     dotted-decimal format.
    *<br><br>
    *  @param description   Optional description of the attribute.
    *<br><br>
    *  @param attributes    The OIDs of attributes to which the rule applies.
    *                       This parameter may be null. All attributes added to
    *                       this array must use the same syntax.
    *<br><br>
    *  @param obsolete      true if this matching rule is obsolete.
    *<br><br>
    *
    *  @param syntaxString   The unique object identifer of the syntax of the
    *                        attribute, in dotted numerical format.
    *<br><br>
    */
   public LDAPMatchingRuleSchema(String[] names,
                                 String oid,
                                 String description,
                                 String[] attributes,
                                 boolean obsolete,
                                 String syntaxString)
   {
      super.names = (String[]) names.clone();
      super.oid = oid;
      super.description = description;
      super.obsolete = obsolete;
      this.attributes = (String[]) attributes.clone();
      this.syntaxString = syntaxString;
      super.value = formatString();
      return;
   }


   /**
    * Constructs a matching rule definition from the raw string values
    * returned from a schema query for "matchingRule" and for
    * "matchingRuleUse" for the same rule.
    *
    *  @param rawMatchingRule    The raw string value returned on a directory
    *                            query for "matchingRule".
    *<br><br>
    *  @param rawMatchingRuleUse  The raw string value returned on a directory
    *                             query for "matchingRuleUse".
    */
   public LDAPMatchingRuleSchema(String rawMatchingRule,
                                 String rawMatchingRuleUse) {
    try{
        SchemaParser matchParser = new SchemaParser(rawMatchingRule);
        super.names = (String[])matchParser.getNames().clone();
        super.oid = matchParser.getID();
        super.description = matchParser.getDescription();
        super.obsolete = matchParser.getObsolete();
        this.syntaxString = matchParser.getSyntax();
        if( rawMatchingRuleUse != null ){
            SchemaParser matchUseParser = new SchemaParser(rawMatchingRuleUse);
            this.attributes = matchUseParser.getApplies();
        }
        super.value = formatString();
    }
    catch( IOException e){
    }
    return;
   }

   /**
    * Returns the OIDs of the attributes to which this rule applies.
    *
    *@return The OIDs of the attributes to which this matching rule applies.
    */
   public String[] getAttributes() {
      return attributes;
   }

   /**
    * Returns the OID of the syntax that this matching rule is valid for.
    *
    *@return The OID of the syntax that this matching rule is valid for.
    */
   public String getSyntaxString() {
      return syntaxString;
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
      if( (token = getSyntaxString()) != null){
        valueBuffer.append(" SYNTAX ");
        valueBuffer.append(token);
      }
      valueBuffer.append(" )");
      return valueBuffer.toString();
   }
}
