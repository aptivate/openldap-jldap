/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2003 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.events;

import com.novell.ldap.controls.LDAPPersistSearchControl;

/**
 * This interface contains the constants for Classification of Events.
 */
public interface EventConstant {
    /**
     * This classification is for events where event classificaton
     * is unknown.
     */
    public static final int CLASSIFICATION_UNKNOWN = -1;

    /**
     *This type definition is for event type where event type is
     *not known.
     */
    public static final int TYPE_UNKNOWN = -1;

    /**
     * The Standard Events System as defined by LDAP Persistence Search Draft
     * "Persistent Search: A Simple LDAP Change Notification Mechanism".
     */
    public static final int CLASSIFICATION_LDAP_PSEARCH = 0;

    /**
     * The Edirectory specific extensions for event processing.
     */
    public static final int CLASSIFICATION_EDIR_EVENT = 1;

    /* LDAP Persistence Search Events Type*/

    /**
    *  Event type specifying that you want to track additions of new entries
    *  to the directory.
    */
    public static final int LDAP_PSEARCH_ADD =
        LDAPPersistSearchControl.ADD;

    /**
    *  Event type specifying that you want to track removals of entries from
    *  the directory.
    */
    public static final int LDAP_PSEARCH_DELETE =
        LDAPPersistSearchControl.DELETE;

    /**
    *  Event type specifying that you want to track modifications of entries
    *  in the directory.
    */
    public static final int LDAP_PSEARCH_MODIFY =
        LDAPPersistSearchControl.MODIFY;

    /**
    *  Change type specifying that you want to track modifications of the DNs
    *  of entries in the directory.
    */
    public static final int LDAP_PSEARCH_MODDN =
        LDAPPersistSearchControl.MODDN;

    /**
    *  Event type specifying that you want to track any of the
    *  modifications (add,delete,modify, modifydn).
    */
    public static final int LDAP_PSEARCH_ANY =
        LDAPPersistSearchControl.ANY;
}
