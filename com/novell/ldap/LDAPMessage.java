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

package com.novell.ldap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.novell.ldap.client.Debug;
import com.novell.ldap.client.RespControlVector;
import com.novell.ldap.rfc2251.RfcControl;
import com.novell.ldap.rfc2251.RfcControls;
import com.novell.ldap.rfc2251.RfcLDAPMessage;
import com.novell.ldap.rfc2251.RfcRequest;
import com.novell.ldap.util.DSMLReader;
import com.novell.ldap.util.ValueXMLhandler;

/**
 * The base class for LDAP request and response messages.
 *
 * <p>Subclassed by response messages used in asynchronous operations.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/asynchronous/Searchas.java.html">Searchas.java</a></p>
 *
 */
public class LDAPMessage implements Externalizable 
{

	/**
	 * A bind request operation.
	 *
	 *<p>BIND_REQUEST = 0</p>
	 */
	public final static int BIND_REQUEST            = 0;

	/**
	 * A bind response operation.
	 *
	 *<p>BIND_RESPONSE = 1</p>
	 */
	public final static int BIND_RESPONSE           = 1;

	/**
	 * An unbind request operation.
	 *
	 *<p>UNBIND_REQUEST = 2</p>
	 */
	public final static int UNBIND_REQUEST          = 2;

	/**
	 * A search request operation.
	 *
	 *<p>SEARCH_REQUEST = 3</p>
	 */
	public final static int SEARCH_REQUEST          = 3;

	/**
	 * A search response containing data.
	 *
	 *<p>SEARCH_RESPONSE = 4</p>
	 */
	public final static int SEARCH_RESPONSE         = 4;

	/**
	 * A search result message - contains search status.
	 *
	 *<p>SEARCH_RESULT = 5</p>
	 */
	public final static int SEARCH_RESULT           = 5;

	/**
	 * A modify request operation.
	 *
	 *<p>MODIFY_REQUEST = 6</p>
	 */
	public final static int MODIFY_REQUEST          = 6;

	/**
	 * A modify response operation.
	 *
	 *<p>MODIFY_RESPONSE = 7</p>
	 */
	public final static int MODIFY_RESPONSE         = 7;

	/**
	 * An add request operation.
	 *
	 *<p>ADD_REQUEST = 8</p>
	 */
	public final static int ADD_REQUEST             = 8;

	/**
	 * An add response operation.
	 *
	 *<p>ADD_RESONSE = 9</p>
	 */
	public final static int ADD_RESPONSE            = 9;

	/**
	 * A delete request operation.
	 *
	 *<p>DEL_REQUEST = 10</p>
	 */
	public final static int DEL_REQUEST             = 10;

	/**
	 * A delete response operation.
	 *
	 *<p>DEL_RESONSE = 11</p>
	 */
	public final static int DEL_RESPONSE            = 11;

	/**
	 * A modify RDN request operation.
	 *
	 *<p>MODIFY_RDN_REQUEST = 12</p>
	 */
	public final static int MODIFY_RDN_REQUEST      = 12;

	/**
	 * A modify RDN response operation.
	 *
	 *<p>MODIFY_RDN_RESPONSE = 13</p>
	 */
	public final static int MODIFY_RDN_RESPONSE     = 13;

	/**
	 * A compare result operation.
	 *
	 *<p>COMPARE_REQUEST = 14</p>
	 */
	public final static int COMPARE_REQUEST         = 14;

	/**
	 * A compare response operation.
	 *
	 *<p>COMPARE_RESPONSE = 15</p>
	 */
	public final static int COMPARE_RESPONSE        = 15;

	/**
	 * An abandon request operation.
	 *
	 *<p>ABANDON_REQUEST = 16</p>
	 */
	public final static int ABANDON_REQUEST         = 16;


	/**
	 * A search result reference operation.
	 *
	 *<p>SEARCH_RESULT_REFERENCE = 19</p>
	 */
	public final static int SEARCH_RESULT_REFERENCE = 19;

	/**
	 * An extended request operation.
	 *
	 *<p>EXTENDED_REQUEST = 23</p>
	 */
	public final static int EXTENDED_REQUEST        = 23;

	/**
	 * An extended response operation.
	 *
	 *<p>EXTENDED_RESONSE = 24</p>
	 */
	public final static int EXTENDED_RESPONSE       = 24;

