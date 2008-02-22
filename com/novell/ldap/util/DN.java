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
package com.novell.ldap.util;
import com.novell.ldap.LDAPDN;
import com.novell.ldap.util.RDN;
import java.util.Vector;
import java.util.ArrayList;

/**
 * <P>A DN encapsulates a Distinguished Name (an ldap name with context). A DN
 * does not need to be fully distinguished, or extend to the Root of a
 * directory.  It provides methods to get information about the DN and to
 * manipulate the DN.  </P>
 *
 * <P> The following are examples of valid DN:
 * <ul>
 *     <li>cn=admin,ou=marketing,o=corporation</li>
 *     <li>cn=admin,ou=marketing</li>
 *     <li>2.5.4.3=admin,ou=marketing</li>
 *     <li>oid.2.5.4.3=admin,ou=marketing</li>
 * </ul>
 *
 * <P>Note: Multivalued attributes are all considered to be one
 * component and are represented in one RDN (see RDN)
 * </P>
 *
 * @see RDN
 */

public class DN extends Object
{

    //parser state identifiers.
    private static final int LOOK_FOR_RDN_ATTR_TYPE = 1;
    private static final int ALPHA_ATTR_TYPE        = 2;
    private static final int OID_ATTR_TYPE          = 3;
    private static final int LOOK_FOR_RDN_VALUE     = 4;
    private static final int QUOTED_RDN_VALUE       = 5;
    private static final int HEX_RDN_VALUE          = 6;
    private static final int UNQUOTED_RDN_VALUE     = 7;

    /* State transition table:  Parsing starts in state 1.

    State   COMMA   DIGIT   "Oid."  ALPHA   EQUAL   QUOTE   SHARP   HEX
    --------------------------------------------------------------------
    1       Err     3       3       2       Err     Err     Err     Err
    2       Err     Err     Err     2       4       Err     Err     Err
    3       Err     3       Err     Err     4       Err     Err     Err
    4       Err     7       Err     7       Err     5       6       7
    5       1       5       Err     5       Err     1       Err     7
    6       1       6       Err     Err     Err     Err     Err     6
    7       1       7       Err     7       Err     Err     Err     7

    */


    private ArrayList rdnList = new ArrayList();

