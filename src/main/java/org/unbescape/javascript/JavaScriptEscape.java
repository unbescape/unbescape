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
package org.unbescape.javascript;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 *   Utility class for performing JavaScript escape/unescape operations.
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
 *       needs of the scenario). Its values are defined by the {@link org.unbescape.javascript.JavaScriptEscapeLevel}
 *       enum.</li>
 *   <li><em>Type</em>, which defines whether escaping should be performed by means of SECs
 *       (Single Escape Characters like <kbd>&#92;n</kbd>) or additionally by means of x-based or u-based
 *       hexadecimal escapes (<kbd>&#92;xE1</kbd> or <kbd>&#92;u00E1</kbd>).
 *       Its values are defined by the {@link org.unbescape.javascript.JavaScriptEscapeType} enum.</li>
 * </ul>
 * <p>
 *   <strong>Unescape</strong> operations need no configuration parameters. Unescape operations
 *   will always perform <em>complete</em> unescape of SECs (<kbd>&#92;n</kbd>), x-based (<kbd>&#92;xE1</kbd>)
 *   and u-based (<kbd>&#92;u00E1</kbd>) hexadecimal escapes, and even octal escapes (<kbd>\057</kbd>, which
 *   are deprecated since ECMAScript v5 and therefore not used for escaping).
 * </p>
 *
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   Specific features of the JavaScript escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>The JavaScript basic escape set is supported. This <em>basic set</em> consists of:
 *         <ul>
 *           <li>The <em>Single Escape Characters</em>:
 *               <kbd>&#92;0</kbd> (<kbd>U+0000</kbd>),
 *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
 *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
 *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
 *               <kbd>&#92;v</kbd> (<kbd>U+000B</kbd>),
 *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
 *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
 *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
 *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
 *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>) and
 *               <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>).
 *               Note that <kbd>&#92;&#47;</kbd> is optional, and will only be used when the <kbd>&#47;</kbd>
 *               symbol appears after <kbd>&lt;</kbd>, as in <kbd>&lt;&#47;</kbd>. This is to avoid accidentally
 *               closing <kbd>&lt;script&gt;</kbd> tags in HTML. Also, note that <kbd>&#92;v</kbd>
 *               (<kbd>U+000B</kbd>) is actually included as a Single Escape
 *               Character in the JavaScript (ECMAScript) specification, but will not be used as it
 *               is not supported by Microsoft Internet Explorer versions < 9.
 *           </li>
 *           <li>
 *               Two ranges of non-displayable, control characters (some of which are already part of the
 *               <em>single escape characters</em> list): <kbd>U+0001</kbd> to <kbd>U+001F</kbd> and
 *               <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
 *           </li>
 *         </ul>
 *   </li>
 *   <li>X-based hexadecimal escapes (a.k.a. <em>hexadecimal escapes</em>) are supported both in escape
 *       and unescape operations: <kbd>&#92;xE1</kbd>.</li>
 *   <li>U-based hexadecimal escapes (a.k.a. <em>unicode escapes</em>) are supported both in escape
 *       and unescape operations: <kbd>&#92;u00E1</kbd>.</li>
 *   <li>Octal escapes are supported, though only in unescape operations: <kbd>&#92;071</kbd>. These are not supported
 *       in escape operations because octal escapes were deprecated in version 5 of the ECMAScript
 *       specification.</li>
 *   <li>Support for the whole Unicode character set: <kbd>&bsol;u0000</kbd> to <kbd>&bsol;u10FFFF</kbd>, including
 *       characters not representable by only one <kbd>char</kbd> in Java (<kbd>&gt;&bsol;uFFFF</kbd>).</li>
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
 *   <dt>SEC</dt>
 *     <dd>Single Escape Character:
 *               <kbd>&#92;0</kbd> (<kbd>U+0000</kbd>),
 *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
 *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
 *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
 *               <kbd>&#92;v</kbd> (<kbd>U+000B</kbd>),
 *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
 *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
 *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
 *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
 *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>) and
 *               <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>) (optional, only in <kbd>&lt;&#47;</kbd>).
 *     </dd>
 *   <dt>XHEXA escapes</dt>
 *     <dd>Also called <em>x-based hexadecimal escapes</em> or simply <em>hexadecimal escapes</em>:
 *         compact representation of unicode codepoints up to <kbd>U+00FF</kbd>, with <kbd>&#92;x</kbd>
 *         followed by exactly two hexadecimal figures: <kbd>&#92;xE1</kbd>. XHEXA is many times used
 *         instead of UHEXA (when possible) in order to obtain shorter escaped strings.</dd>
 *   <dt>UHEXA escapes</dt>
 *     <dd>Also called <em>u-based hexadecimal escapes</em> or simply <em>unicode escapes</em>:
 *         complete representation of unicode codepoints up to <kbd>U+FFFF</kbd>, with <kbd>&#92;u</kbd>
 *         followed by exactly four hexadecimal figures: <kbd>&#92;u00E1</kbd>. Unicode codepoints &gt;
 *         <kbd>U+FFFF</kbd> can be represented in JavaScript by mean of two UHEXA escapes (a
 *         <em>surrogate pair</em>).</dd>
 *   <dt>Octal escapes</dt>
 *     <dd>Octal representation of unicode codepoints up to <kbd>U+00FF</kbd>, with <kbd>&#92;</kbd>
 *         followed by up to three octal figures: <kbd>&#92;071</kbd>. Though up to three octal figures
 *         are allowed, octal numbers &gt; <kbd>377</kbd> (<kbd>0xFF</kbd>) are not supported. Note
 *         <u>octal escapes have been deprecated as of version 5 of the ECMAScript specification</u>.</dd>
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
 *   <li><a href="http://www.ecmascript.org/docs.php" target="_blank">The ECMAScript Specification</a>
 *       [ecmascript.org]</li>
 *   <li><a href="http://mathiasbynens.be/notes/javascript-escapes" target="_blank">JavaScript
 *       character escape sequences</a> [mathiasbynens.be]</li>
 * </ul>
 *
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
public final class JavaScriptEscape {


    /**
     * <p>
     *   Perform a JavaScript level 1 (only basic set) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the JavaScript basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <kbd>&#92;0</kbd> (<kbd>U+0000</kbd>),
     *       <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *       <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *       <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *       <kbd>&#92;v</kbd> (<kbd>U+000B</kbd>),
     *       <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *       <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *       <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>) and
     *       <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>).
     *       Note that <kbd>&#92;&#47;</kbd> is optional, and will only be used when the <kbd>&#47;</kbd>
     *       symbol appears after <kbd>&lt;</kbd>, as in <kbd>&lt;&#47;</kbd>. This is to avoid accidentally
     *       closing <kbd>&lt;script&gt;</kbd> tags in HTML. Also, note that <kbd>&#92;v</kbd>
     *       (<kbd>U+000B</kbd>) is actually included as a Single Escape
     *       Character in the JavaScript (ECMAScript) specification, but will not be used as it
     *       is not supported by Microsoft Internet Explorer versions < 9.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <kbd>U+0001</kbd> to <kbd>U+001F</kbd> and
     *       <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls {@link #escapeJavaScript(String, JavaScriptEscapeType, JavaScriptEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeType#SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static String escapeJavaScriptMinimal(final String text) {
        return escapeJavaScript(text,
                                JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                                JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a JavaScript level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The JavaScript basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <kbd>&#92;0</kbd> (<kbd>U+0000</kbd>),
     *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *               <kbd>&#92;v</kbd> (<kbd>U+000B</kbd>),
     *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>) and
     *               <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>).
     *               Note that <kbd>&#92;&#47;</kbd> is optional, and will only be used when the <kbd>&#47;</kbd>
     *               symbol appears after <kbd>&lt;</kbd>, as in <kbd>&lt;&#47;</kbd>. This is to avoid accidentally
     *               closing <kbd>&lt;script&gt;</kbd> tags in HTML. Also, note that <kbd>&#92;v</kbd>
     *               (<kbd>U+000B</kbd>) is actually included as a Single Escape
     *               Character in the JavaScript (ECMAScript) specification, but will not be used as it
     *               is not supported by Microsoft Internet Explorer versions < 9.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <kbd>U+0001</kbd> to <kbd>U+001F</kbd> and
     *               <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to using <kbd>&#92;xFF</kbd> Hexadecimal Escapes
     *   if possible (characters <= <kbd>U+00FF</kbd>), then default to <kbd>&#92;uFFFF</kbd>
     *   Hexadecimal Escapes. This type of escape <u>produces the smallest escaped string possible</u>.
     * </p>
     * <p>
     *   This method calls {@link #escapeJavaScript(String, JavaScriptEscapeType, JavaScriptEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeType#SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static String escapeJavaScript(final String text) {
        return escapeJavaScript(text,
                                JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                                JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) JavaScript <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.javascript.JavaScriptEscapeType} and
     *   {@link org.unbescape.javascript.JavaScriptEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <kbd>String</kbd>-based <kbd>escapeJavaScript*(...)</kbd> methods call this one with preconfigured
     *   <kbd>type</kbd> and <kbd>level</kbd> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param type the type of escape operation to be performed, see
     *             {@link org.unbescape.javascript.JavaScriptEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.javascript.JavaScriptEscapeLevel}.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeJavaScript(final String text,
                                          final JavaScriptEscapeType type, final JavaScriptEscapeLevel level) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return JavaScriptEscapeUtil.escape(text, type, level);

    }




    /**
     * <p>
     *   Perform a JavaScript level 1 (only basic set) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the JavaScript basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <kbd>&#92;0</kbd> (<kbd>U+0000</kbd>),
     *       <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *       <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *       <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *       <kbd>&#92;v</kbd> (<kbd>U+000B</kbd>),
     *       <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *       <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *       <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>) and
     *       <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>).
     *       Note that <kbd>&#92;&#47;</kbd> is optional, and will only be used when the <kbd>&#47;</kbd>
     *       symbol appears after <kbd>&lt;</kbd>, as in <kbd>&lt;&#47;</kbd>. This is to avoid accidentally
     *       closing <kbd>&lt;script&gt;</kbd> tags in HTML. Also, note that <kbd>&#92;v</kbd>
     *       (<kbd>U+000B</kbd>) is actually included as a Single Escape
     *       Character in the JavaScript (ECMAScript) specification, but will not be used as it
     *       is not supported by Microsoft Internet Explorer versions < 9.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <kbd>U+0001</kbd> to <kbd>U+001F</kbd> and
     *       <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls
     *   {@link #escapeJavaScript(char[], int, int, java.io.Writer, JavaScriptEscapeType, JavaScriptEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeType#SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeJavaScriptMinimal(final char[] text, final int offset, final int len, final Writer writer)
                                               throws IOException {
        escapeJavaScript(text, offset, len, writer,
                         JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                         JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a JavaScript level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The JavaScript basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <kbd>&#92;0</kbd> (<kbd>U+0000</kbd>),
     *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *               <kbd>&#92;v</kbd> (<kbd>U+000B</kbd>),
     *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
     *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>) and
     *               <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>).
     *               Note that <kbd>&#92;&#47;</kbd> is optional, and will only be used when the <kbd>&#47;</kbd>
     *               symbol appears after <kbd>&lt;</kbd>, as in <kbd>&lt;&#47;</kbd>. This is to avoid accidentally
     *               closing <kbd>&lt;script&gt;</kbd> tags in HTML. Also, note that <kbd>&#92;v</kbd>
     *               (<kbd>U+000B</kbd>) is actually included as a Single Escape
     *               Character in the JavaScript (ECMAScript) specification, but will not be used as it
     *               is not supported by Microsoft Internet Explorer versions < 9.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <kbd>U+0001</kbd> to <kbd>U+001F</kbd> and
     *               <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to using <kbd>&#92;xFF</kbd> Hexadecimal Escapes
     *   if possible (characters <= <kbd>U+00FF</kbd>), then default to <kbd>&#92;uFFFF</kbd>
     *   Hexadecimal Escapes. This type of escape <u>produces the smallest escaped string possible</u>.
     * </p>
     * <p>
     *   This method calls
     *   {@link #escapeJavaScript(char[], int, int, java.io.Writer, JavaScriptEscapeType, JavaScriptEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>type</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeType#SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA}</li>
     *   <li><kbd>level</kbd>:
     *       {@link org.unbescape.javascript.JavaScriptEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeJavaScript(final char[] text, final int offset, final int len, final Writer writer)
                                        throws IOException {
        escapeJavaScript(text, offset, len, writer,
                         JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                         JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) JavaScript <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.javascript.JavaScriptEscapeType} and
     *   {@link org.unbescape.javascript.JavaScriptEscapeLevel} argument values.
     * </p>
     * <p>
     *   All other <kbd>char[]</kbd>-based <kbd>escapeJavaScript*(...)</kbd> methods call this one with preconfigured
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
     *             {@link org.unbescape.javascript.JavaScriptEscapeType}.
     * @param level the escape level to be applied, see {@link org.unbescape.javascript.JavaScriptEscapeLevel}.
     */
    public static void escapeJavaScript(final char[] text, final int offset, final int len, final Writer writer,
                                        final JavaScriptEscapeType type, final JavaScriptEscapeLevel level)
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

        JavaScriptEscapeUtil.escape(text, offset, len, writer, type, level);

    }








    /**
     * <p>
     *   Perform a JavaScript <strong>unescape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> JavaScript unescape of SECs, x-based, u-based
     *   and octal escapes.
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
    public static String unescapeJavaScript(final String text) {
        return JavaScriptEscapeUtil.unescape(text);
    }


    /**
     * <p>
     *   Perform a JavaScript <strong>unescape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> JavaScript unescape of SECs, x-based, u-based
     *   and octal escapes.
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
    public static void unescapeJavaScript(final char[] text, final int offset, final int len, final Writer writer)
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

        JavaScriptEscapeUtil.unescape(text, offset, len, writer);

    }




    private JavaScriptEscape() {
        super();
    }


}

