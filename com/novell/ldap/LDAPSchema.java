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

import com.novell.ldap.client.Debug;
import java.util.Enumeration;
import java.util.HashMap;
import com.novell.ldap.resources.ExceptionMessages;
import com.novell.ldap.client.EnumeratedIterator;

/**
 * <p>Represents a schema entry that controls one or more entries held by a
 * Directory Server.</p>
 *
 * <p><code>LDAPSchema</code> Contains methods to parse schema attributes into
 * individual schema definitions, represented by subclasses of
 * {@link LDAPSchemaElement}.  Schema may be retrieved from a Directory server
 * with the fetchSchema method of LDAPConnection or by creating an LDAPEntry
 * containing schema attributes.  The following sample code demonstrates how to
 * retrieve schema elements from LDAPSchema

 * </p>
 * <pre><code>
 *      .
 *      .
 *      .
 *      LDAPSchema schema;
 *      LDAPSchemaElement element;
 *
 *      // connect to the server
 *      lc.connect( ldapHost, ldapPort );
 *      lc.bind( ldapVersion, loginDN, password );
 *
 *      // read the schema from the directory
 *      schema = lc.fetchSchema( lc.getSchemaDN() );
 *
 *      // retrieve the definition of common name
 *      element = schema.getAttributeSchema( "cn" );
 *      System.out.println("The attribute cn has an oid of " + element.getID());
 *      .
 *      .
 *      .
 * </code></pre>
 *
 * <p><B>Other sample code:</B>
 * <DL>
 *     <DT>Adding and deleting Schema.
 *     <DD><a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/ExtendSchema.java.html">ExtendSchema.java</a>
 *
 *     <DT>Modifing an existing schema element
 *     <DD><a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/MakeContainer.java.html">MakeContainer.java</a>
 *
 *     <DT>Listing schema in a GUI
 *     <DD><a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/ListSchema.java.html">ListSchema.java</a>
 *     <DD><a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/schema/ListAttributeSchema.java.html">ListAttributeSchema.java</a>
 *     <DD><a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/schema/ListObjectClassSchema.java.html">ListObjectClassSchema.java</a>

 * </DL>
 *
 *
 *  </p>
 *
 * @see LDAPSchemaElement
 * @see LDAPConnection#fetchSchema
 * @see LDAPConnection#getSchemaDN
 */
public class LDAPSchema extends LDAPEntry {

    /**
     *  @deprecated
     *  Contains all schema changes created by the deprecated methods: add,
     *  modify, remove.  When these methods are removed this should also be
     *  removed.
     */
    private LDAPModificationSet schemaChanges = new LDAPModificationSet();

    /** The idTable hash on the oid (or integer ID for DITStructureRule) and
     *  is used for retrieving enumerations
     */
    private HashMap idTable[] = new HashMap[8];

    /** The nameTable will hash on the names (if available). To insure
     *  case-insensibility, the Keys for this table will be a String cast to
     *  Uppercase.
     */
    private HashMap nameTable[] = new HashMap[8];

    /**
     * The following lists the LDAP names of subschema attributes for
     *  schema elements (definitions):
     */
    /*package*/ static final String[] schemaTypeNames =
    {
                "attributeTypes",
                "objectClasses",
                "ldapSyntaxes",
                "nameForms",
                "dITContentRules",
                "dITStructureRules",
                "matchingRules",
                "matchingRuleUse"
    };

    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int ATTRIBUTE      = 0;
    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int OBJECT_CLASS   = 1;
    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int SYNTAX         = 2;
    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int NAME_FORM      = 3;
    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int DITCONTENT     = 4;
    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int DITSTRUCTURE   = 5;
    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int MATCHING       = 6;
    /** An index into the the arrays schemaTypeNames, idTable, and nameTable */
    /*package*/
    static final int MATCHING_USE   = 7;


