/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPObjectClassSchema.java,v 1.5 2000/08/28 22:18:57 vtag Exp $
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

/*
 * 4.17 public class LDAPObjectClassSchema
 */
 
/**
 *
 *  Represents the definition of an object class. 
 *
 *  <p>The LDAPObjectClassSchema class is used to query the syntax of an object
 *  class, and to add or delete an object class definition from a directory's
 *  schema. </p>
 *
 */
public class LDAPObjectClassSchema extends LDAPSchemaElement{
  String[] superiors;
  String[] required;
  String[] optional;
  int type;

 /**
  * This class definition defines an abstract schema class.
  */
  public final static int ABSTRACT = 0;
  
 /**
  * This class definition defines a structural schema class.
  */
  public final static int STRUCTURAL = 1;
  
 /**
  * This class definition defines an auxiliary schema class.
  */
  public final static int AUXILIARY = 2;

   /*
    * 4.17.1 Constructors
    */

   /**
    * Constructs an object class definition for adding to or deleting from
    * a directory's schema.
    *
    *  @param name      Name of the object class.
    *<br><br>
    *  @param oid       Unique object identifer of the object class, in
    *                   dotted numerical format.
    *<br><br>
    *  @param description    Optional description of the object class.
    *<br><br>
    *  @param superiors      The object classes from which this one derives.
    *<br><br>
    *  @param required       A list of attributes required for an entry with
    *                        this object class.
    *<br><br>
    *  @param optional       A list of attributes acceptable but not required
    *                        for an entry with this object class.
    *<br><br>
    *  @param type           The type of class. Must be one of following:
    *                        ABSTRACT, AUXILIARY, or STRUCTURAL. These
    *                        constants are defined in LDAPObjectClassSchema.
    *<br><br>
    *  @param aliases        Optional list of additional names by which the
    *                        object class may be known; null if there are no
    *                        aliases.
    *
    */
   public LDAPObjectClassSchema(String name,
                                String oid,
                                String[] superiors,
                                String description,
                                String[] required,
                                String[] optional,
                                int type,
                                String[] aliases) {
      super.name = new String(name);
      super.oid = new String(oid);
      super.description = new String(description);
      this.type = type;
      if( superiors != null){
        this.superiors = new String[superiors.length];
	for( int i = 0; i < this.superiors.length; i++ ){
	  this.superiors[i] = superiors[i];
        }
      }
      if( optional != null){
        this.optional = new String[optional.length];
	for( int i = 0; i < this.optional.length; i++ ){
	  this.optional[i] = optional[i];
        }
      }

      if( aliases != null){
        super.aliases = new String[aliases.length];
	for( int i = 0; i < super.aliases.length; i++ ){
	  super.aliases[i] = aliases[i];
        }
       }
   }

   /**
    * Constructs an object class definition from the raw string value
    * returned on a directory query for "objectClasses".
    *
    *  @param raw      The raw string value returned on a directory
    *                  query for "objectClasses".
    */
   public LDAPObjectClassSchema(String raw) {
   }

   /*
    * 4.17.2 getSuperiors
    */

   /**
    * Returns the object classes from which this one derives.
    *
    * @return The object classes superior to this class.
    */
   public String[] getSuperiors() {
      return null;
   }

   /*
    * 4.17.3 getRequiredAttributes
    */

   /**
    * Returns a list of attributes required of an entry with this object
    * class.
    *
    * @return The list of required attributes defined for this class.
    */
   public String[] getRequiredAttributes() {
      return null;
   }

   /*
    * 4.17.4 getOptionalAttributes
    */

   /**
    * Returns a list of optional attributes but not required of an entry
    * with this object class.
    *
    * @return The list of optional attributes defined for this class.
    */
   public String[] getOptionalAttributes() {
      return null;
   }

   /*
    * 4.17.5 getType
    */

   /**
    * Returns one of ABSTRACT, AUXILIARY, or STRUCTURAL. These are
    * constants defined in LDAPObjectClassSchema.
    *
    * @return The type of object class.
    */
   public int getType() {
      return type;
   }

}
