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
 * Program to read a file of valid and invalid DNs and check each to see
 * if the invalid ones are detected.
 *
 * File format
 * <dn>
 * <comment>
 *
 *    or
 *
 * <dn>
 * *<comment>
 *
 * Examples:
 * 1.1.1=
 *     // empty value
 * UID=john,smith
 *    * // unescaped ,
 *
 * An asterisk "*" preceding a <comment> indicates this
 * DN is invalid and an error should be produced.
 *
 * Whitespace preceding <comment> is ignored.
 * All characters (including whitespace) are examined for <dn>
 *
 * Comments in the file begin with # in column 1 and are ignored.
 *
 * Usage:
 *   java DN testfile
 */

import com.novell.ldap.*;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class DN
{
    public static void main( String[] args)
    {
        boolean errorResult;     // True if parsing invalid syntax DN
        int dnCount = 0;         // Number of valid DN test cases
        int syntaxCount = 0;     // Number of invalid DN test cases
        int errorCount = 0;      // Number of valid DNs not parsed
        int noExceptionCount = 0;// Number of invalid DNs parsed
        String[] values;
        if( args.length != 1) {
            System.out.println( "Usage: DN test_file");
            System.exit(1);
        }
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader( new FileReader( args[0]));
            String inDN = null;
            String inComment = null;
            while( (inDN = reader.readLine()) != null) {
                if( inDN.length() > 0) {
                    if( inDN.charAt(0) == '#') {
                        continue;   // Skip comments
                    }
                }
                inComment = reader.readLine(); 
                if( inComment == null) {
                    System.out.println( "DN " + inDN + " at line " +
                        reader.getLineNumber() + " has no comment line");
                    break;
                }

                // Skip leading whitespace on the comment
                int i = 0;
                for( i=0; i < inComment.length(); i++) {
                    if( Character.isWhitespace( inComment.charAt(i))) {
                        continue;
                    }
                    break;
                }
                inComment = inComment.substring(i);

                // Check for type of data in the test file
                if( inDN.length() == 0) {
                    // Normal DN to parse
                    dnCount++;
                    errorResult = false;
                } else
                if( inDN.charAt(0) == '#') {
                    // skip comment
                    continue;
                } else
                if( (inComment.length() > 0) && (inComment.charAt(0) == '*')) {
                    // We Should Detect a Syntax Error
                    syntaxCount++;
                    errorResult = true;
                    inComment = inComment.substring(1);
                } else {
                    // Normal DN to parse
                    dnCount++;
                    errorResult = false;
                }
                try {
                    if( LDAPDN.isValid( inDN)) {
                        String indent1 = "Successful parse for ";
                        String indent2 = "                  ";
                        if( errorResult) {
                            System.out.print( "*** Parser allowed invalid DN \"" +
                                    inDN + "\" " + inComment);
                            if( ((i+1) < args.length ) && (args[i+1].length() > 0) ) {
                                if( args[i+1].charAt(0) == '#') { // comment
                                    System.out.println(" " + args[i+1].substring(1));
                                } else {
                                    System.out.println("");
                                }
                            } else {
                                System.out.println("");
                            }
                            indent1 = indent2 = "      ";
                            noExceptionCount++;
                        } else {
                            System.out.println(indent1 + "Dn value \"" + inDN + "\"");
                        }
                        values = LDAPDN.explodeDN( inDN, false);
                        for( int j=0; j<values.length; j++) {
                            if( j == 0) {
                                System.out.println(indent2 +
                                        "Components: \"" + values[j] + "\"");
                            } else {
                                System.out.println(indent2 + 
                                        "            \"" + values[j] + "\"");
                            }
                        }
                        System.out.println("");
                    } else {
                        if( errorResult) {
                            System.out.print( "Expected DN parsing failure \"" +
                                    inDN + "\" " + inComment);
                            if( ((i+1) < args.length ) && (args[i+1].length() > 0) ) {
                                if( args[i+1].charAt(0) == '#') { // comment
                                    System.out.println(" " + args[i+1].substring(1) + "\n");
                                } else {
                                    System.out.println("\n");
                                }
                            } else {
                                System.out.println("\n");
                            }
                        } else {
                            errorCount++;
                            System.out.println( "*** Unexpected DN parsing failure \"" +
                                    inDN + "\" " + inComment + "\n");
                        }
                    }
                } catch (RuntimeException r) {
                    System.out.println("***RuntimeException for \"" + inDN + "\"" +
                    " - " + r.toString() + "\n");
                    r.printStackTrace();
                    if( errorResult) {
                        noExceptionCount++;
                    } else {
                        errorCount++;
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
        System.out.println("");
        System.out.println(" DNs parsed :                " + dnCount);
        System.out.println("    Incorrect parsing :      " + errorCount);
        System.out.println(" DNs with invalid syntax     " + syntaxCount);
        System.out.println("    Bad Syntax not detected: " + noExceptionCount);
        System.exit(0);
    }
}
