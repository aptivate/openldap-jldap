/* **************************************************************************
 * $Novell: DSMLHandler.java,v 1.19 2002/11/12 17:37:28 $
 *
 * Copyright (C) 2002 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.client;

import com.novell.ldap.*;
import com.novell.ldap.rfc2251.*;
import com.novell.ldap.message.*;
import com.novell.ldap.util.Base64;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

import org.xml.sax.*;

public class DSMLHandler implements ContentHandler, ErrorHandler
{
    /** Holds parsed LDAPMessages ready for use */
    public ArrayList queue = new ArrayList();

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
    private ArrayList controls;

    /* The following values are valid states for the parser: the tags they
        represent are in comments to the right */
    private static final int START=0;
    private static final int BATCH_REQUEST=1;       //<batchRequest>

    /* The following are possible states from the batchRequest state */
    private static final int AUTH_REQUEST=2;        //<authRequest>
    private static final int MODIFY_REQUEST=3;      //<modifyRequest>
    private static final int SEARCH_REQUEST=4;      //<searchRequest>
    private static final int ADD_REQUEST=5;         //<addRequest>
    private static final int DELETE_REQUEST=6;      //<delRequest>
    private static final int MODIFY_DN_REQUEST=7;   //<modDNRequest>
    private static final int COMPARE_REQUEST=8;     //<compareRequest>
    private static final int EXTENDED_REQUEST=9;    //<extendedRequest>

    /* The following are possible states from filter, compare and search */
    private static final int ASSERTION = 10;        //<assertion>
    private static final int VALUE = 11;            //<value>
    private static final int ATTRIBUTES = 12;       //<attributes>
    private static final int ATTRIBUTE  = 13;       //<attribute>
    private static final int FILTER = 14;           //<filter>
    private static final int AND = 15;              //<and>
    private static final int OR = 16;               //<or>
    private static final int NOT = 17;              //<not>
    private static final int EQUALITY_MATCH = 18;   //<equalityMatch>
    private static final int SUBSTRINGS = 19;       //<substrings>
    private static final int GREATER_OR_EQUAL = 20; //<greaterOrEqual>
    private static final int LESS_OR_EQUAL = 21;    //<lessOrEqual>
    private static final int PRESENT = 22;          //<present>
    private static final int APPROXIMATE_MATCH = 23;//<approxMatch>
    private static final int EXTENSIBLE_MATCH = 24; //<extensibleMatch>
    private static final int INITIAL = 25;          //<initial>
    private static final int ANY = 26;              //<any>
    private static final int FINAL = 27;            //<final>

    /* miscelaneous tags :*/
    private static final int ADD_ATTRIBUTE = 28;    //<attr>
    private static final int MODIFICATION = 29;     //<modification>
    private static final int X_NAME = 30;           //<requestName>
    private static final int X_VALUE = 31;          //<requestValue>

    private static final int CONTROL = 32;          //<control>

    private static final int BATCH_RESPONSE= 34;    //<batchResponse>
    /* The folling are possible states from the BatchResponse state
        .... NOT Implemented ... SearchResponse ...*/

    /** state contains the internal parsing state **/
    private int state = START;
    private static final java.util.HashMap requestTags;
    /* valueState indicates the state before an <value> is found */
    private int valueState;
    private boolean critical;
    private String oid;

    static {  //Initialize requestTags
        requestTags = new java.util.HashMap(35, (float)0.25);
        //Load factor of 0.25 optimizes for speed rather than size.

        requestTags.put("batchRequest", new Integer(BATCH_REQUEST));
        requestTags.put("authRequest",  new Integer(AUTH_REQUEST));
        requestTags.put("modifyRequest",new Integer(MODIFY_REQUEST));
        requestTags.put("searchRequest",new Integer(SEARCH_REQUEST));
        requestTags.put("addRequest",   new Integer(ADD_REQUEST));
        requestTags.put("delRequest",   new Integer(DELETE_REQUEST));
        requestTags.put("modDNRequest", new Integer(MODIFY_DN_REQUEST));
        requestTags.put("compareRequest",  new Integer(COMPARE_REQUEST));
        requestTags.put("extendedRequest", new Integer(EXTENDED_REQUEST));
        requestTags.put("batchResponse",new Integer(BATCH_RESPONSE));
        requestTags.put("assertion",    new Integer(ASSERTION));
        requestTags.put("value",        new Integer(VALUE));
        requestTags.put("attributes",   new Integer(ATTRIBUTES));
        requestTags.put("attribute",    new Integer(ATTRIBUTE));
        requestTags.put("filter",       new Integer(FILTER));
        requestTags.put("and",          new Integer(AND));
        requestTags.put("or",           new Integer(OR));
        requestTags.put("not",          new Integer(NOT));
        requestTags.put("equalityMatch",new Integer(EQUALITY_MATCH));
        requestTags.put("substrings",   new Integer(SUBSTRINGS));
        requestTags.put("greaterOrEqual",new Integer(GREATER_OR_EQUAL));
        requestTags.put("lessOrEqual",  new Integer(LESS_OR_EQUAL));
        requestTags.put("present",      new Integer(PRESENT));
        requestTags.put("approxMatch",  new Integer(APPROXIMATE_MATCH));
        requestTags.put("extensibleMatch", new Integer(EXTENSIBLE_MATCH));

        requestTags.put("attr",         new Integer(ADD_ATTRIBUTE));
        requestTags.put("modification", new Integer(MODIFICATION));
        requestTags.put("requestName",  new Integer(X_NAME));
        requestTags.put("requestValue", new Integer(X_VALUE));
        requestTags.put("initial",      new Integer(INITIAL));
        requestTags.put("any",          new Integer(ANY));
        requestTags.put("final",        new Integer(FINAL));

        requestTags.put("control",      new Integer(CONTROL));
        requestTags.put("controlValue", new Integer(VALUE));
    }

    // SAX calls this method when it encounters an element
    public void startElement(String strNamespaceURI,
                             String strSName,
                             String strQName,
                             Attributes attrs) throws SAXException
    {
        Integer elementTag = (Integer)requestTags.get(strSName);
        if (elementTag == null){
            if (state != START){  //Ignore tags outside of DSML tags
            throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
            }
        }
        int tag = elementTag.intValue();
        if (tag == CONTROL) {
            if (state != CONTROL){
                controls.clear();
            }
            handleControl(attrs);
            state = CONTROL;
        }
        switch (state){
            // The following values are valid states for the parser:
            case START:
                // we can now read a Batch_Request tag or Batch_Response tag
                if (tag == BATCH_REQUEST || tag == BATCH_RESPONSE){
                    state = tag;
                } else {
                    throw new SAXException("Invalid beginning tag :" +
                            strQName);
                }
                break;
            case BATCH_REQUEST:
                state = tag;
                if (tag == ADD_REQUEST){
                    attrSet = new LDAPAttributeSet();
                }
                if (tag == MODIFY_REQUEST){
                    modlist.clear();
                }
                parseTagAttributes( tag, attrs );
                break;
            case SEARCH_REQUEST:
                if (tag == ATTRIBUTES ){
                    this.attributeNames.clear();
                    this.attributeValues.clear();
                    state = tag;
                } else if (tag == FILTER){
                    state = FILTER;
                    filter = new RfcFilter();
                } else
                    throw new SAXException("invalid searchRequest tag: " +
                            strSName);
                break;
            case AUTH_REQUEST:
            case MODIFY_REQUEST:
                if (tag == MODIFICATION){
                    state = tag;
                    attributeValues.clear();
                    parseTagAttributes( tag, attrs );
                }
                else
                    throw new SAXException("invalid modifyRequest tag: " +
                            strSName);
                break;
            case ADD_REQUEST:
                if (tag == ADD_ATTRIBUTE){
                    state = tag;
                    attributeValues.clear();
                    attrName = attrs.getValue("name");
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("invalid addRequest tag: " +
                            strSName);
                break;
            case DELETE_REQUEST:
                break;
            case MODIFY_DN_REQUEST:
                break;
            case COMPARE_REQUEST:
                if (tag == ASSERTION) {
                    attrName =  attrs.getValue("name") ;
                    state = tag;
                }
                else
                    throw new SAXException("invalid compareRequest tag: " +
                            strSName);
                break;
            //Tags with multiple names but no value tags embedded
            case ATTRIBUTES:
                //list of attribute names
                if (tag == ATTRIBUTE){
                    //add a search attributes name
                    attributeNames.add(attrs.getValue("name"));
                    state = tag;
                } else {
                    throw new SAXException("invalid attributes tag: " +
                            strSName);
                }
                break;
            //Substring tag can contain initial, any, or final tags
            case SUBSTRINGS:
                if ((tag == INITIAL) || (tag == ANY)  || (tag == FINAL)){
                    state = tag;
                    value = new StringBuffer();
                }
                else {
                    throw new SAXException("invalid substrings tag: " + strSName);
                }
                break;
            case FILTER:
            case AND:
            case OR:
            case NOT:
                handleFilter(tag, attrs, strSName);
                state = tag;
                break;
            case EXTENDED_REQUEST:
                if (tag == X_NAME || tag == X_VALUE){
                    state = tag;
                    value = new StringBuffer();
                }
                break;

            //Tags with <value> tags expected:
            case CONTROL:
            case MODIFICATION:
            case ADD_ATTRIBUTE:
            case ASSERTION:
            //The following states are in a filter tag and should contain values
            case EQUALITY_MATCH:
            case GREATER_OR_EQUAL:
            case LESS_OR_EQUAL:
            case PRESENT:
            case APPROXIMATE_MATCH:
            case EXTENSIBLE_MATCH:
                if (tag == VALUE) {
                    /* remember our current state so we can return to it after
                        the value is parsed */
                    valueState = state;
                    state = tag;
                    value = new StringBuffer();
                    String temp = attrs.getValue("type");
                    if ( temp != null && temp.equals("xsd:base64Binary") ){
                        isBase64 = true;
                    } else {
                        isBase64 = false;
                    }
                }
                else
                    throw new SAXException("invalid tag: " + strSName);
                break;
        }
        return;
    }

    private void handleControl(Attributes attrs) {
        if (controls == null){
            controls = new ArrayList();
        }

        this.oid = attrs.getValue("type");
        this.critical =
                "true".equalsIgnoreCase(attrs.getValue("criticality"));
        return;
    }

    private void handleFilter(int tag, Attributes attrs, String strSName)
            throws SAXException
    {
        try {
            switch (tag){
                case AND:
                    filter.startNestedFilter(RfcFilter.AND);
                    break;
                case OR:
                    filter.startNestedFilter(RfcFilter.OR);
                    break;
                case NOT:
                    filter.startNestedFilter(RfcFilter.NOT);
                    break;
                case SUBSTRINGS:
                    this.attrName = attrs.getValue("name");
                    if (this.attrName == null){
                       throw new SAXException("The mandatory attribute 'name' "+
                                "is missing from tag <" + strSName + ">");
                    }
                    filter.startSubstrings(attrName);
                    break;
                    //don't break, we need the attribute name.
                case EQUALITY_MATCH:
                case GREATER_OR_EQUAL:
                case LESS_OR_EQUAL:
                case PRESENT:
                case APPROXIMATE_MATCH:
                    this.attrName = attrs.getValue("name");
                    if (this.attrName == null){
                       throw new SAXException("The mandatory attribute 'name' "+
                                "is missing from tag <" + strSName + ">");
                    }
                    break;
                case EXTENSIBLE_MATCH:
                    //name is not mandatory for extensible match
                    this.attrName = attrs.getValue("name");
                    String dnAttributes = attrs.getValue("dnAttributes");
                    if (dnAttributes != null &&
                        dnAttributes.equalsIgnoreCase("true"))
                    {
                        this.isDNMatching = true;
                    } else { //false is default
                        this.isDNMatching = false;
                    }
                    this.matchingRule = attrs.getValue("matchingRule");
                    break;
                default:
                    throw new SAXException("invalid tag in filter: "+strSName);
            }
        } catch (LDAPLocalException e) {
            throw new SAXException("An error occured constructing a filter:" +
                    e.toString());
        }
        return;
    }

    private void parseTagAttributes( int tag, Attributes attrs )
            throws SAXException
    {

        switch (tag){
            case SEARCH_REQUEST:
                {
                    String temp;
                    int timeLimit, deref, sizeLimit;

                    //Get dereferencing Aliases
                    temp = attrs.getValue("derefAliases");
                    if (temp == null){
                        deref = LDAPSearchConstraints.DEREF_ALWAYS;
                    } else if (temp.equals("neverDerefAliases")){
                        deref = LDAPSearchConstraints.DEREF_NEVER;
                    } else if (temp.equals("derefInSearching")){
                        deref = LDAPSearchConstraints.DEREF_SEARCHING;
                    } else if (temp.equals("derefFindingBaseObj")){
                        deref = LDAPSearchConstraints.DEREF_FINDING;
                    } else if (temp.equals("derefAlways")){
                        deref = LDAPSearchConstraints.DEREF_ALWAYS;
                    } else throw new SAXException (
                            "unknown attribute in searchRequest, " + temp);

                    //get timelimit
                    temp = attrs.getValue("timelimit");
                    if (temp != null){
                        timeLimit = Integer.parseInt(temp);
                    } else {
                        timeLimit = 0;
                    }

                    //get sizeLimit
                    temp = attrs.getValue("sizeLimit");
                    if (temp != null){
                        sizeLimit = Integer.parseInt(temp);
                    } else {
                        sizeLimit = 0;
                    }

                    //put the above fields into a searchConstraints object
                    searchCons = new LDAPSearchConstraints(
                            timeLimit,
                            timeLimit,  //serverTimeLimit
                            deref,      //dereference int
                            sizeLimit,  //maxResults
                            false,      //doReferrals
                            0,          //batchSize
                            null, //referralHandler,
                            0);

                    //the following are parameters to LDAPSearchRequest
                    dn = attrs.getValue("dn");

                    temp = attrs.getValue("typesOnly");
                    if (temp == null){
                        typesOnly = false;
                    } else if ( new Boolean(temp).booleanValue() == true ){
                        typesOnly = true;
                    } else if ( new Boolean(temp).booleanValue() == false){
                        typesOnly = false;
                    } else {
                        throw new SAXException(
                                "Invalid value for attribute 'typesOnly',"+
                                temp);
                    }

                    //Get Scope
                    temp = attrs.getValue("scope");
                    if (temp == null){
                        scope = LDAPConnection.SCOPE_BASE;
                    } else if (temp.equals("baseObject")){
                        scope = LDAPConnection.SCOPE_BASE;
                    } else if (temp.equals("singleLevel")){
                        scope = LDAPConnection.SCOPE_ONE;
                    } else if (temp.equals("wholeSubtree")){
                        scope = LDAPConnection.SCOPE_SUB;
                    } else throw new SAXException(
                            "Invalid value for attribute 'scope', "+ temp);

                    filter = null;
                }
                break;
            case AUTH_REQUEST:
                break;
            case MODIFY_REQUEST:
                dn = attrs.getValue("dn");
                break;
            case MODIFICATION:
                {
                    String temp;
                    attrName = attrs.getValue("name");
                    temp = attrs.getValue("operation");
                    if (temp == null || attrName == null){
                        throw new SAXException(
                            "Required attribute missing from tag " +"" +
                            "<modification> (operation or name are required)");
                    } else if (temp.equals("add")){
                        operation = LDAPModification.ADD;
                    } else if (temp.equals("replace")){
                        operation = LDAPModification.REPLACE;
                    } else if (temp.equals("delete")){
                        operation = LDAPModification.DELETE;
                    } else {
                        throw new SAXException(
                            "Invalid value for attribute 'operation': "+ temp);
                    }
                }
                break;
            case ADD_REQUEST:
                dn = attrs.getValue("dn");
                break;
            case DELETE_REQUEST:
                dn = attrs.getValue("dn");
                break;
            case MODIFY_DN_REQUEST:
                {
                    String temp;
                    dn = attrs.getValue("dn");
                    newRDN = attrs.getValue("newrdn");
                    temp = attrs.getValue("deleteoldrdn");
                    if ( temp!=null && temp.equals("false")){
                        deleteOldRDN = false;
                    } else {
                        deleteOldRDN = true;
                    }
                    newSuperior = attrs.getValue("newSuperior");
                }
                break;
            case COMPARE_REQUEST:
                /* We cannot create a CompareRequest until we have the value
                 assestion, which is another state */
                dn = attrs.getValue("dn");
                break;
            case EXTENDED_REQUEST:
                break;
        }
        return;
    }

    // SAX calls this method to pass in character data
    // stored between the start and end tags of a particular element
    public void characters(char[] a,
                           int s,
                           int l)
    {
        if (state == INITIAL || state == ANY || state == FINAL ||
            state == X_NAME  || state == X_VALUE || state == VALUE) {
                value.append(a, s, l);
        }
        return;
    }

    // SAX calls this method when the end-tag for an element is encountered
    public void endElement(String strNamespaceURI,
                           String strSName,
                           String strQName) throws SAXException
    {
        Integer elementTag = (Integer)requestTags.get(strSName);
        if (elementTag == null){
            if (state != START){  //Ignore tags outside of DSML tags
                throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
            }
        }
        int tag = elementTag.intValue();

        try {
            switch (tag){
                case BATCH_REQUEST:
                case BATCH_RESPONSE:
                    state = START;
                    break;
                case SEARCH_REQUEST:
                    //queue up search
                    state = BATCH_REQUEST;
                    //Add normal controls to specific search constraints.
                    if (controls != null && controls.size() >0){
                        searchCons.setControls(
                                (LDAPControl[])controls.toArray(
                                        new LDAPControl[controls.size()]));
                    }

                    if (filter == null){
                        message = new LDAPSearchRequest(dn, scope, "",
                                (String[]) attributeNames.toArray(
                                        new String[ attributeNames.size() ] ),
                                searchCons.getDereference(),
                                searchCons.getMaxResults(),
                                searchCons.getServerTimeLimit(),
                                typesOnly,
                                searchCons.getControls());
                    } else {
                        message = new LDAPSearchRequest(dn, scope, filter,
                                (String[]) attributeNames.toArray(
                                        new String[ attributeNames.size() ] ),
                                searchCons.getDereference(),
                                searchCons.getMaxResults(),
                                searchCons.getServerTimeLimit(),
                                typesOnly,
                                searchCons.getControls());
                    }
                    queue.add(message);
                    break;
                case ATTRIBUTES:
                    state = SEARCH_REQUEST;
                    break;
                case ATTRIBUTE:
                    state = ATTRIBUTES;
                    break;
                case AUTH_REQUEST:
                    //bind
                    state = BATCH_REQUEST;
                    break;
                case MODIFY_REQUEST:{
                    //queue up modify
                    state = BATCH_REQUEST;
                    LDAPControl [] cons=null;
                    if (controls!=null && controls.size()>0){
                        cons = (LDAPControl[])controls.toArray(
                                        new LDAPControl[controls.size()]);
                    }
                    message = new LDAPModifyRequest( dn,
                                (LDAPModification[])modlist.toArray(
                                        new LDAPModification[ modlist.size() ]),
                                cons);
                    queue.add(message);
                    break;
                }
                case MODIFICATION:
                    //store each modify in 'list'
                    {
                        state = MODIFY_REQUEST;
                        LDAPModification mod =
                            new LDAPModification( operation,
                                new LDAPAttribute(attrName,
                                (String[]) attributeValues.toArray(
                                        new String[ attributeValues.size()])));
                        modlist.add(mod);
                    }
                    break;
                case MODIFY_DN_REQUEST:{
                    //queue up modify
                    state = BATCH_REQUEST;
                    LDAPControl [] cons=null;
                    if (controls!=null && controls.size()>0){
                        cons = (LDAPControl[])controls.toArray(
                                        new LDAPControl[controls.size()]);
                    }
                    message = new LDAPModifyDNRequest( dn, newRDN, newSuperior,
                            deleteOldRDN, cons);
                    queue.add(message);
                    break;
                }
                case ADD_REQUEST: {
                    //queue up add
                    state = BATCH_REQUEST;
                    entry = new LDAPEntry(dn, attrSet);
                    LDAPControl [] cons=null;
                    if (controls!=null && controls.size()>0){
                        cons = (LDAPControl[])controls.toArray(
                                        new LDAPControl[controls.size()]);
                    }
                    message = new LDAPAddRequest( entry, cons );
                    queue.add(message);
                    break;
                }
                case DELETE_REQUEST:{
                    //queue up delete
                    state = BATCH_REQUEST;
                    LDAPControl [] cons=null;
                    if (controls!=null && controls.size()>0){
                        cons = (LDAPControl[])controls.toArray(
                                        new LDAPControl[controls.size()]);
                    }
                    message = new LDAPDeleteRequest(dn,cons);
                    queue.add(message);
                    break;
                }
                case COMPARE_REQUEST:{
                    //queue up compare
                    state = BATCH_REQUEST;
                    LDAPControl [] cons=null;
                    if (controls!=null && controls.size()>0){
                        cons = (LDAPControl[])controls.toArray(
                                        new LDAPControl[controls.size()]);
                    }
                    Object compareValue = attributeValues.get(0);
                    if (compareValue instanceof byte[]){
                        message = new LDAPCompareRequest(dn, attrName,
                                (byte[])compareValue, cons);
                    } else {
                        message = new LDAPCompareRequest(dn, attrName,
                                ((String)compareValue).getBytes("UTF-8"),cons);
                    }
                    queue.add(message);
                    break;
                }
                case ASSERTION:
                    //attrs is already complete.
                    state = COMPARE_REQUEST;
                    break;
                case ADD_ATTRIBUTE:
                    {
                        byte[] byteValue;
                        Object addValue = attributeValues.get(0);
                        if (addValue instanceof byte[]){
                            byteValue = (byte[])addValue;
                        } else {
                            byteValue = ((String)addValue).getBytes("UTF8");
                        }
                        state = ADD_REQUEST;
                        LDAPAttribute attr = attrSet.getAttribute(attrName);
                        if (attr == null){
                            //create a new attribute
                            attr = new LDAPAttribute(attrName, byteValue);
                            attrSet.add(attr);
                        } else {
                            attr.addValue(byteValue);
                        }
                    }
                    break;
                case EXTENDED_REQUEST:{
                    //queue up x-operation
                    LDAPControl [] cons=null;
                    if (controls!=null && controls.size()>0){
                        cons = (LDAPControl[])controls.toArray(
                                        new LDAPControl[controls.size()]);
                    }
                    message = new LDAPExtendedRequest(
                            new com.novell.ldap.LDAPExtendedOperation(
                                    requestName, requestValue ), cons);
                    queue.add(message);
                    state = BATCH_REQUEST;
                    break;
                }
                case X_NAME:
                    state = EXTENDED_REQUEST;
                    requestName = value.toString();
                    break;
                case X_VALUE:
                    {
                        state = EXTENDED_REQUEST;
                        requestValue = Base64.decode(value, 0, value.length());
                        break;
                    }
                case FILTER:
                    state = SEARCH_REQUEST;
                    break;
                case NOT:
                case AND:
                case OR:
                    state = FILTER;
                    break;
                case EQUALITY_MATCH:{
                    //verify that Equality Match was the last value read
                    if (state != EQUALITY_MATCH){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addAttributeValueAssertion(RfcFilter.EQUALITY_MATCH,
                            attrName, value.toString().getBytes("UTF-8"));
                    state = FILTER;
                    //The finish state could also indicate an OR, AND, or NOT
                    break;
                }
                case PRESENT:{
                    //verify that present was the last value read
                    if (state != PRESENT){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addPresent(attrName);
                    state = FILTER;
                    //The finish state could also indicate an OR, AND, or NOT
                    break;
                }
                case GREATER_OR_EQUAL: {
                    //verify that '>=' was the last value read
                    if (state != GREATER_OR_EQUAL){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addAttributeValueAssertion(RfcFilter.GREATER_OR_EQUAL,
                            attrName, value.toString().getBytes("UTF-8"));
                    state = FILTER;
                    //The finish state could also indicate an OR, AND, or NOT
                    break;
                }
                case LESS_OR_EQUAL: {
                    //verify that '>=' was the last value read
                    if (state != LESS_OR_EQUAL){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addAttributeValueAssertion(RfcFilter.LESS_OR_EQUAL,
                            attrName, value.toString().getBytes("UTF-8"));
                    state = FILTER;
                    //The finish state could also indicate an OR, AND, or NOT
                    break;
                }
                case APPROXIMATE_MATCH: {
                    //verify that Approximate match was the last value read
                    if (state != APPROXIMATE_MATCH){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addAttributeValueAssertion(RfcFilter.APPROX_MATCH,
                            attrName, value.toString().getBytes("UTF-8"));
                    state = FILTER;
                    break;
                }
                case EXTENSIBLE_MATCH:{
                    //verify that Approximate match was the last value read
                    if (state != EXTENSIBLE_MATCH){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addExtensibleMatch(
                            this.matchingRule,
                            attrName, value.toString().getBytes("UTF-8"),
                            isDNMatching);
                    state = FILTER;
                    break;
                }
                //The following states are in a substring tag and should contain values
                case INITIAL:
                    //verify that Initial was the last value read:
                    if (state != INITIAL){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addSubstring(RfcFilter.INITIAL,
                            value.toString().getBytes("UTF-8"));
                    state = SUBSTRINGS;
                    break;
                case ANY:
                    //verify that ANY was the last value read:
                    if (state != ANY){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addSubstring(RfcFilter.ANY,
                            value.toString().getBytes("UTF-8"));
                    state = SUBSTRINGS;
                    break;
                case FINAL:
                    //verify that Initial was the last value read:
                    if (state != FINAL){
                        throw new SAXException("Unexpected tag: "+ strSName);
                    }
                    filter.addSubstring(RfcFilter.FINAL,
                            value.toString().getBytes("UTF-8"));
                    state = SUBSTRINGS;
                    break;
                case SUBSTRINGS:{
                    if (state != FINAL && state != SUBSTRINGS){
                        throw new SAXException("Unexpected closing substring tag");
                    }
                    filter.endSubstrings();
                    break;
                }
                case CONTROL:
                    controls.add(new LDAPControl(oid, critical,
                            value.toString().getBytes("UTF-8")));
                    break;
                case VALUE:
                    state = valueState; //reset state to previous state
                    if (this.isBase64){
                        attributeValues.add(
                                Base64.decode(value, 0, value.length()));
                    } else {
                        attributeValues.add(value.toString());
                    }
                    break;
            }
        } catch (LDAPException e) {
            throw new SAXException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(
                    "UTF8 encoding not supported:" + e );
        }
        return;
    }

    public void warning(SAXParseException e) throws SAXException
    {
        System.out.println("warning: " + e.toString());
        throw e;
    }
    public void error(SAXParseException e) throws SAXException
    {
        System.out.println("error: " + e.toString());
        throw e;
    }
    public void fatalError(SAXParseException e) throws SAXException
    {
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
    public void setDocumentLocator (Locator locator)
    {
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
    public void startDocument ()
	throws SAXException
    {
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
    public void endDocument ()
	throws SAXException
    {
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
    public void startPrefixMapping (String prefix, String uri)
	throws SAXException
    {
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
    public void endPrefixMapping (String prefix)
	throws SAXException
    {
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
    public void ignorableWhitespace (char ch[], int start, int length)
	throws SAXException
    {
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
    public void processingInstruction (String target, String data)
	throws SAXException
    {
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
    public void skippedEntity (String name)
	throws SAXException
    {
	    // no op
        return;
    }

}

