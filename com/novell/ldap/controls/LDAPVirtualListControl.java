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

package com.novell.ldap.controls;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.client.Debug;

 /* The following is the ASN.1 of the VLV Request packet:<br>
 *
 * VirtualListViewRequest ::= SEQUENCE {
 *      beforeCount    INTEGER (0..maxInt),
 *         afterCount     INTEGER (0..maxInt),
 *      CHOICE {
 *          byoffset [0] SEQUENCE {
 *              offset          INTEGER (0 .. maxInt),
 *              contentCount    INTEGER (0 .. maxInt) },
 *          greaterThanOrEqual [1] AssertionValue },
 *      contextID     OCTET STRING OPTIONAL }
 *
 */
 
/**
 * LDAPVirtualListControl is a Server Control used to specify
 * that results from a search are to be returned in pages - which are
 * subsets of the entire virtual result set.
 *
 * <p>On success, an updated LDAPVirtualListResponse object is
 * returned as a response Control, containing information on the virtual
 * list size and the actual first index. This object can then be used
 * by the client with a new requested position or length and sent to the
 * server to obtain a different segment of the virtual list.</p>
 *
 * <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/controls/VLVControl.java.html">VLVControl.java</p>
 */
public class LDAPVirtualListControl extends LDAPControl {

    /* The ASN.1 for the VLV Request has CHOICE field. These private
     * variables represent differnt ids for these different options
     */
    private static int BYOFFSET = 0;
    private static int GREATERTHANOREQUAL = 1;


    /**
     * The Request OID for a VLV Request
     */
    private static String requestOID = "2.16.840.1.113730.3.4.9";

    /*
     * The Response stOID for a VLV Response
     */
    private static String responseOID = "2.16.840.1.113730.3.4.10";

    /*
     * The encoded ASN.1 VLV Control is stored in this variable
     */
    private ASN1Sequence m_vlvRequest;


    /* Private instance variables go here.
     * These variables are used to store copies of various fields
     * that can be set in a VLV control. One could have managed
     * without really defining these private variables by reverse
     * engineering each field from the ASN.1 encoded control.
     * However that would have complicated and slowed down the code.
     */
    private int m_beforeCount;
    private int m_afterCount;
    private String m_jumpTo;
    private String m_context = null;
    private int m_startIndex = 0;
    private    int m_contentCount = -1;

