/* **************************************************************************
 * $Id: SchemaParser.java,v 1.2 2000/10/02 19:57:23 bgudmundson Exp $
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
                //st2.quoteChar(''');
		StringTokenizer strTokens = new StringTokenizer( rawString, " '()\t\r\n", true );
		// Parse the string
		String currToken;
                String currToken2;
            // First parse out the OID
		if(strTokens.hasMoreTokens()){
			currToken = strTokens.nextToken();
  			if(currToken.equals("(")){
                          getTokenPastSpaces(strTokens);
			}
            }
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
            /*
            // Get the rest of the schema elements
		while(strTokens.hasMoreTokens()){
		  currToken = getTokenPastSpaces(strTokens);
    		  if(currToken.equals(")")){
        		break;
          	  }
		  if(currToken.equals("NAME")){
                    parseName( strTokens );
                    continue;
		  }
                  if(currToken.equals("DESC")){
                    parseDescription( strTokens );
                    continue;
		  }
                  if(currToken.equals("SYNTAX")){
                    getTokenPastSpaces( strTokens );
                    continue;
                  }
                  if(currToken.equals("SUP")){
                    getTokenPastSpaces( strTokens );
                    continue;
                  }
                  if(currToken.equals("SINGLE-VALUE")){
                    continue;
                  }
                  if(currToken.equals("OBSOLETE")){
                    continue;
                  }
                  if(currToken.equals("ABSTRACT")){
                    continue;
                  }
                  if(currToken.equals("STRUCTURAL")){
                    continue;
                  }
                  if(currToken.equals("AUXILIARY")){
                    continue;
                  }

                  // All remaining elements are stored as qualifiers
                  AttributeQualifier q = parseQualifier( strTokens, currToken );
                   if( q != null)
                     qualifiers.addElement(q);
                   continue;
		}
  	*/
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
        private String getTokenPastSpaces( StringTokenizer st ){
          // Move past spaces
          String currToken = null;
          while(st.hasMoreTokens()){
            currToken = st.nextToken();
            if(currToken.equals(" ")){
              continue;
            }
	    break;
          }
          return currToken;
        }
        private String parseName( StringTokenizer st ){
          String currToken;
          StringBuffer buf = new StringBuffer();
          boolean inSingleQuote = false;
          boolean inMultiName = false;
          Vector names = new Vector();

          aliases = null;
          currToken = getTokenPastSpaces(st);
          if( currToken.equals("(")){
           inMultiName = true;
           currToken = getTokenPastSpaces(st);
          }
          if( currToken.equals("'")){
            inSingleQuote = true;
          }
	  while((inMultiName == true || inSingleQuote == true) && st.hasMoreTokens()){
        	currToken = st.nextToken();
	        if(currToken.equals("'") ){
		    if(inSingleQuote == true){
                      names.addElement(buf.toString());
                      buf = new StringBuffer();
                      inSingleQuote = false;
		      continue;
                    }
                    else{
		      inSingleQuote = true;
		      continue;
		    }
                }
                if(currToken.equals(" ")){
		  if(inSingleQuote){
		    buf.append(" ");
	          }
		  continue;
		}
		if(currToken.equals(")"))
                  break;

		buf.append(currToken);
	    }
            int size;
            String retString = null;
            if( (size = names.size()) > 0){
	      retString = (String) names.firstElement();
              if( size > 1 ){
                names.removeElementAt(0);
                size -= 1;
                aliases = new String[size];
                names.copyInto(aliases);
              }
            }
           return retString;
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

        private AttributeQualifier parseQualifier( StringTokenizer st, String name ){
          AttributeQualifier qualifier = new AttributeQualifier(name);
          String currToken;
          StringBuffer buf = new StringBuffer();
          boolean inSingleQuote = false;
          boolean inMultiValue = false;

          currToken = getTokenPastSpaces(st);
          if( currToken.equals("(")){
           inMultiValue = true;
           currToken = getTokenPastSpaces(st);
          }
          if( currToken.equals("'")){
            inSingleQuote = true;
          }
	  while((inMultiValue == true || inSingleQuote == true) && st.hasMoreTokens()){
        	currToken = st.nextToken();
	        if(currToken.equals("'") ){
		    if(inSingleQuote == true){
                      qualifier.addValue(buf.toString());
                      buf = new StringBuffer();
                      inSingleQuote = false;
		      continue;
                    }
                    else{
		      inSingleQuote = true;
		      continue;
		    }
                }
                if(currToken.equals(" ")){
		  if(inSingleQuote){
		    buf.append(" ");
	          }
		  continue;
		}
		if(currToken.equals(")"))
                  break;

		buf.append(currToken);
	    }
            return qualifier;
        }

        private String parseDescription( StringTokenizer st ){
          String currToken;
          StringBuffer buf = new StringBuffer();
          boolean inSingleQuote = false;
	    while(st.hasMoreTokens()){
        	currToken = st.nextToken();
	        if(currToken.equals("'") ){
		    if(inSingleQuote == true){
		      break;
                    }
                    else{
		      inSingleQuote = true;
		      continue;
		    }
                }
                if(currToken.equals(" ")){
		  if(inSingleQuote){
		    buf.append(" ");
	          }
		  continue;
	        }
              buf.append(currToken);
            }
            inSingleQuote = false;
	    if(buf.length() > 0){
	      return buf.toString();
            }
          return null;
        }
}
