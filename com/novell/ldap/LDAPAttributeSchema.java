/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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
import com.novell.ldap.client.AttributeQualifier;
import com.novell.ldap.resources.*;
import java.util.Enumeration;
import java.io.IOException;

/**
 * The definition of an attribute type in the schema.
 *
 * <p>LDAPAttributeSchema is used to discover an attribute's
 * syntax, and add or delete an attribute definition.
 * RFC 2252, "Lightweight Directory Access Protocol (v3):
 * Attribute Syntax Definitions" contains a description
 * of the information on the LDAP representation of schema.
 * draft-sermerseim-nds-ldap-schema-02, "LDAP Schema for NDS"
 * defines the schema descriptions and non-standard syntaxes
 * used by Novell eDirectory.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/ExtendSchema.java.html">ExtendSchema.java</p>
 *
 * @see LDAPSchema
 */
public class LDAPAttributeSchema extends LDAPSchemaElement {

    private String syntaxString;
    private boolean single = false;
    private String superior;
    private String equality;
    private String ordering;
    private String substring;
    private boolean collective = false;
    private boolean userMod = true;
    private int usage = USER_APPLICATIONS;

    /**
     * Indicates that the attribute usage is for ordinary application
     * or user data.
     */
    public final static int USER_APPLICATIONS = 0;
    /**
     * Indicates that the attribute usage is for directory operations.
     * Values are vendor specific.
     */
    public final static int DIRECTORY_OPERATION = 1;
    /**
     * Indicates that the attribute usage is for distributed operational
     * attributes. These hold server (DSA) information that is shared among
     * servers holding replicas of the entry.
     */
    public final static int DISTRIBUTED_OPERATION = 2;
    /**
     * Indicates that the attribute usage is for local operational attributes.
     * These hold server (DSA) information that is local to a server.
     */
    public final static int DSA_OPERATION = 3;