    /**
     * Constructs an LDAPSchema object from attributes of an LDAPEntry.
     * <p>The object is empty if the entry parameter contains no schema
     * attributes.  The recognized schema attributes are the following: <br>
     * <pre><code>
     *          "attributeTypes", "objectClasses", "ldapSyntaxes",
     *          "nameForms", "dITContentRules", "dITStructureRules",
     *          "matchingRules","matchingRuleUse"
     * </code></pre>
     * @param entry          An LDAPEntry containing schema information.
     */
    public LDAPSchema(LDAPEntry ent){
        super(ent.getDN(), ent.getAttributeSet());
        //reset all definitions
        for (int i=0; i< schemaTypeNames.length; i++) {
            idTable[i] = new HashMap();
            nameTable[i] = new HashMap();
        }
        Enumeration en = super.getAttributeSet().getAttributes();

        while(en.hasMoreElements()) {
            LDAPAttribute attr = (LDAPAttribute) en.nextElement();
            String value, attrName = attr.getName();
            Enumeration enumString = attr.getStringValues();

            if(attrName.equalsIgnoreCase( schemaTypeNames[OBJECT_CLASS] )) {
                LDAPObjectClassSchema classSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    try {
                        classSchema = new LDAPObjectClassSchema( value );
                    }
                    catch (Exception e){
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.all, "fetchSchema could not "+
                                "parse the schema definition:" + value);
                        }
                        continue; //Error parsing: do not add this definition
                    }
                    addElement( OBJECT_CLASS, classSchema );
                }
            }
            else if(attrName.equalsIgnoreCase( schemaTypeNames[ATTRIBUTE] )) {
                LDAPAttributeSchema attrSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    try {
                        attrSchema = new LDAPAttributeSchema( value );
                    }
                    catch (Exception e){
                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.all, "fetchSchema could not "+
                                "parse the schema definition:" + value);
                        }
                        continue; //Error parsing: do not add this definition
                    }
                    addElement( ATTRIBUTE, attrSchema );
                }
            }
            else if(attrName.equalsIgnoreCase( schemaTypeNames[SYNTAX] )) {
                LDAPSyntaxSchema syntaxSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    syntaxSchema = new LDAPSyntaxSchema( value );
                    addElement( SYNTAX, syntaxSchema );
                }
            }
            else if(attrName.equalsIgnoreCase( schemaTypeNames[MATCHING] )) {
                LDAPMatchingRuleSchema matchingRuleSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    matchingRuleSchema =
                            new LDAPMatchingRuleSchema( value, null );
                    addElement( MATCHING, matchingRuleSchema );
                }
            }
            else if(attrName.equalsIgnoreCase( schemaTypeNames[MATCHING_USE])) {
                LDAPMatchingRuleUseSchema matchingRuleUseSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    matchingRuleUseSchema =
                            new LDAPMatchingRuleUseSchema( value );
                    addElement( MATCHING_USE, matchingRuleUseSchema );
                }
            }
            else if(attrName.equalsIgnoreCase( schemaTypeNames[DITCONTENT] )) {
                LDAPDITContentRuleSchema dITContentRuleSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    dITContentRuleSchema =
                            new LDAPDITContentRuleSchema( value );
                    addElement( DITCONTENT, dITContentRuleSchema );
                }
            }
            else if(attrName.equalsIgnoreCase( schemaTypeNames[DITSTRUCTURE])) {
                LDAPDITStructureRuleSchema dITStructureRuleSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    dITStructureRuleSchema =
                            new LDAPDITStructureRuleSchema( value );
                    addElement( DITSTRUCTURE, dITStructureRuleSchema );
                }
            }
            else if(attrName.equalsIgnoreCase(schemaTypeNames[NAME_FORM])) {
                LDAPNameFormSchema nameFormSchema;
                while(enumString.hasMoreElements()) {
                    value = (String) enumString.nextElement();
                    nameFormSchema = new LDAPNameFormSchema( value );
                    addElement( NAME_FORM, nameFormSchema );
                }
            }
            //All non schema attributes are ignored.
            continue;
        }
        return;
    }

    /**
     * Adds the schema definition to the idList and nameList HashMaps.
     * This method is used by the methods fetchSchema and add.
     *
     * Note that the nameTable has all keys cast to Upper-case.  This is so we
     * can have a case-insensitive HashMap.  The getXXX (String key) methods
     * will also cast to uppercase.
     *
     * @param schemaType    Type of schema definition, use one of the final
     *                      integers defined at the top of this class:
     *                      ATTRIBUTE, OBJECT_CLASS, SYNTAX, NAME_FORM,
     *                      DITCONTENT, DITSTRUCTURE, MATCHING, MATCHING_USE
     *
     * @param element       Schema element definition.
     */
    private void addElement ( int schemaType, LDAPSchemaElement element)
    {
        idTable[schemaType].put( element.getID(), element );
        String names[] = element.getNames();
        for (int i=0; i< names.length; i++) {
            nameTable[schemaType].put( names[i].toUpperCase(), element );
        }
        return;
    }

