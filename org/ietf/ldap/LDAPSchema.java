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

package org.ietf.ldap;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 *  Represents the schema controlling one or more entries held by a
 * Directory Server.
 *
 *  @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html">
            com.novell.ldap.LDAPSchema</a>
 */
public class LDAPSchema extends LDAPEntry
{

    private com.novell.ldap.LDAPSchema schema;

    /*package*/
    LDAPSchema( com.novell.ldap.LDAPSchema novellschema){
        schema = novellschema;
        return;
    }

    /**
     * Constructs an LDAPSchema object from the attributes of an LDAPEntry.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#LDAPSchema(com.novell.ldap.LDAPEntry)">
            com.novell.ldap.LDAPSchema.LDAPSchema(LDAPEntry)</a>
     */
    public LDAPSchema(LDAPEntry entry)
    {
        schema = new com.novell.ldap.LDAPSchema(entry.getWrappedObject());
        return;
    }


    /**
     * Returns a particular attribute definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getAttributeSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getAttributeSchema(String)</a>
     */
    public LDAPAttributeSchema getAttributeSchema( String name )
    {
        com.novell.ldap.LDAPAttributeSchema attr = schema.getAttributeSchema( name);
        if( attr == null) {
            return null;
        }
        return new LDAPAttributeSchema( attr );
    }

    /**
     * Returns a particular object class definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getObjectClassSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getObjectClassSchema(String)</a>
     */
    public LDAPObjectClassSchema getObjectClassSchema( String name )
    {
        com.novell.ldap.LDAPObjectClassSchema obj = schema.getObjectClassSchema(name);
        if( obj == null) {
            return null;
        }
        return new LDAPObjectClassSchema( obj);
    }

    /**
     * Returns a particular matching rule definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getMatchingRuleSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getMatchingRuleSchema(String)</a>
     */
    public LDAPMatchingRuleSchema getMatchingRuleSchema( String name )
    {
        com.novell.ldap.LDAPMatchingRuleSchema match = 
                                    schema.getMatchingRuleSchema(name);
        if( match == null) {
            return null;
        }
        return new LDAPMatchingRuleSchema( match);
    }

    /**
     * Returns a particular matching rule use definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getMatchingRuleUseSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getMatchingRuleUseSchema(String)</a>
     */
    public LDAPMatchingRuleUseSchema getMatchingRuleUseSchema( String name )
    {
        com.novell.ldap.LDAPMatchingRuleUseSchema match = 
                                    schema.getMatchingRuleUseSchema(name);
        if( match == null) {
            return null;
        }
        return new LDAPMatchingRuleUseSchema( match);
    }

	/**
     * Returns a particular DIT structure rule definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getDITStructureRuleSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getDITStructureRuleSchema(String)</a>
     */
    public LDAPDITStructureRuleSchema getDITStructureRuleSchema( String name )
    {
        com.novell.ldap.LDAPDITStructureRuleSchema rule = 
                                    schema.getDITStructureRuleSchema(name);
        if( rule == null) {
            return null;
        }
        return new LDAPDITStructureRuleSchema( rule);
    }

    /**
     * Returns a particular DIT structure rule definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getDITStructureRuleSchema(int)">
            com.novell.ldap.LDAPSchema.getDITStructureRuleSchema(int)</a>
     */
    public LDAPDITStructureRuleSchema getDITStructureRuleSchema( int id )
    {
        com.novell.ldap.LDAPDITStructureRuleSchema rule = 
                                    schema.getDITStructureRuleSchema(id);
        if( rule == null) {
            return null;
        }
        return new LDAPDITStructureRuleSchema( rule);
    }

    /**
     * Returns a particular DIT content rule definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getDITContentRuleSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getDITContentRuleSchema(String)</a>
     */
    public LDAPDITContentRuleSchema getDITContentRuleSchema( String name )
    {
        com.novell.ldap.LDAPDITContentRuleSchema rule = 
                                    schema.getDITContentRuleSchema(name);
        if( rule == null) {
            return null;
        }
        return new LDAPDITContentRuleSchema( rule);
    }

    /**
     * Returns a particular name form definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getNameFormSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getNameFormSchema(String)</a>
     */
    public LDAPNameFormSchema getNameFormSchema( String name )
    {
        com.novell.ldap.LDAPNameFormSchema form = 
                                    schema.getNameFormSchema(name);
        if( form == null) {
            return null;
        }
        return new LDAPNameFormSchema( form);
    }

    /**
     * Returns a particular syntax definition, or null if not found.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getSyntaxSchema(java.lang.String)">
            com.novell.ldap.LDAPSchema.getSyntaxSchema(String)</a>
     */
    public LDAPSyntaxSchema getSyntaxSchema( String oid )
    {
        com.novell.ldap.LDAPSyntaxSchema syntax = 
                                    schema.getSyntaxSchema(oid);
        if( syntax == null) {
            return null;
        }
        return new LDAPSyntaxSchema( syntax);
    }

