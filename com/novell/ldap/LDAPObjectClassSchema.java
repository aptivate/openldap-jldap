/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPObjectClassSchema.java,v 1.10 2000/10/09 19:11:24 vtag Exp $
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
 * 4.23 public class LDAPObjectClassSchema
 */
 
/**
 *
 *  Represents the definition of an object class. 
 *
 *  <p>The LDAPObjectClassSchema class is used to query for the definition of an 
 *  object class, and to add or delete an object class definition from a 
 *  directory's schema. </p>
 *
 */
public class LDAPObjectClassSchema extends LDAPSchemaElement{
  String[] superiors;
  String[] required;
  String[] optional;
  int type;

 /**
  * This class definition defines an abstract schema class.
  *
  * <p>This is equivalent to setting the NDS effective class flag to true.</p>
  */
  public final static int ABSTRACT = 0;
  
 /**
  * This class definition defines a structural schema class.
  *
  * <p>This is equivalent to setting the NDS effective class flag to true.</p>
  */
  public final static int STRUCTURAL = 1;
  
 /**
  * This class definition defines an auxiliary schema class.
  */
  public final static int AUXILIARY = 2;

   /*
    * 4.23.1 Constructors
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
    *  @param superiors      An array of names for object classes from which 
    *                        this one inherits.
    *<br><br>
    *  @param required       An array of names for attributes which are required 
    *                        for an entry with this object class.
    *<br><br>
    *  @param optional       An array of names for attributes which are optional
    *                        but not required for an entry with this object class.
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
    * returned from a directory query for "objectClasses".
    *
    *  @param raw      The raw string value returned from a directory
    *                  query for "objectClasses".
    */
   public LDAPObjectClassSchema(String raw) {
      throw new RuntimeException("Constructor LDAPObjectClassSchema(String raw) not implemented");
   }

   /*
    * 4.23.2 getSuperiors
    */

   /**
    * Returns the object classes from which this one derives.
    *
    * @return The object classes superior to this class.
    */
   public String[] getSuperiors() {
      throw new RuntimeException("Method LDAPObjectClassSchema.getSuperiors not implemented");
   }

   /*
    * 4.23.3 getRequiredAttributes
    */

   /**
    * Returns a list of attributes required for an entry with this object
    * class.
    *
    * @return The list of required attributes defined for this class.
    */
   public String[] getRequiredAttributes() {
      throw new RuntimeException("Method LDAPObjectClassSchema.getRequiredAttributes not implemented");
   }

   /*
    * 4.23.4 getOptionalAttributes
    */

   /**
    * Returns a list of optional attributes but not required of an entry
    * with this object class.
    *
    * @return The list of optional attributes defined for this class.
    */
   public String[] getOptionalAttributes() {
      throw new RuntimeException("Method LDAPObjectClassSchema.getOptionalAttributes not implemented");
   }

   /*
    * 4.23.5 getType
    */

   /**
    * Returns the type of object class.
    *
    *  <p>The getType method returns one of the following constants defined in 
    *  LDAPObjectClassSchema:
    * <ul>
    *   <li>ABSTRACT</li>
    *   <li>AUXILIARY</li>
    *   <li>STRUCTURAL</li> 
    *</ul>
    *<p>See the LDAPSchemaElement.getQualifier method for information on 
    * obtaining the X-NDS flags.</p>
    *
    * @return The type of object class.
    */
   public int getType() {
      return type;
   }

}
