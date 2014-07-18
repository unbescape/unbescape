/*
 * =============================================================================
 * 
 *   Copyright (c) 2014, The UNBESCAPE team (http://www.unbescape.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.unbescape.uri;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


/**
 * <p>
 *   Internal class in charge of performing the real escape/unescape operations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.1.0
 *
 */
final class UriEscapeUtil {


    /*
     * URI ESCAPE/UNESCAPE OPERATIONS
     * ------------------------------
     *
     *   See: http://www.ietf.org/rfc/rfc3986.txt
     *        http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
     *
     *   Different parts of an URI allow different characters, and therefore require different sets of
     *   characters to be escaped (see RFC3986 for a list of reserved characters for each URI part) - but
     *   the escaping method is always the same: convert the character to the bytes representing it in a
     *   specific encoding (UTF-8 by default) and then percent-encode these bytes with two hexadecimal
     *   digits, like '%0A'.
     *
     *   - PATH:            Part of the URI path, might include several path levels/segments:
     *                      '/admin/users/list?x=1' -> 'users/list'
     *   - PATH SEGMENT:    Part of the URI path, can include only one path level ('/' chars will be escaped):
     *                      '/admin/users/list?x=1' -> 'users'
     *   - QUERY PARAMETER: Names and values of the URI query parameters:
     *                      '/admin/users/list?x=1' -> 'x' (name), '1' (value)
     *   - URI FRAGMENT ID: URI fragments:
     *                      '/admin/users/list?x=1#something' -> '#something'
     *
     */





    static enum UriEscapeType {

        PATH {
            @Override
            public boolean isAllowed(final int c) {
                return isPchar(c) || '/' == c;
            }
        },

        PATH_SEGMENT {
            @Override
            public boolean isAllowed(final int c) {
                return isPchar(c);
            }
        },

        QUERY_PARAM {
            @Override
            public boolean isAllowed(final int c) {
                // We specify these symbols separately because some of them are considered 'pchar'
                if ('=' == c || '&' == c || '+' == c || '#' == c) {
                    return false;
                }
                return isPchar(c) || '/' == c || '?' == c;
            }
            @Override
            public boolean canPlusEscapeWhitespace() {
                return true;
            }
        },

        FRAGMENT_ID {
            @Override
            public boolean isAllowed(final int c) {
                return isPchar(c) || '/' == c || '?' == c;
            }
        };


        public abstract boolean isAllowed(final int c);

        /*
         * Determines whether whitespace could appear escaped as '+' in the
         * current escape type.
         *
         * This allows unescaping of application/x-www-form-urlencoded
         * URI query parameters, which specify '+' as escape character
         * for whitespace instead of the '%20' specified by RFC3986.
         *
         * http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        public boolean canPlusEscapeWhitespace() {
            // Will only be true for QUERY_PARAM
            return false;
        }

        /*
         * Specification of 'pchar' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isPchar(final int c) {
            return isUnreserved(c) || isSubDelim(c) || ':' == c || '@' == c;
        }

        /*
         * Specification of 'unreserved' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isUnreserved(final int c) {
            return isAlpha(c) || isDigit(c) || '-' == c || '.' == c || '_' == c || '~' == c;
        }

        /*
         * Specification of 'reserved' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isReserved(final int c) {
            return isGenDelim(c) || isSubDelim(c);
        }

        /*
         * Specification of 'sub-delims' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isSubDelim(final int c) {
            return '!' == c || '$' == c || '&' == c || '\'' == c || '(' == c || ')' == c || '*' == c || '+' == c ||
                    ',' == c || ';' == c || '=' == c;
        }

        /*
         * Specification of 'gen-delims' according to RFC3986
         * http://www.ietf.org/rfc/rfc3986.txt
         */
        private static boolean isGenDelim(final int c) {
            return ':' == c || '/' == c || '?' == c || '#' == c || '[' == c || ']' == c || '@' == c;
        }

        /*
         * Character.isLetter() is not used here because it would include
         * non a-to-z letters.
         */
        static boolean isAlpha(final int c) {
            return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
        }

        /*
         * Character.isDigit() is not used here because it would include
         * non 0-to-9 numbers like i.e. arabic or indian numbers.
         */
        private static boolean isDigit(final int c) {
            return c >= '0' && c <= '9';
        }

    }