// #######################################################################
//   The following methods retrieve a SchemaElement given a Key name:
// #######################################################################

    /**
     * This function abstracts retrieving LDAPSchemaElements from the local
     * copy of schema in this LDAPSchema class.  This is used by
     * <code>getXXX(String name)</code> functions.
     *
     * <p>Note that the nameTable has all keys cast to Upper-case.  This is so
     * we can have a case-insensitive HashMap.  The getXXX (String key)
     * methods will also cast to uppercase.</p>
     *
     * <p>The first character of a NAME string can only be an alpha character
     * (see section 4.1 of rfc2252) Thus if the first character is a digit we
     * can conclude it is an OID.  Note that this digit is ASCII only.</p>
     *
     * @param schemaType Specifies which list is to be used in schema
     *                   lookup.
     * @param key        The key can be either an OID or a name string.
     */
    private LDAPSchemaElement getSchemaElement( int schemaType, String key )
    {
        if( key == null || key.equalsIgnoreCase(""))
            return null;
        char c = key.charAt(0);
        if( c >= '0' && c <= '9' ) {
            //oid lookup
            return (LDAPSchemaElement) idTable[schemaType].get( key );
        } else {
            //name lookup
            return (LDAPSchemaElement)
                    nameTable[schemaType].get( key.toUpperCase() );
        }
    }

    /**
     * Returns a particular attribute definition, or null if not found.
     *
     *  @param name     Name or OID of the attribute for which a definition is
     *                  to be returned.
     *
     *  @return The attribute definition, or null if not found.
     */
    public LDAPAttributeSchema getAttributeSchema( String name )
    {
        return (LDAPAttributeSchema) getSchemaElement( ATTRIBUTE, name);
    }

    /**
     * Returns a particular DIT content rule definition, or null if not found.
     *
     *  @param name     The name of the DIT content rule use for which a
     *                   definition is to be returned.
     *
     *  @return The DIT content rule definition, or null if not found.
     */
    public LDAPDITContentRuleSchema getDITContentRuleSchema( String name )
    {
        return (LDAPDITContentRuleSchema) getSchemaElement( DITCONTENT, name);
    }

    /**
     * Returns a particular DIT structure rule definition, or null if not found.
     *
     *  @param name     The name of the DIT structure rule use for which a
     *                   definition is to be returned.
     *
     *  @return The DIT structure rule definition, or null if not found.
     */
    public LDAPDITStructureRuleSchema getDITStructureRuleSchema( String name )
    {
        return (LDAPDITStructureRuleSchema)getSchemaElement(DITSTRUCTURE, name);
    }

    /**
     * Returns a particular DIT structure rule definition, or null if not found.
     *
     *  @param name     The ID of the DIT structure rule use for which a
     *                   definition is to be returned.
     *
     *  @return The DIT structure rule definition, or null if not found.
     */
    public LDAPDITStructureRuleSchema getDITStructureRuleSchema( int ID )
    {
        Integer IDKey = new Integer(ID);
        return (LDAPDITStructureRuleSchema) idTable[DITSTRUCTURE].get(IDKey);
    }

    /**
     * Returns a particular matching rule definition, or null if not found.
     *
     *  @param name     The name of the matching rule for which a definition
     *                  is to be returned.
     *
     *  @return The matching rule definition, or null if not found.
     */
    public LDAPMatchingRuleSchema getMatchingRuleSchema( String name )
    {
        return (LDAPMatchingRuleSchema) getSchemaElement( MATCHING, name);
    }

    /**
     * Returns a particular matching rule use definition, or null if not found.
     *
     *  @param name     The name of the matching rule use for which a definition
     *                  is to be returned.
     *
     *  @return The matching rule use definition, or null if not found.
     */
    public LDAPMatchingRuleUseSchema getMatchingRuleUseSchema( String name )
    {
        return (LDAPMatchingRuleUseSchema)getSchemaElement(MATCHING_USE, name);
    }

    /**
     * Returns a particular name form definition, or null if not found.
     *
     *  @param name     The name of the name form for which a definition
     *                  is to be returned.
     *
     *  @return The name form definition, or null if not found.
     */
    public LDAPNameFormSchema getNameFormSchema( String name )
    {
        return (LDAPNameFormSchema) getSchemaElement( NAME_FORM, name);
    }

    /**
     * Returns a particular object class definition, or null if not found.
     *
     *  @param name    The name or OID of the object class for which a
     *                 definition is to be returned.
     *
     * @return The object class definition, or null if not found.
     */
    public LDAPObjectClassSchema getObjectClassSchema( String name )
    {
        return (LDAPObjectClassSchema) getSchemaElement( OBJECT_CLASS, name);
    }

    /**
     * Returns a particular syntax definition, or null if not found.
     *
     *  @param name     The oid of the syntax for which a definition
     *                  is to be returned.
     *
     *  @return The syntax definition, or null if not found.
     */
    public LDAPSyntaxSchema getSyntaxSchema( String oid )
    {
        return (LDAPSyntaxSchema) getSchemaElement( SYNTAX, oid);
    }

