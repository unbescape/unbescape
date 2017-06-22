/*
 * =============================================================================
 * 
 *   Copyright (c) 2014-2017, The UNBESCAPE team (http://www.unbescape.org)
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

/**
 * <p>
 *   Utility class for performing XML escape/unescape operations.
 * </p>
 *
 * <strong><u>Configuration of escape/unescape operations</u></strong>
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
 * <strong><u>Features</u></strong>
 *
 * <p>
 *   This class supports both XML 1.0 and XML 1.1 escape/unescape operations. Whichever the XML version used,
 *   <u>only the five predefined XML character entities are supported</u>: <tt>&amp;lt;</tt>,
 *   <tt>&amp;gt;</tt>, <tt>&amp;amp;</tt>, <tt>&amp;quot</tt> and <tt>&amp;apos;</tt>. This
 *   means there is no support for DTD-defined or user-defined entities.
 * </p>
 * <p>
 *   Each version of XML establishes a series of characters that are considered <em>not-valid</em>, even
 *   when escaped &mdash;for example, the <tt>&#92;u0000</tt> (null byte)&mdash;. Escape operations will
 *   automatically remove these chars.
 * </p>
 * <p>
 *   Also, each version of XML establishes a series of control characters that, even if allowed as
 *   valid characters, should always appear escaped. For example: <tt>&#92;u0001</tt> to
 *   <tt>&#92;u0008</tt> in XML 1.1.
 * </p>
 * <p>
 *   This class supports the whole Unicode character set: <tt>&#92;u0000</tt> to <tt>&#92;u10FFFF</tt>,
 *   including characters not representable by only one <tt>char</tt> in Java (<tt>&gt;&#92;uFFFF</tt>).
 * </p>
 *
 * <strong><u>Input/Output</u></strong>
 *
 * <p>
 *   There are four different input/output modes that can be used in escape/unescape operations:
 * </p>
 * <ul>
 *   <li><em><tt>String</tt> input, <tt>String</tt> output</em>: Input is specified as a <tt>String</tt> object
 *       and output is returned as another. In order to improve memory performance, all escape and unescape
 *       operations <u>will return the exact same input object as output if no escape/unescape modifications
 *       are required</u>.</li>
 *   <li><em><tt>String</tt> input, <tt>java.io.Writer</tt> output</em>: Input will be read from a String
 *       and output will be written into the specified <tt>java.io.Writer</tt>.</li>
 *   <li><em><tt>java.io.Reader</tt> input, <tt>java.io.Writer</tt> output</em>: Input will be read from a Reader
 *       and output will be written into the specified <tt>java.io.Writer</tt>.</li>
 *   <li><em><tt>char[]</tt> input, <tt>java.io.Writer</tt> output</em>: Input will be read from a char array
 *       (<tt>char[]</tt>) and output will be written into the specified <tt>java.io.Writer</tt>.
 *       Two <tt>int</tt> arguments called <tt>offset</tt> and <tt>len</tt> will be
 *       used for specifying the part of the <tt>char[]</tt> that should be escaped/unescaped. These methods
 *       should be called with <tt>offset = 0</tt> and <tt>len = text.length</tt> in order to process
 *       the whole <tt>char[]</tt>.</li>
 * </ul>
 *
 * <strong><u>Glossary</u></strong>
 *
 * <dl>
 *   <dt>ER</dt>
 *     <dd>XML Entity Reference: references to variables used to define shortcuts to standard text or
 *         special characters. Entity references start with <tt>'&amp;'</tt> and end with
 *         <tt>';'</tt>.</dd>
 *   <dt>CER</dt>
 *     <dd>Character Entity Reference: XML Entity Reference used to define a shortcut to a specific
 *         character. XML specifies five <em>predefined</em> CERs: <tt>&amp;lt;</tt> (<tt>&lt;</tt>),
 *         <tt>&amp;gt;</tt> (<tt>&gt;</tt>), <tt>&amp;amp;</tt> (<tt>&amp;</tt>),
 *         <tt>&amp;quot;</tt> (<tt>&quot;</tt>) and <tt>&amp;apos;</tt>
 *         (<tt>&#39;</tt>).</dd>
 *   <dt>DCR</dt>
 *     <dd>Decimal Character Reference: base-10 numerical representation of an Unicode codepoint:
 *         <tt>&amp;#225;</tt></dd>
 *   <dt>HCR</dt>
 *     <dd>Hexadecimal Character Reference: hexadecimal numerical representation of an Unicode codepoint:
 *         <tt>&#xE1;</tt>. Note that XML only allows lower-case <tt>'x'</tt> for defining hexadecimal
 *         character entity references (in contrast with HTML, which allows both <tt>'&amp;#x...;'</tt> and
 *         <tt>'&amp;#x...;'</tt>).</dd>
 *   <dt>Unicode Codepoint</dt>
 *     <dd>Each of the <tt>int</tt> values conforming the Unicode code space.
 *         Normally corresponding to a Java <tt>char</tt> primitive value (codepoint &lt;= <tt>&#92;uFFFF</tt>),
 *         but might be two <tt>char</tt>s for codepoints <tt>&#92;u10000</tt> to <tt>&#92;u10FFFF</tt> if the
 *         first <tt>char</tt> is a high surrogate (<tt>&#92;uD800</tt> to <tt>&#92;uDBFF</tt>) and the
 *         second is a low surrogate (<tt>&#92;uDC00</tt> to <tt>&#92;uDFFF</tt>).</dd>
 * </dl>
 *
 * <strong><u>References</u></strong>
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
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeXml10Minimal(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeXml11Minimal(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     *
     * @since 1.1.5
     */
    public static String escapeXml10AttributeMinimal(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     *
     * @since 1.1.5
     */
    public static String escapeXml11AttributeMinimal(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeXml10(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeXml11(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     *
     * @since 1.1.5
     */
    public static String escapeXml10Attribute(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     *
     * @since 1.1.5
     */
    public static String escapeXml11Attribute(final String text) {
        return escapeXml(text, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeXml10(final String text, final XmlEscapeType type, final XmlEscapeLevel level) {
        return escapeXml(text, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeXml11(final String text, final XmlEscapeType type, final XmlEscapeLevel level) {
        return escapeXml(text, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>String</tt> input
     *   meant to be an XML attribute value.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     *
     * @since 1.1.5
     */
    public static String escapeXml10Attribute(final String text, final XmlEscapeType type, final XmlEscapeLevel level) {
        return escapeXml(text, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>String</tt> input
     *   meant to be an XML attribute value.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     *
     * @since 1.1.5
     */
    public static String escapeXml11Attribute(final String text, final XmlEscapeType type, final XmlEscapeLevel level) {
        return escapeXml(text, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
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
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml10Minimal(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml11Minimal(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10AttributeMinimal(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11AttributeMinimal(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml10(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml11(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10Attribute(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(String, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11Attribute(final String text, final Writer writer)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>String</tt> input,
     *   writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>/<tt>Writer</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml10(final String text, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>String</tt> input,
     *   writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>/<tt>Writer</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml11(final String text, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>String</tt> input
     *   meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>String</tt>/<tt>Writer</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10Attribute(final String text, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>String</tt> input
     *   meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>String</tt>/<tt>Writer</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11Attribute(final String text, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(text, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
    }


    /*
     * Private escape method called from XML 1.0 and XML 1.1 public methods, once the correct
     * symbol set has been selected.
     */
    private static void escapeXml(final String text, final Writer writer, final XmlEscapeSymbols symbols,
                                  final XmlEscapeType type, final XmlEscapeLevel level)
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

        XmlEscapeUtil.escape(new InternalStringReader(text), writer, symbols, type, level);

    }





    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml10Minimal(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml11Minimal(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10AttributeMinimal(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11AttributeMinimal(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml10(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML10_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml11(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10Attribute(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(Reader, Writer, XmlEscapeType, XmlEscapeLevel)} with the following
     *   preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11Attribute(final Reader reader, final Writer writer)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>Reader</tt> input,
     *   writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>Reader</tt>/<tt>Writer</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml10(final Reader reader, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>Reader</tt> input,
     *   writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>Reader</tt>/<tt>Writer</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeXml11(final Reader reader, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>Reader</tt> input
     *   meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>Reader</tt>/<tt>Writer</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10Attribute(final Reader reader, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>Reader</tt> input
     *   meant to be an XML attribute value, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>Reader</tt>/<tt>Writer</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11Attribute(final Reader reader, final Writer writer, final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(reader, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
    }


    /*
     * Private escape method called from XML 1.0 and XML 1.1 public methods, once the correct
     * symbol set has been selected.
     */
    private static void escapeXml(final Reader reader, final Writer writer, final XmlEscapeSymbols symbols,
                                  final XmlEscapeType type, final XmlEscapeLevel level)
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

        XmlEscapeUtil.escape(reader, writer, symbols, type, level);

    }





    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
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
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapeXml11Minimal(final char[] text, final int offset, final int len, final Writer writer)
                                          throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10AttributeMinimal(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 1 (only markup-significant chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the five markup-significant characters which
     *   are <em>predefined</em> as Character Entity References in XML:
     *   <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>, <tt>&quot;</tt> and <tt>&#39;</tt>.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_1_ONLY_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11AttributeMinimal(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
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
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapeXml11(final char[] text, final int offset, final int len, final Writer writer)
                                   throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.0 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml10(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10Attribute(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform an XML 1.1 level 2 (markup-significant and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input meant to be an XML attribute value.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The five markup-significant characters: <tt>&lt;</tt>, <tt>&gt;</tt>, <tt>&amp;</tt>,
     *       <tt>&quot;</tt> and <tt>&#39;</tt></li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by replacing those chars by the corresponding XML Character Entity References
     *   (e.g. <tt>'&amp;lt;'</tt>) when such CER exists for the replaced character, and replacing by a hexadecimal
     *   character reference (e.g. <tt>'&amp;#x2430;'</tt>) when there there is no CER for the replaced character.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   This method calls {@link #escapeXml11(char[], int, int, java.io.Writer, XmlEscapeType, XmlEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link org.unbescape.xml.XmlEscapeType#CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link org.unbescape.xml.XmlEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11Attribute(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS,
                XmlEscapeType.CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA,
                XmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>char[]</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapeXml10(final char[] text, final int offset, final int len, final Writer writer,
                                   final XmlEscapeType type, final XmlEscapeLevel level)
                                   throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   All other <tt>char[]</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapeXml11(final char[] text, final int offset, final int len, final Writer writer,
                                   final XmlEscapeType type, final XmlEscapeLevel level)
                                   throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.0 <strong>escape</strong> operation on a <tt>char[]</tt> input
     *   meant to be an XML attribute value.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>char[]</tt>-based <tt>escapeXml10*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml10Attribute(final char[] text, final int offset, final int len, final Writer writer,
                                   final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML10_ATTRIBUTE_SYMBOLS, type, level);
    }


    /**
     * <p>
     *   Perform a (configurable) XML 1.1 <strong>escape</strong> operation on a <tt>char[]</tt> input
     *   meant to be an XML attribute value.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.xml.XmlEscapeType} and {@link org.unbescape.xml.XmlEscapeLevel}
     *   argument values.
     * </p>
     * <p>
     *   Besides, being an attribute value also <tt>&#92;t</tt>, <tt>&#92;n</tt> and <tt>&#92;r</tt> will
     *   be escaped to avoid white-space normalization from removing line feeds (turning them into white
     *   spaces) during future parsing operations.
     * </p>
     * <p>
     *   All other <tt>char[]</tt>-based <tt>escapeXml11*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see {@link org.unbescape.xml.XmlEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.xml.XmlEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.5
     */
    public static void escapeXml11Attribute(final char[] text, final int offset, final int len, final Writer writer,
                                   final XmlEscapeType type, final XmlEscapeLevel level)
            throws IOException {
        escapeXml(text, offset, len, writer, XmlEscapeSymbols.XML11_ATTRIBUTE_SYMBOLS, type, level);
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
     *   Perform an XML <strong>unescape</strong> operation on a <tt>String</tt> input.
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
     * @param text the <tt>String</tt> to be unescaped.
     * @return The unescaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no unescaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String unescapeXml(final String text) {
        // The chosen symbols (1.0 or 1.1) don't really matter, as both contain the same CERs
        if (text == null) {
            return null;
        }
        if (text.indexOf('&') < 0) {
            // Fail fast, avoid more complex (and less JIT-table) method to execute if not needed
            return text;
        }
        return XmlEscapeUtil.unescape(text, XmlEscapeSymbols.XML11_SYMBOLS);
    }


    /**
     * <p>
     *   Perform an XML <strong>unescape</strong> operation on a <tt>String</tt> input,
     *   writing results to a <tt>Writer</tt>.
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
     * @param text the <tt>String</tt> to be unescaped.
     * @param writer the <tt>java.io.Writer</tt> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void unescapeXml(final String text, final Writer writer)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf('&') < 0) {
            // Fail fast, avoid more complex (and less JIT-table) method to execute if not needed
            writer.write(text);
            return;
        }

        // The chosen symbols (1.0 or 1.1) don't really matter, as both contain the same CERs
        XmlEscapeUtil.unescape(new InternalStringReader(text), writer, XmlEscapeSymbols.XML11_SYMBOLS);

    }


    /**
     * <p>
     *   Perform an XML <strong>unescape</strong> operation on a <tt>Reader</tt> input,
     *   writing results to a <tt>Writer</tt>.
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
     * @param reader the <tt>Reader</tt> reading the text to be unescaped.
     * @param writer the <tt>java.io.Writer</tt> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void unescapeXml(final Reader reader, final Writer writer)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        // The chosen symbols (1.0 or 1.1) don't really matter, as both contain the same CERs
        XmlEscapeUtil.unescape(reader, writer, XmlEscapeSymbols.XML11_SYMBOLS);

    }


    /**
     * <p>
     *   Perform an XML <strong>unescape</strong> operation on a <tt>char[]</tt> input.
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
     * @param text the <tt>char[]</tt> to be unescaped.
     * @param offset the position in <tt>text</tt> at which the unescape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be unescaped.
     * @param writer the <tt>java.io.Writer</tt> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
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



    /*
     * This is basically a very simplified, thread-unsafe version of StringReader that should
     * perform better than the original StringReader by removing all synchronization structures.
     *
     * Note the only implemented methods are those that we know are really used from within the
     * stream-based escape/unescape operations.
     */
    private static final class InternalStringReader extends Reader {

        private String str;
        private int length;
        private int next = 0;

        public InternalStringReader(final String s) {
            super();
            this.str = s;
            this.length = s.length();
        }

        @Override
        public int read() throws IOException {
            if (this.next >= length) {
                return -1;
            }
            return this.str.charAt(this.next++);
        }

        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                    ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }
            if (this.next >= this.length) {
                return -1;
            }
            int n = Math.min(this.length - this.next, len);
            this.str.getChars(this.next, this.next + n, cbuf, off);
            this.next += n;
            return n;
        }

        @Override
        public void close() throws IOException {
            this.str = null; // Just set the reference to null, help the GC
        }

    }


}