	/**
	 * An extended response operation.
	 *
	 *<p>EXTENDED_RESONSE = 24</p>
	 */
	public final static int INTERMEDIATE_RESPONSE   = 25;

	/**
	 * A request or response message for an asynchronous LDAP operation.
	 */
	protected RfcLDAPMessage message;

	/**
	 * Lock object to protect counter for message numbers
	 */
	/*
	private static Object msgLock = new Object();
	*/

	/**
	 * Counters used to construct request message #'s, unique for each request
	 * Will be enabled after ASN.1 conversion
	 */
	/*
	private static int msgNum = 0; // LDAP Request counter
	*/
	private int imsgNum = -1;     // This instance LDAPMessage number

	private int messageType = -1;

	/* application defined tag to identify this message */
	private String stringTag = null;
    
    /**
	 * Dummy constuctor
	 */
	/* package */
	LDAPMessage()
	{
		return;
	}
	
	/**
	 * Added for supporting XML Serialization
	 */
	/* package */
	LDAPMessage(int messageType)
	{
		this.messageType = messageType;
		return;
	}

	/**
	 * Creates an LDAPMessage when sending a protocol operation and sends
	 * some optional controls with the message.
	 *
	 * @param op The operation type of message.
	 *<br><br>
	 * @param controls The controls to use with the operation.
	 *
	 * @see #getType
	 */
	/*package*/
	LDAPMessage( int type,
				 RfcRequest op,
				 LDAPControl[] controls)
	{

		// Get a unique number for this request message
		messageType = type;
		RfcControls asn1Ctrls = null;
		if(controls != null) {
			// Move LDAPControls into an RFC 2251 Controls object.
			asn1Ctrls = new RfcControls();
			for(int i=0; i<controls.length; i++) {
				asn1Ctrls.add(controls[i].getASN1Object());
			}
		}

		// create RFC 2251 LDAPMessage
		message = new RfcLDAPMessage(op, asn1Ctrls);
		if( Debug.LDAP_DEBUG) {
			Debug.trace( Debug.apiRequests, "Creating " + toString());
		}
		return;
	}

	/**
	 * Creates an Rfc 2251 LDAPMessage when the libraries receive a response
	 * from a command.
	 *
	 * @param message A response message.
	 */
	protected
	LDAPMessage(RfcLDAPMessage message)
	{
		this.message = message;
		return;
	}

	/**
	 * Returns a mutated clone of this LDAPMessage,
	 * replacing base dn, filter.
	 *
	 * @param dn the base dn
	 * <br><br>
	 * @param filter the filter
	 * <br><br>
	 * @param reference true if a search reference
	 *
	 * @return the object representing the new message
	 */
	/* package */
	final LDAPMessage clone( String dn, String filter, boolean reference)
			throws LDAPException
	{
		return new LDAPMessage(
			(RfcLDAPMessage)message.dupMessage( dn, filter, reference));
	}

	/**
	 * Returns the LDAPMessage request associated with this response
	 */
	/* package */
	final LDAPMessage getRequestingMessage()
	{
		if( Debug.LDAP_DEBUG) {
			if( isRequest()) {
				throw new RuntimeException("LDAPMessage: Cannot retrieve " +
					"requesting message for an LDAP Request Message");
			}
		}
		return message.getRequestingMessage();
	}

	/**
	 * Returns any controls in the message.
	 */
	public LDAPControl[] getControls()
	{

		LDAPControl[] controls = null;
		RfcControls asn1Ctrls = message.getControls();

		// convert from RFC 2251 Controls to LDAPControl[].
		if(asn1Ctrls != null) {
			controls = new LDAPControl[asn1Ctrls.size()];
			for(int i=0; i<asn1Ctrls.size(); i++) {

				/*
				 * At this point we have an RfcControl which needs to be
				 * converted to the appropriate Response Control.  This requires
				 * calling the constructor of a class that extends LDAPControl.
				 * The controlFactory method searches the list of registered
				 * controls and if a match is found calls the constructor
				 * for that child LDAPControl. Otherwise, it returns a regular
				 * LDAPControl object.
				 *
				 * Question: Why did we not call the controlFactory method when
				 * we were parsing the control. Answer: By the time the
				 * code realizes that we have a control it is already too late.
				 */
				RfcControl rfcCtl = (RfcControl)asn1Ctrls.get(i);
				String oid = rfcCtl.getControlType().stringValue();
				byte[] value = rfcCtl.getControlValue().byteValue();
				boolean critical = rfcCtl.getCriticality().booleanValue();

				/* Return from this call should return either an LDAPControl
				 * or a class extending LDAPControl that implements the
				 * appropriate registered response control
				 */
				controls[i] = controlFactory(oid, critical, value);
			}
		}
		return controls;
	}

