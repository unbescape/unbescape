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
package org.unbescape.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 *   Instances of this class group all the complex data structures needed to support escape and unescape
 *   operations for XML.
 * </p>
 * <p>
 *   In contrast with HTML escape operations, the entity references to be used for XML escape/unescape operations
 *   can be defined by the user by manually creating an instance of this class containing all the entities he/she
 *   wants to escape.
 * </p>
 * <p>
 *   It is <strong>not</strong> recommended to use this XML class for HTML escape/unescape operations. Use the methods
 *   in {@link org.unbescape.html.HtmlEscape} instead, as HTML escape rules include a series of tweaks not allowed in
 *   XML, as well as being less lenient with regard to aspects such as case-sensitivity. Besides, the HTML escape
 *   infrastructure is able to apply a series of performance optimizations not possible in XML due to the fact that
 *   the number of HTML Character Entity References (<em>Named Character References</em> in HTML5 jargon) is fixed
 *   and known in advance.
 * </p>
 * <p>
 *   Objects of this class are <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0
 *
 */
final class XmlEscapeSymbols {

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
     *   DCR
     *      Decimal Character Reference: base-10 numerical representation of an Unicode codepoint: &#225;
     *
     *   HCR
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
     * Constants holding the definition of all the HtmlEscapeSymbols for HTML4 and HTML5, to be used in escape and
     * unescape operations.
     */
    static final XmlEscapeSymbols XML11_SYMBOLS;



    static {

        // TODO GENERAL XML: http://www.w3.org/TR/xml-entity-names/
        // TODO See 1.3 Rationale and list of changes for XML 1.1 in http://www.w3.org/TR/xml11/ for VALID CHARACTERS
        XML11_SYMBOLS = Xml11EscapeSymbolsInitializer.initializeXml11();

    }


    /*
     *   NOTE
     *   -------------
     *   Most of the fields in objects of this class are package-accessible, as the class itself is, in order
     *   to allow them (the fields) to be directly accessed from the classes doing the real escape/unescape (basically,
     *   the {@link org.unbescape.xml.XmlEscapeUtil} class.
     *   -------------
     */




    /*
     * Maximum char value inside the ASCII plane
     */
    final char MAX_ASCII_CHAR = 0x7f;

    /*
     * This array will hold the 'escape level' assigned to each ASCII character (codepoint), 0x0 to 0x7f and also
     * a level for the rest of non-ASCII characters.
     * - These levels are used to configure how (and if) escape operations should ignore ASCII or non-ASCII
     *   characters, or escape them somehow if required.
     * - Each XmlEscapeSymbols structure will define a different set of levels for ASCII chars, according to their needs.
     * - Position 0x7f + 1 represents all the non-ASCII characters. The specified value will determine whether
     *   all non-ASCII characters have to be escaped or not.
     */
    final byte[] ESCAPE_LEVELS = new byte[MAX_ASCII_CHAR + 2];

    /*
     * This array will contain all the codepoints that might be escaped, numerically ordered.
     * - Positions in this array will correspond to positions in the SORTED_CERS_BY_CODEPOINT array, so that one array
     *   (this one) holds the codepoints while the other one holds the CERs such codepoints refer to.
     * - Gives the opportunity to store all codepoints in numerical order and therefore be able to perform
     *   binary search operations in order to quickly find codepoints (and translate to CERs) when escaping.
     */
    final int[] SORTED_CODEPOINTS;

    /*
     * This array contains all the CERs corresponding to the codepoints stored in SORTED_CODEPOINTS. This array is
     * ordered so that each index in SORTED_CODEPOINTS can also be used to retrieve the corresponding CER when used
     * on this array.
     */
    final char[][] SORTED_CERS_BY_CODEPOINT;

    /*
     * This array will contain all the CERs that might be unescaped, alphabetically ordered.
     * - Positions in this array will correspond to positions in the SORTED_CODEPOINTS_BY_CER array, so that one array
     *   (this one) holds the CERs while the other one holds the codepoint(s) such CERs refer to.
     * - Gives the opportunity to store all CERs in alphabetical order and therefore be able to perform
     *   binary search operations in order to quickly find CERs (and translate to codepoints) when unescaping.
     */
    final char[][] SORTED_CERS;

    /*
     * This array contains all the codepoints corresponding to the CERs stored in SORTED_CERS. This array is
     * ordered so that each index in SORTED_CERS can also be used to retrieve the corresponding CODEPOINT when used
     * on this array.
     */
    final int[] SORTED_CODEPOINTS_BY_CER;







