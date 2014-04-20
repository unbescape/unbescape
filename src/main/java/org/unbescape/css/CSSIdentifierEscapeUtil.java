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
package org.unbescape.css;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * <p>
 *   Internal class in charge of performing the real escape/unescape operations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0
 *
 */
final class CSSIdentifierEscapeUtil {



    /*
     * CSS IDENTIFIER ESCAPE/UNESCAPE OPERATIONS
     * -----------------------------------------
     *
     *   See: http://www.w3.org/TR/CSS21/syndata.html#value-def-identifier
     *        http://mathiasbynens.be/notes/css-escapes
     *        http://mothereff.in/css-escapes
     *
     *   (Note that, in the following examples, and in order to avoid escape problems during the compilation
     *    of this class, the backslash symbol is replaced by '%')
     *
     *   - SINGLE ESCAPE CHARACTERS (SECs):
     *        U+0020 -> %  (escape + whitespace)
     *        U+0021 -> %!
     *        U+0022 -> %"
     *        U+0023 -> %#
     *        U+0024 -> %$
     *        U+0025 -> %%
     *        U+0026 -> %&
     *        U+0027 -> %'
     *        U+0028 -> %(
     *        U+0029 -> %)
     *        U+002A -> %*
     *        U+002B -> %+
     *        U+002C -> %,
     *        U+002D -> %-  [ ONLY USED WHEN IDENTIFIER STARTS WITH -- OR -{DIGIT} ]
     *        U+002E -> %.
     *        U+002F -> %/
     *        U+003A -> %:  [ NOT USED FOR ESCAPING, NOT RECOGNIZED BY IE < 8 ]
     *        U+003B -> %;
     *        U+003C -> %<
     *        U+003D -> %=
     *        U+003E -> %>
     *        U+003F -> %?
     *        U+0040 -> %@
     *        U+005B -> %[
     *        U+005C -> %%
     *        U+005D -> %]
     *        U+005E -> %^
     *        U+005F -> %_  [ ONLY USED AT THE BEGINNING OF AN INDENTIFIER, TO AVOID PROBLEMS WITH IE6 ]
     *        U+0060 -> %`
     *        U+007B -> %{
     *        U+007C -> %|
     *        U+007D -> %}
     *        U+007E -> %~
     *
     *   - UNICODE ESCAPE [HEXA]
     *        Compact representation: %??* (variable-length. Optionally followed by a whitespace U+0020 - required
     *                                      if after escape comes a hexadecimal char (0-9a-f))
     *        6-digit representation: %?????? (fixed-length. Not followed by whitespace)
     *
     *        Characters > U+FFFF :
      *             - Standard:      %?????? or %??* (but not supported by older WebKit browsers)
     *              - Non-standard:  %u????%u???? (surrogate character pair, only in older WebKit browsers)
     *
     *
     */



    /*
     * Prefixes defined for use in escape and unescape operations
     */
    private static final char ESCAPE_PREFIX = '\\';

    /*
     * Small utility char arrays for hexadecimal conversion.
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();


    /*
     * Structures for holding the Backslash Escapes
     */
    private static int BACKSLASH_CHARS_LEN = '~' + 1; // 0x7E + 1 = 0x7F
    private static char BACKSLASH_CHARS_NO_SEC = 0x0;
    private static char[] BACKSLASH_CHARS;

    /*
     * Structured for holding the 'escape level' assigned to chars (not codepoints) up to ESCAPE_LEVELS_LEN.
     * - The last position of the ESCAPE_LEVELS array will be used for determining the level of all
     *   codepoints >= (ESCAPE_LEVELS_LEN - 1)
     */
    private static final char ESCAPE_LEVELS_LEN = 0x9f + 2; // Last relevant char to be indexed is 0x9f
    private static final byte[] ESCAPE_LEVELS;



