/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2003 Novell, Inc. All Rights Reserved.
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
package com.novell.security.sasl;

import java.security.*;
import java.util.*;

class SaslEnumFactory
    implements Enumeration
{

    static boolean flagj2 = false;
    private List saslFactory;
    private int facIndex;

    SaslEnumFactory(String className, Map props,String cpkgs)
    {
        saslFactory = new ArrayList();
        facIndex = 0;
        String pkglist;

        if( props == null )		{
            pkglist=null;
        }
        else    {
            pkglist=(String) props.get(cpkgs);
        }

        if ( pkglist !=null )    {
            AddClassFromPkgList(pkglist);
        }

        pkglist= System.getProperty(cpkgs);
        if( pkglist != null )    {
            AddClassFromPkgList(pkglist);
        }

        Provider p = Security.getProvider(className);
        if(p != null)
        {
            Object facNames[] = p.values().toArray();
            for(int i = 0; i < facNames.length; i++)
                 saslFactory.add(facNames[i]);
        }
    }
    
    
    private  void AddClassFromPkgList (String pkglist)
    {

        StringTokenizer st= new StringTokenizer( pkglist ,"|");

        while ( st.hasMoreTokens() )            {
            String pkgName = st.nextToken().trim();
            String className = pkgName + ".ClientFactory";
            saslFactory.add(className);
        }
    }

    public boolean hasMoreElements()
    {
        while(facIndex < saslFactory.size())    {
            Object obj;

            synchronized (saslFactory)    {
                 obj = saslFactory.get(facIndex);
            }

            if(obj instanceof String)             {
                 int dup = findDuplicate((String)obj, facIndex);
                 if(dup >= 0)            {
                     synchronized (saslFactory)    {
                     saslFactory.remove(facIndex);
                 }
                 continue;
                 }

                 try            {
                     obj = Class.forName((String)obj);
                 }
                 catch(Exception e)            {
                     synchronized (saslFactory)    {
                         saslFactory.remove(facIndex);
                     }
                     continue;
                 }
             }
             if(!(obj instanceof Class))    {
                 if(!(obj instanceof SaslClientFactory) ){
                     throw new IllegalArgumentException(" Found " + 
                                              obj.getClass().getName());
                 } 
                 else    {
                     boolean flag;
                     int dup = findDuplicate(obj.getClass().getName(),
                                                                 facIndex);
                     if(dup >= 0)                {
                         synchronized (saslFactory)    {
                             saslFactory.remove(facIndex);
                         }
                         continue;
                     }
                     flag = true;
                     return flag;
                 }
             }
             else                           {
                 int dup = findDuplicate(((Class)obj).getName(), facIndex);
                 if(dup >= 0)               {
                     synchronized (saslFactory)    {
                         saslFactory.remove(facIndex);
                 }
                 continue;
                 }
                 boolean flag1;
                 try                   {
                     obj = ((Class)obj).newInstance();
                 }catch (Exception e) {
                     e.printStackTrace();
                 }
                 synchronized (saslFactory)    {
                     saslFactory.set(facIndex, obj);
                 }  
                 flag1 = true;
                 return flag1;
             }
        }
        return false;
    }
   

    int findDuplicate(String name, int ind)
    {
        synchronized(saslFactory){
            for(int i = ind - 1; i >= 0; i--)        {
                 String knownName = saslFactory.get(i).getClass().getName();
                 if(name.equals(knownName))           {
                     return ind;
                 }
             }
        }
        return -1;
    }

    public Object nextElement()
    {
        Object obj = null;
        synchronized(saslFactory)        {
            hasMoreElements();
            obj = saslFactory.get(facIndex++);
        }
        return obj;
    }

    static String getSystemProperty(final String propName)
    {
        if(flagj2)
            return (String)AccessController.doPrivileged(
                                        new PrivilegedAction() {
                 public Object run()
                 {
                     try
                     {
                         return System.getProperty(propName);
                     }
                     catch(SecurityException e)
                     {
                         return null;
                     }
                 }

            });
        else
            return System.getProperty(propName);
    }

    static
    {
        try        {
            Class.forName("java.security.PrivilegedAction");
            flagj2 = true;
        }
        catch(Exception e)        {
            flagj2 = false;
        }
    }
}
