
package com.novell.ldap.rfc2251;

import com.novell.ldap.client.ArrayList;
import com.novell.ldap.LDAPException;

/**
 * This interface represents Protocol Operations that are requests from a
 * client.
 */
public interface RfcRequest {
    /**
     * Builds a new request using the data from the this object.
     */
    public RfcRequest dupRequest(String base, String filter, Integer scope)
        throws LDAPException;
}
