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
package com.novell.ldap.extensions;

/*
 * public class NamingContextConstants
 */

/**
 * Contains a collection of constants used by the Novell LDAP extensions.
 */
public class NamingContextConstants {

    /**
     * A constant for the createNamingContextRequest OID.
     */
    public static final String CREATE_NAMING_CONTEXT_REQ    = "2.16.840.1.113719.1.27.100.3";

    /**
     * A constant for the createNamingContextResponse OID.
     */
    public static final String CREATE_NAMING_CONTEXT_RES    = "2.16.840.1.113719.1.27.100.4";

    /**
     * A constant for the mergeNamingContextRequest OID.
     */
    public static final String MERGE_NAMING_CONTEXT_REQ     = "2.16.840.1.113719.1.27.100.5";

    /**
     * A constant for the mergeNamingContextResponse OID.
     */
    public static final String MERGE_NAMING_CONTEXT_RES     = "2.16.840.1.113719.1.27.100.6";

    /**
     * A constant for the addReplicaRequest OID.
     */
    public static final String ADD_REPLICA_REQ              = "2.16.840.1.113719.1.27.100.7";

    /**
     * A constant for the addReplicaResponse OID.
     */
    public static final String ADD_REPLICA_RES              = "2.16.840.1.113719.1.27.100.8";

    /**
     * A constant for the refreshServerRequest OID.
     */
    public static final String REFRESH_SERVER_REQ           = "2.16.840.1.113719.1.27.100.9";

    /**
     * A constant for the refreshServerResponse OID.
     */
    public static final String REFRESH_SERVER_RES           = "2.16.840.1.113719.1.27.100.10";

    /**
     * A constant for the removeReplicaRequest OID.
     */
    public static final String DELETE_REPLICA_REQ           = "2.16.840.1.113719.1.27.100.11";

    /**
     * A constant for the removeReplicaResponse OID.
     */
    public static final String DELETE_REPLICA_RES           = "2.16.840.1.113719.1.27.100.12";

    /**
     * A constant for the namingContextEntryCountRequest OID.
     */
    public static final String NAMING_CONTEXT_COUNT_REQ     = "2.16.840.1.113719.1.27.100.13";

    /**
     * A constant for the namingContextEntryCountResponse OID.
     */
    public static final String NAMING_CONTEXT_COUNT_RES     = "2.16.840.1.113719.1.27.100.14";

    /**
     * A constant for the changeReplicaTypeRequest OID.
     */
    public static final String CHANGE_REPLICA_TYPE_REQ      = "2.16.840.1.113719.1.27.100.15";

/**
     * A constant for the changeReplicaTypeResponse OID.
     */
    public static final String CHANGE_REPLICA_TYPE_RES      = "2.16.840.1.113719.1.27.100.16";

    /**
     * A constant for the getReplicaInfoRequest OID.
     */
    public static final String GET_REPLICA_INFO_REQ         = "2.16.840.1.113719.1.27.100.17";

    /**
     * A constant for the getReplicaInfoResponse OID.
     */
    public static final String GET_REPLICA_INFO_RES         = "2.16.840.1.113719.1.27.100.18";

    /**
     * A constant for the listReplicaRequest OID.
     */
    public static final String LIST_REPLICAS_REQ            = "2.16.840.1.113719.1.27.100.19";

    /**
     * A constant for the listReplicaResponse OID.
     */
    public static final String LIST_REPLICAS_RES            = "2.16.840.1.113719.1.27.100.20";

    /**
     * A constant for the receiveAllUpdatesRequest OID.
     */
    public static final String RECEIVE_ALL_UPDATES_REQ      = "2.16.840.1.113719.1.27.100.21";

    /**
     * A constant for the receiveAllUpdatesResponse OID.
     */
    public static final String RECEIVE_ALL_UPDATES_RES      = "2.16.840.1.113719.1.27.100.22";

    /**
     * A constant for the sendAllUpdatesRequest OID.
     */
    public static final String SEND_ALL_UPDATES_REQ         = "2.16.840.1.113719.1.27.100.23";

