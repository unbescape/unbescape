/*
 * =============================================================================
 * 
 *   Copyright (c) 2014-2025 Unbescape (http://www.unbescape.org)
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
package org.unbescape.xml;

import java.io.IOException;
import java.io.Reader;
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
final class XmlEscapeUtil {



    /*
     * GLOSSARY
     * ------------------------
     *
     *   ER
     *      XML Entity Reference: references to variables used to define shortcuts to standard text or
     *      special characters. Entity references start with '&' and end with ';'.
     *
     *   CER
     *      Character Entity Reference: XML Entity Reference used to define a shortcut to a specific
     *      character. XML specifies five 'predefined' CERs: &lt; (<), &gt; (>), &amp; (&), &quot; (") and &apos; (').
     *
     *   DCER
     *      Decimal Character Entity Reference: base-10 numerical representation of an Unicode codepoint: &#225;
     *
     *   HCER
     *      Hexadecimal Character Reference: hexadecimal numerical representation of an Unicode codepoint: &#xE1;
     *      Note that XML only allows lower-case 'x' for defining hexadecimal character entity references (in contrast
     *      with HTML, which allows both '&#x...;' and '&#X...;').
     *
     *   Unicode Codepoint
     *      Each of the int values conforming the Unicode code space.
     *      Normally corresponding to a Java char primitive value (codepoint <= \uFFFF),
     *      but might be two chars for codepoints \u10000 to \u10FFFF if the first char is a high
     *      surrogate (\uD800 to \uDBFF) and the second is a low surrogate (\uDC00 to \uDFFF).
     *      See: http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     *
     */




    /*
     * Prefixes and suffix defined for use in decimal/hexa escape and unescape.
     */
    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3 = 'x';
    private static final char[] REFERENCE_DECIMAL_PREFIX = "&#".toCharArray();
    private static final char[] REFERENCE_HEXA_PREFIX = "&#x".toCharArray();
    private static final char REFERENCE_SUFFIX = ';';

    /*
     * Small utility char arrays for hexadecimal conversion.
     * Even if XML does not allow a lower-case 'x' for specifying hexadecimal entity references, it does not
     * force hexadecimal digits themselves to be lower-case.
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();




    private XmlEscapeUtil() {
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

            final int codepoint = Character.codePointAt(text, i);


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
             * Perform the real escape, attending the different combinations of NCR, DCR and HCR needs.
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
     * Perform an escape operation, based on a Reader, according to the specified level and type and writing the
     * result to a Writer.
     *
     * Note this reader is going to be read char-by-char, so some kind of buffering might be appropriate if this
     * is an inconvenience for the specific Reader implementation.
     */
    static void escape(
            final Reader reader, final Writer writer, final XmlEscapeSymbols symbols,
            final XmlEscapeType escapeType, final XmlEscapeLevel escapeLevel)
            throws IOException {

        if (reader == null) {
            return;
        }

        final int level = escapeLevel.getEscapeLevel();
        final boolean useCERs = escapeType.getUseCERs();
        final boolean useHexa = escapeType.getUseHexa();

        int c1, c2; // c0: last char, c1: current char, c2: next char

        c2 = reader.read();

        while (c2 >= 0) {

            c1 = c2;
            c2 = reader.read();

            final int codepoint = codePointAt((char)c1, (char)c2);


            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);


            /*
             * Shortcut: most characters will be ASCII/Alphanumeric, and we won't need to do anything at
             * all for them
             */
            if (codepoint <= (XmlEscapeSymbols.LEVELS_LEN - 2)
                    && level < symbols.ESCAPE_LEVELS[codepoint]
                    && codepointValid) {
                writer.write(c1);
                continue;
            }


            /*
             * Shortcut: we might not want to escape non-ASCII chars at all either.
             */
            if (codepoint > (XmlEscapeSymbols.LEVELS_LEN - 2)
                    && level < symbols.ESCAPE_LEVELS[XmlEscapeSymbols.LEVELS_LEN - 1]
                    && codepointValid) {

                writer.write(c1);

                if (Character.charCount(codepoint) > 1) {
                    // This is to compensate that we are actually escaping two char[] positions with a single codepoint.

                    writer.write(c2);

                    c1 = c2;
                    c2 = reader.read();

                }

                continue;

            }


            /*
             * We know we need to escape, so from here on we will only work with the codepoint -- we can advance
             * the chars.
             */

            if (Character.charCount(codepoint) > 1) {
                // This is to compensate that we are actually reading two char positions with a single codepoint.
                c1 = c2;
                c2 = reader.read();
            }


            /*
             * If the char is invalid, there is nothing to write, simply skip it (which we already did by
             * incrementing the readOffset.
             */
            if (!codepointValid) {
                // nothing to write
                continue;
            }


            /*
             * -----------------------------------------------------------------------------------------
             *
             * Perform the real escape, attending the different combinations of NCR, DCR and HCR needs.
             *
             * -----------------------------------------------------------------------------------------
             */

            if (useCERs) {
                // We will try to use a CER

                final int codepointIndex =
                        Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint);

                if (codepointIndex >= 0) {
                    // CER found! just write it and go for the next char
                    writer.write(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                    continue;
                }

            }

            /*
             * No NCR-escape was possible (or allowed), so we need decimal/hexa escape.
             */

            if (useHexa) {
                writer.write(REFERENCE_HEXA_PREFIX);
                writer.write(Integer.toHexString(codepoint));
            } else {
                writer.write(REFERENCE_DECIMAL_PREFIX);
                writer.write(String.valueOf(codepoint));
            }
            writer.write(REFERENCE_SUFFIX);

        }

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

        final int level = escapeLevel.getEscapeLevel();
        final boolean useCERs = escapeType.getUseCERs();
        final boolean useHexa = escapeType.getUseHexa();

        final int max = (offset + len);

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final int codepoint = Character.codePointAt(text, i);


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
                    // This is to compensate that we are actually reading two char[] positions with a single codepoint.
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
                // This is to compensate that we are actually escaping two char[] positions with a single codepoint.
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
             * Perform the real escape, attending the different combinations of NCR, DCR and HCR needs.
             *
             * -----------------------------------------------------------------------------------------
             */

            if (useCERs) {
                // We will try to use a CER

                final int codepointIndex =
                        Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint);

                if (codepointIndex >= 0) {
                    // CER found! just write it and go for the next char
                    writer.write(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                    continue;
                }

            }

            /*
             * No NCR-escape was possible (or allowed), so we need decimal/hexa escape.
             */

            if (useHexa) {
                writer.write(REFERENCE_HEXA_PREFIX);
                writer.write(Integer.toHexString(codepoint));
            } else {
                writer.write(REFERENCE_DECIMAL_PREFIX);
                writer.write(String.valueOf(codepoint));
            }
            writer.write(REFERENCE_SUFFIX);

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: append the remaining unescaped text to the writer and return.
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
             * Perform the real unescape
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
     * Perform an unescape operation based on a Reader, writing the results to a Writer.
     *
     * Note this reader is going to be read char-by-char, so some kind of buffering might be appropriate if this
     * is an inconvenience for the specific Reader implementation.
     */
    static void unescape(final Reader reader, final Writer writer, final XmlEscapeSymbols symbols) throws IOException {

        if (reader == null) {
            return;
        }

        char[] escapes = new char[10];
        int escapei = 0;

        int c1, c2, ce; // c1: current char, c2: next char, ce: current escaped char

        c2 = reader.read();

        while (c2 >= 0) {

            c1 = c2;
            c2 = reader.read();

            escapei = 0;

            /*
             * Check the need for an unescape operation at this point
             */

            if (c1 != REFERENCE_PREFIX || c2 < 0) {
                writer.write(c1);
                continue;
            }

            int codepoint = 0;

            if (c1 == REFERENCE_PREFIX) {

                if (c2 == '\u0020' || // SPACE
                        c2 == '\n' ||     // LF
                        c2 == '\u0009' || // TAB
                        c2 == '\u000C' || // FF
                        c2 == '\u003C' || // LES-THAN SIGN
                        c2 == '\u0026') { // AMPERSAND
                    // Not a character references. No characters are consumed, and nothing is returned.
                    writer.write(c1);
                    continue;

                } else if (c2 == REFERENCE_NUMERIC_PREFIX2) {

                    final int c3 = reader.read();

                    if (c3 < 0) {
                        // No reference possible
                        writer.write(c1);
                        writer.write(c2);
                        c1 = c2;
                        c2 = c3;
                        continue;
                    }

                    if (c3 == REFERENCE_HEXA_PREFIX3) {
                        // This is a hexadecimal reference

                        ce = reader.read();
                        while (ce >= 0) {
                            if (!((ce >= '0' && ce <= '9') || (ce >= 'A' && ce <= 'F') || (ce >= 'a' && ce <= 'f'))) {
                                break;
                            }
                            if (escapei == escapes.length) {
                                // too many escape chars for our array: grow it!
                                final char[] newEscapes = new char[escapes.length + 4];
                                System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                                escapes = newEscapes;
                            }
                            escapes[escapei] = (char) ce;
                            ce = reader.read();
                            escapei++;
                        }

                        if (escapei == 0) {
                            // We weren't able to consume any hexa chars
                            writer.write(c1);
                            writer.write(c2);
                            writer.write(c3);
                            c1 = c3;
                            c2 = ce;
                            continue;
                        }

                        if (ce != REFERENCE_SUFFIX) {
                            // If the reference does not end in a ';', it's not a valid reference
                            writer.write(c1);
                            writer.write(c2);
                            writer.write(c3);
                            writer.write(escapes, 0, escapei);
                            c1 = escapes[escapei - 1];
                            c2 = ce;
                            continue;
                        }

                        c1 = ce;
                        c2 = reader.read();

                        codepoint = parseIntFromReference(escapes, 0, escapei, 16);

                        // Don't continue here, just let the unescape code below do its job

                    } else if (c3 >= '0' && c3 <= '9') {
                        // This is a decimal reference

                        ce = c3;
                        while (ce >= 0) {
                            if (!(ce >= '0' && ce <= '9')) {
                                break;
                            }
                            if (escapei == escapes.length) {
                                // too many escape chars for our array: grow it!
                                final char[] newEscapes = new char[escapes.length + 4];
                                System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                                escapes = newEscapes;
                            }
                            escapes[escapei] = (char) ce;
                            ce = reader.read();
                            escapei++;
                        }

                        if (escapei == 0) {
                            // We weren't able to consume any decimal chars
                            writer.write(c1);
                            writer.write(c2);
                            c1 = c2;
                            c2 = c3;
                            continue;
                        }

                        if (ce != REFERENCE_SUFFIX) {
                            // If the reference does not end in a ';', it's not a valid reference
                            writer.write(c1);
                            writer.write(c2);
                            writer.write(escapes, 0, escapei);
                            c1 = escapes[escapei - 1];
                            c2 = ce;
                            continue;
                        }

                        c1 = ce;
                        c2 = reader.read();

                        codepoint = parseIntFromReference(escapes, 0, escapei, 10);

                        // Don't continue here, just let the unescape code below do its job

                    } else {
                        // This is not a valid reference, just discard
                        writer.write(c1);
                        writer.write(c2);
                        c1 = c2;
                        c2 = c3;
                        continue;
                    }


                } else {

                    // This is a named reference, must be comprised only of ALPHABETIC chars

                    ce = c2;
                    while (ce >= 0) {
                        if (!((ce >= '0' && ce <= '9') || (ce >= 'A' && ce <= 'Z') || (ce >= 'a' && ce <= 'z'))) {
                            break;
                        }
                        if (escapei == escapes.length) {
                            // too many escape chars for our array: grow it!
                            final char[] newEscapes = new char[escapes.length + 4];
                            System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                            escapes = newEscapes;
                        }
                        escapes[escapei] = (char) ce;
                        ce = reader.read();
                        escapei++;
                    }

                    if (escapei == 0) {
                        // We weren't able to consume any decimal chars
                        writer.write(c1);
                        continue;
                    }

                    if (escapei + 2 >= escapes.length) {
                        // the entire escape sequence does not fit: grow it!
                        final char[] newEscapes = new char[escapes.length + 4];
                        System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                        escapes = newEscapes;
                    }

                    System.arraycopy(escapes, 0, escapes, 1, escapei);
                    escapes[0] = (char) c1;
                    escapei++;

                    if (ce == REFERENCE_SUFFIX) {
                        // If the reference ends in a ';', just consume it
                        escapes[escapei++] = (char) ce;
                        ce = reader.read();
                    }

                    c1 = escapes[escapei - 1];
                    c2 = ce;

                    final int ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, escapes, 0, escapei);
                    if (ncrPosition >= 0) {
                        codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                    } else {
                        // Not found! Just ignore our efforts to find a match.
                        writer.write(escapes, 0, escapei);
                        continue;
                    }

                }

            }

            /*
             * --------------------------
             *
             * Perform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else {
                writer.write((char)codepoint);
            }

        }

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

        final int max = (offset + len);

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != REFERENCE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = 0;

            if (c == REFERENCE_PREFIX) {

                final char c1 = text[i + 1];

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

                    final char c2 = text[i + 2];

                    if (c2 == REFERENCE_HEXA_PREFIX3 && (i + 3) < max) {
                        // This is a hexadecimal reference

                        int f = i + 3;
                        while (f < max) {
                            final char cf = text[f];
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 3)) <= 0) {
                            // We weren't able to consume any hexa chars
                            continue;
                        }

                        if ((f >= max) || text[f] != REFERENCE_SUFFIX) {
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
                            final char cf = text[f];
                            if (!(cf >= '0' && cf <= '9')) {
                                break;
                            }
                            f++;
                        }

                        if ((f - (i + 2)) <= 0) {
                            // We weren't able to consume any decimal chars
                            continue;
                        }

                        if ((f >= max) || text[f] != REFERENCE_SUFFIX) {
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
                        final char cf = text[f];
                        if (!((cf >= 'a' && cf <= 'z') || (cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9'))) {
                            break;
                        }
                        f++;
                    }

                    if ((f - (i + 1)) <= 0) {
                        // We weren't able to consume any alphanumeric
                        continue;
                    }

                    if ((f < max) && text[f] == REFERENCE_SUFFIX) {
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
             * Perform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else {
                writer.write((char) codepoint);
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




    private static int codePointAt(final char c1, final char c2) {
        if (Character.isHighSurrogate(c1)) {
            if (c2 >= 0) {
                if (Character.isLowSurrogate(c2)) {
                    return Character.toCodePoint(c1, c2);
                }
            }
        }
        return c1;
    }



}

