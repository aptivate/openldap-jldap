/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
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

package com.novell.ldap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.novell.ldap.client.Debug;
import com.novell.ldap.client.ReferralInfo;
import com.novell.ldap.resources.ExceptionMessages;
import com.novell.ldap.util.Base64;
import com.novell.ldap.util.LDAPXMLHandler;
import com.novell.ldap.util.SAXEventMultiplexer;
import com.novell.ldap.util.ValueXMLhandler;

/**
 * <p>An LDAPSearchResults object is returned from a synchronous search
 * operation. It provides access to all results received during the
 * operation (entries and exceptions).</p>
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/jldap_sample/Search.java.html">Search.java</p>
 *
 * @see LDAPConnection#search
 */
public class LDAPSearchResults  
implements Externalizable
{

    private Vector entries;             // Search entries
    private int entryCount;             // # Search entries in vector
    private int entryIndex;             // Current position in vector
    private Vector references;          // Search Result References
    private int referenceCount;         // # Search Result Reference in vector
    private int referenceIndex;         // Current position in vector
    private int batchSize;              // Application specified batch size
    private boolean completed = false;  // All entries received
    private LDAPControl[] controls = null; // Last set of controls
    private LDAPSearchQueue queue;
    private static Object nameLock = new Object(); // protect resultsNum
    private static int resultsNum = 0;  // used for debug
    private String name;                // used for debug
    private LDAPConnection conn;        // LDAPConnection which started search
    private LDAPSearchConstraints cons; // LDAPSearchConstraints for search
    private ArrayList referralConn = null;// Referral Connections
    
    /**
	 * This constructor was added to support default Serialization
	 *
	 */
    public LDAPSearchResults() {
		//Required so that a DSML version of this cladd can be utilized
    }


    /**
     * Constructs a queue object for search results.
     *
     * @param  conn The LDAPConnection which initiated the search
     *<br><br>
     * @param queue The queue for the search results.
     *<br><br>
     * @param cons The LDAPSearchConstraints associated with this search
     */
    /* package */
    
    
    
    LDAPSearchResults(  LDAPConnection conn,
                        LDAPSearchQueue queue,
                        LDAPSearchConstraints cons)
    {
        // setup entry Vector
        this.conn = conn;
        this.cons = cons;
        int batchSize = cons.getBatchSize();
        int vectorIncr = (batchSize == 0) ? 64 : 0;
        entries = new Vector( (batchSize == 0) ? 64 : batchSize, vectorIncr );
        entryCount = 0;
        entryIndex = 0;

        // setup search reference Vector
        references = new Vector( 5, 5);
        referenceCount = 0;
        referenceIndex = 0;

        this.queue = queue;
        this.batchSize = (batchSize == 0) ? Integer.MAX_VALUE : batchSize;

        if( Debug.LDAP_DEBUG ) {
            synchronized(nameLock) {
                name = "LDAPSearchResults(" + ++resultsNum + "): ";
            }
            Debug.trace( Debug.messages, name +
                            " Object created, batch size " + this.batchSize +
                            ", hops " + cons.getHopLimit());
        }
        return;
    }

    /**
     * Returns a count of the items in the search result.
     *
     * <p>Returns a count of the entries and exceptions remaining in the object.
     * If the search was submitted with a batch size greater than zero, getCount
     * reports the number of results received so far but not enumerated with 
     * next(). The behavior is same for search submitted with batch size equals
     * to 0.</p>
     * <p> For blocking calls with batch size equals to 0, get count returns the
     * count of all the expected results from search for first time once after
     * enumerated with hasMore(). The subsequent iterations decrement count value 
     * for both blocking and non-blocking calls. </p>
     *
     * @return The number of items received but not retrieved by the application
     */
    public int getCount()
    {
        int qCount = queue.getMessageAgent().getCount();
        return entryCount - entryIndex + referenceCount - referenceIndex + qCount;
    }

    /**
     * Returns the latest server controls returned by the server
     * in the context of this search request, or null
     * if no server controls were returned.
     *
     * @return The server controls returned with the search request, or null
     *         if none were returned.
     */
    public LDAPControl[] getResponseControls()
    {
        return controls;
    }

    /**
     * Reports if there are more search results.
     *
     * @return true if there are more search results.
     */
    public boolean hasMore()
    {
        boolean ret = false;
        if( (entryIndex < entryCount) || (referenceIndex < referenceCount)) {
            // we have data
            ret = true;
        }
        else
        if(completed == false) { // reload the Vector by getting more results
            resetVectors();
            ret = (entryIndex < entryCount) || (referenceIndex < referenceCount);
        }
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name +
                "hasMoreElements: returns " + ret + ", enumeration status " +
                ", entryIdx=" + entryIndex +
                ", entryCnt=" + entryCount +
                ", referIdx=" + referenceIndex +
                ", referCnt=" + referenceCount);
        }
        return ret;
    }

    /*
     * If both of the vectors are empty, get more data for them.
     */
    private void resetVectors()
    {
        // If we're done, no further checking needed
        if( completed) {
            return;
        }
        // Checks if we have run out of references
        if( (referenceIndex != 0) && (referenceIndex >= referenceCount) ) {
            references.setSize(0);
            referenceCount = 0;
            referenceIndex = 0;
        }
        // Checks if we have run out of entries
        if( (entryIndex != 0) && (entryIndex >= entryCount) ) {
            entries.setSize(0);
            entryCount = 0;
            entryIndex = 0;
       }
       // If no data at all, must reload enumeration
       if( (referenceIndex == 0) && (referenceCount == 0) &&
                (entryIndex == 0) && (entryCount == 0)) {
            completed = getBatchOfResults();
       }
       return;
    }
    /**
     * Returns the next result as an LDAPEntry.
     *
     * <p>If automatic referral following is disabled or if a referral
     * was not followed, next() will throw an LDAPReferralException
     * when the referral is received.</p>
     *
     * @return The next search result as an LDAPEntry.
     *
     * @exception LDAPException A general exception which includes an error
     *                          message and an LDAP error code.
     * @exception LDAPReferralException A referral was received and not
     *                          followed.
     */
    public LDAPEntry next() throws LDAPException
    {
        if( completed && (entryIndex >= entryCount) &&
                (referenceIndex >= referenceCount) ) {
            throw new NoSuchElementException(
                "LDAPSearchResults.next() no more results");
        }
        // Check if the enumeration is empty and must be reloaded
        resetVectors();

        Object element = null;
        // Check for Search References & deliver to app as they come in
        // We only get here if not following referrals/references
        if( referenceIndex < referenceCount ) {
            String refs[] = (String[])(references.elementAt( referenceIndex++) );
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                    "next: throws referral exception");
                for( int i = 0; i < refs.length; i++) {
                    Debug.trace( Debug.messages, name + " \t" + refs[i]);
                }
            }
            LDAPReferralException rex = new LDAPReferralException(
                ExceptionMessages.REFERENCE_NOFOLLOW);
            rex.setReferrals( refs);
            throw rex;
        } else
        if( entryIndex < entryCount ) {
            // Check for Search Entries and the Search Result
            element = entries.elementAt( entryIndex++ );
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                    "next: returns " + element.getClass().getName() + "@" +
                    Integer.toHexString(element.hashCode()) +
                    ", elements remaining " + entries.size());
            }
            if(element instanceof LDAPResponse) {         // Search done w/bad status
                if( ((LDAPResponse)element).hasException()) {

                    LDAPResponse lr = (LDAPResponse)element;
                    ReferralInfo ri = lr.getActiveReferral();

                    if( Debug.LDAP_DEBUG ) {
                        Debug.trace( Debug.messages, name +
                            "next: LDAPResponse has embedded exception" +
                            " from following referral - " + (ri != null));
                    }
                    if( ri != null) {
                        // Error attempting to follow a search continuation reference
                        LDAPReferralException rex = new LDAPReferralException(
                            ExceptionMessages.REFERENCE_ERROR,
                            lr.getException());
                        rex.setReferrals(ri.getReferralList());
                        rex.setFailedReferral( ri.getReferralUrl().toString());
                        throw rex;
                    }
                }
                // Throw an exception if not success
                ((LDAPResponse)element).chkResultCode();
            } else
            if( element instanceof LDAPException) {
                if( Debug.LDAP_DEBUG ) {
                    Debug.trace( Debug.messages, name +
                        "next: LDAPException " + element.toString());
                }
                throw (LDAPException)element;
            }
        } else {
            // If not a Search Entry, Search Result, or search continuation
            // we are very confused.
            if( Debug.LDAP_DEBUG ) {
                Debug.trace( Debug.messages, name +
                    "next: No entry found and request incomplete\n" +
                    "\tentryIdx " + entryIndex +
                    ", entryCnt " + entryCount +
                    ", referIdx " + referenceIndex +
                    ", referCnt " + referenceCount );
            }
            // LDAPSearchResults.next(): No entry found & request is not complete
            throw new LDAPException(
                ExceptionMessages.REFERRAL_LOCAL,
                new Object[] { "next" },
                LDAPException.LOCAL_ERROR,(String)null);
        }
        return (LDAPEntry)element;
    }

    /**
     * Collects batchSize elements from an LDAPSearchQueue message
     * queue and places them in a Vector.
     *
     * <p>If the last message from the server,
     * the result message, contains an error, it will be stored in the Vector
     * for nextElement to process. (although it does not increment the search
     * result count) All search result entries will be placed in the Vector.
     * If a null is returned from getResponse(), it is likely that the search
     * was abandoned.</p>
     *
     * @return true if all search results have been placed in the vector.
     */
    private boolean getBatchOfResults()
    {
        LDAPMessage msg;

        // <=batchSize so that we can pick up the result-done message
        for(int i=0; i<batchSize; ) {
            try {
                if((msg = queue.getResponse()) != null) {
                    // Only save controls if there are some
                    LDAPControl[] ctls = msg.getControls();
                    if( ctls != null ) {

                        if( Debug.LDAP_DEBUG) {
                            Debug.trace( Debug.controls, name +
                                "Saving returned controls in " +
                                "LDAPSearchResults local variable.");
                        }
                        controls = ctls;
                    }

                    if(msg instanceof LDAPSearchResult) { // Search Entry
                        Object entry = ((LDAPSearchResult)msg).getEntry();
                        entries.addElement( entry );
                        i++;
                        entryCount++;
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "read LDAPEntry@" +
                                Integer.toHexString(entry.hashCode()) +
                                " from LDAPMessage@" +
                                Integer.toHexString(msg.hashCode()) );
                        }
                    } else
                    if(msg instanceof LDAPSearchResultReference) { // Search Ref
                        String[] refs = ((LDAPSearchResultReference)msg).getReferrals();
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name + "got " +
                                    refs.length + " references in entry ");
                            for( int k=0; k < refs.length; k++ ) {
                                Debug.trace( Debug.messages,
                                    name + "reference " + k + "\t" + refs[k]);
                            }
                        }

                        if( cons.getReferralFollowing() ) {
                           referralConn = conn.chaseReferral(
                                    queue, cons, msg, refs,
                                    0, true, referralConn);
                        } else {
                            references.addElement( refs );
                            referenceCount++;
                        }
                    } else { // LDAPResponse
                        LDAPResponse resp = (LDAPResponse)msg;
                        int resultCode = resp.getResultCode();
                        // Check for an embedded exception
                        if( resp.hasException()) {
                            // Fake it, results in an exception when msg read
                            resultCode = LDAPException.CONNECT_ERROR;
                            if( Debug.LDAP_DEBUG ) {
                                Debug.trace( Debug.messages, name +
                                    "LDAPResponse with embeddedException");
                            }
                        } else
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "read LDAPResponse@" +
                                Integer.toHexString(resp.hashCode()) +
                                ", result " + resultCode);
                        }

                        if( (resultCode == LDAPException.REFERRAL) &&
                                        cons.getReferralFollowing() ) {
                            // Following referrals
                            if( Debug.LDAP_DEBUG ) {
                                Debug.trace( Debug.messages, name +
                                    "following referrals");
                            }
                            referralConn = conn.chaseReferral(
                                        queue, cons, resp,
                                        resp.getReferrals(), 0,
                                        false, referralConn);
                        } else
                        if(resultCode != LDAPException.SUCCESS) {
                            // Results in an exception when message read
                            entries.addElement(resp);
                            entryCount++;
                            if( Debug.LDAP_DEBUG ) {
                                Debug.trace( Debug.messages, name +
                                    "Add LDAPResponse to entry list, count = " +
                                entries.size());
                            }
                        }
                        if( Debug.LDAP_DEBUG ) {
                            Debug.trace( Debug.messages, name +
                                "checking for done");
                        }
                        // We are done only when we have read all messages
                        // including those received from following referrals
                        int[] msgIDs = queue.getMessageIDs();
                        if( msgIDs.length == 0) {
                            if( Debug.LDAP_DEBUG ) {
                                Debug.trace( Debug.messages, name +
                                    "Search completed, all responses processed");
                            }
                            // Release referral exceptions
                            conn.releaseReferralConnections( referralConn);
                            return true; // search completed
                        } else {
                            if( Debug.LDAP_DEBUG ) {
                                Debug.trace( Debug.messages, name +
                                    "Search not done, " + msgIDs.length +
                                    " Messages still active");
                            }
                        }
                        continue;
                    }
                } else {
                    if( Debug.LDAP_DEBUG) {
                        Debug.trace( Debug.messages, name +
                            "Connection timeout, no results returned");
                    }
                    // We get here if the connection timed out
                    // we have no responses, no message IDs and no exceptions
                    LDAPException e = new LDAPException( null,
                                LDAPException.LDAP_TIMEOUT,(String)null);
                    entries.addElement(e );
                    break;
                }
            } catch(LDAPException e) {
                if( Debug.LDAP_DEBUG ) {
                    Debug.trace( Debug.messages, name +
                       "Caught exception, add to entry queue: " + e.toString());
                }
                // Hand exception off to user
                entries.addElement( e);
            }
            continue;
        }
        return false; // search not completed
    }

    /**
     * Cancels the search request and clears the message and enumeration.
     */
    /*package*/
    void abandon() {
        if( Debug.LDAP_DEBUG ) {
            Debug.trace( Debug.messages, name + "abandon: Entry");
        }
        // first, remove message ID and timer and any responses in the queue
        queue.getMessageAgent().abandonAll();

        // next, clear out enumeration
        resetVectors();
        completed = true;
    }
    
    //*************************************************************************
    // Externalizable methods implementation
    //*************************************************************************
	  private void writeAttribute(LDAPAttribute attr, StringBuffer buff) 
			  throws java.io.IOException
	  {
		  buff.append(ValueXMLhandler.newLine(2));
		  buff.append("<attr name=\"");
		  buff.append(attr.getName());
		  buff.append("\">");
	
			String values[] = attr.getStringValueArray();
			byte bytevalues[][] = attr.getByteValueArray();
			for(int i=0; i<values.length; i++){
				buff.append(ValueXMLhandler.newLine(3));
				
				if (Base64.isValidUTF8(bytevalues[i], false)){
					buff.append("<value><![CDATA[");
					buff.append(values[i]);
					buff.append("]]></value>");
				} else {
					buff.append("<value xsi:type=\"xsd:base64Binary\"><![CDATA[");
					buff.append(Base64.encode(bytevalues[i]));
					buff.append("]]></value>");
				}
			}
			buff.append(ValueXMLhandler.newLine(2));
			buff.append("</attr>");        
	  }
	  
	  private void writeControl(LDAPControl control, StringBuffer buff) 
	  throws java.io.IOException
		{
		  	buff.append("<control type=\"");
			buff.append(control.getID());
			buff.append("\" criticality=\""+ control.isCritical()+ "\"");
	
			byte value[] = control.getValue();
			if (value == null){
				buff.append("/>");
			} else {
				buff.append(">");
				buff.append(ValueXMLhandler.newLine(2));
				buff.append("<controlValue xsi:type=\"xsd:base64Binary\">");
				buff.append(Base64.encode(value));
				buff.append("</controlValue>");
				buff.append(ValueXMLhandler.newLine(1));
				buff.append("</control>");
			}
			buff.append(ValueXMLhandler.newLine(0));
		}

	  /**
		* This method is used to deserialize the DSML encoded representation of
		* this class.
		* @param input InputStream for the DSML formatted data. 
		* @return Deserialized form of this class.
		* @throws IOException when serialization fails.
		*/    
	    public static Object readDSML(InputStream input)throws IOException    
	    {
			SAXEventMultiplexer xmlreader = new SAXEventMultiplexer();
			xmlreader.setLDAPXMLHandler(getTopXMLHandler("LDAPSearchResults",null));		
			return (Vector) xmlreader.parseXML(input);
	    }
	    
	    private static LDAPXMLHandler getTopXMLHandler(String tagname, LDAPXMLHandler
		 parenthandler) {
		  return new LDAPXMLHandler(tagname, parenthandler) {

			Vector ldapEntries = new Vector();
			protected void initHandler() {
			  //set the child handlers
			  setchildelement(LDAPSearchResults.getXMLEntryHandler("LDAPEntry",this));
			  setchildelement(LDAPSearchResults.getXMLControlHandler("control",this));
			}

			protected void endElement() {
				setObject(ldapEntries);
			}
			protected void addValue(String tag, Object value) {
			  if (tag.equals("LDAPEntry")) {
			  	ldapEntries.add(value);
			  } 
			  else if (tag.equals("control")) {
			  	ldapEntries.add(value);
			  }
			}
		  };

		}
		
		/**
		* This method return the LDAPHandler which handles the XML (DSML) tags
		* for returned Server controls of this class
		* @param tagname Name of the Root tag used to represent this class.
		* @param parenthandler Parent LDAPXMLHandler for this tag.
		* @return LDAPXMLHandler to handle this element.
		*/    
	  static LDAPXMLHandler getXMLControlHandler(
	    String tagname,
	    LDAPXMLHandler parenthandler) {
	    return new LDAPXMLHandler(tagname, parenthandler) {
	      String oid;
	      boolean critical;
	      byte[] controlvalue;
	      protected void initHandler() {
	        //set value handler.
	        setchildelement(new ValueXMLhandler("controlValue", this));
	      }

	      protected void endElement() {
	        LDAPControl control = new LDAPControl(oid, critical, controlvalue);
	        setObject(control);
	      }
	      protected void addValue(String tag, Object value) {
	        if (tag.equals("controlValue")) {
	          controlvalue = (byte[]) value;
	        }
	      }

	      protected void handleAttributes(Attributes attributes)
	        throws SAXException {
	        oid = attributes.getValue("type");
	        if (oid == null) {
	          //Oid is mandatory.
	          throw new SAXException("type is mandatory for a Control");
	        }
	        critical = "true".equalsIgnoreCase(attributes.getValue("criticality"));
	      }

	    };

	  }

		/**
		* This method return the LDAPHandler which handles the XML (DSML) tags
		* for returned LDAPEntries of this class
		* @param tagname Name of the Root tag used to represent this class.
		* @param parenthandler Parent LDAPXMLHandler for this tag.
		* @return LDAPXMLHandler to handle this element.
		*/    		 
		static LDAPXMLHandler getXMLEntryHandler(String tagname,LDAPXMLHandler parenthandler) {
			return new LDAPXMLHandler(tagname, parenthandler) {
				String dn;
				ArrayList valuelist = new ArrayList();
				protected void initHandler() {
					//set LDAPAttribute handler.
					setchildelement(LDAPAttribute.getXMLHandler("attr",this));
				}
				protected void endElement() {
					LDAPAttributeSet attrset = new LDAPAttributeSet();
					attrset.addAll(valuelist);
					LDAPEntry entry = new LDAPEntry(dn,attrset);				
					setObject(entry);
					
					valuelist.clear();
				}
				protected void addValue(String tag, Object value) {
					if (tag.equals("attr")) {
						valuelist.add(value);
					}
				}
				protected void handleAttributes(Attributes attributes)throws SAXException {
						dn = attributes.getValue("dn");
						if (dn== null)
							throw new SAXException("invalid entry Tag, dn is mandatory element: ");
							}
	    		
				};
			}

   /**
   * Writes the object state to a stream in XML format  
   * @param out The ObjectOutput stream where the Object in XML format 
   * is being written to
   * @throws IOException - If I/O errors occur
   */  
   public void writeExternal(ObjectOutput out) throws IOException
   {
		StringBuffer buff = new StringBuffer();
		buff.append(ValueXMLhandler.newLine(0));
		buff.append(ValueXMLhandler.newLine(0));
		
		String header = "";
		header += "*************************************************************************\n";
		header += "** The encrypted data above and below is the Class definition and  ******\n";
		header += "** other data specific to Java Serialization Protocol. The data  ********\n";
		header += "** which is of most application specific interest is as follows... ******\n";
		header += "*************************************************************************\n";
		header += "****************** Start of application data ****************************\n";
		header += "*************************************************************************\n";
		  
		buff.append(header);
		buff.append(ValueXMLhandler.newLine(0));
		
		buff.append("<LDAPSearchResults>");
		buff.append(ValueXMLhandler.newLine(1));
		//write data for LDAP Entries returned from search
		while (hasMore()){
        	LDAPEntry entry = null;
        	try{
        		entry = (LDAPEntry)next();
        	}catch(LDAPException le){
        		//Ignore the Exception. Continue to the next entry
        		continue;
        	}
			 buff.append("<LDAPEntry dn=\"");
    		 buff.append(entry.getDN());
    		 buff.append("\">");
      
    		 Iterator i = entry.getAttributeSet().iterator();
    		 while (i.hasNext()){
    			 writeAttribute( (LDAPAttribute) i.next(),buff);
    		 }
    		 buff.append(ValueXMLhandler.newLine(1));
    		 buff.append("</LDAPEntry>");
    		 buff.append(ValueXMLhandler.newLine(1));
		 }
		 
		 //write data for server response controls
		 LDAPControl[] controls = getResponseControls();
         if ( controls != null ) {
             for( int i = 0; i < controls.length; i++ ) {
             	writeControl(controls[i], buff);
             }
         }
		buff.append("</LDAPSearchResults>"); 
		buff.append(ValueXMLhandler.newLine(0));
		buff.append(ValueXMLhandler.newLine(0));
		
		String tail = "";
		tail += "*************************************************************************\n";
		tail += "****************** End of application data ******************************\n";
		tail += "*************************************************************************\n";
		  
		buff.append(tail);
		buff.append(ValueXMLhandler.newLine(0));       
		out.write(buff.toString().getBytes());	
   }
   /**
	 * Reads the serialized object from the underlying input stream.
	 * @param in The ObjectInput stream where the Serialized Object is being read from
	 * @throws IOException - If I/O errors occur
	 * @throws ClassNotFoundException - If the class for an object being restored 
	 * cannot be found.
	 */ 
	 public void readExternal(ObjectInput in) 
			throws IOException, ClassNotFoundException
	 {
		ObjectInputStream reader = (ObjectInputStream)in;	
		StringBuffer rawBuff = new StringBuffer();
		while(reader.available() != 0)
			rawBuff.append((char)reader.read());

		String readData = rawBuff.toString();
		readData = readData.substring(readData.indexOf('<'), 
			  (readData.lastIndexOf('>') + 1));

		//Insert  parsing logic here for separating whitespaces in non-text nodes
		StringBuffer parsedBuff = new StringBuffer();
		ValueXMLhandler.parseInput(readData, parsedBuff);
	  
		BufferedInputStream istream = 
			new BufferedInputStream(
				new ByteArrayInputStream((parsedBuff.toString()).getBytes()));

		Vector readList = 
					  (Vector)LDAPSearchResults.readDSML(istream);
		
		/*
		 * Temporary structures
		 */
		Vector ldapEntries = new Vector();
		ArrayList controlList = new ArrayList();
		
		Enumeration it = readList.elements();
		while(it.hasMoreElements()){
			LDAPEntry entry;
			LDAPControl control;
			Object ob = it.nextElement();
			if (ob instanceof LDAPEntry)
				ldapEntries.add((LDAPEntry)ob);
			else if (ob instanceof LDAPControl) 
				controlList.add((LDAPControl)ob);
		}
		
		this.entries = ldapEntries;
		
		controls = new LDAPControl[controlList.size()];
		for(int i=0; i < controlList.size(); i++)
			controls[i] = (LDAPControl)controlList.get(i);
	
		//Garbage collect the readObject from readDSML()..	
		readList = null;
		ldapEntries = null;
		controlList = null;
	 }
	 
	 /**
     * Returns LDAP Entries after De-serialization
     *
     * @return entries as Vector.
     */
	 public Vector getDeSerializedEntries(){
	 	return entries;
	 }
	 
	 /**
     * Returns LDAP Server Controls after De-serialization
     *
     * @return controls as Array.
     */
	 public LDAPControl[] getDeSerializedControls(){
	 	return controls;
	 }

}
