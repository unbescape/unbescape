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
import static org.unbescape.html.HtmlEscapeTestUtil.testUnescape;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class HtmlEscape02Test {


    private static final String TEXT =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789";




    @Test
    public void testHtml5() throws Exception {

        final String textHtml5DecLevel0 = TEXT;
        final String textHtml5DecLevel1 = TEXT;
        final String textHtml5DecLevel2 = TEXT;
        final String textHtml5DecLevel3 =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ&#32;abcdefghijklmnopqrstuvwxyz&#32;0123456789";
        final String textHtml5DecLevel4 =
                "&#65;&#66;&#67;&#68;&#69;&#70;&#71;&#72;&#73;&#74;&#75;&#76;&#77;&#78;&#79;" +
                "&#80;&#81;&#82;&#83;&#84;&#85;&#86;&#87;&#88;&#89;&#90;&#32;&#97;&#98;&#99;&#100;&#101;&#102;&#103;&#104;&#105;" +
                "&#106;&#107;&#108;&#109;&#110;&#111;&#112;&#113;&#114;&#115;&#116;&#117;&#118;&#119;&#120;&#121;&#122;&#32;&#48;" +
                "&#49;&#50;&#51;&#52;&#53;&#54;&#55;&#56;&#57;";

        testEscapeHtml5Decimal0(TEXT, textHtml5DecLevel0);
        testEscapeHtml5Decimal1(TEXT, textHtml5DecLevel1);
        testEscapeHtml5Decimal2(TEXT, textHtml5DecLevel2);
        testEscapeHtml5Decimal3(TEXT, textHtml5DecLevel3);
        testEscapeHtml5Decimal4(TEXT, textHtml5DecLevel4);

        testUnescape(textHtml5DecLevel0, TEXT);
        testUnescape(textHtml5DecLevel1, TEXT);
        testUnescape(textHtml5DecLevel2, TEXT);
        testUnescape(textHtml5DecLevel3, TEXT);
        testUnescape(textHtml5DecLevel4, TEXT);

        final String textHtml5HexaLevel0 = TEXT;
        final String textHtml5HexaLevel1 = TEXT;
        final String textHtml5HexaLevel2 = TEXT;
        final String textHtml5HexaLevel3 =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ&#x20;abcdefghijklmnopqrstuvwxyz&#x20;0123456789";
        final String textHtml5HexaLevel4 =
                "&#x41;&#x42;&#x43;&#x44;&#x45;&#x46;&#x47;&#x48;&#x49;&#x4a;&#x4b;&#x4c;&#x4d;&#x4e;&#x4f;&#x50;" +
                "&#x51;&#x52;&#x53;&#x54;&#x55;&#x56;&#x57;&#x58;&#x59;&#x5a;&#x20;&#x61;&#x62;&#x63;&#x64;&#x65;&#x66;&#x67;" +
                "&#x68;&#x69;&#x6a;&#x6b;&#x6c;&#x6d;&#x6e;&#x6f;&#x70;&#x71;&#x72;&#x73;&#x74;&#x75;&#x76;&#x77;&#x78;" +
                "&#x79;&#x7a;&#x20;&#x30;&#x31;&#x32;&#x33;&#x34;&#x35;&#x36;&#x37;&#x38;&#x39;";

        testEscapeHtml5Hexa0(TEXT, textHtml5HexaLevel0);
        testEscapeHtml5Hexa1(TEXT, textHtml5HexaLevel1);
        testEscapeHtml5Hexa2(TEXT, textHtml5HexaLevel2);
        testEscapeHtml5Hexa3(TEXT, textHtml5HexaLevel3);
        testEscapeHtml5Hexa4(TEXT, textHtml5HexaLevel4);

        testUnescape(textHtml5HexaLevel0, TEXT);
        testUnescape(textHtml5HexaLevel1, TEXT);
        testUnescape(textHtml5HexaLevel2, TEXT);
        testUnescape(textHtml5HexaLevel3, TEXT);
        testUnescape(textHtml5HexaLevel4, TEXT);

    }





    @Test
    public void testHtml4() throws Exception {

        final String textHtml4DecLevel0 = TEXT;
        final String textHtml4DecLevel1 = TEXT;
        final String textHtml4DecLevel2 = TEXT;
        final String textHtml4DecLevel3 =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ&#32;abcdefghijklmnopqrstuvwxyz&#32;0123456789";
        final String textHtml4DecLevel4 =
                "&#65;&#66;&#67;&#68;&#69;&#70;&#71;&#72;&#73;&#74;&#75;&#76;&#77;&#78;&#79;" +
                "&#80;&#81;&#82;&#83;&#84;&#85;&#86;&#87;&#88;&#89;&#90;&#32;&#97;&#98;&#99;&#100;&#101;&#102;&#103;&#104;&#105;" +
                "&#106;&#107;&#108;&#109;&#110;&#111;&#112;&#113;&#114;&#115;&#116;&#117;&#118;&#119;&#120;&#121;&#122;&#32;&#48;" +
                "&#49;&#50;&#51;&#52;&#53;&#54;&#55;&#56;&#57;";

        testEscapeHtml4Decimal0(TEXT, textHtml4DecLevel0);
        testEscapeHtml4Decimal1(TEXT, textHtml4DecLevel1);
        testEscapeHtml4Decimal2(TEXT, textHtml4DecLevel2);
        testEscapeHtml4Decimal3(TEXT, textHtml4DecLevel3);
        testEscapeHtml4Decimal4(TEXT, textHtml4DecLevel4);

        testUnescape(textHtml4DecLevel0, TEXT);
        testUnescape(textHtml4DecLevel1, TEXT);
        testUnescape(textHtml4DecLevel2, TEXT);
        testUnescape(textHtml4DecLevel3, TEXT);
        testUnescape(textHtml4DecLevel4, TEXT);

        final String textHtml4HexaLevel0 = TEXT;
        final String textHtml4HexaLevel1 = TEXT;
        final String textHtml4HexaLevel2 = TEXT;
        final String textHtml4HexaLevel3 =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ&#x20;abcdefghijklmnopqrstuvwxyz&#x20;0123456789";
        final String textHtml4HexaLevel4 =
                "&#x41;&#x42;&#x43;&#x44;&#x45;&#x46;&#x47;&#x48;&#x49;&#x4a;&#x4b;&#x4c;&#x4d;&#x4e;&#x4f;&#x50;" +
                "&#x51;&#x52;&#x53;&#x54;&#x55;&#x56;&#x57;&#x58;&#x59;&#x5a;&#x20;&#x61;&#x62;&#x63;&#x64;&#x65;&#x66;&#x67;" +
                "&#x68;&#x69;&#x6a;&#x6b;&#x6c;&#x6d;&#x6e;&#x6f;&#x70;&#x71;&#x72;&#x73;&#x74;&#x75;&#x76;&#x77;&#x78;" +
                "&#x79;&#x7a;&#x20;&#x30;&#x31;&#x32;&#x33;&#x34;&#x35;&#x36;&#x37;&#x38;&#x39;";

        testEscapeHtml4Hexa0(TEXT, textHtml4HexaLevel0);
        testEscapeHtml4Hexa1(TEXT, textHtml4HexaLevel1);
        testEscapeHtml4Hexa2(TEXT, textHtml4HexaLevel2);
        testEscapeHtml4Hexa3(TEXT, textHtml4HexaLevel3);
        testEscapeHtml4Hexa4(TEXT, textHtml4HexaLevel4);

        testUnescape(textHtml4HexaLevel0, TEXT);
        testUnescape(textHtml4HexaLevel1, TEXT);
        testUnescape(textHtml4HexaLevel2, TEXT);
        testUnescape(textHtml4HexaLevel3, TEXT);
        testUnescape(textHtml4HexaLevel4, TEXT);

    }





    @Test
    public void testDecimal() throws Exception {

        final String textDecLevel0 = TEXT;
        final String textDecLevel1 = TEXT;
        final String textDecLevel2 = TEXT;
        final String textDecLevel3 =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ&#32;abcdefghijklmnopqrstuvwxyz&#32;0123456789";
        final String textDecLevel4 =
                "&#65;&#66;&#67;&#68;&#69;&#70;&#71;&#72;&#73;&#74;&#75;&#76;&#77;&#78;&#79;" +
                "&#80;&#81;&#82;&#83;&#84;&#85;&#86;&#87;&#88;&#89;&#90;&#32;&#97;&#98;&#99;&#100;&#101;&#102;&#103;&#104;&#105;" +
                "&#106;&#107;&#108;&#109;&#110;&#111;&#112;&#113;&#114;&#115;&#116;&#117;&#118;&#119;&#120;&#121;&#122;&#32;&#48;" +
                "&#49;&#50;&#51;&#52;&#53;&#54;&#55;&#56;&#57;";

        testEscapeDecimal0(TEXT, textDecLevel0);
        testEscapeDecimal1(TEXT, textDecLevel1);
        testEscapeDecimal2(TEXT, textDecLevel2);
        testEscapeDecimal3(TEXT, textDecLevel3);
        testEscapeDecimal4(TEXT, textDecLevel4);

        testUnescape(textDecLevel0, TEXT);
        testUnescape(textDecLevel1, TEXT);
        testUnescape(textDecLevel2, TEXT);
        testUnescape(textDecLevel3, TEXT);
        testUnescape(textDecLevel4, TEXT);

    }





    @Test
    public void testHexa() throws Exception {

        final String textHexaLevel0 = TEXT;
        final String textHexaLevel1 = TEXT;
        final String textHexaLevel2 = TEXT;
        final String textHexaLevel3 =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ&#x20;abcdefghijklmnopqrstuvwxyz&#x20;0123456789";
        final String textHexaLevel4 =
                "&#x41;&#x42;&#x43;&#x44;&#x45;&#x46;&#x47;&#x48;&#x49;&#x4a;&#x4b;&#x4c;&#x4d;&#x4e;&#x4f;&#x50;" +
                "&#x51;&#x52;&#x53;&#x54;&#x55;&#x56;&#x57;&#x58;&#x59;&#x5a;&#x20;&#x61;&#x62;&#x63;&#x64;&#x65;&#x66;&#x67;" +
                "&#x68;&#x69;&#x6a;&#x6b;&#x6c;&#x6d;&#x6e;&#x6f;&#x70;&#x71;&#x72;&#x73;&#x74;&#x75;&#x76;&#x77;&#x78;" +
                "&#x79;&#x7a;&#x20;&#x30;&#x31;&#x32;&#x33;&#x34;&#x35;&#x36;&#x37;&#x38;&#x39;";

        testEscapeHexa0(TEXT, textHexaLevel0);
        testEscapeHexa1(TEXT, textHexaLevel1);
        testEscapeHexa2(TEXT, textHexaLevel2);
        testEscapeHexa3(TEXT, textHexaLevel3);
        testEscapeHexa4(TEXT, textHexaLevel4);

        testUnescape(textHexaLevel0, TEXT);
        testUnescape(textHexaLevel1, TEXT);
        testUnescape(textHexaLevel2, TEXT);
        testUnescape(textHexaLevel3, TEXT);
        testUnescape(textHexaLevel4, TEXT);

    }








    public HtmlEscape02Test() {
        super();
    }


}