    public DN (){
        return;
    }
    /**
     * Constructs a new DN based on the specified string representation of a
     * distinguished name. The syntax of the DN must conform to that specified
     * in RFC 2253.
     *
     * @param      dnString a string representation of the distinguished name
     * @exception  IllegalArgumentException  if the the value of the dnString
     *               parameter does not adhere to the syntax described in
     *               RFC 2253
     */
    public DN(String dnString){
        /* the empty string is a valid DN */
        if (dnString.length() == 0)
            return;

        char     currChar;
        char     nextChar;
        int      currIndex;
        char[]   tokenBuf = new char[dnString.length()];
        int      tokenIndex;
        int      lastIndex;
        int      valueStart;
        int      state;
        int      trailingSpaceCount = 0;
        String   attrType = "";
        String   attrValue = "";
        String   rawValue = "";
        int      hexDigitCount = 0;
        RDN      currRDN = new RDN();

        //indicates whether an OID number has a first digit of ZERO
        boolean firstDigitZero = false;

        tokenIndex = 0;
        currIndex = 0;
        valueStart = 0;
        state = LOOK_FOR_RDN_ATTR_TYPE;
        lastIndex = dnString.length()-1;
        while (currIndex <= lastIndex)
        {
            currChar = dnString.charAt(currIndex);
            switch (state)
            {
            case LOOK_FOR_RDN_ATTR_TYPE: //parsing and RDName
            //skip any spaces
            while (currChar == ' ' && (currIndex < lastIndex))
                currChar = dnString.charAt(++currIndex);
            if (isAlpha(currChar))
            {
                if (dnString.startsWith("oid.", currIndex) ||
                       dnString.startsWith("OID.", currIndex))
                {  //form is "oid.###.##.###... or OID.###.##.###...
                    currIndex += 4; //skip oid. prefix and get to actual oid
                    if (currIndex > lastIndex)
                        throw new IllegalArgumentException(dnString);
                    currChar = dnString.charAt(currIndex);
                    if (isDigit(currChar))
                    {
                        tokenBuf[tokenIndex++] = currChar;
                        state = OID_ATTR_TYPE;
                    }
                    else
                        throw new IllegalArgumentException(dnString);
                }
                else
                {
                    tokenBuf[tokenIndex++] = currChar;
                    state = ALPHA_ATTR_TYPE;
                }
            }
            else if (isDigit(currChar))
            {
                --currIndex;
                state = OID_ATTR_TYPE;
            }
            else if (!Character.isSpaceChar(currChar))
                throw new IllegalArgumentException(dnString);
            break;

            case ALPHA_ATTR_TYPE:
            if (isAlpha(currChar) || isDigit(currChar) || (currChar == '-'))
                tokenBuf[tokenIndex++] = currChar;
            else
            {
                //skip any spaces
                while ((currChar == ' ') && (currIndex < lastIndex))
                    currChar = dnString.charAt(++currIndex);
                if (currChar == '=')
                {
                    attrType = new String(tokenBuf, 0, tokenIndex);
                    tokenIndex = 0;
                    state = LOOK_FOR_RDN_VALUE;
                }
                else
                    throw new IllegalArgumentException(dnString);
            }
            break;

            case OID_ATTR_TYPE:
              //This state starts on the first digit of each number in an OID.
            if (!isDigit(currChar))
                throw new IllegalArgumentException(dnString);
            firstDigitZero = (currChar == '0') ? true: false;
            tokenBuf[tokenIndex++] = currChar;
            currChar = dnString.charAt(++currIndex);

            if ( //Check for a leading zero.
                    (isDigit(currChar) && firstDigitZero) ||
                 //Check for zero by itself (except as the last number)
                    (currChar == '.' && firstDigitZero) )
            {
                throw new IllegalArgumentException(dnString);
            }

            //consume all numbers.
            while (isDigit(currChar) && (currIndex < lastIndex)){
                tokenBuf[tokenIndex++] = currChar;
                currChar = dnString.charAt(++currIndex);
            }
            if (currChar == '.'){
                tokenBuf[tokenIndex++] = currChar;
                //The state remains at OID_ATTR_TYPE
            }
            else
            {
                //skip any spaces
                while (currChar == ' ' && (currIndex < lastIndex))
                    currChar = dnString.charAt(++currIndex);
                if (currChar == '=')
                {
                    attrType = new String(tokenBuf, 0, tokenIndex);
                    tokenIndex = 0;
                    state = LOOK_FOR_RDN_VALUE;
                }
                else
                    throw new IllegalArgumentException(dnString);
            }
            break;

            case LOOK_FOR_RDN_VALUE:
                //skip any spaces
            while (currChar == ' ')
            {
                if (currIndex < lastIndex)
                    currChar = dnString.charAt(++currIndex);
                else
                    throw new IllegalArgumentException(dnString);
            }
            if (currChar == '"')
            {
                state = QUOTED_RDN_VALUE;
                valueStart = currIndex;
            }
            else if (currChar == '#')
            {
                hexDigitCount = 0;
                tokenBuf[tokenIndex++] = currChar;
                valueStart = currIndex;
                state = HEX_RDN_VALUE;
            }
            else
            {
                valueStart = currIndex;
                //check this character again in the UNQUOTED_RDN_VALUE state
                currIndex--;
                state = UNQUOTED_RDN_VALUE;
            }
            break;

            case UNQUOTED_RDN_VALUE:
            if (currChar == '\\')
            {
                if (!(currIndex < lastIndex))
                    throw new IllegalArgumentException(dnString);
                currChar = dnString.charAt(++currIndex);
                if (isHexDigit(currChar))
                {
                    if (!(currIndex < lastIndex))
                        throw new IllegalArgumentException(dnString);
                    nextChar = dnString.charAt(++currIndex);
                    if (isHexDigit(nextChar))
                    {
                        char tmpc = hexToChar(currChar, nextChar);
                        if (this.needsEscape(tmpc)) {
                        	tokenBuf[tokenIndex++] = '\\';
                        	tokenBuf[tokenIndex++] = tmpc;
                        }
                        else {
                        	tokenBuf[tokenIndex++] = tmpc;
                        }
                    	 
                        trailingSpaceCount = 0;
                    }
                    else
                        throw new IllegalArgumentException(dnString);
                }
                else if (needsEscape(currChar) || currChar == '#' ||
                        currChar == '=' || currChar == ' ')
                {
                    tokenBuf[tokenIndex++] = currChar;
                    trailingSpaceCount = 0;
                }
                else
                    throw new IllegalArgumentException(dnString);
            }
            else if (currChar == ' ')
            {
                trailingSpaceCount++;
                tokenBuf[tokenIndex++] = currChar;
            }
            else if ((currChar == ',') ||
                     (currChar == ';') ||
                     (currChar == '+'))
            {
                attrValue =
                    new String(tokenBuf, 0, tokenIndex - trailingSpaceCount);
                rawValue =
                    dnString.substring(valueStart, currIndex-trailingSpaceCount);

                currRDN.add(attrType, attrValue, rawValue);
                if (currChar != '+'){
                    rdnList.add(currRDN);
                    currRDN = new RDN();
                }

                trailingSpaceCount = 0;
                tokenIndex = 0;
                state = LOOK_FOR_RDN_ATTR_TYPE;
            }
            else if (needsEscape( currChar )){
                throw new IllegalArgumentException(dnString);
            }
            else
            {
                trailingSpaceCount = 0;
                tokenBuf[tokenIndex++] = currChar;
            }
            break; //end UNQUOTED RDN VALUE

            case QUOTED_RDN_VALUE:
            if (currChar == '"')
            {
                rawValue = dnString.substring(valueStart, currIndex+1);
                if (currIndex < lastIndex)
                    currChar = dnString.charAt(++currIndex);
                //skip any spaces
                while ((currChar == ' ') && (currIndex < lastIndex))
                    currChar = dnString.charAt(++currIndex);
                if ((currChar == ',') ||
                    (currChar == ';') ||
                    (currChar == '+') ||
                    (currIndex == lastIndex))
                {
                    attrValue = new String(tokenBuf, 0, tokenIndex);

                    currRDN.add(attrType, attrValue, rawValue);
                    if (currChar != '+'){
                        rdnList.add(currRDN);
                        currRDN = new RDN();
                    }
                    trailingSpaceCount = 0;
                    tokenIndex = 0;
                    state = LOOK_FOR_RDN_ATTR_TYPE;
                }
                else
                    throw new IllegalArgumentException(dnString);
            }
            else if (currChar == '\\')
            {
                currChar = dnString.charAt(++currIndex);
                if (isHexDigit(currChar))
                {
                    nextChar = dnString.charAt(++currIndex);
                    if (isHexDigit(nextChar))
                    {
                    	char tmpc = hexToChar(currChar, nextChar);
                    	if (this.needsEscape(tmpc)) {
                    		tokenBuf[tokenIndex++] = '\\';
                    	}	
                    	
                    	tokenBuf[tokenIndex++] = tmpc;
                    	
                        trailingSpaceCount = 0;
                    }
                    else
                        throw new IllegalArgumentException(dnString);
                }
                else if (needsEscape(currChar)|| currChar == '#' ||
                        currChar == '=' || currChar == ' ')
                {
                    tokenBuf[tokenIndex++] = currChar;
                    trailingSpaceCount = 0;
                }
                else
                    throw new IllegalArgumentException(dnString);
            }
            else
                tokenBuf[tokenIndex++] = currChar;
            break; //end QUOTED RDN VALUE

            case HEX_RDN_VALUE:
            if ((!isHexDigit(currChar)) || (currIndex > lastIndex))
            {
                //check for odd number of hex digits
                if ((hexDigitCount%2) != 0 || hexDigitCount == 0)
                    throw new IllegalArgumentException(dnString);
                else
                {
                    rawValue = dnString.substring(valueStart, currIndex);
                    //skip any spaces
                    while ((currChar == ' ') && (currIndex < lastIndex))
                        currChar = dnString.charAt(++currIndex);
                    if ((currChar == ',') ||
                        (currChar == ';') ||
                        (currChar == '+') ||
                        (currIndex == lastIndex))
                    {
                        attrValue = new String(tokenBuf, 0, tokenIndex);

                        //added by cameron
                        currRDN.add(attrType, attrValue, rawValue);
                        if (currChar != '+'){
                            rdnList.add(currRDN);
                            currRDN = new RDN();
                        }
                        tokenIndex = 0;
                        state = LOOK_FOR_RDN_ATTR_TYPE;
                    }
                    else
                    {
                        throw new IllegalArgumentException(dnString);
                    }
                }
            }
            else
            {
                tokenBuf[tokenIndex++] = currChar;
                hexDigitCount++;
            }
            break; //end HEX RDN VALUE
            }//end switch
            currIndex++;
        }//end while

        //check ending state
        if (state == UNQUOTED_RDN_VALUE ||
            (state == HEX_RDN_VALUE //if this is hex is must have an even count
                && (hexDigitCount%2) == 0)
                && hexDigitCount != 0)
        {
            attrValue =
                new String(tokenBuf, 0, tokenIndex - trailingSpaceCount);
            rawValue =
                dnString.substring(valueStart, currIndex - trailingSpaceCount);
            currRDN.add(attrType,attrValue,rawValue);
            rdnList.add(currRDN);
        }
        else if (state == LOOK_FOR_RDN_VALUE){
            //empty value is valid
            attrValue = "";
            rawValue = dnString.substring(valueStart);
            currRDN.add(attrType, attrValue, rawValue);
            rdnList.add(currRDN);
        }
        else
        {
            throw new IllegalArgumentException(dnString);
        }
    } //end DN constructor (string dn)