    static {

        /*
         * Initialize Single Escape Characters
         */
        BACKSLASH_CHARS = new char[BACKSLASH_CHARS_LEN];
        Arrays.fill(BACKSLASH_CHARS, BACKSLASH_CHARS_NO_SEC);
        BACKSLASH_CHARS[0x20] = ' ';
        BACKSLASH_CHARS[0x21] = '!';
        BACKSLASH_CHARS[0x22] = '"';
        BACKSLASH_CHARS[0x23] = '#';
        BACKSLASH_CHARS[0x24] = '$';
        BACKSLASH_CHARS[0x25] = '%';
        BACKSLASH_CHARS[0x26] = '&';
        BACKSLASH_CHARS[0x27] = '\'';
        BACKSLASH_CHARS[0x28] = '(';
        BACKSLASH_CHARS[0x29] = ')';
        BACKSLASH_CHARS[0x2A] = '*';
        BACKSLASH_CHARS[0x2B] = '+';
        BACKSLASH_CHARS[0x2C] = ',';
        // hyphen: will only be escaped when identifer starts with '--' or '-{digit}'
        BACKSLASH_CHARS[0x2D] = '-';
        BACKSLASH_CHARS[0x2E] = '.';
        BACKSLASH_CHARS[0x2F] = '/';
        // colon: will not be used for escaping: not recognized by IE < 8
        // BACKSLASH_CHARS[0x3A] = ':';
        BACKSLASH_CHARS[0x3B] = ';';
        BACKSLASH_CHARS[0x3C] = '<';
        BACKSLASH_CHARS[0x3D] = '=';
        BACKSLASH_CHARS[0x3E] = '>';
        BACKSLASH_CHARS[0x3F] = '?';
        BACKSLASH_CHARS[0x40] = '@';
        BACKSLASH_CHARS[0x5B] = '[';
        BACKSLASH_CHARS[0x5C] = '%';
        BACKSLASH_CHARS[0x5D] = ']';
        BACKSLASH_CHARS[0x5E] = '^';
        // underscore: will only be escaped at the beginning of an identifier (in order to avoid issues in IE6)
        BACKSLASH_CHARS[0x5F] = '_';
        BACKSLASH_CHARS[0x60] = '`';
        BACKSLASH_CHARS[0x7B] = '{';
        BACKSLASH_CHARS[0x7C] = '|';
        BACKSLASH_CHARS[0x7D] = '}';
        BACKSLASH_CHARS[0x7E] = '~';



        /*
         * Initialization of escape levels.
         * Defined levels :
         *
         *    - Level 1 : Basic escape set
         *    - Level 2 : Basic escape set plus all non-ASCII
         *    - Level 3 : All non-alphanumeric characters
         *    - Level 4 : All characters
         *
         */
        ESCAPE_LEVELS = new byte[ESCAPE_LEVELS_LEN];

        /*
         * Everything is level 3 unless contrary indication.
         */
        Arrays.fill(ESCAPE_LEVELS, (byte)3);

        /*
         * Everything non-ASCII is level 2 unless contrary indication.
         */
        for (char c = 0x80; c < ESCAPE_LEVELS_LEN; c++) {
            ESCAPE_LEVELS[c] = 2;
        }

        /*
         * Alphanumeric characters are level 4.
         */
        for (char c = 'A'; c <= 'Z'; c++) {
            ESCAPE_LEVELS[c] = 4;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            ESCAPE_LEVELS[c] = 4;
        }
        for (char c = '0'; c <= '9'; c++) {
            ESCAPE_LEVELS[c] = 4;
        }

        /*
         * Backslash Escapes will be level 1 (always escaped)
         */
        ESCAPE_LEVELS[0x20] = 1;
        ESCAPE_LEVELS[0x21] = 1;
        ESCAPE_LEVELS[0x22] = 1;
        ESCAPE_LEVELS[0x23] = 1;
        ESCAPE_LEVELS[0x24] = 1;
        ESCAPE_LEVELS[0x25] = 1;
        ESCAPE_LEVELS[0x26] = 1;
        ESCAPE_LEVELS[0x27] = 1;
        ESCAPE_LEVELS[0x28] = 1;
        ESCAPE_LEVELS[0x29] = 1;
        ESCAPE_LEVELS[0x2A] = 1;
        ESCAPE_LEVELS[0x2B] = 1;
        ESCAPE_LEVELS[0x2C] = 1;
        // hyphen: will only be escaped when identifer starts with '--' or '-{digit}'
        ESCAPE_LEVELS[0x2D] = 1;
        ESCAPE_LEVELS[0x2E] = 1;
        ESCAPE_LEVELS[0x2F] = 1;
        // colon: will not be used for escaping: not recognized by IE < 8
        ESCAPE_LEVELS[0x3A] = 1;
        ESCAPE_LEVELS[0x3B] = 1;
        ESCAPE_LEVELS[0x3C] = 1;
        ESCAPE_LEVELS[0x3D] = 1;
        ESCAPE_LEVELS[0x3E] = 1;
        ESCAPE_LEVELS[0x3F] = 1;
        ESCAPE_LEVELS[0x40] = 1;
        ESCAPE_LEVELS[0x5B] = 1;
        ESCAPE_LEVELS[0x5C] = 1;
        ESCAPE_LEVELS[0x5D] = 1;
        ESCAPE_LEVELS[0x5E] = 1;
        // underscore: will only be escaped at the beginning of an identifier (in order to avoid issues in IE6)
        ESCAPE_LEVELS[0x5F] = 1;
        ESCAPE_LEVELS[0x60] = 1;
        ESCAPE_LEVELS[0x7B] = 1;
        ESCAPE_LEVELS[0x7C] = 1;
        ESCAPE_LEVELS[0x7D] = 1;
        ESCAPE_LEVELS[0x7E] = 1;


        /*
         * Two ranges of non-displayable, control characters:
         * U+0000 to U+001F and U+007F to U+009F.
         */
        for (char c = 0x00; c <= 0x1F; c++) {
            ESCAPE_LEVELS[c] = 1;
        }
        for (char c = 0x7F; c <= 0x9F; c++) {
            ESCAPE_LEVELS[c] = 1;
        }

    }



