/* ************************************************************************
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

/**
 * This callback is used by SaslClient and SaslServer to retrieve realm
 * information.
 */
public class RealmCallback
             extends javax.security.auth.callback.TextInputCallback
{

    /**
     * Constructs a RealmCallback with a prompt.
     * @param prompt        The non-null prompt to use to request the 
     * realm information
     * @exception IllegalArgumentException  If prompt is Null or Empty
     */
    public RealmCallback (String prompt) 
        throws IllegalArgumentException
    {
         super(prompt);
    }
    /**
     * Constructs a RealmCallback with a prompt and a default realm.
     * @param prompt        The non-null prompt to use to request the realm
     * information
     * @param defaultRealm  The non-null default realm to use 
     * @exception IllegalArgumentException  If prompt is Null or Empty , or if
     *  defaultRealm is empty or null
     */
    public RealmCallback (String prompt, String defaultRealm)   
       throws IllegalArgumentException
    {
      super(prompt,defaultRealm);
    }
}