    /**
     * Returns an enumeration of attribute definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getAttributeSchemas()">
            com.novell.ldap.LDAPSchema.getAttributeSchemas()</a>
     */
    public Enumeration getAttributeSchemas()
    {
        class AttrEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            AttrEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
                return new LDAPAttributeSchema(
                    (com.novell.ldap.LDAPAttributeSchema)enumer.nextElement());
            }
        }
        return new AttrEnumWrapper( schema.getAttributeSchemas());
    }

    /**
     * Returns an enumeration of object class definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getObjectClassSchemas()">
            com.novell.ldap.LDAPSchema.getObjectClassSchemas()</a>
     */
    public Enumeration getObjectClassSchemas()
    {
        class ObjEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            ObjEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
                return new LDAPObjectClassSchema(
                    (com.novell.ldap.LDAPObjectClassSchema)enumer.nextElement());
            }
        }
        return new ObjEnumWrapper( schema.getObjectClassSchemas());
    }

    /**
     * Returns an enumeration of matching rule definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getMatchingRuleSchemas()">
            com.novell.ldap.LDAPSchema.getMatchingRuleSchemas()</a>
     */
    public Enumeration getMatchingRuleSchemas()
    {
        class MatchEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            MatchEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
                return new LDAPMatchingRuleSchema(
                    (com.novell.ldap.LDAPMatchingRuleSchema)enumer.nextElement());
            }
        }
        return new MatchEnumWrapper( schema.getMatchingRuleSchemas());
    }

    /**
     * Returns an enumeration of matching rule use definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getMatchingRuleUseSchemas()">
            com.novell.ldap.LDAPSchema.getMatchingRuleUseSchemas()</a>
     */
    public Enumeration getMatchingRuleUseSchemas()
    {
        class UseEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            UseEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
                return new LDAPMatchingRuleUseSchema(
                 (com.novell.ldap.LDAPMatchingRuleUseSchema)enumer.nextElement());
            }
        }
        return new UseEnumWrapper( schema.getMatchingRuleUseSchemas());
    }

    /**
     * Returns an enumeration of DIT structure rule definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getDITStructureRuleSchemas()">
            com.novell.ldap.LDAPSchema.getDITStructureRuleSchemas()</a>
     */
    public Enumeration getDITStructureRuleSchemas()
    {
        class StructEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            StructEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
              return new LDAPDITStructureRuleSchema(
                (com.novell.ldap.LDAPDITStructureRuleSchema)enumer.nextElement());
            }
        }
        return new StructEnumWrapper( schema.getDITStructureRuleSchemas());
    }

    /**
     * Returns an enumeration of DIT content rule definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getDITContentRuleSchemas()">
            com.novell.ldap.LDAPSchema.getDITContentRuleSchemas()</a>
     */
    public Enumeration getDITContentRuleSchemas()
    {
        class ContentEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            ContentEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
                return new LDAPDITContentRuleSchema(
                  (com.novell.ldap.LDAPDITContentRuleSchema)enumer.nextElement());
            }
        }
        return new ContentEnumWrapper( schema.getDITContentRuleSchemas());
    }

    /**
     * Returns an enumeration of name form definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getNameFormSchemas()">
            com.novell.ldap.LDAPSchema.getNameFormSchemas()</a>
     */
    public Enumeration getNameFormSchemas()
    {
        class NameEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            NameEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
                return new LDAPNameFormSchema(
                        (com.novell.ldap.LDAPNameFormSchema)enumer.nextElement());
            }
        }
        return new NameEnumWrapper( schema.getNameFormSchemas());
    }

    /**
     * Returns an enumeration of syntax definitions.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getSyntaxSchemas()">
            com.novell.ldap.LDAPSchema.getSyntaxSchemas()</a>
     */
    public Enumeration getSyntaxSchemas()
    {
        class SyntaxEnumWrapper implements Enumeration
        {
            private Enumeration enumer;
            SyntaxEnumWrapper( Enumeration enumer)
            {
                this.enumer = enumer;
                return;
            }
            public boolean hasMoreElements()
            {
                return enumer.hasMoreElements();
            }
            public Object nextElement() throws NoSuchElementException
            {
                return new LDAPSyntaxSchema(
                        (com.novell.ldap.LDAPSyntaxSchema)enumer.nextElement());
            }
        }
        return new SyntaxEnumWrapper( schema.getSyntaxSchemas());
    }

    /**
     * Returns an enumeration of attribute names.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getAttributeNames()">
            com.novell.ldap.LDAPSchema.getAttributeNames()</a>
     */
    public Enumeration getAttributeNames()
    {
        return schema.getAttributeNames();
    }

    /**
     * Returns an enumeration of object class names.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getObjectClassNames()">
            com.novell.ldap.LDAPSchema.getObjectClassNames()</a>
     */
    public Enumeration getObjectClassNames()
    {
        return schema.getObjectClassNames();
    }

    /**
     * Returns an enumeration of matching rule names.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getMatchingRuleNames()">
            com.novell.ldap.LDAPSchema.getMatchingRuleNames()</a>
     */
    public Enumeration getMatchingRuleNames()
    {
        return schema.getMatchingRuleNames();
    }

    /**
   	 * Returns an enumeration of matching rule use names.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getMatchingRuleUseNames()">
            com.novell.ldap.LDAPSchema.getMatchingRuleUseNames()</a>
     */
    public Enumeration getMatchingRuleUseNames()
    {
        return schema.getMatchingRuleUseNames();
    }

    /**
   	 * Returns an enumeration of DIT structure rule names.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getDITStructureRuleNames()">
            com.novell.ldap.LDAPSchema.getDITStructureRuleNames()</a>
     */
    public Enumeration getDITStructureRuleNames()
    {
        return schema.getDITStructureRuleNames();
    }

   /**
   	 * Returns an enumeration of DIT content rule names.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getDITContentRuleNames()">
            com.novell.ldap.LDAPSchema.getDITContentRuleNames()</a>
     */
    public Enumeration getDITContentRuleNames()
    {
        return schema.getDITContentRuleNames();
    }

   /**
   	 * Returns an enumeration of name form names.
     *
     * @see <a href="../../../../api/com/novell/ldap/LDAPSchema.html#getNameFormNames()">
            com.novell.ldap.LDAPSchema.getNameFormNames()</a>
     */
    public Enumeration getNameFormNames()
    {
        return schema.getNameFormNames();
    }
}
