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

import com.novell.ldap.client.Debug;
import java.util.Enumeration;
import java.util.Hashtable;
import com.novell.ldap.resources.ExceptionMessages;

/**
 *  Represents a local copy of the schema that controls one or more entries
 *  held by a Directory Server.
 *
 * <p>The fetchSchema method populates this object with a local copy of schema
 * definitions.  The methods add, modify and remove change the local
 * copy of the schema definitions.  These changes are then commited to the
 * directory by calling the method saveSchema.  The methods fetchSchema and
 * saveSchema are the only methods that interact with the Directory Server.</p>
 *
 * <p>Other methods use the local copy of the schema to retrieve individual
 * schema definitions, represented by subclasses of
 * <code>LDAPSchemaElement</code>.</p>
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/
 *jldap_sample/jldap_sample/ExtendSchema.java.html">ExtendSchema.java</p>
 *
 * @see LDAPSchemaElement
 */
public class LDAPSchema {

    LDAPModificationSet schemaChanges;

    /** The idTable hash on the oid (or integer ID for DITStructureRule) and
     *  is used for retrieving enumerations
     */
    private Hashtable idTable[] = new Hashtable[8];

    /** The nameTable will hash on the names (if available). To insure
     *  case-insensibility, the Keys for this table will be a String cast to
     *  Uppercase.
     */
    private Hashtable nameTable[] = new Hashtable[8];

    /** The following lists the LDAP names of subschema attributes for
     *  schema definitions:
     */
    private static final String[] schemaTypeNames =
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

    /** the following are indexes to the above three arrays */
    private static final int ATTRIBUTE      = 0;
    private static final int OBJECT_CLASS   = 1;
    private static final int SYNTAX         = 2;
    private static final int NAME_FORM      = 3;
    private static final int DITCONTENT     = 4;
    private static final int DITSTRUCTURE   = 5;
    private static final int MATCHING       = 6;
    private static final int MATCHING_USE   = 7;

   /**
    * Constructs an empty LDAPSchema object.
    */
   public LDAPSchema() {
        for (int i=0; i< schemaTypeNames.length; i++) {
            idTable[i]  = new Hashtable();
            nameTable[i] = new Hashtable();
        }
        schemaChanges = new LDAPModificationSet();
        return;
   }

   /**
    * Retrieves the entire schema from a directory server and makes a local
    * copy of the schema definitions.
    *
    * <p>The schema entry is located by reading the Root DSE subschemaSubentry
    * attribute.  This is equivalent to calling
    * {@link #fetchSchema(LDAPConnection, String) } with the DN parameter as
    * an empty string: <code>fetchSchema(ld, "")</code>.
    *
    *  @param ld       An open connection to a directory server.
    *
    *  @exception LDAPException     This exception occurs if more than one
    *       subschemaSubentry attribute is found. In which case the result code
    *       will be CONSTRAINT_VIOLATION.
    *  @exception LDAPException     This exception also occurs in the
    *       LDAPConnection.read method if the schema cannot be retrieved with
    *       the specified connection.
    */
   public void fetchSchema(LDAPConnection ld) throws LDAPException
   {
        fetchSchema(ld,"");
        return;
   }

