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
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/
package com.novell.ldap.extensions;

import com.novell.ldap.*;
import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * Retrieves the replica information from a GetReplicaInfoResponse object.
 *
 *  <p>An object in this class is generated from an ExtendedResponse using the
 *  ExtendedResponseFactory class.</p>
 *
 *  <p>The getReplicaInfoResponse extension uses the following OID:<br>
 *  &nbsp;&nbsp;&nbsp;2.16.840.1.113719.1.27.100.18</p>
 *
 */
public class GetReplicaInfoResponse extends LDAPExtendedResponse {

   // Other info as returned by the server
   private int partitionID;
   private int replicaState;
   private int modificationTime;
   private int purgeTime;
   private int localPartitionID;
   private String partitionDN;
   private int replicaType;
   private int flags;

   /**
    * Constructs an object from the responseValue which contains the
    * replica information.
    *
    * <p>The constructor parses the responseValue which has the following
    * format:<br>
    *  responseValue ::=<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; partitionID &nbsp;&nbsp;&nbsp;        INTEGER<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; replicaState &nbsp;&nbsp;&nbsp;       INTEGER<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; modificationTime &nbsp;&nbsp;&nbsp;   INTEGER<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; purgeTime &nbsp;&nbsp;&nbsp;          INTEGER<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; localPartitionID &nbsp;&nbsp;&nbsp;   INTEGER<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; partitionDN &nbsp;&nbsp;&nbsp;      OCTET STRING<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; replicaType  &nbsp;&nbsp;&nbsp;       INTEGER<br>
    *  &nbsp;&nbsp;&nbsp;&nbsp; flags &nbsp;&nbsp;&nbsp;              INTEGER</p>
    *
    * @exception IOException The response value could not be decoded.
    */
   public GetReplicaInfoResponse (RfcLDAPMessage rfcMessage)
         throws IOException {

        super(rfcMessage);

        // parse the contents of the reply
        byte [] returnedValue = this.getValue();
        if (returnedValue == null)
            throw new IOException("No returned value");

        // Create a decoder object
        LBERDecoder decoder = new LBERDecoder();
        if (decoder == null)
            throw new IOException("Decoding error");

        // Parse the parameters in the order

        ByteArrayInputStream currentPtr = new ByteArrayInputStream(returnedValue);

        // Parse partitionID
        ASN1Integer asn1_partitionID = (ASN1Integer)decoder.decode(currentPtr);
        if (asn1_partitionID == null)
            throw new IOException("Decoding error");

        partitionID = asn1_partitionID.getInt();


        // Parse replicaState
        ASN1Integer asn1_replicaState = (ASN1Integer)decoder.decode(currentPtr);
        if (asn1_replicaState == null)
            throw new IOException("Decoding error");

        replicaState = asn1_replicaState.getInt();

        // Parse modificationTime
        ASN1Integer asn1_modificationTime = (ASN1Integer)decoder.decode(currentPtr);
        if (asn1_modificationTime == null)
            throw new IOException("Decoding error");

        modificationTime = asn1_modificationTime.getInt();

        // Parse purgeTime
        ASN1Integer asn1_purgeTime = (ASN1Integer)decoder.decode(currentPtr);
        if (asn1_purgeTime == null)
            throw new IOException("Decoding error");

        purgeTime = asn1_purgeTime.getInt();

        // Parse localPartitionID
        ASN1Integer asn1_localPartitionID = (ASN1Integer)decoder.decode(currentPtr);
        if (asn1_localPartitionID == null)
            throw new IOException("Decoding error");

        localPartitionID = asn1_localPartitionID.getInt();

        // Parse partitionDN
        ASN1OctetString asn1_partitionDN = (ASN1OctetString)decoder.decode(currentPtr);
        if (asn1_partitionDN == null)
            throw new IOException("Decoding error");

        partitionDN = new String(asn1_partitionDN.getContent());
        if (partitionDN == null)
            throw new IOException("Decoding error");


        // Parse replicaType
        ASN1Integer asn1_replicaType = (ASN1Integer)decoder.decode(currentPtr);
        if (asn1_replicaType == null)
            throw new IOException("Decoding error");

        replicaType = asn1_replicaType.getInt();


        // Parse flags
        ASN1Integer asn1_flags = (ASN1Integer)decoder.decode(currentPtr);
        if (asn1_flags == null)
            throw new IOException("Decoding error");

        flags = asn1_flags.getInt();

   }


   /**
    * Returns the numeric identifier for the naming context (partition).
    *
    * @return Integer value specifying the naming context ID.
    */
   public int getpartitionID() {
        return partitionID;
   }

   /**
    * Returns the current state of the replica.
    *
    * @return Integer value specifying the current state of the replica. See
    * NamingContextConstants class for possible values for this field.
    *
    * @see NamingContextConstants#LDAP_RS_BEGIN_ADD
    * @see NamingContextConstants#LDAP_RS_DEAD_REPLICA
    * @see NamingContextConstants#LDAP_RS_DYING_REPLICA
    * @see NamingContextConstants#LDAP_RS_JS_0
    * @see NamingContextConstants#LDAP_RS_JS_1
    * @see NamingContextConstants#LDAP_RS_JS_2
    * @see NamingContextConstants#LDAP_RS_LOCKED
    * @see NamingContextConstants#LDAP_RS_MASTER_DONE
    * @see NamingContextConstants#LDAP_RS_MASTER_START
    * @see NamingContextConstants#LDAP_RS_SS_0
    * @see NamingContextConstants#LDAP_RS_TRANSITION_ON
    */
   public int getreplicaState() {
        return replicaState;
   }



   /**
    * Returns the time of the most recent modification.
    *
    * @return Integer value specifying the last modification time.
    */
   public int getmodificationTime() {
        return modificationTime;
   }


   /**
    * Returns the most recent time in which all data has been synchronized.
    *
    * @return Integer value specifying the last purge time.
    */
   public int getpurgeTime() {
        return purgeTime;
   }

   /**
    * Returns the local numeric identifier for the replica.
    *
    * @return Integer value specifying the local ID of the partition.
    */
   public int getlocalPartitionID() {
        return localPartitionID;
   }

   /**
    * Returns the distinguished name of the naming context.
    *
    * @return String value specifying the name of the partition read.
    */
   public String getpartitionDN() {
        return partitionDN;
   }

   /**
    *  Returns the replica type.
    *
    * <p>See the NamingContextConstants class for possible values for
    * this field.</p>
    *
    * @return Integer identifying the type of the replica.
    *
    * @see NamingContextConstants#LDAP_RT_MASTER
    * @see NamingContextConstants#LDAP_RT_SECONDARY
    * @see NamingContextConstants#LDAP_RT_READONLY
    * @see NamingContextConstants#LDAP_RT_SUBREF
    * @see NamingContextConstants#LDAP_RT_SPARSE_WRITE
    * @see NamingContextConstants#LDAP_RT_SPARSE_READ
    */
   public int getreplicaType() {
        return replicaType;
   }

   /**
    * Returns flags that specify whether the replica is busy or is a boundary.
    *
    * <p>See the NamingContextConstants class for possible values for
    * this field.</p>
    *
    * @return Integer value specifying the flags for the replica.
    *
    * @see NamingContextConstants#LDAP_DS_FLAG_BUSY
    * @see NamingContextConstants#LDAP_DS_FLAG_BOUNDARY
    */
   public int getflags() {
        return flags;
   }

}
