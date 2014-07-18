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
package org.unbescape.csv;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 *   Internal class in charge of performing the real escape/unescape operations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
final class CsvEscapeUtil {



    /*
     * CSV ESCAPE/UNESCAPE OPERATIONS
     * ------------------------------
     *
     *   See: http://tools.ietf.org/html/rfc4180 (de-facto standard)
     *        http://en.wikipedia.org/wiki/Comma-separated_values
     *        http://creativyst.com/Doc/Articles/CSV/CSV01.htm
     *
     *   ---------------------------------------------------------------------------------------------------------------
     *   NOTE: in order for Microsoft Excel to correcly open a CSV file, including field values with line breaks,
     *         you should follow these rules when creating them, besides escaping fields:
     *
     *        - Separate fields with semi-colon (';'), records with Windows-style line breaks ('\r\n', U+000D + U+000A).
     *        - Enclose field values in double-quotes ('"') if they contain any non-alphanumeric characters.
     *        - Don't leave any whitespace between the field separator (';') and the enclosing quotes ('"').
     *        - Escape double-quotes ('"') inside field values that are enclosed in double-quotes with two
     *          double-quotes ('""').
     *        - Use '\n' (U+000A, unix-style line breaks) for line breaks inside field values, even if records
     *          are separated with Windows-style line breaks ('\r\n') [ EXCEL 2003 compatibility ].
     *        - Open your CSV file in Excel with File -> Open..., not with Data -> Import... The latter option will
     *          not correctly understand line breaks inside field values (up to Excel 2010).
     *
     *        (Note unbescape will perform escaping of field values only, so it will take care of enclosing in
     *        double-quotes, using unix-style line breaks inside values, etc. But separating fields (e.g. with ';'),
     *        delimiting records (e.g. with '\r\n') and using the correct character encoding when writing CSV files
     *        will be the responsibility of the application calling unbescape.)
     *   ---------------------------------------------------------------------------------------------------------------
     *   NOTE: The described format for Excel is also supported by OpenOffice.org Calc (File -> Open...) and also
     *         Google Spreadsheets (File -> Import...)
     *   ---------------------------------------------------------------------------------------------------------------
     *
     */



    private static final char DOUBLE_QUOTE = '"';
    private static final char[] TWO_DOUBLE_QUOTES = "\"\"".toCharArray();



    private CsvEscapeUtil() {
        super();
    }





    /*
     * Perform an escape operation, based on String.
     */
    static String escape(final String text) {

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        final int offset = 0;
        final int max = text.length();

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);