    /**
     * Checks a character to see if it is an ascii alphabetic character in
     * ranges 65-90 or 97-122.
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character is an ascii alphabetic
     *            character
     */
    private boolean isAlpha(
        char ch)
    {
        if (((ch < 91) && (ch > 64)) || //ASCII a-z
            ((ch < 123) && (ch > 96)))  //ASCII A-Z
            return true;
        else
            return false;
    }


    /**
     * Checks a character to see if it is an ascii digit (0-9) character in
     * the ascii value range 48-57.
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character is an ascii alphabetic
     *            character
     */
    private boolean isDigit(
        char ch)
    {
        if ((ch < 58) && (ch > 47)) //ASCII 0-9
            return true;
        else
            return false;
    }

    /**
     * Checks a character to see if it is valid hex digit 0-9, a-f, or
     * A-F (ASCII value ranges 48-47, 65-70, 97-102).
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character is a valid hex digit
     */

    private static boolean isHexDigit(char ch){
        if (((ch < 58) && (ch > 47)) || //ASCII 0-9
            ((ch < 71) && (ch > 64)) || //ASCII a-f
            ((ch < 103) && (ch > 96)))  //ASCII A-F
            return true;
        else
            return false;
    }

    /**
     * Checks a character to see if it must always be escaped in the
     * string representation of a DN.  We must tests for space, sharp, and
     * equals individually.
     *
     * @param   ch the character to be tested.
     * @return  <code>true</code> if the character needs to be escaped in at
     *            least some instances.
     */
    private boolean needsEscape( char ch) {
        if (
            (ch == ',') ||
            (ch == '+') ||
            (ch == '\"') ||
            (ch == ';') ||
            (ch == '<') ||
            (ch == '>') ||
            (ch == '\\'))
            return true;
        else
            return false;
    }

