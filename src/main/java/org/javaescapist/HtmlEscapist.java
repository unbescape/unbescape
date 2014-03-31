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


    public static enum HtmlEscapeContext {

        ATTRIBUTE_OR_TEXT(1),
        SINGLE_QUOTED_ATTRIBUTE(1);


        private final int escapeLevel;

        HtmlEscapeContext(final int escapeLevel) {
            this.escapeLevel = escapeLevel;
        }

        public int getEscapeLevel() {
            return this.escapeLevel;
        }

    }


    public static enum HtmlEscapeType {

        HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(MarkupEscapist.MarkupEscapeType.NAMED_REFERENCES_DEFAULT_TO_DECIMAL),
        HTML4_NAMED_REFERENCES_DEFAULT_TO_HEXA(MarkupEscapist.MarkupEscapeType.NAMED_REFERENCES_DEFAULT_TO_HEXA),
        HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(MarkupEscapist.MarkupEscapeType.NAMED_REFERENCES_DEFAULT_TO_DECIMAL),
        HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA(MarkupEscapist.MarkupEscapeType.NAMED_REFERENCES_DEFAULT_TO_HEXA),
        DECIMAL_REFERENCES(MarkupEscapist.MarkupEscapeType.DECIMAL_REFERENCES),
        HEXADECIMAL_REFERENCES(MarkupEscapist.MarkupEscapeType.HEXADECIMAL_REFERENCES);


        private final MarkupEscapist.MarkupEscapeType markupEscapeType;

        HtmlEscapeType(final MarkupEscapist.MarkupEscapeType markupEscapeType) {
            this.markupEscapeType = markupEscapeType;
        }

        public MarkupEscapist.MarkupEscapeType getMarkupEscapeType() {
            return this.markupEscapeType;
        }

    }





    public static String escapeHtml(final String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
    }


    public static String escapeHtml(final String text, final HtmlEscapeType type) {
        return escapeHtml(text, type, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
    }


    public static String escapeHtml(final String text, final HtmlEscapeType type, final HtmlEscapeContext context) {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (context == null) {
            throw new IllegalArgumentException("The 'context' argument cannot be null");
        }

        final boolean html5 =
            (type.equals(HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL) ||
                 type.equals(HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA));

        final EscapeSymbols symbols = (html5? EscapeSymbols.HTML5_SYMBOLS : EscapeSymbols.HTML4_SYMBOLS);

        return MarkupEscapist.escape(
                symbols, text, context.getEscapeLevel(), type.getMarkupEscapeType());

    }





    public static void escapeHtml(final char[] text, final Writer writer) throws IOException {
        escapeHtml(text, 0, text.length, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
    }


    public static void escapeHtml(final char[] text, final Writer writer, final HtmlEscapeType type)
                                  throws IOException {
        escapeHtml(text, 0, text.length, writer, type, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
    }


    public static void escapeHtml(final char[] text, final Writer writer, final HtmlEscapeType type,
                                  final HtmlEscapeContext context) throws IOException {
        escapeHtml(text, 0, text.length, writer, type, context);
    }


    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer)
                                  throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
    }


    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer,
                                  final HtmlEscapeType type) throws IOException {
        escapeHtml(text, offset, len, writer, type, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
    }


    public static void escapeHtml(final char[] text, final int offset, final int len, final Writer writer,
                                  final HtmlEscapeType type, final HtmlEscapeContext context) throws IOException {

        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }

        if (context == null) {
            throw new IllegalArgumentException("The 'context' argument cannot be null");
        }

        MarkupEscapist.escape(
                EscapeSymbols.HTML4_SYMBOLS, text, offset, len, writer,
                context.getEscapeLevel(), type.getMarkupEscapeType());

    }






    public static String unescapeHtml(final String text) {
        return MarkupEscapist.unescape(text);
    }




    private HtmlEscapist() {
        super();
    }



}

