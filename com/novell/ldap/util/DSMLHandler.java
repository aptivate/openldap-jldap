/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2002 - 2003 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 */
package com.novell.ldap.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.novell.ldap.LDAPAddRequest;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPCompareRequest;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPDeleteRequest;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPExtendedRequest;
import com.novell.ldap.LDAPExtendedResponse;
import com.novell.ldap.LDAPLocalException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPModifyDNRequest;
import com.novell.ldap.LDAPModifyRequest;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchRequest;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.LDAPSearchResultReference;
import com.novell.ldap.rfc2251.RfcFilter;

/* package */
class DSMLHandler
  extends DefaultHandler
  implements ContentHandler, ErrorHandler {

  /*Use for Reusing attr tag between AddRequest and Search Response.*/
  private boolean isAddRequest;

  /** Holds parsed LDAPMessages ready for use */
  /* package */
  private ArrayList queue = new ArrayList();

  /* variables used for message information */
  private LDAPMessage message = null;
  private LDAPEntry entry = null;
  private LDAPAttributeSet attrSet = null;
  /* Multiple <value> contents will be stored in this list:*/
  private ArrayList attributeValues = new ArrayList();
  /* attributeNames is used for compare and search attribute names: */
  private ArrayList attributeNames = new ArrayList();
  /* modlist is used for modifications in the ModRequest operation */
  private ArrayList modlist = new ArrayList();
  private LDAPSearchConstraints searchCons = null;
  private String attrName = null, dn, newRDN, newSuperior;

  /* extended request information */
  private String requestName;
  private byte[] requestValue;
  /* holds the content in a value tag */
  private StringBuffer value;
  private boolean typesOnly, deleteOldRDN, isBase64;
  private int scope, operation;

  /* filter variabls: */
  private RfcFilter filter;
  private boolean isDNMatching;
  private String matchingRule;
  private ArrayList controls = new ArrayList();
  /* Referral List */
  private ArrayList referrallist = new ArrayList();

  /* Response Variable */
  private int responsetype = 0;
  // Used to store Response type for creation of response.
  private int responsecode = 0;
  private String responseDesc = null;
  private String errorMessage = null;

  /* The following values are valid states for the parser: the tags they
      represent are in comments to the right */
  private static final int START = 0;
  private static final int BATCH_REQUEST = 1; //<batchRequest>

  /* The following are possible states from the batchRequest state */
  private static final int AUTH_REQUEST = 2; //<authRequest>
  private static final int MODIFY_REQUEST = 3; //<modifyRequest>
  private static final int SEARCH_REQUEST = 4; //<searchRequest>
  private static final int ADD_REQUEST = 5; //<addRequest>
  private static final int DELETE_REQUEST = 6; //<delRequest>
  private static final int MODIFY_DN_REQUEST = 7; //<modDNRequest>
  private static final int COMPARE_REQUEST = 8; //<compareRequest>
  private static final int EXTENDED_REQUEST = 9; //<extendedRequest>

  /* The following are possible states from filter, compare and search */
  private static final int ASSERTION = 10; //<assertion>
  private static final int VALUE = 11; //<value>
  private static final int ATTRIBUTES = 12; //<attributes>
  private static final int ATTRIBUTE = 13; //<attribute>
  private static final int FILTER = 14; //<filter>
  private static final int AND = 15; //<and>
  private static final int OR = 16; //<or>
  private static final int NOT = 17; //<not>
  private static final int EQUALITY_MATCH = 18; //<equalityMatch>
  private static final int SUBSTRINGS = 19; //<substrings>
  private static final int GREATER_OR_EQUAL = 20; //<greaterOrEqual>
  private static final int LESS_OR_EQUAL = 21; //<lessOrEqual>
  private static final int PRESENT = 22; //<present>
  private static final int APPROXIMATE_MATCH = 23; //<approxMatch>
  private static final int EXTENSIBLE_MATCH = 24; //<extensibleMatch>
  private static final int INITIAL = 25; //<initial>
  private static final int ANY = 26; //<any>
  private static final int FINAL = 27; //<final>

  /* miscelaneous tags :*/
  private static final int ADD_ATTRIBUTE = 28; //<attr>
  private static final int MODIFICATION = 29; //<modification>
  private static final int X_NAME = 30; //<requestName>
  private static final int X_VALUE = 31; //<requestValue>

  private static final int CONTROL = 32; //<control>

  /* batch Response set of items */
  private static final int BATCH_RESPONSE = 34; //<batchResponse>

  //For Add Response
  private static final int ADD_RESPONSE = 35; //<addResponse>
  private static final int LDAP_RESPONSE = 36; //Generic Response Type.
  private static final int RESULT_CODE = 37; //<resultCode>
  private static final int ERROR_MESSAGE = 38; //<errorMessage>
  private static final int ERROR_RESPONSE = 53; //<errorResponse>
  private static final int MESSAGE = 54; //<errorResponse>
  private static final int REFERRAL_LIST = 39; //<referral>

  //For Search Response

  private static final int SEARCH_RESPONSE = 40; //<searchResponse>
  private static final int SEARCH_RESULT_ENTRY = 41; //<searchResultEntry>
  private static final int SEARCH_RESULT_REFERENCE = 42;
  //<searchResultReference>
  private static final int SEARCH_RESULT_REFERENCE_REF = 43;
  private static final int SEARCH_RESULT_DONE = 44; //<searchResultDone>

  //Extended Response

  private static final int EXTENDED_RESPONSE = 45; //<extendedResponse>
  private static final int EXTENDED_RESPONSE_NAME = 46; //<responseName>
  private static final int EXTENDED_RESPONSE_RESPONSE = 47; //<response>

  //Other Responses
  private static final int AUTH_RESPONSE = 48; //<authResponse>
  private static final int MODIFY_RESPONSE = 49; //<modifyResponse>
  private static final int DEL_RESPONSE = 50; //<delResponse>
  private static final int MODIFYDN_RESPONSE = 51; //<modDNResponse>
  private static final int COMPARE_RESPONSE = 52; //<compareResponse>

  /* The folling are possible states from the
      .... NOT Implemented ... SearchResponse ,Extended Response
      and ErrorResponse
   ...*/

  /** state contains the internal parsing state **/
  private int state = START;
  private static final java.util.HashMap requestTags;
  /* valueState indicates the state before an <value> is found */
  private int valueState;
  private boolean critical;
  private String oid;
  private String requestID;
  //Request ID for the entire batch
  //    also used as Response ID for the entire batch
  private String batchRequestID;
  //Indicates that the messages can be processed in parallel
  private boolean isParallel;
  //Indicates that the results can be returned unordered
  private boolean isUnordered;
  private boolean isResumeOnError;
  //Used to store previous state for controls
  private int prevstate = 0;
  //Used for Extended response, Since Extended Response
  //add ldap response state have a common code,
  //this code helps to separate the two.
  private boolean isextendedstate = false;
  //Search Ids
  private String searchResponseid;

private String errorType;

private ArrayList errors = new ArrayList();

  static { //Initialize requestTags
    requestTags = new java.util.HashMap(35, (float) 0.25);
    //Load factor of 0.25 optimizes for speed rather than size.

    requestTags.put("batchRequest", new Integer(BATCH_REQUEST));
    requestTags.put("authRequest", new Integer(AUTH_REQUEST));
    requestTags.put("modifyRequest", new Integer(MODIFY_REQUEST));
    requestTags.put("searchRequest", new Integer(SEARCH_REQUEST));
    requestTags.put("addRequest", new Integer(ADD_REQUEST));
    requestTags.put("delRequest", new Integer(DELETE_REQUEST));
    requestTags.put("modDNRequest", new Integer(MODIFY_DN_REQUEST));
    requestTags.put("compareRequest", new Integer(COMPARE_REQUEST));
    requestTags.put("extendedRequest", new Integer(EXTENDED_REQUEST));
    requestTags.put("batchResponse", new Integer(BATCH_RESPONSE));
    requestTags.put("assertion", new Integer(ASSERTION));
    requestTags.put("value", new Integer(VALUE));
    requestTags.put("attributes", new Integer(ATTRIBUTES));
    requestTags.put("attribute", new Integer(ATTRIBUTE));
    requestTags.put("filter", new Integer(FILTER));
    requestTags.put("and", new Integer(AND));
    requestTags.put("or", new Integer(OR));
    requestTags.put("not", new Integer(NOT));
    requestTags.put("equalityMatch", new Integer(EQUALITY_MATCH));
    requestTags.put("substrings", new Integer(SUBSTRINGS));
    requestTags.put("greaterOrEqual", new Integer(GREATER_OR_EQUAL));
    requestTags.put("lessOrEqual", new Integer(LESS_OR_EQUAL));
    requestTags.put("present", new Integer(PRESENT));
    requestTags.put("approxMatch", new Integer(APPROXIMATE_MATCH));
    requestTags.put("extensibleMatch", new Integer(EXTENSIBLE_MATCH));

    requestTags.put("attr", new Integer(ADD_ATTRIBUTE));
    requestTags.put("modification", new Integer(MODIFICATION));
    requestTags.put("requestName", new Integer(X_NAME));
    requestTags.put("requestValue", new Integer(X_VALUE));
    requestTags.put("initial", new Integer(INITIAL));
    requestTags.put("any", new Integer(ANY));
    requestTags.put("final", new Integer(FINAL));

    requestTags.put("control", new Integer(CONTROL));
    requestTags.put("controlValue", new Integer(VALUE));

    //Add Response  Objects.
    requestTags.put("addResponse", new Integer(ADD_RESPONSE));
    requestTags.put("resultCode", new Integer(RESULT_CODE));
    requestTags.put("errorMessage", new Integer(ERROR_MESSAGE));
    requestTags.put("message", new Integer(MESSAGE));
    requestTags.put("errorResponse", new Integer(ERROR_RESPONSE));
    requestTags.put("referral", new Integer(REFERRAL_LIST));

    //Search Response Objects
    requestTags.put("searchResponse", new Integer(SEARCH_RESPONSE));
    requestTags.put("searchResultEntry", new Integer(SEARCH_RESULT_ENTRY));
    requestTags.put(
      "searchResultReference",
      new Integer(SEARCH_RESULT_REFERENCE));
    requestTags.put("ref", new Integer(SEARCH_RESULT_REFERENCE_REF));

    requestTags.put("searchResultDone", new Integer(SEARCH_RESULT_DONE));

    //Other Responses
    requestTags.put("authResponse", new Integer(AUTH_RESPONSE));
    requestTags.put("modifyResponse", new Integer(MODIFY_RESPONSE));
    requestTags.put("delResponse", new Integer(DEL_RESPONSE));
    requestTags.put("modDNResponse", new Integer(MODIFYDN_RESPONSE));
    requestTags.put("compareResponse", new Integer(COMPARE_RESPONSE));

    //extended Response
    requestTags.put("extendedResponse", new Integer(EXTENDED_RESPONSE));
    requestTags.put("responseName", new Integer(EXTENDED_RESPONSE_NAME));
    requestTags.put("response", new Integer(EXTENDED_RESPONSE_RESPONSE));

  }

  // SAX calls this method when it encounters an element
  public void startElement(
    String strNamespaceURI,
    String strSName,
    String strQName,
    Attributes attrs)
    throws SAXException {
    Integer elementTag = (Integer) requestTags.get(strSName);
    if (elementTag == null) {
      if (state != START) {
        //Ignore tags outside of DSML tags
        throw new SAXNotRecognizedException(
          "Element name, \"" + strQName + "\" not recognized");
      } else {
        return;
      }
    }
    int tag = elementTag.intValue();
    if (tag == CONTROL) {
      handleControl(attrs);
      prevstate = state;
      state = CONTROL;

    } else {

      switch (state) {
        // The following values are valid states for the parser:
        case START :
          // we can now read a Batch_Request tag or Batch_Response tag
          if (tag == BATCH_REQUEST || tag == BATCH_RESPONSE) {
            state = tag;

            parseTagAttributes(tag, attrs);
          } else {
            throw new SAXException("Invalid beginning tag :" + strQName);
          }
          break;
        case BATCH_REQUEST :
          state = tag;
          if (tag == ADD_REQUEST) {
            attrSet = new LDAPAttributeSet();
          }
          if (tag == MODIFY_REQUEST) {
            modlist.clear();
          }
          parseTagAttributes(tag, attrs);
          break;
        case BATCH_RESPONSE :
          if (tag == ADD_RESPONSE) {
            //Process AddResponse.
            responsetype = LDAPMessage.ADD_RESPONSE;
            state = LDAP_RESPONSE;

            //Handling as a generic LdapResponse.
            parseTagAttributes(LDAP_RESPONSE, attrs);
          } else if (tag == SEARCH_RESPONSE) {
            responsetype = LDAPMessage.SEARCH_RESPONSE;
            parseTagAttributes(tag, attrs);
            state = SEARCH_RESPONSE;

            searchResponseid = requestID;
          } else if (tag == EXTENDED_RESPONSE) {
            responsetype = LDAPMessage.EXTENDED_RESPONSE;
            parseTagAttributes(LDAP_RESPONSE, attrs);
            state = EXTENDED_RESPONSE;
          } else if (tag == MODIFY_RESPONSE) {
            //Process Modify Response.
            responsetype = LDAPMessage.MODIFY_RESPONSE;
            state = LDAP_RESPONSE;
            //Handling as a generic LdapResponse.
            parseTagAttributes(LDAP_RESPONSE, attrs);
          } else if (tag == DEL_RESPONSE) {
            //Process Delete esponse.
            responsetype = LDAPMessage.DEL_RESPONSE;
            state = LDAP_RESPONSE;
            //Handling as a generic LdapResponse.
            parseTagAttributes(LDAP_RESPONSE, attrs);
          } else if (tag == MODIFYDN_RESPONSE) {
            //Process Modify DN Response.
            responsetype = LDAPMessage.MODIFY_RDN_RESPONSE;
            state = LDAP_RESPONSE;
            //Handling as a generic LdapResponse.
            parseTagAttributes(LDAP_RESPONSE, attrs);
          } else if (tag == COMPARE_RESPONSE) {
            //Process Compare Response.
            responsetype = LDAPMessage.COMPARE_RESPONSE;
            state = LDAP_RESPONSE;
            //Handling as a generic LdapResponse.
            parseTagAttributes(LDAP_RESPONSE, attrs);
          } else if (tag == ERROR_RESPONSE) {
            
          	//Process Compare Response.
            responsetype = LDAPMessage.ABANDON_REQUEST;
            state = ERROR_RESPONSE;
            //Handling as a generic LdapResponse.
            parseTagAttributes(ERROR_RESPONSE, attrs);
          }  
          
          else {
            throw new SAXException("invalid tag: " + strSName);

          }
          referrallist.clear();
          break;
        case SEARCH_RESPONSE :
          if (tag == SEARCH_RESULT_DONE) {
            //                        Generic Ldap Result for a Ldap Response
            state = LDAP_RESPONSE;
            responsetype = LDAPMessage.SEARCH_RESULT;
            //Handling as a generic LdapResponse.
            parseTagAttributes(LDAP_RESPONSE, attrs);
          } else if (tag == SEARCH_RESULT_REFERENCE) {
            //referals objects to track.
            referrallist.clear();
            //Use the REFERRAL LIST STATE
            state = SEARCH_RESULT_REFERENCE;
          } else if (tag == SEARCH_RESULT_ENTRY) {
            state = SEARCH_RESULT_ENTRY;
            //Parse the request Batch ID
            parseTagAttributes(SEARCH_RESULT_ENTRY, attrs);
            attrSet = new LDAPAttributeSet();
          }
          break;

        case SEARCH_RESULT_ENTRY :
          /* Attribute is same as the add request */
          if (tag == ADD_ATTRIBUTE) {
            //Tag
            state = tag;
            attributeValues.clear();
            attrName = attrs.getValue("name");
            isAddRequest = false;
          }
          break;
        case SEARCH_RESULT_REFERENCE :
          if (tag == SEARCH_RESULT_REFERENCE_REF) {
            //                        nothing to do, just cleanup value.
            if (value == null) {

              value = new StringBuffer();
            } else {

              //cleanup value.
              value.delete(0, value.length());

            }
            state = SEARCH_RESULT_REFERENCE_REF;
          }
          break;
        case EXTENDED_RESPONSE :
          if (value == null) {
            value = new StringBuffer();
          } else {

            //cleanup value.
            value.delete(0, value.length());
          }
          if (tag == EXTENDED_RESPONSE_NAME) {
            state = EXTENDED_RESPONSE_NAME;
          }
          if (tag == EXTENDED_RESPONSE_RESPONSE) {
            state = EXTENDED_RESPONSE_RESPONSE;
            String temp = attrs.getValue("xsi:type");
            if (temp != null && temp.equals("xsd:base64Binary")) {
              isBase64 = true;
            } else {
              isBase64 = false;
            }
          }

          //no break, extended response , extendeds generic response.
          isextendedstate = true;
          
        case LDAP_RESPONSE :
          //Process Generic Ldap Response.
          if (tag == RESULT_CODE) {
            //Mandatory
            if (attrs.getValue("code") == null) {

              throw new SAXException("Response Code not provided");
            }
            responsecode = (new Integer(attrs.getValue("code"))).intValue();
            responseDesc = attrs.getValue("descr");

          } else if (tag == ERROR_MESSAGE || tag == MESSAGE) {
            //nothing to do, just cleanup value.
            if (value == null) {
              value = new StringBuffer();
            } else {

              //cleanup value.
              value.delete(0, value.length());
            }
            state = tag;
          } else if (tag == REFERRAL_LIST) {
            //                        nothing to do, just cleanup value.
            if (value == null) {
              value = new StringBuffer();
            } else {

              //cleanup value.
              value.delete(0, value.length());
            }
            state = tag;
          }
          break;
        case ERROR_RESPONSE :
        	if (value == null) {
                value = new StringBuffer();
              } else {

                //cleanup value.
                value.delete(0, value.length());
              }
              state = tag;
              break;
        case SEARCH_REQUEST :
          if ((isParallel == true && isUnordered == true)
            && requestID == null) {
            throw new SAXException("requestID not provided");
          }
          if (tag == ATTRIBUTES) {
            this.attributeNames.clear();
            this.attributeValues.clear();
            state = tag;
          } else if (tag == FILTER) {
            state = FILTER;
            filter = new RfcFilter();
          } else {
            throw new SAXException("invalid searchRequest tag: " + strSName);
          }
          break;
        case AUTH_REQUEST :
        case MODIFY_REQUEST :
          if ((isParallel == true && isUnordered == true)
            && requestID == null) {
            throw new SAXException("requestID not provided");
          }

          if (tag == MODIFICATION) {
            state = tag;
            attributeValues.clear();
            String tempID = requestID;
            parseTagAttributes(tag, attrs);
            requestID = tempID;
            tempID = null;
          } else {
            throw new SAXException("invalid modifyRequest tag: " + strSName);
          }
          break;
        case ADD_REQUEST :
          if ((isParallel == true && isUnordered == true)
            && requestID == null) {
            throw new SAXException("requestID not provided");
          }

          if (tag == ADD_ATTRIBUTE) {
            state = tag;
            attributeValues.clear();
            attrName = attrs.getValue("name");
            isAddRequest = true;
          } else {
            //I may not have to check for this if decide to validate
            throw new SAXException("invalid addRequest tag: " + strSName);
          }
          break;
        case DELETE_REQUEST :
          if ((isParallel == true && isUnordered == true)
            && requestID == null) {
            throw new SAXException("requestID not provided");
          }

          break;
        case MODIFY_DN_REQUEST :
          if ((isParallel == true && isUnordered == true)
            && requestID == null) {
            throw new SAXException("requestID not provided");
          }

          break;
        case COMPARE_REQUEST :
          if ((isParallel == true && isUnordered == true)
            && requestID == null) {
            throw new SAXException("requestID not provided");
          }
          attributeValues.clear();
          if (tag == ASSERTION) {
            attrName = attrs.getValue("name");
            state = tag;
          } else {
            throw new SAXException("invalid compareRequest tag: " + strSName);
          }
          break;
          //Tags with multiple names but no value tags embedded
        case ATTRIBUTES :
          //list of attribute names
          if (tag == ATTRIBUTE) {
            //add a search attributes name
            attributeNames.add(attrs.getValue("name"));
            state = tag;
          } else {
            throw new SAXException("invalid attributes tag: " + strSName);
          }
          break;
          //Substring tag can contain initial, any, or final tags
        case SUBSTRINGS :
          if ((tag == INITIAL) || (tag == ANY) || (tag == FINAL)) {
            state = tag;
            value = new StringBuffer();
          } else {
            throw new SAXException("invalid substrings tag: " + strSName);
          }
          break;
        case FILTER :
        case AND :
        case OR :
        case NOT :
          handleFilter(tag, attrs, strSName);
          state = tag;
          break;
        case EXTENDED_REQUEST :
          attributeValues.clear();
          if (tag == X_NAME || tag == X_VALUE) {
            state = tag;
            value = new StringBuffer();
          }
          break;

          //Tags with <value> tags expected:
        case CONTROL :
        case MODIFICATION :
        case ADD_ATTRIBUTE :
        case ASSERTION :
          //The following states are in a filter tag and should contain values
        case EQUALITY_MATCH :
        case GREATER_OR_EQUAL :
        case LESS_OR_EQUAL :
        case PRESENT :
        case APPROXIMATE_MATCH :
        case EXTENSIBLE_MATCH :
          if (tag == VALUE) {
            /* remember our current state so we can return to it after
                the value is parsed */
            valueState = state;
            state = tag;
            value = new StringBuffer();
            String temp = attrs.getValue("xsi:type");
            if (temp != null && temp.equals("xsd:base64Binary")) {
              isBase64 = true;
            } else {
              isBase64 = false;
            }
          } else {
            throw new SAXException("invalid tag: " + strSName);
          }
          break;
        default :
          throw new SAXException("invalid tag: " + strSName);
      }
    }
    return;
  }

  private void handleControl(Attributes attrs) throws SAXException {
    if (controls == null) {
      controls = new ArrayList();
    }

    this.oid = attrs.getValue("type");
    if (oid == null) {
      //Oid is mandatory.
      throw new SAXException("type is mandatory for a Control");
    }
    this.critical = "true".equalsIgnoreCase(attrs.getValue("criticality"));
    return;
  }

  private void handleFilter(int tag, Attributes attrs, String strSName)
    throws SAXException {
    try {
      switch (tag) {
        case AND :
          filter.startNestedFilter(RfcFilter.AND);
          break;
        case OR :
          filter.startNestedFilter(RfcFilter.OR);
          break;
        case NOT :
          filter.startNestedFilter(RfcFilter.NOT);
          break;
        case SUBSTRINGS :
          this.attrName = attrs.getValue("name");
          if (this.attrName == null) {
            throw new SAXException(
              "The mandatory attribute 'name' "
                + "is missing from tag <"
                + strSName
                + ">");
          }
          filter.startSubstrings(attrName);
          break;
          //don't break, we need the attribute name.
        case EQUALITY_MATCH :
        case GREATER_OR_EQUAL :
        case LESS_OR_EQUAL :
        case PRESENT :
        case APPROXIMATE_MATCH :
          this.attrName = attrs.getValue("name");
          if (this.attrName == null) {
            throw new SAXException(
              "The mandatory attribute 'name' "
                + "is missing from tag <"
                + strSName
                + ">");
          }
          break;
        case EXTENSIBLE_MATCH :
          //name is not mandatory for extensible match
          this.attrName = attrs.getValue("name");
          String dnAttributes = attrs.getValue("dnAttributes");
          if (dnAttributes != null && dnAttributes.equalsIgnoreCase("true")) {
            this.isDNMatching = true;
          } else { //false is default
            this.isDNMatching = false;
          }
          this.matchingRule = attrs.getValue("matchingRule");
          break;
        default :
          throw new SAXException("invalid tag in filter: " + strSName);
      }
    } catch (LDAPLocalException e) {
      throw new SAXException(
        "An error occured constructing a filter:" + e.toString());
    }
    return;
  }

  private void parseTagAttributes(int tag, Attributes attrs)
    throws SAXException {

    switch (tag) {
      case ERROR_RESPONSE:
      	this.errorType = attrs.getValue("type");
      	break;
      	
      case BATCH_RESPONSE :
        batchRequestID = attrs.getValue("requestID");
        break;
      case LDAP_RESPONSE :
        //responseId is processed in a common block.

        //process dn
        dn = attrs.getValue("matchedDN");
        break;
      case SEARCH_RESPONSE :
        //no handling , only read the response ID.
        break;
      case SEARCH_RESULT_ENTRY :
        dn = attrs.getValue("dn");
        if (dn == null) {
          throw new SAXException("DN is Mandatory in SearchResultEntry");
        }
      case BATCH_REQUEST :
        {
          batchRequestID = attrs.getValue("requestID");

          String temp = attrs.getValue("processing");
          //default is sequential: isParallel=false
          isParallel = (temp != null && temp.equals("parallel"));
          temp = attrs.getValue("responseOrder");
          //default ordering is sequential: isUnordered=false
          isUnordered = (temp != null && temp.equals("unordered"));
          temp = attrs.getValue("onError");
          //default action on error is exit: isResumeOnError=false
          isResumeOnError = (temp != null && temp.equals("resume"));
        }
      case SEARCH_REQUEST :
        {
          String temp;
          int timeLimit, deref, sizeLimit;

          //Get dereferencing Aliases
          temp = attrs.getValue("derefAliases");
          if (temp == null) {
            deref = LDAPSearchConstraints.DEREF_ALWAYS;
          } else if (temp.equals("neverDerefAliases")) {
            deref = LDAPSearchConstraints.DEREF_NEVER;
          } else if (temp.equals("derefInSearching")) {
            deref = LDAPSearchConstraints.DEREF_SEARCHING;
          } else if (temp.equals("derefFindingBaseObj")) {
            deref = LDAPSearchConstraints.DEREF_FINDING;
          } else if (temp.equals("derefAlways")) {
            deref = LDAPSearchConstraints.DEREF_ALWAYS;
          } else {
            throw new SAXException(
              "unknown attribute in searchRequest, " + temp);
          }
          //get timelimit
          temp = attrs.getValue("timeLimit");
          if (temp != null) {
            timeLimit = Integer.parseInt(temp);
          } else {
            timeLimit = 0;
          }

          //get sizeLimit
          temp = attrs.getValue("sizeLimit");
          if (temp != null) {
            sizeLimit = Integer.parseInt(temp);
          } else {
            sizeLimit = 0;
          }

          //put the above fields into a searchConstraints object
          searchCons = new LDAPSearchConstraints(timeLimit, timeLimit,
            //serverTimeLimit
    deref, //dereference int
    sizeLimit, //maxResults
    false, //doReferrals
    0, //batchSize
    null, //referralHandler,
  0);

          //the following are parameters to LDAPSearchRequest
          dn = attrs.getValue("dn");

          temp = attrs.getValue("typesOnly");
          if (temp == null) {
            typesOnly = false;
          } else if (new Boolean(temp).booleanValue() == true) {
            typesOnly = true;
          } else if (new Boolean(temp).booleanValue() == false) {
            typesOnly = false;
          } else {
            throw new SAXException(
              "Invalid value for attribute 'typesOnly'," + temp);
          }

          //Get Scope
          temp = attrs.getValue("scope");
          if (temp == null) {
            scope = LDAPConnection.SCOPE_BASE;
          } else if (temp.equals("baseObject")) {
            scope = LDAPConnection.SCOPE_BASE;
          } else if (temp.equals("singleLevel")) {
            scope = LDAPConnection.SCOPE_ONE;
          } else if (temp.equals("wholeSubtree")) {
            scope = LDAPConnection.SCOPE_SUB;
          } else if (temp.equals("subordinateSubtree")) {
            scope = LDAPConnection.SCOPE_SUBORDINATESUBTREE;
          } else {
            throw new SAXException(
              "Invalid value for attribute 'scope', " + temp);
          }
          filter = null;
        }
        break;
      case AUTH_REQUEST :
        break;
      case MODIFY_REQUEST :
        dn = attrs.getValue("dn");
        break;
      case MODIFICATION :
        {
          String temp;
          attrName = attrs.getValue("name");
          temp = attrs.getValue("operation");
          if (temp == null || attrName == null) {
            throw new SAXException(
              "Required attribute missing from tag "
                + ""
                + "<modification> (operation or name are required)");
          } else if (temp.equals("add")) {
            operation = LDAPModification.ADD;
          } else if (temp.equals("replace")) {
            operation = LDAPModification.REPLACE;
          } else if (temp.equals("delete")) {
            operation = LDAPModification.DELETE;
          } else {
            throw new SAXException(
              "Invalid value for attribute 'operation': " + temp);
          }
        }
        break;
      case ADD_REQUEST :
        dn = attrs.getValue("dn");
        break;
      case DELETE_REQUEST :
        dn = attrs.getValue("dn");
        break;
      case MODIFY_DN_REQUEST :
        {
          String temp;
          dn = attrs.getValue("dn");
          newRDN = attrs.getValue("newrdn");
          temp = attrs.getValue("deleteoldrdn");
          if (temp != null && temp.equals("false")) {
            deleteOldRDN = false;
          } else {
            deleteOldRDN = true;
          }
          newSuperior = attrs.getValue("newSuperior");
        }
        break;
      case COMPARE_REQUEST :
        /* We cannot create a CompareRequest until we have the value
         assertion, which is another state */
        dn = attrs.getValue("dn");
        break;
      case EXTENDED_REQUEST :
        break;

    }
    requestID = attrs.getValue("requestID");
    return;
  }

  // SAX calls this method to pass in character data
  // stored between the start and end tags of a particular element
  public void characters(char[] a, int s, int l) {
    if (state == INITIAL
      || state == ANY
      || state == FINAL
      || state == X_NAME
      || state == X_VALUE
      || state == VALUE
      || state == ERROR_MESSAGE
      || state == MESSAGE
	  || state == ERROR_RESPONSE
      || state == EXTENDED_RESPONSE_NAME
      || state == EXTENDED_RESPONSE_RESPONSE
      || state == REFERRAL_LIST
      || state == SEARCH_RESULT_REFERENCE_REF) {
      value.append(a, s, l);
    }
    return;
  }

  // SAX calls this method when the end-tag for an element is encountered
  public void endElement(
    String strNamespaceURI,
    String strSName,
    String strQName)
    throws SAXException {
    Integer elementTag = (Integer) requestTags.get(strSName);
    if (elementTag == null) {
      if (state != START) { //Ignore tags outside of DSML tags
        throw new SAXNotRecognizedException(
          "Element name, \"" + strQName + "\" not recognized");
      } else {
        return;
      }
    }
    int tag = elementTag.intValue();
    LDAPControl[] controlarr = null;
    String[] referalarr = null;
    try {
      switch (tag) {
        case ERROR_RESPONSE:
        	if (this.errorMessage.indexOf(':') != -1) {
        		String num,msg;
        		num = errorMessage.substring(0,errorMessage.indexOf(':'));
        		
        		try {
        			int errorNum = Integer.parseInt(num);
        			this.errors.add(new LDAPException(this.errorType,errorNum,this.errorMessage.substring(errorMessage.indexOf(':') + 1)));
        		} catch (NumberFormatException nfe) {
        			this.errors.add(new LDAPException(this.errorType,LDAPException.UNWILLING_TO_PERFORM,this.errorMessage));
        		}
        	} else {
        		this.errors.add(new LDAPException(this.errorType,LDAPException.UNWILLING_TO_PERFORM,this.errorMessage));
        	}
        	state = BATCH_RESPONSE;
        case BATCH_REQUEST :
        case BATCH_RESPONSE :
          state = START;
          break;
        case SEARCH_RESULT_REFERENCE_REF :
          String url = new String(value.toString().getBytes("UTF-8"));
          referrallist.add(url);
          state = SEARCH_RESULT_REFERENCE;
          break;
        case SEARCH_RESULT_ENTRY :
          //                queue up search
          {

            state = SEARCH_RESPONSE;
            entry = new LDAPEntry(dn, attrSet);
            LDAPControl[] cons = null;
            if (controls != null && controls.size() > 0) {
              cons =
                (LDAPControl[]) controls.toArray(
                  new LDAPControl[controls.size()]);
            }
            message = new LDAPSearchResult(entry, cons);
            if (requestID != null) {
              message.setTag(requestID);
            }
            requestID = null;
            queue.add(message);
            controls.clear();
          }
          break;

        case SEARCH_RESULT_REFERENCE :
          state = SEARCH_RESPONSE;
          if (!referrallist.isEmpty()) {
            referalarr =
              (String[]) referrallist.toArray(new String[referrallist.size()]);
          }
          try {
            message = new LDAPSearchResultReference(referalarr);

          } catch (MalformedURLException e2) {
            //Can ignore, if the server is not sending
            //a valid ldap url we can ignore.
            System.out.println("MalformeURL Found");
          }

          if (requestID != null) {
            message.setTag(requestID);
          }
          requestID = null;
          errorMessage = null;
          controls.clear();
          queue.add(message);
          break;
        case SEARCH_RESPONSE :
          state = BATCH_RESPONSE;
          break;
        case SEARCH_RESULT_DONE :
          state = SEARCH_RESPONSE;

          if (controls != null && controls.size() > 0) {
            controlarr =
              (LDAPControl[]) controls.toArray(
                new LDAPControl[controls.size()]);
          }

          if (!referrallist.isEmpty()) {

            referalarr =
              (String[]) referrallist.toArray(new String[referrallist.size()]);
          }
            message =
              new LDAPResponse(
                responsetype,
                responsecode,
                dn,
                errorMessage,
                referalarr,
                controlarr);

          if (requestID != null) {
            message.setTag(requestID);
          }
          requestID = null;
          errorMessage = null;
          controls.clear();
          queue.add(message);
          break;

        case EXTENDED_RESPONSE_NAME :
          state = EXTENDED_RESPONSE;
          requestName = value.toString();
          break;
        case EXTENDED_RESPONSE_RESPONSE :
          state = EXTENDED_RESPONSE;
          if (isBase64) {
            String temp = value.toString();

            requestValue = Base64.decode(temp);
          } else {
            requestValue = value.toString().getBytes("UTF-8");
          }
          isBase64 = false;
          break;
        case EXTENDED_RESPONSE :
        	isextendedstate = false;
          //queue up x-operation

          if (controls != null && controls.size() > 0) {
            controlarr =
              (LDAPControl[]) controls.toArray(
                new LDAPControl[controls.size()]);
          }

          if (!referrallist.isEmpty()) {

            referalarr =
              (String[]) referrallist.toArray(new String[referrallist.size()]);
          }
          try {
            message =
              new LDAPExtendedResponse(
                responsecode,
                dn,
                errorMessage,
                referalarr,
                controlarr,
                requestName,
                requestValue);
          } catch (MalformedURLException e1) {
            //Can ignore, if the server is not sending
            //a valid ldap url we can ignore.
            System.out.println("MalformeURL Found");
          }

          if (requestID != null) {
            message.setTag(requestID);
          }
          requestID = null;
          queue.add(message);
          state = BATCH_RESPONSE;
          controls.clear();
          break;

        case DEL_RESPONSE :
        case MODIFY_RESPONSE :
        case MODIFYDN_RESPONSE :
        case COMPARE_RESPONSE :
        case ADD_RESPONSE :
          state = BATCH_RESPONSE;

          if (controls != null && controls.size() > 0) {
            controlarr =
              (LDAPControl[]) controls.toArray(
                new LDAPControl[controls.size()]);
          }

          if (!referrallist.isEmpty()) {

            referalarr =
              (String[]) referrallist.toArray(new String[referrallist.size()]);
          }
            message =
              new LDAPResponse(
                responsetype,
                responsecode,
                dn,
                errorMessage,
                referalarr,
            /* Add Referals */
            controlarr);

          if (requestID != null) {
            message.setTag(requestID);
          }
          requestID = null;
          errorMessage = null;
          controls.clear();
          queue.add(message);
          break;
        case RESULT_CODE :
          //nothing to do.
          break;
        case REFERRAL_LIST :

          String turl = new String(value.toString().getBytes("UTF-8"));
          referrallist.add(turl);
          state = LDAP_RESPONSE;
          break;
        case MESSAGE:
        	errorMessage = new String(value.toString().getBytes("UTF-8"));
            
            state = ERROR_RESPONSE;
            
            break;
        case ERROR_MESSAGE :

          errorMessage = new String(value.toString().getBytes("UTF-8"));
          if (!isextendedstate) {
              state = LDAP_RESPONSE;
          } else {
              state = EXTENDED_RESPONSE;
          }
          break;
        case SEARCH_REQUEST :
          //queue up search
          state = BATCH_REQUEST;
          //Add normal controls to specific search constraints.
          if (controls != null && controls.size() > 0) {
            searchCons.setControls(
              (LDAPControl[]) controls.toArray(
                new LDAPControl[controls.size()]));
          }
          controls.clear();
          if (filter == null) {
            message =
              new LDAPSearchRequest(
                dn,
                scope,
                "",
                (String[]) attributeNames.toArray(
                  new String[attributeNames.size()]),
                searchCons.getDereference(),
                searchCons.getMaxResults(),
                searchCons.getServerTimeLimit(),
                typesOnly,
                searchCons.getControls());
          } else {
            message =
              new LDAPSearchRequest(
                dn,
                scope,
                filter,
                (String[]) attributeNames.toArray(
                  new String[attributeNames.size()]),
                searchCons.getDereference(),
                searchCons.getMaxResults(),
                searchCons.getServerTimeLimit(),
                typesOnly,
                searchCons.getControls());
          }
          if (requestID != null) {
            message.setTag(requestID);
          }
          requestID = null;
          queue.add(message);
          break;
        case ATTRIBUTES :
          state = SEARCH_REQUEST;
          break;
        case ATTRIBUTE :
          state = ATTRIBUTES;
          break;
        case AUTH_REQUEST :
          //bind
          state = BATCH_REQUEST;
          break;
        case MODIFY_REQUEST :
          {
            //queue up modify
            state = BATCH_REQUEST;
            LDAPControl[] cons = null;
            if (controls != null && controls.size() > 0) {
              cons =
                (LDAPControl[]) controls.toArray(
                  new LDAPControl[controls.size()]);
            }
            message =
              new LDAPModifyRequest(
                dn,
                (LDAPModification[]) modlist.toArray(
                  new LDAPModification[modlist.size()]),
                cons);
            if (requestID != null) {
              message.setTag(requestID);
            }
            requestID = null;
            queue.add(message);
            controls.clear();
            break;
          }
        case MODIFICATION :
          //store each modify in 'list'
          {
            LDAPAttribute at = new LDAPAttribute(attrName);
            for (int i = 0; i < attributeValues.size(); i++) {
              at.addValue((byte[]) attributeValues.get(i));
            }

            state = MODIFY_REQUEST;
            LDAPModification mod = new LDAPModification(operation, at);
            modlist.add(mod);
          }
          break;
        case MODIFY_DN_REQUEST :
          {
            //queue up modify
            state = BATCH_REQUEST;
            LDAPControl[] cons = null;
            if (controls != null && controls.size() > 0) {
              cons =
                (LDAPControl[]) controls.toArray(
                  new LDAPControl[controls.size()]);
            }
            message =
              new LDAPModifyDNRequest(
                dn,
                newRDN,
                newSuperior,
                deleteOldRDN,
                cons);

            if (requestID != null) {
              message.setTag(requestID);
            }
            requestID = null;
            queue.add(message);
            controls.clear();
            break;
          }
        case ADD_REQUEST :
          {
            //queue up add
            state = BATCH_REQUEST;
            entry = new LDAPEntry(dn, attrSet);
            LDAPControl[] cons = null;
            if (controls != null && controls.size() > 0) {
              cons =
                (LDAPControl[]) controls.toArray(
                  new LDAPControl[controls.size()]);
            }
            message = new LDAPAddRequest(entry, cons);
            if (requestID != null) {
              message.setTag(requestID);
            }
            requestID = null;
            queue.add(message);
            controls.clear();
            break;
          }
        case DELETE_REQUEST :
          {
            //queue up delete
            state = BATCH_REQUEST;
            LDAPControl[] cons = null;
            if (controls != null && controls.size() > 0) {
              cons =
                (LDAPControl[]) controls.toArray(
                  new LDAPControl[controls.size()]);
            }
            message = new LDAPDeleteRequest(dn, cons);
            if (requestID != null) {
              message.setTag(requestID);
            }
            requestID = null;
            queue.add(message);
            controls.clear();
            break;
          }
        case COMPARE_REQUEST :
          {
            //queue up compare
            state = BATCH_REQUEST;
            LDAPControl[] cons = null;
            if (controls != null && controls.size() > 0) {
              cons =
                (LDAPControl[]) controls.toArray(
                  new LDAPControl[controls.size()]);
            }
            Object compareValue = attributeValues.get(0);
            if (compareValue instanceof byte[]) {
              message =
                new LDAPCompareRequest(
                  dn,
                  attrName,
                  (byte[]) compareValue,
                  cons);
            } else {
              message =
                new LDAPCompareRequest(
                  dn,
                  attrName,
                  ((String) compareValue).getBytes("UTF-8"),
                  cons);
            }
            if (requestID != null)
              message.setTag(requestID);
            requestID = null;
            queue.add(message);
            controls.clear();
            break;
          }
        case ASSERTION :
          //attrs is already complete.
          state = COMPARE_REQUEST;
          break;
        case ADD_ATTRIBUTE :
          { //find an existing LDAPAttribute
            LDAPAttribute attr = attrSet.getAttribute(attrName);

            if (attr == null) {
              //create a new LDAPAttribute and use it.
              attr = new LDAPAttribute(attrName);
              attrSet.add(attr);
            }

            //iterate through values and add them the LDAPAttribute
            int size = attributeValues.size();
            for (int i = 0; i < size; i++) {
              byte[] byteValue;
              Object addValue = attributeValues.get(i);
              if (addValue instanceof byte[]) {
                byteValue = (byte[]) addValue;
              } else {
                byteValue = ((String) addValue).getBytes("UTF8");
              }
              /*
               * Return to previous state which can be Add Request
               *  or Search Result Entry, depending on the Add Request.
               */
              if (isAddRequest)
                state = ADD_REQUEST;
              else
                state = SEARCH_RESULT_ENTRY;

              attr.addValue(byteValue);
            }
          }
          break;
        case EXTENDED_REQUEST :
          {
            //queue up x-operation
            LDAPControl[] cons = null;
            if (controls != null && controls.size() > 0) {
              cons =
                (LDAPControl[]) controls.toArray(
                  new LDAPControl[controls.size()]);
            }
            message =
              new LDAPExtendedRequest(
                new com.novell.ldap.LDAPExtendedOperation(
                  requestName,
                  requestValue),
                cons);
            if (requestID != null)
              message.setTag(requestID);
            requestID = null;
            queue.add(message);
            state = BATCH_REQUEST;
            controls.clear();
            break;
          }
        case X_NAME :
          state = EXTENDED_REQUEST;
          requestName = value.toString();
          break;
        case X_VALUE :
          state = EXTENDED_REQUEST;
          requestValue = Base64.decode(value, 0, value.length());
          break;
        case FILTER :
          state = SEARCH_REQUEST;
          break;
        case NOT :
          filter.endNestedFilter(LDAPSearchRequest.NOT);
          state = FILTER;
          break;
        case AND :
          filter.endNestedFilter(LDAPSearchRequest.AND);
          state = FILTER;
          break;
        case OR :
          filter.endNestedFilter(LDAPSearchRequest.OR);
          state = FILTER;
          break;
        case EQUALITY_MATCH :
          {
            //verify that Equality Match was the last value read
            if (state != EQUALITY_MATCH) {
              throw new SAXException("Unexpected tag: " + strSName);
            }
            filter.addAttributeValueAssertion(
              RfcFilter.EQUALITY_MATCH,
              attrName,
              value.toString().getBytes("UTF-8"));
            state = FILTER;
            //The finish state could also indicate an OR, AND, or NOT
            break;
          }
        case PRESENT :
          {
            //verify that present was the last value read
            if (state != PRESENT) {
              throw new SAXException("Unexpected tag: " + strSName);
            }
            filter.addPresent(attrName);
            state = FILTER;
            //The finish state could also indicate an OR, AND, or NOT
            break;
          }
        case GREATER_OR_EQUAL :
          {
            //verify that '>=' was the last value read
            if (state != GREATER_OR_EQUAL) {
              throw new SAXException("Unexpected tag: " + strSName);
            }
            filter.addAttributeValueAssertion(
              RfcFilter.GREATER_OR_EQUAL,
              attrName,
              value.toString().getBytes("UTF-8"));
            state = FILTER;
            //The finish state could also indicate an OR, AND, or NOT
            break;
          }
        case LESS_OR_EQUAL :
          {
            //verify that '>=' was the last value read
            if (state != LESS_OR_EQUAL) {
              throw new SAXException("Unexpected tag: " + strSName);
            }
            filter.addAttributeValueAssertion(
              RfcFilter.LESS_OR_EQUAL,
              attrName,
              value.toString().getBytes("UTF-8"));
            state = FILTER;
            //The finish state could also indicate an OR, AND, or NOT
            break;
          }
        case APPROXIMATE_MATCH :
          {
            //verify that Approximate match was the last value read
            if (state != APPROXIMATE_MATCH) {
              throw new SAXException("Unexpected tag: " + strSName);
            }
            filter.addAttributeValueAssertion(
              RfcFilter.APPROX_MATCH,
              attrName,
              value.toString().getBytes("UTF-8"));
            state = FILTER;
            break;
          }
        case EXTENSIBLE_MATCH :
          {
            //verify that Approximate match was the last value read
            if (state != EXTENSIBLE_MATCH) {
              throw new SAXException("Unexpected tag: " + strSName);
            }
            filter.addExtensibleMatch(
              this.matchingRule,
              attrName,
              value.toString().getBytes("UTF-8"),
              isDNMatching);
            state = FILTER;
            break;
          }
          //The following states are in a substring tag and should
          //contain values
        case INITIAL :
          //verify that Initial was the last value read:
          if (state != INITIAL) {
            throw new SAXException("Unexpected tag: " + strSName);
          }
          filter.addSubstring(
            RfcFilter.INITIAL,
            value.toString().getBytes("UTF-8"));
          state = SUBSTRINGS;
          break;
        case ANY :
          //verify that ANY was the last value read:
          if (state != ANY) {
            throw new SAXException("Unexpected tag: " + strSName);
          }
          filter.addSubstring(
            RfcFilter.ANY,
            value.toString().getBytes("UTF-8"));
          state = SUBSTRINGS;
          break;
        case FINAL :
          //verify that Initial was the last value read:
          if (state != FINAL) {
            throw new SAXException("Unexpected tag: " + strSName);
          }
          filter.addSubstring(
            RfcFilter.FINAL,
            value.toString().getBytes("UTF-8"));
          state = SUBSTRINGS;
          break;
        case SUBSTRINGS :
          {
            if (state != FINAL && state != SUBSTRINGS) {
              throw new SAXException("Unexpected closing substring tag");
            }
            filter.endSubstrings();
            state = FILTER;
            break;
          }
        case CONTROL :
          byte[] temp;
          if (this.isBase64) {
            temp = Base64.decode(value, 0, value.length());
          } else {
            temp = value.toString().getBytes("UTF-8");
          }
          controls.add(
            new LDAPControl(oid, critical, temp));
          //return to previous state.
          state = prevstate;
          break;
        case VALUE :
          state = valueState; //reset state to previous state
          if (this.isBase64) {
            attributeValues.add(Base64.decode(value, 0, value.length()));
          } else {
            
            
          	if (value == null) value = new StringBuffer();
            attributeValues.add(value.toString().getBytes("UTF-8"));
          }
          break;
      }
    } catch (LDAPException e) {
      throw new SAXException(e);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF8 encoding not supported:" + e);
    }
    return;
  }

  public void warning(SAXParseException e) throws SAXException {
   
  }

  public void error(SAXParseException e) throws SAXException {
    System.out.println("error: " + e.toString());
    throw e;
  }

  public void fatalError(SAXParseException e) throws SAXException {
    System.out.println("line : " + e.getLineNumber() + ", column : " + e.getColumnNumber());
  	System.out.println("fatal error: " + e.toString());
    throw e;
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of ContentHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Receive a Locator object for document events.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass if they wish to store the locator for use
   * with other document events.</p>
   *
   * @param locator A locator for all SAX document events.
   * @see org.xml.sax.ContentHandler#setDocumentLocator
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator(Locator locator) {
    // no op
    return;
  }

  /**
   * Receive notification of the beginning of the document.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the beginning
   * of a document (such as allocating the root node of a tree or
   * creating an output file).</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startDocument
   */
  public void startDocument() throws SAXException {
    // no op
    return;
  }

  /**
   * Receive notification of the end of the document.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the end
   * of a document (such as finalising a tree or closing an output
   * file).</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endDocument
   */
  public void endDocument() throws SAXException {
    // no op
  }

  /**
   * Receive notification of the start of a Namespace mapping.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the start of
   * each Namespace prefix scope (such as storing the prefix mapping).</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI mapped to the prefix.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startPrefixMapping
   */
  public void startPrefixMapping(String prefix, String uri)
    throws SAXException {
    // no op
    return;
  }

  /**
   * Receive notification of the end of a Namespace mapping.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the end of
   * each prefix mapping.</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endPrefixMapping
   */
  public void endPrefixMapping(String prefix) throws SAXException {
    // no op
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method to take specific actions for each chunk of ignorable
   * whitespace (such as adding data to a node or buffer, or printing
   * it to a file).</p>
   *
   * @param ch The whitespace characters.
   * @param start The start position in the character array.
   * @param length The number of characters to use from the
   *               character array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#ignorableWhitespace
   */
  public void ignorableWhitespace(char ch[], int start, int length)
    throws SAXException {
    // no op
    return;
  }

  /**
   * Receive notification of a processing instruction.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions for each
   * processing instruction, such as setting status variables or
   * invoking other methods.</p>
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *             none is supplied.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   */
  public void processingInstruction(String target, String data)
    throws SAXException {
    // no op
    return;
  }

  /**
   * Receive notification of a skipped entity.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions for each
   * processing instruction, such as setting status variables or
   * invoking other methods.</p>
   *
   * @param name The name of the skipped entity.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   */
  public void skippedEntity(String name) throws SAXException {
    // no op
    return;
  }

  /* package */
  String getBatchRequestID() {
    return this.batchRequestID;
  }

  /* package */
  boolean isParallelProcessing() {
    return this.isParallel;
  }

  /* package */
  boolean isResponseUnordered() {
    return this.isUnordered;
  }

  /* package */
  boolean isResumeOnError() {
    return this.isResumeOnError;
  }
  /*package */
  ArrayList getQueue() {
    return this.queue;
  }
  
  ArrayList getErrors() {
  	return this.errors;
  }
}
