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

package com.novell.ldap.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *  A utility class to get strings from the ExceptionMessages and
 *  ResultCodeMessages resources.
 */
public class ResourcesHandler
{
    // Cannot create an instance of this class
    private ResourcesHandler()
    {
        return;
    }

    /*
     *  Initialized when the first result string is requested
     */
    private static ResourceBundle defaultResultCodes = null;

    /**
     *  Initialized when the first Exception message string is requested
     */
    private static ResourceBundle defaultMessages = null;


    /**
     * Package where resources are found
     */
    private static String pkg = "com.novell.ldap.resources.";

    /**
     * The default Locale
     */
    private static Locale defaultLocale = Locale.getDefault();

    /**
     * Returns a string using the MessageOrKey as a key into
     * ExceptionMessages or, if the Key does not exist, returns the
     * string messageOrKey.  In addition it formats the arguments into the message
     * according to MessageFormat.
     *
     * @param messageOrKey    Key string for the resource.
     *<br><br>
     * @param arguments
     *
     * @return the text for the message specified by the MessageKey or the Key
     *         if it there is no message for that key.
     */
    public static String getMessage( String messageOrKey, Object[] arguments)
    {
        return getMessage( messageOrKey, arguments, null);
    }

    /**
     * Returns the message stored in the ExceptionMessages resource for the
     * specified locale using messageOrKey and argments passed into the
     * constructor.  If no string exists in the resource then this returns
     * the string stored in message.  (This method is identical to
     * getLDAPErrorMessage(Locale locale).)
     *
     * @param messageOrKey    Key string for the resource.
     *<br><br>
     * @param arguments
     * <br><br>
     * @param locale          The Locale that should be used to pull message
     *                        strings out of ExceptionMessages.
     *
     * @return the text for the message specified by the MessageKey or the Key
     *         if it there is no message for that key.
     */
    public static String getMessage( String messageOrKey,
                                     Object[] arguments,
                                     Locale locale)
    {
        String pattern;
        ResourceBundle messages = null;
        
        if( messageOrKey == null) {
            messageOrKey = "";
        }

        try {
            if( (locale == null) || defaultLocale.equals( locale) ) {
                locale = defaultLocale;
                // Default Locale
                if( defaultMessages == null) {
                    defaultMessages =  ResourceBundle.getBundle(
                        pkg + "ExceptionMessages", defaultLocale);
                }
                messages = defaultMessages;
            } else {
                messages =  ResourceBundle.getBundle(
                    pkg + "ExceptionMessages", locale);
            }
            pattern = messages.getString(messageOrKey);
        } catch (MissingResourceException mre){
            pattern = messageOrKey;
        }

        // Format the message if arguments were passed
        if (arguments != null) {
            MessageFormat mf = new MessageFormat(pattern);
            mf.setLocale(locale);
            //this needs to be reset with the new local - i18n defect in java
            mf.applyPattern(pattern);
            pattern = mf.format(arguments);
        }
        return pattern;
    }

    /**
     * Returns a string representing the LDAP result code from the 
     * default ResultCodeMessages resource.
     *
     * @param code    the result code 
     *<br><br>
     * @return        the String representing the result code.
     */
    public static String getResultString( int code)
    {
       return getResultString( code, null);
    }

    /**
     * Returns a string representing the LDAP result code.  The message
     * is obtained from the locale specific ResultCodeMessage resource.
     *
     * @param code    the result code 
     * <br><br>
     * @param locale          The Locale that should be used to pull message
     *                        strings out of ResultMessages.
     *
     * @return        the String representing the result code.
     */
    public static String getResultString( int code, Locale locale)
    {
        ResourceBundle messages;
        String result;

        try {
            if( (locale == null) || defaultLocale.equals( locale) ) {
                locale = defaultLocale;
                // Default Locale
                if (defaultResultCodes == null) {
                    defaultResultCodes = ResourceBundle.getBundle(
                                pkg + "ResultCodeMessages", defaultLocale);
                }
                messages = defaultResultCodes;
            } else {
                messages =  ResourceBundle.getBundle(
                        pkg + "ResultCodeMessages", locale);
            }
            result = messages.getString(Integer.toString(code));
        } catch (MissingResourceException mre){
            result = getMessage( ExceptionMessages.UNKNOWN_RESULT,
                                 new Object[] { new Integer(code) },
                                 locale);
        }
        return result;
    }
}//end class ResourcesHandler