// ########################################################################
// The following methods return an Enumeration of SchemaElements by schema type
// ########################################################################

    /**
     * Returns an enumeration of attribute definitions.
     *
     * @return An enumeration of attribute definitions.
     */
    public Enumeration getAttributeSchemas()
    {
        return new EnumeratedIterator (
                idTable[ATTRIBUTE].values().iterator());
    }

    /**
     * Returns an enumeration of DIT content rule definitions.
     *
     * @return An enumeration of DIT content rule definitions.
     */
    public Enumeration getDITContentRuleSchemas()
    {
        return new EnumeratedIterator (
                idTable[DITCONTENT].values().iterator() );
    }

    /**
     * Returns an enumeration of DIT structure rule definitions.
     *
     * @return An enumeration of DIT structure rule definitions.
     */
    public Enumeration getDITStructureRuleSchemas()
    {
        return new EnumeratedIterator (
                idTable[DITSTRUCTURE].values().iterator() );
    }

    /**
     * Returns an enumeration of matching rule definitions.
     *
     * @return An enumeration of matching rule definitions.
     */
    public Enumeration getMatchingRuleSchemas()
    {
        return new EnumeratedIterator (
                idTable[MATCHING].values().iterator() );
    }

    /**
     * Returns an enumeration of matching rule use definitions.
     *
     * @return An enumeration of matching rule use definitions.
     */
    public Enumeration getMatchingRuleUseSchemas()
    {
        return new EnumeratedIterator (
                idTable[MATCHING_USE].values().iterator() );
    }

    /**
     * Returns an enumeration of name form definitions.
     *
     * @return An enumeration of name form definitions.
     */
    public Enumeration getNameFormSchemas()
    {
        return new EnumeratedIterator (
                idTable[NAME_FORM].values().iterator() );
    }

    /**
     * Returns an enumeration of object class definitions.
     *
     * @return An enumeration of object class definitions.
     */
    public Enumeration getObjectClassSchemas()
    {
        return new EnumeratedIterator (
                idTable[OBJECT_CLASS].values().iterator() );
    }

    /**
     * Returns an enumeration of syntax definitions.
     *
     * @return An enumeration of syntax definitions.
     */
    public Enumeration getSyntaxSchemas()
    {
        return new EnumeratedIterator (
                idTable[SYNTAX].values().iterator() );
    }

