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
package org.unbescape.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * <p>
 *   Utility class for performing CSS escape/unescape operations.
 * </p>
 *
 * <p>
 *   This class supports both escaping of <strong>CSS identifiers</strong> and
 *   <strong>CSS Strings</strong> (or <em>literals</em>).
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
 *       needs of the scenario). Its values are defined by the {@link CssIdentifierEscapeLevel}
 *       and {@link CssStringEscapeLevel} enums.</li>
 *   <li><em>Type</em>, which defines whether escaping should be performed by means of <em>backslash escapes</em>
 *       or by means of hexadecimal numerical escape sequences.
 *       Its values are defined by the {@link CssIdentifierEscapeType}
 *       and {@link CssStringEscapeType} enums.</li>
 * </ul>
 * <p>
 *   <strong>Unescape</strong> operations need no configuration parameters. Unescape operations
 *   will always perform <em>complete</em> unescape of backslash and hexadecimal escapes, including all
 *   required <em>tweaks</em> (i.e. optional whitespace characters) needed for unescaping.
 * </p>
 *
 * <strong><u>Features</u></strong>
 *
 * <p>
 *   Specific features of the CSS escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>Complete set of CSS <em>Backslash Escapes</em> supported (e.g. <tt>&#92;+</tt>, <tt>&#92;(</tt>,
 *       <tt>&#92;)</tt>, etc.).</li>
 *   <li>Full set of escape syntax rules supported, both for <strong>CSS identifiers</strong> and
 *       <strong>CSS Strings</strong> (or <em>literals</em>).</li>
 *   <li>Non-standard tweaks supported: <tt>&#92;:</tt> not used because of lacking support in
 *       Internet Explorer &lt; 8, <tt>&#92;_</tt> escaped at the beginning of identifiers for better
 *       Internet Explorer 6 support, etc.</li>
 *   <li>Hexadecimal escapes (a.k.a. <em>unicode escapes</em>) are supported both in escape
 *       and unescape operations, and both in <em>compact</em> (<tt>&#92;E1 </tt>) and six-digit
 *       forms (<tt>&#92;0000E1</tt>).</li>
 *   <li>Support for the whole Unicode character set: <tt>&#92;u0000</tt> to <tt>&#92;u10FFFF</tt>, including
 *       characters not representable by only one <tt>char</tt> in Java (<tt>&gt;&#92;uFFFF</tt>).</li>
 *   <li>Support for unescaping unicode characters &gt; U+FFFF both when represented in standard form (one char,
 *       <tt>&#92;20000</tt>) and non-standard (surrogate pair, <tt>&#92;D840&#92;DC00</tt>, used by older
 *       WebKit browsers).</li>
 * </ul>
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
 *   <dt>Backslash escapes</dt>
 *     <dd>Escape sequences performed by means of prefixing a <em>backslash</em> (<tt>&#92;</tt>) to
 *         the escaped char: <tt>&#92;+</tt>, <tt>&#92;(</tt>, <tt>&#92;)</tt></dd>
 *   <dt>HEXA escapes</dt>
 *     <dd>Complete representation of unicode codepoints up to <tt>U+10FFFF</tt>, in two forms:
 *         <ul>
 *           <li><em>Compact</em>: non-zero-padded hexadecimal representation (<tt>&#92;E1 </tt>), followed
 *               by an optional whitespace (<tt>U+0020</tt>), required if after the escaped character comes
 *               a hexadecimal digit (<tt>[0-9A-Fa-f]</tt>) or another whitespace (<tt>&nbsp;</tt>).</li>
 *           <li><em>Six-digit</em>: zero-padded hexadecimal representation (<tt>&#92;0000E1</tt>), followed
 *               by an optional whitespace (<tt>U+0020</tt>), required if after the escaped character comes
 *               another whitespace (<tt>&nbsp;</tt>).</li>
 *         </ul>
 *     </dd>
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
 *   <li><a href="http://www.w3.org/TR/CSS21/syndata.html#value-def-identifier" target="_blank">Cascading
 *       Style Sheets Level 2 Revision 1 (CSS 2.1) Specification</a> [w3.org]</li>
 *   <li><a href="http://mathiasbynens.be/notes/css-escapes">CSS character escape sequences</a> [mathiasbynens.be]</li>
 *   <li><a href="http://mothereff.in/css-escapes">CSS escapes tester</a> [mothereff.in]</li>
 * </ul>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0.0
 *
 */
public final class CssEscape {




    /**
     * <p>
     *   Perform a CSS String level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS String basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssString(String, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssStringMinimal(final String text) {
        return escapeCssString(text,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS String level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS String basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(String, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssString(final String text) {
        return escapeCssString(text,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS String <strong>escape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssStringEscapeType} and
     *   {@link CssStringEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapeCssString*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see
     *             {@link CssStringEscapeType}.
     * @param level the escape level to be applied, see {@link CssStringEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeCssString(final String text,
                                    final CssStringEscapeType type, final CssStringEscapeLevel level) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return CssStringEscapeUtil.escape(text, type, level);

    }




    /**
     * <p>
     *   Perform a CSS String level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS String basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssString(String, Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssStringMinimal(final String text, final Writer writer)
            throws IOException {
        escapeCssString(text, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS String level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS String basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(String, Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssString(final String text, final Writer writer)
            throws IOException {
        escapeCssString(text, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS String <strong>escape</strong> operation on a <tt>String</tt> input,
     *   writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssStringEscapeType} and
     *   {@link CssStringEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>/<tt>Writer</tt>-based <tt>escapeCssString*(...)</tt> methods call this one
     *   with preconfigured <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see
     *             {@link CssStringEscapeType}.
     * @param level the escape level to be applied, see {@link CssStringEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeCssString(final String text, final Writer writer,
                                       final CssStringEscapeType type, final CssStringEscapeLevel level)
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

        CssStringEscapeUtil.escape(new InternalStringReader(text), writer, type, level);

    }




    /**
     * <p>
     *   Perform a CSS String level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS String basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssString(Reader, Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssStringMinimal(final Reader reader, final Writer writer)
            throws IOException {
        escapeCssString(reader, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS String level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS String basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(Reader, Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssString(final Reader reader, final Writer writer)
            throws IOException {
        escapeCssString(reader, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS String <strong>escape</strong> operation on a <tt>Reader</tt> input,
     *   writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssStringEscapeType} and
     *   {@link CssStringEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>Reader</tt>/<tt>Writer</tt>-based <tt>escapeCssString*(...)</tt> methods call this one
     *   with preconfigured <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see
     *             {@link CssStringEscapeType}.
     * @param level the escape level to be applied, see {@link CssStringEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeCssString(final Reader reader, final Writer writer,
                                       final CssStringEscapeType type, final CssStringEscapeLevel level)
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

        CssStringEscapeUtil.escape(reader, writer, type, level);

    }




    /**
     * <p>
     *   Perform a CSS String level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS String basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(char[], int, int, java.io.Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssStringMinimal(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssString(text, offset, len, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS String level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS String basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(char[], int, int, java.io.Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssStringEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssString(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssString(text, offset, len, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS String <strong>escape</strong> operation on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssStringEscapeType} and
     *   {@link CssStringEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>char[]</tt>-based <tt>escapeCssString*(...)</tt> methods call this one with preconfigured
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
     * @param type the type of escape operation to be performed, see
     *             {@link CssStringEscapeType}.
     * @param level the escape level to be applied, see {@link CssStringEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapeCssString(final char[] text, final int offset, final int len, final Writer writer,
                                  final CssStringEscapeType type, final CssStringEscapeLevel level)
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

        CssStringEscapeUtil.escape(text, offset, len, writer, type, level);

    }









    /**
     * <p>
     *   Perform a CSS Identifier level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS Identifier basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92; </tt> (<tt>U+0020</tt>),
     *       <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *       <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *       <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *       <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *       <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *       <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *       <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *       <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *       <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *       <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *       <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *       <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *       <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *       <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *       <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *       <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *       <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *       <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *       <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *       <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *       <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *       <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *       <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *       <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *       <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *       <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *       Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *       when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *       (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *       problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *       (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *       used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssIdentifier(String, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssIdentifierMinimal(final String text) {
        return escapeCssIdentifier(text,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS Identifier level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS Identifier basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92; </tt> (<tt>U+0020</tt>),
     *               <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *               <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *               <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *               <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *               <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *               <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *               <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *               <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *               <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *               <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *               <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *               <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *               <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *               <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *               <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *               <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *               <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *               <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *               <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *               <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *               <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *               <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *               <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *               <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *               <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *               <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *               Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *               when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *               (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *               problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *               (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(String, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssIdentifier(final String text) {
        return escapeCssIdentifier(text,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS Identifier <strong>escape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssIdentifierEscapeType} and
     *   {@link CssIdentifierEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>-based <tt>escapeCssIdentifier*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param type the type of escape operation to be performed, see
     *             {@link CssIdentifierEscapeType}.
     * @param level the escape level to be applied, see {@link CssIdentifierEscapeLevel}.
     * @return The escaped result <tt>String</tt>. As a memory-performance improvement, will return the exact
     *         same object as the <tt>text</tt> input argument if no escaping modifications were required (and
     *         no additional <tt>String</tt> objects will be created during processing). Will
     *         return <tt>null</tt> if input is <tt>null</tt>.
     */
    public static String escapeCssIdentifier(final String text,
                                         final CssIdentifierEscapeType type, final CssIdentifierEscapeLevel level) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return CssIdentifierEscapeUtil.escape(text, type, level);

    }




    /**
     * <p>
     *   Perform a CSS Identifier level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS Identifier basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92; </tt> (<tt>U+0020</tt>),
     *       <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *       <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *       <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *       <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *       <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *       <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *       <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *       <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *       <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *       <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *       <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *       <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *       <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *       <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *       <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *       <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *       <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *       <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *       <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *       <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *       <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *       <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *       <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *       <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *       <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *       <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *       Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *       when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *       (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *       problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *       (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *       used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssIdentifier(String, Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifierMinimal(final String text, final Writer writer)
            throws IOException {
        escapeCssIdentifier(text, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS Identifier level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>String</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS Identifier basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92; </tt> (<tt>U+0020</tt>),
     *               <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *               <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *               <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *               <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *               <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *               <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *               <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *               <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *               <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *               <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *               <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *               <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *               <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *               <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *               <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *               <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *               <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *               <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *               <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *               <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *               <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *               <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *               <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *               <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *               <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *               <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *               Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *               when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *               (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *               problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *               (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(String, Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifier(final String text, final Writer writer)
            throws IOException {
        escapeCssIdentifier(text, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS Identifier <strong>escape</strong> operation on a <tt>String</tt> input,
     *   writing the results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssIdentifierEscapeType} and
     *   {@link CssIdentifierEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>String</tt>/<tt>Writer</tt>-based <tt>escapeCssIdentifier*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see
     *             {@link CssIdentifierEscapeType}.
     * @param level the escape level to be applied, see {@link CssIdentifierEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeCssIdentifier(final String text, final Writer writer,
                                           final CssIdentifierEscapeType type, final CssIdentifierEscapeLevel level)
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

        CssIdentifierEscapeUtil.escape(new InternalStringReader(text), writer, type, level);

    }




    /**
     * <p>
     *   Perform a CSS Identifier level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS Identifier basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92; </tt> (<tt>U+0020</tt>),
     *       <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *       <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *       <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *       <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *       <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *       <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *       <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *       <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *       <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *       <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *       <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *       <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *       <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *       <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *       <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *       <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *       <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *       <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *       <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *       <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *       <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *       <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *       <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *       <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *       <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *       <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *       Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *       when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *       (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *       problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *       (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *       used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssIdentifier(Reader, Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifierMinimal(final Reader reader, final Writer writer)
            throws IOException {
        escapeCssIdentifier(reader, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS Identifier level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>Reader</tt> input, writing results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS Identifier basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92; </tt> (<tt>U+0020</tt>),
     *               <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *               <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *               <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *               <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *               <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *               <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *               <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *               <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *               <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *               <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *               <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *               <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *               <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *               <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *               <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *               <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *               <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *               <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *               <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *               <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *               <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *               <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *               <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *               <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *               <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *               <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *               Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *               when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *               (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *               problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *               (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(Reader, Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifier(final Reader reader, final Writer writer)
            throws IOException {
        escapeCssIdentifier(reader, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS Identifier <strong>escape</strong> operation on a <tt>Reader</tt> input,
     *   writing the results to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssIdentifierEscapeType} and
     *   {@link CssIdentifierEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>Reader</tt>/<tt>Writer</tt>-based <tt>escapeCssIdentifier*(...)</tt> methods call this one with preconfigured
     *   <tt>type</tt> and <tt>level</tt> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be escaped.
     * @param writer the <tt>java.io.Writer</tt> to which the escaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @param type the type of escape operation to be performed, see
     *             {@link CssIdentifierEscapeType}.
     * @param level the escape level to be applied, see {@link CssIdentifierEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     *
     * @since 1.1.2
     */
    public static void escapeCssIdentifier(final Reader reader, final Writer writer,
                                           final CssIdentifierEscapeType type, final CssIdentifierEscapeLevel level)
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

        CssIdentifierEscapeUtil.escape(reader, writer, type, level);

    }




    /**
     * <p>
     *   Perform a CSS Identifier level 1 (only basic set) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS Identifier basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <tt>&#92; </tt> (<tt>U+0020</tt>),
     *       <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *       <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *       <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *       <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *       <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *       <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *       <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *       <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *       <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *       <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *       <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *       <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *       <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *       <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *       <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *       <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *       <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *       <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *       <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *       <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *       <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *       <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *       <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *       <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *       <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *       <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *       <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *       <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *       <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *       Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *       when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *       (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *       problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *       (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *       used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *       and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(char[], int, int, java.io.Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifierMinimal(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssIdentifier(text, offset, len, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS Identifier level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS Identifier basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <tt>&#92; </tt> (<tt>U+0020</tt>),
     *               <tt>&#92;!</tt> (<tt>U+0021</tt>),
     *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
     *               <tt>&#92;#</tt> (<tt>U+0023</tt>),
     *               <tt>&#92;$</tt> (<tt>U+0024</tt>),
     *               <tt>&#92;%</tt> (<tt>U+0025</tt>),
     *               <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
     *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
     *               <tt>&#92;(</tt> (<tt>U+0028</tt>),
     *               <tt>&#92;)</tt> (<tt>U+0029</tt>),
     *               <tt>&#92;*</tt> (<tt>U+002A</tt>),
     *               <tt>&#92;+</tt> (<tt>U+002B</tt>),
     *               <tt>&#92;,</tt> (<tt>U+002C</tt>),
     *               <tt>&#92;.</tt> (<tt>U+002E</tt>),
     *               <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
     *               <tt>&#92;;</tt> (<tt>U+003B</tt>),
     *               <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
     *               <tt>&#92;=</tt> (<tt>U+003D</tt>),
     *               <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
     *               <tt>&#92;?</tt> (<tt>U+003F</tt>),
     *               <tt>&#92;@</tt> (<tt>U+0040</tt>),
     *               <tt>&#92;[</tt> (<tt>U+005B</tt>),
     *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
     *               <tt>&#92;]</tt> (<tt>U+005D</tt>),
     *               <tt>&#92;^</tt> (<tt>U+005E</tt>),
     *               <tt>&#92;`</tt> (<tt>U+0060</tt>),
     *               <tt>&#92;{</tt> (<tt>U+007B</tt>),
     *               <tt>&#92;|</tt> (<tt>U+007C</tt>),
     *               <tt>&#92;}</tt> (<tt>U+007D</tt>) and
     *               <tt>&#92;~</tt> (<tt>U+007E</tt>).
     *               Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
     *               when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
     *               (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
     *               problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
     *               (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
     *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
     *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <tt>&#92;FF </tt>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(char[], int, int, java.io.Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><tt>type</tt>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><tt>level</tt>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifier(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssIdentifier(text, offset, len, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS Identifier <strong>escape</strong> operation on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssIdentifierEscapeType} and
     *   {@link CssIdentifierEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <tt>char[]</tt>-based <tt>escapeCssIdentifier*(...)</tt> methods call this one with preconfigured
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
     * @param type the type of escape operation to be performed, see
     *             {@link CssIdentifierEscapeType}.
     * @param level the escape level to be applied, see {@link CssIdentifierEscapeLevel}.
     * @throws IOException if an input/output exception occurs
     */
    public static void escapeCssIdentifier(final char[] text, final int offset, final int len, final Writer writer,
                                       final CssIdentifierEscapeType type, final CssIdentifierEscapeLevel level)
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

        CssIdentifierEscapeUtil.escape(text, offset, len, writer, type, level);

    }








    /**
     * <p>
     *   Perform a CSS <strong>unescape</strong> operation on a <tt>String</tt> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> CSS unescape of backslash and hexadecimal escape
     *   sequences.
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
    public static String unescapeCss(final String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf('\\') < 0) {
            // Fail fast, avoid more complex (and less JIT-table) method to execute if not needed
            return text;
        }
        return CssUnescapeUtil.unescape(text);
    }


    /**
     * <p>
     *   Perform a CSS <strong>unescape</strong> operation on a <tt>String</tt> input, writing results
     *   to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> CSS unescape of backslash and hexadecimal escape
     *   sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <tt>String</tt> to be unescaped.
     * @param writer the <tt>java.io.Writer</tt> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void unescapeCss(final String text, final Writer writer)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf('\\') < 0) {
            // Fail fast, avoid more complex (and less JIT-table) method to execute if not needed
            writer.write(text);
            return;
        }

        CssUnescapeUtil.unescape(new InternalStringReader(text), writer);
    }


    /**
     * <p>
     *   Perform a CSS <strong>unescape</strong> operation on a <tt>String</tt> input, writing results
     *   to a <tt>Writer</tt>.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> CSS unescape of backslash and hexadecimal escape
     *   sequences.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param reader the <tt>Reader</tt> reading the text to be unescaped.
     * @param writer the <tt>java.io.Writer</tt> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if input is <tt>null</tt>.
     * @throws IOException if an input/output exception occurs
     */
    public static void unescapeCss(final Reader reader, final Writer writer)
            throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        CssUnescapeUtil.unescape(reader, writer);
    }


    /**
     * <p>
     *   Perform a CSS <strong>unescape</strong> operation on a <tt>char[]</tt> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> CSS unescape of backslash and hexadecimal escape
     *   sequences.
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
    public static void unescapeCss(final char[] text, final int offset, final int len, final Writer writer)
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

        CssUnescapeUtil.unescape(text, offset, len, writer);

    }
    




    private CssEscape() {
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

