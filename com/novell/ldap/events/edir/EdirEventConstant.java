/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999-2002 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.events.edir;

/**
 * This interface defines the various constants used in this package.  See the
 * Events Documentation for each event and specific data.
 */
public interface EdirEventConstant {
    public static final int EVT_STATUS_ALL = 0;
    public static final int EVT_STATUS_SUCCESS = 1;
    public static final int EVT_STATUS_FAILURE = 2;
    public static final int EVT_INVALID = 0;
    public static final int EVT_CREATE_ENTRY = 1;
    public static final int EVT_DELETE_ENTRY = 2;
    public static final int EVT_RENAME_ENTRY = 3;
    public static final int EVT_MOVE_SOURCE_ENTRY = 4;
    public static final int EVT_ADD_VALUE = 5;
    public static final int EVT_DELETE_VALUE = 6;
    public static final int EVT_CLOSE_STREAM = 7;
    public static final int EVT_DELETE_ATTRIBUTE = 8;
    public static final int EVT_SET_BINDERY_CONTEXT = 9;
    public static final int EVT_CREATE_BINDERY_OBJECT = 10;
    public static final int EVT_DELETE_BINDERY_OBJECT = 11;
    public static final int EVT_CHECK_SEV = 12;
    public static final int EVT_UPDATE_SEV = 13;
    public static final int EVT_MOVE_DEST_ENTRY = 14;
    public static final int EVT_DELETE_UNUSED_EXTREF = 15;
    public static final int EVT_REMOTE_SERVER_DOWN = 17;
    public static final int EVT_NCP_RETRY_EXPENDED = 18;
    public static final int EVT_PARTITION_OPERATION_EVENT = 20;
    public static final int EVT_CHANGE_MODULE_STATE = 21;
    public static final int EVT_DB_AUTHEN = 26;
    public static final int EVT_DB_BACKLINK = 27;
    public static final int EVT_DB_BUFFERS = 28;
    public static final int EVT_DB_COLL = 29;
    public static final int EVT_DB_DSAGENT = 30;
    public static final int EVT_DB_EMU = 31;
    public static final int EVT_DB_FRAGGER = 32;
    public static final int EVT_DB_INIT = 33;
    public static final int EVT_DB_INSPECTOR = 34;
    public static final int EVT_DB_JANITOR = 35;
    public static final int EVT_DB_LIMBER = 36;
    public static final int EVT_DB_LOCKING = 37;
    public static final int EVT_DB_MOVE = 38;
    public static final int EVT_DB_MIN = 39;
    public static final int EVT_DB_MISC = 40;
    public static final int EVT_DB_PART = 41;
    public static final int EVT_DB_RECMAN = 42;
    public static final int EVT_DB_RESNAME = 44;
    public static final int EVT_DB_SAP = 45;
    public static final int EVT_DB_SCHEMA = 46;
    public static final int EVT_DB_SKULKER = 47;
    public static final int EVT_DB_STREAMS = 48;
    public static final int EVT_DB_SYNC_IN = 49;
    public static final int EVT_DB_THREADS = 50;
    public static final int EVT_DB_TIMEVECTOR = 51;
    public static final int EVT_DB_VCLIENT = 52;
    public static final int EVT_AGENT_OPEN_LOCAL = 53;
    public static final int EVT_AGENT_CLOSE_LOCAL = 54;
    public static final int EVT_DS_ERR_VIA_BINDERY = 55;
    public static final int EVT_DSA_BAD_VERB = 56;
    public static final int EVT_DSA_REQUEST_START = 57;
    public static final int EVT_DSA_REQUEST_END = 58;
    public static final int EVT_MOVE_SUBTREE = 59;
    public static final int EVT_NO_REPLICA_PTR = 60;
    public static final int EVT_SYNC_IN_END = 61;
    public static final int EVT_BKLINK_SEV = 62;
    public static final int EVT_BKLINK_OPERATOR = 63;
    public static final int EVT_DELETE_SUBTREE = 64;
    public static final int EVT_REFERRAL = 67;
    public static final int EVT_UPDATE_CLASS_DEF = 68;
    public static final int EVT_UPDATE_ATTR_DEF = 69;
    public static final int EVT_LOST_ENTRY = 70;
    public static final int EVT_PURGE_ENTRY_FAIL = 71;
    public static final int EVT_PURGE_START = 72;
    public static final int EVT_PURGE_END = 73;
    public static final int EVT_LIMBER_DONE = 76;
    public static final int EVT_SPLIT_DONE = 77;
    public static final int EVT_SYNC_SVR_OUT_START = 78;
    public static final int EVT_SYNC_SVR_OUT_END = 79;
    public static final int EVT_SYNC_PART_START = 80;
    public static final int EVT_SYNC_PART_END = 81;
    public static final int EVT_MOVE_TREE_START = 82;
    public static final int EVT_MOVE_TREE_END = 83;
    public static final int EVT_JOIN_DONE = 86;
    public static final int EVT_PARTITION_LOCKED = 87;
    public static final int EVT_PARTITION_UNLOCKED = 88;
    public static final int EVT_SCHEMA_SYNC = 89;
    public static final int EVT_NAME_COLLISION = 90;
    public static final int EVT_NLM_LOADED = 91;
    public static final int EVT_LUMBER_DONE = 94;
    public static final int EVT_BACKLINK_PROC_DONE = 95;
    public static final int EVT_SERVER_RENAME = 96;
    public static final int EVT_SYNTHETIC_TIME = 97;
    public static final int EVT_SERVER_ADDRESS_CHANGE = 98;
    public static final int EVT_DSA_READ = 99;
    public static final int EVT_LOGIN = 100;
    public static final int EVT_CHGPASS = 101;
    public static final int EVT_LOGOUT = 102;
    public static final int EVT_ADD_REPLICA = 103;
    public static final int EVT_REMOVE_REPLICA = 104;
    public static final int EVT_SPLIT_PARTITION = 105;
    public static final int EVT_JOIN_PARTITIONS = 106;
    public static final int EVT_CHANGE_REPLICA_TYPE = 107;
    public static final int EVT_REMOVE_ENTRY = 108;
    public static final int EVT_ABORT_PARTITION_OP = 109;
    public static final int EVT_RECV_REPLICA_UPDATES = 110;
    public static final int EVT_REPAIR_TIME_STAMPS = 111;
    public static final int EVT_SEND_REPLICA_UPDATES = 112;
    public static final int EVT_VERIFY_PASS = 113;
    public static final int EVT_BACKUP_ENTRY = 114;
    public static final int EVT_RESTORE_ENTRY = 115;
    public static final int EVT_DEFINE_ATTR_DEF = 116;
    public static final int EVT_REMOVE_ATTR_DEF = 117;
    public static final int EVT_REMOVE_CLASS_DEF = 118;
    public static final int EVT_DEFINE_CLASS_DEF = 119;
    public static final int EVT_MODIFY_CLASS_DEF = 120;
    public static final int EVT_RESET_DS_COUNTERS = 121;
    public static final int EVT_REMOVE_ENTRY_DIR = 122;
    public static final int EVT_COMPARE_ATTR_VALUE = 123;
    public static final int EVT_STREAM = 124;
    public static final int EVT_LIST_SUBORDINATES = 125;
    public static final int EVT_LIST_CONT_CLASSES = 126;
    public static final int EVT_INSPECT_ENTRY = 127;
    public static final int EVT_RESEND_ENTRY = 128;
    public static final int EVT_MUTATE_ENTRY = 129;
    public static final int EVT_MERGE_ENTRIES = 130;
    public static final int EVT_MERGE_TREE = 131;
    public static final int EVT_CREATE_SUBREF = 132;
    public static final int EVT_LIST_PARTITIONS = 133;
    public static final int EVT_READ_ATTR = 134;
    public static final int EVT_READ_REFERENCES = 135;
    public static final int EVT_UPDATE_REPLICA = 136;
    public static final int EVT_START_UPDATE_REPLICA = 137;
    public static final int EVT_END_UPDATE_REPLICA = 138;
    public static final int EVT_SYNC_PARTITION = 139;
    public static final int EVT_SYNC_SCHEMA = 140;
    public static final int EVT_CREATE_BACKLINK = 141;
    public static final int EVT_CHECK_CONSOLE_OPERATOR = 142;
    public static final int EVT_CHANGE_TREE_NAME = 143;
    public static final int EVT_START_JOIN = 144;
    public static final int EVT_ABORT_JOIN = 145;
    public static final int EVT_UPDATE_SCHEMA = 146;
    public static final int EVT_START_UPDATE_SCHEMA = 147;
    public static final int EVT_END_UPDATE_SCHEMA = 148;
    public static final int EVT_MOVE_TREE = 149;
    public static final int EVT_RELOAD_DS = 150;
    public static final int EVT_ADD_PROPERTY = 151;
    public static final int EVT_DELETE_PROPERTY = 152;
    public static final int EVT_ADD_MEMBER = 153;
    public static final int EVT_DELETE_MEMBER = 154;
    public static final int EVT_CHANGE_PROP_SECURITY = 155;
    public static final int EVT_CHANGE_OBJ_SECURITY = 156;
    public static final int EVT_CONNECT_TO_ADDRESS = 158;
    public static final int EVT_SEARCH = 159;
    public static final int EVT_PARTITION_STATE_CHG = 160;
    public static final int EVT_REMOVE_BACKLINK = 161;
    public static final int EVT_LOW_LEVEL_JOIN = 162;
    public static final int EVT_CREATE_NAMEBASE = 163;
    public static final int EVT_CHANGE_SECURITY_EQUALS = 164;
    public static final int EVT_DB_NCPENG = 166;
    public static final int EVT_CRC_FAILURE = 167;
    public static final int EVT_ADD_ENTRY = 168;
    public static final int EVT_MODIFY_ENTRY = 169;
    public static final int EVT_OPEN_BINDERY = 171;
    public static final int EVT_CLOSE_BINDERY = 172;
    public static final int EVT_CHANGE_CONN_STATE = 173;
    public static final int EVT_NEW_SCHEMA_EPOCH = 174;
    public static final int EVT_DB_AUDIT = 175;
    public static final int EVT_DB_AUDIT_NCP = 176;
    public static final int EVT_DB_AUDIT_SKULK = 177;
    public static final int EVT_MODIFY_RDN = 178;
    public static final int EVT_ENTRYID_SWAP = 181;
    public static final int EVT_INSIDE_NCP_REQUEST = 182;
    public static final int EVT_DB_LOST_ENTRY = 183;
    public static final int EVT_DB_CHANGE_CACHE = 184;
    public static final int EVT_LOW_LEVEL_SPLIT = 185;
    public static final int EVT_DB_PURGE = 186;
    public static final int EVT_END_NAMEBASE_TRANSACTION = 187;
    public static final int EVT_ALLOW_LOGIN = 188;
    public static final int EVT_DB_CLIENT_BUFFERS = 189;
    public static final int EVT_DB_WANMAN = 190;
    public static final int EVT_LOCAL_REPLICA_CHANGE = 197;
    public static final int EVT_DB_DRL = 198;
    public static final int EVT_MOVE_ENTRY_SOURCE = 199;
    public static final int EVT_MOVE_ENTRY_DEST = 200;
    public static final int EVT_NOTIFY_REF_CHANGE = 201;
    public static final int EVT_DB_ALLOC = 202;
    public static final int EVT_CONSOLE_OPERATION = 203;
    public static final int EVT_DB_SERVER_PACKET = 204;
    public static final int EVT_DB_OBIT = 207;
    public static final int EVT_REPLICA_IN_TRANSITION = 208;
    public static final int EVT_DB_SYNC_DETAIL = 209;
    public static final int EVT_DB_CONN_TRACE = 210;