// #######################################################################
//  The following methods retrieve an Enumeration of Names of a schema type
// #######################################################################

    /**
     * Returns an enumeration of attribute names.
     *
     * @return An enumeration of attribute names.
     */
    public Enumeration getAttributeNames()
    {
        return new EnumeratedIterator (
                nameTable[ATTRIBUTE].keySet().iterator() );
    }

    /**
     * Returns an enumeration of DIT content rule names.
     *
     * @return An enumeration of DIT content rule names.
     */
    public Enumeration getDITContentRuleNames()
    {
        return new EnumeratedIterator (
                nameTable[DITCONTENT].keySet().iterator() );
    }

    /**
     * Returns an enumeration of DIT structure rule names.
     *
     * @return An enumeration of DIT structure rule names.
     */
    public Enumeration getDITStructureRuleNames()
    {
        return new EnumeratedIterator (
                nameTable[DITSTRUCTURE].keySet().iterator() );
    }

    /**
     * Returns an enumeration of matching rule names.
     *
     * @return An enumeration of matching rule names.
     */
    public Enumeration getMatchingRuleNames()
    {
        return new EnumeratedIterator (
                nameTable[MATCHING].keySet().iterator() );
    }

    /**
     * Returns an enumeration of matching rule use names.
     *
     * @return An enumeration of matching rule use names.
     */
    public Enumeration getMatchingRuleUseNames()
    {
        return new EnumeratedIterator (
                nameTable[MATCHING_USE].keySet().iterator() );
    }

    /**
     * Returns an enumeration of name form names.
     *
     * @return An enumeration of name form names.
     */
    public Enumeration getNameFormNames()
    {
        return new EnumeratedIterator (
                nameTable[NAME_FORM].keySet().iterator() );
    }

    /**
     * Returns an enumeration of object class names.
     *
     * @return An enumeration of object class names.
     */
    public Enumeration getObjectClassNames()
    {
        return new EnumeratedIterator (
                nameTable[OBJECT_CLASS].keySet().iterator() );
    }


    /**
     * This helper function returns a number that represents the type of schema
     * definition the element represents.  The top of this file enumerates
     * these types.
     *
     * @param element   A class extending LDAPSchemaElement.
     *
     * @return      a Number that identifies the type of schema element and
     *              will be one of the following:
     *                      ATTRIBUTE, OBJECT_CLASS, SYNTAX, NAME_FORM,
     *                      DITCONTENT, DITSTRUCTURE, MATCHING, MATCHING_USE
     */
    private int getType( LDAPSchemaElement element )
    {
        if( element instanceof LDAPAttributeSchema)
            return this.ATTRIBUTE;
        else if( element instanceof LDAPObjectClassSchema)
            return this.OBJECT_CLASS;
        else if( element instanceof LDAPSyntaxSchema)
            return this.SYNTAX;
        else if( element instanceof LDAPNameFormSchema)
            return this.NAME_FORM;
        else if( element instanceof LDAPMatchingRuleSchema)
            return this.MATCHING;
        else if( element instanceof LDAPMatchingRuleUseSchema)
            return this.MATCHING_USE;
        else if( element instanceof LDAPDITContentRuleSchema)
            return this.DITCONTENT;
        else if( element instanceof LDAPDITStructureRuleSchema)
            return this.DITSTRUCTURE;
        else
            throw new IllegalArgumentException(
                "The specified schema element type is not recognized");
    }