            /*
             * Shortcut: most characters will be Alphanumeric, and we won't need to do anything at
             * all for them.
             */
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                continue;
            }


            /*
             * At this point we know for sure we will need some kind of escape, so we
             * initialize the string builder.
             */
            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 20);
                // If we need this, it's because we have non-alphanumeric chars. And that means
                // we should enclose in double-quotes.
                strBuilder.append(DOUBLE_QUOTE);
            }

            /*
             * Now we copy all the contents pending up to this point.
             */
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            readOffset = i + 1;

            /*
             * Check whether the character is a double-quote (in which case, we escape it)
             */
            if (c == DOUBLE_QUOTE) {
                strBuilder.append(TWO_DOUBLE_QUOTES);
                continue;
            }

            strBuilder.append(c);

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no escape was actually needed. Otherwise
         *                 append the remaining unescaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        // If we reached here, it's because we had non-alphanumeric chars. And that means
        // we should enclose in double-quotes.
        strBuilder.append(DOUBLE_QUOTE);

        return strBuilder.toString();

    }





    /*
     * Perform an escape operation, based on char[], according to the specified level and type.
     */
    static void escape(final char[] text, final int offset, final int len, final Writer writer)
                       throws IOException {

        if (text == null || text.length == 0) {
            return;
        }

        final int max = (offset + len);

        int readOffset = offset;

        for (int i = offset; i < max; i++) {

            final char c = text[i];


            /*
             * Shortcut: most characters will be Alphanumeric, and we won't need to do anything at
             * all for them.
             */
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                continue;
            }


            /*
             * At this point we know for sure we will need some kind of escape, so we
             * initialize the string builder.
             */
            if (readOffset == offset) {
                // If we need this, it's because we have non-alphanumeric chars. And that means
                // we should enclose in double-quotes.
                writer.write(DOUBLE_QUOTE);
            }

            /*
             * Now we copy all the contents pending up to this point.
             */
            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            readOffset = i + 1;

            /*
             * Check whether the character is a double-quote (in which case, we escape it)
             */
            if (c == DOUBLE_QUOTE) {
                writer.write(TWO_DOUBLE_QUOTES);
                continue;
            }

            writer.write(c);

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: append the remaining unescaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (max - readOffset > 0) {
            writer.write(text, readOffset, (max - readOffset));
        }

        if (readOffset > offset) {
            // If we reached here, it's because we had non-alphanumeric chars. And that means
            // we should enclose in double-quotes.
            writer.write(DOUBLE_QUOTE);
        }

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

        boolean isQuoted = false;

        for (int i = offset; i < max; i++) {

            final char c = text.charAt(i);

            /*
             * Shortcut: from an unescape point of view, we will ignore most characters
             */
            if (i > offset && c != DOUBLE_QUOTE) {
                continue;
            }


            /*
             * Check the only character that is really involved in unescape operations: the double-quote
             */
            if (c == DOUBLE_QUOTE) {

                if (i == offset) {

                    // If the first char is a double-quote, and so is the final one, we will need
                    // to remove them both.
                    if (i + 1 >= max) {
                        // Shortcut: The double-quote is the only char, just don't do anything
                        continue;
                    }

                    if (text.charAt(max - 1) == DOUBLE_QUOTE) {
                        // Confirmed: the value is enclosed in double-quotes. We should remove them.
                        isQuoted = true;
                        // Skip these double quotes in the final result;
                        referenceOffset = i + 1;
                        readOffset = i + 1;
                        continue;
                    }

                    // if none of the above are true, just consider the double-quotes a normal char
                    continue;

                } else {

                    if (isQuoted && i + 2 < max) {
                        // Value is quoted, and we are in the middle of it

                        final char c1 = text.charAt(i + 1);
                        if (c1 == DOUBLE_QUOTE) {
                            // This is an escaped double-quote: skip one of the chars (= unescape)
                            referenceOffset = i + 1;
                        } // else just write the quotes anyway (lenient behaviour). Not the last char, so don't remove.

                    } else if (isQuoted && i + 1 >= max) {

                        // This is the closing-double-quote, skip
                        referenceOffset = i + 1;

                    } else {
                        // else not quoted. Write the quotes anyway (lenient behaviour).
                        continue;
                    }

                }

            } else {

                // If the character is not a double-quote, we don't need to do anything at all
                continue;

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
             * Write the character
             * --------------------------
             */

            if (referenceOffset < max) {
                strBuilder.append(c);
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

        boolean isQuoted = false;

        for (int i = offset; i < max; i++) {

            final char c = text[i];

            /*
             * Shortcut: from an unescape point of view, we will ignore most characters
             */
            if (i > offset && c != DOUBLE_QUOTE) {
                continue;
            }


            /*
             * Check the only character that is really involved in unescape operations: the double-quote
             */
            if (c == DOUBLE_QUOTE) {

                if (i == offset) {

                    // If the first char is a double-quote, and so is the final one, we will need
                    // to remove them both.
                    if (i + 1 >= max) {
                        // Shortcut: The double-quote is the only char, just don't do anything
                        continue;
                    }

                    if (text[max - 1] == DOUBLE_QUOTE) {
                        // Confirmed: the value is enclosed in double-quotes. We should remove them.
                        isQuoted = true;
                        // Skip these double quotes in the final result;
                        referenceOffset = i + 1;
                        readOffset = i + 1;
                        continue;
                    }

                    // if none of the above are true, just consider the double-quotes a normal char
                    continue;

                } else {

                    if (isQuoted && i + 2 < max) {
                        // Value is quoted, and we are in the middle of it

                        final char c1 = text[i + 1];
                        if (c1 == DOUBLE_QUOTE) {
                            // This is an escaped double-quote: skip one of the chars (= unescape)
                            referenceOffset = i + 1;
                        } // else just write the quotes anyway (lenient behaviour). Not the last char, so don't remove.

                    } else if (isQuoted && i + 1 >= max) {

                        // This is the closing-double-quote, skip
                        referenceOffset = i + 1;

                    } else {
                        // else not quoted. Write the quotes anyway (lenient behaviour).
                        continue;
                    }

                }

            } else {

                // If the character is not a double-quote, we don't need to do anything at all
                continue;

            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * can copy all the contents pending up to this point.
             */

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            i = referenceOffset;
            readOffset = i + 1;

            /*
             * --------------------------
             * Write the character
             * --------------------------
             */

            if (referenceOffset < max) {
                writer.write(c);
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