    private CSSIdentifierEscapeUtil() {
        super();
    }




    static char[] toCompactHexa(final int codepoint) {
        if (codepoint == 0) {
            return new char[] { '0', ' ' };
        }
        int div = 20;
        char[] result = null;
        while (result == null && div >= 0) {
            if ((codepoint >>> div) % 0x10 > 0) {
                result = new char[(div / 4) + (div == 20? 1 : 2)];
            }
            div -= 4;
        }
        div = 0;
        for (int i = (codepoint > 0xFFFFF? result.length - 1 : result.length - 2); i >= 0; i--) {
            result[i] = HEXA_CHARS_UPPER[(codepoint >>> div) % 0x10];
            div += 4;
        }
        if (codepoint <= 0xFFFFF) {
            result[result.length - 1] = ' ';
        }
        return result;
    }


    static char[] toSixDigitHexa(final int codepoint) {
        final char[] result = new char[6];
        result[5] = HEXA_CHARS_UPPER[codepoint % 0x10];
        result[4] = HEXA_CHARS_UPPER[(codepoint >>> 4) % 0x10];
        result[3] = HEXA_CHARS_UPPER[(codepoint >>> 8) % 0x10];
        result[2] = HEXA_CHARS_UPPER[(codepoint >>> 12) % 0x10];
        result[1] = HEXA_CHARS_UPPER[(codepoint >>> 16) % 0x10];
        result[0] = HEXA_CHARS_UPPER[(codepoint >>> 20) % 0x10];
        return result;
    }