    /**
     * Converts two valid hex digit characters that form the string
     * representation of an ascii character value to the actual ascii
     * character.
     *
     * @param   hex1 the hex digit for the high order byte.
     * @param   hex0 the hex digit for the low order byte.
     * @return  the character whose value is represented by the parameters.
     */

    private static char hexToChar(char hex1, char hex0)
        throws IllegalArgumentException {
        int result;

        if ((hex1 < 58) && (hex1 > 47)) //ASCII 0-9
            result = (hex1-48) * 16;
        else if ((hex1 < 71) && (hex1 > 64)) //ASCII a-f
            result = (hex1-55) * 16;
        else if ((hex1 < 103) && (hex1 > 96))  //ASCII A-F
            result = (hex1-87) * 16;
        else
            throw new IllegalArgumentException("Not hex digit");

        if ((hex0 < 58) && (hex0 > 47)) //ASCII 0-9
            result += (hex0-48);
        else if ((hex0 < 71) && (hex0 > 64)) //ASCII a-f
            result += (hex0-55);
        else if ((hex0 < 103) && (hex0 > 96))  //ASCII A-F
            result += (hex0-87);
        else
            throw new IllegalArgumentException("Not hex digit");

        return (char)result;
    }

    /**
     * Creates and returns a string that represents this DN.  The string
     * follows RFC 2253, which describes String representation of DN's and
     * RDN's
     *
     * @return A DN string.
     */
    public String toString() {
        int length=rdnList.size();
        String dn = "";
        if (length < 1)
            return null;
        dn = LDAPDN.escapeRDN(rdnList.get(0).toString());
        for (int i=1; i<length; i++)
            dn += "," + LDAPDN.escapeRDN(rdnList.get(i).toString());
        return dn;
    }