    /**
     * A constant for the sendAllUpdatesResponse OID.
     */
    public static final String SEND_ALL_UPDATES_RES         = "2.16.840.1.113719.1.27.100.24";

    /**
     * A constant for the requestNamingContextSyncRequest OID.
     */
    public static final String NAMING_CONTEXT_SYNC_REQ      = "2.16.840.1.113719.1.27.100.25";

    /**
     * A constant for the requestNamingContextSyncResponse OID.
     */
    public static final String NAMING_CONTEXT_SYNC_RES      = "2.16.840.1.113719.1.27.100.26";

    /**
     * A constant for the requestSchemaSyncRequest OID.
     */
    public static final String SCHEMA_SYNC_REQ              = "2.16.840.1.113719.1.27.100.27";

    /**
     * A constant for the requestSchemaSyncResponse OID.
     */
    public static final String SCHEMA_SYNC_RES              = "2.16.840.1.113719.1.27.100.28";

    /**
     * A constant for the abortNamingContextOperationRequest OID.
     */
    public static final String ABORT_NAMING_CONTEXT_OP_REQ  = "2.16.840.1.113719.1.27.100.29";

    /**
     * A constant for the abortNamingContextOperationResponse OID.
     */
    public static final String ABORT_NAMING_CONTEXT_OP_RES  = "2.16.840.1.113719.1.27.100.30";

    /**
     * A constant for the getContextIdentityNameRequest OID.
     */
    public static final String GET_IDENTITY_NAME_REQ        = "2.16.840.1.113719.1.27.100.31";

    /**
     * A constant for the getContextIdentityNameResponse OID.
     */
    public static final String GET_IDENTITY_NAME_RES        = "2.16.840.1.113719.1.27.100.32";

    /**
     * A constant for the getEffectivePrivilegesRequest OID.
     */
    public static final String GET_EFFECTIVE_PRIVILEGES_REQ = "2.16.840.1.113719.1.27.100.33";

    /**
     * A constant for the getEffectivePrivilegesResponse OID.
     */
    public static final String GET_EFFECTIVE_PRIVILEGES_RES = "2.16.840.1.113719.1.27.100.34";

    /**
     * A constant for the setReplicationFilterRequest OID.
     */
    public static final String SET_REPLICATION_FILTER_REQ   = "2.16.840.1.113719.1.27.100.35";

    /**
     * A constant for the setReplicationFilterResponse OID.
     */
    public static final String SET_REPLICATION_FILTER_RES   = "2.16.840.1.113719.1.27.100.36";

    /**
     * A constant for the getReplicationFilterRequest OID.
     */
    public static final String GET_REPLICATION_FILTER_REQ   = "2.16.840.1.113719.1.27.100.37";

    /**
     * A constant for the getReplicationFilterResponse OID.
     */
    public static final String GET_REPLICATION_FILTER_RES   = "2.16.840.1.113719.1.27.100.38";

    /**
     * A constant for the createOrphanNamingContextRequest OID.
     */
    public static final String CREATE_ORPHAN_NAMING_CONTEXT_REQ     = "2.16.840.1.113719.1.27.100.39";

    /**
     * A constant for the createOrphanNamingContextResponse OID.
     */
    public static final String CREATE_ORPHAN_NAMING_CONTEXT_RES     = "2.16.840.1.113719.1.27.100.40";

    /**
     * A constant for the removeOrphanNamingContextRequest OID.
     */
    public static final String REMOVE_ORPHAN_NAMING_CONTEXT_REQ     = "2.16.840.1.113719.1.27.100.41";

    /**
     * A constant for the removeOrphanNamingContextResponse OID.
     */
    public static final String REMOVE_ORPHAN_NAMING_CONTEXT_RES     = "2.16.840.1.113719.1.27.100.42";

    /**
     * A constant for the triggerBackLinkerRequest OID.
     */
    public static final String TRIGGER_BKLINKER_REQ     = "2.16.840.1.113719.1.27.100.43";

    /**
     * A constant for the triggerBackLinkerResponse OID.
     */
    public static final String TRIGGER_BKLINKER_RES     = "2.16.840.1.113719.1.27.100.44";

