/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/controls/LDAPVirtualListControl.java,v 1.6 2001/03/01 00:30:07 cmorris Exp $
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

package com.novell.ldap.controls;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 * 3.1 public class LDAPVirtualListControl 
 *              extends LDAPControl 
 *  
 * LDAPVirtualListControl is a Server Control to specify that results 
 * from a search are to be returned in pages, subsets of the entire 
 * virtual result set. On success, an updated LDAPVirtualList object is 
 * returned as a response Control, containing information on the virtual 
 * list size and the actual first index. This object can then be updated 
 * by the client with a new requested position or length and sent to the 
 * server to obtain a different segment of the virtual list.
 *
 *
 *           VirtualListViewRequest ::= SEQUENCE {
 *                beforeCount    INTEGER (0..maxInt),
 *                afterCount     INTEGER (0..maxInt),
 *                CHOICE {
 *                        byoffset [0] SEQUENCE {
 *                         offset          INTEGER (0 .. maxInt),
 *                         contentCount    INTEGER (0 .. maxInt) },
 *                        greaterThanOrEqual [1] AssertionValue },
 *                contextID     OCTET STRING OPTIONAL }
 *
 */
public class LDAPVirtualListControl extends LDAPControl {


	public static final int BYOFFSET = 0;
	public static final int GREATERTHANOREQUAL = 1;
    public static final String OID = "2.16.840.1.113730.3.4.9";

	// Private copy of various fields that can be set on the 
	// VLV control
	private ASN1Sequence m_vlvRequest;
	private int m_beforeCount;
	private int m_afterCount;
	private String m_jumpTo;
	private String m_context = null;
	private int m_startIndex = 0;
	private	int m_contentCount = -1;


   /**
	* 3.1.1 Constructors 
	* Constructs a virtual list control using the specified filter 
	* expression for the first entry, which defines the extent of the 
	* virtual search results, and the number of entries before and after a 
	* located index to be returned. 
	*
	*  @param jumpTo		A search expression that defines the first 
	*						element to be returned in the virtual search 
    *						results. The filter expression in the search 
    *						operation itself may be, for example, 
    *						"objectclass=person" and the jumpTo expression in 
    *						the virtual list control may be "cn=m*", to 
    *						retrieve a subset of entries starting at or 
    *						centered around those with a common name 
    *						beginning with the letter "M". 
    * 
    * @param beforeCount	The number of entries before startIndex (the 
	*						reference entry) to be returned. 
    *  
    * @param afterCount		The number of entries after startIndex to be 
	*						returned. 
	*/
	public LDAPVirtualListControl(	String	jumpTo, 
									int		beforeCount, 
									int		afterCount ) 
	{
		this(jumpTo, beforeCount, afterCount, null);
	}
 


  /**
	* 3.1.1 Constructors 
	* Constructs a virtual list control using the specified filter 
	* expression for the first entry, which defines the extent of the 
	* virtual search results, and the number of entries before and after a 
	* located index to be returned. 
	*
	*  @param jumpTo		A search expression that defines the first 
	*						element to be returned in the virtual search 
    *						results. The filter expression in the search 
    *						operation itself may be, for example, 
    *						"objectclass=person" and the jumpTo expression in 
    *						the virtual list control may be "cn=m*", to 
    *						retrieve a subset of entries starting at or 
    *						centered around those with a common name 
    *						beginning with the letter "M". 
    * 
    * @param beforeCount	The number of entries before startIndex (the 
	*						reference entry) to be returned. 
    *  
    * @param afterCount		The number of entries after startIndex to be 
	*						returned. 
	*    
    *  
    * @param context		Used by some implementations to process requests 
	*						more efficiently. The context should be null on 
	*						the first search, and thereafter it should be 
	*						whatever was returned by the server in the 
	*						virtual list response control. 
	*/
    public LDAPVirtualListControl(	String	jumpTo, 
									int		beforeCount, 
									int		afterCount, 
									String	context ) 
	{

		// Draft requires this to be a critical control
        super(OID, true, null);

		// Save off the fields locally
		m_beforeCount = beforeCount;
		m_afterCount = afterCount;
		m_jumpTo = jumpTo;
		m_context = context;

		// Build the request
		BuildTypedVLVRequest();

		// Set the request data in the parent LDAPControl
		setValue (m_vlvRequest.getEncoding(new LBEREncoder()));
	}

