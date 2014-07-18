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
package org.unbescape.html;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 *   Utility class for performing HTML escape/unescape operations.
 * </p>
 *
 * <h4><u>Configuration of escape/unescape operations</u></h4>
 *
 * <p>
 *   <strong>Escape</strong> operations can be (optionally) configured by means of:
 * </p>
 * <ul>
 *   <li><em>Level</em>, which defines how deep the escape operation must be (what
 *       chars are to be considered eligible for escaping, depending on the specific
 *       needs of the scenario). Its values are defined by the {@link org.unbescape.html.HtmlEscapeLevel}
 *       enum.</li>
 *   <li><em>Type</em>, which defines whether escaping should be performed by means of NCRs
 *       (Named Character References), by means of decimal/hexadecimal numerical references,
 *       using the HTML5 or the HTML 4 NCR set, etc. Its values are defined by the
 *       {@link org.unbescape.html.HtmlEscapeType} enum.</li>
 * </ul>
 * <p>
 *   <strong>Unescape</strong> operations need no configuration parameters. Unescape operations
 *   will always perform <em>complete</em> unescape of NCRs (whole HTML5 set supported), decimal
 *   and hexadecimal references.
 * </p>
 *
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   Specific features of the HTML escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>Whole HTML5 NCR (Named Character Reference) set supported, if required:
 *       <kbd>&amp;rsqb;</kbd>,<kbd>&amp;NewLine;</kbd>, etc. (HTML 4 set available too).</li>
 *   <li>Mixed named and numerical (decimal or hexa) character references supported.</li>
 *   <li>Ability to default to numerical (decimal or hexa) references when an applicable NCR does not exist
 *       (depending on the selected operation <em>level</em>).</li>
 *   <li>Support for the whole Unicode character set: <kbd>&bsol;u0000</kbd> to <kbd>&bsol;u10FFFF</kbd>, including
 *       characters not representable by only one <kbd>char</kbd> in Java (<kbd>&gt;&bsol;uFFFF</kbd>).</li>
 *   <li>Support for unescape of double-char NCRs in HTML5: <kbd>'&amp;fjlig;'</kbd> &rarr; <kbd>'fj'</kbd>.</li>
 *   <li>Support for a set of HTML5 unescape <em>tweaks</em> included in the HTML5 specification:
 *       <ul>
 *         <li>Unescape of numerical character references not ending in semi-colon
 *             (e.g. <kbd>'&amp;#x23ac'</kbd>).</li>
 *         <li>Unescape of specific NCRs not ending in semi-colon (e.g. <kbd>'&amp;aacute'</kbd>).</li>
 *         <li>Unescape of specific numerical character references wrongly specified by their Windows-1252
 *             codepage code instead of the Unicode one (e.g. <kbd>'&amp;#x80;'</kbd> for '&euro;'
 *             (<kbd>'&amp;euro;'</kbd>) instead of <kbd>'&amp;#x20ac;'</kbd>).</li>
 *       </ul>
 *   </li>
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
 * <h4><u>Glossary</u></h4>
 *
 * <dl>
 *   <dt>NCR</dt>
 *     <dd>Named Character Reference or <em>Character Entity Reference</em>: textual
 *         representation of an Unicode codepoint: <kbd>&amp;aacute;</kbd></dd>
 *   <dt>DCR</dt>
 *     <dd>Decimal Character Reference: base-10 numerical representation of an Unicode codepoint:
 *         <kbd>&amp;#225;</kbd></dd>
 *   <dt>HCR</dt>
 *     <dd>Hexadecimal Character Reference: hexadecimal numerical representation of an Unicode codepoint:
 *         <kbd>&amp;#xE1;</kbd></dd>
 *   <dt>Unicode Codepoint</dt>
 *     <dd>Each of the <kbd>int</kbd> values conforming the Unicode code space.
 *         Normally corresponding to a Java <kbd>char</kbd> primitive value (codepoint <= <kbd>&bsol;uFFFF</kbd>),
 *         but might be two <kbd>char</kbd>s for codepoints <kbd>&bsol;u10000</kbd> to <kbd>&bsol;u10FFFF</kbd> if the
 *         first <kbd>char</kbd> is a high surrogate (<kbd>&bsol;uD800</kbd> to <kbd>&bsol;uDBFF</kbd>) and the
 *         second is a low surrogate (<kbd>&bsol;uDC00</kbd> to <kbd>&bsol;uDFFF</kbd>).</dd>
 * </dl>
 *
 * <h4><u>References</u></h4>
 *
 * <p>
 *   The following references apply:
 * </p>
 * <ul>
 *   <li><a href="http://www.w3.org/International/questions/qa-escapes" target="_blank">Using character escapes in
 *       markup and CSS</a> [w3.org]</li>
 *   <li><a href="http://www.w3.org/TR/html4/sgml/entities.html" target="_blank">Named Character References (or
 *       <em>Character entity references</em>) in HTML 4</a> [w3.org]</li>
 *   <li><a href="http://www.w3.org/TR/html5/syntax.html#named-character-references" target="_blank">Named Character
 *       References (or <em>Character entity references</em>) in HTML5</a> [w3.org]</li>
 *   <li><a href="http://www.w3.org/TR/html51/syntax.html#named-character-references" target="_blank">Named Character
 *       References (or <em>Character entity references</em>) in HTML 5.1</a> [w3.org]</li>
 *   <li><a href="http://www.w3.org/TR/html5/syntax.html#consume-a-character-reference" target="_blank">How to consume a
 *       character reference (HTML5 specification)</a> [w3.org]</li>
 *   <li><a href="https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet"
 *       target="_blank">OWASP XSS (Cross Site Scripting) Prevention Cheat Sheet</a> [owasp.org]</li>
 *   <li><a href="http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html"
 *       target="_blank">Supplementary characters in the Java Platform</a> [oracle.com]</li>
 * </ul>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0.0
 *
 */
