/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 2002 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.util;

import java.io.UnsupportedEncodingException;

/**
 * The Base64 utility class performs base64 encoding and decoding.
 *
 * The Base64 Content-Transfer-Encoding is designed to represent
 * arbitrary sequences of octets in a form that need not be humanly
 * readable.  The encoding and decoding algorithms are simple, but the
 * encoded data are consistently only about 33 percent larger than the
 * unencoded data.  The base64 encoding algorithm is defined by
 * RFC 2045.
 */
public class Base64
{
    /** Conversion table for encoding to base64.
     *
     * emap is a six-bit value to base64 (8-bit) converstion table.
     * For example, the value of the 6-bit value 15
     * is mapped to 0x50 which is the ASCII letter 'P', i.e. the letter P
     * is the base64 encoded character that represents the 6-bit value 15.
     *//*
     * 8-bit base64 encoded character                 base64       6-bit
     *                                                encoded      original
     *                                                character    binary value
     */
    private static final char emap[] = {
     'A',  'B',  'C',  'D',  'E',  'F',  'G',  'H',  // A-H;       0 - 7
     'I',  'J',  'K',  'L',  'M',  'N',  'O',  'P',  // I-P;       8 -15
     'Q',  'R',  'S',  'T',  'U',  'V',  'W',  'X',  // Q-X;       16-23
     'Y',  'Z',  'a',  'b',  'c',  'd',  'e',  'f',  // YZ, a-f;   24-31
     'g',  'h',  'i',  'j',  'k',  'l',  'm',  'n',  // g-n;       32-39
     'o',  'p',  'q',  'r',  's',  't',  'u',  'v',  // o-v;       40-47
     'w',  'x',  'y',  'z',  '0',  '1',  '2',  '3',  // w-z, 0-3;  48-55
     '4',  '5',  '6',  '7',  '8',  '9',  '+',  '/'}; // 4-9, + /;  56-63

    /** conversion table for decoding from base64.
     *
     * dmap is a base64 (8-bit) to six-bit value converstion table.
     * For example the ASCII character 'P' has a value of 80.
     * The value in the 80th position of the table is 0x0f or 15.
     * 15 is the original 6-bit value that the letter 'P' represents.
     *//*
     * 6-bit decoded value                            base64    base64
     *                                                encoded   character
     *                                                value
     *
     * Note: about half of the values in the table are only place holders
     */
    private static final byte dmap[] = {
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 0 -7
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 8 -15
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 16-23
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 24-31
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 31-39
    0x00, 0x00, 0x00, 0x3e, 0x00, 0x00, 0x00, 0x3f, // 40-47   '   +   /'
    0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, // 48-55   '01234567'
    0x3c, 0x3d, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 56-63   '89      '
    0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // 64-71   ' ABCDEFG'
    0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, // 72-79   'HIJKLMNO'
    0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 80-87   'PQRSTUVW'
    0x17, 0x18, 0x19, 0x00, 0x00, 0x00, 0x00, 0x00, // 88-95   'XYZ     '
    0x00, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, // 96-103  ' abcdefg'
    0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, // 104-111 'hijllmno'
    0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, // 112-119 'pqrstuvw'
    0x31, 0x32, 0x33, 0x00, 0x00, 0x00, 0x00, 0x00};// 120-127 'xyz     '

    /**
     * Default constructor, don't allow instances of the
     * utility class to be created.
     */
    private Base64() {
        return;
    }


    /**
     * Encodes the specified String into a base64 encoded String object.
     *
     * @param inputString  The String object to be encoded.
     *
     * @return a String containing the encoded value of the input.
     */
    public static final String encode(String inputString)
    {
        try {
            return encode(inputString.getBytes("UTF-8"));
        } catch( UnsupportedEncodingException ue) {
            throw new RuntimeException(
                    "US-ASCII String encoding not supported by JVM");
        }
    }

