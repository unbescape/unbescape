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

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 *   Utility class for performing XML escape/unescape operations.
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
 *       needs of the scenario). Its values are defined by the {@link org.unbescape.xml.XmlEscapeLevel}
 *       enum.</li>
 *   <li><em>Type</em>, which defines whether escaping should be performed by means of CERs
 *       (Character Entity References) or by means of decimal/hexadecimal numerical references.
 *       Its values are defined by the {@link org.unbescape.xml.XmlEscapeType} enum.</li>
 * </ul>
 * <p>
 *   <strong>Unescape</strong> operations need no configuration parameters. Unescape operations
 *   will always perform <em>complete</em> unescape of CERs, decimal and hexadecimal references.
 * </p>
 *
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   This class supports both XML 1.0 and XML 1.1 escape/unescape operations. Whichever the XML version used,
 *   <u>only the five predefined XML character entities are supported</u>: <kbd>&amp;lt;</kbd>,
 *   <kbd>&amp;gt;</kbd>, <kbd>&amp;amp;</kbd>, <kbd>&amp;quot</kbd> and <kbd>&amp;apos;</kbd>. This
 *   means there is no support for DTD-defined or user-defined entities.
 * </p>
 * <p>
 *   Each version of XML establishes a series of characters that are considered <em>not-valid</em>, even
 *   when escaped &mdash;for example, the <kbd>&bsol;u0000</kbd> (null byte)&mdash;. Escape operations will
 *   automatically remove these chars.
 * </p>
 * <p>
 *   Also, each version of XML establishes a series of control characters that, even if allowed as
 *   valid characters, should always appear escaped. For example: <kbd>&bsol;u0001</kbd> to
 *   <kbd>&bsol;u0008</kbd> in XML 1.1.
 * </p>
 * <p>
 *   This class supports the whole Unicode character set: <kbd>&bsol;u0000</kbd> to <kbd>&bsol;u10FFFF</kbd>,
 *   including characters not representable by only one <kbd>char</kbd> in Java (<kbd>&gt;&bsol;uFFFF</kbd>).
 * </p>
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
 *   <dt>ER</dt>
 *     <dd>XML Entity Reference: references to variables used to define shortcuts to standard text or
 *         special characters. Entity references start with <kbd>'&amp;'</kbd> and end with
 *         <kbd>';'</kbd>.</dd>
 *   <dt>CER</dt>
 *     <dd>Character Entity Reference: XML Entity Reference used to define a shortcut to a specific
 *         character. XML specifies five <em>predefined</em> CERs: <kbd>&amp;lt;<kbd> (<kbd>&lt;</kbd>),
 *         <kbd>&amp;gt;</kbd> (<kbd>&gt;</kbd>), <kbd>&amp;amp;</kbd> (<kbd>&amp;</kbd>),
 *         <kbd>&amp;quot;</kbd> (<kbd>&quot;</kbd>) and <kbd>&amp;apos;</kbd>
 *         (<kbd>&apos;</kbd>).</dd>
 *   <dt>DCR</dt>
 *     <dd>Decimal Character Reference: base-10 numerical representation of an Unicode codepoint:
 *         <kbd>&amp;#225;</kbd></dd>
 *   <dt>HCR</dt>
 *     <dd>Hexadecimal Character Reference: hexadecimal numerical representation of an Unicode codepoint:
 *         <kbd>&#xE1;</kbd>. Note that XML only allows lower-case <kbd>'x'</kbd> for defining hexadecimal
 *         character entity references (in contrast with HTML, which allows both <kbd>'&#x...;'</kbd> and
 *         <kbd>'&#X...;'</kbd>).</dd>
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
 *   <li><a href="http://www.w3.org/TR/xml" target="_blank">XML 1.0 Specification</a> [w3.org]</li>
 *   <li><a href="http://www.w3.org/TR/xml11" target="_blank">XML 1.1 Specification</a> [w3.org]</li>
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
public final class XmlEscape {




    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
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
    public static String escapeXml10Minimal(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
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
    public static String escapeXml11Minimal(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
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
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <kbd>'&amp;lt;'</kbd>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <kbd>'&amp;#x2430;'</kbd>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
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
    public static String escapeXml10(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
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
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <kbd>'&amp;lt;'</kbd>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <kbd>'&amp;#x2430;'</kbd>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
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
    public static String escapeXml11(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <kbd>String</kbd>-based <kbd>escapeXml10*(...)</kbd> methods call this one with preconfigured
     *   <kbd>type</kbd> and <kbd>level</kbd> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeXml10(final String text, final XmlEscapeType type, final XmlEscapeLevel level) {
        return escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <kbd>String</kbd>-based <kbd>escapeXml11*(...)</kbd> methods call this one with preconfigured
     *   <kbd>type</kbd> and <kbd>level</kbd> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeXml11(final String text, final XmlEscapeType type, final XmlEscapeLevel level) {
        return escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }


    /*
     * Private escape method called from XML 1.0 and XML 1.1 public methods, once the correct
     * symbol set has been selected.
     */
    private static String escapeXml(final String text, final XmlEscapeSymbols symbols,
                                    final XmlEscapeType type, final XmlEscapeLevel level) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return XmlEscapeUtil.escape(text, symbols, type, level);

    }





    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
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
    public static void escapeXml10Minimal(final char[] text, final int offset, final int len, final Writer writer)
                                          throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
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
    public static void escapeXml11Minimal(final char[] text, final int offset, final int len, final Writer writer)
                                          throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
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
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <kbd>'&amp;lt;'</kbd>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <kbd>'&amp;#x2430;'</kbd>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
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
    public static void escapeXml10(final char[] text, final int offset, final int len, final Writer writer)
                                   throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
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
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <kbd>'&amp;lt;'</kbd>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <kbd>'&amp;#x2430;'</kbd>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
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
    public static void escapeXml11(final char[] text, final int offset, final int len, final Writer writer)
                                   throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <kbd>char[]</kbd>-based <kbd>escapeXml10*(...)</kbd> methods call this one with preconfigured
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
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     */
    public static void escapeXml10(final char[] text, final int offset, final int len, final Writer writer,
                                   final XmlEscapeType type, final XmlEscapeLevel level)
                                   throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <kbd>char[]</kbd>-based <kbd>escapeXml11*(...)</kbd> methods call this one with preconfigured
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
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     */
    public static void escapeXml11(final char[] text, final int offset, final int len, final Writer writer,
                                   final XmlEscapeType type, final XmlEscapeLevel level)
                                   throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }


    /*
     * Private escape method called from XML 1.0 and XML 1.1 public methods, once the correct
     * symbol set has been selected.
     */
    private static void escapeXml(final char[] text, final int offset, final int len, final Writer writer,
                                 final XmlEscapeSymbols symbols, final XmlEscapeType type, final XmlEscapeLevel level)
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

        XmlEscapeUtil.escape(text, offset, len, writer, symbols, type, level);

    }


    /**
     * <p>
     *   Perform an XML <strong>unescape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> XML 1.0/1.1 unescape of CERs, decimal
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
    public static String unescapeXml(final String text) {
        // The chosen symbols (1.0 or 1.1) don't really matter, as both contain the same CERs
        return XmlEscapeUtil.unescape(text, XmlEscapeSymbols.XML11_SYMBOLS);
    }



    /**
     * <p>
     *   Perform an XML <strong>unescape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> XML 1.0/1.1 unescape of CERs, decimal
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
    public static void unescapeXml(final char[] text, final int offset, final int len, final Writer writer)
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

        // The chosen symbols (1.0 or 1.1) don't really matter, as both contain the same CERs
        XmlEscapeUtil.unescape(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS);

    }





    private XmlEscape() {
        super();
    }


}

