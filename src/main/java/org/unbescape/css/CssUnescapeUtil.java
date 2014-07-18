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
     *        6-digit representation: %?????? (fixed-length. Not required to be followed by whitespace, unless after
     *                                        escape comes a whitespace U+0020)
     *
     *        Characters > U+FFFF :
     *              - Standard:      %?????? or %??* (but not supported by older WebKit browsers)
     *              - Non-standard:  %u????%u???? (surrogate character pair, only in older WebKit browsers)
     *
     *
     */



    /*
     * Prefixes defined for use in escape and unescape operations
     */
    private static final char ESCAPE_PREFIX = '\\';

    /*
     * Small utility char arrays for hexadecimal conversion.
     */
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();




    private CssUnescapeUtil() {
        super();
    }






    /*
     * This methods (the two versions) are used instead of Integer.parseInt(str,radix) in order to avoid the need
     * to create substrings of the text being unescaped to feed such method.
     * -  No need to check all chars are within the radix limits - reference parsing code will already have done so.
     */

    static int parseIntFromReference(final String text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text.charAt(i);
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
        }
        return result;
    }

    static int parseIntFromReference(final char[] text, final int start, final int end, final int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            final char c = text[i];
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
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

                        int f = i + 2;
                        while (f < (i + 7) && f < max) {
                            final char cf = text.charAt(f);
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        codepoint = parseIntFromReference(text, i + 1, f, 16);

                        // Fast-forward to the first char after the parsed codepoint
                        referenceOffset = f - 1;

                        // If there is a whitespace after the escape, just ignore it.
                        if (f < max && text.charAt(f) == ' ') {
                            referenceOffset++;
                        }

                        // Don't continue here, just let the unescape code below do its job


                    } else if (c1 == '\n' || c1 == '\r' || c1 == '\f') {

                        // The only characters that cannot be escaped by means of a backslash are line feed,
                        // carriage return and form feed (besides hexadecimal digits).
                        i++;
                        continue;

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
             * Peform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                strBuilder.append(Character.toChars(codepoint));
            } else {
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

                        int f = i + 2;
                        while (f < (i + 7) && f < max) {
                            final char cf = text[f];
                            if (!((cf >= '0' && cf <= '9') || (cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f'))) {
                                break;
                            }
                            f++;
                        }

                        codepoint = parseIntFromReference(text, i + 1, f, 16);

                        // Fast-forward to the first char after the parsed codepoint
                        referenceOffset = f - 1;

                        // If there is a whitespace after the escape, just ignore it.
                        if (f < max && text[f] == ' ') {
                            referenceOffset++;
                        }

                        // Don't continue here, just let the unescape code below do its job


                    } else if (c1 == '\n' || c1 == '\r' || c1 == '\f') {

                        // The only characters that cannot be escaped by means of a backslash are line feed,
                        // carriage return and form feed (besides hexadecimal digits).
                        i++;
                        continue;

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
             * Peform the real unescape
             *
             * --------------------------
             */

            if (codepoint > '\uFFFF') {
                writer.write(Character.toChars(codepoint));
            } else {
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