public final class HtmlEscape {




    /**
     * <p>
     *   Perform an HTML5 level 2 (result is ASCII) <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>,
     *       <kbd>&quot</kbd> and <kbd>&apos;</kbd></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding HTML5 Named Character References
     *   (e.g. <kbd>'&amp;acute;'</kbd>) when such NCR exists for the replaced character, and replacing by a decimal
     *   character reference (e.g. <kbd>'&amp;#8345;'</kbd>) when there there is no NCR for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(String, HtmlEscapeType, HtmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static String escapeHtml5(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an HTML5 level 1 (XML-style) <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>. It is called
     *   <em>XML-style</em> in order to link it with JSP's <kbd>escapeXml</kbd> attribute in JSTL's
     *   <kbd>&lt;c:out ... /&gt;</kbd> tags.
     * </p>
     * <p>
     *  Note this method may <strong>not</strong> produce the same results as {@link #escapeHtml4Xml(String)} because
     *  it will escape the apostrophe as <kbd>&amp;apos;</kbd>, whereas in HTML 4 such NCR does not exist
     *  (the decimal numeric reference <kbd>&amp;#39;</kbd> is used instead).
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(String, HtmlEscapeType, HtmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static String escapeHtml5Xml(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an HTML 4 level 2 (result is ASCII) <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>,
     *       <kbd>&quot</kbd> and <kbd>&apos;</kbd></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding HTML 4 Named Character References
     *   (e.g. <kbd>'&amp;acute;'</kbd>) when such NCR exists for the replaced character, and replacing by a decimal
     *   character reference (e.g. <kbd>'&amp;#8345;'</kbd>) when there there is no NCR for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(String, HtmlEscapeType, HtmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static String escapeHtml4(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an HTML 4 level 1 (XML-style) <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>. It is called
     *   <em>XML-style</em> in order to link it with JSP's <kbd>escapeXml</kbd> attribute in JSTL's
     *   <kbd>&lt;c:out ... /&gt;</kbd> tags.
     * </p>
     * <p>
     *  Note this method may <strong>not</strong> produce the same results as {@link #escapeHtml5Xml(String)} because
     *  it will escape the apostrophe as <kbd>&amp;#39;</kbd>, whereas in HTML5 there is a specific NCR for
     *  such character (<kbd>&amp;apos;</kbd>).
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(String, HtmlEscapeType, HtmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static String escapeHtml4Xml(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) HTML <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.html.HtmlEscapeType} and {@link org.unbescape.html.HtmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <kbd>String</kbd>-based <kbd>escapeHtml*(...)</kbd> methods call this one with preconfigured
     *   <kbd>type</kbd> and <kbd>level</kbd> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.html.HtmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.html.HtmlEscapeLevel}.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeHtml(final String text, final HtmlEscapeType type, final HtmlEscapeLevel level) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return HtmlEscapeUtil.escape(text, type, level);

    }







    /**
     * <p>
     *   Perform an HTML5 level 2 (result is ASCII) <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>,
     *       <kbd>&quot</kbd> and <kbd>&apos;</kbd></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding HTML5 Named Character References
     *   (e.g. <kbd>'&amp;acute;'</kbd>) when such NCR exists for the replaced character, and replacing by a decimal
     *   character reference (e.g. <kbd>'&amp;#8345;'</kbd>) when there there is no NCR for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(char[], int, int, java.io.Writer, HtmlEscapeType, HtmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static void escapeHtml5(final char[] text, final int offset, final int len, final Writer writer)
                                   throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an HTML5 level 1 (XML-style) <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>. It is called
     *   <em>XML-style</em> in order to link it with JSP's <kbd>escapeXml</kbd> attribute in JSTL's
     *   <kbd>&lt;c:out ... /&gt;</kbd> tags.
     * </p>
     * <p>
     *  Note this method may <strong>not</strong> produce the same results as
     *  {@link #escapeHtml4Xml(char[], int, int, java.io.Writer)} because
     *  it will escape the apostrophe as <kbd>&amp;apos;</kbd>, whereas in HTML 4 such NCR does not exist
     *  (the decimal numeric reference <kbd>&amp;#39;</kbd> is used instead).
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(char[], int, int, java.io.Writer, HtmlEscapeType, HtmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static void escapeHtml5Xml(final char[] text, final int offset, final int len, final Writer writer)
                                      throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an HTML 4 level 2 (result is ASCII) <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>,
     *       <kbd>&quot</kbd> and <kbd>&apos;</kbd></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding HTML 4 Named Character References
     *   (e.g. <kbd>'&amp;acute;'</kbd>) when such NCR exists for the replaced character, and replacing by a decimal
     *   character reference (e.g. <kbd>'&amp;#8345;'</kbd>) when there there is no NCR for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(char[], int, int, java.io.Writer, HtmlEscapeType, HtmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static void escapeHtml4(final char[] text, final int offset, final int len, final Writer writer)
                                   throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an HTML 4 level 1 (XML-style) <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>. It is called
     *   <em>XML-style</em> in order to link it with JSP's <kbd>escapeXml</kbd> attribute in JSTL's
     *   <kbd>&lt;c:out ... /&gt;</kbd> tags.
     * </p>
     * <p>
     *  Note this method may <strong>not</strong> produce the same results as
     *  {@link #escapeHtml5Xml(char[], int, int, java.io.Writer)}  because it will escape the apostrophe as
     *  <kbd>&amp;#39;</kbd>, whereas in HTML5 there is a specific NCR for such character (<kbd>&amp;apos;</kbd>).
     * </p>
     * <p>
     *   This method calls {@link #escapeHtml(char[], int, int, java.io.Writer, HtmlEscapeType, HtmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeType#HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.html.HtmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
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
    public static void escapeHtml4Xml(final char[] text, final int offset, final int len, final Writer writer)
                                      throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) HTML <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.html.HtmlEscapeType} and {@link org.unbescape.html.HtmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <kbd>char[]</kbd>-based <kbd>escapeHtml*(...)</kbd> methods call this one with preconfigured
     *   <kbd>type</kbd> and <kbd>level</kbd> values.
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
     * @param type the type of escape operation to be performed, see {@link org.unbescape.html.HtmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.html.HtmlEscapeLevel}.
     */
    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer,
                                  final HtmlEscapeType type, final HtmlEscapeLevel level)
                                  throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
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

        HtmlEscapeUtil.escape(text, offset, len, writer, type, level);

    }






    /**
     * <p>
     *   Perform an HTML <strong>unescape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> unescape of NCRs (whole HTML5 set supported), decimal
     *   and hexadecimal references.
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
    public static String unescapeHtml(final String text) {
        return HtmlEscapeUtil.unescape(text);
    }



    /**
     * <p>
     *   Perform an HTML <strong>unescape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> unescape of NCRs (whole HTML5 set supported), decimal
     *   and hexadecimal references.
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
    public static void unescapeHtml(final char[] text, final int offset, final int len, final Writer writer)
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

        HtmlEscapeUtil.unescape(text, offset, len, writer);

    }




    private HtmlEscape() {
        super();
    }



}