    /*
     * Perform an escape operation, based on String, according to the specified level and type.
     */
    static String escape(final String text, final CSSIdentifierEscapeType escapeType, final CSSIdentifierEscapeLevel escapeLevel) {

        if (text == null) {
            return null;
        }

        final int level = escapeLevel.getEscapeLevel();
        final boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        final boolean useCompactHexa = escapeType.getUseCompactHexa();

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
             * Shortcut: most characters will be ASCII/Alphanumeric, and we won't need to do anything at
             * all for them
             */
            if (codepoint <= (ESCAPE_LEVELS_LEN - 2) && level < ESCAPE_LEVELS[codepoint] &&
                    (i > offset || codepoint < '0' || codepoint > '9')) {
                // Note how we check whether the first char is a decimal number, in which case we have to escape it
                continue;
            }


            /*
             * Hyphen check: only escape when it's the first char and it's followed by '-' or a digit.
             */
            if (codepoint == '-' && level < 3) {
                if (i > offset || i + 1 >= max) {
                    continue;
                }
                final char c1 = text.charAt(i + 1);
                if (c1 != '-' && (c1 < '0' || c1 > '9')) {
                    continue;
                }
            }


            /*
             * Underscore check: only escape when it's the first char.
             */
            if (codepoint == '_' && level < 3 && i > offset) {
                continue;
            }


            /*
             * Shortcut: we might not want to escape non-ASCII chars at all either.
             */
            if (codepoint > (ESCAPE_LEVELS_LEN - 2) && level < ESCAPE_LEVELS[ESCAPE_LEVELS_LEN - 1]) {

                if (Character.charCount(codepoint) > 1) {
                    // This is to compensate that we are actually escaping two char[] positions with a single codepoint.
                    i++;
                }

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
             * ------------------------------------------------------------------------------------------
             *
             * Peform the real escape, attending the different combinations of BACKSLASH and HEXA escapes
             *
             * ------------------------------------------------------------------------------------------
             */

            if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN) {
                // We will try to use a BACKSLASH ESCAPE

                final char sec = BACKSLASH_CHARS[codepoint];

                if (sec != BACKSLASH_CHARS_NO_SEC) {
                    // SEC found! just write it and go for the next char
                    strBuilder.append(ESCAPE_PREFIX);
                    strBuilder.append(sec);
                    continue;
                }

            }

            /*
             * No SEC-escape was possible, so we need hexa escape (compact or 6-digit).
             */

            if (useCompactHexa) {
                strBuilder.append(ESCAPE_PREFIX);
                strBuilder.append(toCompactHexa(codepoint));
                continue;
            }

            strBuilder.append(ESCAPE_PREFIX);
            strBuilder.append(toSixDigitHexa(codepoint));

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
     * Perform an escape operation, based on char[], according to the specified level and type.
     */
    static void escape(final char[] text, final int offset, final int len, final Writer writer,
                       final CSSIdentifierEscapeType escapeType, final CSSIdentifierEscapeLevel escapeLevel)
                       throws IOException {

        if (text == null || text.length == 0) {
            return;
        }

        final int level = escapeLevel.getEscapeLevel();
        final boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        final boolean useCompactHexa = escapeType.getUseCompactHexa();

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
             * Shortcut: most characters will be ASCII/Alphanumeric, and we won't need to do anything at
             * all for them
             */
            if (codepoint <= (ESCAPE_LEVELS_LEN - 2) && level < ESCAPE_LEVELS[codepoint] &&
                    (i > offset || codepoint < '0' || codepoint > '9')) {
                // Note how we check whether the first char is a decimal number, in which case we have to escape it
                continue;
            }


            /*
             * Hyphen check: only escape when it's the first char and it's followed by '-' or a digit.
             */
            if (codepoint == '-' && level < 3) {
                if (i > offset || i + 1 >= max) {
                    continue;
                }
                final char c1 = text[i + 1];
                if (c1 != '-' && (c1 < '0' || c1 > '9')) {
                    continue;
                }
            }


            /*
             * Underscore check: only escape when it's the first char.
             */
            if (codepoint == '_' && level < 3 && i > offset) {
                continue;
            }


            /*
             * Shortcut: we might not want to escape non-ASCII chars at all either.
             */
            if (codepoint > (ESCAPE_LEVELS_LEN - 2) && level < ESCAPE_LEVELS[ESCAPE_LEVELS_LEN - 1]) {

                if (Character.charCount(codepoint) > 1) {
                    // This is to compensate that we are actually escaping two char[] positions with a single codepoint.
                    i++;
                }

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
             * ------------------------------------------------------------------------------------------
             *
             * Peform the real escape, attending the different combinations of BACKSLASH and HEXA escapes
             *
             * ------------------------------------------------------------------------------------------
             */

            if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN) {
                // We will try to use a BACKSLASH ESCAPE

                final char sec = BACKSLASH_CHARS[codepoint];

                if (sec != BACKSLASH_CHARS_NO_SEC) {
                    // SEC found! just write it and go for the next char
                    writer.write(ESCAPE_PREFIX);
                    writer.write(sec);
                    continue;
                }

            }

            /*
             * No SEC-escape was possible, so we need hexa escape (compact or 6-digit).
             */

            if (useCompactHexa) {
                writer.write(ESCAPE_PREFIX);
                writer.write(toCompactHexa(codepoint));
                continue;
            }

            writer.write(ESCAPE_PREFIX);
            writer.write(toSixDigitHexa(codepoint));

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: append the remaining unescaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

    }








    /*
     * This methods (the two versions) are used instead of Integer.parseInt(str,radix) in order to avoid the need
     * to create substrings of the text being unescaped to feed such method.
     * -  No need to check all chars are within the radix limits - reference parsing code will already have done so.
     */

    static int parseIntFromReference(final String text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text.charAt(i);
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
        }
        return result;
    }

