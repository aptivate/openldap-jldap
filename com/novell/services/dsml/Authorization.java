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
package com.novell.services.dsml;
import javax.servlet.http.HttpServletRequest;
import com.novell.ldap.util.Base64;

/**
 * Authorization - grab authorization information out of HTTPServletRequest.
 */
public class Authorization
{
    String auth = null;
    String password = null;
    String dn = null;

    /**
     * Creates an anonymously authorized object.
     */
    public Authorization(){
        super();
    }

    /**
     * Authorization.
     *
     * <p> Parse an HttpServletRequest and get the Authorization DN and password.
     * </p>
     * @param req HttpServletRequest.
     */
    public Authorization(HttpServletRequest req)
    {
        // Get Authorization header
        String tmp = req.getHeader("Authorization");
        // If there is Authorization header
        if(null != tmp && 0 != tmp.length())
        {
            tmp = tmp.trim();
            if(null != tmp && 0 != tmp.length())
            {
                // See if it is base 64.
                // This is the only one we handle right now.
                if (tmp.startsWith("Basic "))
                {
                    // Remove the Basic string.
                    tmp = tmp.substring(6);
                    if(null != tmp && 0 != tmp.length())
                    {
                        // Do base 64 decoding.
                        tmp = new String(Base64.decode(tmp));
                        if(null != tmp && 0 != tmp.length())
                            auth = tmp;
                    }
                }
            }
        }

        if(null != auth)
        {
            int i = auth.indexOf( ':' );
            // If No colon,
            if ( i < 0 )
            {
                // Then auth is all DN
                dn = auth;
            }
            else
            {
                dn = auth.substring( 0, i);
                password = auth.substring(i+1);
                password = password.trim();
            }
            dn = dn.trim();

        }
        // Set password to null if empty
        if(password != null && password.length() == 0)
            password = null;
        // Set dn to null if empty
        if(dn != null && dn.length() == 0)
            dn = null;
    }


    /**
     * getAuth.
     *
     * <p> Get authorization string.</p>
     */
    public String getAuth()
    {
        return auth;
    }

    /**
     * getDN.
     *
     * <p> Get DN string.</p>
     */
    public String getDN()
    {
        return dn;
    }

    /**
     * getPassword.
     *
     * <p> Get password string.</p>
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * getPasswordBytes.
     *
     * <p> Get password as bytes.</p>
     */
    public byte[] getPasswordBytes()
    {
        if(null == password)
            return null;
        else
            return password.getBytes();
    }

}

