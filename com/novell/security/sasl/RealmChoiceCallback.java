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

/**
 * This callback is used by SaslClient and SaslServer to obtain one or
 * more realms given a list of realm choices.
 */

public class RealmChoiceCallback
                 extends javax.security.auth.callback.ChoiceCallback
{

    /** 
     * Constructs a RealmChoiceCallback with a prompt, a list of choices and
     * a default choice.
     * @param prompt        The non-null prompt to use to request the realm.
     * @param choices       The non-null list of realms to choose from.
     * @param defaultChoice The choice to be used as the default choice when
     *  the list of choices is displayed. It is an index into the choices arary.
     * @param multipleSel   Specifies whether or not multiple selections can be
     *  made from the list of choices.
     */

    public RealmChoiceCallback (String prompt,
                               String[]choices,
                               int defaultChoice,
                               boolean multipleSel) {
        super(prompt,choices,defaultChoice,multipleSel);
    }

}

