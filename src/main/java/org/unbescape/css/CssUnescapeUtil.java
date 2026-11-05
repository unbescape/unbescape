/*
 * =============================================================================
 * 
 *   Copyright (c) 2014-2026 Unbescape (https://www.unbescape.org)
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
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Writer;

/**
 * <p>
 *   Internal class in charge of performing the real unescape operations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
final class CssUnescapeUtil {



    /*
     * CSS STRING AND IDENTIFIER UNESCAPE OPERATIONS
     * ---------------------------------------------
     *
     *   See: http://www.w3.org/TR/CSS21/syndata.html#value-def-identifier
     *        http://mathiasbynens.be/notes/css-escapes
     *        http://mothereff.in/css-escapes
     *
     *   (Note that, in the following examples, and in order to avoid escape problems during the compilation
     *    of this class, the backslash symbol is replaced by '%')
     *
     *   - BACKSLASH ESCAPES:
     *        U+0020 -> %  (escape + whitespace)
     *        U+0021 -> %!
     *        U+0022 -> %"
     *        U+0023 -> %#
     *        U+0024 -> %$
     *        U+0025 -> %%
     *        U+0026 -> %&
     *        U+0027 -> %'
     *        U+0028 -> %(
     *        U+0029 -> %)
     *        U+002A -> %*
     *        U+002B -> %+
     *        U+002C -> %,
     *        U+002D -> %-  [ ONLY USED WHEN IDENTIFIER STARTS WITH -- OR -{DIGIT} ]
     *        U+002E -> %.
     *        U+002F -> %/
     *        U+003A -> %:  [ NOT USED FOR ESCAPING, NOT RECOGNIZED BY IE < 8 ]
     *        U+003B -> %;
     *        U+003C -> %<
     *        U+003D -> %=
     *        U+003E -> %>
     *        U+003F -> %?
     *        U+0040 -> %@
     *        U+005B -> %[
     *        U+005C -> %%
     *        U+005D -> %]
     *        U+005E -> %^
     *        U+005F -> %_  [ ONLY USED AT THE BEGINNING OF AN INDENTIFIER, TO AVOID PROBLEMS WITH IE6 ]
     *        U+0060 -> %`
     *        U+007B -> %{
     *        U+007C -> %|
     *        U+007D -> %}
     *        U+007E -> %~
     *
     *   - UNICODE ESCAPE [HEXA]
     *        Compact representation: %??* (variable-length. Optionally followed by a whitespace U+0020 - required
     *                                      if after escape comes a hexadecimal char (0-9a-f) or a whitespace U+0020)
     *        When unescaping, a single whitespace after the escape (space, tab, line feed, form feed, carriage
     *        return or CR LF) ends it and is ignored. Escapes for U+0000, for surrogates or for values above
     *        U+10FFFF are unescaped as U+FFFD (see https://www.w3.org/TR/css-syntax-3/#consume-escaped-code-point).
     *        6-digit representation: %?????? (fixed-length. Not required to be followed by whitespace, unless after
     *                                        escape comes a whitespace U+0020)
     *
     *        Characters > U+FFFF :
     *              - Standard:      %?????? or %??* (but not supported by older WebKit browsers)
     *              - Non-standard:  %u????%u???? (surrogate character pair, only in older WebKit browsers)
     *
     *   - ESCAPED LINE FEED (strings only): These are Line Continuators. According to the CSS specification (see
     *        https://www.w3.org/TR/CSS2/syndata.html#escaped-characters and
     *        https://www.w3.org/TR/CSS2/syndata.html#strings), a backslash followed by a newline (line feed,
     *        form feed, carriage return or CR LF) should be ignored in output (it is a way to span CSS literals over
     *        multiple lines).
     *
     */



    /*
     * Prefixes defined for use in escape and unescape operations
     */
    private static final char ESCAPE_PREFIX = '\\';




    private CssUnescapeUtil() {
        super();
    }






    /*
     * Computes the numeric value (0..15) of a hexadecimal-digit char (0-9, A-F, a-f), returning -1 for any
     * other char. Used by the parseIntFromReference methods below. This replaces a previous per-char linear
     * scan over the HEXA_CHARS_* arrays with a direct branch, while returning exactly the same values
     * (including -1 for any non-hexadecimal char).
     */
    private static int hexaValue(final char c) {
        if (c >= '0' && c <= '9') {
            return (int) c - (int) '0';
        }
        if (c >= 'A' && c <= 'F') {
            return (int) c - (int) 'A' + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return (int) c - (int) 'a' + 10;
        }
        return -1;
    }


    /*
     * As established by the CSS Syntax specification, a hexadecimal escape for U+0000, for a surrogate or for a
     * value out of the Unicode range is unescaped as U+FFFD (REPLACEMENT CHARACTER).
     */
    private static int replaceInvalidCodepoint(final int codepoint) {
        if (codepoint == 0x0 || (codepoint >= 0xD800 && codepoint <= 0xDFFF) || codepoint > Character.MAX_CODE_POINT) {
            return 0xFFFD;
        }
        return codepoint;
    }


    /*
     * Helpers for hexadecimal escapes (two versions each): the end (exclusive) of the up to six hexadecimal digits
     * starting at 'start', and the length of the whitespace that may follow them (CR LF counts as one).
     */
    private static int hexaEnd(final String text, final int start, final int max) {
        int f = start;
        while (f < (start + 6) && f < max && hexaValue(text.charAt(f)) >= 0) {
            f++;
        }
        return f;
    }

    private static int hexaEnd(final char[] text, final int start, final int max) {
        int f = start;
        while (f < (start + 6) && f < max && hexaValue(text[f]) >= 0) {
            f++;
        }
        return f;
    }

    private static int whitespaceLength(final String text, final int start, final int max) {
        if (start >= max) {
            return 0;
        }
        final char c = text.charAt(start);
        if (c == ' ' || c == '\t' || c == '\n' || c == '\f') {
            return 1;
        }
        if (c == '\r') {
            return ((start + 1) < max && text.charAt(start + 1) == '\n') ? 2 : 1;
        }
        return 0;
    }

    private static int whitespaceLength(final char[] text, final int start, final int max) {
        if (start >= max) {
            return 0;
        }
        final char c = text[start];
        if (c == ' ' || c == '\t' || c == '\n' || c == '\f') {
            return 1;
        }
        if (c == '\r') {
            return ((start + 1) < max && text[start + 1] == '\n') ? 2 : 1;
        }
        return 0;
    }


    /*
     * These methods (the two versions) are used instead of Integer.parseInt(str,radix) in order to avoid the need
     * to create substrings of the text being unescaped to feed such method.
     * -  No need to check all chars are within the radix limits - reference parsing code will already have done so.
     */

    static int parseIntFromReference(final String text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text.charAt(i);
            result = (radix * result) + hexaValue(c);
        }
        return result;
    }

    static int parseIntFromReference(final char[] text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text[i];
            result = (radix * result) + hexaValue(c);
        }
        return result;
    }





    /*
     * Perform an unescape operation based on String.
     */
    static String unescape(final String text) {

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != ESCAPE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = -1;

            if (c == ESCAPE_PREFIX) {

                final char c1 = text.charAt(i + 1);

                switch (c1) {
                        // newlines (line feed, form feed, carriage return). When escaped, these are line continuators
                    case '\n':
                    case '\f': codepoint = -2; referenceOffset = i + 1; break;
                        // carriage return, which may be followed by a line feed (CR LF is a single newline)
                    case '\r':
                        codepoint = -2;
                        referenceOffset = ((i + 2) < max && text.charAt(i + 2) == '\n') ? (i + 2) : (i + 1);
                        break;
                    case ' ':
                    case '!':
                    case '"':
                    case '#':
                    case '$':
                    case '%':
                    case '&':
                    case '\'':
                    case '(':
                    case ')':
                    case '*':
                    case '+':
                    case ',':
                        // hyphen: will only be escaped when identifer starts with '--' or '-{digit}'
                    case '-':
                    case '.':
                    case '/':
                        // colon: will not be used for escaping: not recognized by IE < 8
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                        // underscore: will only be escaped at the beginning of an identifier (in order to avoid issues in IE6)
                    case '_':
                    case '`':
                    case '{':
                    case '|':
                    case '}':
                    case '~': codepoint = (int)c1; referenceOffset = i + 1; break;
                }

                if (codepoint == -1) {

                    if ((c1 >= '0' && c1 <= '9') || (c1 >= 'A' && c1 <= 'F') || (c1 >= 'a' && c1 <= 'f')) {
                        // This is a hexa escape

                        final int f = hexaEnd(text, i + 1, max);
                        codepoint = parseIntFromReference(text, i + 1, f, 16);

                        // Fast-forward to the last char of the escape. If there is a whitespace after it (CR LF
                        // counting as one), it is part of the escape and will be ignored.
                        referenceOffset = f - 1 + whitespaceLength(text, f, max);

                        if (codepoint >= 0xD800 && codepoint <= 0xDBFF) {
                            // Non-standard (older WebKit) syntax for chars > U+FFFF: a high surrogate escape
                            // directly followed by a low surrogate escape. Combine them into a single char.
                            final int s = referenceOffset + 1;
                            if ((s + 1) < max && text.charAt(s) == ESCAPE_PREFIX && hexaValue(text.charAt(s + 1)) >= 0) {
                                final int f2 = hexaEnd(text, s + 1, max);
                                final int low = parseIntFromReference(text, s + 1, f2, 16);
                                if (low >= 0xDC00 && low <= 0xDFFF) {
                                    codepoint = Character.toCodePoint((char) codepoint, (char) low);
                                    referenceOffset = f2 - 1 + whitespaceLength(text, f2, max);
                                }
                            }
                        }

                        codepoint = replaceInvalidCodepoint(codepoint);

                        // Don't continue here, just let the unescape code below do its job


                    } else {

                        // We weren't able to consume any valid escape chars, just consider it a normal char,
                        // which is allowed by the CSS escape syntax.

                        codepoint = (int) c1;
                        referenceOffset = i + 1;

                    }

                }

            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * can increase the offset and initialize the string builder if needed, along with
             * copying to it all the contents pending up to this point.
             */

            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            i = referenceOffset;
            readOffset = i + 1;

            /*
             * --------------------------
             *
             * Perform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                strBuilder.append(Character.toChars(codepoint));
            } else if (codepoint != -2){ // We use -2 to signal the line continuator, which should be ignored in output
                strBuilder.append((char)codepoint);
            }

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no unescape was actually needed. Otherwise
         *                 append the remaining escaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        return strBuilder.toString();

    }






    /*
     * Perform an unescape operation based on a Reader, writing the results to a Writer.
     *
     * Note this reader is going to be read char-by-char, so some kind of buffering might be appropriate if this
     * is an inconvenience for the specific Reader implementation.
     */
    static void unescape(final Reader originalReader, final Writer writer) throws IOException {

        if (originalReader == null) {
            return;
        }

        Reader reader = originalReader;
        PushbackReader pushbackReader = null;

        int escapei = 0;
        final char[] escapes = new char[6];
        final char[] lowEscapes = new char[6];
        int c1, c2, ce; // c1: current char, c2: next char, ce: current escape char

        c2 = reader.read();

        while (c2 >= 0) {

            c1 = c2;
            c2 = reader.read();

            /*
             * Check the need for an unescape operation at this point
             */

            if (c1 != ESCAPE_PREFIX || c2 < 0) {
                writer.write(c1);
                continue;
            }

            int codepoint = -1;

            if (c1 == ESCAPE_PREFIX) {

                switch (c2) {
                        // newlines (line feed, form feed, carriage return). When escaped, these are line continuators
                    case '\n':
                    case '\f': codepoint = -2; c1 = c2; c2 = reader.read(); break;
                        // carriage return, which may be followed by a line feed (CR LF is a single newline)
                    case '\r':
                        codepoint = -2;
                        c1 = c2;
                        c2 = reader.read();
                        if (c2 == '\n') {
                            c1 = c2;
                            c2 = reader.read();
                        }
                        break;
                    case ' ':
                    case '!':
                    case '"':
                    case '#':
                    case '$':
                    case '%':
                    case '&':
                    case '\'':
                    case '(':
                    case ')':
                    case '*':
                    case '+':
                    case ',':
                        // hyphen: will only be escaped when identifer starts with '--' or '-{digit}'
                    case '-':
                    case '.':
                    case '/':
                        // colon: will not be used for escaping: not recognized by IE < 8
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                        // underscore: will only be escaped at the beginning of an identifier (in order to avoid issues in IE6)
                    case '_':
                    case '`':
                    case '{':
                    case '|':
                    case '}':
                    case '~': codepoint = c2; c1 = c2; c2 = reader.read(); break;
                }

                if (codepoint == -1) {

                    if ((c2 >= '0' && c2 <= '9') || (c2 >= 'A' && c2 <= 'F') || (c2 >= 'a' && c2 <= 'f')) {
                        // This is a hexa escape

                        escapei = 0;
                        ce = c2;
                        while (ce >= 0 && escapei < 6) {
                            if (!((ce >= '0' && ce <= '9') || (ce >= 'A' && ce <= 'F') || (ce >= 'a' && ce <= 'f'))) {
                                break;
                            }
                            escapes[escapei] = (char) ce;
                            ce = reader.read();
                            escapei++;
                        }

                        c1 = escapes[5];
                        c2 = ce;

                        codepoint = parseIntFromReference(escapes, 0, escapei, 16);

                        // If there is a whitespace after the escape, just ignore it (CR LF counts as one).
                        if (c2 == ' ' || c2 == '\t' || c2 == '\n' || c2 == '\f') {
                            c1 = c2;
                            c2 = reader.read();
                        } else if (c2 == '\r') {
                            c1 = c2;
                            c2 = reader.read();
                            if (c2 == '\n') {
                                c1 = c2;
                                c2 = reader.read();
                            }
                        }

                        if (codepoint >= 0xD800 && codepoint <= 0xDBFF && c2 == ESCAPE_PREFIX) {

                            // Non-standard (older WebKit) syntax for chars > U+FFFF: a high surrogate escape
                            // directly followed by a low surrogate escape. Combine them into a single char.
                            // If what follows is not a low surrogate escape, it will be pushed back to be
                            // processed normally (pushback is only needed here, so it is only set up here).

                            if (pushbackReader == null) {
                                pushbackReader = new PushbackReader(reader, 8);
                                reader = pushbackReader;
                            }

                            int lowi = 0;
                            ce = reader.read();
                            while (ce >= 0 && lowi < 6 && hexaValue((char) ce) >= 0) {
                                lowEscapes[lowi] = (char) ce;
                                ce = reader.read();
                                lowi++;
                            }

                            final int low = parseIntFromReference(lowEscapes, 0, lowi, 16);

                            if (lowi > 0 && low >= 0xDC00 && low <= 0xDFFF) {

                                codepoint = Character.toCodePoint((char) codepoint, (char) low);

                                c1 = lowEscapes[lowi - 1];
                                c2 = ce;

                            // If there is a whitespace after the escape, just ignore it (CR LF counts as one).
                            if (c2 == ' ' || c2 == '\t' || c2 == '\n' || c2 == '\f') {
                                c1 = c2;
                                c2 = reader.read();
                            } else if (c2 == '\r') {
                                c1 = c2;
                                c2 = reader.read();
                                if (c2 == '\n') {
                                    c1 = c2;
                                    c2 = reader.read();
                                }
                            }

                            } else {

                                if (ce >= 0) {
                                    pushbackReader.unread(ce);
                                }
                                pushbackReader.unread(lowEscapes, 0, lowi);

                            }

                        }

                        codepoint = replaceInvalidCodepoint(codepoint);


                        // Don't continue here, just let the unescape code below do its job


                    } else {

                        // We weren't able to consume any valid escape chars, just consider it a normal char,
                        // which is allowed by the CSS escape syntax.

                        codepoint = c2;

                        c1 = c2;
                        c2 = reader.read();

                    }

                }

            }

            /*
             * --------------------------
             *
             * Perform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else if (codepoint != -2){ // We use -2 to signal the line continuator, which should be ignored in output
                writer.write((char)codepoint);
            }

        }

    }






    /*
     * Perform an unescape operation based on char[].
     */
    static void unescape(final char[] text, final int offset, final int len, final Writer writer)
                         throws IOException {

        if (text == null) {
            return;
        }

        final int max = (offset + len);

        int readOffset = offset;
        int referenceOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != ESCAPE_PREFIX || (i + 1) >= max) {
                continue;
            }

            int codepoint = -1;

            if (c == ESCAPE_PREFIX) {

                final char c1 = text[i + 1];

                switch (c1) {
                        // newlines (line feed, form feed, carriage return). When escaped, these are line continuators
                    case '\n':
                    case '\f': codepoint = -2; referenceOffset = i + 1; break;
                        // carriage return, which may be followed by a line feed (CR LF is a single newline)
                    case '\r':
                        codepoint = -2;
                        referenceOffset = ((i + 2) < max && text[i + 2] == '\n') ? (i + 2) : (i + 1);
                        break;
                    case ' ':
                    case '!':
                    case '"':
                    case '#':
                    case '$':
                    case '%':
                    case '&':
                    case '\'':
                    case '(':
                    case ')':
                    case '*':
                    case '+':
                    case ',':
                    // hyphen: will only be escaped when identifer starts with '--' or '-{digit}'
                    case '-':
                    case '.':
                    case '/':
                    // colon: will not be used for escaping: not recognized by IE < 8
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                    // underscore: will only be escaped at the beginning of an identifier (in order to avoid issues in IE6)
                    case '_':
                    case '`':
                    case '{':
                    case '|':
                    case '}':
                    case '~': codepoint = (int)c1; referenceOffset = i + 1; break;
                }

                if (codepoint == -1) {

                    if ((c1 >= '0' && c1 <= '9') || (c1 >= 'A' && c1 <= 'F') || (c1 >= 'a' && c1 <= 'f')) {
                        // This is a hexa escape

                        final int f = hexaEnd(text, i + 1, max);
                        codepoint = parseIntFromReference(text, i + 1, f, 16);

                        // Fast-forward to the last char of the escape. If there is a whitespace after it (CR LF
                        // counting as one), it is part of the escape and will be ignored.
                        referenceOffset = f - 1 + whitespaceLength(text, f, max);

                        if (codepoint >= 0xD800 && codepoint <= 0xDBFF) {
                            // Non-standard (older WebKit) syntax for chars > U+FFFF: a high surrogate escape
                            // directly followed by a low surrogate escape. Combine them into a single char.
                            final int s = referenceOffset + 1;
                            if ((s + 1) < max && text[s] == ESCAPE_PREFIX && hexaValue(text[s + 1]) >= 0) {
                                final int f2 = hexaEnd(text, s + 1, max);
                                final int low = parseIntFromReference(text, s + 1, f2, 16);
                                if (low >= 0xDC00 && low <= 0xDFFF) {
                                    codepoint = Character.toCodePoint((char) codepoint, (char) low);
                                    referenceOffset = f2 - 1 + whitespaceLength(text, f2, max);
                                }
                            }
                        }

                        codepoint = replaceInvalidCodepoint(codepoint);

                        // Don't continue here, just let the unescape code below do its job


                    } else {

                        // We weren't able to consume any valid escape chars, just consider it a normal char,
                        // which is allowed by the CSS escape syntax.

                        codepoint = (int) c1;
                        referenceOffset = i + 1;

                    }

                }

            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * can write all the contents pending up to this point.
             */

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            i = referenceOffset;
            readOffset = i + 1;

            /*
             * --------------------------
             *
             * Perform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else if (codepoint != -2){ // We use -2 to signal the line continuator, which should be ignored in output
                writer.write((char) codepoint);
            }

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: append the remaining escaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

    }



}