    /**
     * Compares this DN to the specified DN to determine if they are equal.
     *
     * @param   toDN the DN to compare to
     * @return  <code>true</code> if the DNs are equal; otherwise
     *          <code>false</code>
     */
    public boolean equals( DN toDN ){
        int length = toDN.rdnList.size();

        if( this.rdnList.size() != length)
            return false;

        for(int i=0; i<length; i++){
            if (!((RDN)rdnList.get(i)).equals( (RDN)toDN.rdnList.get(i) ))
                return false;
        }
        return true;
    }

    /**
     * return a string array of the individual RDNs contained in the DN
     *
     * @param noTypes   If true, returns only the values of the
     *                  components, and not the names, e.g. "Babs
     *                  Jensen", "Accounting", "Acme", "us" - instead of
     *                  "cn=Babs Jensen", "ou=Accounting", "o=Acme", and
     *                  "c=us".
     * @return  <code>String[]</code> containing the rdns in the DN with
     *                 the leftmost rdn in the first element of the array
     *
     */
    public String[] explodeDN(boolean  noTypes) {
        int length = rdnList.size();
        String[] rdns = new String[length];
        for(int i=0; i<length; i++)
            rdns[i]=((RDN)rdnList.get(i)).toString(noTypes);
        return rdns;
    }

    /**
     * Retrieves the count of RDNs, or individule names, in the Distinguished name
     * @return the count of RDN
     */
    public int countRDNs(){
        return rdnList.size();
    }

    /**
     * Retrieves a list of RDN Objects, or individual names of the DN
     * @return list of RDNs
     */
    public Vector getRDNs(){
        int size = rdnList.size();
        Vector v = new Vector( size);
        for( int i = 0; i < size; i++) {
            v.addElement( rdnList.get(i));
        }
        return v;
    }

    /** Determines if this DN is <I>contained</I> by the DN passed in.  For
     *  example:  "cn=admin, ou=marketing, o=corporation" is contained by
     *  "o=corporation", "ou=marketing, o=corporation", and "ou=marketing"
     *  but <B>not</B> by "cn=admin" or "cn=admin,ou=marketing,o=corporation"
     *  Note: For users of Netscape's SDK this method is comparable to contains
     *
     * @param containerDN of a container
     * @return true if containerDN contains this DN
     */
    public boolean isDescendantOf(DN containerDN){
        int i = containerDN.rdnList.size() -1;  //index to an RDN of the ContainerDN
        int j = this.rdnList.size() -1;              //index to an RDN of the ContainedDN
        //Search from the end of the DN for an RDN that matches the end RDN of
        //containerDN.
        if(i > j) // the length of the container DN should always be less than the contained one 
        	return false ;
        while ( !((RDN)this.rdnList.get(j--)).equals((RDN)containerDN.rdnList.get(i))){
            if (j <= 0)
                return false;
                //if the end RDN of containerDN does not have any equal
                //RDN in rdnList, then containerDN does not contain this DN
        }
        i--;  //avoid a redundant compare
        //step backwards to verify that all RDNs in containerDN exist in this DN
        for (/* i, j */ ; i>=0 && j >=0; i--, j--){
            if (!((RDN)this.rdnList.get(j)).equals(
                (RDN)containerDN.rdnList.get(i)))
                return false;
        }
        if (j == 0 && i == 0) //the DNs are identical and thus not contained
            return false;

        return true;
    }

    /**
     * Returns the Parent of this DN
     * @return Parent DN
     */
    public DN getParent(){
       DN parent = new DN();
       parent.rdnList = (ArrayList)this.rdnList.clone();
       if (parent.rdnList.size() >= 1)
           parent.rdnList.remove(0);  //remove first object
       return parent;
    }

    /**
     * Adds the RDN to the beginning of the current DN.
     * @param rdn an RDN to be added
     */
    public void addRDN(RDN rdn){
       rdnList.add(0, rdn);
    }

    /**
     * Adds the RDN to the beginning of the current DN.
     * @param rdn an RDN to be added
     */
    public void addRDNToFront(RDN rdn){
       rdnList.add(0, rdn);
    }

    /**
     * Adds the RDN to the end of the current DN
     * @param rdn an RDN to be added
     */
    public void addRDNToBack(RDN rdn){
        rdnList.add(rdn);
    }
} //end class DN
