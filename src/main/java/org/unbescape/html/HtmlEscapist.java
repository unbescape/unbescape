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
package org.unbescape.html;

import java.io.IOException;
import java.io.Writer;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class HtmlEscapist {

    /*
     * SEE: https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet
     * SEE: http://www.w3.org/TR/2011/WD-html5-20110113/named-character-references.html
     * SEE: http://dev.w3.org/html5/html-author/charref
     *
     * SEE, FOR UNESCAPING: http://www.w3.org/TR/html5/syntax.html#consume-a-character-reference
     * SEE, FOR REFERENCE: http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */




    public static String escapeHtml(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT_WITH_APOS);
    }


    public static String escapeHtml(final String text, final HtmlEscapeType type) {
        return escapeHtml(text, type, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT_WITH_APOS);
    }


    public static String escapeHtml(final String text, final HtmlEscapeLevel level) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, level);
    }


    public static String escapeHtml(final String text, final HtmlEscapeType type, final HtmlEscapeLevel level) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }

        return HtmlEscapeUtil.escape(text, type, level);

    }







    public static void escapeHtml(final char[] text, final Writer writer)
                                  throws IOException {
        escapeHtml(text, 0, text.length, writer,
                HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT_WITH_APOS);
    }


    public static void escapeHtml(final char[] text, final Writer writer, final HtmlEscapeType type)
                                  throws IOException {
        escapeHtml(text, 0, text.length, writer, type,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT_WITH_APOS);
    }


    public static void escapeHtml(final char[] text, final Writer writer, final HtmlEscapeLevel level)
                                  throws IOException {
        escapeHtml(text, 0, text.length, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, level);
    }


    public static void escapeHtml(final char[] text, final Writer writer,
                                  final HtmlEscapeType type, final HtmlEscapeLevel level)
                                  throws IOException {
        escapeHtml(text, 0, text.length, writer, type, level);
    }


    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer)
                                  throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT_WITH_APOS);
    }


    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer,
                                  final HtmlEscapeType type) throws IOException {
        escapeHtml(text, offset, len, writer, type,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT_WITH_APOS);
    }


    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer,
                                  final HtmlEscapeLevel level)
                                  throws IOException {
        escapeHtml(text, offset, len, writer,
                HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, level);
    }


    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer,
                                  final HtmlEscapeType type, final HtmlEscapeLevel level)
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

        if (offset < 0 || offset > text.length) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + text.length);
        }

        if (len < 0 || (offset + len) > text.length) {
            throw new IllegalArgumentException(
                    "Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + text.length);
        }

        HtmlEscapeUtil.escape(text, offset, len, writer, type, level);

    }






    public static String unescapeHtml(final String text) {
        return HtmlEscapeUtil.unescape(text);
    }



    public static void unescapeHtml(final char[] text, final Writer writer)
            throws IOException{
        HtmlEscapeUtil.unescape(text, 0, text.length, writer);
    }



    public static void unescapeHtml(final char[] text, final int offset, final int len, final Writer writer)
                                    throws IOException{
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }

        HtmlEscapeUtil.unescape(text, offset, len, writer);
    }




    private HtmlEscapist() {
        super();
    }



}

