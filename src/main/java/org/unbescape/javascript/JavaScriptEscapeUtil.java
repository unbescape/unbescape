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
package org.unbescape.javascript;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.unbescape.xml.XmlEscapeLevel;
import org.unbescape.xml.XmlEscapeSymbols;
import org.unbescape.xml.XmlEscapeType;

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
final class JavaScriptEscapeUtil {



    /*
     * JAVASCRIPT ESCAPE/UNESCAPE OPERATIONS
     * -------------------------------------
     *
     *   See: http://www.ecmascript.org/docs.php
     *        http://mathiasbynens.be/notes/javascript-escapes
     *
     *   - SINGLE ESCAPE CHARACTERS (SECs):
     *        U+0000 -> \0
     *        U+0008 -> \b
     *        U+0009 -> \t
     *        U+000A -> \n
     *        U+000B -> \v  [NOT USED IN ESCAPE - Not supported by Internet Explorer < 9]
     *        U+000C -> \f
     *        U+000D -> \r
     *        U+0022 -> \"
     *        U+0027 -> \'
     *        U+005C -> \\
     *   - HEXADECIMAL ESCAPE [XHEXA] (only for characters <= U+00FF): \x??
     *   - UNICODE ESCAPE [UHEXA] (also hexadecimal)
     *        Characters <= U+FFFF: \u????
     *        Characters > U+FFFF : \u????\u???? (high + low surrogate chars)
     *                              \u{?*} [NOT USED - Possible syntax for ECMAScript 6]
     *   - OCTAL ESCAPE: \??? [NOT USED IN ESCAPE- Deprecated]
     *   - GENERAL ESCAPE: \* -> * ('\a' -> 'a')
     *                     (except the [\,\n] sequence, which is not an escape sequence but a line continuation)
     *
     */



    /*
     * Prefixes defined for use in escape and unescape operations
     */
    private static final char ESCAPE_PREFIX = '\\';
    private static final char ESCAPE_XHEXA_PREFIX2 = 'x';
    private static final char ESCAPE_UHEXA_PREFIX2 = 'u';
    private static final char[] ESCAPE_XHEXA_PREFIX = "\\x".toCharArray();
    private static final char[] ESCAPE_UHEXA_PREFIX = "\\u".toCharArray();

    /*
     * Small utility char arrays for hexadecimal conversion.
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();




    private JavaScriptEscapeUtil() {
        super();
    }





    /*
     * Perform an escape operation, based on String, according to the specified level and type.
     */
    static String escape(final String text, final XmlEscapeSymbols symbols,
                         final XmlEscapeType escapeType, final XmlEscapeLevel escapeLevel) {

        if (text == null) {
            return null;
        }

        final int level = escapeLevel.getEscapeLevel();
        final boolean useCERs = escapeType.getUseCERs();
        final boolean useHexa = escapeType.getUseHexa();

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


            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);


            /*
             * Shortcut: most characters will be ASCII/Alphanumeric, and we won't need to do anything at
             * all for them
             */
            if (codepoint <= (XmlEscapeSymbols.LEVELS_LEN - 2)
                    && level < symbols.ESCAPE_LEVELS[codepoint]
                    && codepointValid) {
                continue;
            }


