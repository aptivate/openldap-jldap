package com.novell.ldap.ldif_dsml;

import com.novell.ldap.*;
import com.novell.ldap.message.*;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

import org.xml.sax.*;

/*package public*/
class DSMLHandler implements ContentHandler, ErrorHandler
{
    public ArrayList queue = new ArrayList();
    /* variables used for message information */
    private LDAPMessage message = null;
    private LDAPEntry entry = null;
    private LDAPAttributeSet attrSet = null;
    /* attribute list is used differently for different operations:
        - for attribute names (string) In the Compare operation
        - for attribute values = (string) In multiple <value> attributes */
    private ArrayList attributeList = new ArrayList();
    /* modlist is used for modifications in the ModRequest operation */
    private ArrayList modlist = new ArrayList();

    /* individual variables used to build messages LDAP attributes */
    private String requestName;
    private byte[] requestValue;
    private LDAPSearchConstraints searchCons = null;
    private String attrName = null;
    private String dn, filter, newRDN, newSuperior;
    private StringBuffer value;
    private boolean typesOnly, deleteOldRDN, isBase64;
    private int scope, operation;

    /* The following values are valid states for the parser: tags are in
    comments*/
    private static final int START=0;
    private static final int BATCH_REQUEST=1;       //batchRequest

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
    private static final int VALUE_COMPLETE = 12;   //</value>
    private static final int ATTRIBUTES = 13;       //<attributes>
    private static final int ATTRIBUTE  = 14;       //<attribute>
    private static final int FILTER = 15;           //<filter>
    private static final int SUBSTRINGS = 16;       //<substrings>
    private static final int FINAL = 17;            //<final>

    /* miscelaneous tags :*/
    private static final int ADD_ATTRIBUTE = 18;    //<attr>
    private static final int MODIFICATION = 19;     //<modification>
    private static final int X_NAME = 20;           //<requestName>
    private static final int X_VALUE = 21;          //<requestValue>

    private static final int BATCH_RESPONSE= 22;    //<batchResponse>
    /* The folling are possible states from the BatchResponse state
        .... SearchResponse ...*/

    /** state contains the internal parsing state **/
    private int state = START;
    private static java.util.HashMap requestTags = null;

    static {  //Initialize requestTags
        if (requestTags == null){
            requestTags = new java.util.HashMap(23, 9999);
            //High load factor means optimized for lookup time
            requestTags.put("batchRequest", new Integer(BATCH_REQUEST));
            requestTags.put("authRequest",  new Integer(AUTH_REQUEST));
            requestTags.put("modifyRequest",new Integer(MODIFY_REQUEST));
            requestTags.put("searchRequest",new Integer(SEARCH_REQUEST));
            requestTags.put("addRequest",   new Integer(ADD_REQUEST));
            requestTags.put("delRequest",new Integer(DELETE_REQUEST));
            requestTags.put("modDNRequest",  new Integer(MODIFY_DN_REQUEST));
            requestTags.put("compareRequest",   new Integer(COMPARE_REQUEST));
            requestTags.put("extendedRequest",  new Integer(EXTENDED_REQUEST));
            requestTags.put("batchResponse",new Integer(BATCH_RESPONSE));
            requestTags.put("assertion",    new Integer(ASSERTION));
            requestTags.put("value",        new Integer(VALUE));
            requestTags.put("attributes",   new Integer(ATTRIBUTES));
            requestTags.put("attribute",    new Integer(ATTRIBUTE));
            requestTags.put("filter",       new Integer(FILTER));
            requestTags.put("substrings",   new Integer(SUBSTRINGS));
            requestTags.put("final",        new Integer(FINAL));
            requestTags.put("attr",         new Integer(ADD_ATTRIBUTE));
            requestTags.put("modification", new Integer(MODIFICATION));
            requestTags.put("requestName",  new Integer(X_NAME));
            requestTags.put("requestValue", new Integer(X_VALUE));
        }
    }

