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
final class PropertiesUnescapeUtil {



    /*
     * JAVA PROPERTIES UNESCAPE OPERATIONS
     * -----------------------------------
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
      *        escapes, in Java properties unicode escapes are just like any other type of escape, i.e. more similar
      *        to other languages like JavaScript.
     *
     *   ------------------------
     *
     */



    /*
     * Prefixes defined for use in escape and unescape operations
     */
    private static final char ESCAPE_PREFIX = '\\';
    private static final char ESCAPE_UHEXA_PREFIX2 = 'u';

    /*
     * Small utility char arrays for hexadecimal conversion.
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();






    private PropertiesUnescapeUtil() {
        super();
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

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != ESCAPE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = -1;

            if (c == ESCAPE_PREFIX) {

                final char c1 = text.charAt(i + 1);

                switch (c1) {
                    case 't': codepoint = 0x09; referenceOffset = i + 1; break;
                    case 'n': codepoint = 0x0A; referenceOffset = i + 1; break;
                    case 'f': codepoint = 0x0C; referenceOffset = i + 1; break;
                    case 'r': codepoint = 0x0D; referenceOffset = i + 1; break;
                    case '\\': codepoint = 0x5C; referenceOffset = i + 1; break;
                }

                if (codepoint == -1) {

                    if (c1 == ESCAPE_UHEXA_PREFIX2) {
                        // This can be a uhexa escape, we need exactly four more characters

                        int f = i + 2;
                        while (f < (i + 6) && f < max) {
                            final char cf = text.charAt(f);
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 2)) < 4) {
                            // We weren't able to consume the required four hexa chars, leave it as slash+'u', which
                            // is invalid, and let the corresponding Java Properties parser fail.
                            i++;
                            continue;
                        }

                        codepoint = parseIntFromReference(text, i + 2, f, 16);

                        // Fast-forward to the first char after the parsed codepoint
                        referenceOffset = f - 1;

                        // Don't continue here, just let the unescape code below do its job


                    } else {

                        // We weren't able to consume any valid escape chars, just consider it a normal char,
                        // which is allowed by the Java Properties specification

                        codepoint = (int) c1;
                        referenceOffset = i + 1;

                    }

                }

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

            i = referenceOffset;
            readOffset = i + 1;

            /*
             * --------------------------
             *
             * Peform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                strBuilder.append(Character.toChars(codepoint));
            } else {
                strBuilder.append((char)codepoint);
            }

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
    static void unescape(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {

        if (text == null) {
            return;
        }

        final int max = (offset + len);

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != ESCAPE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = -1;

            if (c == ESCAPE_PREFIX) {

                final char c1 = text[i + 1];

                switch (c1) {
                    case 't': codepoint = 0x09; referenceOffset = i + 1; break;
                    case 'n': codepoint = 0x0A; referenceOffset = i + 1; break;
                    case 'f': codepoint = 0x0C; referenceOffset = i + 1; break;
                    case 'r': codepoint = 0x0D; referenceOffset = i + 1; break;
                    case '\\': codepoint = 0x5C; referenceOffset = i + 1; break;
                }

                if (codepoint == -1) {

                    if (c1 == ESCAPE_UHEXA_PREFIX2) {
                        // This can be a uhexa escape, we need exactly four more characters

                        int f = i + 2;
                        while (f < (i + 6) && f < max) {
                            final char cf = text[f];
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 2)) < 4) {
                            // We weren't able to consume the required four hexa chars, leave it as slash+'u', which
                            // is invalid, and let the corresponding Java Properties parser fail.
                            i++;
                            continue;
                        }

                        codepoint = parseIntFromReference(text, i + 2, f, 16);

                        // Fast-forward to the first char after the parsed codepoint
                        referenceOffset = f - 1;

                        // Don't continue here, just let the unescape code below do its job


                    } else {

                        // We weren't able to consume any valid escape chars, just consider it a normal char,
                        // which is allowed by the Java Properties specification

                        codepoint = (int) c1;
                        referenceOffset = i + 1;

                    }

                }

            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * write all the contents pending up to this point.
             */

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            i = referenceOffset;
            readOffset = i + 1;

            /*
             * --------------------------
             *
             * Peform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else {
                writer.write((char)codepoint);
            }

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: writer the remaining escaped text and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

    }



}