            /*
             * Shortcut: we might not want to escape non-ASCII chars at all either.
             */
            if (codepoint > (XmlEscapeSymbols.LEVELS_LEN - 2)
                    && level < symbols.ESCAPE_LEVELS[XmlEscapeSymbols.LEVELS_LEN - 1]
                    && codepointValid) {

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
             * If the char is invalid, there is nothing to write, simply skip it (which we already did by
             * incrementing the readOffset.
             */
            if (!codepointValid) {
                continue;
            }


            /*
             * -----------------------------------------------------------------------------------------
             *
             * Peform the real escape, attending the different combinations of NCR, DCR and HCR needs.
             *
             * -----------------------------------------------------------------------------------------
             */

            if (useCERs) {
                // We will try to use a CER

                final int codepointIndex =
                        Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint);

                if (codepointIndex >= 0) {
                    // CER found! just write it and go for the next char
                    strBuilder.append(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                    continue;
                }

            }

            /*
             * No NCR-escape was possible (or allowed), so we need decimal/hexa escape.
             */

            if (useHexa) {
                strBuilder.append(REFERENCE_HEXA_PREFIX);
                strBuilder.append(Integer.toHexString(codepoint));
            } else {
                strBuilder.append(REFERENCE_DECIMAL_PREFIX);
                strBuilder.append(String.valueOf(codepoint));
            }
            strBuilder.append(REFERENCE_SUFFIX);

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
                       final XmlEscapeSymbols symbols,
                       final XmlEscapeType escapeType, final XmlEscapeLevel escapeLevel)
                       throws IOException {

        if (text == null || text.length == 0) {
            return;
        }

        // TODO Copy from the String version

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
    static String unescape(final String text, final XmlEscapeSymbols symbols) {

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

            if (c != REFERENCE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = 0;

            if (c == REFERENCE_PREFIX) {

                final char c1 = text.charAt(i + 1);

                if (c1 == '\u0020' || // SPACE
                    c1 == '\n' ||     // LF
                    c1 == '\u0009' || // TAB
                    c1 == '\u000C' || // FF
                    c1 == '\u003C' || // LES-THAN SIGN
                    c1 == '\u0026') { // AMPERSAND
                    // Not a character references. No characters are consumed, and nothing is returned.
                    continue;

                } else if (c1 == REFERENCE_NUMERIC_PREFIX2) {

                    if (i + 2 >= max) {
                        // No reference possible
                        continue;
                    }

                    final char c2 = text.charAt(i + 2);

                    if (c2 == REFERENCE_HEXA_PREFIX3 && (i + 3) < max) {
                        // This is a hexadecimal reference

                        int f = i + 3;
                        while (f < max) {
                            final char cf = text.charAt(f);
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 3)) <= 0) {
                            // We weren't able to consume any hexa chars
                            continue;
                        }

                        if ((f >= max) || text.charAt(f) != REFERENCE_SUFFIX) {
                            continue;
                        }

                        f++; // Count the REFERENCE_SUFFIX (semi-colon)

                        codepoint = parseIntFromReference(text, i + 3, f - 1, 16);
                        referenceOffset = f - 1;

                        // Don't continue here, just let the unescape code below do its job

                    } else if (c2 >= '0' && c2 <= '9') {
                        // This is a decimal reference

                        int f = i + 2;
                        while (f < max) {
                            final char cf = text.charAt(f);
                            if (!(cf >= '0' && cf <= '9')) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 2)) <= 0) {
                            // We weren't able to consume any decimal chars
                            continue;
                        }

                        if ((f >= max) || text.charAt(f) != REFERENCE_SUFFIX) {
                            continue;
                        }

                        f++; // Count the REFERENCE_SUFFIX (semi-colon)

                        codepoint = parseIntFromReference(text, i + 2, f - 1, 10);
                        referenceOffset = f - 1;

                        // Don't continue here, just let the unescape code below do its job

                    } else {
                        // This is not a valid reference, just discard
                        continue;
                    }


                } else {

                    // This is a named reference, must be comprised only of ALPHABETIC chars

                    int f = i + 1;
                    while (f < max) {
                        final char cf = text.charAt(f);
                        if (!((cf >= 'a' && cf <= 'z') || (cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9'))) {
                            break;
                        }
                        f++;
                    }

                    if ((f - (i + 1)) <= 0) {
                        // We weren't able to consume any alphanumeric
                        continue;
                    }

                    if ((f < max) && text.charAt(f) == REFERENCE_SUFFIX) {
                        f++;
                    }

                    final int ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, text, i, f);
                    if (ncrPosition >= 0) {
                        codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                    } else {
                        // Not found! Just ignore our efforts to find a match.
                        continue;
                    }

                    referenceOffset = f - 1;

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
    static void unescape(final char[] text, final int offset, final int len, final Writer writer,
                         final XmlEscapeSymbols symbols)
                         throws IOException {

        if (text == null) {
            return;
        }


        // TODO Copy from the String version


    }



}