// #######################################################################
//  The following methods are deprecated and will be removed Fall 2003
// #######################################################################

    /**
     * <p>This method
     *  has been renamed to getAttributeSchemas in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  from the API in the fall of 2003.</p>
     *
     *  @deprecated replaced by {@link #getAttributeSchemas}.
     */
    public Enumeration getAttributes()
    {
        return this.getAttributeSchemas();
    }

    /**
     *  This method has been renamed to getDITContentRuleSchema in IETF draft 17
     *  of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be
     *  removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getDITContentRuleSchema}.
     */
    public LDAPDITContentRuleSchema getDITContentRule(String name)
    {
        return this.getDITContentRuleSchema(name);
    }

    /**
     *  This method has been renamed to getDITContentRuleSchemas in IETF draft 17 of the
     *  Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be
     *  removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getDITContentRuleSchemas}.
     */
    public Enumeration getDITContentRules()
    {
        return this.getDITContentRuleSchemas();
    }

    /**
     *  This method has been renamed to getDITStructureRuleSchema in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getDITStructureRuleSchema}.
     */
    public LDAPDITStructureRuleSchema getDITStructureRule(int id)
    {
        return this.getDITStructureRuleSchema(id);
    }

    /**
     *  This method has been renamed to getDITStructureRuleSchemas in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getDITStructureRuleSchemas}.
     */
    public Enumeration getDITStructureRules()
    {
        return this.getDITStructureRuleSchemas();
    }

    /**
     *  This method has been renamed to getMatchingRuleSchema in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getMatchingRuleSchema}.
     */
    public LDAPMatchingRuleSchema getMatchingRule(String name)
    {
        return this.getMatchingRuleSchema(name);
    }

    /**
     *  This method has been renamed to getMatchingRuleSchemas in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getMatchingRuleSchemas}.
     */
    public Enumeration getMatchingRules()
    {
        return this.getMatchingRuleSchemas();
    }
    /**
     *  This method has been renamed to getMatchingRuleUseSchema in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getMatchingRuleUseSchema}.
     */
    public LDAPMatchingRuleUseSchema getMatchingRuleUse(String name)
    {
        return this.getMatchingRuleUseSchema(name);
    }

    /**
     *  This method has been renamed to getMatchingRuleUseSchemas in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getMatchingRuleUseSchemas}.
     */
    public Enumeration getMatchingRuleUses()
    {
        return this.getMatchingRuleUseSchemas();
    }

    /**
     *  This method
     *  has been renamed to getNameFormSchema in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getNameFormSchema}.
     */
    public LDAPNameFormSchema getNameForm(String name)
    {
        return this.getNameFormSchema(name);
    }

    /**
     *  This method has been renamed to getNameFormSchemas in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getNameFormSchemas}.
     */
    public Enumeration getNameForms()
    {
        return this.getNameFormSchemas();
    }

    /**
     *  This method has been renamed to getObjectClassSchema in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getObjectClassSchema}.
     */
    public LDAPObjectClassSchema getObjectClass(String name)
    {
        return this.getObjectClassSchema(name);
    }

    /**
     *  This method has been renamed to getObjectClassSchemas in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getObjectClassSchemas}.
     */
    public Enumeration getObjectClasses()
    {
        return this.getObjectClassSchemas();
    }

    /**
     *  This method has been renamed to getSyntaxSchema in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getSyntaxSchema}.
     */
    public LDAPSyntaxSchema getSyntax(String name)
    {
        return this.getSyntaxSchema(name);
    }

    /**
     *  This method has been renamed to getSyntaxSchemas in IETF
     *  draft 17 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.
     *
     *  @deprecated replaced by {@link #getSyntaxSchemas}.
     */
    public Enumeration getSyntaxes()
    {
        return this.getSyntaxSchemas();
    }

