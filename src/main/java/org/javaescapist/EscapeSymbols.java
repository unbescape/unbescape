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
final class EscapeSymbols {


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
    static final int NCRS_BY_CODEPOINT_LEN = 0x2fff;

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
    final short[] NCRS_BY_CODEPOINT = new short[NCRS_BY_CODEPOINT_LEN];

    /*
     * This map will work as an overflow of the NCRS_BY_CODEPOINT array, so that the codepoint-to-NCR relation is
     * stored here (with hash-based access) for codepoints >= NCRS_BY_CODEPOINT_LEN (0x2fff).
     * - The use of a Map here still allows for reasonabily fast access for those rare cases in which codepoints above
     *   0x2fff are used.
     * - In the real world, this map will contain the 138 values needed by HTML5 for codepoints >= 0x2fff.
     * - Approximate max size will be (being a complex object like a Map, it's a rough approximation):
     *   16 (header) + 138 * (16 (entry header) + 16*2 (key, value headers) + 4 (key) + 2 (value)) = 7468 bytes
     */
    final Map<Integer,Short> NCRS_BY_CODEPOINT_OVERFLOW;// No need to instantiate it until we know it's needed

    /*
     * Maximum char value inside the ASCII plane
     */
    final char MAX_ASCII_CHAR = 0x7f;

    /*
     * This array will hold the 'escape level' assigned to each ASCII character (codepoint), 0x0 to 0x7f.
     * - These levels are used to configure how (and if) escape operations should ignore ASCII characters, or
     *   escape them somehow if required.
     * - Each EscapeSymbols structure will define a different set of levels for ASCII chars, according to their needs.
     */
    final byte[] ASCII_ESCAPE_LEVEL = new byte[MAX_ASCII_CHAR + 1];

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
    final char[][] SORTED_NCRS;

    /*
     * This array contains all the codepoints corresponding to the NCRs stored in SORTED_NCRS. This array is ordered
     * so that each index in SORTED_NCRS can also be used to retrieve the original CODEPOINT when used on this array.
     * - Values in this array can be positive (= single codepoint) or negative (= double codepoint, will need further
     *   resolution by means of the DOUBLE_CODEPOINTS array)
     * - Max size in real world, when populated for HTML5: 2125 NCRs * 4 bytes/objref -> 8500 bytes.
     */
    final int[] SORTED_CODEPOINTS;


    /*
     * This array stores the sequences of two codepoints that are escaped as a single NCR. The indexes of this array are
     * referenced as negative numbers at the SORTED_CODEPOINTS array, and the values are int[2], containing the
     * sequence of codepoints. HTML4 has no NCRs like this, HTML5 has 93.
     * - Note this array is only used in UNESCAPE operations. Double-codepoint NCR escaping is not performed because
     *   the resulting characters are exactly equivalent to the escaping of the two codepoints separately.
     * - Max size in real world, when populated for HTML5 (rough approximate): 93 * (4 (ref) + 16 + 2 * 4) = 2604 bytes
     */
    final int[][] DOUBLE_CODEPOINTS;


    /*
     * This constant will be used at the NCRS_BY_CODEPOINT array to specify there is no NCR associated with a
     * codepoint.
     */
    static final short NO_NCR = (short) 0;




    /*
     * Constants holding the definition of all the EscapeSymbols for HTML4 and HTML5, to be used in escape and
     * unescape operations.
     */
    static final EscapeSymbols HTML4_SYMBOLS;
    static final EscapeSymbols HTML5_SYMBOLS;




    static {

        HTML4_SYMBOLS = Html4EscapeSymbolsInitializer.initializeHtml4();
        HTML5_SYMBOLS = Html5EscapeSymbolsInitializer.initializeHtml5();

    }






    EscapeSymbols(final References references, final byte[] asciiLevels) {

        super();

        // Initialize ASCII escape levels: just copy the array
        System.arraycopy(asciiLevels, 0, ASCII_ESCAPE_LEVEL, 0, (0x7f + 1));


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
                            if (NCRS_BY_CODEPOINT[cp] == NO_NCR) {
                                // Only the first NCR for each codepoint will be used for escaping.
                                NCRS_BY_CODEPOINT[cp] = i;
                            } else {
                                final int positionOfCurrent = positionInList(ncrs, SORTED_NCRS[NCRS_BY_CODEPOINT[cp]]);
                                final int positionOfNew = positionInList(ncrs, ncr);
                                if (positionOfNew < positionOfCurrent) {
                                    // The order in which NCRs were originally specified in the references argument
                                    // marks which NCR should be used for escaping (the first one), if several NCRs
                                    // have the same codepoint.
                                    NCRS_BY_CODEPOINT[cp] = i;
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



    private static int positionInList(final List<char[]> list, final char[] element) {
        int i = 0;
        for (final char[] e : list) {
            if (Arrays.equals(e, element)) {
                return i;
            }
            i++;
        }
        return -1;
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