    /**
     * Encodes the specified bytes into a base64 array of bytes.
     * Each byte in the return array represents a base64 character.
     *
     * @param inputBytes  the byte array to be encoded.
     *
     * @return            a String containing the base64 encoded data
     */
    public static final String encode(byte[] inputBytes)
    {
        int  i, j, k;
        int t, t1,t2;
        int  ntb;  // number of three-bytes in inputBytes
        boolean onePadding = false, twoPaddings = false;
        char[] encodedChars;     // base64 encoded chars
        int  len = inputBytes.length;

        if( len == 0) {
            // No data, return no data.
            return new String("");
        }

        // every three bytes will be encoded into four bytes
        if ( len%3 == 0 ) {
            ntb = len / 3;
        }
        // the last one or two bytes will be encoded into
        // four bytes with one or two paddings
        else {
            ntb = len / 3 + 1;
        }

        // need two paddings
        if( (len%3) == 1 ) {
            twoPaddings = true;
        }
        // need one padding
        else if ( (len%3) == 2 ) {
                onePadding = true;
        }

        encodedChars = new char[ntb*4];

        // map of decoded and encoded bits
        //     bits in 3 decoded bytes:   765432  107654  321076  543210
        //     bits in 4 encoded bytes: 76543210765432107654321076543210
        //       plain           "AAA":   010000  010100  000101  000001
        //       base64 encoded "QUFB": 00010000000101000000010100000001
        // one padding:
        //     bits in 2 decoded bytes:   765432  10 7654  3210
        //     bits in 4 encoded bytes: 765432107654 321076543210 '='
        //       plain            "AA":   010000  010100  0001
        //       base64 encoded "QUE=": 00010000000101000000010000111101
        // two paddings:
        //     bits in 1 decoded bytes:   765432  10
        //     bits in 4 encoded bytes: 7654321076543210 '=' '='
        //       plain             "A":   010000  01
        //       base64 encoded "QQ==": 00010000000100000011110100111101
        //
        // note: the encoded bits which have no corresponding decoded bits
        // are filled with zeros; '=' = 00111101.
        for ( i = 0, j = 0, k = 1; i < len; i+=3, j+=4, k++) {

            // build encodedChars[j]
            t = 0x00ff & inputBytes[i];
            encodedChars[j] = emap[t >> 2];

            // build encodedChars[j+1]
            if ( (k==ntb) && twoPaddings) {
                encodedChars[j+1] = emap[(t&0x03) << 4];
                encodedChars[j+2] = '=';
                encodedChars[j+3] = '=';
                break;
            }
            else {
                t1 = 0x00ff & inputBytes[i+1];
                encodedChars[j+1] =
                   emap[((t&0x03) << 4) +( (t1&0xf0) >> 4) ];
            }

            // build encodedChars[j+2]
            if((k==ntb) && onePadding) {
                encodedChars[j+2] = emap[(t1&0x0f) << 2];
                encodedChars[j+3] = '=';
                break;
            }
            else {
                t2 = 0x00ff & inputBytes[i+2];
                encodedChars[j+2] =
                      (emap[(t1&0x0f) << 2 | (t2&0xc0) >> 6]);
            }

            // build encodedChars[j+3]
            encodedChars[j+3] = (emap[(t2&0x3f)]);
        }
        return new String(encodedChars);
    }


    /**
     * Decodes the input base64 encoded String.
     * The resulting binary data is returned as an array of bytes.
     *
     * @param encodedString The base64 encoded String object.
     *
     * @return The decoded byte array.
     */
    public static final byte[] decode(String encodedString)
    {
        char[] c = new char[encodedString.length()];
        encodedString.getChars(0, encodedString.length(), c, 0);
        return decode(c);
    }

    /**
     * Decodes the input base64 encoded array of characters.
     * The resulting binary data is returned as an array of bytes.
     *
     * @param encodedChars The character array containing the base64 encoded data.
     *
     * @return A byte array object containing decoded bytes.
     */
    public static final byte[] decode(char[] encodedChars)
    {
        int  i, j, k;
        int  ecLen = encodedChars.length;  // length of encodedChars
        int  gn = ecLen/4;         // number of four-byte groups in encodedChars
        int  dByteLen;             // length of decoded bytes, default is '0'
        boolean onePad = false, twoPads = false;
        byte[] decodedBytes;       // decoded bytes

        if( encodedChars.length == 0) {
            return new byte[0];
        }
        // the number of encoded bytes should be multiple of 4
        if ( (ecLen%4) != 0) {
            throw new RuntimeException("com.novell.ldap.ldif_dsml."
                     + "Base64Decoder: decode: mal-formatted encode value");
        }

        // every four-bytes in encodedString, except the last one if it in the
        // form of '**==' or '***=' ( can't be '*===' or '===='), will be
        // decoded into three bytes.
        if ( (encodedChars[ecLen-1] == (int)'=') &&
             (encodedChars[ecLen-2] == (int)'=') ) {
            // the last four bytes of encodedChars is in the form of '**=='
            twoPads = true;
            // the first two bytes of the last four-bytes of encodedChars will
            // be decoded into one byte.
            dByteLen = gn * 3 - 2;
            decodedBytes = new byte[dByteLen];
        } else
        if ( encodedChars[ecLen-1] == '=' ) {
            // the last four bytes of encodedChars is in the form of '***='
            onePad = true;
            // the first two bytes of the last four-bytes of encodedChars will
            // be decoded into two bytes.
            dByteLen = gn * 3 - 1;
            decodedBytes = new byte[dByteLen];
        } else {
            // the last four bytes of encodedChars is in the form of '****',
            // e.g. no pad.
            dByteLen = gn * 3;
            decodedBytes = new byte[dByteLen];
        }

        // map of encoded and decoded bits
        // no padding:
        //     bits in 4 encoded bytes: 76543210 76543210 76543210 76543210
        //     bits in 3 decoded bytes:   765432   107654   321076   543210
        //        base64  string "QUFB":00010000 00010100 000001010 0000001
        //          plain string  "AAA":   010000  010100  000101  000001
        // one padding:
        //     bits in 4 encoded bytes: 76543210 76543210 76543210 76543210
        //     bits in 2 decoded bytes:   765432   107654   3210
        //       base64  string "QUE=": 00010000 000101000 0000100 00111101
        //         plain string   "AA":   010000  010100  0001
        // two paddings:
        //     bits in 4 encoded bytes: 76543210 76543210 76543210 76543210
        //     bits in 1 decoded bytes:   765432   10
        //       base64  string "QQ==": 00010000 00010000 00111101 00111101
        //         plain string    "A":   010000  01
        for ( i=0, j=0, k=1; i<ecLen; i+=4, j+=3,k++ ) {
            // build decodedBytes[j].
            decodedBytes[j] = (byte)( dmap[encodedChars[i]]<<2
                                   | (dmap[encodedChars[i+1]]&0x30)>>4 );

            // build decodedBytes[j+1]
            if ( (k==gn) && twoPads ){
                break;
            } else {
                decodedBytes[j+1] = (byte)( (dmap[encodedChars[i+1]]&0x0f)<<4
                                           |(dmap[encodedChars[i+2]]&0x3c)>>2 );
            }

            // build decodedBytes[j+2]
            if( (k==gn) && onePad ) {
                break;
            } else {
                decodedBytes[j+2] = (byte)( (dmap[encodedChars[i+2]]&0x03)<<6
                                           | dmap[encodedChars[i+3]]&0x3f);
            }
        }
        return decodedBytes;
    }

