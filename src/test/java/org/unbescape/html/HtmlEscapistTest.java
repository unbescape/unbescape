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
import java.io.StringWriter;
import java.io.Writer;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class HtmlEscapistTest {







    public static void main(String[] args) throws Exception {

        System.out.println(HtmlEscapist.escapeHtml("hello"));
        System.out.println(HtmlEscapist.escapeHtml("hello how are you"));
        System.out.println(HtmlEscapist.escapeHtml("I'm < 355, & you?"));
        System.out.println(HtmlEscapist.escapeHtml("\u0163\"I'm < 355, & you\u00E1?\""));
        System.out.println(HtmlEscapist.escapeHtml("[\u0163\u00E1aeiouABC0123\uD840\uDC00']\ud835\udccd", HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL));
        System.out.println(HtmlEscapist.escapeHtml("[\u0163\u00E1aeiouABC0123\uD840\uDC00']\ud835\udccd", HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL));


        for (final HtmlEscapeLevel context : HtmlEscapeLevel.values()) {
            for (final HtmlEscapeType type : HtmlEscapeType.values()) {
                System.out.println("(" + type + "," + context + ") " + HtmlEscapist.escapeHtml("\u0163\"I'm < 355, & you\u00E1?\"", type, context));
            }
        }

        final long start = System.nanoTime();
        for (int i = 0; i < 10000000; i++) {
            final String s = HtmlEscapist.escapeHtml("\u0163\"I'm < 355, & you? \u00FA\"");
        }
        final long end = System.nanoTime();

        System.out.println("nanos: " + (end - start));



        StringWriter sw = new StringWriter();
        HtmlEscapist.escapeHtml("hello".toCharArray(), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        HtmlEscapist.escapeHtml("hello how are you".toCharArray(), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        HtmlEscapist.escapeHtml("I'm < 355, & you?".toCharArray(), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        HtmlEscapist.escapeHtml("\u0163\"I'm < 355, & you\u00E1?\"".toCharArray(), sw);
        System.out.println(sw.toString());


        for (final HtmlEscapeLevel context : HtmlEscapeLevel.values()) {
            for (final HtmlEscapeType type : HtmlEscapeType.values()) {
                sw = new StringWriter();
                HtmlEscapist.escapeHtml("\u0163\"I'm < 355, & you\u00E1?\"".toCharArray(), sw, type, context);
                System.out.println("(" + type + "," + context + ") " + sw.toString());
            }
        }

        final Writer writer = new Writer() {

            @Override
            public void write(final char[] cbuf, final int off, final int len) throws IOException {

            }

            @Override
            public void flush() throws IOException {

            }

            @Override
            public void close() throws IOException {

            }
        };

        final char[] text = "\u0163\"I'm < 355, & you\u00E1? \u00FA\"".toCharArray();
        final long start2 = System.nanoTime();
        for (int i = 0; i < 10000000; i++) {
            HtmlEscapist.escapeHtml(text, writer);
        }
        final long end2 = System.nanoTime();

        System.out.println("nanos: " + (end2 - start2));

        System.out.println(SMPTest.asHexCharString(new String(Character.toChars(0x20000))));

    }


    public HtmlEscapistTest() {
        super();
    }


}