    /*
     * This is where we register the control responses
     */
    static
    {
        /* Register the VLV Sort Control class which is returned by the server
         * in response to a VLV Sort Request
         */
        try {
            LDAPControl.register(responseOID,
             Class.forName("com.novell.ldap.controls.LDAPVirtualListResponse"));
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                   "Registered VLV Control Response Class");
            }
        } catch (ClassNotFoundException e) {
            if( Debug.LDAP_DEBUG) {
                Debug.trace( Debug.controls,
                   "Could not register VLV Control Response - Class not found");
            }
        }
    }

   /**
    * Constructs a virtual list control using the specified filter
    * expression.
    *
    * <p>The expression specifies the first entry to be used for the
    * virtual search results. The other two paramers are the number of
    * entries before and after a located index to be returned.</p>
    *
    * @param jumpTo            A search expression that defines the first
    * element to be returned in the virtual search results. The filter
    * expression in the search operation itself may be, for example,
    * "objectclass=person" and the jumpTo expression in the virtual
    * list control may be "cn=m*", to retrieve a subset of entries
    * starting at or centered around those with a common name beginning
    * with the letter "M". <br><br>
    *
    * @param beforeCount    The number of entries before startIndex (the
    * reference entry) to be returned. <br><br>
    *
    * @param afterCount        The number of entries after startIndex to be
    * returned. <br><br>
    */
    public LDAPVirtualListControl(    String    jumpTo,
                                    int        beforeCount,
                                    int        afterCount )
    {
        /* Set the OPTIONAL context field to null
         */
        this(jumpTo, beforeCount, afterCount, null);
        return;
    }



   /**
    * Constructs a virtual list control using the specified filter
    * expression along with an optional server context.
    *
    * <p>The expression specifies the first entry to be used for the
    * virtual search results. The other two paramers are the number of
    * entries before and after a located index to be returned.</p>
    *
    * @param jumpTo    A search expression that defines the first
    * element to be returned in the virtual search results. The filter
    * expression in the search operation itself may be, for example,
    * "objectclass=person" and the jumpTo expression in the virtual
    * list control may be "cn=m*", to retrieve a subset of entries
    * starting at or centered around those with a common name beginning
    * with the letter "M".<br><br>
    *
    * @param beforeCount The number of entries before startIndex (the
    * reference entry) to be returned. <br><br>
    *
    * @param afterCount The number of entries after startIndex to be
    * returned. <br><br>
    *
    * @param context Used by some implementations to process requests
    * more efficiently. The context should be null on the first search,
    * and thereafter it should be whatever was returned by the server in the
    * virtual list response control.
    */
    public LDAPVirtualListControl(    String    jumpTo,
                                    int        beforeCount,
                                    int        afterCount,
                                    String    context )
    {

        /* Draft requires this to be a critical control. Hence the
         * true value for the criticality when calling parent
         * constructor
         */
        super(requestOID, true, null);

        /* Save off the fields in local variables
         */
        m_beforeCount = beforeCount;
        m_afterCount = afterCount;
        m_jumpTo = jumpTo;
        m_context = context;

        /* Call private method to build the ASN.1 encoded request packet.
         */
        BuildTypedVLVRequest();

        /* Set the request data field in the in the parent LDAPControl to
         * the ASN.1 encoded value of this control.  This encoding will be
         * appended to the search request when the control is sent.
         */
        setValue (m_vlvRequest.getEncoding(new LBEREncoder()));
        return;
    }

    /** Private method used to construct the ber encoded control
     *  Used only when using the typed mode of VLV Control.
     */
    private void BuildTypedVLVRequest()
    {
        /* Create a new ASN1Sequence object */
        m_vlvRequest = new ASN1Sequence(4);

        /* Add the beforeCount and afterCount fields to the sequence */
        m_vlvRequest.add(new ASN1Integer(m_beforeCount));
        m_vlvRequest.add(new ASN1Integer(m_afterCount));

        /* The next field is dependent on the type of indexing being used.
         * A "typed" VLV request uses a ASN.1 OCTET STRING to index to the
         * correct object in the list.  Encode the ASN.1 CHOICE corresponding
         * to this option (as indicated by the greaterthanOrEqual field)
         * in the ASN.1.
         */
        m_vlvRequest.add(new ASN1Tagged(new ASN1Identifier( ASN1Identifier.CONTEXT,
                                                            false,
                                                            GREATERTHANOREQUAL),
                                        new ASN1OctetString(m_jumpTo),
                                        false));

        /* Add the optional context string if one is available.
         */
        if (m_context != null)
            m_vlvRequest.add(new ASN1OctetString(m_context));

        return;
    }

  /**
    * Use this constructor to fetch a subset when the size of the
    * virtual list is known,
    *
    * <br><br>
    * @param beforeCount The number of entries before startIndex (the
    * reference entry) to be returned. <br><br>
    *
    * @param afterCount    The number of entries after startIndex to be
    * returned.<br><br>
    *
    * @param startIndex The index of the reference entry to be returned.<br><br>
    *
    * @param contentCount The total number of entries assumed to be in the
    * list. This is a number returned on a previous search, in the
    * LDAPVirtualListResponse. The server may use this number to adjust
    * the returned subset offset.
    */
    public LDAPVirtualListControl(  int        startIndex,
                                    int        beforeCount,
                                    int        afterCount,
                                    int        contentCount )
    {
        /* Set the OPTIONAL context field to null
         */
        this(startIndex, beforeCount, afterCount, contentCount, null);
        return;
    }



  /**
    * Use this constructor to fetch a subset when the size of the
    * virtual list is known,
    *   
    * <br><br>
    * @param beforeCount    The number of entries before startIndex (the
    * reference entry) to be returned.<br><br>
    *
    * @param afterCount        The number of entries after startIndex to be
    * returned.<br><br>
    *
    * @param startIndex     The index of the reference entry to be
    * returned.<br><br>
    *
    * @param contentCount    The total number of entries assumed to be in the
    * list. This is a number returned on a previous search, in the
    * LDAPVirtualListResponse. The server may use this number to adjust
    * the returned subset offset.<br><br>
    *
    * @param context        Used by some implementations to process requests
    * more efficiently. The context should be null on the first search,
    * and thereafter it should be whatever was returned by the server in the
    * virtual list response control.
    */
    public LDAPVirtualListControl(    int        startIndex,
                                    int        beforeCount,
                                    int        afterCount,
                                    int        contentCount,
                                    String    context )
    {
        /* Draft requires this to be a critical control. Hence the
         * true value for the criticality when calling parent
         * constructor
         */
        super(requestOID, true, null);


        /* Save off the fields in local variables
         */
        m_beforeCount = beforeCount;
        m_afterCount = afterCount;
        m_startIndex = startIndex;
        m_contentCount = contentCount;
        m_context = context;

        /* Call private method to build the ASN.1 encoded request packet.
         */
        BuildIndexedVLVRequest();

        /* Set the request data field in the in the parent LDAPControl to
         * the ASN.1 encoded value of this control.  This encoding will be
         * appended to the search request when the control is sent.
         */
        setValue (m_vlvRequest.getEncoding(new LBEREncoder()));
        return;
    }

    /** Private method used to construct the ber encoded control
     *  Used only when using the Indexed mode of VLV Control
     */
    private void BuildIndexedVLVRequest()
    {
        /* Create a new ASN1Sequence object */
        m_vlvRequest = new ASN1Sequence(4);

        /* Add the beforeCount and afterCount fields to the sequence */
        m_vlvRequest.add(new ASN1Integer(m_beforeCount));
        m_vlvRequest.add(new ASN1Integer(m_afterCount));

        /* The next field is dependent on the type of indexing being used.
         * An "indexed" VLV request uses a ASN.1 SEQUENCE to index to the
         * correct object in the list.  Encode the ASN.1 CHOICE corresponding
         * to this option (as indicated by the byoffset fieldin the ASN.1.
         */
        ASN1Sequence byoffset = new ASN1Sequence(2);
        byoffset.add(new ASN1Integer(m_startIndex));
        byoffset.add(new ASN1Integer(m_contentCount));;

        /* Add the ASN.1 sequence to the encoded data
         */
        m_vlvRequest.add(new ASN1Tagged(    new ASN1Identifier(ASN1Identifier.CONTEXT, true,
                                            BYOFFSET),
                                        byoffset,
                                        false));

        /* Add the optional context string if one is available.
         */
        if (m_context != null)
            m_vlvRequest.add(new ASN1OctetString(m_context));

        return;
    }


  /**
    *    Returns the number of entries after the top/center one to return per
    *   page of results.
    */
    public int getAfterCount()
    {
        return m_afterCount;
    }



  /**
    *    Returns the number of entries before the top/center one to return per
    *   page of results.
    */
    public int getBeforeCount()
    {
        return m_beforeCount;
    }



  /**
    *    Returns the size of the virtual search results list. For a newly
    *    constructed control - one which is not the result of parseResponse on
    *    a control returned by a server - the method returns -1.
    */
   public int getListSize()
   {
        return m_contentCount;

   }



  /**
    *    Sets the assumed size of the virtual search results list. This will
    *    typically be a number returned on a previous virtual list request in
    *    an LDAPVirtualListResponse.
    */
    public void setListSize( int size )
    {
        m_contentCount = size;

        /* since we just changed a field we need to rebuild the ber
         * encoded control
         */
        BuildIndexedVLVRequest();

        /* Set the request data field in the in the parent LDAPControl to
         * the ASN.1 encoded value of this control.  This encoding will be
         * appended to the search request when the control is sent.
         */
        setValue (m_vlvRequest.getEncoding(new LBEREncoder()));

    }



  /**
    * Sets the center or starting list index to return, and the number of
    * results before and after.
    *
    * <br><br>
    * @param    listIndex        The center or starting list index to be
    * returned. <br><br>
    *
    * @param    beforeCount        The number of entries before "listIndex" to be
    * returned. <br><br>
    *
    * @param    afterCount        The number of entries after "listIndex" to be
    * returned. <br><br>
    */
    public void setRange( int listIndex,
                         int beforeCount,
                         int afterCount )
    {

        /* Save off the fields in local variables
         */
        m_beforeCount = beforeCount;
        m_afterCount = afterCount;
        m_startIndex = listIndex;

        /* since we just changed a field we need to rebuild the ber
         * encoded control
         */
        BuildIndexedVLVRequest();

        /* Set the request data field in the in the parent LDAPControl to
         * the ASN.1 encoded value of this control.  This encoding will be
         * appended to the search request when the control is sent.
         */
        setValue (m_vlvRequest.getEncoding(new LBEREncoder()));

    }

  // PROPOSED ADDITION TO NEXT VERSION OF DRAFT (v7)
  /**
   * Sets the center or starting list index to return, and the number of
   * results before and after.
   *
   * <br><br>
   * @param jumpTo A search expression that defines the first
   * element to be returned in the virtual search results. The filter
   * expression in the search operation itself may be, for example,
   * "objectclass=person" and the jumpTo expression in the virtual
   * list control may be "cn=m*", to retrieve a subset of entries
   * starting at or centered around those with a common name
   * beginning with the letter "M".<br><br>
   *
   * @param    beforeCount    The number of entries before "listIndex" to be
   * returned.<br><br>
   *
   * @param    afterCount The number of entries after "listIndex" to be
   * returned.<br><br>
   */

    public void setRange( String jumpTo,
                         int beforeCount,
                         int afterCount )
    {
        /* Save off the fields in local variables
         */
        m_beforeCount = beforeCount;
        m_afterCount = afterCount;
        m_jumpTo = jumpTo;

        /* since we just changed a field we need to rebuild the ber
         * encoded control
         */
        BuildTypedVLVRequest();

        /* Set the request data field in the in the parent LDAPControl to
         * the ASN.1 encoded value of this control.  This encoding will be
         * appended to the search request when the control is sent.
         */
        setValue (m_vlvRequest.getEncoding(new LBEREncoder()));
    }

  /**
    *   Returns the cookie used by some servers to optimize the processing of
    *    virtual list requests.
    */
    public String getContext()
    {
        return m_context;
    }




  /**
    *    Sets the cookie used by some servers to optimize the processing of
    *    virtual list requests. It should be the context field returned in a
    *    virtual list response control for the same search.
    */
   public void setContext( String context )
   {
        /* Index of the context field if one exists in the ber
         */
        int CONTEXTIDINDEX = 3;

        /* Save off the new value in private variable
         */
        m_context = context;

        /* Is there a context field that is already in the ber
         */
        if (m_vlvRequest.size() == 4) {
            /* If YES then replace it */
            m_vlvRequest.set(CONTEXTIDINDEX, new ASN1OctetString(m_context));
        }
        else if (m_vlvRequest.size() == 3) {
            /* If no then add a new one */
            m_vlvRequest.add(new ASN1OctetString(m_context));
        }

        /* Set the request data field in the in the parent LDAPControl to
         * the ASN.1 encoded value of this control.  This encoding will be
         * appended to the search request when the control is sent.
         */
        setValue (m_vlvRequest.getEncoding(new LBEREncoder()));

   }
}
