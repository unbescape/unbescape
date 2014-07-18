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
package org.unbescape.csv;

import java.io.IOException;
import java.io.Writer;


/**
 * <p>
 *   Utility class for performing CSV escape/unescape operations.
 * </p>
 *
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   Specific features of the CSV escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>Works according to the rules specified in RFC4180 (there is no <em>CSV standard</em> as such).</li>
 *   <li>Encloses escaped values in double-quotes (<kbd>"value"</kbd>) if they contain any non-alphanumeric
 *       characters.</li>
 *   <li>Escapes double-quote characters (<kbd>"</kbd>) by writing them twice: <kbd>""</kbd>.</li>
 * </ul>
 *
 * <h4><u>Input/Output</u></h4>
 *
 * <p>
 *   There are two different input/output modes that can be used in escape/unescape operations:
 * </p>
 * <ul>
 *   <li><em><kbd>String</kbd> input, <kbd>String</kbd> output</em>: Input is specified as a <kbd>String</kbd> object
 *       and output is returned as another. In order to improve memory performance, all escape and unescape
 *       operations <u>will return the exact same input object as output if no escape/unescape modifications
 *       are required</u>.</li>
 *   <li><em><kbd>char[]</kbd> input, <kbd>java.io.Writer</kbd> output</em>: Input will be read from a char array
 *       (<kbd>char[]</kbd>) and output will be written into the specified <kbd>java.io.Writer</kbd>.
 *       Two <kbd>int</kbd> arguments called <kbd>offset</kbd> and <kbd>len</kbd> will be
 *       used for specifying the part of the <kbd>char[]</kbd> that should be escaped/unescaped. These methods
 *       should be called with <kbd>offset = 0</kbd> and <kbd>len = text.length</kbd> in order to process
 *       the whole <kbd>char[]</kbd>.</li>
 * </ul>
 *
 * <h4><u>Specific instructions for Microsoft Excel-compatible files</u></h4>
 *
 * <p>
 *   In order for Microsoft Excel to correcly open a CSV file &mdash;including field values with line
 *   breaks&mdash; these rules should be followed:
 * </p>
 * <ul>
 *   <li>Separate fields with semi-colon (<kbd>;</kbd>), records with Windows-style line breaks
 *       (<kbd>&#92;r&#92;n</kbd>, <kbd>U+000D</kbd> + <kbd>U+000A</kbd>).</li>
 *   <li>Enclose field values in double-quotes (<kbd>"</kbd>) if they contain any non-alphanumeric characters.</li>
 *   <li>Don't leave any whitespace between the field separator (<kbd>;</kbd>) and the enclosing quotes
 *       (<kbd>"</kbd>).</li>
 *   <li>Escape double-quote characters (<kbd>"</kbd>) inside field values with two double-quotes (<kbd>""</kbd>).</li>
 *   <li>Use <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>, unix-style line breaks) for line breaks inside field values,
 *       even if records are separated with Windows-style line breaks (<kbd>&#92;r&#92;n</kbd>)
 *       [ EXCEL 2003 compatibility ].</li>
 *   <li>Open CSV files in Excel with <kbd>File -> Open...</kbd>, not with <kbd>Data -> Import...</kbd>
 *       The latter option will not correctly understand line breaks inside field values (up to Excel 2010).</li>
 * </ul>
 * <p>
 *   <em>(Note unbescape will perform escaping of field values only, so it will take care of enclosing in
 *   double-quotes, using unix-style line breaks inside values, etc. But separating fields (e.g. with <kbd>;</kbd>),
 *   delimiting records (e.g. with <kbd>\r\n</kbd>) and using the correct character encoding when writing CSV files
 *   will be the responsibility of the application calling unbescape.)</em>
 * </p>
 * <p>
 *   The described format for Excel is also supported by OpenOffice.org Calc (<kbd>File -> Open...</kbd>) and also
 *   Google Spreadsheets (<kbd>File -> Import...</kbd>)
 * </p>
 *
 * <h4><u>References</u></h4>
 *
 * <p>
 *   The following references apply:
 * </p>
 * <ul>
 *   <li><a href="http://tools.ietf.org/html/rfc4180" target="_blank">RFC4180: Common Format and MIME Type
 *       for Comma-Separated Values (CSV) files</a> [ietf.org]</li>
 *   <li><a href="http://en.wikipedia.org/wiki/Comma-separated_values" target="_blank">Comma-Separated Values</a>
 *       [wikipedia.org]</li>
 *   <li><a href="http://creativyst.com/Doc/Articles/CSV/CSV01.htm" target="_blank">How to: The Comma Separated Value
 *       (CSV) File Format - Create or parse data in this popular pseudo-standard format</a> [creativyst.com]</li>
 * </ul>
 *
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
public final class CsvEscape {


    /**
     * <p>
     *   Perform a CSV <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeCsv(final String text) {
        return CsvEscapeUtil.escape(text);
    }






    /**
     * <p>
     *   Perform a CSV <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be escaped.
     * @param offset the position in <kbd>text</kbd> at which the escape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be escaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void escapeCsv(final char[] text, final int offset, final int len, final Writer writer)
                                  throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        CsvEscapeUtil.escape(text, offset, len, writer);

    }








    /**
     * <p>
     *   Perform a CSV <strong>unescape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be unescaped.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeCsv(final String text) {
        return CsvEscapeUtil.unescape(text);
    }


    /**
     * <p>
     *   Perform a CSV <strong>unescape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the unescape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be unescaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static void unescapeCsv(final char[] text, final int offset, final int len, final Writer writer)
                                    throws IOException{
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        final int textLen = (text == null? 0 : text.length);

        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        if (len < 0 || (offset + len) > textLen) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }

        CsvEscapeUtil.unescape(text, offset, len, writer);

    }




    private CsvEscape() {
        super();
    }


}