   /**
    * Constructs an attribute definition for adding to or deleting from a
    * directory's schema.
    *
    * @param names Names of the attribute.
    *<br><br>
    * @param oid   Object identifer of the attribute, in
    *              dotted numerical format.
    *<br><br>
    * @param description   Optional description of the attribute.
    *<br><br>
    * @param syntaxString  Object identifer of the syntax of the
    *              attribute, in dotted numerical format.
    *<br><br>
    * @param single    True if the attribute is to be single-valued.
    *<br><br>
    * @param superior  Optional name of the attribute type which this
    *              attribute type derives from; null if there is no
    *              superior attribute type.
    *<br><br>
    * @param obsolete  True if the attribute is obsolete.
    *<br><br>
    * @param equality  Optional matching rule name; null if there is not
    *               an equality matching rule for this attribute.
    *<br><br>
    * @param ordering Optional matching rule name; null if there is not
    *               an ordering matching rule for this attribute.
    *<br><br>
    * @param substring    Optional matching rule name; null if there is not
    *                a substring matching rule for this attribute.
    *<br><br>
    * @param collective    True of this attribute is a collective attribute
    *<br><br>
    * @param isUserModifiable  False if this attribute is a read-only attribute
    *<br><br>
    * @param usage        Describes what the attribute is used for. Must be
    *                one of the following: USER_APPLICATIONS,
    *                DIRECTORY_OPERATION, DISTRIBUTED_OPERATION or
    *                DSA_OPERATION.
    */
   public LDAPAttributeSchema(String[] names,
                              String oid,
                              String description,
                              String syntaxString,
                              boolean single,
                              String superior,
                              boolean obsolete,
                              String equality,
                              String ordering,
                              String substring,
                              boolean collective,
                              boolean isUserModifiable,
                              int usage) {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.ATTRIBUTE]);
        super.names = names;
        super.oid = oid;
        super.description = description;
        super.obsolete = obsolete;
        this.syntaxString = syntaxString;
        this.single = single;        
        this.equality = equality;
        this.ordering = ordering;
        this.substring = substring;
        this.collective = collective;
        this.userMod = isUserModifiable;
        this.usage = usage;
        super.setValue(formatString());
        return;
   }



   /**
    * Constructs an attribute definition from the raw string value returned
    * on a directory query for "attributetypes".
    *
    *  @param raw      The raw string value returned on a directory
    *                  query for "attributetypes".
    */
   public LDAPAttributeSchema(String raw) {
       super(LDAPSchema.schemaTypeNames[LDAPSchema.ATTRIBUTE]);
       try{
            SchemaParser parser = new SchemaParser( raw );

           if( parser.getNames() != null)
               super.names = parser.getNames();
           if( parser.getID() != null)
               super.oid = parser.getID();
           if( parser.getDescription() != null)
               super.description = parser.getDescription();
           if( parser.getSyntax() != null)
               syntaxString = parser.getSyntax();
           if( parser.getSuperior() != null)
               syntaxString = parser.getSuperior();
           single = parser.getSingle();
           super.obsolete = parser.getObsolete();
           Enumeration qualifiers = parser.getQualifiers();
           AttributeQualifier attrQualifier;
           while(qualifiers.hasMoreElements()){
               attrQualifier = (AttributeQualifier) qualifiers.nextElement();
               setQualifier(attrQualifier.getName(), attrQualifier.getValues());
           }
           super.setValue(formatString());
       }
       catch( IOException e){
           throw new RuntimeException(e.toString());
       }
       return;
   }

   /**
    * Returns the object identifer of the syntax of the attribute, in
    * dotted numerical format.
    *
    * @return The object identifer of the attribute's syntax.
    */
   public String getSyntaxString() {
        return syntaxString;
   }

   /**
    * Returns the name of the attribute type which this attribute derives
    * from, or null if there is no superior attribute.
    *
    * @return The attribute's superior attribute, or null if there is none.
    */
   public String getSuperior() {
      return superior;
   }

   /**
    * Returns true if the attribute is single-valued.
    *
    * @return True if the attribute is single-valued; false if the attribute
    *         is multi-valued.
    */
   public boolean isSingleValued() {
      return single;
   }

   /**
    * Returns the matching rule for this attribute.
    *
    * @return The attribute's equality matching rule; null if it has no equality
    *          matching rule.
    */
   public String getEqualityMatchingRule() {
      return equality;
   }

   /**
    * Returns the ordering matching rule for this attribute.
    *
    * @return The attribute's ordering matching rule; null if it has no ordering
    *          matching rule.
    */
   public String getOrderingMatchingRule() {
      return ordering;
   }

  /**
   * Returns the substring matching rule for this attribute.
   *
   * @return The attribute's substring matching rule; null if it has no substring
   *          matching rule.
   */
   public String getSubstringMatchingRule() {
      return substring;
   }

   /**
    * Returns true if the attribute is a collective attribute.
    *
    * @return True if the attribute is a collective; false if the attribute
    *         is not a collective attribute.
    */
   public boolean isCollective() {
      return collective;
   }

   /**
    * Returns false if the attribute is read-only.
    *
    * @return False if the attribute is read-only; true if the attribute
    *         is read-write.
    */
   public boolean isUserModifiable() {
      return userMod;
   }

   /**
    * Returns the usage of the attribute.
    *
    * @return One of the following values: USER_APPLICATIONS,
    *          DIRECTORY_OPERATION, DISTRIBUTED_OPERATION or
    *          DSA_OPERATION.
    */
   public int getUsage() {
      return usage;
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
      if( (token = getSuperior()) != null){
        valueBuffer.append(" SUP ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getEqualityMatchingRule()) != null){
        valueBuffer.append(" EQUALITY ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getOrderingMatchingRule()) != null){
        valueBuffer.append(" ORDERING ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getSubstringMatchingRule()) != null){
        valueBuffer.append(" SUBSTR ");
        valueBuffer.append("'" + token + "'");
      }
      if( (token = getSyntaxString()) != null){
        valueBuffer.append(" SYNTAX ");
        valueBuffer.append(token);
      }
      if( isSingleValued()){
        valueBuffer.append(" SINGLE-VALUE");
      }
      if( isCollective()){
        valueBuffer.append(" COLLECTIVE");
      }
      if( isUserModifiable() == false){
        valueBuffer.append(" NO-USER-MODIFICATION");
      }
      int useType;
      if( (useType = getUsage()) != USER_APPLICATIONS ){
        switch( useType){
            case DIRECTORY_OPERATION :
                   valueBuffer.append( " USAGE directoryOperation" );
                   break;
              case DISTRIBUTED_OPERATION :
                   valueBuffer.append( " USAGE distributedOperation" );
                   break;
            case DSA_OPERATION :
                 valueBuffer.append( " USAGE dSAOperation" );
                   break;
                 default:
                      break;
        }
      }
      Enumeration en = getQualifierNames();
      while( en.hasMoreElements()){
        token = (String) en.nextElement();
        if( (token != null)){
          valueBuffer.append(" " + token );
          strArray = getQualifier(token);
          if(strArray != null){
            if(strArray.length > 1)
                valueBuffer.append("(");
            for( int i = 0; i < strArray.length; i++ ){
              valueBuffer.append(" '" + strArray[i] + "'");
            }
            if(strArray.length > 1)
                valueBuffer.append(" )");
          }
        }
      }
      valueBuffer.append(" )");
      return valueBuffer.toString();
   }


   // #######################################################################
   //  The following are deprecated and will be removed in the fall of 2003
   // #######################################################################

   /**
    * <p>This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.</p>
    *
    * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
    * be used directly with {@link LDAPModification},
    * see {@link LDAPConnection#modify LDAPConnection.modify()}</p>
    *
    * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
    */
   public void add(LDAPConnection ld) throws LDAPException {
       try {
           add(ld,"");
       } catch(LDAPException e){
           throw e;
       }
   }

   /**
    * <p>This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.</p>
    *
    * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
    * be used directly with {@link LDAPModification},
    * see {@link LDAPConnection#modify LDAPConnection.modify()}</p>
    *
    * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
    */
  public void add(LDAPConnection ld, String dn) throws LDAPException {
    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn, LDAPConnection.SCOPE_BASE,
                                        "objectclass=*", attrSubSchema,
                                        false);
        if(sr != null && sr.hasMoreElements()){
            String schemaDN;
            LDAPEntry ent = sr.next();
            LDAPAttributeSet attrSet = ent.getAttributeSet();
            Enumeration en = attrSet.getAttributes();
            LDAPAttribute attr;
            if(en.hasMoreElements()){
                attr = (LDAPAttribute) en.nextElement();
                Enumeration enumString = attr.getStringValues();
                if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "attributeTypes" };
                    sr = ld.search( schemaDN,
                            LDAPConnection.SCOPE_BASE,
                            "objectclass=*",
                            attrSearchName,
                            false);
                    String attrName;
                    if(sr != null && sr.hasMoreElements()){
                        ent = sr.next();
                        attrSet = ent.getAttributeSet();
                        en = attrSet.getAttributes();
                        while(en.hasMoreElements()){
                            attr = (LDAPAttribute) en.nextElement();
                            attrName = attr.getName();
                            if(attrName.equalsIgnoreCase("attributeTypes")){
                                // add the value to the attributes values
                                LDAPAttribute newValue = new LDAPAttribute(
                                        "attributeTypes",getValue());
                                LDAPModification lModify = new LDAPModification(
                                    LDAPModification.ADD,newValue);
                                ld.modify(schemaDN,lModify);
                            }
                            continue;
                        }
                    }
                }
            }
        }
    }

    catch( LDAPException e){
      throw e;
    }
  }

  /**
    * <p>This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.</p>
    * 
    * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
    * be used directly with {@link LDAPModification},
    * see {@link LDAPConnection#modify LDAPConnection.modify()}</p>
    *
    * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
    */
  public void remove(LDAPConnection ld) throws LDAPException {
    try{
        remove(ld,"");
    }
    catch(LDAPException e){
        throw e;
    }
  }

  /**
    * <p>This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.</p>
    * 
    * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
    * be used directly with {@link LDAPModification},
    * see {@link LDAPConnection#modify LDAPConnection.modify()}</p>
    *
    * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
    */
  public void remove(LDAPConnection ld, String dn) throws LDAPException {
    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn,
                                    LDAPConnection.SCOPE_BASE, "objectclass=*",
                                    attrSubSchema, false);
        if(sr != null && sr.hasMoreElements()){
            String schemaDN;
            LDAPEntry ent = sr.next();
            LDAPAttributeSet attrSet = ent.getAttributeSet();
            Enumeration en = attrSet.getAttributes();
            LDAPAttribute attr;
            if(en.hasMoreElements()){
                attr = (LDAPAttribute) en.nextElement();
                Enumeration enumString = attr.getStringValues();
                if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "attributeTypes" };
                    sr = ld.search( schemaDN,
                            LDAPConnection.SCOPE_BASE,
                            "objectclass=*",
                            attrSearchName,
                            false);
                    String attrName;
                    if(sr != null && sr.hasMoreElements()){
                        ent = sr.next();
                        attrSet = ent.getAttributeSet();
                        en = attrSet.getAttributes();
                        while(en.hasMoreElements()){
                        attr = (LDAPAttribute) en.nextElement();
                        attrName = attr.getName();
                        if(attrName.equalsIgnoreCase("attributeTypes")){
                        // remove the value from the attributes values
                            LDAPAttribute newValue = new LDAPAttribute(
                                "attributeTypes",getValue());
                            LDAPModification lModify = new LDAPModification(
                                LDAPModification.DELETE,newValue);
                            ld.modify(schemaDN,lModify);
                        }
                        continue;
                    }
                }
            }
        }
    }
  }

    catch( LDAPException e){
      throw e;
    }
  }

  /**
    * <p>This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.</p>
    *
    * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
    * be used directly with {@link LDAPModification},
    * see {@link LDAPConnection#modify LDAPConnection.modify()}</p>
    *
    * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
    */
  public void modify(LDAPConnection ld, LDAPSchemaElement newValue) throws LDAPException {
    try{
        modify(ld, newValue, "");
    }
    catch(LDAPException e){
        throw e;
    }
  }

  /**
    * <p>This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.</p>
    *
    * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
    * be used directly with {@link LDAPModification},
    * see {@link LDAPConnection#modify LDAPConnection.modify()}</p>
    *
    * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
    */
  public void modify(LDAPConnection ld, LDAPSchemaElement newValue, String dn) throws LDAPException {
    if( newValue instanceof LDAPAttributeSchema != true ){
        throw new LDAPException(ExceptionMessages.NOT_AN_ATTRIBUTE, //"Schema element is not an LDAPAttributeSchema object",
                LDAPException.INVALID_ATTRIBUTE_SYNTAX);
    }

    try{
        String attrSubSchema[] = { "subschemaSubentry" };
        LDAPSearchResults sr = ld.search( dn,
                                    LDAPConnection.SCOPE_BASE, "objectclass=*",
                                    attrSubSchema, false);
        if(sr != null && sr.hasMoreElements()){
            String schemaDN;
            LDAPEntry ent = sr.next();
            LDAPAttributeSet attrSet = ent.getAttributeSet();
            Enumeration en = attrSet.getAttributes();
            LDAPAttribute attr;
            if(en.hasMoreElements()){
                attr = (LDAPAttribute) en.nextElement();
                Enumeration enumString = attr.getStringValues();
                if(enumString.hasMoreElements()){
                    schemaDN = (String) enumString.nextElement();
                    String[] attrSearchName= { "attributeTypes" };
                    sr = ld.search( schemaDN,
                            LDAPConnection.SCOPE_BASE,
                            "objectclass=*",
                            attrSearchName,
                            false);
                    String attrName;
                    if(sr != null && sr.hasMoreElements()){
                        ent = sr.next();
                        attrSet = ent.getAttributeSet();
                        en = attrSet.getAttributes();
                        while(en.hasMoreElements()){
                            attr = (LDAPAttribute) en.nextElement();
                            attrName = attr.getName();
                            if(attrName.equalsIgnoreCase("attributeTypes")){
                            // modify the attribute
                            LDAPAttribute modValue = new LDAPAttribute(
                                        "attributeTypes", newValue.getValue());
                            LDAPModificationSet mods = new LDAPModificationSet();
                            mods.add(LDAPModification.DELETE, modValue);
                            mods.add(LDAPModification.ADD, modValue);
                            ld.modify(schemaDN,mods);
                        }
                        continue;
                  }
               }
            }
          }
        }
      }
        catch( LDAPException e){
            throw e;
        }
    }

   /**
    * This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.
    * @deprecated replaced by {@link #toString}.
    */
   public String getValue(){
        return toString();
   }

   /**
    * This method removed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.
    *  @deprecated replaced by {@link #isUserModifiable}.
    */
   public boolean isModifiable() {
      return isUserModifiable();
   }

   /**
    * This constructor has been changed in the IETF draft 17 of the
    * Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt), and will be removed
    * from the LDAP Classes for Java API in the fall of 2003.
    *
    * Constructs an attribute definition for adding to or deleting from a
    * directory's schema.
    *
    * @param aliases   Optional list of additional names by which the
    *              attribute may be known; null if there are no
    *              aliases.

    *  @deprecated replaced by {@link #LDAPAttributeSchema( String[], String,
    *       String, String, boolean, String, boolean, String, String, String,
    *       boolean, boolean, int) LDAPAttributeSchema}.
    */
   public LDAPAttributeSchema(String name, String oid, String description,
                              String syntaxString, boolean single,
                              String superior, String[] aliases,
                              boolean obsolete, String equality, String ordering,
                              String substring, boolean collective, boolean userMod,
                              int usage) {
        super(LDAPSchema.schemaTypeNames[LDAPSchema.ATTRIBUTE]);
        int aliasLength = 0;
        if (aliases != null)
            aliasLength = aliases.length;
        super.names = new String[aliasLength + 1];
        super.names[0] = name;
        for(int i=0; i<aliasLength ;i++)
            super.names[i+1] = aliases[i];

        super.oid = oid;
        super.description = description;
        super.obsolete = obsolete;
        this.syntaxString = syntaxString;
        this.single = single;
        this.superior = superior;
        
        this.equality = equality;
        this.ordering = ordering;
        this.substring = substring;
        this.collective = collective;
        this.userMod = userMod;
        this.usage = usage;
        super.setValue(formatString());
        return;
   }
}