    // SAX calls this method when it encounters an element
    public void startElement(String strNamespaceURI,
                             String strLocalName,
                             String strQName,
                             Attributes attrs) throws SAXException
    {
        Integer elementTag = (Integer)requestTags.get(strQName);
        if (elementTag == null){
            throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
        }
        int tag = elementTag.intValue();

        switch (state){
            // The following values are valid states for the parser:
            case START:
                // we can now read a Batch_Request tag or Batch_Response tag
                if (tag == BATCH_REQUEST || tag == BATCH_RESPONSE){
                    state = tag;
                } else {
                    throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
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
                if (tag == ATTRIBUTES){
                    this.attributeList.clear();
                    state = tag;
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete searchRequest tag");

                break;
            case AUTH_REQUEST:
            case MODIFY_REQUEST:
                if (tag == MODIFICATION){
                    state = tag;
                    attributeList.clear();
                    parseTagAttributes( tag, attrs );
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete modifyRequest tag");
                break;
            case ADD_REQUEST:
                if (tag == ADD_ATTRIBUTE){
                    state = tag;
                    this.attributeList.clear();
                    attrName = attrs.getValue("name");
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete addRequest tag");

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
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete compareRequest tag");
                break;
            //Tags with <value> tags embedded:
            case MODIFICATION:
            case ADD_ATTRIBUTE:
            case ASSERTION:
                if (tag == VALUE){
                    state = tag;
                    value = new StringBuffer();
                    String temp = attrs.getValue("type");
                    if ( temp != null && temp.equals("xsd:base64Binary") ){
                        isBase64 = true;
                    } else {
                        isBase64 = false;
                    }
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete assertion tag");
                break;
            //Tags with multiple <value> tags embedded
            case ATTRIBUTES:
                //list of attribute names
                if (tag == ATTRIBUTE){
                    //add a search attributes name
                    attributeList.add(attrs.getValue("name"));
                    state = tag;
                }
                else //I may not have to check for this if decide to validate
                    throw new SAXException("incomplete attributes tag");

                break;
            case FILTER:
                break;
            case SUBSTRINGS:
                break;
            case FINAL:
                break;
            case EXTENDED_REQUEST:
                if (tag == X_NAME || tag == X_VALUE){
                    state = tag;
                    value = new StringBuffer();
                }
                break;
            case VALUE_COMPLETE:
                if (tag == VALUE){
                    value = new StringBuffer();
                    state = tag;
                }
                break;
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
        switch (state){
            case X_NAME:
            case X_VALUE:
            case VALUE:
                value.append(a, s, l);
                break;
        }
        return;
    }

    // SAX calls this method when the end-tag for an element is encountered
    public void endElement(String strNamespaceURI,
                           String strLocalName,
                           String strQName) throws SAXException
    {

        Integer elementTag = (Integer)requestTags.get(strQName);
        if (elementTag == null){
            throw new SAXException("Element name, \"" + strQName
                                + "\" not recognized");
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
                    message = new LDAPSearchRequest(dn, scope, filter,
                                (String[]) attributeList.toArray(
                                        new String[ attributeList.size() ] ),
                                typesOnly, searchCons );
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
                case MODIFY_REQUEST:
                    //queue up modify
                    state = BATCH_REQUEST;
                    message = new LDAPModifyRequest(
                                dn,
                                (LDAPModification[])modlist.toArray(
                                        new LDAPModification[ modlist.size() ]),
                                null);
                    queue.add(message);
                    break;
                case MODIFICATION:
                    //store each modify in 'list'
                    {
                        state = MODIFY_REQUEST;
                        LDAPModification mod =
                            new LDAPModification( operation,
                                new LDAPAttribute(attrName,
                                (String[]) attributeList.toArray(
                                        new String[ attributeList.size()])));
                        modlist.add(mod);
                    }
                    break;
                case MODIFY_DN_REQUEST:
                    //queue up modify
                    state = BATCH_REQUEST;
                    message = new LDAPModifyDNRequest( dn, newRDN, newSuperior,
                            deleteOldRDN, null);
                    queue.add(message);
                    break;
                case ADD_REQUEST:
                    //queue up add
                    state = BATCH_REQUEST;
                    entry = new LDAPEntry(dn, attrSet);
                    message = new LDAPAddRequest( entry, null );
                    queue.add(message);
                    break;
                case DELETE_REQUEST:
                    //queue up delete
                    state = BATCH_REQUEST;
                    message = new LDAPDeleteRequest(dn, null);
                    queue.add(message);
                    break;
                case COMPARE_REQUEST:
                    //queue up compare
                    state = BATCH_REQUEST;
                    try {
                        if (isBase64){
                            Base64Decoder decode = new Base64Decoder();
                            message = new LDAPCompareRequest(dn, attrName,
                                    decode.decoder(value, 0, value.length()),
                                    null);
                        }
                        message = new LDAPCompareRequest(dn, attrName,
                                value.toString().getBytes("UTF8"), null);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(
                                "UTF8 not supported by JVM: " + e);
                    }
                    queue.add(message);
                    break;
                case ASSERTION:
                    //attrs is already complete.
                    state = COMPARE_REQUEST;
                    break;
                case ADD_ATTRIBUTE:
                    {
                        byte[] byteValue;
                        if (isBase64){
                            Base64Decoder decode = new Base64Decoder();
                            byteValue = decode.decoder(value, 0, value.length());
                        } else {
                            try {
                                byteValue = value.toString().getBytes("UTF8");
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(
                                        "UTF8 encoding not supported:" + e );
                            }
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
                case FILTER:
                    state = SEARCH_REQUEST;
                    //RfcFilter filter = new RfcFilter(
                    break;
                case EXTENDED_REQUEST:
                    //queue up x-operation
                    message = new LDAPExtendedRequest(
                            new com.novell.ldap.LDAPExtendedOperation(
                                    requestName, requestValue ),
                            null);
                    queue.add(message);
                    state = BATCH_REQUEST;
                    break;
                case X_NAME:
                    state = EXTENDED_REQUEST;
                    requestName = value.toString();
                    break;
                case X_VALUE:
                    {
                        state = EXTENDED_REQUEST;
                        Base64Decoder decode = new Base64Decoder();
                        requestValue = decode.decoder(value, 0, value.length());
                        break;
                    }
                case VALUE:
                    state = VALUE_COMPLETE;
                    attributeList.add(value.toString());
                    break;
            }
        } catch (LDAPException e) {
            throw new SAXException(e);
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