    /*
     * Create a new XmlEscapeSymbols structure. This will initialize all the structures needed to cover the
     * specified references and escape levels, including sorted arrays, overflow maps, etc.
     */
    XmlEscapeSymbols(final References references, final byte[] escapeLevels) {

        super();

        // Initialize ASCII escape levels: just copy the array
        System.arraycopy(escapeLevels, 0, ESCAPE_LEVELS, 0, (0x7f + 2));

        // Initialize the length of the escaping structures
        final int structureLen = references.references.size();

        // Initialize some auxiliary structures
        final List<char[]> cers = new ArrayList<char[]>(structureLen + 5);
        final List<Integer> codepoints = new ArrayList<Integer>(structureLen + 5);

        // For each reference, initialize its corresponding codepoint -> CER and CER -> codepoint structures
        for (final Reference reference : references.references) {
            cers.add(reference.cer);
            codepoints.add(Integer.valueOf(reference.codepoint));
        }

        // We can initialize now the arrays
        SORTED_CODEPOINTS = new int[structureLen];
        SORTED_CERS_BY_CODEPOINT = new char[structureLen][];
        SORTED_CERS = new char[structureLen][];
        SORTED_CODEPOINTS_BY_CER = new int[structureLen];

        final List<char[]> cersOrdered = new ArrayList<char[]>(cers);
        Collections.sort(cersOrdered, new Comparator<char[]>() {
            public int compare(final char[] o1, final char[] o2) {
                return new String(o1).compareTo(new String(o2));
            }
        });

        final List<Integer> codepointsOrdered = new ArrayList<Integer>(codepoints);
        Collections.sort(codepointsOrdered);

        // Order the CODEPOINT -> CERs (escape)structures
        for (short i = 0; i < structureLen; i++) {

            final int codepoint = codepointsOrdered.get(i);
            SORTED_CODEPOINTS[i] = codepoint;
            for (short j = 0; j  < structureLen; j++) {
                if (codepoint == codepoints.get(j)) {
                    SORTED_CERS_BY_CODEPOINT[i] = cers.get(j);
                    break;
                }
            }

        }

        // Order the CERs -> CODEPOINT (unescape)structures
        for (short i = 0; i < structureLen; i++) {

            final char[] cer = cersOrdered.get(i);
            SORTED_CERS[i] = cer;
            for (short j = 0; j  < structureLen; j++) {
                if (Arrays.equals(cer, cers.get(j))) {
                    SORTED_CODEPOINTS_BY_CER[i] = codepoints.get(j);
                    break;
                }
            }

        }

    }




    /*
     * These two methods (two versions: for String and for char[]) compare each of the candidate
     * text fragments with an CER coming from the SORTED_CERS array, during binary search operations.
     */

    private static int compare(final char[] cer, final String text, final int start, final int end) {
        final int textLen = end - start;
        final int maxCommon = Math.min(cer.length, textLen);
        int i;
        // char 0 is discarded, will be & in both cases
        for (i = 1; i < maxCommon; i++) {
            final char tc = text.charAt(start + i);
            if (cer[i] < tc) {
                return -1;
            } else if (cer[i] > tc) {
                return 1;
            }
        }
        if (cer.length > i) {
            return 1;
        }
        if (textLen > i) {
            return -1;
        }
        return 0;
    }

    private static int compare(final char[] cer, final char[] text, final int start, final int end) {
        final int textLen = end - start;
        final int maxCommon = Math.min(cer.length, textLen);
        int i;
        // char 0 is discarded, will be & in both cases
        for (i = 1; i < maxCommon; i++) {
            final char tc = text[start + i];
            if (cer[i] < tc) {
                return -1;
            } else if (cer[i] > tc) {
                return 1;
            }
        }
        if (cer.length > i) {
            return 1;
        }
        if (textLen > i) {
            return -1;
        }
        return 0;
    }



    /*
     * These two methods (two versions: for String and for char[]) are used during unescape at the
     * {@link XmlEscapeUtil} class in order to quickly find the entity corresponding to a preselected fragment
     * of text (if there is such entity).
     */

    static int binarySearch(final char[][] values,
                            final String text, final int start, final int end) {

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {

            final int mid = (low + high) >>> 1;
            final char[] midVal = values[mid];

            final int cmp = compare(midVal, text, start, end);

            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else {
                // Found!!
                return mid;
            }

        }

        return Integer.MIN_VALUE; // Not found!

    }

    static int binarySearch(final char[][] values,
                            final char[] text, final int start, final int end) {

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {

            final int mid = (low + high) >>> 1;
            final char[] midVal = values[mid];

            final int cmp = compare(midVal, text, start, end);

            if (cmp == -1) {
                low = mid + 1;
            } else if (cmp == 1) {
                high = mid - 1;
            } else {
                // Found!!
                return mid;
            }

        }

        return Integer.MIN_VALUE; // Not found!

    }






    /*
     * Inner utility classes that model the named character references to be included in an initialized
     * instance of the XmlEscapeSymbols class.
     */


    static final class References {

        private final List<Reference> references = new ArrayList<Reference>(200);

        References() {
            super();
        }

        void addReference(final int codepoint, final String cer) {
            this.references.add(new Reference(cer, codepoint));
        }

    }


    private static final class Reference {

        private final char[] cer;
        private final int codepoint;

        private Reference(final String cer, final int codepoint) {
            super();
            this.cer = cer.toCharArray();
            this.codepoint = codepoint;
        }

    }



}

