/*
 * =============================================================================
 * 
 *   Copyright (c) 2014, The JAVAESCAPIST team (http://www.javaescapist.org)
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
package org.javaescapist;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class MarkupEscapist {


    static enum MarkupEscapeType {
        LITERAL_DEFAULT_TO_DECIMAL,
        LITERAL_DEFAULT_TO_HEXA,
        DECIMAL,
        HEXA
    }


    // This array will be used for determining which chars have a corresponding literal escape.
    // - Chars themselves will be used as indexes.
    // - short array values will be indexes to the LITERAL_ESCAPES array, thus avoiding this one to be too large
    //   (char is 2 bytes, whereas a String pointer is 4 (32bits) or 8 (64bits)
    private final short CHARS_WITH_LITERAL_ESCAPES_LEN;
    private final short[] CHARS_WITH_LITERAL_ESCAPES;

    // Contain the literal escapes themselves (Strings), as well as the chars in the same order.
    private final short LITERAL_ESCAPES_LEN;
    private final String[] LITERAL_ESCAPES;
    private final char[] CHARS_FOR_LITERAL_ESCAPES;


    private static final char[] DECIMAL_ESCAPE_PREFIX = "&#".toCharArray();
    private static final char[] HEXA_ESCAPE_PREFIX = "&#x".toCharArray();


    private short literalEscapesIndex;


    MarkupEscapist(final char maxEscapedChar, final short totalLiteralEscapes) {

        super();

        this.CHARS_WITH_LITERAL_ESCAPES_LEN = (short) (maxEscapedChar + 1);
        this.CHARS_WITH_LITERAL_ESCAPES = new short[this.CHARS_WITH_LITERAL_ESCAPES_LEN];

        this.LITERAL_ESCAPES_LEN = totalLiteralEscapes;
        this.LITERAL_ESCAPES = new String[this.LITERAL_ESCAPES_LEN];
        this.CHARS_FOR_LITERAL_ESCAPES = new char[this.LITERAL_ESCAPES_LEN];


        Arrays.fill(CHARS_WITH_LITERAL_ESCAPES, (short) -1);

        // Will be used for filling the LITERAL_ESCAPES array.
        this.literalEscapesIndex = 0;

    }


    void addLiteralEscape(final char c, final String literalEscape) {
        CHARS_WITH_LITERAL_ESCAPES[c] = this.literalEscapesIndex;
        LITERAL_ESCAPES[this.literalEscapesIndex] = literalEscape;
        CHARS_FOR_LITERAL_ESCAPES[this.literalEscapesIndex] = c;
        this.literalEscapesIndex++;
    }


    void initialize() {
        sortLiteralEscapes();
    }


    void sortLiteralEscapes() {

        final String[] literalEscapesOrig = LITERAL_ESCAPES.clone();
        final char[] charsForLiteralEscapesOrig = CHARS_FOR_LITERAL_ESCAPES.clone();

        Arrays.sort(LITERAL_ESCAPES);

        for (short i = 0; i < LITERAL_ESCAPES_LEN; i++) {
            final String literalEscape = LITERAL_ESCAPES[i];
            for (short j = 0; j  < LITERAL_ESCAPES_LEN; j++) {
                if (literalEscape.equals(literalEscapesOrig[j])) {
                    final char c = charsForLiteralEscapesOrig[j];
                    CHARS_FOR_LITERAL_ESCAPES[i] = c;
                    CHARS_WITH_LITERAL_ESCAPES[c] = i;
                    break;
                }
            }
        }

    }



    private static boolean arrayContains(final char[] array, final int arrayLen, final char c) {
        for (int i = 0; i < arrayLen; i++) {
            if (c == array[i]) {
                return true;
            }
        }
        return false;
    }


    String escape(final String text, final char[] nonEscapableChars, final char[] nonLiteralEscapableChars,
                  final MarkupEscapeType markupEscapeType) {

        if (markupEscapeType == null) {
            throw new IllegalArgumentException("Argument 'markupEscapeType' cannot be null");
        }

        if (text == null) {
            return null;
        }

        final int nonEscapableCharsLen = (nonEscapableChars == null? 0 : nonEscapableChars.length);
        final int nonLiteralEscapableCharsLen = (nonLiteralEscapableChars == null? 0 : nonLiteralEscapableChars.length);

        final boolean literal =
                (MarkupEscapeType.LITERAL_DEFAULT_TO_DECIMAL.equals(markupEscapeType) ||
                 MarkupEscapeType.LITERAL_DEFAULT_TO_HEXA.equals(markupEscapeType));
        final boolean hexa =
                (MarkupEscapeType.HEXA.equals(markupEscapeType) ||
                 MarkupEscapeType.LITERAL_DEFAULT_TO_HEXA.equals(markupEscapeType));

        StringBuilder strBuilder = null;
        int readOffset = 0;
        final int textLen = text.length();

        for (int i = 0; i < textLen; i++) {

            final char c = text.charAt(i);

            if (c <= 0x7f && CHARS_WITH_LITERAL_ESCAPES[c] == -1) {
                continue;
            }

            if (arrayContains(nonEscapableChars, nonEscapableCharsLen, c)) {
                continue;
            }

            if (strBuilder == null) {
                strBuilder = new StringBuilder(textLen + 15);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            if (!literal || c >= CHARS_WITH_LITERAL_ESCAPES_LEN || CHARS_WITH_LITERAL_ESCAPES[c] == -1) {
                // char should be escaped, but there is no literal for it, or maybe we just dont want literals

                if (hexa) {
                    strBuilder.append(HEXA_ESCAPE_PREFIX);
                    strBuilder.append(Integer.toHexString((int) c));
                } else {
                    strBuilder.append(DECIMAL_ESCAPE_PREFIX);
                    strBuilder.append((int) c);
                }
                strBuilder.append(';');

            } else {
                // char should be escaped AND there is literal for it

                if (arrayContains(nonLiteralEscapableChars, nonLiteralEscapableCharsLen, c)) {
                    // literal shouldn't be applied, defaulting
                    if (hexa) {
                        strBuilder.append(HEXA_ESCAPE_PREFIX);
                        strBuilder.append(Integer.toHexString((int) c));
                    } else {
                        strBuilder.append(DECIMAL_ESCAPE_PREFIX);
                        strBuilder.append((int) c);
                    }
                    strBuilder.append(';');
                } else {
                    // just apply the literal
                    strBuilder.append(LITERAL_ESCAPES[CHARS_WITH_LITERAL_ESCAPES[c]]);
                }

            }

            readOffset = i + 1;

        }

        if (strBuilder == null) {
            return text;
        }

        if (textLen - readOffset > 0) {
            strBuilder.append(text, readOffset, textLen);
        }

        return strBuilder.toString();

    }




    void escape(final char[] text, final int offset, final int len, final Writer writer,
                final char[] nonEscapableChars, final char[] nonLiteralEscapableChars,
                final MarkupEscapeType markupEscapeType)
                throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        if (markupEscapeType == null) {
            throw new IllegalArgumentException("Argument 'markupEscapeType' cannot be null");
        }

        if (offset < 0 || offset > text.length) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + text.length);
        }

        if (len < 0 || (offset + len) > text.length) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + text.length);
        }

        if (text == null || text.length == 0) {
            return;
        }

        final int nonEscapableCharsLen = (nonEscapableChars == null? 0 : nonEscapableChars.length);
        final int nonLiteralEscapableCharsLen = (nonLiteralEscapableChars == null? 0 : nonLiteralEscapableChars.length);

        final boolean literal =
                (MarkupEscapeType.LITERAL_DEFAULT_TO_DECIMAL.equals(markupEscapeType) ||
                        MarkupEscapeType.LITERAL_DEFAULT_TO_HEXA.equals(markupEscapeType));
        final boolean hexa =
                (MarkupEscapeType.HEXA.equals(markupEscapeType) ||
                        MarkupEscapeType.LITERAL_DEFAULT_TO_HEXA.equals(markupEscapeType));

        int readOffset = offset;
        final int maxLen = (offset + len);

        for (int i = offset; i < maxLen; i++) {

            final char c = text[i];

            if (c <= 0x7f && CHARS_WITH_LITERAL_ESCAPES[c] == -1) {
                continue;
            }

            if (arrayContains(nonEscapableChars, nonEscapableCharsLen, c)) {
                continue;
            }

            if (i - readOffset > 0) {
                writer.write(text, readOffset, (i - readOffset));
            }

            if (!literal || c >= CHARS_WITH_LITERAL_ESCAPES_LEN || CHARS_WITH_LITERAL_ESCAPES[c] == -1) {
                // char should be escaped, but there is no literal for it, or maybe we just dont want literals

                if (hexa) {
                    writer.write("&#x");
                    writer.write(Integer.toHexString((int) c));
                } else {
                    writer.write("&#");
                    writer.write(String.valueOf((int) c));
                }
                writer.write(';');

            } else {
                // char should be escaped AND there is literal for it

                if (arrayContains(nonLiteralEscapableChars, nonLiteralEscapableCharsLen, c)) {
                    // literal shouldn't be applied, defaulting
                    if (hexa) {
                        writer.write("&#x");
                        writer.write(Integer.toHexString((int) c));
                    } else {
                        writer.write("&#");
                        writer.write(String.valueOf((int) c));
                    }
                    writer.write(';');
                } else {
                    // just apply the literal
                    writer.write(LITERAL_ESCAPES[CHARS_WITH_LITERAL_ESCAPES[c]]);
                }

            }

            readOffset = i + 1;

        }

        if (maxLen - readOffset > 0) {
            writer.write(text, readOffset, (maxLen - readOffset));
        }

    }



}

