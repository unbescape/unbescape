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

import org.junit.Test;

import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeDecimal0;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeDecimal1;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeDecimal2;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeDecimal3;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeDecimal4;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHexa0;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHexa1;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHexa2;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHexa3;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHexa4;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Decimal0;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Decimal1;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Decimal2;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Decimal3;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Decimal4;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Hexa0;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Hexa1;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Hexa2;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Hexa3;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml4Hexa4;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Decimal0;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Decimal1;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Decimal2;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Decimal3;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Decimal4;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Hexa0;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Hexa1;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Hexa2;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Hexa3;
import static org.unbescape.html.HtmlEscapeTestUtil.testEscapeHtml5Hexa4;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class HtmlI18nEscapeTest {



    private static final String TEXT =
            "Jako efektivn\u0115j\u0161\u00ED se n\u00E1m jev\u00ED po\u0159\u00E1d\u00E1n\u00ED tzv.\n" +
            "Gira prost\u0159ednictv\u00EDm na\u0161ich autorizovan\u00FDch dealer\u016F v " +
            "\u010Cech\u00E1ch a na Morav\u011B, kter\u00E9 prob\u011Bhnou v pr\u016Fb\u011Bhu " +
            "z\u00E1\u0159\u00ED a \u0159\u00EDj0na.";




    @Test
    public void testHtml5() throws Exception {


        final String textHtml5DecLevel0 = TEXT;
        final String textHtml5DecLevel1 = TEXT;
        final String textHtml5DecLevel2 =
                "Jako efektivn&#277;j&scaron;&iacute; se n&aacute;m jev&iacute; po&rcaron;&aacute;d&aacute;n&iacute; tzv.\n" +
                "Gira prost&rcaron;ednictv&iacute;m na&scaron;ich autorizovan&yacute;ch dealer&uring; v " +
                "&Ccaron;ech&aacute;ch a na Morav&ecaron;, kter&eacute; prob&ecaron;hnou v pr&uring;b&ecaron;hu " +
                "z&aacute;&rcaron;&iacute; a &rcaron;&iacute;j0na.";
        final String textHtml5DecLevel3 =
                "Jako&#32;efektivn&#277;j&scaron;&iacute;&#32;se&#32;n&aacute;m&#32;jev&iacute;&#32;po&rcaron;&aacute;d&aacute;n&iacute;&#32;tzv&period;&NewLine;" +
                "Gira&#32;prost&rcaron;ednictv&iacute;m&#32;na&scaron;ich&#32;autorizovan&yacute;ch&#32;dealer&uring;&#32;v&#32;" +
                "&Ccaron;ech&aacute;ch&#32;a&#32;na&#32;Morav&ecaron;&comma;&#32;kter&eacute;&#32;prob&ecaron;hnou&#32;v&#32;pr&uring;b&ecaron;hu&#32;" +
                "z&aacute;&rcaron;&iacute;&#32;a&#32;&rcaron;&iacute;j0na&period;";
        final String textHtml5DecLevel4 =
                "&#74;&#97;&#107;&#111;&#32;&#101;&#102;&#101;&#107;&#116;&#105;&#118;&#110;&#277;&#106;&scaron;&iacute;&#32;" +
                "&#115;&#101;&#32;&#110;&aacute;&#109;&#32;&#106;&#101;&#118;&iacute;&#32;&#112;&#111;&rcaron;&aacute;&#100;&aacute;&#110;&iacute;&#32;" +
                "&#116;&#122;&#118;&period;&NewLine;&#71;&#105;&#114;&#97;&#32;&#112;&#114;&#111;&#115;&#116;&rcaron;&#101;&#100;&#110;&#105;&#99;&#116;&#118;&iacute;&#109;&#32;" +
                "&#110;&#97;&scaron;&#105;&#99;&#104;&#32;&#97;&#117;&#116;&#111;&#114;&#105;&#122;&#111;&#118;&#97;&#110;&yacute;&#99;&#104;&#32;" +
                "&#100;&#101;&#97;&#108;&#101;&#114;&uring;&#32;&#118;&#32;&Ccaron;&#101;&#99;&#104;&aacute;&#99;&#104;&#32;&#97;&#32;&#110;&#97;&#32;" +
                "&#77;&#111;&#114;&#97;&#118;&ecaron;&comma;&#32;&#107;&#116;&#101;&#114;&eacute;&#32;&#112;&#114;&#111;&#98;&ecaron;&#104;&#110;&#111;&#117;&#32;" +
                "&#118;&#32;&#112;&#114;&uring;&#98;&ecaron;&#104;&#117;&#32;&#122;&aacute;&rcaron;&iacute;&#32;&#97;&#32;&rcaron;&iacute;&#106;&#48;&#110;&#97;&period;";

        testEscapeHtml5Decimal0(TEXT, textHtml5DecLevel0);
        testEscapeHtml5Decimal1(TEXT, textHtml5DecLevel1);
        testEscapeHtml5Decimal2(TEXT, textHtml5DecLevel2);
        testEscapeHtml5Decimal3(TEXT, textHtml5DecLevel3);
        testEscapeHtml5Decimal4(TEXT, textHtml5DecLevel4);


        final String textHtml5HexaLevel0 = TEXT;
        final String textHtml5HexaLevel1 = TEXT;
        final String textHtml5HexaLevel2 =
                "Jako efektivn&#x115;j&scaron;&iacute; se n&aacute;m jev&iacute; po&rcaron;&aacute;d&aacute;n&iacute; tzv.\n" +
                "Gira prost&rcaron;ednictv&iacute;m na&scaron;ich autorizovan&yacute;ch dealer&uring; v " +
                "&Ccaron;ech&aacute;ch a na Morav&ecaron;, kter&eacute; prob&ecaron;hnou v pr&uring;b&ecaron;hu " +
                "z&aacute;&rcaron;&iacute; a &rcaron;&iacute;j0na.";
        final String textHtml5HexaLevel3 =
                "Jako&#x20;efektivn&#x115;j&scaron;&iacute;&#x20;se&#x20;n&aacute;m&#x20;jev&iacute;&#x20;" +
                "po&rcaron;&aacute;d&aacute;n&iacute;&#x20;tzv&period;&NewLine;Gira&#x20;prost&rcaron;ednictv&iacute;m&#x20;" +
                "na&scaron;ich&#x20;autorizovan&yacute;ch&#x20;dealer&uring;&#x20;v&#x20;&Ccaron;ech&aacute;ch&#x20;a&#x20;" +
                "na&#x20;Morav&ecaron;&comma;&#x20;kter&eacute;&#x20;prob&ecaron;hnou&#x20;v&#x20;pr&uring;b&ecaron;hu&#x20;" +
                "z&aacute;&rcaron;&iacute;&#x20;a&#x20;&rcaron;&iacute;j0na&period;";
        final String textHtml5HexaLevel4 =
                "&#x4a;&#x61;&#x6b;&#x6f;&#x20;&#x65;&#x66;&#x65;&#x6b;&#x74;&#x69;&#x76;&#x6e;&#x115;&#x6a;&scaron;&iacute;&#x20;" +
                "&#x73;&#x65;&#x20;&#x6e;&aacute;&#x6d;&#x20;&#x6a;&#x65;&#x76;&iacute;&#x20;" +
                "&#x70;&#x6f;&rcaron;&aacute;&#x64;&aacute;&#x6e;&iacute;&#x20;&#x74;&#x7a;&#x76;&period;&NewLine;&#x47;&#x69;&#x72;&#x61;&#x20;" +
                "&#x70;&#x72;&#x6f;&#x73;&#x74;&rcaron;&#x65;&#x64;&#x6e;&#x69;&#x63;&#x74;&#x76;&iacute;&#x6d;&#x20;" +
                "&#x6e;&#x61;&scaron;&#x69;&#x63;&#x68;&#x20;&#x61;&#x75;&#x74;&#x6f;&#x72;&#x69;&#x7a;&#x6f;&#x76;&#x61;&#x6e;&yacute;&#x63;&#x68;&#x20;" +
                "&#x64;&#x65;&#x61;&#x6c;&#x65;&#x72;&uring;&#x20;&#x76;&#x20;&Ccaron;&#x65;&#x63;&#x68;&aacute;&#x63;&#x68;&#x20;&#x61;&#x20;" +
                "&#x6e;&#x61;&#x20;&#x4d;&#x6f;&#x72;&#x61;&#x76;&ecaron;&comma;&#x20;&#x6b;&#x74;&#x65;&#x72;&eacute;&#x20;" +
                "&#x70;&#x72;&#x6f;&#x62;&ecaron;&#x68;&#x6e;&#x6f;&#x75;&#x20;&#x76;&#x20;&#x70;&#x72;&uring;&#x62;&ecaron;&#x68;&#x75;&#x20;" +
                "&#x7a;&aacute;&rcaron;&iacute;&#x20;&#x61;&#x20;&rcaron;&iacute;&#x6a;&#x30;&#x6e;&#x61;&period;";

        testEscapeHtml5Hexa0(TEXT, textHtml5HexaLevel0);
        testEscapeHtml5Hexa1(TEXT, textHtml5HexaLevel1);
        testEscapeHtml5Hexa2(TEXT, textHtml5HexaLevel2);
        testEscapeHtml5Hexa3(TEXT, textHtml5HexaLevel3);
        testEscapeHtml5Hexa4(TEXT, textHtml5HexaLevel4);

    }





    @Test
    public void testHtml4() throws Exception {

        final String textHtml4DecLevel0 = TEXT;
        final String textHtml4DecLevel1 = TEXT;
        final String textHtml4DecLevel2 =
                "Jako efektivn&#277;j&scaron;&iacute; se n&aacute;m jev&iacute; po&#345;&aacute;d&aacute;n&iacute; tzv.\n" +
                "Gira prost&#345;ednictv&iacute;m na&scaron;ich autorizovan&yacute;ch dealer&#367; v " +
                "&#268;ech&aacute;ch a na Morav&#283;, kter&eacute; prob&#283;hnou v pr&#367;b&#283;hu " +
                "z&aacute;&#345;&iacute; a &#345;&iacute;j0na.";
        final String textHtml4DecLevel3 =
                "Jako&#32;efektivn&#277;j&scaron;&iacute;&#32;se&#32;n&aacute;m&#32;jev&iacute;&#32;po&#345;&aacute;d&aacute;n&iacute;&#32;tzv&#46;&#10;" +
                "Gira&#32;prost&#345;ednictv&iacute;m&#32;na&scaron;ich&#32;autorizovan&yacute;ch&#32;dealer&#367;&#32;v&#32;" +
                "&#268;ech&aacute;ch&#32;a&#32;na&#32;Morav&#283;&#44;&#32;kter&eacute;&#32;prob&#283;hnou&#32;v&#32;pr&#367;b&#283;hu&#32;" +
                "z&aacute;&#345;&iacute;&#32;a&#32;&#345;&iacute;j0na&#46;";
        final String textHtml4DecLevel4 =
                "&#74;&#97;&#107;&#111;&#32;&#101;&#102;&#101;&#107;&#116;&#105;&#118;&#110;&#277;&#106;&scaron;&iacute;&#32;" +
                "&#115;&#101;&#32;&#110;&aacute;&#109;&#32;&#106;&#101;&#118;&iacute;&#32;&#112;&#111;&#345;&aacute;&#100;&aacute;&#110;&iacute;&#32;" +
                "&#116;&#122;&#118;&#46;&#10;&#71;&#105;&#114;&#97;&#32;&#112;&#114;&#111;&#115;&#116;&#345;&#101;&#100;&#110;&#105;&#99;&#116;&#118;&iacute;&#109;&#32;" +
                "&#110;&#97;&scaron;&#105;&#99;&#104;&#32;&#97;&#117;&#116;&#111;&#114;&#105;&#122;&#111;&#118;&#97;&#110;&yacute;&#99;&#104;&#32;" +
                "&#100;&#101;&#97;&#108;&#101;&#114;&#367;&#32;&#118;&#32;&#268;&#101;&#99;&#104;&aacute;&#99;&#104;&#32;&#97;&#32;&#110;&#97;&#32;" +
                "&#77;&#111;&#114;&#97;&#118;&#283;&#44;&#32;&#107;&#116;&#101;&#114;&eacute;&#32;&#112;&#114;&#111;&#98;&#283;&#104;&#110;&#111;&#117;&#32;" +
                "&#118;&#32;&#112;&#114;&#367;&#98;&#283;&#104;&#117;&#32;&#122;&aacute;&#345;&iacute;&#32;&#97;&#32;&#345;&iacute;&#106;&#48;&#110;&#97;&#46;";

        testEscapeHtml4Decimal0(TEXT, textHtml4DecLevel0);
        testEscapeHtml4Decimal1(TEXT, textHtml4DecLevel1);
        testEscapeHtml4Decimal2(TEXT, textHtml4DecLevel2);
        testEscapeHtml4Decimal3(TEXT, textHtml4DecLevel3);
        testEscapeHtml4Decimal4(TEXT, textHtml4DecLevel4);


        final String textHtml4HexaLevel0 = TEXT;
        final String textHtml4HexaLevel1 = TEXT;
        final String textHtml4HexaLevel2 =
                "Jako efektivn&#x115;j&scaron;&iacute; se n&aacute;m jev&iacute; po&#x159;&aacute;d&aacute;n&iacute; tzv.\n" +
                "Gira prost&#x159;ednictv&iacute;m na&scaron;ich autorizovan&yacute;ch dealer&#x16f; v " +
                "&#x10c;ech&aacute;ch a na Morav&#x11b;, kter&eacute; prob&#x11b;hnou v pr&#x16f;b&#x11b;hu " +
                "z&aacute;&#x159;&iacute; a &#x159;&iacute;j0na.";
        final String textHtml4HexaLevel3 =
                "Jako&#x20;efektivn&#x115;j&scaron;&iacute;&#x20;se&#x20;n&aacute;m&#x20;jev&iacute;&#x20;" +
                "po&#x159;&aacute;d&aacute;n&iacute;&#x20;tzv&#x2e;&#xa;Gira&#x20;prost&#x159;ednictv&iacute;m&#x20;" +
                "na&scaron;ich&#x20;autorizovan&yacute;ch&#x20;dealer&#x16f;&#x20;v&#x20;&#x10c;ech&aacute;ch&#x20;a&#x20;" +
                "na&#x20;Morav&#x11b;&#x2c;&#x20;kter&eacute;&#x20;prob&#x11b;hnou&#x20;v&#x20;pr&#x16f;b&#x11b;hu&#x20;" +
                "z&aacute;&#x159;&iacute;&#x20;a&#x20;&#x159;&iacute;j0na&#x2e;";
        final String textHtml4HexaLevel4 =
                "&#x4a;&#x61;&#x6b;&#x6f;&#x20;&#x65;&#x66;&#x65;&#x6b;&#x74;&#x69;&#x76;&#x6e;&#x115;&#x6a;&scaron;&iacute;&#x20;" +
                "&#x73;&#x65;&#x20;&#x6e;&aacute;&#x6d;&#x20;&#x6a;&#x65;&#x76;&iacute;&#x20;" +
                "&#x70;&#x6f;&#x159;&aacute;&#x64;&aacute;&#x6e;&iacute;&#x20;&#x74;&#x7a;&#x76;&#x2e;&#xa;&#x47;&#x69;&#x72;&#x61;&#x20;" +
                "&#x70;&#x72;&#x6f;&#x73;&#x74;&#x159;&#x65;&#x64;&#x6e;&#x69;&#x63;&#x74;&#x76;&iacute;&#x6d;&#x20;" +
                "&#x6e;&#x61;&scaron;&#x69;&#x63;&#x68;&#x20;&#x61;&#x75;&#x74;&#x6f;&#x72;&#x69;&#x7a;&#x6f;&#x76;&#x61;&#x6e;&yacute;&#x63;&#x68;&#x20;" +
                "&#x64;&#x65;&#x61;&#x6c;&#x65;&#x72;&#x16f;&#x20;&#x76;&#x20;&#x10c;&#x65;&#x63;&#x68;&aacute;&#x63;&#x68;&#x20;&#x61;&#x20;" +
                "&#x6e;&#x61;&#x20;&#x4d;&#x6f;&#x72;&#x61;&#x76;&#x11b;&#x2c;&#x20;&#x6b;&#x74;&#x65;&#x72;&eacute;&#x20;" +
                "&#x70;&#x72;&#x6f;&#x62;&#x11b;&#x68;&#x6e;&#x6f;&#x75;&#x20;&#x76;&#x20;&#x70;&#x72;&#x16f;&#x62;&#x11b;&#x68;&#x75;&#x20;" +
                "&#x7a;&aacute;&#x159;&iacute;&#x20;&#x61;&#x20;&#x159;&iacute;&#x6a;&#x30;&#x6e;&#x61;&#x2e;";

        testEscapeHtml4Hexa0(TEXT, textHtml4HexaLevel0);
        testEscapeHtml4Hexa1(TEXT, textHtml4HexaLevel1);
        testEscapeHtml4Hexa2(TEXT, textHtml4HexaLevel2);
        testEscapeHtml4Hexa3(TEXT, textHtml4HexaLevel3);
        testEscapeHtml4Hexa4(TEXT, textHtml4HexaLevel4);

    }





    @Test
    public void testDecimal() throws Exception {

        final String textDecLevel0 = TEXT;
        final String textDecLevel1 = TEXT;
        final String textDecLevel2 =
                "Jako efektivn&#277;j&#353;&#237; se n&#225;m jev&#237; po&#345;&#225;d&#225;n&#237; tzv.\n" +
                "Gira prost&#345;ednictv&#237;m na&#353;ich autorizovan&#253;ch dealer&#367; v " +
                "&#268;ech&#225;ch a na Morav&#283;, kter&#233; prob&#283;hnou v pr&#367;b&#283;hu " +
                "z&#225;&#345;&#237; a &#345;&#237;j0na.";
        final String textDecLevel3 =
                "Jako&#32;efektivn&#277;j&#353;&#237;&#32;se&#32;n&#225;m&#32;jev&#237;&#32;po&#345;&#225;d&#225;n&#237;&#32;tzv&#46;&#10;" +
                "Gira&#32;prost&#345;ednictv&#237;m&#32;na&#353;ich&#32;autorizovan&#253;ch&#32;dealer&#367;&#32;v&#32;" +
                "&#268;ech&#225;ch&#32;a&#32;na&#32;Morav&#283;&#44;&#32;kter&#233;&#32;prob&#283;hnou&#32;v&#32;pr&#367;b&#283;hu&#32;" +
                "z&#225;&#345;&#237;&#32;a&#32;&#345;&#237;j0na&#46;";
        final String textDecLevel4 =
                "&#74;&#97;&#107;&#111;&#32;&#101;&#102;&#101;&#107;&#116;&#105;&#118;&#110;&#277;&#106;&#353;&#237;&#32;" +
                "&#115;&#101;&#32;&#110;&#225;&#109;&#32;&#106;&#101;&#118;&#237;&#32;&#112;&#111;&#345;&#225;&#100;&#225;&#110;&#237;&#32;" +
                "&#116;&#122;&#118;&#46;&#10;&#71;&#105;&#114;&#97;&#32;&#112;&#114;&#111;&#115;&#116;&#345;&#101;&#100;&#110;&#105;&#99;&#116;&#118;&#237;&#109;&#32;" +
                "&#110;&#97;&#353;&#105;&#99;&#104;&#32;&#97;&#117;&#116;&#111;&#114;&#105;&#122;&#111;&#118;&#97;&#110;&#253;&#99;&#104;&#32;" +
                "&#100;&#101;&#97;&#108;&#101;&#114;&#367;&#32;&#118;&#32;&#268;&#101;&#99;&#104;&#225;&#99;&#104;&#32;&#97;&#32;&#110;&#97;&#32;" +
                "&#77;&#111;&#114;&#97;&#118;&#283;&#44;&#32;&#107;&#116;&#101;&#114;&#233;&#32;&#112;&#114;&#111;&#98;&#283;&#104;&#110;&#111;&#117;&#32;" +
                "&#118;&#32;&#112;&#114;&#367;&#98;&#283;&#104;&#117;&#32;&#122;&#225;&#345;&#237;&#32;&#97;&#32;&#345;&#237;&#106;&#48;&#110;&#97;&#46;";

        testEscapeDecimal0(TEXT, textDecLevel0);
        testEscapeDecimal1(TEXT, textDecLevel1);
        testEscapeDecimal2(TEXT, textDecLevel2);
        testEscapeDecimal3(TEXT, textDecLevel3);
        testEscapeDecimal4(TEXT, textDecLevel4);

    }





    @Test
    public void testHexa() throws Exception {

        final String textHexaLevel0 = TEXT;
        final String textHexaLevel1 = TEXT;
        final String textHexaLevel2 =
                "Jako efektivn&#x115;j&#x161;&#xed; se n&#xe1;m jev&#xed; po&#x159;&#xe1;d&#xe1;n&#xed; tzv.\n" +
                "Gira prost&#x159;ednictv&#xed;m na&#x161;ich autorizovan&#xfd;ch dealer&#x16f; v " +
                "&#x10c;ech&#xe1;ch a na Morav&#x11b;, kter&#xe9; prob&#x11b;hnou v pr&#x16f;b&#x11b;hu " +
                "z&#xe1;&#x159;&#xed; a &#x159;&#xed;j0na.";
        final String textHexaLevel3 =
                "Jako&#x20;efektivn&#x115;j&#x161;&#xed;&#x20;se&#x20;n&#xe1;m&#x20;jev&#xed;&#x20;po&#x159;&#xe1;d&#xe1;n&#xed;&#x20;tzv&#x2e;&#xa;" +
                "Gira&#x20;prost&#x159;ednictv&#xed;m&#x20;na&#x161;ich&#x20;autorizovan&#xfd;ch&#x20;dealer&#x16f;&#x20;v&#x20;" +
                "&#x10c;ech&#xe1;ch&#x20;a&#x20;na&#x20;Morav&#x11b;&#x2c;&#x20;kter&#xe9;&#x20;prob&#x11b;hnou&#x20;v&#x20;pr&#x16f;b&#x11b;hu&#x20;" +
                "z&#xe1;&#x159;&#xed;&#x20;a&#x20;&#x159;&#xed;j0na&#x2e;";
        final String textHexaLevel4 =
                "&#x4a;&#x61;&#x6b;&#x6f;&#x20;&#x65;&#x66;&#x65;&#x6b;&#x74;&#x69;&#x76;&#x6e;&#x115;&#x6a;&#x161;&#xed;&#x20;" +
                "&#x73;&#x65;&#x20;&#x6e;&#xe1;&#x6d;&#x20;&#x6a;&#x65;&#x76;&#xed;&#x20;&#x70;&#x6f;&#x159;&#xe1;" +
                "&#x64;&#xe1;&#x6e;&#xed;&#x20;&#x74;&#x7a;&#x76;&#x2e;&#xa;&#x47;&#x69;&#x72;&#x61;&#x20;&#x70;&#x72;" +
                "&#x6f;&#x73;&#x74;&#x159;&#x65;&#x64;&#x6e;&#x69;&#x63;&#x74;&#x76;&#xed;&#x6d;&#x20;&#x6e;&#x61;" +
                "&#x161;&#x69;&#x63;&#x68;&#x20;&#x61;&#x75;&#x74;&#x6f;&#x72;&#x69;&#x7a;&#x6f;&#x76;&#x61;&#x6e;" +
                "&#xfd;&#x63;&#x68;&#x20;&#x64;&#x65;&#x61;&#x6c;&#x65;&#x72;&#x16f;&#x20;&#x76;&#x20;&#x10c;&#x65;" +
                "&#x63;&#x68;&#xe1;&#x63;&#x68;&#x20;&#x61;&#x20;&#x6e;&#x61;&#x20;&#x4d;&#x6f;&#x72;&#x61;&#x76;" +
                "&#x11b;&#x2c;&#x20;&#x6b;&#x74;&#x65;&#x72;&#xe9;&#x20;&#x70;&#x72;&#x6f;&#x62;&#x11b;&#x68;&#x6e;" +
                "&#x6f;&#x75;&#x20;&#x76;&#x20;&#x70;&#x72;&#x16f;&#x62;&#x11b;&#x68;&#x75;&#x20;&#x7a;&#xe1;&#x159;" +
                "&#xed;&#x20;&#x61;&#x20;&#x159;&#xed;&#x6a;&#x30;&#x6e;&#x61;&#x2e;";

        testEscapeHexa0(TEXT, textHexaLevel0);
        testEscapeHexa1(TEXT, textHexaLevel1);
        testEscapeHexa2(TEXT, textHexaLevel2);
        testEscapeHexa3(TEXT, textHexaLevel3);
        testEscapeHexa4(TEXT, textHexaLevel4);

    }





    public HtmlI18nEscapeTest() {
        super();
    }


}

