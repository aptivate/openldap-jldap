/* **************************************************************************
 * $Id: AttributeQualifier.java,v 1.2 2001/03/01 00:30:04 cmorris Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap.client;

import com.novell.ldap.client.ArrayList;

public class AttributeQualifier{
   String name;
   ArrayList values;

   public AttributeQualifier( String name ){
     this.name = name;
     values = new ArrayList(5);
     return;
   }
   public void addValue( String value ){
     values.add( value );
     return;
   }
   public String getName(){
     return name;
   }
   public String[] getValues(){
     String[] strValues = null;
     if( values.size() > 0 ){
      strValues = new String[values.size()];
      for(int i = 0; i < values.size(); i++ ){
        strValues[i] = (String) values.get(i);
       }
    }
    return strValues;
   }
 }

