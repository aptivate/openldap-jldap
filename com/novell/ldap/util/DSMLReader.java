
package com.novell.ldap.ldif_dsml;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import com.novell.ldap.*;

import java.util.Iterator;
import java.util.Enumeration;
import java.io.IOException;

import org.xml.sax.*;
import javax.xml.parsers.*;

public class DSMLReader implements LDAPReader {

    private int messageIndex = 0;
    private DSMLHandler handler = new DSMLHandler();

    /** this method is for testing only */
    public static void main(String[] args) throws Exception
    {
        if( args.length == 0) {
            System.out.println("usage: DSMLParser file");
            System.exit(1);
        }
        // Create SAX Handler
        String DSMLFilePath = args[0];
        DSMLReader reader = new DSMLReader( DSMLFilePath );
        LDAPMessage message = reader.readOperation();
        System.out.println(message);
        //handler.queue should be populated with LDAPMessages after parsing
        try {
            LDAPConnection conn = new LDAPConnection();
            conn.connect( "151.155.155.13", 389);

            LDAPSearchQueue queue =
                    conn.applyToDIT( message, null, null);

            while (( message = queue.getResponse()) != null ) {
                // the message is a search result reference
                if ( message instanceof LDAPSearchResultReference ) {
                    String urls[] =
                        ((LDAPSearchResultReference)message).getReferrals();
                    System.out.println("Search result references:");
                        for ( int i = 0; i < urls.length; i++ )
                            System.out.println(urls[i]);
                }
                // the message is a search result
                else if ( message instanceof LDAPSearchResult ) {
                    LDAPEntry entry = ((LDAPSearchResult)message).getEntry();

                    System.out.println("\n" + entry.getDN());
                    System.out.println("\tAttributes: ");

                    LDAPAttributeSet attributeSet = entry.getAttributeSet();
                    Iterator allAttributes = attributeSet.iterator();

                    while(allAttributes.hasNext()) {
                        LDAPAttribute attribute =
                                (LDAPAttribute)allAttributes.next();
                        String attributeName = attribute.getName();

                        System.out.println("\t\t" + attributeName);

                        Enumeration allValues = attribute.getStringValues();

                        if( allValues != null) {
                            while(allValues.hasMoreElements()) {
                               String Value =
                                    (String) allValues.nextElement();
                               System.out.println("\t\t\t" + Value);
                            }
                        }
                    }
                }
                // the message is a search response
                else {
                    LDAPResponse response = (LDAPResponse)message;
                    int status = response.getResultCode();
                    // the return code is LDAP success
                    int type = response.getType();
                    if (type==LDAPMessage.SEARCH_RESULT){
                        System.out.print("Search ");
                    }
                    if ( status == LDAPException.SUCCESS ) {

                        System.out.println("Operation succeeded.");
                    }
                    // the reutrn code is referral exception
                    else if ( status == LDAPException.REFERRAL ) {
                        String urls[]=((LDAPResponse)message).getReferrals();
                        System.out.println("Referrals:");
                        for ( int i = 0; i < urls.length; i++ )
                            System.out.println(urls[i]);
                    }
                    else {
                        System.out.println("Operation failed.");
                        throw new LDAPException( response.getErrorMessage(),
                                                 status,
                                                 response.getMatchedDN());
                    }
                }
            }
            // disconnect with the server
            conn.disconnect();
        }
        catch( LDAPException e ) {
            System.out.println( "Error: " + e.toString() );
        }
        return;
    }

    public DSMLReader (String dsmlFile)
                throws LDAPLocalException, java.io.FileNotFoundException
    {
        this( new java.io.BufferedInputStream( new java.io.FileInputStream(dsmlFile)) );
    }
    public DSMLReader (java.io.InputStream inputStream) throws LDAPLocalException {
        // Create an XML Parser

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            //spf.setNamespaceAware(true);
            //spf.setValidating(true);

            XMLReader parser = null;
            SAXParser saxParser = spf.newSAXParser();
            parser = saxParser.getXMLReader();
            // assign the handler to the parser
            parser.setContentHandler(handler);
            // parse the document

            InputSource is = new InputSource(inputStream);
            parser.parse(is);
        } catch (FactoryConfigurationError e) {
            throw new LDAPLocalException(
                    "The SAX parser factory is configured incorrectly:" + e,
                    LDAPException.LOCAL_ERROR);
        } catch (ParserConfigurationException e) {
            throw new LDAPLocalException(
                    "The SAX parser is configured incorrectly:" + e,
                    LDAPException.LOCAL_ERROR);
        } catch (SAXException e) {
            throw new LDAPLocalException(
                    "The following error occured while parsing DSML: " + e,
                    LDAPException.LOCAL_ERROR);
        } catch (IOException e) {
            throw new LDAPLocalException(
                    "The following error occured while reading DSML: " + e,
                    LDAPException.LOCAL_ERROR);
        }
    }

    public LDAPMessage readOperation() {
        if (this.messageIndex >= handler.queue.size())
            return null;
        return (LDAPMessage) handler.queue.get( this.messageIndex ++ );
    }
}