    /*
     * Prefixes defined for use in escape and unescape operations
     */
    private static final char ESCAPE_PREFIX = '%';

    /*
     * Small utility char arrays for hexadecimal conversion.
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();






    private UriEscapeUtil() {
        super();
    }




    static char[] printHexa(final byte b) {
        final char[] result = new char[2];
        result[0] = HEXA_CHARS_UPPER[(b >> 4) & 0xF];
        result[1] = HEXA_CHARS_UPPER[b & 0xF];
        return result;
    }


    static byte parseHexa(final char c1, final char c2) {

        byte result = 0;
        for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
            if (c1 == HEXA_CHARS_UPPER[j] || c1 == HEXA_CHARS_LOWER[j]) {
                result += (j << 4);
                break;
            }
        }
        for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
            if (c2 == HEXA_CHARS_UPPER[j] || c2 == HEXA_CHARS_LOWER[j]) {
                result += j;
                break;
            }
        }
        return result;

    }









    /*
     * Perform an escape operation, based on String, according to the specified type.
     */
    static String escape(final String text, final UriEscapeType escapeType, final String encoding) {

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            /*
             * Compute the codepoint. This will be used instead of the char for the rest of the process.
             */

            final int codepoint;
            if (c < Character.MIN_HIGH_SURROGATE) { // shortcut: U+D800 is the lower limit of high-surrogate chars.
                codepoint = (int) c;
            } else if (Character.isHighSurrogate(c) && (i + 1) < max) {
                final char c1 = text.charAt(i + 1);
                if (Character.isLowSurrogate(c1)) {
                    codepoint = Character.toCodePoint(c, c1);
                } else {
                    codepoint = (int) c;
                }
            } else { // just a normal, single-char, high-valued codepoint.
                codepoint = (int) c;
            }

            /*
             * Shortcut: most characters will be alphabetic, and we won't need to do anything at
             * all for them. No need to use the complete UriEscapeType check system at all.
             */
            if (UriEscapeType.isAlpha(codepoint)) {
                continue;
            }

            /*
             * Check whether the character is allowed or not
             */
            if (escapeType.isAllowed(codepoint)) {
                continue;
            }


            /*
             * At this point we know for sure we will need some kind of escape, so we
             * can increase the offset and initialize the string builder if needed, along with
             * copying to it all the contents pending up to this point.
             */

            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 20);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            if (Character.charCount(codepoint) > 1) {
                // This is to compensate that we are actually reading two char[] positions with a single codepoint.
                i++;
            }

            readOffset = i + 1;


            /*
             * -----------------------------------------------------------------------------------------
             *
             * Peform the real escape
             *
             * -----------------------------------------------------------------------------------------
             */

            final byte[] charAsBytes;
            try {
                charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
            } catch (final UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
            for (final byte b : charAsBytes) {
                strBuilder.append('%');
                strBuilder.append(printHexa(b));
            }


        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no escape was actually needed. Otherwise
         *                 append the remaining unescaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        return strBuilder.toString();

    }






    /*
     * Perform an escape operation, based on char[], according to the specified type
     */
    static void escape(final char[] text, final int offset, final int len, final Writer writer,
                       final UriEscapeType escapeType, final String encoding)
                       throws IOException {

        if (text == null || text.length == 0) {
            return;
        }

        final int max = (offset + len);

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            /*
             * Compute the codepoint. This will be used instead of the char for the rest of the process.
             */

            final int codepoint;
            if (c < Character.MIN_HIGH_SURROGATE) { // shortcut: U+D800 is the lower limit of high-surrogate chars.
                codepoint = (int) c;
            } else if (Character.isHighSurrogate(c) && (i + 1) < max) {
                final char c1 = text[i + 1];
                if (Character.isLowSurrogate(c1)) {
                    codepoint = Character.toCodePoint(c, c1);
                } else {
                    codepoint = (int) c;
                }
            } else { // just a normal, single-char, high-valued codepoint.
                codepoint = (int) c;
            }

            /*
             * Shortcut: most characters will be alphabetic, and we won't need to do anything at
             * all for them. No need to use the complete UriEscapeType check system at all.
             */
            if (UriEscapeType.isAlpha(codepoint)) {
                continue;
            }

            /*
             * Check whether the character is allowed or not
             */
            if (escapeType.isAllowed(codepoint)) {
                continue;
            }



            /*
             * At this point we know for sure we will need some kind of escape, so we
             * can write all the contents pending up to this point.
             */

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            if (Character.charCount(codepoint) > 1) {
                // This is to compensate that we are actually reading two char[] positions with a single codepoint.
                i++;
            }

            readOffset = i + 1;


            /*
             * -----------------------------------------------------------------------------------------
             *
             * Peform the real escape
             *
             * -----------------------------------------------------------------------------------------
             */

            final byte[] charAsBytes;
            try {
                charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
            } catch (final UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }
            for (final byte b : charAsBytes) {
                writer.write('%');
                writer.write(printHexa(b));
            }


        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no escape was actually needed. Otherwise
         *                 append the remaining unescaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

    }












