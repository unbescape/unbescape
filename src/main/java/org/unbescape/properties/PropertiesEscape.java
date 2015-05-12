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
package org.unbescape.properties;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 *   Utility class for performing Java Properties (<tt>.properties</tt> files) escape/unescape operations.
 * </p>
 *
 * <p>
 *   This class supports both escaping of <strong>properties keys</strong> and
 *   <strong>properties values</strong>.
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
 *       needs of the scenario). Its values are defined by the {@link org.unbescape.properties.PropertiesKeyEscapeLevel}
 *       and {@link org.unbescape.properties.PropertiesValueEscapeLevel} enums.</li>
 * </ul>
 * <p>
 *   <strong>Unescape</strong> operations need no configuration parameters. Unescape operations
 *   will always perform <em>complete</em> Java Properties unescape of SECs and u-based escapes.
 * </p>
 *
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   Specific features of the <tt>.properties</tt> key/value escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>The Java Properties basic escape set is supported. This <em>basic set</em> consists of:
 *         <ul>
 *           <li>The <em>Single Escape Characters</em>:
 *               <tt>&#92;t</tt> (<tt>U+0009</tt>),
 *               <tt>&#92;n</tt> (<tt>U+000A</tt>),
 *               <tt>&#92;f</tt> (<tt>U+000C</tt>),
 *               <tt>&#92;r</tt> (<tt>U+000D</tt>) and
 *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
 *               Besides,
 *               <tt>&#92;&nbsp;</tt> (<tt>U+0020</tt>),
 *               <tt>&#92;:</tt> (<tt>U+003A</tt>) and
 *               <tt>&#92;=</tt> (<tt>U+003D</tt>) will be used in Properties keys (not values).
 *           </li>
 *           <li>
 *               Two ranges of non-displayable, control characters (some of which are already part of the
 *               <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
 *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
 *           </li>
 *         </ul>
 *   </li>
 *   <li>U-based hexadecimal escapes (a.k.a. <em>unicode escapes</em>) are supported both in escape
 *       and unescape operations: <tt>&#92;u00E1</tt>.</li>
 *   <li>Full set of escape syntax rules supported, both for <strong><tt>.properties</tt> keys</strong> and
 *       <strong><tt>.properties</tt> values</strong>.</li>
 *   <li>Support for the whole Unicode character set: <tt>&#92;u0000</tt> to <tt>&#92;u10FFFF</tt>, including
 *       characters not representable by only one <tt>char</tt> in Java (<tt>&gt;&#92;uFFFF</tt>).</li>
 * </ul>
 *
 * <h4><u>Input/Output</u></h4>
 *
 * <p>
 *   There are two different input/output modes that can be used in escape/unescape operations:
 * </p>
 * <ul>
 *   <li><em><tt>String</tt> input, <tt>String</tt> output</em>: Input is specified as a <tt>String</tt> object
 *       and output is returned as another. In order to improve memory performance, all escape and unescape
 *       operations <u>will return the exact same input object as output if no escape/unescape modifications
 *       are required</u>.</li>
 *   <li><em><tt>char[]</tt> input, <tt>java.io.Writer</tt> output</em>: Input will be read from a char array
 *       (<tt>char[]</tt>) and output will be written into the specified <tt>java.io.Writer</tt>.
 *       Two <tt>int</tt> arguments called <tt>offset</tt> and <tt>len</tt> will be
 *       used for specifying the part of the <tt>char[]</tt> that should be escaped/unescaped. These methods
 *       should be called with <tt>offset = 0</tt> and <tt>len = text.length</tt> in order to process
 *       the whole <tt>char[]</tt>.</li>
 * </ul>
 *
 * <h4><u>Glossary</u></h4>
 *
 * <dl>
 *   <dt>SEC</dt>
 *     <dd>Single Escape Character:
 *               <tt>&#92;t</tt> (<tt>U+0009</tt>),
 *               <tt>&#92;n</tt> (<tt>U+000A</tt>),
 *               <tt>&#92;f</tt> (<tt>U+000C</tt>),
 *               <tt>&#92;r</tt> (<tt>U+000D</tt>) and
 *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
 *               Besides,
 *               <tt>&#92;&nbsp;</tt> (<tt>U+0020</tt>),
 *               <tt>&#92;:</tt> (<tt>U+003A</tt>) and
 *               <tt>&#92;=</tt> (<tt>U+003D</tt>) will be used in Properties keys (not values).
 *     </dd>
 *   <dt>UHEXA escapes</dt>
 *     <dd>Also called <em>u-based hexadecimal escapes</em> or simply <em>unicode escapes</em>:
 *         complete representation of unicode codepoints up to <tt>U+FFFF</tt>, with <tt>&#92;u</tt>
 *         followed by exactly four hexadecimal figures: <tt>&#92;u00E1</tt>. Unicode codepoints &gt;
 *         <tt>U+FFFF</tt> can be represented in Java by mean of two UHEXA escapes (a
 *         <em>surrogate pair</em>).</dd>
 *   <dt>Unicode Codepoint</dt>
 *     <dd>Each of the <tt>int</tt> values conforming the Unicode code space.
 *         Normally corresponding to a Java <tt>char</tt> primitive value (codepoint &lt;= <tt>&#92;uFFFF</tt>),
 *         but might be two <tt>char</tt>s for codepoints <tt>&#92;u10000</tt> to <tt>&#92;u10FFFF</tt> if the
 *         first <tt>char</tt> is a high surrogate (<tt>&#92;uD800</tt> to <tt>&#92;uDBFF</tt>) and the
 *         second is a low surrogate (<tt>&#92;uDC00</tt> to <tt>&#92;uDFFF</tt>).</dd>
 * </dl>
 *
 * <h4><u>References</u></h4>
 *
 * <p>
 *   The following references apply:
 * </p>
 * <ul>
 *   <li><a href="http://en.wikipedia.org/wiki/.properties" target="_blank"><tt>.properties</tt></a> [wikipedia.org]</li>
 *   <li><a href="http://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.Reader-"
 *        target="_blank">Java API: <tt>java.util.Properties#load(java.io.Reader)</tt></a> [oracle.com]</li>
 * </ul>
 *
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
public final class PropertiesEscape {




    /**
     * <p>
     *   Perform a Java Properties Value level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the Java Properties basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *       <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *       <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *       <tt>&#92;r</tt> (<tt>U+000D</tt>) and
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls {@link #escapePropertiesValue(String, PropertiesValueEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesValueEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if <tt>text</tt> is <tt>null</tt>.
     */
    public static String escapePropertiesValueMinimal(final String text) {
        return escapePropertiesValue(text, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a Java Properties Value level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The Java Properties basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *               <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *               <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *               <tt>&#92;r</tt> (<tt>U+000D</tt>) and
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to <tt>&#92;uFFFF</tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapePropertiesValue(String, PropertiesValueEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesValueEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if <tt>text</tt> is <tt>null</tt>.
     */
    public static String escapePropertiesValue(final String text) {
        return escapePropertiesValue(text, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) Java Properties Value <strong>escape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.properties.PropertiesValueEscapeLevel} argument value.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapePropertiesValue*(...)</tt> methods call this one with
     *   preconfigured <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param level the escape level to be applied, see {@link org.unbescape.properties.PropertiesValueEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if <tt>text</tt> is <tt>null</tt>.
     */
    public static String escapePropertiesValue(final String text, final PropertiesValueEscapeLevel level) {

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return PropertiesValueEscapeUtil.escape(text, level);

    }




    /**
     * <p>
     *   Perform a Java Properties Value level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the Java Properties basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *       <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *       <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *       <tt>&#92;r</tt> (<tt>U+000D</tt>) and
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls {@link #escapePropertiesValue(char[], int, int, java.io.Writer, PropertiesValueEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesValueEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <tt>text</tt> is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapePropertiesValueMinimal(final char[] text, final int offset, final int len, final Writer writer)
                                                    throws IOException {
        escapePropertiesValue(text, offset, len, writer, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a Java Properties Value level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The Java Properties basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *               <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *               <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *               <tt>&#92;r</tt> (<tt>U+000D</tt>) and
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to <tt>&#92;uFFFF</tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapePropertiesValue(char[], int, int, java.io.Writer, PropertiesValueEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesValueEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <tt>text</tt> is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapePropertiesValue(final char[] text, final int offset, final int len, final Writer writer)
                                             throws IOException {
        escapePropertiesValue(text, offset, len, writer, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) Java Properties Value <strong>escape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.properties.PropertiesValueEscapeLevel} argument value.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapePropertiesValue*(...)</tt> methods call this one with
     *   preconfigured <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <tt>text</tt> is <tt>null</tt>.
     * @param level the escape level to be applied, see {@link org.unbescape.properties.PropertiesValueEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapePropertiesValue(final char[] text, final int offset, final int len, final Writer writer,
                                             final PropertiesValueEscapeLevel level)
                                             throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
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

        PropertiesValueEscapeUtil.escape(text, offset, len, writer, level);

    }









    /**
     * <p>
     *   Perform a Java Properties Key level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the Java Properties Key basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *       <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *       <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *       <tt>&#92;r</tt> (<tt>U+000D</tt>),
     *       <tt>&#92;&nbsp;</tt> (<tt>U+0020</tt>),
     *       <tt>&#92;:</tt> (<tt>U+003A</tt>),
     *       <tt>&#92;=</tt> (<tt>U+003D</tt>) and
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls {@link #escapePropertiesKey(String, PropertiesKeyEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesKeyEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if <tt>text</tt> is <tt>null</tt>.
     */
    public static String escapePropertiesKeyMinimal(final String text) {
        return escapePropertiesKey(text, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a Java Properties Key level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The Java Properties Key basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *               <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *               <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *               <tt>&#92;r</tt> (<tt>U+000D</tt>),
     *               <tt>&#92;&nbsp;</tt> (<tt>U+0020</tt>),
     *               <tt>&#92;:</tt> (<tt>U+003A</tt>),
     *               <tt>&#92;=</tt> (<tt>U+003D</tt>) and
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to <tt>&#92;uFFFF</tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapePropertiesKey(String, PropertiesKeyEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesKeyEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if <tt>text</tt> is <tt>null</tt>.
     */
    public static String escapePropertiesKey(final String text) {
        return escapePropertiesKey(text, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) Java Properties Key <strong>escape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.properties.PropertiesKeyEscapeLevel} argument value.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapePropertiesKey*(...)</tt> methods call this one with
     *   preconfigured <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param level the escape level to be applied, see {@link org.unbescape.properties.PropertiesKeyEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if <tt>text</tt> is <tt>null</tt>.
     */
    public static String escapePropertiesKey(final String text, final PropertiesKeyEscapeLevel level) {

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return PropertiesKeyEscapeUtil.escape(text, level);
    }




    /**
     * <p>
     *   Perform a Java Properties Key level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the Java Properties Key basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *       <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *       <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *       <tt>&#92;r</tt> (<tt>U+000D</tt>),
     *       <tt>&#92;&nbsp;</tt> (<tt>U+0020</tt>),
     *       <tt>&#92;:</tt> (<tt>U+003A</tt>),
     *       <tt>&#92;=</tt> (<tt>U+003D</tt>) and
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls {@link #escapePropertiesKey(char[], int, int, java.io.Writer, PropertiesKeyEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesKeyEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <tt>text</tt> is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapePropertiesKeyMinimal(final char[] text, final int offset, final int len, final Writer writer)
                                                  throws IOException {
        escapePropertiesKey(text, offset, len, writer, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a Java Properties Key level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The Java Properties Key basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <tt>&#92;t</tt> (<tt>U+0009</tt>),
     *               <tt>&#92;n</tt> (<tt>U+000A</tt>),
     *               <tt>&#92;f</tt> (<tt>U+000C</tt>),
     *               <tt>&#92;r</tt> (<tt>U+000D</tt>),
     *               <tt>&#92;&nbsp;</tt> (<tt>U+0020</tt>),
     *               <tt>&#92;:</tt> (<tt>U+003A</tt>),
     *               <tt>&#92;=</tt> (<tt>U+003D</tt>) and
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to <tt>&#92;uFFFF</tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapePropertiesKey(char[], int, int, java.io.Writer, PropertiesKeyEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>level</tt>:
     *       {@link PropertiesKeyEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
     * </ul>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <tt>text</tt> is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapePropertiesKey(final char[] text, final int offset, final int len, final Writer writer)
                                           throws IOException {
        escapePropertiesKey(text, offset, len, writer, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) Java Properties Key <strong>escape</strong> operation on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.properties.PropertiesKeyEscapeLevel} argument value.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapePropertiesKey*(...)</tt> methods call this one with
     *   preconfigured <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be escaped.
     * @param offset the position in <tt>text</tt> at which the escape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if <tt>text</tt> is <tt>null</tt>.
     * @param level the escape level to be applied, see {@link org.unbescape.properties.PropertiesKeyEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapePropertiesKey(final char[] text, final int offset, final int len, final Writer writer,
                                           final PropertiesKeyEscapeLevel level)
                                           throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
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

        PropertiesKeyEscapeUtil.escape(text, offset, len, writer, level);

    }








    /**
     * <p>
     *   Perform a Java Properties (key or value) <strong>unescape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> Java Properties unescape of SECs and u-based escapes.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be unescaped.
     * @return The unescaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no unescaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if <tt>text</tt> is <tt>null</tt>.
     */
    public static String unescapeProperties(final String text) {
        return PropertiesUnescapeUtil.unescape(text);
    }


    /**
     * <p>
     *   Perform a Java Properties (key or value) <strong>unescape</strong> operation on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> Java Properties unescape of SECs and u-based escapes.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>char[]</tt> to be unescaped.
     * @param offset the position in <tt>text</tt> at which the unescape operation should start.
     * @param len the number of characters in <tt>text</tt> that should be unescaped.
     * @param writer the <tt>java.io.Writer</tt> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <tt>text</tt> is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void unescapeProperties(final char[] text, final int offset, final int len, final Writer writer)
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

        PropertiesUnescapeUtil.unescape(text, offset, len, writer);

    }





    private PropertiesEscape() {
        super();
    }



}