    static int parseIntFromReference(final char[] text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text[i];
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
        }
        return result;
    }





    /*
     * Perform an unescape operation based on String.
     */
    static String unescape(final String text) {

//        if (text == null) {
//            return null;
//        }
//
//        StringBuilder strBuilder = null;
//
//        final int offset = 0;
//        final int max = text.length();
//
//        int readOffset = offset;
//        int referenceOffset = offset;
//
//        for (int i = offset; i < max; i++) {
//
//            final char c = text.charAt(i);
//
//            /*
//             * Check the need for an unescape operation at this point
//             */
//
//            if (c != ESCAPE_PREFIX || (i + 1) >= max) {
//                continue;
//            }
//
//            int codepoint = -1;
//
//            if (c == ESCAPE_PREFIX) {
//
//                final char c1 = text.charAt(i + 1);
//
//                switch (c1) {
//                    case 'b': codepoint = 0x08; referenceOffset = i + 1; break;
//                    case 't': codepoint = 0x09; referenceOffset = i + 1; break;
//                    case 'n': codepoint = 0x0A; referenceOffset = i + 1; break;
//                    case 'f': codepoint = 0x0C; referenceOffset = i + 1; break;
//                    case 'r': codepoint = 0x0D; referenceOffset = i + 1; break;
//                    case '"': codepoint = 0x22; referenceOffset = i + 1; break;
//                    case '\\': codepoint = 0x5C; referenceOffset = i + 1; break;
//                    case '/': codepoint = 0x2F; referenceOffset = i + 1; break;
//                }
//
//                if (codepoint == -1) {
//
//                    if (c1 == ESCAPE_UHEXA_PREFIX2) {
//                        // This can be a uhexa escape, we need exactly four more characters
//
//                        int f = i + 2;
//                        while (f < (i + 6) && f < max) {
//                            final char cf = text.charAt(f);
//                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
//                                break;
//                            }
//                            f++;
//                        }
//
//                        if ((f - (i + 2)) < 4) {
//                            // We weren't able to consume the required four hexa chars, leave it as slash+'u', which
//                            // is invalid, and let the corresponding JSON parser fail.
//                            i++;
//                            continue;
//                        }
//
//                        codepoint = parseIntFromReference(text, i + 2, f, 16);
//
//                        // Fast-forward to the first char after the parsed codepoint
//                        referenceOffset = f - 1;
//
//                        // Don't continue here, just let the unescape code below do its job
//
//                    } else {
//
//                        // Other escape sequences are not allowed by JSON. So we leave it as is
//                        // and expect the corresponding JSON parser to fail.
//                        i++;
//                        continue;
//
//                    }
//
//                }
//
//            }
//
//
//            /*
//             * At this point we know for sure we will need some kind of unescape, so we
//             * can increase the offset and initialize the string builder if needed, along with
//             * copying to it all the contents pending up to this point.
//             */
//
//            if (strBuilder == null) {
//                strBuilder = new StringBuilder(max + 5);
//            }
//
//            if (i - readOffset > 0) {
//                strBuilder.append(text, readOffset, i);
//            }
//
//            i = referenceOffset;
//            readOffset = i + 1;
//
//            /*
//             * --------------------------
//             *
//             * Peform the real unescape
//             *
//             * --------------------------
//             */
//
//            if (codepoint > '\uFFFF') {
//                strBuilder.append(Character.toChars(codepoint));
//            } else {
//                strBuilder.append((char)codepoint);
//            }
//
//        }
//
//
//        /*
//         * -----------------------------------------------------------------------------------------------
//         * Final cleaning: return the original String object if no unescape was actually needed. Otherwise
//         *                 append the remaining escaped text to the string builder and return.
//         * -----------------------------------------------------------------------------------------------
//         */
//
//        if (strBuilder == null) {
//            return text;
//        }
//
//        if (max - readOffset > 0) {
//            strBuilder.append(text, readOffset, max);
//        }
//
//        return strBuilder.toString();

        return null;
    }






    /*
     * Perform an unescape operation based on char[].
     */
    static void unescape(final char[] text, final int offset, final int len, final Writer writer)
                         throws IOException {

        if (text == null) {
            return;
        }

        // TODO Copy from String version

    }



}

