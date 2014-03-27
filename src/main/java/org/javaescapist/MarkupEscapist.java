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
        LITERAL_DEFAULT_TO_DECIMAL,
        LITERAL_DEFAULT_TO_HEXA,
        DECIMAL,
        HEXA
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
     * NCRs with a maximum codepoint of 0x2666 - HTML5 has 1125 NCRs with a maximum codepoint of 120171, but
     * only 138 scarcely used NCRs live above codepoint 0x2fff so an overflow map should be enough for
     * those 138 cases).
     */
    private static final short NCRS_BY_CODEPOINT_LEN = 0x2fff;

    /*
     * This array will contain the NCRs for the first NCRS_BY_CODEPOINT_LEN (0x2fff) codepoints, indexed by
     * the codepoints themselves so that they (even in the form of mere char's) can be used for array random access.
     * - Values are short in order to index values at the SORTED_NCRS array. This avoids the need for this
     *   array to hold String pointers, which would be 4 bytes in size each (compared to shorts, which are 2 bytes).
     * - Chars themselves or int codepoints can (will, in fact) be used as indexes.
     * - Given values are short, the maximum amount of total references this class can handle is 0x7fff = 32767
     *   (which is safe, because HTML5 has 1125).
     * - All XML and HTML4 NCRs will fit in this array. In the case of HTML5 NCRs, only 138 of the 1125 will
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
     * - Max size in real world, when populated for HTML5: 1125 NCRs * 4 bytes/objref -> 4500 bytes, plus the texts.
     */
    private final char[][] SORTED_NCRS;

    /*
     * This array contains all the codepoints corresponding to the NCRs stored in SORTED_NCRS. This array is ordered
     * so that each index in SORTED_NCRS can also be used to retrieve the original CODEPOINT when used on this array.
     * - Values in this array can be positive (= single codepoint) or negative (= double codepoint, will need further
     *   resolution by means of the DOUBLE_CODEPOINTS array)
     * - Max size in real world, when populated for HTML5: 1125 NCRs * 4 bytes/objref -> 4500 bytes.
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



    private static final char[] DECIMAL_ESCAPE_PREFIX = "&#".toCharArray();
    private static final char[] HEXA_ESCAPE_PREFIX = "&#x".toCharArray();





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

            final short ncrIndex = (short) ncrs.size();

            ncrs.add(referenceNcr);

            if (referenceCodepoints.length == 1) {
                // Only one codepoint (might be > 1 chars, though), this is the normal case

                final int referenceCodepoint = referenceCodepoints[0];
                codepoints.add(Integer.valueOf(referenceCodepoint));

                if (referenceCodepoint >= NCRS_BY_CODEPOINT_LEN) {
                    // For values that should go to the NCRS_BY_CODEPOINT array, we will take care
                    // of them later. In this case, we only add the overflow values to their map.
                    ncrsByCodepointOverflow.put(Integer.valueOf(referenceCodepoint), Short.valueOf(ncrIndex));
                }

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

        // Only create the overflow map if it is really needed.
        if (ncrsByCodepointOverflow.size() > 0) {
            NCRS_BY_CODEPOINT_OVERFLOW = ncrsByCodepointOverflow;
        } else {
            NCRS_BY_CODEPOINT_OVERFLOW = null;
        }


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
                    NCRS_BY_CODEPOINT[cp] = i;
                    break;
                }
            }
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




    private static boolean arrayContains(final char[] array, final int arrayLen, final char c) {
        for (int i = 0; i < arrayLen; i++) {
            if (c == array[i]) {
                return true;
            }
        }
        return false;
    }


    String escape(final String text, final char[] nonEscapableChars, final char[] nonLiteralEscapableChars,
                  final MarkupEscapeType markupEscapeType) {

        if (markupEscapeType == null) {
            throw new IllegalArgumentException("Argument 'markupEscapeType' cannot be null");
        }

        if (text == null) {
            return null;
        }

        final int nonEscapableCharsLen = (nonEscapableChars == null? 0 : nonEscapableChars.length);
        final int nonLiteralEscapableCharsLen = (nonLiteralEscapableChars == null? 0 : nonLiteralEscapableChars.length);

        final boolean literal =
                (MarkupEscapeType.LITERAL_DEFAULT_TO_DECIMAL.equals(markupEscapeType) ||
                 MarkupEscapeType.LITERAL_DEFAULT_TO_HEXA.equals(markupEscapeType));
        final boolean hexa =
                (MarkupEscapeType.HEXA.equals(markupEscapeType) ||
                 MarkupEscapeType.LITERAL_DEFAULT_TO_HEXA.equals(markupEscapeType));

        StringBuilder strBuilder = null;
        int readOffset = 0;
        final int textLen = text.length();

        for (int i = 0; i < textLen; i++) {

            final char c = text.charAt(i);

            if (c <= 0x7f && NCRS_BY_CODEPOINT[c] == -1) {
                continue;
            }

            if (arrayContains(nonEscapableChars, nonEscapableCharsLen, c)) {
                continue;
            }

            if (strBuilder == null) {
                strBuilder = new StringBuilder(textLen + 15);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            if (!literal || c >= NCRS_BY_CODEPOINT_LEN || NCRS_BY_CODEPOINT[c] == -1) {
                // char should be escaped, but there is no literal for it, or maybe we just dont want literals

                final int codePoint;
                if (Character.isHighSurrogate(c) && (i + 1) < textLen) {
                    // This might be a non-BMP (suplementary) Unicode character, therefore represented by two
                    // character instead of just one. We also need to check whether the next char is a low surrogate.
                    final char c2 = text.charAt(i + 1);
                    if (Character.isLowSurrogate(c2)) {
                        codePoint = Character.toCodePoint(c, c2);
                        i++;
                    } else {
                        codePoint = (int) c;
                    }
                } else {
                    codePoint = (int) c;
                }

                if (hexa) {
                    strBuilder.append(HEXA_ESCAPE_PREFIX);
                    strBuilder.append(Integer.toHexString(codePoint));
                } else {
                    strBuilder.append(DECIMAL_ESCAPE_PREFIX);
                    strBuilder.append(String.valueOf(codePoint));
                }
                strBuilder.append(';');

            } else {
                // char should be escaped AND there is literal for it

                if (arrayContains(nonLiteralEscapableChars, nonLiteralEscapableCharsLen, c)) {
                    // literal shouldn't be applied, defaulting
                    if (hexa) {
                        strBuilder.append(HEXA_ESCAPE_PREFIX);
                        strBuilder.append(Integer.toHexString((int) c));
                    } else {
                        strBuilder.append(DECIMAL_ESCAPE_PREFIX);
                        strBuilder.append(String.valueOf((int) c));
                    }
                    strBuilder.append(';');
                } else {
                    // just apply the literal
                    strBuilder.append(SORTED_NCRS[NCRS_BY_CODEPOINT[c]]);
                }

            }

            readOffset = i + 1;

        }

        if (strBuilder == null) {
            return text;
        }

        if (textLen - readOffset > 0) {
            strBuilder.append(text, readOffset, textLen);
        }

        return strBuilder.toString();

    }




    void escape(final char[] text, final int offset, final int len, final Writer writer,
                final char[] nonEscapableChars, final char[] nonLiteralEscapableChars,
                final MarkupEscapeType markupEscapeType)
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





    static final class References {

        private final List<Reference> references = new ArrayList<Reference>(200);

        References() {
            super();
        }

        void addReference(final String ncr, final int codepoint, final char character) {
            this.references.add(new Reference(ncr, new int[] { codepoint }, new char[] { character }));
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