	/**
	 * Instantiates an LDAPControl.  We search through our list of
	 * registered controls.  If we find a matchiing OID we instantiate
	 * that control by calling its contructor.  Otherwise we default to
	 * returning a regular LDAPControl object
	 *
	 */
	private final LDAPControl controlFactory(String oid,boolean critical,byte[] value)
	{
		RespControlVector regControls = LDAPControl.getRegisteredControls();
		try {
			/*
			 * search through the registered extension list to find the
			 * response control class
			 */
			Class respCtlClass = regControls.findResponseControl(oid);

			// Did not find a match so return default LDAPControl
			if ( respCtlClass == null)
				return new LDAPControl(oid, critical, value);

			if( Debug.LDAP_DEBUG) {
				Debug.trace( Debug.controls,
				 "For oid " + oid + ", found class " + respCtlClass.toString());

			}

			/* If found, get LDAPControl constructor */
			Class[] argsClass = { String.class, boolean.class, byte[].class };
			Object[] args = new Object[] {oid, new Boolean(critical), value};
			Exception ex = null;
			try {
				Constructor ctlConstructor =
										 respCtlClass.getConstructor(argsClass);

				try {
					/* Call the control constructor for a registered Class*/
					Object ctl = null;
					ctl = ctlConstructor.newInstance(args);
					return (LDAPControl)ctl;
				} catch (InstantiationException e) {
					// Could not create the ResponseControl object
					// All possible exceptions are ignored. We fall through
					// and create a default LDAPControl object
					ex = e;
				} catch (IllegalAccessException e) {
					ex = e;
				} catch (InvocationTargetException e) {
					ex = e;
				}
			} catch (NoSuchMethodException e) {
				// bad class was specified, fall through and return a
				// default LDAPControl object
				ex = e;
			}
			if( Debug.LDAP_DEBUG) {
				Debug.trace( Debug.controls,
					  "Unable to create new instance of child LDAPControl");
				Debug.trace( Debug.controls,
				   ex.toString());
			}
		} catch (NoSuchFieldException e) {
			// No match with the OID
			// Do nothing. Fall through and construct a default LDAPControl object.
			if( Debug.LDAP_DEBUG) {
				Debug.trace( Debug.controls,
					  "Oid " + oid + " not registered");
			}
		}
		// If we get here we did not have a registered response control
		// for this oid.  Return a default LDAPControl object.
		return new LDAPControl( oid, critical, value);
	}

	/**
	 * Returns the message ID.  The message ID is an integer value
	 * identifying the LDAP request and its response.
	 */
	public int getMessageID()
	{
		if( imsgNum == -1) {
			imsgNum = message.getMessageID();
		}
		return imsgNum;
	}

	/**
	 * Returns the LDAP operation type of the message.
	 *
	 * <p>The type is one of the following:</p>
	 * <ul>
	 *   <li>BIND_REQUEST            = 0;</li>
	 *   <li>BIND_RESPONSE           = 1;</li>
	 *   <li>UNBIND_REQUEST          = 2;</li>
	 *   <li>SEARCH_REQUEST          = 3;</li>
	 *   <li>SEARCH_RESPONSE         = 4;</li>
	 *   <li>SEARCH_RESULT           = 5;</li>
	 *   <li>MODIFY_REQUEST          = 6;</li>
	 *   <li>MODIFY_RESPONSE         = 7;</li>
	 *   <li>ADD_REQUEST             = 8;</li>
	 *   <li>ADD_RESPONSE            = 9;</li>
	 *   <li>DEL_REQUEST             = 10;</li>
	 *   <li>DEL_RESPONSE            = 11;</li>
	 *   <li>MODIFY_RDN_REQUEST      = 12;</li>
	 *   <li>MODIFY_RDN_RESPONSE     = 13;</li>
	 *   <li>COMPARE_REQUEST         = 14;</li>
	 *   <li>COMPARE_RESPONSE        = 15;</li>
	 *   <li>ABANDON_REQUEST         = 16;</li>
	 *   <li>SEARCH_RESULT_REFERENCE = 19;</li>
	 *   <li>EXTENDED_REQUEST        = 23;</li>
	 *   <li>EXTENDED_RESPONSE       = 24;</li>
	 * </ul>
	 *
	 * @return The operation type of the message.
	 */
	public int getType()
	{
		if( messageType == -1) {
			messageType = message.getType();
		}
		return messageType;
	}

