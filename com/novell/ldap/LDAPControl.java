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
import java.io.ObjectOutput;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.novell.ldap.asn1.ASN1Boolean;
import com.novell.ldap.asn1.ASN1OctetString;
import com.novell.ldap.client.RespControlVector;
import com.novell.ldap.rfc2251.RfcControl;
import com.novell.ldap.rfc2251.RfcLDAPOID;
import com.novell.ldap.util.Base64;
import com.novell.ldap.util.LDAPXMLHandler;
import com.novell.ldap.util.SAXEventMultiplexer;
import com.novell.ldap.util.ValueXMLhandler;
/**
 *  Encapsulates optional additional parameters or constraints to be applied to
 *  an LDAP operation.
 *
 * <p>When included with LDAPConstraints or LDAPSearchConstraints
 * on an LDAPConnection or with a specific operation request, it is
 * sent to the server along with operation requests.</p>
 *
 * @see LDAPConnection#getResponseControls
 * @see LDAPSearchConstraints#getControls
 * @see LDAPSearchConstraints#setControls
 */
public class LDAPControl implements Cloneable,Externalizable {

    private static RespControlVector registeredControls =
                                                    new RespControlVector(5, 5);

    private RfcControl control; // An RFC 2251 Control

	/**
	 * This constructor was added to support default Serialization
	 *
	 */
	public LDAPControl()
	{
		super();
	}
    
    /**
     * Constructs a new LDAPControl object using the specified values.
     *
     *  @param oid     The OID of the control, as a dotted string.
     *<br><br>
     *  @param critical   True if the LDAP operation should be discarded if
     *                    the control is not supported. False if
     *                    the operation can be processed without the control.
     *<br><br>
     *  @param values     The control-specific data.
     */
    public LDAPControl(String oid, boolean critical, byte[] values)
    {
        if( oid == null) {
            throw new IllegalArgumentException("An OID must be specified");
        }
        if( values == null) {
            control = new RfcControl( new RfcLDAPOID(oid),
                                      new ASN1Boolean(critical));
        } else {
            control = new RfcControl( new RfcLDAPOID(oid),
                                      new ASN1Boolean(critical),
                                      new ASN1OctetString(values));
        }
        return;
    }

    /**
     * Create an LDAPControl from an existing control.
     */
    protected LDAPControl(RfcControl control)
    {
        this.control = control;
        return;
    }

    /**
     * Returns a copy of the current LDAPControl object.
     *
     * @return A copy of the current LDAPControl object.
     */
    public Object clone()
    {
        LDAPControl cont;
        try {
            cont = (LDAPControl)super.clone();
        } catch( CloneNotSupportedException ce) {
            throw new RuntimeException("Internal error, cannot create clone");
        }
       byte[] vals = this.getValue();
       byte[] twin = null;
       if( vals != null) {
           //is this necessary?
           // Yes even though the contructor above allocates a
           // new ASN1OctetString, vals in that constuctor
           // is only copied by reference
           twin = new byte[vals.length];
           for(int i = 0; i < vals.length; i++){
             twin[i]=vals[i];
           }
           cont.control = new RfcControl( new RfcLDAPOID(getID()),
                                          new ASN1Boolean(isCritical()),
                                          new ASN1OctetString(twin));
       }
       return cont;
    }

    /**
     * Returns the identifier of the control.
     *
     * @return The object ID of the control.
     */
    public String getID()
    {
        return new String(control.getControlType().stringValue());
    }

    /**
     * Returns the control-specific data of the object.
     *
     * @return The control-specific data of the object as a byte array,
     * or null if the control has no data.
     */
    public byte[] getValue()
    {
        byte[] result = null;
        ASN1OctetString val = control.getControlValue();
        if( val != null) {
            result = val.byteValue();
        }
        return result;
    }


    /**
     * Sets the control-specific data of the object.  This method is for
     * use by an extension of LDAPControl.
     */
    protected void setValue(byte[] controlValue)
    {
        control.setControlValue(new ASN1OctetString(controlValue));
        return;
    }


    /**
     * Returns whether the control is critical for the operation.
     *
     * @return Returns true if the control must be supported for an associated
     * operation to be executed, and false if the control is not required for
     * the operation.
     */
    public boolean isCritical()
    {
        return control.getCriticality().booleanValue();
    }

    /**
     * Registers a class to be instantiated on receipt of a control with the
     * given OID.
     *
     * <p>Any previous registration for the OID is overridden. The
     * controlClass must be an extension of LDAPControl.</p>
     *
     *  @param oid            The object identifier of the control.
     *<br><br>
     *  @param controlClass   A class which can instantiate an LDAPControl.
     */
    public static void register(String oid, Class controlClass)
    {
        registeredControls.registerResponseControl(oid, controlClass);
        return;
    }