/***********************changes since draft 17*******************/

    /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.</p>
     *
     *  @deprecated replaced by {@link #LDAPSchema(LDAPEntry)} and
     *  {@link LDAPConnection#fetchSchema}.
     */
    public LDAPSchema() {
        for (int i=0; i< schemaTypeNames.length; i++) {
            idTable[i]  = new HashMap();
            nameTable[i] = new HashMap();
        }
        return;
    }

   /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.</p>
     *
     *  @deprecated replaced by {@link LDAPConnection#fetchSchema LDAPConnection.fetchSchema()}.
    */
   public void fetchSchema(LDAPConnection ld) throws LDAPException
   {
        fetchSchema(ld,"");
        return;
   }

    /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.</p>
     *
     *  @deprecated replaced by {@link LDAPConnection#fetchSchema LDAPConnection.fetchSchema()}.
     */
    public void fetchSchema(LDAPConnection ld, String dn) throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.fetchSchema()");
        }
        LDAPSchema newschema = ld.fetchSchema( ld.getSchemaDN(dn) );
        this.dn = newschema.dn;
        this.attrs = newschema.attrs;
        return;
    }
    /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of 2003.</p>
     *
     * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
     * be used directly with {@link LDAPModification},
     * see {@link LDAPConnection#modify LDAPConnection.modify()}.</p>
     *
     * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
     */
    public void saveSchema (LDAPConnection ld) throws LDAPException
    {
        saveSchema(ld, "");
        return;
    }

    /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of
     *  2003.</p>
     *
     * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
     * be used directly with {@link LDAPModification},
     * see {@link LDAPConnection#modify LDAPConnection.modify()}.</p>
     *
     * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
     */
    public void saveSchema (LDAPConnection ld, String dn) throws LDAPException
    {
        String schemaDN = ld.getSchemaDN( dn );
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.saveSchema(), " +
                    "subschemaSubentry of \"" + dn + "\" = " + schemaDN );
        }
        ld.modify(schemaDN, schemaChanges );

        //clear schemaChanges
        schemaChanges = new LDAPModificationSet();
        return;
    }

// #######################################################################
//  The following methods add, modify, and remove schema definitions
// ########################################################################

    /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of
     *  2003.</p>
     *
     * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
     * be used directly with {@link LDAPModification},
     * see {@link LDAPConnection#modify LDAPConnection.modify()}.</p>
     *
     * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
     */

    public void add(LDAPSchemaElement element)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.add()");
        }

        /* first decide which HashMaps to use */
        int schemaType = getType( element );

        // Check if the names or OID is already used (in that schema type)
        if( this.idTable[schemaType].containsKey( element.getID() ))
            throw new IllegalArgumentException(
                "The ID, "+ element.getID() +", is already in use");

        String names[] = element.getNames();
        for (int i=0; i<names.length; i++) {
            if( idTable[schemaType].containsKey( names[i].toUpperCase() ))
                throw new IllegalArgumentException(
                        "The name, " + names[i] +", is already in use");
        }

        /* The element's id and names are unique. Add it to the idTable, and
            namesTable (via addElement), and add the change to schemaChanges.*/
        addElement( schemaType, element );
        LDAPAttribute schemaDef =
             new LDAPAttribute(schemaTypeNames[schemaType], element.toString());
        schemaChanges.add( LDAPModification.ADD, schemaDef );
        return;
    }

    /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall ofu
     *  2003.</p>
     *
     * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
     * be used directly with {@link LDAPModification},
     * see {@link LDAPConnection#modify LDAPConnection.modify()}.</p>
     *
     * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
     */
    public void modify( LDAPSchemaElement element )
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.modify()");
        }
        int schemaType = getType( element );

        // Locate the old Definition
        LDAPSchemaElement oldElement =
            (LDAPSchemaElement) idTable[schemaType].get( element.getID() );

        if( oldElement != null)
            remove( oldElement );
        add ( element );
        return;
    }

    /**
     *  <p>This method has been removed in IETF
     *  draft 18 of the Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt)
     *  and will be removed from the LDAP Classes for Java API in the fall of
     *  2003.</p>
     *
     * <p>LDAPSchemaElement now extends {@link LDAPAttribute} and can
     * be used directly with {@link LDAPModification},
     * see {@link LDAPConnection#modify LDAPConnection.modify()}.</p>
     *
     * @deprecated replaced by {@link LDAPConnection#modify LDAPConnection.modify()}.
     */
    public void remove( LDAPSchemaElement element )
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.remove()");
        }
        int schemaType = getType( element );

        // Remove from the ID list
        if( idTable[schemaType].containsKey( element.getID() )) {
             idTable[schemaType].remove( element.getID() );

            // Remove from the Names list
            String names[] = element.getNames();
            for (int i=0; i< names.length; i++) {
                if( nameTable[schemaType].containsKey( names[i] )) {
                    nameTable[schemaType].remove( names[i].toUpperCase() ) ;
                }
            }
            /* Save this deletion to schemaChanges */
            LDAPAttribute schemaDef =
                 new LDAPAttribute( schemaTypeNames[schemaType],
                                    element.toString() );
            this.schemaChanges.add( LDAPModification.DELETE, schemaDef );
        }
        return;
    }
}
