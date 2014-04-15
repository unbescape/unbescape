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
 *
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0
 *
 */
public final class JavaScriptEscape {


    public static String escapeJavaScript(final String text) {
        return escapeJavaScript(text,
                                JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                                JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    public static String escapeJavaScriptAscii(final String text) {
        return escapeJavaScript(text,
                                JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                                JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


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




    public static void escapeJavaScript(final char[] text, final int offset, final int len, final Writer writer)
                                        throws IOException {
        escapeJavaScript(text, offset, len, writer,
                         JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                         JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }


    public static void escapeJavaScriptAscii(final char[] text, final int offset, final int len, final Writer writer)
                                             throws IOException {
        escapeJavaScript(text, offset, len, writer,
                         JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA,
                         JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }


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








    public static String unescapeJavaScript(final String text) {
        return JavaScriptEscapeUtil.unescape(text);
    }


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

        // The chosen symbols (1.0 or 1.1) don't really matter, as both contain the same CERs
        JavaScriptEscapeUtil.unescape(text, offset, len, writer);

    }




    private JavaScriptEscape() {
        super();
    }


}