    /*
    public static final int EVT_CHANGE_CONFIG_PARM = 211;


    public static final int EVT_COMPUTE_CONN_SEV_INLINE = 212;
    */
    public static final int EVT_BEGIN_NAMEBASE_TRANSACTION = 213;
    public static final int EVT_DB_DIRXML = 214;
    public static final int EVT_VR_DRIVER_STATE_CHANGE = 215;
    public static final int EVT_REQ_UPDATE_SERVER_STATUS = 216;
    public static final int EVT_DB_DIRXML_DRIVERS = 217;
    public static final int EVT_DB_NDSMON = 218;
    public static final int EVT_CHANGE_SERVER_ADDRS = 219;
    public static final int EVT_DB_DNS = 220;
    public static final int EVT_DB_REPAIR = 221;
    public static final int EVT_DB_REPAIR_DEBUG = 222;
    public static final int EVT_ITERATOR = 224;
    public static final int EVT_DB_SCHEMA_DETAIL = 225;
    public static final int EVT_LOW_LEVEL_JOIN_BEGIN = 226;
    public static final int EVT_DB_IN_SYNC_DETAIL = 227;
    public static final int EVT_PRE_DELETE_ENTRY = 228;
    public static final int EVT_DB_SSL = 229;
    public static final int EVT_DB_PKI = 230;
    public static final int EVT_DB_HTTPSTK = 231;
    public static final int EVT_DB_LDAPSTK = 232;
    public static final int EVT_DB_NICIEXT = 233;
    public static final int EVT_DB_SECRET_STORE = 234;
    public static final int EVT_DB_NMAS = 235;
    public static final int EVT_DB_BACKLINK_DETAIL = 236;
    public static final int EVT_DB_DRL_DETAIL = 237;
    public static final int EVT_DB_OBJECT_PRODUCER = 238;
    public static final int EVT_DB_SEARCH = 239;
    public static final int EVT_DB_SEARCH_DETAIL = 240;
    public static final int EVT_STATUS_LOG = 241;
    public static final int EVT_DB_NPKI_API = 242;
    public static final int EVT_MAX_EVENTS = 243;