	/** Private method used to construct the ber encoded control
	 *  Used only when using the typed mode of VLV Control
	 */
	private void BuildTypedVLVRequest() 
	{
		m_vlvRequest = new ASN1Sequence(4);

		// Add the beforeCount and afterCount fields
        m_vlvRequest.add(new ASN1Integer(m_beforeCount));
		m_vlvRequest.add(new ASN1Integer(m_afterCount));

		// Add the CHOICE option corresponding to greaterthanOrEqual
		m_vlvRequest.add(new ASN1Tagged(	new ASN1Identifier(ASN1Identifier.CONTEXT, false,
											GREATERTHANOREQUAL),
										new RfcAssertionValue(m_jumpTo),
										false));

		// Add the optional context string if one is avaialable
		if (m_context != null)
			m_vlvRequest.add(new ASN1OctetString(m_context));

	}

  /**
	* 3.1.1 Constructors 
	* Use this constructor when the size of the virtual list is known, to 
	* fetch a subset. 
   	*
    * 
    * @param beforeCount	The number of entries before startIndex (the 
	*						reference entry) to be returned. 
    *  
    * @param afterCount		The number of entries after startIndex to be 
	*						returned. 
	*    
    * @param startIndex     The index of the reference entry to be returned. 
    *
    * @param contentCount	The total number of entries assumed to be in the 
	*						list. This is a number returned on a previous 
	*						search, in the LDAPVirtualListResponse. The 
	*						server may use this number to adjust the returned 
	*						subset offset. 
	*
	*/
	public LDAPVirtualListControl(	int		startIndex, 
									int		beforeCount, 
									int		afterCount, 
									int		contentCount ) 
	{
		this(startIndex, beforeCount, afterCount, contentCount, null);
	}



  /**
	* 3.1.1 Constructors 
	* Use this constructor when the size of the virtual list is known, to 
	* fetch a subset. 
   	*
    * 
    * @param beforeCount	The number of entries before startIndex (the 
	*						reference entry) to be returned. 
    *  
    * @param afterCount		The number of entries after startIndex to be 
	*						returned. 
	*    
    * @param startIndex     The index of the reference entry to be returned. 
    *
    * @param contentCount	The total number of entries assumed to be in the 
	*						list. This is a number returned on a previous 
	*						search, in the LDAPVirtualListResponse. The 
	*						server may use this number to adjust the returned 
	*						subset offset. 
	*
    *  
    * @param context		Used by some implementations to process requests 
	*						more efficiently. The context should be null on 
	*						the first search, and thereafter it should be 
	*						whatever was returned by the server in the 
	*						virtual list response control. 
	*/
    public LDAPVirtualListControl(	int		startIndex, 
									int		beforeCount, 
									int		afterCount, 
									int		contentCount, 
									String	context ) 
	{
		// Draft requires this to be a critical control
        super(OID, true, null);

        
		// Save off the fields locally
		m_beforeCount = beforeCount;
		m_afterCount = afterCount;
		m_startIndex = startIndex;
		m_contentCount = contentCount;
		m_context = context;

		// Build the request
		BuildIndexedVLVRequest();

		setValue (m_vlvRequest.getEncoding(new LBEREncoder()));

	}
    
	/** Private method used to construct the ber encoded control
	 *  Used only when using the Indexed mode of VLV Control
	 */
	private void BuildIndexedVLVRequest() 
	{
		m_vlvRequest = new ASN1Sequence(4);

		// Add the beforeCount and afterCount fields
        m_vlvRequest.add(new ASN1Integer(m_beforeCount));
		m_vlvRequest.add(new ASN1Integer(m_afterCount));

		// Construct the inner CHOICE sequence
		ASN1Sequence byoffset = new ASN1Sequence(2);
		byoffset.add(new ASN1Integer(m_startIndex));
		byoffset.add(new ASN1Integer(m_contentCount));;

		// Add the CHOICE option corresponding to byoffset choice
		m_vlvRequest.add(new ASN1Tagged(	new ASN1Identifier(ASN1Identifier.CONTEXT, true,
											BYOFFSET),
										byoffset,
										false));

		// Add the optional context string if one is avaialable
		if (m_context != null)
			m_vlvRequest.add(new ASN1OctetString(m_context));

	}