    /**
     * Retrieves the schema in effect at a particular entry in a directory
     * server and makes a local copy of the schema definitions.
     *
     * <p>The subschemaSubentry attribute of the entry identified by the
     * distinguished name, <code>dn</code>, is queried to find the location of
     * the schema definitions in effect for that entry.</p>
     *
     *  @param ld       An open connection to a directory server.
     *<br><br>
     *  @param dn       The distinguished name of the entry from which to
     *                  identify schema.
     *
     *  @exception LDAPException     This exception occurs if more than one
     *       subschemaSubentry attribute is found. In which case the result
     *       code will be CONSTRAINT_VIOLATION.
     *  @exception LDAPException     This exception also occurs in the
     *       LDAPConnection.read method if the schema cannot be retrieved with
     *       the specified connection.
     */
    public void fetchSchema(LDAPConnection ld,
                           String dn) throws LDAPException
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.fetchSchema()");
        }

        //reset all definitions
        for (int i=0; i< schemaTypeNames.length; i++) {
            idTable[i].clear();
            nameTable[i].clear();
        }
        schemaChanges = new LDAPModificationSet();

        /* get the subschemaSubentry DN from the ld passed in */
        String schemaDN = getSchemaDN(ld, dn);

        /* Read the schema definitions.  If no entry is found an
         * Exception is thrown */
        LDAPEntry ent = ld.read(schemaDN, this.schemaTypeNames);

        LDAPAttributeSet attrSet = ent.getAttributeSet();
        Enumeration      en = attrSet.getAttributes();

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
                            Debug.trace( Debug.apiRequests, "fetchSchema could not "+
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
            continue;
        }
        return;
    }

    /**
     * Saves any schema changes, made to this local copy of schema, to a
     * Directory Server.
     *
     * </p> This is equivalent to calling
     * {@link #saveSchema(LDAPConnection, String) } with the DN parameter as an
     * empty string: <code>saveSchema(ld, "")</code>.
     * saveSchema will use the Root DSE as the entry at which to find the
     * subschemaSubentry.  </p>
     *
     * <p>All changes are submitted as a single transactional
     * request to the Directory Server.  This will include all changes which
     * have been made to the object using the {@link #add add},
     * {@link #modify modify}, or {@link #remove remove} methods since the
     * object was contructed or since <code>saveSchema</code> last completed
     * successfully. </p>
     *
     * @param   ld  An open connection to a Directory Server.
     *
     * @exception LDAPException     Occurs with the result CONTRAINT_VIOLATION
     *          if more than one subschemaSubentry value is found in the entry.
     * @exception LDAPException     Occurs if the schema changes cannot be saved
     */
    public void saveSchema (LDAPConnection ld) throws LDAPException
    {
        saveSchema(ld, "");
        return;
    }

    /**
     * Saves any schema changes, made to this local copy of schema, to a
     * Directory Server.
     *
     * <p>The subschemaSubentry attribute of the entry identified by the
     * distinguished name, <code>dn</code>, is queried to find the location of
     * the schema definitions in effect for that entry.</p>
     *
     * </p>All changes are submitted as a single transactional
     * request to the Directory Server.  This will include all changes which
     * have been made to the object using the {@link #add add},
     * {@link #modify modify}, or {@link #remove remove} methods since the
     * object was contructed or since <code>saveSchema</code> last completed
     * successfully. </p>
     *
     * @exception LDAPException     Occurs with the result CONTRAINT_VIOLATION
     *          if more than one subschemaSubentry value is found in the entry.
     * @exception LDAPException     Occurs if the schema changes cannot be saved
     */
    public void saveSchema (LDAPConnection ld, String dn) throws LDAPException
    {
        String schemaDN = getSchemaDN( ld, dn );
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.saveSchema(), " +
                    "subschemaSubentry of \"" + dn + "\" = " + schemaDN );
        }
        ld.modify(schemaDN, schemaChanges );

        //clear schemaChanges
        schemaChanges = new LDAPModificationSet();
        return;
    }

    /**
     * Reads the subschemaSubentry of the entry specified. Used by fetchSchema
     * and saveSchema.
     *
     * @param ld     An open connection to a directory server.
     *
     * @param dn     Distinguished name of any entry.  The subschemaSubentry
     *               attribute is queried from this entry.
     *
     * @return      Distinguished Name of a schema entry in effect for the entry
     *               identified by <code>dn</code>.
     *
     * @exception LDAPException     This exception occurs if more than one
     *       subschemaSubentry attribute is found. In which case the result code
     *       will be CONSTRAINT_VIOLATION.
     */
    private String getSchemaDN( LDAPConnection ld, String dn )
        throws LDAPException
    {
        String attrSubSchema[] = { "subschemaSubentry" };

        /* Read the entries subschemaSubentry attribute. Throws an exception if
         * no entries are returned. */
        LDAPEntry ent = ld.read( dn, attrSubSchema );

        String schemaDN;
        LDAPAttribute attr = ent.getAttribute( attrSubSchema[0] );
        String values[] = attr.getStringValueArray();
        if( values == null || values.length < 1 ) {
            throw new LDAPException(
                ExceptionMessages.NO_SCHEMA,
                new Object[] { dn },
                LDAPException.NO_RESULTS_RETURNED);
        }
        else if( values.length > 1 ) {
            throw new LDAPException(
                ExceptionMessages.MULTIPLE_SCHEMA,
                new Object[] { dn },
                LDAPException.CONSTRAINT_VIOLATION);
        }
        return values[0];
   }

    /**
     * Adds the schema definition to the idList and nameList hashTables.
     * This method is used by the methods fetchSchema and add.
     *
     * Note that the nameTable has all keys cast to Upper-case.  This is so we
     * can have a case-insensitive HashTable.  The getXXX (String key) methods
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

/*#######################################################################
   The following methods retrieve a SchemaElement given a Key name:
 ########################################################################*/

    /**
     * This function abstracts retrieving LDAPSchemaElements from the local
     * copy of schema in this LDAPSchema class.  This is used by
     * <code>getXXX(String name)</code> functions.
     *
     * <p>Note that the nameTable has all keys cast to Upper-case.  This is so
     * we can have a case-insensitive HashTable.  The getXXX (String key)
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

/*#######################################################################
 The following methods return an Enumeration of SchemaElements by schema type:
 ########################################################################*/

    /**
     * Returns an enumeration of attribute definitions.
     *
     * @return An enumeration of attribute definitions.
     */
    public Enumeration getAttributeSchemas()
    {
        return idTable[ATTRIBUTE].elements();
    }

    /**
     * Returns an enumeration of DIT content rule definitions.
     *
     * @return An enumeration of DIT content rule definitions.
     */
    public Enumeration getDITContentRuleSchemas()
    {
        return idTable[DITCONTENT].elements();
    }

    /**
     * Returns an enumeration of DIT structure rule definitions.
     *
     * @return An enumeration of DIT structure rule definitions.
     */
    public Enumeration getDITStructureRuleSchemas()
    {
        return idTable[DITSTRUCTURE].elements();
    }

    /**
     * Returns an enumeration of matching rule definitions.
     *
     * @return An enumeration of matching rule definitions.
     */
    public Enumeration getMatchingRuleSchemas()
    {
        return idTable[MATCHING].elements();
    }

    /**
     * Returns an enumeration of matching rule use definitions.
     *
     * @return An enumeration of matching rule use definitions.
     */
    public Enumeration getMatchingRuleUseSchemas()
    {
        return idTable[MATCHING_USE].elements();
    }

    /**
     * Returns an enumeration of name form definitions.
     *
     * @return An enumeration of name form definitions.
     */
    public Enumeration getNameFormSchemas()
    {
        return idTable[NAME_FORM].elements();
    }

    /**
     * Returns an enumeration of object class definitions.
     *
     * @return An enumeration of object class definitions.
     */
    public Enumeration getObjectClassSchemas()
    {
        return idTable[OBJECT_CLASS].elements();
    }

    /**
     * Returns an enumeration of syntax definitions.
     *
     * @return An enumeration of syntax definitions.
     */
    public Enumeration getSyntaxSchemas()
    {
        return idTable[SYNTAX].elements();
    }