    /* Oid for requests */
    public static final String NLDAP_MONITOR_EVENTS_REQUEST =
        "2.16.840.1.113719.1.27.100.79";
    public static final String NLDAP_MONITOR_EVENTS_RESPONSE =
        "2.16.840.1.113719.1.27.100.80";
    public static final String NLDAP_EVENT_NOTIFICATION =
        "2.16.840.1.113719.1.27.100.81";
    public static final String NLDAP_FILTERED_MONITOR_EVENTS_REQUEST =
        "2.16.840.1.113719.1.27.100.84";

    /* For Debug Event Parameter Types. */

    /**
     * This constant is returned as Type by Debug Parameter class (Debug
     * Events)when data is an EntryID. EntryId  is represented as a
     * Integer Object.
     */
    public static final int DB_PARAM_TYPE_ENTRYID = 1;

    /**
     * This constant is returned as Type by Debug Parameter class (Debug
     * Events) when data is an String.
     */
    public static final int DB_PARAM_TYPE_STRING = 2;

    /**
     * This constant is returned as Type by Debug Parameter class (Debug
     * Events) when data is an Binary.  Binary is represented as an byte
     * array.
     */
    public static final int DB_PARAM_TYPE_BINARY = 3;

    /**
     * This constant is returned as Type by Debug Parameter class (Debug
     * Events) when data is an Integer.
     */
    public static final int DB_PARAM_TYPE_INTEGER = 4;

    /**
     * This constant is returned as Type by Debug Parameter class (Debug
     * Events) when data is an Address. Address is represented as a
     * ReferralAddress Object.
     */
    public static final int DB_PARAM_TYPE_ADDRESS = 5;

    /**
     * This constant is returned as Type by Debug Parameter class (Debug
     * Events) when data is an TimeStamp. TimeStamp is represented as a
     * DSETimeStamp Object.
     */
    public static final int DB_PARAM_TYPE_TIMESTAMP = 6;

    /**
     * This constant is returned as Type by Debug Parameter class (Debug
     * Events) when data is an TimeVector. TimeVector is represented as
     * an List of DSETimeStamp Object.
     */
    public static final int DB_PARAM_TYPE_TIMEVECTOR = 7;
}
