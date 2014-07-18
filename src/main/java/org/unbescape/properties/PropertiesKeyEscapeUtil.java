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
package org.unbescape.properties;

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
 * @since 1.0.0
 *
 */
final class PropertiesKeyEscapeUtil {



    /*
     * JAVA PROPERTIES KEY ESCAPE OPERATIONS
     * -------------------------------------
     *
     *   See: http://en.wikipedia.org/wiki/.properties
     *        http://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-
     *
     *   (Note that, in the following examples, and in order to avoid escape problems during the compilation
     *    of this class, the backslash symbol is replaced by '%')
     *
     *   - SINGLE ESCAPE CHARACTERS (SECs):
     *        U+0009 -> %t
     *        U+000A -> %n
     *        U+000C -> %f
     *        U+000D -> %r
     *        U+0020 -> %  [ ONLY REQUIRED IN KEYS ]
     *        U+003A -> %: [ ONLY REQUIRED IN KEYS ]
     *        U+003D -> %= [ ONLY REQUIRED IN KEYS ]
     *        U+005C -> %%
     *   - UNICODE ESCAPE [UHEXA]
     *        Characters <= U+FFFF: %u????
     *        Characters > U+FFFF : %u????%u???? (surrogate character pair)
     *
     *
     *   ------------------------
     *
     *   NOTE: In Java .properties files, there is a difference between how keys and values must be escaped. Keys
     *         require the escape of ':', '=' and whitespaces (U+0020), because the first of these characters (or a
     *         combination of them) found in a .properties line is considered part of the key-value separator.
     *
     *   ------------------------
     *
     *   NOTE: The way unicode (UHEXA) escapes are treated in Java literals and Java properties is different: whereas
     *         in Java literals these are processed by the compiler and therefore processed before any other kind of
     *         escapes, in Java properties unicode escapes are just like any other type of escape, i.e. more similar
     *         to other languages like JavaScript.
     *
     *   ------------------------
     *
     */



    /*
     * Prefixes defined for use in escape and unescape operations
     */
    private static final char ESCAPE_PREFIX = '\\';
    private static final char[] ESCAPE_UHEXA_PREFIX = "\\u".toCharArray();

    /*
     * Small utility char arrays for hexadecimal conversion.
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();


    /*
     * Structures for holding the Single Escape Characters
     */
    private static int SEC_CHARS_LEN = '\\' + 1; // 0x5C + 1 = 0x5D
    private static char SEC_CHARS_NO_SEC = '*';
    private static char[] SEC_CHARS;

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
        SEC_CHARS = new char[SEC_CHARS_LEN];
        Arrays.fill(SEC_CHARS,SEC_CHARS_NO_SEC);
        SEC_CHARS[0x09] = 't';
        SEC_CHARS[0x0A] = 'n';
        SEC_CHARS[0x0C] = 'f';
        SEC_CHARS[0x0D] = 'r';
        SEC_CHARS[0x20] = ' ';
        SEC_CHARS[0x3A] = ':';
        SEC_CHARS[0x3B] = '=';
        SEC_CHARS[0x5C] = '\\';



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
         * Simple Escape Character will be level 1 (always escaped)
         */
        ESCAPE_LEVELS[0x09] = 1;
        ESCAPE_LEVELS[0x0A] = 1;
        ESCAPE_LEVELS[0x0C] = 1;
        ESCAPE_LEVELS[0x0D] = 1;
        ESCAPE_LEVELS[0x20] = 1;
        ESCAPE_LEVELS[0x3A] = 1;
        ESCAPE_LEVELS[0x3B] = 1;
        ESCAPE_LEVELS[0x5C] = 1;

        /*
         * Java defines one ranges of non-displayable, control characters: U+0000 to U+001F.
         * Additionally, the U+007F to U+009F range is also escaped (which is allowed).
         */
        for (char c = 0x00; c <= 0x1F; c++) {
            ESCAPE_LEVELS[c] = 1;
        }
        for (char c = 0x7F; c <= 0x9F; c++) {
            ESCAPE_LEVELS[c] = 1;
        }

    }



    private PropertiesKeyEscapeUtil() {
        super();
    }




    static char[] toUHexa(final int codepoint) {
        final char[] result = new char[4];
        result[3] = HEXA_CHARS_UPPER[codepoint % 0x10];
        result[2] = HEXA_CHARS_UPPER[(codepoint >>> 4) % 0x10];
        result[1] = HEXA_CHARS_UPPER[(codepoint >>> 8) % 0x10];
        result[0] = HEXA_CHARS_UPPER[(codepoint >>> 12) % 0x10];
        return result;
    }



    /*
     * Perform an escape operation, based on String, according to the specified level.
     */
    static String escape(final String text, final PropertiesKeyEscapeLevel escapeLevel) {

        if (text == null) {
            return null;
        }

        final int level = escapeLevel.getEscapeLevel();

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
            if (codepoint <= (ESCAPE_LEVELS_LEN - 2) && level < ESCAPE_LEVELS[codepoint]) {
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
             * -----------------------------------------------------------------------------------------
             *
             * Peform the real escape, attending the different combinations of SECs and UHEXA
             *
             * -----------------------------------------------------------------------------------------
             */

            if (codepoint < SEC_CHARS_LEN) {
                // We will try to use a SEC

                final char sec = SEC_CHARS[codepoint];

                if (sec != SEC_CHARS_NO_SEC) {
                    // SEC found! just write it and go for the next char
                    strBuilder.append(ESCAPE_PREFIX);
                    strBuilder.append(sec);
                    continue;
                }

            }

            /*
             * No SEC-escape was possible, so we need uhexa escape.
             */

            if (Character.charCount(codepoint) > 1) {
                final char[] codepointChars = Character.toChars(codepoint);
                strBuilder.append(ESCAPE_UHEXA_PREFIX);
                strBuilder.append(toUHexa(codepointChars[0]));
                strBuilder.append(ESCAPE_UHEXA_PREFIX);
                strBuilder.append(toUHexa(codepointChars[1]));
                continue;
            }

            strBuilder.append(ESCAPE_UHEXA_PREFIX);
            strBuilder.append(toUHexa(codepoint));

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
     * Perform an escape operation, based on char[], according to the specified level.
     */
    static void escape(final char[] text, final int offset, final int len, final Writer writer,
                       final PropertiesKeyEscapeLevel escapeLevel)
                       throws IOException {

        if (text == null || text.length == 0) {
            return;
        }

        final int level = escapeLevel.getEscapeLevel();

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
            if (codepoint <= (ESCAPE_LEVELS_LEN - 2) && level < ESCAPE_LEVELS[codepoint]) {
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
             * copy all the contents pending up to this point.
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
             * Peform the real escape, attending the different combinations of SECs and UHEXA
             *
             * -----------------------------------------------------------------------------------------
             */

            if (codepoint < SEC_CHARS_LEN) {
                // We will try to use a SEC

                final char sec = SEC_CHARS[codepoint];

                if (sec != SEC_CHARS_NO_SEC) {
                    // SEC found! just write it and go for the next char
                    writer.write(ESCAPE_PREFIX);
                    writer.write(sec);
                    continue;
                }

            }

            /*
             * No SEC-escape was possible, so we need uhexa escape.
             */

            if (Character.charCount(codepoint) > 1) {
                final char[] codepointChars = Character.toChars(codepoint);
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(toUHexa(codepointChars[0]));
                writer.write(ESCAPE_UHEXA_PREFIX);
                writer.write(toUHexa(codepointChars[1]));
                continue;
            }

            writer.write(ESCAPE_UHEXA_PREFIX);
            writer.write(toUHexa(codepoint));

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



}