/*#######################################################################
   The following methods retrieve an Enumeration of Names of a schema type
 ########################################################################*/

    /**
     * Returns an enumeration of attribute names.
     *
     * @return An enumeration of attribute names.
     */
    public Enumeration getAttributeNames()
    {
        return nameTable[ATTRIBUTE].keys();
    }

    /**
     * Returns an enumeration of DIT content rule names.
     *
     * @return An enumeration of DIT content rule names.
     */
    public Enumeration getDITContentRuleNames()
    {
        return nameTable[DITCONTENT].keys();
    }

    /**
     * Returns an enumeration of DIT structure rule names.
     *
     * @return An enumeration of DIT structure rule names.
     */
    public Enumeration getDITStructureRuleNames()
    {
        return nameTable[DITSTRUCTURE].keys();
    }

    /**
     * Returns an enumeration of matching rule names.
     *
     * @return An enumeration of matching rule names.
     */
    public Enumeration getMatchingRuleNames()
    {
        return nameTable[MATCHING].keys();
    }

    /**
     * Returns an enumeration of matching rule use names.
     *
     * @return An enumeration of matching rule use names.
     */
    public Enumeration getMatchingRuleUseNames()
    {
        return nameTable[MATCHING_USE].keys();
    }

    /**
     * Returns an enumeration of name form names.
     *
     * @return An enumeration of name form names.
     */
    public Enumeration getNameFormNames()
    {
        return nameTable[NAME_FORM].keys();
    }

    /**
     * Returns an enumeration of object class names.
     *
     * @return An enumeration of object class names.
     */
    public Enumeration getObjectClassNames()
    {
        return nameTable[OBJECT_CLASS].keys();
    }