    /**
     * Decodes a base64 encoded StringBuffer.
     * Decodes all or part of the input base64 encoded StringBuffer, each
     * Character value representing a base64 character. The resulting
     * binary data is returned as an array of bytes.
     *
     * @param  encodedSBuf The StringBuffer object that contains base64
     * encoded data.
     * @param  start  The start index of the base64 encoded data.
     * @param  end  The end index + 1 of the base64 encoded data.
     *
     * @return The decoded byte array
     */
    public static final byte[] decode(StringBuffer encodedSBuf, int start, int end)
    {
        int  i, j, k;
        int  esbLen = end - start;// length of the encoded part
        int  gn = esbLen/4;      // number of four-bytes group in ebs
        int  dByteLen;             // length of dbs, default is '0'
        boolean onePad = false, twoPads = false;
        byte[] decodedBytes;     // decoded bytes

        if( encodedSBuf.length() == 0) {
            return new byte[0];
        }
        // the number of encoded bytes should be multiple of number 4
        if ( (esbLen%4) != 0) {
            throw new RuntimeException("com.novell.ldap.ldif_dsml."
                + "Base64Decoder: decode error: mal-formatted encode value");
        }

        // every four-bytes in ebs, except the last one if it in the form of
        // '**==' or '***=' ( can't be '*===' or '===='), will be decoded into
        // three bytes.
        if ( (encodedSBuf.charAt(end-1) == (int)'=') &&
             (encodedSBuf.charAt(end-2) == (int)'=') ) {
            // the last four bytes of ebs is in the form of '**=='
            twoPads = true;
            // the first two bytes of the last four-bytes of ebs will be
            // decoded into one byte.
            dByteLen = gn * 3 - 2;
            decodedBytes = new byte[dByteLen];
        }
        else if ( encodedSBuf.charAt(end-1) == (int)'=' ) {
            // the last four bytes of ebs is in the form of '***='
            onePad = true;
            // the first two bytes of the last four-bytes of ebs will be
            // decoded into two bytes.
            dByteLen = gn * 3 - 1;
            decodedBytes = new byte[dByteLen];
        }
        else {
            // the last four bytes of ebs is in the form of '****', eg. no pad.
            dByteLen = gn * 3;
            decodedBytes = new byte[dByteLen];
        }

        // map of encoded and decoded bits
        // no padding:
        //     bits in 4 encoded bytes: 76543210 76543210 76543210 76543210
        //     bits in 3 decoded bytes:   765432   107654   321076   543210
        //        base64  string "QUFB":00010000 00010100 000001010 0000001
        //          plain string  "AAA":   010000  010100  000101  000001
        // one padding:
        //     bits in 4 encoded bytes: 76543210 76543210 76543210 76543210
        //     bits in 2 decoded bytes:   765432   107654   3210
        //       base64  string "QUE=": 00010000 000101000 0000100 00111101
        //         plain string   "AA":   010000  010100  0001
        // two paddings:
        //     bits in 4 encoded bytes: 76543210 76543210 76543210 76543210
        //     bits in 1 decoded bytes:   765432   10
        //       base64  string "QQ==": 00010000 00010000 00111101 00111101
        //         plain string    "A":   010000  01
        for ( i=0, j=0, k=1; i<esbLen; i+=4, j+=3,k++ ) {
            // build decodedBytes[j].
            decodedBytes[j] =
                        (byte)( dmap[encodedSBuf.charAt(start+i)]<<2
                            |  (dmap[encodedSBuf.charAt(start+i+1)]&0x30)>>4 );

            // build decodedBytes[j+1]
            if ( (k==gn) && twoPads ){
                break;
            }
            else {
                decodedBytes[j+1] =
                        (byte)( (dmap[encodedSBuf.charAt(start+i+1)]&0x0f)<<4
                              | (dmap[encodedSBuf.charAt(start+i+2)]&0x3c)>>2);
            }

            // build decodedBytes[j+2]
            if( (k==gn) && onePad ) {
                break;
            }
            else {
                decodedBytes[j+2] =
                        (byte)( (dmap[encodedSBuf.charAt(start+i+2)]&0x03)<<6
                               | dmap[encodedSBuf.charAt(start+i+3)]&0x3f);
            }
        }
        return decodedBytes;
    }