	/**
	 * Indicates whether the message is a request or a response
	 *
	 * @return true if the message is a request, false if it is a response,
	 * a search result, or a search result reference.
	 */
	public boolean isRequest()
	{
		return message.isRequest();
	}

	/**
	 * Returns the RFC 2251 LDAPMessage composed in this object.
	 */
	/* package */
	RfcLDAPMessage getASN1Object()
	{
		return message;
	}

	/**
	 * Creates a String representation of this object
	 *
	 * @return a String representation for this LDAPMessage
	 */
	public String toString()
	{
		return getName() + "(" + getMessageID() + "): " + message.toString();
	}

	private final
	String getName()
	{
		String name;
		switch(getType()) {
			case SEARCH_RESPONSE:
				name = "LDAPSearchResponse";
				break;
			case SEARCH_RESULT:
				name = "LDAPSearchResult";
				break;
			case SEARCH_REQUEST:
				name = "LDAPSearchRequest";
				break;
			case MODIFY_REQUEST:
				name = "LDAPModifyRequest";
				break;
			case MODIFY_RESPONSE:
				name = "LDAPModifyResponse";
				break;
			case ADD_REQUEST:
				name = "LDAPAddRequest";
				break;
			case ADD_RESPONSE:
				name = "LDAPAddResponse";
				break;
			case DEL_REQUEST:
				name = "LDAPDelRequest";
				break;
			case DEL_RESPONSE:
				name = "LDAPDelResponse";
				break;
			case MODIFY_RDN_REQUEST:
				name = "LDAPModifyRDNRequest";
				break;
			case MODIFY_RDN_RESPONSE:
				name = "LDAPModifyRDNResponse";
				break;
			case COMPARE_REQUEST:
				name = "LDAPCompareRequest";
				break;
			case COMPARE_RESPONSE:
				name = "LDAPCompareResponse";
				break;
			case BIND_REQUEST:
				name = "LDAPBindRequest";
				break;
			case BIND_RESPONSE:
				name = "LDAPBindResponse";
				break;
			case UNBIND_REQUEST:
				name = "LDAPUnbindRequest";
				break;
			case ABANDON_REQUEST:
				name = "LDAPAbandonRequest";
				break;
			case SEARCH_RESULT_REFERENCE:
				name = "LDAPSearchResultReference";
				break;
			case EXTENDED_REQUEST:
				name = "LDAPExtendedRequest";
				break;
			case EXTENDED_RESPONSE:
				name = "LDAPExtendedResponse";
				break;
			case INTERMEDIATE_RESPONSE:
				name = "LDAPIntermediateResponse";
				break;
			default:
				throw new RuntimeException("LDAPMessage: Unknown Type " + getType());
		}
		return name;
	}

	/**
	 * Sets a string identifier tag for this message.
	 *
	 * <p>This method allows an API to set a tag and later identify messages
	 * by retrieving the tag associated with the message.
	 * Tags are set by the application and not by the API or the server.
	 * Message tags are not included with any message sent to or received
	 * from the server.</p>
	 *
	 * <p>Tags set on a request to the server
	 * are automatically associated with the response messages when they are
	 * received by the API and transferred to the application.
	 * The application can explicitly set a different value in a
	 * response message.</p>
	 *
	 * <p>To set a value in a server request, for example an
	 * {@link LDAPSearchRequest}, you must create the object,
	 * set the tag, and use the
	 * {@link LDAPConnection#sendRequest LDAPConnection.sendRequest()}
	 * method to send it to the server.</p>
	 *
	 * @param stringTag  the String assigned to identify this message.
	 *
	 * @see #getTag
	 * @see #isRequest
	 */
	public void setTag(String stringTag)
	{
		this.stringTag = stringTag;
		return;
	}

	/**
	 * Retrieves the identifier tag for this message.
	 *
	 * <p>An identifier can be associated with a message with the
	 * <code>setTag</code> method.
	 * Tags are set by the application and not by the API or the server.
	 * If a server response <code>isRequest() == false</code> has no tag,
	 * the tag associated with the corresponding server request is used.</p>
	 *
	 * @return the identifier associated with this message or <code>null</code>
	 *          if none.
	 *
	 * @see #setTag
	 * @see #isRequest
	 */
	public String getTag()
	{
		if (this.stringTag != null) {
			return this.stringTag;
		}
		if (isRequest()) {
			return null;
		}
		LDAPMessage m = this.getRequestingMessage();
		if (m == null) {
			return null;
		}
		return m.stringTag;
	}

