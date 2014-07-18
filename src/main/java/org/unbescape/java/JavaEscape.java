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
package org.unbescape.java;

import java.io.IOException;
import java.io.Writer;


/**
 * <p>
 *   Utility class for performing Java escape/unescape operations.
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
 *       needs of the scenario). Its values are defined by the {@link JavaEscapeLevel}
 *       enum.</li>
 * </ul>
 * <p>
 *   Unbescape does not define a <em>'type'</em> for Java escaping (just a <em>level</em>) because,
 *   given the way Unicode Escapes work in Java, there is no possibility to choose whether we want to escape, for
 *   example, a <em>tab character</em> (U+0009) as a Single Escape Char (<kbd>&#92;t</kbd>) or as a Unicode Escape
 *   (<kbd>&#92;u0009</kbd>). Given Unicode Escapes are processed by the compiler and not the runtime, using
 *   <kbd>&#92;u0009</kbd> instead of <kbd>&#92;t</kbd> would really insert a tab character inside our source
 *   code before compiling, which is not equivalent to inserting <kbd>"&#92;t"</kbd> in a <kbd>String</kbd> literal.
 * </p>
 * <p>
 *   <strong>Unescape</strong> operations need no configuration parameters. Unescape operations
 *   will always perform <em>complete</em> unescape of SECs (<kbd>&#92;n</kbd>),
 *   u-based (<kbd>&#92;u00E1</kbd>) and octal (<kbd>&#92;341</kbd>) escapes.
 * </p>
 *
 * <h4><u>Features</u></h4>
 *
 * <p>
 *   Specific features of the Java escape/unescape operations performed by means of this class:
 * </p>
 * <ul>
 *   <li>The Java basic escape set is supported. This <em>basic set</em> consists of:
 *         <ul>
 *           <li>The <em>Single Escape Characters</em>:
 *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
 *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
 *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
 *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
 *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
 *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
 *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>) and
 *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>). Note <kbd>&#92;&apos;</kbd> is not really needed in
 *               String literals (only in Character literals), so it won't be used until escape level 3.
 *           </li>
 *           <li>
 *               Two ranges of non-displayable, control characters (some of which are already part of the
 *               <em>single escape characters</em> list): <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
 *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
 *           </li>
 *         </ul>
 *   </li>
 *   <li>U-based hexadecimal escapes (a.k.a. <em>unicode escapes</em>) are supported both in escape
 *       and unescape operations: <kbd>&#92;u00E1</kbd>.</li>
 *   <li>Octal escapes are supported, though only in unescape operations: <kbd>&#92;071</kbd>. These are not supported
 *       in escape operations because the use of octal escapes is not recommended by the Java Language Specification
 *       (it's usage is allowed mainly for C compatibility reasons).</li>
 *   <li>Support for the whole Unicode character set: <kbd>&bsol;u0000</kbd> to <kbd>&bsol;u10FFFF</kbd>, including
 *       characters not representable by only one <kbd>char</kbd> in Java (<kbd>&gt;&bsol;uFFFF</kbd>).</li>
 * </ul>
 *
 * <h4><u>Specific features of Unicode Escapes in Java</u></h4>
 *
 * <p>
 *   The way Unicode Escapes work in Java is different to other languages like e.g. JavaScript. In Java,
 *   these UHEXA escapes are processed by the compiler itself, and therefore resolved before any other
 *   type of escapes. Besides, UHEXA escapes can appear anywhere in the code, not only String literals.
 *   This means that, while in JavaScript <kbd>'a&#92;u005Cna'</kbd> would be displayed as <kbd>a&#92;na</kbd>,
 *   in Java <kbd>"a&#92;u005Cna"</kbd> would in fact be displayed in two lines:
 *   <kbd>a</kbd>+<kbd>&lt;LF&gt;</kbd>+<kbd>a</kbd>.
 * </p>
 * <p>
 *   Going even further, this is perfectly valid Java code:
 * </p>
 * <code>
 *   final String hello = &#92;u0022Hello, World!&#92;u0022;
 * </code>
 * <p>
 *   Also, Java allows to write any number of <kbd>'u'</kbd> characters in this type of escapes, like
 *   <kbd>&#92;uu00E1</kbd> or even <kbd>&#92;uuuuuuuuu00E1</kbd>. This is so in order to enable legacy
 *   compatibility with older code-processing tools that didn't support Unicode processing at all, which
 *   would fail when finding an Unicode escape like <kbd>&#92;u00E1</kbd>, but not <kbd>&#92;uu00E1</kbd>
 *   (because they would consider <kbd>&#92;u</kbd> as the escape). So this is valid Java code too:
 * </p>
 * <code>
 *   final String hello = &#92;uuuuuuuu0022Hello, World!&#92;u0022;
 * </code>
 * <p>
 *   In order to correctly unescape Java UHEXA escapes like <kbd>"a&#92;u005Cna"</kbd>, Unbescape will
 *   perform a two-pass process so that all unicode escapes are processed in the first pass, and then
 *   the single escape characters and octal escapes in the second pass.
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
 *   <dt>SEC</dt>
 *     <dd>Single Escape Character:
 *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
 *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
 *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
 *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
 *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
 *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
 *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>) and
 *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>). Note <kbd>&#92;&apos;</kbd> is not really needed in
 *               String literals (only in Character literals), so it won't be used until escape level 3.
 *     </dd>
 *   <dt>UHEXA escapes</dt>
 *     <dd>Also called <em>u-based hexadecimal escapes</em> or simply <em>unicode escapes</em>:
 *         complete representation of unicode codepoints up to <kbd>U+FFFF</kbd>, with <kbd>&#92;u</kbd>
 *         followed by exactly four hexadecimal figures: <kbd>&#92;u00E1</kbd>. Unicode codepoints &gt;
 *         <kbd>U+FFFF</kbd> can be represented in Java by mean of two UHEXA escapes (a
 *         <em>surrogate pair</em>).</dd>
 *   <dt>Octal escapes</dt>
 *     <dd>Octal representation of unicode codepoints up to <kbd>U+00FF</kbd>, with <kbd>&#92;</kbd>
 *         followed by up to three octal figures: <kbd>&#92;071</kbd>. Though up to three octal figures
 *         are allowed, octal numbers &gt; <kbd>377</kbd> (<kbd>0xFF</kbd>) are not supported. These are not supported
 *         in escape operations because the use of octal escapes is not recommended by the Java Language Specification
 *         (it's usage is allowed mainly for C compatibility reasons).</dd>
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
 *   <li><a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html" target="_blank">The Java 7 Language
 *       Specification - Chapter 3: Lexical Structure.</a> [oracle.com]</li>
 *   <li><a href="http://arity23.blogspot.com/2013/04/secrets-of-scala-lexer-1-uuuuunicode.html"
 *       target="_blank">Secrets of the Scala Lexer 1: &#92;uuuuunicode</a> [blogspot.com]</li>
 *   <li><a href="http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html"
 *       target="_blank">Supplementary characters in the Java Platform</a> [oracle.com]</li>
 * </ul>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
public final class JavaEscape {


    /**
     * <p>
     *   Perform a Java level 1 (only basic set) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the Java basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *       <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *       <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *       <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *       <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>) and
     *       <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>). Note <kbd>&#92;&apos;</kbd> is not really needed in
     *       String literals (only in Character literals), so it won't be used until escape level 3.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *       and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls {@link #escapeJava(String, JavaEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>level</kbd>:
     *       {@link JavaEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static String escapeJavaMinimal(final String text) {
        return escapeJava(text, JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a Java level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The Java basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>) and
     *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>). Note <kbd>&#92;&apos;</kbd> is not really needed in
     *               String literals (only in Character literals), so it won't be used until escape level 3.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to <kbd>&#92;uFFFF</kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeJava(String, JavaEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>level</kbd>:
     *       {@link JavaEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static String escapeJava(final String text) {
        return escapeJava(text, JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) Java <strong>escape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.java.JavaEscapeLevel} argument value.
     * </p>
     * <p>
     *   All other <kbd>String</kbd>-based <kbd>escapeJava*(...)</kbd> methods call this one with preconfigured
     *   <kbd>level</kbd> values.
     * </p>
     * <p>
     *   This method is <strong>thread-safe</strong>.
     * </p>
     *
     * @param text the <kbd>String</kbd> to be escaped.
     * @param level the escape level to be applied, see {@link org.unbescape.java.JavaEscapeLevel}.
     * @return The escaped result <kbd>String</kbd>. As a memory-performance improvement, will return the exact
     *         same object as the <kbd>text</kbd> input argument if no escaping modifications were required (and
     *         no additional <kbd>String</kbd> objects will be created during processing). Will
     *         return <kbd>null</kbd> if <kbd>text</kbd> is <kbd>null</kbd>.
     */
    public static String escapeJava(final String text, final JavaEscapeLevel level) {

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return JavaEscapeUtil.escape(text, level);

    }




    /**
     * <p>
     *   Perform a Java level 1 (only basic set) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 1</em> means this method will only escape the Java basic escape set:
     * </p>
     * <ul>
     *   <li>The <em>Single Escape Characters</em>:
     *       <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *       <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *       <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *       <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *       <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *       <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *       <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>) and
     *       <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>). Note <kbd>&#92;&apos;</kbd> is not really needed in
     *       String literals (only in Character literals), so it won't be used until escape level 3.
     *   </li>
     *   <li>
     *       Two ranges of non-displayable, control characters (some of which are already part of the
     *       <em>single escape characters</em> list): <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *       and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *   </li>
     * </ul>
     * <p>
     *   This method calls {@link #escapeJava(char[], int, int, java.io.Writer, JavaEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>level</kbd>:
     *       {@link JavaEscapeLevel#LEVEL_1_BASIC_ESCAPE_SET}</li>
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
    public static void escapeJavaMinimal(final char[] text, final int offset, final int len, final Writer writer)
                                         throws IOException {
        escapeJava(text, offset, len, writer, JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a Java level 2 (basic set and all non-ASCII chars) <strong>escape</strong> operation
     *   on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   <em>Level 2</em> means this method will escape:
     * </p>
     * <ul>
     *   <li>The Java basic escape set:
     *         <ul>
     *           <li>The <em>Single Escape Characters</em>:
     *               <kbd>&#92;b</kbd> (<kbd>U+0008</kbd>),
     *               <kbd>&#92;t</kbd> (<kbd>U+0009</kbd>),
     *               <kbd>&#92;n</kbd> (<kbd>U+000A</kbd>),
     *               <kbd>&#92;f</kbd> (<kbd>U+000C</kbd>),
     *               <kbd>&#92;r</kbd> (<kbd>U+000D</kbd>),
     *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
     *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>) and
     *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>). Note <kbd>&#92;&apos;</kbd> is not really needed in
     *               String literals (only in Character literals), so it won't be used until escape level 3.
     *           </li>
     *           <li>
     *               Two ranges of non-displayable, control characters (some of which are already part of the
     *               <em>single escape characters</em> list): <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
     *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
     *           </li>
     *         </ul>
     *   </li>
     *   <li>All non ASCII characters.</li>
     * </ul>
     * <p>
     *   This escape will be performed by using the Single Escape Chars whenever possible. For escaped
     *   characters that do not have an associated SEC, default to <kbd>&#92;uFFFF</kbd>
     *   Hexadecimal Escapes.
     * </p>
     * <p>
     *   This method calls {@link #escapeJava(char[], int, int, java.io.Writer, JavaEscapeLevel)}
     *   with the following preconfigured values:
     * </p>
     * <ul>
     *   <li><kbd>level</kbd>:
     *       {@link JavaEscapeLevel#LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET}</li>
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
    public static void escapeJava(final char[] text, final int offset, final int len, final Writer writer)
                                  throws IOException {
        escapeJava(text, offset, len, writer, JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


    /**
     * <p>
     *   Perform a (configurable) Java <strong>escape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   This method will perform an escape operation according to the specified
     *   {@link org.unbescape.java.JavaEscapeLevel} argument value.
     * </p>
     * <p>
     *   All other <kbd>char[]</kbd>-based <kbd>escapeJava*(...)</kbd> methods call this one with preconfigured
     *   <kbd>level</kbd> values.
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
     * @param level the escape level to be applied, see {@link org.unbescape.java.JavaEscapeLevel}.
     */
    public static void escapeJava(final char[] text, final int offset, final int len, final Writer writer,
                                  final JavaEscapeLevel level)
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

        JavaEscapeUtil.escape(text, offset, len, writer, level);

    }








    /**
     * <p>
     *   Perform a Java <strong>unescape</strong> operation on a <kbd>String</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> Java unescape of SECs, u-based and octal escapes.
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
    public static String unescapeJava(final String text) {
        return JavaEscapeUtil.unescape(text);
    }


    /**
     * <p>
     *   Perform a Java <strong>unescape</strong> operation on a <kbd>char[]</kbd> input.
     * </p>
     * <p>
     *   No additional configuration arguments are required. Unescape operations
     *   will always perform <em>complete</em> Java unescape of SECs, u-based and octal escapes.
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
    public static void unescapeJava(final char[] text, final int offset, final int len, final Writer writer)
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

        JavaEscapeUtil.unescape(text, offset, len, writer);

    }




    private JavaEscape() {
        super();
    }


}