/*#######################################################################
   The following methods add, modify, and remove schema definitions
 ########################################################################*/

    /**
     * Adds a schema element definition to the local copy of schema in this
     * object.
     *
     * <p>Changes to schema by this and other methods are not commited to a
     * directory server until the method {@link #saveSchema} is called. </p>
     *
     * @param element   Definition of a schema element to be added to this
     *                  schema object.
     * @exception IllegalArgumentException
     *                  if the schema object already contains an element of the
     *                  same name or OID.
     * @see #saveSchema
     */
    public void add(LDAPSchemaElement element)
    {
        if( Debug.LDAP_DEBUG) {
            Debug.trace( Debug.apiRequests, "LDAPSchema.add()");
        }

        /* first decide which hashTables to use */
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
     * Modifies a schema element definition to the local copy schema in this
     * object.
     *
     * <p>The element that has the same schema type and OID as
     * <code>element</code> is replaced by the element specified.  If the
     * schema object does not contain an element with the schema type and OID
     * of <code>element,</code> it is added.</p>
     *
     * <p>Changes to schema by this and other methods are not commited to a
     * directory server until the method {@link #saveSchema} is called. </p>
     *
     * @param element   A non-null schema definition
     * @see #saveSchema
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
     * Removes a schema element definition from the local copy of schema in
     * this object.
     *
     * <p>If the schema object does not contain an element with the same schema
     * type and OID as <code>element</code>, nothing happens. </p>
     *
     * <p>Changes to schema by this and other methods are not commited to a
     * directory server until the method {@link #saveSchema} is called. </p>
     *
     * @param element   A non-null schema definition
     * @see #saveSchema
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

/*#######################################################################
   The following methods are deprecated and will be removed Fall 2003
 ########################################################################*/

    /**
     *  @deprecated replaced by {@link #getAttributeSchema}.  This method
     *  has been renamed to getAttributeSchema in IETF draft 17 of the Java LDAP
     *  API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public LDAPAttributeSchema getAttribute(String name)
    {
        return this.getAttributeSchema(name);
    }

    /**
     *  @deprecated replaced by {@link #getAttributeSchemas}.  This method
     *  has been renamed to getAttributeSchemas in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public Enumeration getAttributes()
    {
        return this.getAttributeSchemas();
    }

    /**
     *  @deprecated replaced by {@link #getDITContentRuleSchema}.  This method
     *  has been renamed to getDITContentRuleSchema in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public LDAPDITContentRuleSchema getDITContentRule(String name)
    {
        return this.getDITContentRuleSchema(name);
    }

    /**
     *  @deprecated replaced by {@link #getDITContentRuleSchemas}.  This method
     *  has been renamed to getDITContentRuleSchemas in IETF draft 17 of the
     *  Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be
     *  removed in fall of 2003.
     */
    public Enumeration getDITContentRules()
    {
        return this.getDITContentRuleSchemas();
    }

    /**
     *  @deprecated replaced by {@link #getDITStructureRuleSchema}.  This method
     *  has been renamed to getDITStructureRuleSchema in IETF draft 17 of the
     *  Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be
     *  removed in fall of 2003.
     */
    public LDAPDITStructureRuleSchema getDITStructureRule(int id)
    {
        return this.getDITStructureRuleSchema(id);
    }

    /**
     *  @deprecated replaced by {@link #getDITStructureRuleSchemas}. This method
     *  has been renamed to getDITStructureRuleSchemas in IETF draft 17 of the
     *  Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be
     *  removed in fall of 2003.
     */
    public Enumeration getDITStructureRules()
    {
        return this.getDITStructureRuleSchemas();
    }

    /**
     *  @deprecated replaced by {@link #getMatchingRuleSchema}.  This method
     *  has been renamed to getMatchingRuleSchema in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public LDAPMatchingRuleSchema getMatchingRule(String name)
    {
        return this.getMatchingRuleSchema(name);
    }

    /**
     *  @deprecated replaced by {@link #getMatchingRuleSchemas}.  This method
     *  has been renamed to getMatchingRuleSchemas in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public Enumeration getMatchingRules()
    {
        return this.getMatchingRuleSchemas();
    }
    /**
     *  @deprecated replaced by {@link #getMatchingRuleUseSchema}.  This method
     *  has been renamed to getMatchingRuleUseSchema in IETF draft 17 of the
     *  Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be
     *  removed in fall of 2003.
     */
    public LDAPMatchingRuleUseSchema getMatchingRuleUse(String name)
    {
        return this.getMatchingRuleUseSchema(name);
    }

    /**
     *  @deprecated replaced by {@link #getMatchingRuleUseSchemas}.  This method
     *  has been renamed to getMatchingRuleUseSchemas in IETF draft 17 of the
     *  Java LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be
     *  removed in fall of 2003.
     */
    public Enumeration getMatchingRuleUses()
    {
        return this.getMatchingRuleUseSchemas();
    }

    /**
     *  @deprecated replaced by {@link #getNameFormSchema}.  This method
     *  has been renamed to getNameFormSchema in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public LDAPNameFormSchema getNameForm(String name)
    {
        return this.getNameFormSchema(name);
    }

    /**
     *  @deprecated replaced by {@link #getNameFormSchemas}.  This method
     *  has been renamed to getNameFormSchemas in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public Enumeration getNameForms()
    {
        return this.getNameFormSchemas();
    }

    /**
     *  @deprecated replaced by {@link #getObjectClassSchema}.  This method
     *  has been renamed to getObjectClassSchema in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public LDAPObjectClassSchema getObjectClass(String name)
    {
        return this.getObjectClassSchema(name);
    }

    /**
     *  @deprecated replaced by {@link #getObjectClassSchemas}.  This method
     *  has been renamed to getObjectClassSchemas in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public Enumeration getObjectClasses()
    {
        return this.getObjectClassSchemas();
    }

    /**
     *  @deprecated replaced by {@link #getSyntaxSchema}.  This method
     *  has been renamed to getSyntaxSchema in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public LDAPSyntaxSchema getSyntax(String name)
    {
        return this.getSyntaxSchema(name);
    }

    /**
     *  @deprecated replaced by {@link #getSyntaxSchemas}.  This method
     *  has been renamed to getSyntaxSchemas in IETF draft 17 of the Java
     *  LDAP API (draft-ietf-ldapext-ldap-java-api-xx.txt) and will be removed
     *  in fall of 2003.
     */
    public Enumeration getSyntaxes()
    {
        return this.getSyntaxSchemas();
    }
}
