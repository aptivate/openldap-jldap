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

package com.novell.ldap;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

import java.util.Enumeration;
import java.util.Iterator;
import com.novell.ldap.resources.ExceptionMessages;

/**
 * Creates the LburpRequest
 */

public class LDAPLburpRequest{

    LDAPMessage  request;
    public int opType;
    
    public LDAPLburpRequest( LDAPMessage arequest)
    {
        request = arequest;
        opType = request.getASN1Object().getType();
    }
    
    public ASN1Sequence getRequestASN1Sequence()
    {
        ASN1Sequence rSeq=null;
        RfcLDAPMessage mess=request.getASN1Object();
        switch( mess.getType() ) {
        
            case LDAPMessage.ADD_REQUEST:
                RfcAddRequest areq = (RfcAddRequest)mess.getRequest();
                rSeq = (ASN1Sequence)areq;
                break;
                
            case LDAPMessage.MODIFY_REQUEST:
                RfcModifyRequest mreq = (RfcModifyRequest)mess.getRequest();
                rSeq = (ASN1Sequence)mreq;
                break;
                
            case LDAPMessage.MODIFY_RDN_REQUEST:
                RfcModifyDNRequest nreq = (RfcModifyDNRequest)mess.getRequest();
                rSeq = (ASN1Sequence)nreq;
                break;
                
            default:
                break;
        }
        return rSeq;
    }

    public ASN1OctetString getRequestASN1OcString()
    {
        ASN1OctetString rStr=null;
        RfcLDAPMessage mess=request.getASN1Object();
        if( mess.getType() == LDAPMessage.DEL_REQUEST )
        {
            RfcDelRequest dreq = (RfcDelRequest)mess.getRequest();
            rStr = (ASN1OctetString)dreq;
        }
        return rStr;
    }
        
    public ASN1SequenceOf getControlsASN1Object()
    {
        return request.getASN1Object().getControls();
    }
}

