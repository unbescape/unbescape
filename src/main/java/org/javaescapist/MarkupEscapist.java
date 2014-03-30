/*
 * =============================================================================
 * 
 *   Copyright (c) 2014, The JAVAESCAPIST team (http://www.javaescapist.org)
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
package org.javaescapist;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class MarkupEscapist {


    static enum MarkupEscapeType {
        NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
        NAMED_REFERENCES_DEFAULT_TO_HEXA,
        DECIMAL_REFERENCES,
        HEXADECIMAL_REFERENCES
    }

    static enum BaseImmunityType {
        BASE_IMMUNITY_ALL_ASCII,
        BASE_IMMUNITY_ONLY_ALPHANUMERIC
    }


    /*
     * GLOSSARY
     * ------------------------
     *    NCR: Named Character Reference:       &aacute;
     *    DCR: Decimal Character Reference:     &#225;
     *    HCR: Hexadecimal Character Reference: &#xE1;
     *    Codepoint: each of the int values conforming the Unicode code space.
     *               Normally corresponding to a Java char primitive value (codepoint <= U+FFFF), but might be two
     *               chars for codepoints U+FFFF to U+10FFFF if the first char is a high surrogate (\uD800 to \uDBFF)
     *               and the second is a low surrogate (\uDC00 to \uDFFF).
     *               See: http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */



    /*
     * Length of the array used for holding the 'base' NCRS indexed by the codepoints themselves. This size
     * (0x2fff - 12287) is considered enough to hold most of the NCRS that should be needed (HTML4 has 252
     * NCRs with a maximum codepoint of 0x2666 - HTML5 has 2125 NCRs with a maximum codepoint of 120171, but
     * only 138 scarcely used NCRs live above codepoint 0x2fff so an overflow map should be enough for
     * those 138 cases).
     */
    private static final int NCRS_BY_CODEPOINT_LEN = 0x2fff;

    /*
     * This array will contain the NCRs for the first NCRS_BY_CODEPOINT_LEN (0x2fff) codepoints, indexed by
     * the codepoints themselves so that they (even in the form of mere char's) can be used for array random access.
     * - Values are short in order to index values at the SORTED_NCRS array. This avoids the need for this
     *   array to hold String pointers, which would be 4 bytes in size each (compared to shorts, which are 2 bytes).
     * - Chars themselves or int codepoints can (will, in fact) be used as indexes.
     * - Given values are short, the maximum amount of total references this class can handle is 0x7fff = 32767
     *   (which is safe, because HTML5 has 2125).
     * - All XML and HTML4 NCRs will fit in this array. In the case of HTML5 NCRs, only 138 of the 2125 will
     *   not fit here (NCRs assigned to codepoints > 0x2fff), and an overflow map will be provided for them.
     * - Approximate size will be 16 (header) + 12287 * 2 = 24590 bytes.
     */
    private final short[] NCRS_BY_CODEPOINT = new short[NCRS_BY_CODEPOINT_LEN];

    /*
     * This map will work as an overflow of the NCRS_BY_CODEPOINT array, so that the codepoint-to-NCR relation is
     * stored here (with hash-based access) for codepoints >= NCRS_BY_CODEPOINT_LEN (0x2fff).
     * - The use of a Map here still allows for reasonabily fast access for those rare cases in which codepoints above
     *   0x2fff are used.
     * - In the real world, this map will contain the 138 values needed by HTML5 for codepoints >= 0x2fff.
     * - Approximate max size will be (being a complex object like a Map, it's a rough approximation):
     *   16 (header) + 138 * (16 (entry header) + 16*2 (key, value headers) + 4 (key) + 2 (value)) = 7468 bytes
     */
    private final Map<Integer,Short> NCRS_BY_CODEPOINT_OVERFLOW;// No need to instantiate it until we know it's needed

    /*
     * This array will contain all the NCRs, alphabetically ordered.
     * - Positions in this array will correspond to positions in the SORTED_CODEPOINTS array, so that one array
     *   (this one) holds the NCRs while the other one holds the codepoint(s) such NCRs refer to.
     * - Gives the opportunity to store all NCRs in alphabetical order and therefore be able to perform
     *   binary search operations in order to quickly find NCRs (and translate to codepoints) when unescaping.
     * - Note this array will contain:
     *     * All NCRs referenced from NCRS_BY_CODEPOINT
     *     * NCRs whose codepoint is >= 0x2fff and therefore live in NCRS_BY_CODEPOINT_OVERFLOW
     *     * NCRs which are not referenced in any of the above because they are a shortcut for (and completely
     *       equivalent to) a sequence of two codepoints. These NCRs will only be unescaped, but never escaped.
     * - Max size in real world, when populated for HTML5: 2125 NCRs * 4 bytes/objref -> 8500 bytes, plus the texts.
     */
    private final char[][] SORTED_NCRS;

    /*
     * This array contains all the codepoints corresponding to the NCRs stored in SORTED_NCRS. This array is ordered
     * so that each index in SORTED_NCRS can also be used to retrieve the original CODEPOINT when used on this array.
     * - Values in this array can be positive (= single codepoint) or negative (= double codepoint, will need further
     *   resolution by means of the DOUBLE_CODEPOINTS array)
     * - Max size in real world, when populated for HTML5: 2125 NCRs * 4 bytes/objref -> 8500 bytes.
     */
    private final int[] SORTED_CODEPOINTS;


    /*
     * This array stores the sequences of two codepoints that are escaped as a single NCR. The indexes of this array are
     * referenced as negative numbers at the SORTED_CODEPOINTS array, and the values are int[2], containing the
     * sequence of codepoints. HTML4 has no NCRs like this, HTML5 has 93.
     * - Note this array is only used in UNESCAPE operations. Double-codepoint NCR escaping is not performed because
     *   the resulting characters are exactly equivalent to the escaping of the two codepoints separately.
     * - Max size in real world, when populated for HTML5 (rough approximate): 93 * (4 (ref) + 16 + 2 * 4) = 2604 bytes
     */
    private final int[][] DOUBLE_CODEPOINTS;


    /*
     * This constant will be used at the NCRS_BY_CODEPOINT array to specify there is no NCR associated with a
     * codepoint.
     */
    private static final short NO_NCR = (short) 0;


    /*
     * Constant defined for the highest possible ASCII char / codepoint value
     */
    private static final char MAX_ASCII_CHAR = '\u007f';


    /*
     * Prefixes and suffix defined for use in decimal/hexa escaping and unescaping.
     */
    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3 = 'x';
    private static final char[] REFERENCE_DECIMAL_PREFIX = "&#".toCharArray();
    private static final char[] REFERENCE_HEXA_PREFIX = "&#x".toCharArray();
    private static final char REFERENCE_SUFFIX = ';';

    /*
     * Small utility char arrays for hexadecimal conversion
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();




    MarkupEscapist(final References references) {

        super();

        // Initialize some auxiliary structures
        final List<char[]> ncrs = new ArrayList<char[]>(references.references.size() + 5);
        final List<Integer> codepoints = new ArrayList<Integer>(references.references.size() + 5);
        final List<int[]> doubleCodepoints = new ArrayList<int[]>(100);
        final Map<Integer,Short> ncrsByCodepointOverflow = new HashMap<Integer, Short>(20);

        // For each reference, initialize its corresponding codepoint -> ncr and ncr -> codepoint structures
        for (final Reference reference : references.references) {

            final char[] referenceNcr = reference.ncr;
            final int[] referenceCodepoints = reference.codepoints;

            ncrs.add(referenceNcr);

            if (referenceCodepoints.length == 1) {
                // Only one codepoint (might be > 1 chars, though), this is the normal case

                final int referenceCodepoint = referenceCodepoints[0];
                codepoints.add(Integer.valueOf(referenceCodepoint));

            } else if (referenceCodepoints.length == 2) {
                // Two codepoints, therefore this NCR will translate when unescaping into a two-codepoint
                // (probably two-char, too) sequence. We will use a negative codepoint value to signal this.

                doubleCodepoints.add(referenceCodepoints);
                // Will need to subtract one from its index when unescaping (codepoint = -1 -> position 0)
                codepoints.add(Integer.valueOf((-1) * doubleCodepoints.size()));

            } else {

                throw new RuntimeException(
                        "Unsupported codepoints #: " + referenceCodepoints.length + " for " + new String(referenceNcr));

            }

        }

        // We hadn't touched this array before. First thing to do is initialize it, as it will have a huge
        // amount of "empty" (i.e. non-assigned) values.
        Arrays.fill(NCRS_BY_CODEPOINT, NO_NCR);


        // We can initialize now these arrays that will hold the NCR-to-codepoint correspondence, but we cannot copy
        // them directly from our auxiliary structures because we need to order the NCRs alphabetically first.

        SORTED_NCRS = new char[ncrs.size()][];
        SORTED_CODEPOINTS = new int[codepoints.size()];

        final List<char[]> ncrsOrdered = new ArrayList<char[]>(ncrs);
        Collections.sort(ncrsOrdered, new Comparator<char[]>() {
            public int compare(final char[] o1, final char[] o2) {
                return new String(o1).compareTo(new String(o2));
            }
        });

        for (short i = 0; i < SORTED_NCRS.length; i++) {

            final char[] ncr = ncrsOrdered.get(i);
            SORTED_NCRS[i] = ncr;

            for (short j = 0; j  < SORTED_NCRS.length; j++) {

                if (Arrays.equals(ncr,ncrs.get(j))) {

                    final int cp = codepoints.get(j);
                    SORTED_CODEPOINTS[i] = cp;

                    if (cp > 0) {
                        // Not negative (i.e. not double-codepoint)
                        if (cp < NCRS_BY_CODEPOINT_LEN) {
                            // Not overflown
                            final short currentNCRForCodepoint = NCRS_BY_CODEPOINT[cp];
                            if (ncr[ncr.length - 1] == REFERENCE_SUFFIX) {
                                // Never assign an NCR not ending in semicolon - They are always duplicates.
                                if (currentNCRForCodepoint == NO_NCR) {
                                    NCRS_BY_CODEPOINT[cp] = i;
                                } else {
                                    // There are more than one NCRs for the same codepoint, so we need to choose
                                    // one for escaping operations. The shortest NCR will be chosen, if both end in
                                    // a semicolon (if one doesn't, the one ending in semicolon will be chosen).
                                    if (ncr.length < SORTED_NCRS[currentNCRForCodepoint].length) {
                                        NCRS_BY_CODEPOINT[cp] = i;
                                    }
                                }
                            }
                        } else {
                            // Codepoint should be overflown
                            ncrsByCodepointOverflow.put(Integer.valueOf(cp), Short.valueOf(i));
                        }
                    }

                    break;

                }

            }

        }


        // Only create the overflow map if it is really needed.
        if (ncrsByCodepointOverflow.size() > 0) {
            NCRS_BY_CODEPOINT_OVERFLOW = ncrsByCodepointOverflow;
        } else {
            NCRS_BY_CODEPOINT_OVERFLOW = null;
        }


        // Finally, the double-codepoints structure can be initialized, if really needed.
        if (doubleCodepoints.size() > 0) {
            DOUBLE_CODEPOINTS = new int[doubleCodepoints.size()][];
            for (int i = 0; i < DOUBLE_CODEPOINTS.length; i++) {
                DOUBLE_CODEPOINTS[i] = doubleCodepoints.get(i);
            }
        } else {
            DOUBLE_CODEPOINTS = null;
        }

    }




    private static boolean isAlphanumeric(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }


    private static boolean arrayContains(final int[] array, final int arrayLen, final int n) {
        for (int i = 0; i < arrayLen; i++) {
            if (n == array[i]) {
                return true;
            }
        }
        return false;
    }



    String escape(final String text, final int[] immunecodepoints, final int[] forbiddenncrs,
                  final BaseImmunityType baseImmunityType, final MarkupEscapeType markupEscapeType) {

        if (baseImmunityType == null) {
            throw new IllegalArgumentException("Argument 'baseImmunityType' cannot be null");
        }

        if (markupEscapeType == null) {
            throw new IllegalArgumentException("Argument 'markupEscapeType' cannot be null");
        }

        if (text == null) {
            return null;
        }

        final int immunecodepointsLen = (immunecodepoints == null? 0 : immunecodepoints.length);
        final int forbiddenncrsLen = (forbiddenncrs == null? 0 : forbiddenncrs.length);

        final boolean onlyAlphaImmune = baseImmunityType.equals(BaseImmunityType.BASE_IMMUNITY_ONLY_ALPHANUMERIC);

        final boolean useNCRs =
                (MarkupEscapeType.NAMED_REFERENCES_DEFAULT_TO_DECIMAL.equals(markupEscapeType) ||
                 MarkupEscapeType.NAMED_REFERENCES_DEFAULT_TO_HEXA.equals(markupEscapeType));
        final boolean useHexa =
                (MarkupEscapeType.NAMED_REFERENCES_DEFAULT_TO_HEXA.equals(markupEscapeType) ||
                 MarkupEscapeType.HEXADECIMAL_REFERENCES.equals(markupEscapeType));

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);


            /*
             * Shortcut: most characters will be ASCII/Alphanumeric, and we won't need to do anything at
             * all for them
             */

            if (c <= MAX_ASCII_CHAR && NCRS_BY_CODEPOINT[c] == NO_NCR ) {
                if (!onlyAlphaImmune || isAlphanumeric(c)) {
                    continue;
                }
            }


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
             * Check direct codepoint immunity
             */

            if (arrayContains(immunecodepoints, immunecodepointsLen, codepoint)) {
                continue;
            }


            /*
             * At this point we know for sure we will need some kind of escaping, so we
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
                // This is to compensate that we are actually escaping two char[] positions with a single codepoint.
                i++;
            }

            readOffset = i + 1;


            /*
             * -----------------------------------------------------------------------------------------
             *
             * Peform the real escaping, attending the different combinations of NCR, DCR and HCR needs.
             *
             * -----------------------------------------------------------------------------------------
             */

            if (useNCRs && !arrayContains(forbiddenncrs, forbiddenncrsLen, codepoint)) {
                // We will try to use an NCR

                if (codepoint < NCRS_BY_CODEPOINT_LEN) {
                    // codepoint < 0x2fff - all HTML4, most HTML5

                    final short ncrIndex = NCRS_BY_CODEPOINT[codepoint];
                    if (ncrIndex != NO_NCR) {
                        // There is an NCR for this codepoint!
                        strBuilder.append(SORTED_NCRS[ncrIndex]);
                        continue;
                    } // else, just let it exit the block and let decimal/hexa escaping do its job

                } else if (NCRS_BY_CODEPOINT_OVERFLOW != null) {
                    // codepoint >= 0x2fff. NCR, if exists, will live at the overflow map (if there is one).

                    final Short ncrIndex = NCRS_BY_CODEPOINT_OVERFLOW.get(Integer.valueOf(codepoint));
                    if (ncrIndex != null) {
                        strBuilder.append(SORTED_NCRS[ncrIndex.shortValue()]);
                        continue;
                    } // else, just let it exit the block and let decimal/hexa escaping do its job

                }

            }

            /*
             * No NCR-escaping was possible (or allowed), so we need decimal/hexa escaping.
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
         * Final cleaning: return the original String object if no escaping was actually needed. Otherwise
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




    void escape(final char[] text, final int offset, final int len, final Writer writer,
                final int[] immunecodepoints, final int[] forbiddenncrs,
                final BaseImmunityType baseImmunityType, final MarkupEscapeType markupEscapeType)
                throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (markupEscapeType == null) {
            throw new IllegalArgumentException("Argument 'markupEscapeType' cannot be null");
        }

        if (offset < 0 || offset > text.length) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + text.length);
        }

        if (len < 0 || (offset + len) > text.length) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + text.length);
        }

        if (text == null || text.length == 0) {
            return;

        }


        // TODO Fill with code from String version, once it is finished


    }




    /*
     * This translation is needed during unescaping to support ill-formed escaping codes for Windows 1252 codes
     * instead of the correct unicode ones (for example, &#x80; for the euro symbol instead of &#x20aC;). This is
     * something browsers do support, and included in the HTML5 spec for consuming character references.
     * See http://www.w3.org/TR/html5/syntax.html#consume-a-character-reference
     */
    static int translateIllFormedCodepoint(final int codepoint) {
        switch (codepoint) {
            case 0x00: return 0xFFFD;
            case 0x80: return 0x20AC;
            case 0x82: return 0x201A;
            case 0x83: return 0x0192;
            case 0x84: return 0x201E;
            case 0x85: return 0x2026;
            case 0x86: return 0x2020;
            case 0x87: return 0x2021;
            case 0x88: return 0x02C6;
            case 0x89: return 0x2030;
            case 0x8A: return 0x0160;
            case 0x8B: return 0x2039;
            case 0x8C: return 0x0152;
            case 0x8E: return 0x017D;
            case 0x91: return 0x2018;
            case 0x92: return 0x2019;
            case 0x93: return 0x201C;
            case 0x94: return 0x201D;
            case 0x95: return 0x2022;
            case 0x96: return 0x2013;
            case 0x97: return 0x2014;
            case 0x98: return 0x02DC;
            case 0x99: return 0x2122;
            case 0x9A: return 0x0161;
            case 0x9B: return 0x203A;
            case 0x9C: return 0x0153;
            case 0x9E: return 0x017E;
            case 0x9F: return 0x0178;
            default: return codepoint;
        }
    }


    /*
     * This method is used instead of Integer.parseInt(str,radix) in order to avoid the need
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



    private static int compare(final char[] ncr, final String text, final int start, final int end) {
        final int textLen = end - start;
        final int maxCommon = Math.min(ncr.length, textLen);
        int i;
        // char 0 is discarded, will be & in both cases
        for (i = 1; i < maxCommon; i++) {
            final char tc = text.charAt(start + i);
            if (ncr[i] < tc) {
                return -1;
            } else if (ncr[i] > tc) {
                return 1;
            }
        }
        if (ncr.length > i) {
            return 1;
        }
        if (textLen > i) {
            // We have a partial match. Can be an NCR not finishing in a semicolon
            return - ((textLen - i) + 10);
        }
        return 0;
    }



    static int binarySearch(final char[][] values,
                            final String text, final int start, final int end) {

        int low = 0;
        int high = values.length - 1;

        int partialIndex = Integer.MIN_VALUE;
        int partialValue = Integer.MIN_VALUE;

        while (low <= high) {

            final int mid = (low + high) >>> 1;
            final char[] midVal = values[mid];

            final int cmp = compare(midVal, text, start, end);

            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else if (cmp < -10) {
                // Partial match
                low = mid + 1;
                if (partialIndex == Integer.MIN_VALUE || partialValue < cmp) {
                    partialIndex = mid;
                    partialValue = cmp; // partial will always be negative, and -10. We look for the smallest partial
                }
            } else {
                // Found!!
                return mid;
            }

        }

        if (partialIndex != Integer.MIN_VALUE) {
            // We have a partial result. We return the closest result index as negative + (-10)
            return (-1) * (partialIndex + 10);
        }

        return Integer.MIN_VALUE; // Not found!

    }




    /*
     * See: http://www.w3.org/TR/html5/syntax.html#consume-a-character-reference
     */
    String unescape(final String text) {

        if (text == null) {
            return text;
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

                    if (c2 == 'x' || c2 == 'X' && (i + 3) < max) {
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

                        codepoint = parseIntFromReference(text, i + 3, f, 16);
                        referenceOffset = f - 1;

                        if ((f < max) && text.charAt(f) == REFERENCE_SUFFIX) {
                            referenceOffset++;
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                        // Don't continue here, just let the unescaping code below do its job

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

                        codepoint = parseIntFromReference(text, i + 2, f, 10);
                        referenceOffset = f - 1;

                        if ((f < max) && text.charAt(f) == REFERENCE_SUFFIX) {
                            referenceOffset++;
                        }

                        codepoint = translateIllFormedCodepoint(codepoint);

                        // Don't continue here, just let the unescaping code below do its job

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

                    final int ncrPosition = binarySearch(SORTED_NCRS, text, i, f);
                    if (ncrPosition >= 0) {
                        codepoint = SORTED_CODEPOINTS[ncrPosition];
                    } else if (ncrPosition == Integer.MIN_VALUE) {
                        // Not found! Just ignore our efforts to find a match.
                        continue;
                    } else if (ncrPosition < -10) {
                        // Found but partial!
                        final int partialIndex = (-1) * (ncrPosition + 10);
                        final char[] partialMatch = SORTED_NCRS[partialIndex];
                        codepoint = SORTED_CODEPOINTS[partialIndex];
                        f -= ((f - i) - partialMatch.length); // un-consume the chars remaining from the partial match
                    } else {
                        // Should never happen!
                        throw new RuntimeException("Invalid unescaping codepoint after search: " + ncrPosition);
                    }

                    referenceOffset = f - 1;

                }

            }


            /*
             * At this point we know for sure we will need some kind of unescaping, so we
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
             * Peform the real unescaping
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                strBuilder.append(Character.toChars(codepoint));
            } else if (codepoint < 0) {
                // This is a double-codepoint unescaping operation
                final int[] codepoints = DOUBLE_CODEPOINTS[((-1) * codepoint) - 1];
                if (codepoints[0] > '\uFFFF') {
                    strBuilder.append(Character.toChars(codepoints[0]));
                } else {
                    strBuilder.append((char) codepoints[0]);
                }
                if (codepoints[1] > '\uFFFF') {
                    strBuilder.append(Character.toChars(codepoints[1]));
                } else {
                    strBuilder.append((char) codepoints[1]);
                }
            } else {
                strBuilder.append((char)codepoint);
            }

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no unescaping was actually needed. Otherwise
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








    static final class References {

        private final List<Reference> references = new ArrayList<Reference>(200);

        References() {
            super();
        }

        void addReference(final int codepoint, final String ncr) {
            this.references.add(new Reference(ncr, new int[]{codepoint}));
        }

        void addReference(final int codepoint0, final int codepoint1, final String ncr) {
            this.references.add(new Reference(ncr, new int[] { codepoint0, codepoint1 }));
        }

    }



    private static final class Reference {

        private final char[] ncr;
        private final int[] codepoints;

        private Reference(final String ncr, final int[] codepoints) {
            super();
            this.ncr = ncr.toCharArray();
            this.codepoints = codepoints;
        }

    }



}

