/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2003 Novell, Inc. All Rights Reserved.
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
package com.novell.security.sasl;


import java.io.*;

/**
 * This class represents Exception thrown on errors and failures that occur
 * when using SASL.
 */
public class SaslException extends IOException
{

    private Throwable exception;


    /**
     * Constructs a new instance of SaslException. The root exception and
     * the detailed message are null.
     */
    public SaslException() {
           super();
       }

    /**
     * Constructs a default exception with a detailed message and no root
     * exception.
     * @param message A possibly null string containing details of the exception
     */
    public SaslException(String message) {
                 super(message);
      }

    /**
     * Constructs a new instance of SaslException with a detailed message and a
     * root exception.
     * <p> For example, a SaslException might result from a problem with the
     * callback handler, which might throw a NoSuchCallbackException if it does
     * not support the requested callback, or throw an IOException if it had
     * problems obtaining data for the callback. The SaslException's root
     * exception would then be the exception thrown by the callback handler. 
     *
     * @param message    Possibly null additional detail about the exception. 
     * @param ex         A possibly null root exception that caused this
     */
    public SaslException(String message, Throwable ex) {
            super(message);
            exception = ex;
    }
    /**
     * Returns the cause of this exception or null if the cause is nonexistent
     * or unknown. The cause is the throwable that caused this exception to be
     * thrown. 
     * @return The possibly null exception that caused this exception.
     */
    public Throwable   getCause()         {
        return exception;
    }

    /**
     * Prints this exception's stack trace to System.err. If this exception
     * has a root exception, the stack trace of the root exception is also
     *   printed to System.err.
     */
    public void    printStackTrace() {
        printStackTrace( System.err );
    }

    /**
     * Prints this exception's stack trace to a print stream. If this
     * exception has a root exception, the stack trace of the root exception
     * is also printed to the print stream.
     * @param ps   The non-null print stream to which to print.
     */
    public void printStackTrace(PrintStream ps) {
        if ( exception != null ) {
        synchronized ( ps ) {
            super.printStackTrace(ps);
            ps.print("Exception caused by:");
            exception.printStackTrace( ps );
            }
        } else
        super.printStackTrace( ps );
    }

    /**
     * Prints this exception's stack trace to a print writer. If this
     * exception has a root exception, the stack trace of the root exception
     * is also printed to the print writer.
     * @param pw The non-null print writer to which to print.
     */
    public void    printStackTrace(PrintWriter pw) {
            if ( exception != null ) {
                synchronized (pw) {
                super.printStackTrace(pw);
                pw.print("Exception caused by:");
                exception.printStackTrace( pw );
            }
        } else
            super.printStackTrace( pw );
    }

   /**
    * Returns the string representation of this exception.
    * @return The non-null string representation of this exception
    */
   public String toString() {
        String answer = super.toString();
        if (exception != null && exception != this) {
            answer += " [Exception caused by : " + exception.toString() + "]";
        }
        return answer;
    }
}