	/**
	 * This method does DSML serialization of the instance.
	 *
	 * @param oout Outputstream where the serialzed data has to be written
	 *
	 * @throws IOException if write fails on OutputStream 
	 */    
	public void writeDSML(java.io.OutputStream  oout) throws java.io.IOException
	{
		com.novell.ldap.util.DSMLWriter out=new 
						 com.novell.ldap.util.DSMLWriter(oout);
		try{
			out.useIndent(true);
			out.setIndent(4);    	
			out.writeMessage(this);
			out.finish();
		}catch(LDAPLocalException le){
				}
	}
	
	/**
	 * This method is used to deserialize the DSML encoded representation of
	 * this class.
	 * @param input InputStream for the DSML formatted data. 
	 * @return Deserialized form of this class.
	 * @throws IOException when serialization fails.
	 */
	public static Object readDSML(InputStream input) throws IOException {
	 LDAPMessage msg = null;
	 try {
		DSMLReader reader = new DSMLReader(input);
		msg = reader.readMessage();
		} catch (LDAPLocalException e) {
		 e.printStackTrace();
		 throw new IOException("LDAPLocalException"+ e);
		}
		return msg;	
	}

	/**
   * Writes the object state to a stream in XML format  
   * @param out The ObjectOutput stream where the Object in XML format 
   * is being written to
   * @throws IOException - If I/O errors occur
   */  
   public void writeExternal(ObjectOutput out) throws IOException
   {
	  String header = "\n\n";
	  header += "*************************************************************************\n";
	  header += "** The encrypted data above and below is the Class definition and  ******\n";
	  header += "** other data specific to Java Serialization Protocol. The data  ********\n";
	  header += "** which is of most application specific interest is as follows... ******\n";
	  header += "*************************************************************************\n";
	  header += "****************** Start of application data ****************************\n";
	  header += "*************************************************************************\n\n";
		
	  String tail = "";
	  tail += "\n*************************************************************************\n";
	  tail += "****************** End of application data ******************************\n";
	  tail += "*************************************************************************\n";

	  try{		
		com.novell.ldap.util.DSMLWriter writer =new 
			 com.novell.ldap.util.DSMLWriter((ObjectOutputStream)out);
			
		writer.useIndent(true);
		writer.setIndent(4); 
		out.writeUTF(header);   	
		writer.writeMessage(this);
		writer.finish();
		out.writeUTF(tail);
		}
		catch(LDAPLocalException le){
			le.printStackTrace();
		}
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
		while(reader.available() != 0){
			rawBuff.append((char)reader.read());
		}
		String readData = rawBuff.toString();
	    String readProperties = readData.substring(readData.indexOf('<'), 
					(readData.lastIndexOf('>') + 1));
	  			
	  //Insert  parsing logic here for separating whitespaces in non-text nodes
	  StringBuffer parsedBuff = new StringBuffer();
	  ValueXMLhandler.parseInput(readProperties, parsedBuff);
	    
	  BufferedInputStream istream = 
			  new BufferedInputStream(
					  new ByteArrayInputStream((parsedBuff.toString()).getBytes()));
					  
		LDAPMessage readObject = null;
		 try {
			DSMLReader dsmlreader = new DSMLReader(istream);
		readObject = dsmlreader.readMessage();
		} catch (LDAPLocalException e) {
			 throw new IOException("LDAPLocalException"+ e);
		}
		
		//get the generic contols in super class of all messages
		LDAPControl[] cont = readObject.getControls();
		RfcControls asn1Ctrls = null;
		RfcRequest operation = null;
		 if(cont != null) {
			 asn1Ctrls = new RfcControls();
			 for(int i=0; i<cont.length; i++) {
				 asn1Ctrls.add(cont[i].getASN1Object());
			 }
		 }
		 //Subclasses override this..
		setDeserializedValues(readObject, asn1Ctrls);
		
   }

   //Sub classes need to override this method
   protected void setDeserializedValues(LDAPMessage readObject, 
   		RfcControls asn1Ctrls) throws IOException, ClassNotFoundException {
   		//Empty implementation goes here..
   }   
   
}
