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

import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.esapi.codecs.HTMLEntityCodec;
import org.springframework.web.util.HtmlUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class Benchmark {





    static String asHexCharString(final String text) {
        final StringBuilder strBuilder = new StringBuilder();
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            final char c = text.charAt(i);
            strBuilder.append("\\u" + Integer.toHexString((int)c).toUpperCase());
        }
        return strBuilder.toString();
    }


    public static void main(String[] args) throws Exception {


        final HTMLEntityCodec htmlEntityCodec = new HTMLEntityCodec();
        final char[] immune = new char[0];

        final String s1 = "[\u0163\u00E1aeiouABC0123&#x20000;']\ud835\udccd";
        System.out.println(s1);
        final String s2 = StringEscapeUtils.unescapeHtml4(s1);
        System.out.println("UNESCAPED:         " + s2 + " -> " + asHexCharString(s2));
        final String s3 = HtmlEscape.escapeHtml(s2, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA);
        System.out.println("UNBESCAPE:      " + s3);
        final String s4 = StringEscapeUtils.escapeHtml4(s2);
        System.out.println("STRINGESCAPEUTILS: " + s4);
        final String s5 = HtmlUtils.htmlEscape(s2);
        System.out.println("SPRING HTMLUTILS:  " + s5);
        final String s6 = htmlEntityCodec.encode(immune, s2);
        System.out.println("ESAPI:             " + s6);


        String testMsg = "\"Im < 355, & you\u00E1?\"";
//        String testMsg = "\u0163\"I'm < 355, & you\u00E1?\"";
//        String testMsg = "Im less than 355, and you?";
        for (int i = 0; i < 5; i++) {
            testMsg = testMsg + testMsg;
        }
        final int execs = 100000;

        // Warmup
        for (int i = 0; i < 100; i++) {
            final String result1 = HtmlEscape.escapeHtml(testMsg, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL);
            final String result2 = StringEscapeUtils.escapeHtml4(testMsg);
            final String result3 = HtmlUtils.htmlEscape(testMsg);
            final String result4 = htmlEntityCodec.encode(immune, testMsg);
        }

        final long jstart = System.nanoTime();
        for (int i = 0; i < execs; i++) {
            final String result = HtmlEscape.escapeHtml(testMsg, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL);
        }
        final long jfinish = System.nanoTime();

        final long cstart = System.nanoTime();
        for (int i = 0; i < execs; i++) {
            final String result = StringEscapeUtils.escapeHtml4(testMsg);
        }
        final long cfinish = System.nanoTime();

        final long gstart = System.nanoTime();
        for (int i = 0; i < execs; i++) {
            final String result = HtmlUtils.htmlEscape(testMsg);
        }
        final long gfinish = System.nanoTime();

        System.out.println(String.format("J: %15d nanosecs", Long.valueOf(jfinish - jstart)));
        System.out.println(String.format("C: %15d nanosecs", Long.valueOf(cfinish - cstart)));
        System.out.println(String.format("S: %15d nanosecs", Long.valueOf(gfinish - gstart)));

        System.out.println(HtmlEscape.escapeHtml(testMsg));
        System.out.println(HtmlEscape.escapeHtml(testMsg) == testMsg);
        System.out.println(StringEscapeUtils.escapeHtml4(testMsg));
        System.out.println(StringEscapeUtils.escapeHtml4(testMsg) == testMsg);
        System.out.println(HtmlUtils.htmlEscape(testMsg));
        System.out.println(HtmlUtils.htmlEscape(testMsg) == testMsg);


        System.out.println("UNESCAPE: " + HtmlEscape.unescapeHtml(
                HtmlEscape.escapeHtml(s2, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA)));
        System.out.println("UNESCAPE: " + HtmlEscape.unescapeHtml(
                HtmlEscape.escapeHtml(s2, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL)));
        System.out.println("UNESCAPE: " + HtmlEscape.unescapeHtml("&euro; - &#x20aC; - &#8364; - &#x80; - &#128; - &#x80gs - &#128as"));

//        final String testUnescMsg = HtmlEscape.escapeHtml(s2, HtmlEscape.HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL);
        final String testUnescMsg = "&lsqb;&tcedil;&aacuteaeiouABC0123&#131072;&#39;&rsqb;&xscr;";

        System.out.println("ESCAPED:           " + testUnescMsg);
        final String u3 = HtmlEscape.unescapeHtml(testUnescMsg);
        System.out.println("UNBESCAPE:         " + u3);
        final String u4 = StringEscapeUtils.unescapeHtml4(testUnescMsg);
        System.out.println("STRINGESCAPEUTILS: " + u4);
        final String u5 = HtmlUtils.htmlUnescape(testUnescMsg);
        System.out.println("SPRING HTMLUTILS:  " + u5);
        final String u6 = htmlEntityCodec.decode(testUnescMsg);
        System.out.println("ESAPI:             " + u6);


        // Warmup
        for (int i = 0; i < 100; i++) {
            final String result1 = HtmlEscape.unescapeHtml(testUnescMsg);
            final String result2 = StringEscapeUtils.unescapeHtml4(testUnescMsg);
            final String result3 = HtmlUtils.htmlUnescape(testUnescMsg);
            final String result4 = htmlEntityCodec.decode(testUnescMsg);
        }

        final long ujstart = System.nanoTime();
        for (int i = 0; i < execs; i++) {
            final String result = HtmlEscape.unescapeHtml(testUnescMsg);
        }
        final long ujfinish = System.nanoTime();

        final long ucstart = System.nanoTime();
        for (int i = 0; i < execs; i++) {
            final String result = StringEscapeUtils.unescapeHtml4(testUnescMsg);
        }
        final long ucfinish = System.nanoTime();

        final long ugstart = System.nanoTime();
        for (int i = 0; i < execs; i++) {
            final String result = HtmlUtils.htmlUnescape(testUnescMsg);
        }
        final long ugfinish = System.nanoTime();

        final long uestart = System.nanoTime();
        for (int i = 0; i < execs; i++) {
            final String result = htmlEntityCodec.decode(testUnescMsg);
        }
        final long uefinish = System.nanoTime();

        System.out.println(String.format("J: %15d nanosecs", Long.valueOf(ujfinish - ujstart)));
        System.out.println(String.format("C: %15d nanosecs", Long.valueOf(ucfinish - ucstart)));
        System.out.println(String.format("S: %15d nanosecs", Long.valueOf(ugfinish - ugstart)));
        System.out.println(String.format("E: %15d nanosecs", Long.valueOf(uefinish - uestart)));

        final String partial01 = "&lsqb;&tcedil;&aacuteaeiouABC0123&#131072;&#39;&rsqb;&xscr;";
        final String uPartial01 = HtmlEscape.unescapeHtml(partial01);
        System.out.println("UNBESCAPE PARTIAL:         " + uPartial01);
        final String uPartial02 = StringEscapeUtils.unescapeHtml4(partial01);
        System.out.println("STRINGESCAPEUTILS PARTIAL: " + uPartial02);

    }


    public Benchmark() {
        super();
    }


}

