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
/*
 * Program to read a file of filters and expected generated values
 * It checks to see if the correct values are generated
 *
 * File format
 * <filter>
 * <value>
 *
 * Example:
 * (attr=*)
 *    87 04 61 74 74 72
 *
 * Whitespace preceding <value> is ignored.
 * All characters (including whitespace) are examined for <filter>
 *
 * Comments in the file begin with # in column 1 and are ignored.
 *
 * Usage:
 *   java Filter testfile
 */
import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Filter
{
    public static void main( String[] args)
    {
        boolean hexResult;
        boolean errorResult;
        int filterCount = 0;
        int syntaxCount = 0;
        int errorCount = 0;
        int noExceptionCount = 0;
        if( args.length != 1) {
            System.out.println( "Usage: Filter test_file");
            System.exit(1);
        }
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader( new FileReader( args[0]));
            String inFilter;
            String inHex;
            while( (inFilter = reader.readLine()) != null) {
                if( inFilter.length() > 0) {
                    if( inFilter.charAt(0) == '#') {
                        continue;   // Skip comments
                    }
                }
                inHex = reader.readLine(); 
                if( inHex == null) {
                    System.out.println( "Filter " + inFilter + " at line " +
                        reader.getLineNumber() + " has no hex value");
                    break;
                }

                // Skip leading whitespace on the hex filter value
                int i = 0;
                for( i=0; i < inHex.length(); i++) {
                    if( Character.isWhitespace( inHex.charAt(i))) {
                        continue;
                    }
                    break;
                }
                if( i >= inHex.length()) {
                    System.out.println( "Filter " + inFilter + " at line " +
                        reader.getLineNumber() + " has an empty hex value");
                    continue;
                }
                inHex = inHex.substring(i);

                // Check for filters that should generate errors
                if( inHex.charAt(0) == '*') {
                    filterCount++;
                    hexResult = false;
                    errorResult = true;
                } else {
                    syntaxCount++;
                    hexResult = true;
                    errorResult = false;
                }

                // Encode the filter to ASN1
                StringBuffer buf = new StringBuffer( inFilter.length() * 3);
                try {
                    RfcFilter filter = new RfcFilter(inFilter);
                    ASN1Object o = filter.getContent();
                    byte[] encoded = o.getEncoding(new LBEREncoder());
                    for( int j=0; j<encoded.length; j++) {
                        int val = encoded[j] & 0xff;
                        if( val < 0x10) {
                            buf.append("0" + Integer.toString(val, 16));
                        } else {
                            buf.append(Integer.toString(val, 16));
                        }
                        if( (j+1) != encoded.length) {
                            buf.append(" ");
                        }
                    }

                    // Verify encoded filter with expected value
                    if( inHex.equals( buf.toString()) ) {
                        System.out.println("Filter matches \"" +
                                    inFilter + "\"\n");
                    } else {
                        if( hexResult) {
                            errorCount++;
                            System.out.println( "*** Filter MISMATCH \"" +
                                    inFilter + "\"");
                            System.out.println( "       Expected : " + inHex);
                            System.out.println( "       Generated: " +
                                    buf.toString() + "\n");
                        } else {
                            noExceptionCount++;
                            System.out.println(
                                    "*** No filter exception generated \""
                                    + inFilter + "\"\n");
                            System.out.println( "       Generated: " +
                                    buf.toString() + "\n");
                        }
                    }
                } catch( LDAPException e) {
                    if( errorResult) {
                        System.out.println( "Expected filter exception \"" +
                                inFilter + "\"" + e.toString() + "\n");
                    } else {
                        errorCount++;
                        System.out.println( 
                                "*** Unexpected filter Exception \"" +
                                inFilter + "\"");
                        System.out.println( e.toString() + "\n");
                    }
                }
            }        
        } catch( FileNotFoundException ex) {
            System.out.println("File \"" + args[0] + "\" not found");
        } catch( IOException ioe) {
            System.out.println("Error reading line " + reader.getLineNumber() +
                    " from file \"" + args[0] + "\"");
            System.out.println("        " + ioe.toString());
        }

        // Print statistics
        System.out.println("");
        System.out.println(" Filters encoded:            " + filterCount);
        System.out.println("    Incorrect encoding:      " + errorCount);
        System.out.println(" Filters with invalid syntax " + syntaxCount);
        System.out.println("    Bad Syntax not detected: " + noExceptionCount);
        System.exit(0);
    }
}
