/* **************************************************************************
 * $Id: SchemaParser.java,v 1.3 2000/10/10 22:26:24 bgudmundson Exp $
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

package com.novell.ldap.client;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.IOException;
import com.novell.ldap.LDAPObjectClassSchema;
import com.novell.ldap.LDAPAttributeSchema;

public class SchemaParser{

	String rawString;
	String name;
        String aliases[] = null;
	String id;
	String description;
        String syntax;
        String superior;
        String superiors[];
        String required[];
        String optional[];
        boolean single = false;
        boolean obsolete = false;
        String equality;
        String ordering;
        String substring;
        boolean collective = false;
        boolean userMod = true;
        int usage = LDAPAttributeSchema.USER_APPLICATIONS;
        int type = -1;
        Vector qualifiers = new Vector();

	public SchemaParser( String rawString ) throws IOException {
		this.rawString = rawString;
  		StreamTokenizer st2 = new StreamTokenizer(new StringReader(rawString));
                st2.ordinaryChar('.');
                st2.ordinaryChars('0', '9');
                st2.ordinaryChar('{');
                st2.ordinaryChar('}');
                st2.ordinaryChar('_');
                st2.ordinaryChar(';');
                st2.wordChars('.', '9');
                st2.wordChars('{', '}');
                st2.wordChars('_', '_');
                st2.wordChars(';', ';');
            //First parse out the OID
            try{
              String currName;
              if( StreamTokenizer.TT_EOF !=  st2.nextToken()){
                  if(st2.ttype == '('){
                  if(StreamTokenizer.TT_WORD ==  st2.nextToken()){
                    id = st2.sval;
                  }
                  while(StreamTokenizer.TT_EOF !=  st2.nextToken()){
                    if(st2.ttype == StreamTokenizer.TT_WORD ){
                      if(st2.sval.equals("NAME")){
                        if(st2.nextToken() == '\'' ){
                          name = st2.sval;
                        }
                        else if(st2.ttype == '(' ){
                          if(st2.nextToken() == '\'' ){
                           name = st2.sval;
                          }
                          Vector names = new Vector();
                          while( st2.nextToken() == '\''){
                            names.addElement(st2.sval);
                          }
                          if(names.size() > 0){
                            aliases = new String[names.size()];
                            names.copyInto(aliases);
                          }
                        }
                        continue;
                      }
                      if(st2.sval.equals("DESC")){
                        if( st2.nextToken() == '\'' ){
                          description = st2.sval;
                        }
                        continue;
                      }
                      if(st2.sval.equals("SYNTAX")){
                        if( st2.nextToken() == StreamTokenizer.TT_WORD ){
                          syntax = st2.sval;
                        }
                        continue;
                      }
                      if(st2.sval.equals("EQUALITY")){
                        if( st2.nextToken() == StreamTokenizer.TT_WORD ){
                          equality = st2.sval;
                        }
                        continue;
                      }
                      if(st2.sval.equals("ORDERING")){
                        if( st2.nextToken() == StreamTokenizer.TT_WORD ){
                          ordering = st2.sval;
                        }
                        continue;
                      }
                      if(st2.sval.equals("SUBSTR")){
                        if( st2.nextToken() == StreamTokenizer.TT_WORD ){
                          substring = st2.sval;
                        }
                        continue;
                      }
                      if(st2.sval.equals("SUP")){
                        Vector values = new Vector();
                        st2.nextToken();
                        if(st2.ttype == '(' ){
                          st2.nextToken();
                          while(st2.ttype != ')' ){
                            if(st2.ttype != '$'){
                            	values.addElement(st2.sval);
                             }
                             st2.nextToken();
                          }
                        }
                      	else{
                      		values.addElement(st2.sval);
                        	superior = st2.sval;
                        }
                        if(values.size() > 0){
                            superiors = new String[values.size()];
                            values.copyInto(superiors);
                          }
                        continue;
                      }
                      if(st2.sval.equals("SINGLE-VALUE")){
                        single = true;
                        continue;
                      }
                      if(st2.sval.equals("OBSOLETE")){
                        obsolete = true;
                        continue;
                      }
                      if(st2.sval.equals("COLLECTIVE")){
                        collective = true;
                        continue;
                      }
                      if(st2.sval.equals("NO-USER-MODIFICATION")){
                        userMod = false;
                        continue;
                      }
                      if(st2.sval.equals("MUST")){
                        Vector values = new Vector();
                        st2.nextToken();
                        if(st2.ttype == '(' ){
                          st2.nextToken();
                          while(st2.ttype != ')' ){
                            if(st2.ttype != '$'){
                            	values.addElement(st2.sval);
                             }
                             st2.nextToken();
                          }
                        }
                      	else
                      		values.addElement(st2.sval);
                        if(values.size() > 0){
                            required = new String[values.size()];
                            values.copyInto(required);
                          }
                        continue;
                      }
                      if(st2.sval.equals("MAY")){
                        Vector values = new Vector();
                        st2.nextToken();
                        if(st2.ttype == '(' ){
                          st2.nextToken();
                          for(int i = 0; st2.ttype != ')'; i++ ){
                            if(st2.ttype != '$'){
                            	values.addElement(st2.sval);
                             }
                             st2.nextToken();
                          }
                        }
                      	else
                      		values.addElement(st2.sval);
                        if(values.size() > 0){
                            optional = new String[values.size()];
                            values.copyInto(optional);
                          }
                        continue;
                      }
                      if(st2.sval.equals("ABSTRACT")){
                        type = LDAPObjectClassSchema.ABSTRACT;
                        continue;
                      }
                      if(st2.sval.equals("STRUCTURAL")){
                        type = LDAPObjectClassSchema.STRUCTURAL;
                        continue;
                      }
                      if(st2.sval.equals("AUXILIARY")){
                        type = LDAPObjectClassSchema.AUXILIARY;
                        continue;
                      }
                      if(st2.sval.equals("USAGE")){
                        if( st2.nextToken() == StreamTokenizer.TT_WORD ){
                          currName = st2.sval;
                          if( currName.equals("directoryOperation")){
                              usage = LDAPAttributeSchema.DIRECTORY_OPERATION;
                          }
                          else if( currName.equals("distributedOperation")){
                              usage = LDAPAttributeSchema.DISTRIBUTED_OPERATION;
                          }
                          else if( currName.equals("dSAOperation")){
                              usage = LDAPAttributeSchema.DSA_OPERATION;
                          }
                          else if( currName.equals("userApplications")){
                              usage = LDAPAttributeSchema.USER_APPLICATIONS;
                          }
                        }
                        continue;
                      }
                      currName = st2.sval;
                      AttributeQualifier q = parseQualifier( st2, currName );
		      if( q != null)
                     	qualifiers.addElement(q);
                      continue;
                    }
                  }
		        }
              }
            }
            catch(IOException e){
              throw e;
            }
	}

	public void setRawString( String rawString ) {
		this.rawString = rawString;
	}

	public String getRawString(){
		return rawString;
	}

	public String getName(){
		return name;
	}

        public Enumeration getQualifiers(){
          return qualifiers.elements();
        }

        public String[] getAliases() {
          String[] retVal = null;
          if( aliases != null ){
            retVal = new String[aliases.length];
            for(int i = 0; i < aliases.length; i++ ){
              retVal[i] = aliases[i];
            }
          }
          return retVal;
        }
        public String getID() {
		return id;
	}
    public String getDescription() {
		return description;
	}
    public String getSyntax() {
		return syntax;
	}
    public String getSuperior() {
		return superior;
	}
    public boolean getSingle() {
		return single;
	}
    public boolean getObsolete() {
		return obsolete;
	}
    public String getEquality() {
        return equality;
    }
    public String getOrdering() {
        return ordering;
    }
    public String getSubstring() {
        return substring;
    }
    public boolean getCollective(){
        return collective;
    }
    public boolean getUserMod() {
        return userMod;
    }
    public int getUsage() {
        return usage;
    }
 	public int getType() {
		return type;
	}
 	public String[] getSuperiors() {
		return superiors;
	}
 	public String[] getRequired() {
		return required;
	}
 	public String[] getOptional() {
		return optional;
	}

    private AttributeQualifier parseQualifier( StreamTokenizer st, String name ) throws IOException {
        AttributeQualifier qualifier = new AttributeQualifier(name);

        try{
            if(st.nextToken() == '\'' ){
          		qualifier.addValue(st.sval);
           	}
           	else if(st.ttype == '(' ){
           		while(st.nextToken() == '\'' ){
           			qualifier.addValue(st.sval);
             		}
           	}
        }
        catch(IOException e){
            throw e;
        }
        return qualifier;
    }

}
