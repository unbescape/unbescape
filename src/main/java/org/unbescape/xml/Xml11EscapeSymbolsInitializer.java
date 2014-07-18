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
package org.unbescape.xml;

import java.util.Arrays;

/**
 * <p>
 *   This class initializes the {@link org.unbescape.xml.XmlEscapeSymbols#XML11_SYMBOLS} structure.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0.0
 *
 */
final class Xml11EscapeSymbolsInitializer {


    static XmlEscapeSymbols initializeXml11() {

        final XmlEscapeSymbols.References xml11References = new XmlEscapeSymbols.References();

        /*
         * --------------------------------------------------------------------------------------------------
         *   XML 1.1 CHARACTER ENTITY REFERENCES
         *   See: http://www.w3.org/TR/xml11
         * --------------------------------------------------------------------------------------------------
         */
        xml11References.addReference( 34, "&quot;");
        xml11References.addReference( 38, "&amp;");
        xml11References.addReference( 39, "&apos;");
        xml11References.addReference( 60, "&lt;");
        xml11References.addReference( 62, "&gt;");


        /*
         * Initialization of escape levels.
         * Defined levels :
         *
         *    - Level 1 : Markup-significant characters (including the apostrophe)
         *    - Level 2 : Markup-significant characters plus all non-ASCII
         *    - Level 3 : All non-alphanumeric characters
         *    - Level 4 : All characters
         *
         * Note all XML 1.1 levels include the escape of allowed control characteres.
         *
         */
        final byte[] escapeLevels = new byte[XmlEscapeSymbols.LEVELS_LEN];

        /*
         * Everything is level 3 unless contrary indication.
         */
        Arrays.fill(escapeLevels, (byte)3);

        /*
         * Everything non-ASCII is level 2 unless contrary indication.
         */
        for (char c = 0x80; c < XmlEscapeSymbols.LEVELS_LEN; c++) {
            escapeLevels[c] = 2;
        }

        /*
         * Alphanumeric characters are level 4.
         */
        for (char c = 'A'; c <= 'Z'; c++) {
            escapeLevels[c] = 4;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            escapeLevels[c] = 4;
        }
        for (char c = '0'; c <= '9'; c++) {
            escapeLevels[c] = 4;
        }

        /*
         * The five XML predefined entities will be escaped always (level 1)
         */
        escapeLevels['\''] = 1;
        escapeLevels['"'] = 1;
        escapeLevels['<'] = 1;
        escapeLevels['>'] = 1;
        escapeLevels['&'] = 1;

        /*
         * XML 1.1 allows a series of control characters, but they should appear
         * escaped: [#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]
         */
        for (char c = 0x1; c <= 0x8; c++) {
            escapeLevels[c] = 1;
        }
        escapeLevels[0xB] = 1;
        escapeLevels[0xC] = 1;
        for (char c = 0xE; c <= 0x1F; c++) {
            escapeLevels[c] = 1;
        }
        for (char c = 0x7F; c <= 0x84; c++) {
            escapeLevels[c] = 1;
        }
        for (char c = 0x86; c <= 0x9F; c++) {
            escapeLevels[c] = 1;
        }

        /*
         * Create the new symbols structure
         */
        return new XmlEscapeSymbols(xml11References, escapeLevels, new Xml11CodepointValidator());

    }



    private Xml11EscapeSymbolsInitializer() {
        super();
    }



    static final class Xml11CodepointValidator implements XmlCodepointValidator {

        /*
         * XML 1.1 does not allow the null byte, nor unpaired surrogate chars
         * (characters used for composing two-char codepoints, but appearing on their own).
         */
        public boolean isValid(final int codepoint) {
            if (codepoint == 0x0) {
                return false;
            }
            if (codepoint <= 0xD7FF) { // U+D800 - U+DFFF are reserved for low + high surrogates
                return true;
            }
            if (codepoint < 0xE000) {
                return false;
            }
            if (codepoint <= 0xFFFD) { // U+FFFE and U+FFFF are non-characters, and therefore not valid
                return true;
            }
            if (codepoint < 0x10000) {
                return false;
            }
            return true;
        }

    }


}