  /**
	*	3.1.2 getAfterCount 
    *	Returns the number of entries after the top/center one to return per 
	*   page of results. 
	*/
	public int getAfterCount() 
    {
	
		return m_afterCount;
		
	}   
    


  /**
	*	3.1.3 getBeforeCount 
	*	Returns the number of entries before the top/center one to return per  
	*   page of results. 
	*/
	public int getBeforeCount() 
    {

		return m_beforeCount;
	}    
    


  /**
    *	3.1.4 getListSize 
	*	Returns the size of the virtual search results list. For a newly 
	*	constructed control - one which is not the result of parseResponse on 
	*	a control returned by a server - the method returns -1. 
	*
	*	Same as the contentCount if it exists
	*/
   public int getListSize() 
   {
		return m_contentCount;

   }
    

    
    
  /**
    *	3.1.5 setListSize 
	*	Sets the assumed size of the virtual search results list. This will 
	*	typically be a number returned on a previous virtual list request in 
	*	an LDAPVirtualListResponse. 
	*/    
	public void setListSize( int size ) 
    {
		m_contentCount = size;

		// Rebuild the request
		BuildIndexedVLVRequest();
		setValue (m_vlvRequest.getEncoding(new LBEREncoder()));

	}
    

    
  /**
    *	3.1.6 setRange 
	*	Sets the center or starting list index to return, and the number of 
	*	results before and after. 
	*
	*	@param	listIndex		The center or starting list index to be returned. 
    *
    *	@param	beforeCount		The number of entries before "listIndex" to be 
	*							returned. 
    *
    *	@param	afterCount		The number of entries after "listIndex" to be 
	*							returned. 
	*/
    
	public void setRange( int listIndex, 
                         int beforeCount, 
                         int afterCount ) 
    {


	}

    
  /**
    *	3.1.6 setRange -missing
	*	Sets the center or starting list index to return, and the number of 
	*	results before and after. 
	*
	*  @param jumpTo		A search expression that defines the first 
	*						element to be returned in the virtual search 
    *						results. The filter expression in the search 
    *						operation itself may be, for example, 
    *						"objectclass=person" and the jumpTo expression in 
    *						the virtual list control may be "cn=m*", to 
    *						retrieve a subset of entries starting at or 
    *						centered around those with a common name 
    *						beginning with the letter "M". 
    *
    *	@param	beforeCount		The number of entries before "listIndex" to be 
	*							returned. 
    *
    *	@param	afterCount		The number of entries after "listIndex" to be 
	*							returned. 
	*/
    
	public void setRange( String jumpTo, 
                         int beforeCount, 
                         int afterCount ) 
    {

	}
    
  /**
	*	3.1.7 getContext 
	*   Returns the cookie used by some servers to optimize the processing of 
	*	virtual list requests. 
	*/    
	public String getContext() 
    {
		return m_context;
	}



    
  /**
    *	3.1.8 setContext 
	*	Sets the cookie used by some servers to optimize the processing of 
	*	virtual list requests. It should be the context field returned in a 
	*	virtual list response control for the same search. 
	*/
   public void setContext( String context ) 
   {	
		int CONTEXTIDINDEX = 3;
		m_context = context;

		if (m_vlvRequest.size() == 4) {
			// Replace it!
			m_vlvRequest.set(CONTEXTIDINDEX, new ASN1OctetString(m_context));
		}
		else if (m_vlvRequest.size() == 3) {
			// Add a new one - none existed before
			m_vlvRequest.add(new ASN1OctetString(m_context));
		}

		// Do not forget to set the value field in the parent
		setValue (m_vlvRequest.getEncoding(new LBEREncoder()));
		
   }
   
    
}