    /* package */
    static RespControlVector getRegisteredControls()
    {
        return registeredControls;
    }

    /**
     * Returns the RFC 2251 Control object.
     *
     * @return An ASN.1 RFC 2251 Control.
     */
    /*package*/ final RfcControl getASN1Object()
    {
        return control;
    }

    void newLine(int indentTabs,java.io.Writer out) throws IOException
    {
        String tabString = "    ";    
        
        out.write("\n");
        for (int i=0; i< indentTabs; i++){
            out.write(tabString);
        }
        return;
    }
    
    /**
     * This method does DSML serialization of the instance.
     *
     * @param oout Outputstream where the serialzed data has to be written
     *
     * @throws IOException if write fails on OutputStream 
     */    
    public void writeDSML(java.io.OutputStream oout) throws IOException
    {
        java.io.Writer out=new java.io.OutputStreamWriter(oout,"UTF-8");
        int indent=0;
//        newLine(indent,out);
        out.write("<control type=\"");
        out.write(getID());
        out.write("\" criticality=\""+isCritical()+ "\"");

        byte value[] = getValue();
        if (value == null){
            out.write("/>");
        } else {
            out.write(">");
            newLine(indent+1,out);
            out.write("<controlValue xsi:type=\"xsd:base64Binary\">");
            out.write(Base64.encode(value));
            out.write("</controlValue>");
            newLine(indent,out);
            out.write("</control>");
        }
        out.close();
    }
	/**
	* This method is used to deserialize the DSML encoded representation of
	* this class.
	* @param input InputStream for the DSML formatted data. 
	* @return Deserialized form of this class.
	* @throws IOException when serialization fails.
	*/   
  public static Object readDSML(InputStream input) throws IOException {
    SAXEventMultiplexer xmlreader = new SAXEventMultiplexer();
    xmlreader.setLDAPXMLHandler(getXMLHandler("control", null));
    return (LDAPControl) xmlreader.parseXML(input);
  }
	/**
	* This method return the LDAPHandler which handles the XML (DSML) tags
	* for this class
	* @param tagname Name of the Root tag used to represent this class.
	* @param parenthandler Parent LDAPXMLHandler for this tag.
	* @return LDAPXMLHandler to handle this element.
	*/    
  static LDAPXMLHandler getXMLHandler(
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
	 * Returns a  string representation of this class.
	 *
	 * @return The string representation of this class.
	 */
  public String toString()
  {
		StringBuffer result = new StringBuffer("LDAPControl: ");
		result.append("((oid="+getID()+"");
		result.append(",critical="+isCritical()+")");
		result.append("(value="+getValue()+"))");
		return result.toString();
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
		
		buff.append("<control type=\"");
		buff.append(getID());
		buff.append("\" criticality=\""+isCritical()+ "\"");

		byte value[] = getValue();
		if (value == null){
			buff.append("/>");
		} else {
			buff.append(">");
			buff.append(ValueXMLhandler.newLine(1));
			buff.append("<controlValue xsi:type=\"xsd:base64Binary\">");
			buff.append(Base64.encode(value));
			buff.append("</controlValue>");
			buff.append(ValueXMLhandler.newLine(0));
			buff.append("</control>");
		}
		
		buff.append(ValueXMLhandler.newLine(0));
		buff.append(ValueXMLhandler.newLine(0));
		
		String tail = "";
		tail += "*************************************************************************\n";
		tail += "****************** End of application data ******************************\n";
		tail += "*************************************************************************\n";
		  
		buff.append(tail);
		buff.append(ValueXMLhandler.newLine(0));       
		out.writeUTF(buff.toString());
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
	  String readData = in.readUTF();
	  String readProperties = readData.substring(readData.indexOf('<'), 
	  			(readData.lastIndexOf('>') + 1));
	  			
	  //Insert  parsing logic here for separating whitespaces in non-text nodes
	  StringBuffer parsedBuff = new StringBuffer();
	  ValueXMLhandler.parseInput(readProperties, parsedBuff);
	  
	  BufferedInputStream istream = 
			  new BufferedInputStream(
					  new ByteArrayInputStream((parsedBuff.toString()).getBytes()));
	  LDAPControl readObject = 
					(LDAPControl)LDAPControl.readDSML(istream);
	  byte[] vals = readObject.getValue();
	  if( vals == null) {
			this.control = new RfcControl( new RfcLDAPOID(readObject.getID()),
									  new ASN1Boolean(readObject.isCritical()));
		} else {
			control = new RfcControl( new RfcLDAPOID(readObject.getID()),
									  new ASN1Boolean(readObject.isCritical()),
									  new ASN1OctetString(vals));
		}

	  //Garbage collect the readObject from readDSML()..	
	  readObject = null;
   }   
     
}
