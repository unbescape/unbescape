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


    public static enum HtmlEscapeContext {
        ATTRIBUTE_OR_TEXT(new char[] {'\''}, null), // Don't escape apostrophes at all
        SINGLE_QUOTED_ATTRIBUTE(null, new char[] {'\''}); // Escape apostrophes, but not with literals (&apos; is forbidden in HTML)

        private final char[] nonEscapedChars;
        private final char[] nonLiteralEscapedChars;

        HtmlEscapeContext(final char[] nonEscapedChars, final char[] nonLiteralEscapedChars) {
            this.nonEscapedChars = nonEscapedChars;
            this.nonLiteralEscapedChars = nonLiteralEscapedChars;
        }

        public char[] getNonEscapedChars() {
            return nonEscapedChars;
        }

        public char[] getNonLiteralEscapedChars() {
            return nonLiteralEscapedChars;
        }

    }


    public static enum HtmlEscapeType {
        LITERAL_DEFAULT_TO_DECIMAL(MarkupEscapist.MarkupEscapeType.LITERAL_DEFAULT_TO_DECIMAL),
        LITERAL_DEFAULT_TO_HEXA(MarkupEscapist.MarkupEscapeType.LITERAL_DEFAULT_TO_HEXA),
        DECIMAL(MarkupEscapist.MarkupEscapeType.DECIMAL),
        HEXA(MarkupEscapist.MarkupEscapeType.HEXA);

        private final MarkupEscapist.MarkupEscapeType markupEscapeType;

        HtmlEscapeType(final MarkupEscapist.MarkupEscapeType markupEscapeType) {
            this.markupEscapeType = markupEscapeType;
        }

        public MarkupEscapist.MarkupEscapeType getMarkupEscapeType() {
            return this.markupEscapeType;
        }

    }



    private static final MarkupEscapist HTML_MARKUP_ESCAPER;

    // - Max char with literal escape is 9830 (\u2666 = &diams;).
    private static final char MAX_ESCAPED_CHAR = '\u2666';
    // There are exactly 253 literal escape sequences in HTML (4+)
    private static final short TOTAL_LITERAL_ESCAPES = 253;



    static {

        HTML_MARKUP_ESCAPER = new MarkupEscapist(MAX_ESCAPED_CHAR, TOTAL_LITERAL_ESCAPES);

        /*
         * -----------------------------------------------------------
         *   HTML ESCAPE ENTITIES
         *   See: http://www.w3.org/TR/REC-html40/sgml/entities.html
         * -----------------------------------------------------------
         */

        /* HTML ESCAPE ENTITIES FOR MARKUP-SIGNIFICANT CHARACTERS */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\'', "&apos;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('"', "&quot;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('&', "&amp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('<', "&lt;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('>', "&gt;");
        /* HTML ESCAPE ENTITIES FOR ISO-8859-1 CHARACTERS */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A0', "&nbsp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A1', "&iexcl;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A2', "&cent;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A3', "&pound;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A4', "&curren;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A5', "&yen;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A6', "&brvbar;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A7', "&sect;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A8', "&uml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00A9', "&copy;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00AA', "&ordf;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00AB', "&laquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00AC', "&not;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00AD', "&shy;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00AE', "&reg;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00AF', "&macr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B0', "&deg;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B1', "&plusmn;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B2', "&sup2;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B3', "&sup3;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B4', "&acute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B5', "&micro;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B6', "&para;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B7', "&middot;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B8', "&cedil;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00B9', "&sup1;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00BA', "&ordm;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00BB', "&raquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00BC', "&frac14;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00BD', "&frac12;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00BE', "&frac34;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00BF', "&iquest;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C0', "&Agrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C1', "&Aacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C2', "&Acirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C3', "&Atilde;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C4', "&Auml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C5', "&Aring;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C6', "&AElig;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C7', "&Ccedil;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C8', "&Egrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00C9', "&Eacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00CA', "&Ecirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00CB', "&Euml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00CC', "&Igrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00CD', "&Iacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00CE', "&Icirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00CF', "&Iuml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D0', "&ETH;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D1', "&Ntilde;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D2', "&Ograve;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D3', "&Oacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D4', "&Ocirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D5', "&Otilde;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D6', "&Ouml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D7', "&times;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D8', "&Oslash;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00D9', "&Ugrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00DA', "&Uacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00DB', "&Ucirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00DC', "&Uuml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00DD', "&Yacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00DE', "&THORN;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00DF', "&szlig;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E0', "&agrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E1', "&aacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E2', "&acirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E3', "&atilde;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E4', "&auml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E5', "&aring;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E6', "&aelig;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E7', "&ccedil;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E8', "&egrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00E9', "&eacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00EA', "&ecirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00EB', "&euml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00EC', "&igrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00ED', "&iacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00EE', "&icirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00EF', "&iuml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F0', "&eth;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F1', "&ntilde;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F2', "&ograve;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F3', "&oacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F4', "&ocirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F5', "&otilde;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F6', "&ouml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F7', "&divide;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F8', "&oslash;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00F9', "&ugrave;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00FA', "&uacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00FB', "&ucirc;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00FC', "&uuml;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00FD', "&yacute;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00FE', "&thorn;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u00FF', "&yuml;");
        /* HTML ESCAPE ENTITIES FOR SYMBOLS, MATHEMATICAL SYMBOLS AND GREEK LETTERS */
        /* - Greek */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0192', "&fnof;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0391', "&Alpha;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0392', "&Beta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0393', "&Gamma;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0394', "&Delta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0395', "&Epsilon;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0396', "&Zeta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0397', "&Eta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0398', "&Theta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0399', "&Iota;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u039A', "&Kappa;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u039B', "&Lambda;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u039C', "&Mu;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u039D', "&Nu;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u039E', "&Xi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u039F', "&Omicron;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A0', "&Pi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A1', "&Rho;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A3', "&Sigma;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A4', "&Tau;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A5', "&Upsilon;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A6', "&Phi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A7', "&Chi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A8', "&Psi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03A9', "&Omega;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B1', "&alpha;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B2', "&beta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B3', "&gamma;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B4', "&delta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B5', "&epsilon;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B6', "&zeta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B7', "&eta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B8', "&theta;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03B9', "&iota;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03BA', "&kappa;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03BB', "&lambda;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03BC', "&mu;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03BD', "&nu;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03BE', "&xi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03BF', "&omicron;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C0', "&pi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C1', "&rho;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C2', "&sigmaf;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C3', "&sigma;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C4', "&tau;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C5', "&upsilon;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C6', "&phi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C7', "&chi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C8', "&psi;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03C9', "&omega;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03D1', "&thetasym;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03D2', "&upsih;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u03D6', "&piv;");
        /* - General punctuation */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2022', "&bull;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2026', "&hellip;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2032', "&prime;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2033', "&Prime;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u203E', "&oline;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2044', "&frasl;");
        /* - Letter-like symbols */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2118', "&weierp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2111', "&image;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u211C', "&real;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2122', "&trade;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2135', "&alefsym;");
        /* - Arrows */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2190', "&larr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2191', "&uarr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2192', "&rarr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2193', "&darr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2194', "&harr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u21B5', "&crarr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u21D0', "&lArr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u21D1', "&uArr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u21D2', "&rArr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u21D3', "&dArr;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u21D4', "&hArr;");
        /* - Mathematical operators */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2200', "&forall;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2202', "&part;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2203', "&exist;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2205', "&empty;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2207', "&nabla;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2208', "&isin;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2209', "&notin;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u220B', "&ni;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u220F', "&prod;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2211', "&sum;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2212', "&minus;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2217', "&lowast;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u221A', "&radic;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u221D', "&prop;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u221E', "&infin;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2220', "&ang;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2227', "&and;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2228', "&or;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2229', "&cap;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u222A', "&cup;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u222B', "&int;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2234', "&there4;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u223C', "&sim;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2245', "&cong;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2248', "&asymp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2260', "&ne;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2261', "&equiv;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2264', "&le;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2265', "&ge;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2282', "&sub;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2283', "&sup;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2284', "&nsub;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2286', "&sube;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2287', "&supe;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2295', "&oplus;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2297', "&otimes;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u22A5', "&perp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u22C5', "&sdot;");
        /* - Miscellaneous technical */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2308', "&lceil;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2309', "&rceil;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u230A', "&lfloor;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u230B', "&rfloor;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2329', "&lang;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u232A', "&rang;");
        /* - Geometric shapes */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u25CA', "&loz;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2660', "&spades;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2663', "&clubs;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2665', "&hearts;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2666', "&diams;");
        /* HTML ESCAPE ENTITIES FOR INTERNATIONALIZATION CHARACTERS */
        /* - Latin Extended-A */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0152', "&OElig;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0153', "&oelig;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0160', "&Scaron;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0161', "&scaron;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u0178', "&Yuml;");
        /* - Spacing modifier letters */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u02C6', "&circ;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u02DC', "&tilde;");
        /* - General punctuation */
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2002', "&ensp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2003', "&emsp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2009', "&thinsp;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u200C', "&zwnj;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u200D', "&zwj;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u200E', "&lrm;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u200F', "&rlm;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2013', "&ndash;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2014', "&mdash;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2018', "&lsquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2019', "&rsquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u201A', "&sbquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u201C', "&ldquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u201D', "&rdquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u201E', "&bdquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2020', "&dagger;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2021', "&Dagger;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2030', "&permil;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u2039', "&lsaquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u203A', "&rsaquo;");
        HTML_MARKUP_ESCAPER.addLiteralEscape('\u20AC', "&euro;");

        HTML_MARKUP_ESCAPER.initialize();

    }
    




    public static String escapeHtml(final String text) {
        return escapeHtml(text, HtmlEscapeType.LITERAL_DEFAULT_TO_DECIMAL, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
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

        return HTML_MARKUP_ESCAPER.escape(
                text, context.getNonEscapedChars(), context.getNonLiteralEscapedChars(), type.getMarkupEscapeType());

    }





    public static void escapeHtml(final char[] text, final Writer writer) throws IOException {
        escapeHtml(text, 0, text.length, writer, HtmlEscapeType.LITERAL_DEFAULT_TO_DECIMAL, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
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
        escapeHtml(text, offset, len, writer, HtmlEscapeType.LITERAL_DEFAULT_TO_DECIMAL, HtmlEscapeContext.ATTRIBUTE_OR_TEXT);
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

        HTML_MARKUP_ESCAPER.escape(
                text, offset, len, writer,
                context.getNonEscapedChars(), context.getNonLiteralEscapedChars(), type.getMarkupEscapeType());

    }



    private HtmlEscapist() {
        super();
    }



}