    /**
     * Checks if the input byte array contains only safe values, that is,
     * the data does not need to be encoded for use with LDIF.
     * The rules for checking safety are based on the rules for LDIF
     * (LDAP Data Interchange Format) per RFC 2849.  The data does
     * not need to be encoded if all the following are true:
     *<br>
     * <p>The data cannot start with the following byte values:</p>
     *<pre>
     *         00 (NUL)
     *         10 (LF)
     *         13 (CR)
     *         32 (SPACE)
     *         58 (:)
     *         60 (<)
     *         Any character with value greater than 127
     *         (Negative for a byte value)
     *</pre>
     * <p>The data cannot contain any of the following byte values:</p>
     *<pre>
     *         00 (NUL)
     *         10 (LF)
     *         13 (CR)
     *         Any character with value greater than 127
     *         (Negative for a byte value)
     *</pre>
     * <p>The data cannot end with a space.</p>
     *
     * @param bytes the bytes to be checked.
     *
     * @return true if encoding not required for LDIF
     */
    public static final boolean isLDIFSafe(byte[] bytes)
    {
        int len = bytes.length;
        if( len > 0) {
            int testChar = bytes[0];
            // unsafe if first character is a NON-SAFE-INIT-CHAR
            if (      (testChar == 0x00)     // NUL
                   || (testChar == 0x0A)     // linefeeder
                   || (testChar == 0x0D)     // carrage return
                   || (testChar == 0x20)     // space(' ')
                   || (testChar == 0x3A)     // colon(':')
                   || (testChar == 0x3C)     // less-than('<')
                   || (testChar <  0)) {     // non ascii (>127 is negative)
                return  false;
            }
            // unsafe if last character is a space
            if( bytes[len-1] == ' ') {
                return false;
            }
            // unsafe if contains any non safe character
            if( len > 1){
                for ( int i = 1; i < bytes.length; i++ ) {
                    testChar = bytes[i];
                    if (      (testChar == 0x00) // NUL
                           || (testChar == 0x0A) // linefeeder
                           || (testChar == 0x0D) // carrage return
                           || (testChar <  0)) { // non ascii (>127 is negative)
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /**
     * Checks if the input String contains only safe values, that is,
     * the data does not need to be encoded for use with LDIF.
     * The rules for checking safety are based on the rules for LDIF
     * (LDAP Data Interchange Format) per RFC 2849.  The data does
     * not need to be encoded if all the following are true:
     *<br>
     * <p>The data cannot start with the following char values:</p>
     *<pre>
     *         00 (NUL)
     *         10 (LF)
     *         13 (CR)
     *         32 (SPACE)
     *         58 (:)
     *         60 (<)
     *         Any character with value greater than 127
     *</pre>
     * <p>The data cannot contain any of the following char values:</p>
     *<pre>
     *         00 (NUL)
     *         10 (LF)
     *         13 (CR)
     *         Any character with value greater than 127
     *</pre>
     * <p>The data cannot end with a space.</p>
     *
     * @param str the String to be checked.
     *
     * @return true if encoding not required for LDIF
     */
    public static final boolean isLDIFSafe(String str)
    {
        try {
            return( isLDIFSafe(str.getBytes("UTF-8")));
        } catch( UnsupportedEncodingException ue) {
            throw new RuntimeException(
                    "UTF-8 String encoding not supported by JVM");
        }
    }
}