    /**
     * A constant for the triggerJanitorRequest OID.
     */
    public static final String TRIGGER_JANITOR_REQ      = "2.16.840.1.113719.1.27.100.47";

    /**
     * A constant for the triggerJanitorResponse OID.
     */
    public static final String TRIGGER_JANITOR_RES     = "2.16.840.1.113719.1.27.100.48";

    /**
     * A constant for the triggerLimberRequest OID.
     */
    public static final String TRIGGER_LIMBER_REQ       = "2.16.840.1.113719.1.27.100.49";

    /**
     * A constant for the triggerLimberResponse OID.
     */
    public static final String TRIGGER_LIMBER_RES     = "2.16.840.1.113719.1.27.100.50";

    /**
     * A constant for the triggerSkulkerRequest OID.
     */
    public static final String TRIGGER_SKULKER_REQ      = "2.16.840.1.113719.1.27.100.51";

    /**
     * A constant for the triggerSkulkerResponse OID.
     */
    public static final String TRIGGER_SKULKER_RES     = "2.16.840.1.113719.1.27.100.52";

    /**
     * A constant for the triggerSchemaSyncRequest OID.
     */
    public static final String TRIGGER_SCHEMA_SYNC_REQ      = "2.16.840.1.113719.1.27.100.53";

    /**
     * A constant for the triggerSchemaSyncResponse OID.
     */
    public static final String TRIGGER_SCHEMA_SYNC_RES     = "2.16.840.1.113719.1.27.100.54";

    /**
     * A constant for the triggerPartitionPurgeRequest OID.
     */
    public static final String TRIGGER_PART_PURGE_REQ       = "2.16.840.1.113719.1.27.100.55";

    /**
     * A constant for the triggerPartitionPurgeResponse OID.
     */
    public static final String TRIGGER_PART_PURGE_RES     = "2.16.840.1.113719.1.27.100.56";
    
    /**
     * A constant for the DNStoX500DNRequest OID.
     */
    public static final String LDAP_DNS_TO_X500_DN_EXTENDED_REQUEST     = "2.16.840.1.113719.1.27.100.101";
    
    /**
     * A constant for the DNStoX500DNResponse OID.
     */
    public static final String LDAP_DNS_TO_X500_DN_EXTENDED_REPLY     = "2.16.840.1.113719.1.27.100.102";

    /**
     * A constant that specifies that all servers in a replica ring must be
     * running for a naming context operation to proceed.
     */
    public static final int LDAP_ENSURE_SERVERS_UP = 1;


    /**
     * Identifies this replica as the master replica of the naming context.
     *
     * <p>On this type of replica, entries can be modified, and naming context
     * operations can be performed.</p>
     */
    public static final int LDAP_RT_MASTER          = 0;

    /**
     * Identifies this replica as a secondary replica of the naming context.
     *
     * <p>On this type of replica, read and write operations can be performed,
     *  and entries can be modified.</p>
     */
    public static final int LDAP_RT_SECONDARY       = 1;

    /**
     * Identifies this replica as a read-only replica of the naming context.
     *
     * <p>Only Novell eDirectory synchronization processes can modifie
     * entries on this replica.</p>
     */
    public static final int LDAP_RT_READONLY        = 2;

    /**
     * Identifies this replica as a subordinate reference replica of the
     * naming context.
     *
     * <p>Novell eDirectory automatically adds these replicas to a server
     * when the server does not contain replicas of all child naming contexts.
     * Only eDirectory can modify information on these types of replicas. </p>
     */
    public static final int LDAP_RT_SUBREF          = 3;

    /**
     * Identifies this replica as a read/write replica of the naming context,
     * but the replica contains sparse data.
     *
     * <p>The replica has been configured to contain only specified object types
     * and attributes. On this type of replica, only the attributes and objects
     * contained in the sparse data can be modified.</p>
     */
    public static final int LDAP_RT_SPARSE_WRITE    = 4;

