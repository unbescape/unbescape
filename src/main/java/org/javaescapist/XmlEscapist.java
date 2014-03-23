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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class XmlEscapist {


    private static final Entity[] ENTITIES;
    private static final int ENTITIES_LEN;




    static {

        final List<Entity> entities = new ArrayList<Entity>(200);

        entities.add(new Entity('\'', "&apos;", 39));
        entities.add(new Entity('"', "&quot;", 34));
        entities.add(new Entity('&', "&amp;", 38));
        entities.add(new Entity('<', "&lt;", 60));
        entities.add(new Entity('>', "&gt;", 62));


        /*
         * Final static array initialisation
         */

        int maxEscapedChar = Integer.MIN_VALUE;
        for (final Entity entity : entities) {
            if (entity.character > maxEscapedChar) {
                maxEscapedChar = entity.character;
            }
        }

        ENTITIES_LEN = maxEscapedChar + 1;
        ENTITIES = new Entity[ENTITIES_LEN];
        Arrays.fill(ENTITIES, null);
        for (final Entity entity : entities) {
            ENTITIES[entity.character] = entity;
        }

    }






    public static String escapeXml(final String text) {

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;
        int readOffset = 0;
        final int textLen = text.length();

        for (int i = 0; i < textLen; i++) {

            final char c = text.charAt(i);

            if (c >= ENTITIES_LEN || ENTITIES[c] == null) {
                continue;
            }

            final Entity entity = ENTITIES[c];
            final String escape =
                    (entity.literalEscapeAllowed)? entity.literalEscape : entity.codeEscape;

            if (strBuilder == null) {
                strBuilder = new StringBuilder(textLen + 15);
            }
            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }
            strBuilder.append(escape);
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





    private XmlEscapist() {
        super();
    }



    private static final class Entity {

        final char character;
        final String literalEscape;
        final String codeEscape;
        final boolean literalEscapeAllowed;

        private Entity(final char character, final String literalEscape,
                       final int codeEscape) {
            this(character, literalEscape, codeEscape, true);
        }

        private Entity(final char character, final String literalEscape,
                       final int codeEscape, final boolean literalEscapeAllowed) {
            if (literalEscape == null) {
                throw new IllegalArgumentException(
                        "Literal escape for char '" + character + "' and " +
                        "code escape " + codeEscape + " cannot be null");
            }
            this.character = character;
            this.literalEscape = literalEscape;
            this.codeEscape = "&#" + codeEscape + ';';
            this.literalEscapeAllowed = literalEscapeAllowed;
        }

    }




    public static void main(String[] args) {

        System.out.println(escapeXml("hello"));
        System.out.println(escapeXml("hello how are you"));
        System.out.println(escapeXml("I'm < 355, & you?"));
        System.out.println(escapeXml("\"I'm < 355, & you?\""));
        System.out.println(escapeXml("\"I'm < 355, & you? \u00FA\""));

    }





}

