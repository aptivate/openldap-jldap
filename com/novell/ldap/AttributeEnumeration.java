/* **************************************************************************
* $Novell$
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

import java.util.*;
import com.novell.ldap.client.Debug;

public class AttributeEnumeration implements Enumeration
{
    private int         index = 0;
    private String[] attrs;

    public AttributeEnumeration( String[] attrs )
    {
        this.attrs = attrs;
    }
    public boolean hasMoreElements()
    {
        return (index < attrs.length);
    }
    public Object nextElement()
    {
        if( this.hasMoreElements()) {
            return attrs[index++];
        } else {
            throw new NoSuchElementException("No more URL attributes");
        }
    }
}