    /**
     * Identifies this replica as a read-only replica of the naming context,
     * but the replica contains sparse data.
     *
     * <p>The replica has been configured to contain only specified object types
     * and attributes. On this type of replica, only Novell eDirectory
     * synchronization processes can modify the sparse data.</p>
     */
    public static final int LDAP_RT_SPARSE_READ     = 5;

    //Replica States

    /**
     * Indicates that the replica is fully functioning and capable of responding
     * to requests.
     */
    public static final int LDAP_RS_ON              = 0;

    /**
     * Indicates that a new replica has been added but has not received a full
     * download of information from the replica ring.
     */
    public static final int LDAP_RS_NEW_REPLICA     = 1;

    /**
     * Indicates that the replica is being deleted and that the request has
     * been received.
     */
    public static final int LDAP_RS_DYING_REPLICA   = 2;

    /**
     * Indicates that the replica is locked. The move operation uses this state
     * to lock the parent naming context of the child naming context that is moving.
     */
    public static final int LDAP_RS_LOCKED          = 3;

    /**
     * Indicates that a new replica has finished receiving its download from the
     * master replica and is now receiving synchronization updates from other
     * replicas.
     */
    public static final int LDAP_RS_TRANSITION_ON   = 6;


    /**
     * Indicates that the dying replica needs to synchronize with another replica
     * before being converted either to an external reference, if a root replica,
     * or to a subordinate reference, if a non-root replica.
     */
     public static final int LDAP_RS_DEAD_REPLICA    = 7;

    /**
     * Indicates that the subordinate references of the new replica are being
     * added.
     */
    public static final int LDAP_RS_BEGIN_ADD       = 8;

    /**
     * Indicates that a naming context is receiving a new master replica.
     *
     * <p>The replica that will be the new master replica is set to this state.</p>
     */
    public static final int LDAP_RS_MASTER_START    = 11;

    /**
     * Indicates that a naming context has a new master replica.
     *
     * <p>When the new master is set to this state, Novell eDirectory knows
     * that the replica is now the master and changes its replica type to
     * master and the old master to read/write.</p>
     */
    public static final int LDAP_RS_MASTER_DONE     = 12;

    /**
     * Indicates that the naming context is going to split into two naming contexts.
     *
     * <p>In this state, other replicas of the naming context are informed of the
     * pending split.</p>
     */
    public static final int LDAP_RS_SS_0            = 48;   // Replica splitting 0

    /**
     * Indicates that that the split naming context operation has started.
     *
     * <p>When the split is finished, the state will change to RS_ON.</p>
     */
    public static final int LDAP_RS_SS_1            = 49;   // Replica splitting 1

    /**
     * Indicates that that two naming contexts are in the process of joining
     * into one naming context.
     *
     * <p>In this state, the replicas that are affected are informed of the join
     * operation. The master replica of the parent and child naming contexts are
     * first set to this state and then all the replicas of the parent and child.
     * New replicas are added where needed.</p>
     */
    public static final int LDAP_RS_JS_0            = 64;   // Replica joining 0

    /**
     * Indicates that that two naming contexts are in the process of joining
     * into one naming context.
     *
     * <p>This state indicates that the join operation is waiting for the new
     * replicas to synchronize and move to the RS_ON state.</p>
     */
    public static final int LDAP_RS_JS_1            = 65;   // Replica joining 1

    /**
     * Indicates that that two naming contexts are in the process of joining
     * into one naming context.
     *
     * <p>This state indicates that all the new replicas are in the RS_ON state
     * and that the rest of the work can be completed.</p>
     */
    public static final int LDAP_RS_JS_2            = 66;   // Replica joining 2
 

    // Values for flags used in the replica info class structure

    /**
     * Indicates that the replica is involved with a partition operation,
     * for example, merging a tree or moving a subtree.
     */
    public static final int LDAP_DS_FLAG_BUSY       = 0x0001;

    /**
     * Indicates that this naming context is on the DNS federation boundary.
     * This flag is only set on DNS trees.
     */
    public static final int LDAP_DS_FLAG_BOUNDARY   = 0x0002;


    public NamingContextConstants()  {}
}
