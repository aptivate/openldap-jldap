/* **************************************************************************
 * $Novell$
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

package com.novell.ldap.util;

/**
 * <P>A RDN encapsulates an ldap relative distinguished name.
 *
 * @see RDN
 */

public class RDN extends Object
{
   private String    m_attributeType;
   private String    m_attributeValue;
   private String    m_rawValue; //the unnormalized value

/***************************************************************************/
    /**
     * Constructs a new RDN with the specified attribute type and attribute
     * value.
     *
     * @param  attrType the attribute type
     * @param  attrValue the attribute value
     * @param  rawValue the attribute value before it is normalized
     */
/***************************************************************************/
   protected RDN(
      String   attrType,
      String   attrValue,
      String   rawValue)
   {
      m_attributeType = attrType;
      m_attributeValue = attrValue;
      m_rawValue = rawValue;
   }

/***************************************************************************/
    /**
     * compare two RDN attribute types lexocographically
     *
     * @return the RDN's normalized attribute type
     */
/***************************************************************************/
   protected int compareAttributeType(
      RDN toRdn)
   {
      return m_attributeType.compareTo(toRdn.getAttributeType());
   }
/***************************************************************************/
    /**
     * Get the RDN's normalized attribute type
     *
     * @return the RDN's normalized attribute type
     */
/***************************************************************************/
   protected String getAttributeType()
   {
      return m_attributeType;
   }

/***************************************************************************/
    /**
     * Get the RDN's normalized attribute value
     *
     * @return the RDN's normalized attribute value
     */
/***************************************************************************/
   protected String getAttributeValue()
   {
      return m_attributeValue;
   }

/***************************************************************************/
    /**
     * Get the RDN's unnormalized attribute value
     *
     * @return the RDN's normalized attribute value
     */
/***************************************************************************/
   protected String getRawValue()
   {
      return m_rawValue;
   }

/***************************************************************************/
    /**
     * Compare this DN to the specified DN to see if they are equal.
     *
     * @param   toDN the DN to compare to
     * @return  <code>true</code> if the DNs are equal; otherwise
     *          <code>false</code>
     */
/***************************************************************************/
   public boolean equals(
      RDN toRDN)
   {
      if ((this.m_attributeType.equalsIgnoreCase(toRDN.m_attributeType)) &&
          (this.m_attributeValue.equalsIgnoreCase(toRDN.m_attributeValue)))
         return true;
      else
         return false;
   }

} //end class RDN
