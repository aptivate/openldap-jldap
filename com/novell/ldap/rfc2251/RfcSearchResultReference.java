
package com.novell.ldap.protocol;

import java.io.*;
import java.util.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

/**
 *       SearchResultReference ::= [APPLICATION 19] SEQUENCE OF LDAPURL
 */
public class RfcSearchResultReference extends ASN1SequenceOf 
                                    implements Enumeration {

    //*************************************************************************
    // Constructors for SearchResultReference
    //*************************************************************************

    private Enumeration references = null;
    private String name = "SearchResultReference@" + Integer.toHexString(hashCode());
    /**
     * The only time a client will create a SearchResultReference is when it is
     * decoding it from an InputStream
     */
    public RfcSearchResultReference(ASN1Decoder dec, InputStream in, int len)
       throws IOException
    {
        super(dec, in, len);
        return;
    }

    //*************************************************************************
    // Accessors
    //*************************************************************************

    /**
     * Override getIdentifier to return an application-wide id.
     */
    public ASN1Identifier getIdentifier()
    {
        return new ASN1Identifier(ASN1Identifier.APPLICATION, true,
                                RfcProtocolOp.SEARCH_RESULT_REFERENCE);
    }

    public Enumeration elements()
    {
        references = super.elements();
        return this;
    }

    public boolean hasMoreElements()
    {
        if( references == null )
            return false;
        return references.hasMoreElements();
    }
    public Object nextElement()
    {
        if( references == null )
            throw new NoSuchElementException(name + "Enumeration not initialized");
        return  ((ASN1OctetString)(references.nextElement())).getString();
    }
}
