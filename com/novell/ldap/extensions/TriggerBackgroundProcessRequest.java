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
package com.novell.ldap.extensions;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.resources.*;

/**
 * This API is used to trigger the specified background process on the
 * Novell eDirectory server.
 *
 *  <p>The TriggerBackgroundProcessRequest uses tone of the following OID's
 *  depending on the process being triggered:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.43</p>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.47</p>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.49</p>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.51</p>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.53</p>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.55</p>
 *
 *  <p>The requestValue has the following format:<br>
 *
 *  requestValue ::=<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;   NULL
 </p>
 */

public class TriggerBackgroundProcessRequest extends LDAPExtendedOperation {

    /**
     * Constants used to refer to different Novell eDirectory
     * background processes
     */
    public static final int LDAP_BK_PROCESS_BKLINKER    = 1;
    public static final int LDAP_BK_PROCESS_JANITOR     = 2;
    public static final int LDAP_BK_PROCESS_LIMBER      = 3;
    public static final int LDAP_BK_PROCESS_SKULKER     = 4;
    public static final int LDAP_BK_PROCESS_SCHEMA_SYNC = 5;
    public static final int LDAP_BK_PROCESS_PART_PURGE  = 6;

/**
 *
 * Based on the process ID specified this constructer cosntructs an
 * LDAPExtendedOperation object with the apppropriate OID.
 *
 * @param processID   This id identifies the background process to be triggerd.
 *
 * @exception LDAPException A general exception which includes an error message
 *                          and an LDAP error code.
 */
    public TriggerBackgroundProcessRequest(int processID)
                throws LDAPException {

        super(null, null);

        switch (processID) {

        case LDAP_BK_PROCESS_BKLINKER:
            setID(ReplicationConstants.TRIGGER_BKLINKER_REQ);
            break;
        case LDAP_BK_PROCESS_JANITOR:
            setID(ReplicationConstants.TRIGGER_JANITOR_REQ);
            break;
        case LDAP_BK_PROCESS_LIMBER:
            setID(ReplicationConstants.TRIGGER_LIMBER_REQ);
            break;
        case LDAP_BK_PROCESS_SKULKER:
            setID(ReplicationConstants.TRIGGER_SKULKER_REQ);
            break;
        case LDAP_BK_PROCESS_SCHEMA_SYNC:
            setID(ReplicationConstants.TRIGGER_SCHEMA_SYNC_REQ);
            break;
        case LDAP_BK_PROCESS_PART_PURGE:
            setID(ReplicationConstants.TRIGGER_PART_PURGE_REQ);
            break;
        default:
            throw new IllegalArgumentException(
                                     ExceptionMessages.PARAM_ERROR);

        }
    }

}
