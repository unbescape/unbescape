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
package org.unbescape.css;

import java.io.IOException;
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
 * <h4><u>Configuration of escape/unescape operations</u></h4>
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
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   Specific features of the CSS escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>Complete set of CSS <em>Backslash Escapes</em> supported (e.g. <kbd>&#92;+</kbd>, <kbd>&#92;(</kbd>,
 *       <kbd>&#92;)</kbd>, etc.).</li>
 *   <li>Full set of escape syntax rules supported, both for <strong>CSS identifiers</strong> and
 *       <strong>CSS Strings</strong> (or <em>literals</em>).</li>
 *   <li>Non-standard tweaks supported: <kbd>&#92;:</kbd> not used because of lacking support in
 *       Internet Explorer &lt; 8, <kbd>&#92;_</kbd> escaped at the beginning of identifiers for better
 *       Internet Explorer 6 support, etc.</li>
 *   <li>Hexadecimal escapes (a.k.a. <em>unicode escapes</em>) are supported both in escape
 *       and unescape operations, and both in <em>compact</em> (<kbd>&#92;E1 </kbd>) and six-digit
 *       forms (<kbd>&#92;0000E1</kbd>).</li>
 *   <li>Support for the whole Unicode character set: <kbd>&bsol;u0000</kbd> to <kbd>&bsol;u10FFFF</kbd>, including
 *       characters not representable by only one <kbd>char</kbd> in Java (<kbd>&gt;&bsol;uFFFF</kbd>).</li>
 *   <li>Support for unescaping unicode characters &gt; U+FFFF both when represented in standard form (one char,
 *       <kbd>&#92;20000</kbd>) and non-standard (surrogate pair, <kbd>&#92;D840&#92;DC00</kbd>, used by older
 *       WebKit browsers).</li>
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
 *   <dt>Backslash escapes</dt>
 *     <dd>Escape sequences performed by means of prefixing a <em>backslash</em> (<kbd>&#92;</kbd>) to
 *         the escaped char: <kbd>&#92;+</kbd>, <kbd>&#92;(</kbd>, <kbd>&#92;)</kbd></dd>
 *   <dt>HEXA escapes</dt>
 *     <dd>Complete representation of unicode codepoints up to <kbd>U+10FFFF</kbd>, in two forms:
 *         <ul>
 *           <li><em>Compact</em>: non-zero-padded hexadecimal representation (<kbd>&#92;E1 </kbd>), followed
 *               by an optional whitespace (<kbd>U+0020</kbd>), required if after the escaped character comes
 *               a hexadecimal digit (<kbd>[0-9A-Fa-f]</kbd>) or another whitespace (<kbd>&nbps;</kbd>).</li>
 *           <li><em>Six-digit</em>: zero-padded hexadecimal representation (<kbd>&#92;0000E1</kbd>), followed
 *               by an optional whitespace (<kbd>U+0020</kbd>), required if after the escaped character comes
 *               another whitespace (<kbd>&nbsp;</kbd>).</li>
 *         </ul>
 *     </dd>
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
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS String basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>) and
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *       and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssString(String, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssStringEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssStringMinimal(final String text) {
        return escapeCssString(text,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS String level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS String basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>) and
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(String, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssStringEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssString(final String text) {
        return escapeCssString(text,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS String <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssStringEscapeType} and
     *   {@link CssStringEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <kbd>String</kbd>-based <kbd>escapeCssString*(...)</kbd> methods call this one with preconfigured
     *   <kbd>type</kbd> and <kbd>level</kbd> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param type the type of escape operation to be performed, see
     *             {@link CssStringEscapeType}.
     * @param level the escape level to be applied, see {@link CssStringEscapeLevel}.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
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
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS String basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>) and
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>).
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *       and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(char[], int, int, java.io.Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssStringEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssStringMinimal(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssString(text, offset, len, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS String level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS String basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>) and
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>).
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssString(char[], int, int, java.io.Writer, CssStringEscapeType, CssStringEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssStringEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssStringEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssString(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssString(text, offset, len, writer,
                CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS String <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssStringEscapeType} and
     *   {@link CssStringEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <kbd>char[]</kbd>-based <kbd>escapeCssString*(...)</kbd> methods call this one with preconfigured
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
     * @param type the type of escape operation to be performed, see
     *             {@link CssStringEscapeType}.
     * @param level the escape level to be applied, see {@link CssStringEscapeLevel}.
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
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS Identifier basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <kbd>&#92; </kbd> (<kbd>U+0020</kbd>),
     *       <kbd>&#92;!</kbd> (<kbd>U+0021</kbd>),
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *       <kbd>&#92;#</kbd> (<kbd>U+0023</kbd>),
     *       <kbd>&#92;$</kbd> (<kbd>U+0024</kbd>),
     *       <kbd>&#92;%</kbd> (<kbd>U+0025</kbd>),
     *       <kbd>&#92;&amp;</kbd> (<kbd>U+0026</kbd>),
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *       <kbd>&#92;(</kbd> (<kbd>U+0028</kbd>),
     *       <kbd>&#92;)</kbd> (<kbd>U+0029</kbd>),
     *       <kbd>&#92;*</kbd> (<kbd>U+002A</kbd>),
     *       <kbd>&#92;+</kbd> (<kbd>U+002B</kbd>),
     *       <kbd>&#92;,</kbd> (<kbd>U+002C</kbd>),
     *       <kbd>&#92;.</kbd> (<kbd>U+002E</kbd>),
     *       <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>),
     *       <kbd>&#92;;</kbd> (<kbd>U+003B</kbd>),
     *       <kbd>&#92;&lt;</kbd> (<kbd>U+003C</kbd>),
     *       <kbd>&#92;=</kbd> (<kbd>U+003D</kbd>),
     *       <kbd>&#92;&gt;</kbd> (<kbd>U+003E</kbd>),
     *       <kbd>&#92;?</kbd> (<kbd>U+003F</kbd>),
     *       <kbd>&#92;@</kbd> (<kbd>U+0040</kbd>),
     *       <kbd>&#92;[</kbd> (<kbd>U+005B</kbd>),
     *       <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>),
     *       <kbd>&#92;]</kbd> (<kbd>U+005D</kbd>),
     *       <kbd>&#92;^</kbd> (<kbd>U+005E</kbd>),
     *       <kbd>&#92;`</kbd> (<kbd>U+0060</kbd>),
     *       <kbd>&#92;{</kbd> (<kbd>U+007B</kbd>),
     *       <kbd>&#92;|</kbd> (<kbd>U+007C</kbd>),
     *       <kbd>&#92;}</kbd> (<kbd>U+007D</kbd>) and
     *       <kbd>&#92;~</kbd> (<kbd>U+007E</kbd>).
     *       Note that the <kbd>&#92;-</kbd> (<kbd>U+002D</kbd>) escape sequence exists, but will only be used
     *       when an identifier starts with two hypens or hyphen + digit. Also, the <kbd>&#92;_</kbd>
     *       (<kbd>U+005F</kbd>) escape will only be used at the beginning of an identifier to avoid
     *       problems with Internet Explorer 6. In the same sense, note that the <kbd>&#92;:</kbd>
     *       (<kbd>U+003A</kbd>) escape sequence is also defined in the standard, but will not be
     *       used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *       and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeCssIdentifier(String, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssIdentifierMinimal(final String text) {
        return escapeCssIdentifier(text,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS Identifier level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS Identifier basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <kbd>&#92; </kbd> (<kbd>U+0020</kbd>),
     *               <kbd>&#92;!</kbd> (<kbd>U+0021</kbd>),
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *               <kbd>&#92;#</kbd> (<kbd>U+0023</kbd>),
     *               <kbd>&#92;$</kbd> (<kbd>U+0024</kbd>),
     *               <kbd>&#92;%</kbd> (<kbd>U+0025</kbd>),
     *               <kbd>&#92;&amp;</kbd> (<kbd>U+0026</kbd>),
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *               <kbd>&#92;(</kbd> (<kbd>U+0028</kbd>),
     *               <kbd>&#92;)</kbd> (<kbd>U+0029</kbd>),
     *               <kbd>&#92;*</kbd> (<kbd>U+002A</kbd>),
     *               <kbd>&#92;+</kbd> (<kbd>U+002B</kbd>),
     *               <kbd>&#92;,</kbd> (<kbd>U+002C</kbd>),
     *               <kbd>&#92;.</kbd> (<kbd>U+002E</kbd>),
     *               <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>),
     *               <kbd>&#92;;</kbd> (<kbd>U+003B</kbd>),
     *               <kbd>&#92;&lt;</kbd> (<kbd>U+003C</kbd>),
     *               <kbd>&#92;=</kbd> (<kbd>U+003D</kbd>),
     *               <kbd>&#92;&gt;</kbd> (<kbd>U+003E</kbd>),
     *               <kbd>&#92;?</kbd> (<kbd>U+003F</kbd>),
     *               <kbd>&#92;@</kbd> (<kbd>U+0040</kbd>),
     *               <kbd>&#92;[</kbd> (<kbd>U+005B</kbd>),
     *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>),
     *               <kbd>&#92;]</kbd> (<kbd>U+005D</kbd>),
     *               <kbd>&#92;^</kbd> (<kbd>U+005E</kbd>),
     *               <kbd>&#92;`</kbd> (<kbd>U+0060</kbd>),
     *               <kbd>&#92;{</kbd> (<kbd>U+007B</kbd>),
     *               <kbd>&#92;|</kbd> (<kbd>U+007C</kbd>),
     *               <kbd>&#92;}</kbd> (<kbd>U+007D</kbd>) and
     *               <kbd>&#92;~</kbd> (<kbd>U+007E</kbd>).
     *               Note that the <kbd>&#92;-</kbd> (<kbd>U+002D</kbd>) escape sequence exists, but will only be used
     *               when an identifier starts with two hypens or hyphen + digit. Also, the <kbd>&#92;_</kbd>
     *               (<kbd>U+005F</kbd>) escape will only be used at the beginning of an identifier to avoid
     *               problems with Internet Explorer 6. In the same sense, note that the <kbd>&#92;:</kbd>
     *               (<kbd>U+003A</kbd>) escape sequence is also defined in the standard, but will not be
     *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(String, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static String escapeCssIdentifier(final String text) {
        return escapeCssIdentifier(text,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS Identifier <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssIdentifierEscapeType} and
     *   {@link CssIdentifierEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <kbd>String</kbd>-based <kbd>escapeCssIdentifier*(...)</kbd> methods call this one with preconfigured
     *   <kbd>type</kbd> and <kbd>level</kbd> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param type the type of escape operation to be performed, see
     *             {@link CssIdentifierEscapeType}.
     * @param level the escape level to be applied, see {@link CssIdentifierEscapeLevel}.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
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
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the CSS Identifier basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Backslash Escapes</em>:
     *       <kbd>&#92; </kbd> (<kbd>U+0020</kbd>),
     *       <kbd>&#92;!</kbd> (<kbd>U+0021</kbd>),
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *       <kbd>&#92;#</kbd> (<kbd>U+0023</kbd>),
     *       <kbd>&#92;$</kbd> (<kbd>U+0024</kbd>),
     *       <kbd>&#92;%</kbd> (<kbd>U+0025</kbd>),
     *       <kbd>&#92;&amp;</kbd> (<kbd>U+0026</kbd>),
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *       <kbd>&#92;(</kbd> (<kbd>U+0028</kbd>),
     *       <kbd>&#92;)</kbd> (<kbd>U+0029</kbd>),
     *       <kbd>&#92;*</kbd> (<kbd>U+002A</kbd>),
     *       <kbd>&#92;+</kbd> (<kbd>U+002B</kbd>),
     *       <kbd>&#92;,</kbd> (<kbd>U+002C</kbd>),
     *       <kbd>&#92;.</kbd> (<kbd>U+002E</kbd>),
     *       <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>),
     *       <kbd>&#92;;</kbd> (<kbd>U+003B</kbd>),
     *       <kbd>&#92;&lt;</kbd> (<kbd>U+003C</kbd>),
     *       <kbd>&#92;=</kbd> (<kbd>U+003D</kbd>),
     *       <kbd>&#92;&gt;</kbd> (<kbd>U+003E</kbd>),
     *       <kbd>&#92;?</kbd> (<kbd>U+003F</kbd>),
     *       <kbd>&#92;@</kbd> (<kbd>U+0040</kbd>),
     *       <kbd>&#92;[</kbd> (<kbd>U+005B</kbd>),
     *       <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>),
     *       <kbd>&#92;]</kbd> (<kbd>U+005D</kbd>),
     *       <kbd>&#92;^</kbd> (<kbd>U+005E</kbd>),
     *       <kbd>&#92;`</kbd> (<kbd>U+0060</kbd>),
     *       <kbd>&#92;{</kbd> (<kbd>U+007B</kbd>),
     *       <kbd>&#92;|</kbd> (<kbd>U+007C</kbd>),
     *       <kbd>&#92;}</kbd> (<kbd>U+007D</kbd>) and
     *       <kbd>&#92;~</kbd> (<kbd>U+007E</kbd>).
     *       Note that the <kbd>&#92;-</kbd> (<kbd>U+002D</kbd>) escape sequence exists, but will only be used
     *       when an identifier starts with two hypens or hyphen + digit. Also, the <kbd>&#92;_</kbd>
     *       (<kbd>U+005F</kbd>) escape will only be used at the beginning of an identifier to avoid
     *       problems with Internet Explorer 6. In the same sense, note that the <kbd>&#92;:</kbd>
     *       (<kbd>U+003A</kbd>) escape sequence is also defined in the standard, but will not be
     *       used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *       and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(char[], int, int, java.io.Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifierMinimal(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssIdentifier(text, offset, len, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a CSS Identifier level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The CSS Identifier basic escape set:
     *         <ul>
     *           <li>The <em>Backslash Escapes</em>:
     *               <kbd>&#92; </kbd> (<kbd>U+0020</kbd>),
     *               <kbd>&#92;!</kbd> (<kbd>U+0021</kbd>),
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *               <kbd>&#92;#</kbd> (<kbd>U+0023</kbd>),
     *               <kbd>&#92;$</kbd> (<kbd>U+0024</kbd>),
     *               <kbd>&#92;%</kbd> (<kbd>U+0025</kbd>),
     *               <kbd>&#92;&amp;</kbd> (<kbd>U+0026</kbd>),
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *               <kbd>&#92;(</kbd> (<kbd>U+0028</kbd>),
     *               <kbd>&#92;)</kbd> (<kbd>U+0029</kbd>),
     *               <kbd>&#92;*</kbd> (<kbd>U+002A</kbd>),
     *               <kbd>&#92;+</kbd> (<kbd>U+002B</kbd>),
     *               <kbd>&#92;,</kbd> (<kbd>U+002C</kbd>),
     *               <kbd>&#92;.</kbd> (<kbd>U+002E</kbd>),
     *               <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>),
     *               <kbd>&#92;;</kbd> (<kbd>U+003B</kbd>),
     *               <kbd>&#92;&lt;</kbd> (<kbd>U+003C</kbd>),
     *               <kbd>&#92;=</kbd> (<kbd>U+003D</kbd>),
     *               <kbd>&#92;&gt;</kbd> (<kbd>U+003E</kbd>),
     *               <kbd>&#92;?</kbd> (<kbd>U+003F</kbd>),
     *               <kbd>&#92;@</kbd> (<kbd>U+0040</kbd>),
     *               <kbd>&#92;[</kbd> (<kbd>U+005B</kbd>),
     *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>),
     *               <kbd>&#92;]</kbd> (<kbd>U+005D</kbd>),
     *               <kbd>&#92;^</kbd> (<kbd>U+005E</kbd>),
     *               <kbd>&#92;`</kbd> (<kbd>U+0060</kbd>),
     *               <kbd>&#92;{</kbd> (<kbd>U+007B</kbd>),
     *               <kbd>&#92;|</kbd> (<kbd>U+007C</kbd>),
     *               <kbd>&#92;}</kbd> (<kbd>U+007D</kbd>) and
     *               <kbd>&#92;~</kbd> (<kbd>U+007E</kbd>).
     *               Note that the <kbd>&#92;-</kbd> (<kbd>U+002D</kbd>) escape sequence exists, but will only be used
     *               when an identifier starts with two hypens or hyphen + digit. Also, the <kbd>&#92;_</kbd>
     *               (<kbd>U+005F</kbd>) escape will only be used at the beginning of an identifier to avoid
     *               problems with Internet Explorer 6. In the same sense, note that the <kbd>&#92;:</kbd>
     *               (<kbd>U+003A</kbd>) escape sequence is also defined in the standard, but will not be
     *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using Backslash escapes whenever possible. For escaped
     *   characters that do not have an associated Backslash, default to <kbd>&#92;FF </kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeCssIdentifier(char[], int, int, java.io.Writer, CssIdentifierEscapeType, CssIdentifierEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link CssIdentifierEscapeType#BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link CssIdentifierEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeCssIdentifier(final char[] text, final int offset, final int len, final Writer writer)
            throws IOException {
        escapeCssIdentifier(text, offset, len, writer,
                CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA,
                CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) CSS Identifier <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link CssIdentifierEscapeType} and
     *   {@link CssIdentifierEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <kbd>char[]</kbd>-based <kbd>escapeCssIdentifier*(...)</kbd> methods call this one with preconfigured
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
     * @param type the type of escape operation to be performed, see
     *             {@link CssIdentifierEscapeType}.
     * @param level the escape level to be applied, see {@link CssIdentifierEscapeLevel}.
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
     *   Perform a CSS <strong>unescape</strong> operation on a <kbd>String</kbd> input.
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
     * @param text the <kbd>String</kbd> to be unescaped.
     * @return The unescaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no unescaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String unescapeCss(final String text) {
        return CssUnescapeUtil.unescape(text);
    }


    /**
     * <p>
     *   Perform a CSS <strong>unescape</strong> operation on a <kbd>char[]</kbd> input.
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
     * @param text the <kbd>char[]</kbd> to be unescaped.
     * @param offset the position in <kbd>text</kbd> at which the unescape operation should start.
     * @param len the number of characters in <kbd>text</kbd> that should be unescaped.
     * @param writer the <kbd>java.io.Writer</kbd> to which the unescaped result will be written. Nothing will
     *               be written at all to this writer if <kbd>text</kbd> is <kbd>null</kbd>.
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



}