    /*
     * Perform an unescape operation based on String.
     */
    static String unescape(final String text, final UriEscapeType escapeType, final String encoding) {

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != ESCAPE_PREFIX && (c != '+' || !escapeType.canPlusEscapeWhitespace())) {
                continue;
            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * can increase the offset and initialize the string builder if needed, along with
             * copying to it all the contents pending up to this point.
             */

            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }


            /*
             * Deal with possible '+'-escaped whitespace (application/x-www-form-urlencoded)
             */
            if (c == '+') {
                // if we reached this point with c == '+', it's escaping a whitespace
                strBuilder.append(' ');
                readOffset = i + 1;
                continue;
            }


            /*
             * ESCAPE PROCESS
             * --------------
             * If there are more than one percent-encoded/escaped sequences together, we will
             * need to unescape them all at once (because they might be bytes --up to 4-- of
             * the same char).
             */


            // Max possible size will be the remaining amount of chars / 3
            final byte[] bytes = new byte[(max-i)/3];
            char aheadC = c;
            int pos = 0;

            while (((i + 2) < max) && aheadC == ESCAPE_PREFIX) {
                bytes[pos++] = parseHexa(text.charAt(i + 1), text.charAt(i + 2));
                i += 3;
                if (i < max) {
                    aheadC = text.charAt(i);
                }
            }

            if (i < max && aheadC == ESCAPE_PREFIX) {
                // Incomplete escape sequence!
                throw new IllegalArgumentException("Incomplete escaping sequence in input");
            }

            try {
                strBuilder.append(new String(bytes, 0, pos, encoding));
            } catch (final UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }


            readOffset = i;

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no unescape was actually needed. Otherwise
         *                 append the remaining escaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        return strBuilder.toString();

    }






    /*
     * Perform an unescape operation based on char[].
     */
    static void unescape(final char[] text, final int offset, final int len, final Writer writer,
                         final UriEscapeType escapeType, final String encoding)
                         throws IOException {

        if (text == null) {
            return;
        }

        final int max = (offset + len);

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != ESCAPE_PREFIX && (c != '+' || !escapeType.canPlusEscapeWhitespace())) {
                continue;
            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * can increase the offset copy all the contents pending up to this point.
             */

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }


            /*
             * Deal with possible '+'-escaped whitespace (application/x-www-form-urlencoded)
             */
            if (c == '+') {
                // if we reached this point with c == '+', it's escaping a whitespace
                writer.write(' ');
                readOffset = i + 1;
                continue;
            }


            /*
             * ESCAPE PROCESS
             * --------------
             * If there are more than one percent-encoded/escaped sequences together, we will
             * need to unescape them all at once (because they might be bytes --up to 4-- of
             * the same char).
             */

            // Max possible size will be the remaining amount of chars / 3
            final byte[] bytes = new byte[(max-i)/3];
            char aheadC = c;
            int pos = 0;

            while (((i + 2) < max) && aheadC == ESCAPE_PREFIX) {
                bytes[pos++] = parseHexa(text[i + 1], text[i + 2]);
                i += 3;
                if (i < max) {
                    aheadC = text[i];
                }
            }

            if (i < max && aheadC == ESCAPE_PREFIX) {
                // Incomplete escape sequence!
                throw new IllegalArgumentException("Incomplete escaping sequence in input");
            }

            try {
                writer.write(new String(bytes, 0, pos, encoding));
            } catch (final UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
            }


            readOffset = i;

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no unescape was actually needed. Otherwise
         *                 append the remaining escaped text and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

    }



}

