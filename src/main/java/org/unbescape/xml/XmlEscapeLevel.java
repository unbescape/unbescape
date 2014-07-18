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

/**
 * <p>
 *   Levels defined for XML escape/unescape operations:
 * </p>
 *
 * <ul>
 *     <li><strong>Level 1</strong>: Escape only markup-significant characters (all five <em>XML predefined
 *         entities</em>). Therefore <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and
 *         <kbd>&apos;</kbd> will be escaped. This level is safe for use in texts and also XML tag attributes
 *         (tag attributes are always quoted in XML). Note the result of a level-1 escape operation might
 *         still contain non-ASCII characters if they existed in input, and therefore you will still need
 *         to correctly manage your input/output character encoding settings.</li>
 *     <li><strong>Level 2</strong>: Escape all markup-significant characters (as defined in level 1), plus all
 *         non-ASCII characters. The result of a level-2 escape operation is therefore always ASCII-only text, and
 *         safer to use in complex scenarios with mixed input/output character encodings. This level is safe for
 *         use in texts and also XML tag attributes (tag attributes are always quoted in XML). </li>
 *     <li><strong>Level 3</strong>: Escape all non-alphanumeric characters, this is, all but those in the
 *         <kbd>A</kbd>-<kbd>Z</kbd>, <kbd>a</kbd>-<kbd>z</kbd> and <kbd>0</kbd>-<kbd>9</kbd> ranges. This level
 *         can be safely used for completely escaping texts, including whitespace, line feeds, punctuation, etc. in
 *         scenarios where this adds an extra level of safety.</li>
 *     <li><strong>Level 4</strong>: Escape all characters, even alphanumeric ones.</li>
 * </ul>
 *
 * <p>
 *   Note that, apart from the settings established by each of these levels, different XML versions might establish
 *   the required escaping of a series of <em>control characteres</em> (basically, all the allowed ones). These
 *   control character will be <em>always</em> escaped, from level 1. Besides, some characters considered invalid
 *   in such versions of XML might be directly removed from output.
 * </p>
 *
 * <p>
 *   Also note that no <em>level 0</em> exists, in order to keep consistency with HTML escape levels defined in
 *   {@link org.unbescape.html.HtmlEscapeLevel}.
 * </p>
 *
 * <p>
 *   For further information, see the <em>Glossary</em> and the <em>References</em> sections at the
 *   documentation for the {@link org.unbescape.xml.XmlEscape} class.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0.0
 *
 */
public enum XmlEscapeLevel {

    /**
     * Level 1 escape: escape only markup-significant characters (all five <em>XML predefined entities</em>):
     * <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd>
     */
    LEVEL_1_ONLY_MARKUP_SIGNIFICANT(1),

    /**
     * Level 2 escape: escape markup-significant characters plus all non-ASCII characters (result will always be ASCII).
     */
    LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT(2),

    /**
     * Level 3 escape: escape all non-alphanumeric characteres (escape all but those in the
     * <kbd>A</kbd>-<kbd>Z</kbd>, <kbd>a</kbd>-<kbd>z</kbd> and <kbd>0</kbd>-<kbd>9</kbd> ranges).
     */
    LEVEL_3_ALL_NON_ALPHANUMERIC(3),

    /**
     * Level 4 escape: escape all characters, including alphanumeric.
     */
    LEVEL_4_ALL_CHARACTERS(4);




    private final int escapeLevel;


    /**
     * <p>
     *   Utility method for obtaining an enum value from its corresponding <kbd>int</kbd> level value.
     * </p>
     *
     * @param level the level
     * @return the escape level enum constant, or <kbd>IllegalArgumentException</kbd> if level does not exist.
     */
    public static XmlEscapeLevel forLevel(final int level) {
        switch (level) {
            case 1: return LEVEL_1_ONLY_MARKUP_SIGNIFICANT;
            case 2: return LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT;
            case 3: return LEVEL_3_ALL_NON_ALPHANUMERIC;
            case 4: return LEVEL_4_ALL_CHARACTERS;
            default:
                throw new IllegalArgumentException("No escape level enum constant defined for level: " + level);
        }
    }


    XmlEscapeLevel(final int escapeLevel) {
        this.escapeLevel = escapeLevel;
    }

    /**
     * Return the <kbd>int</kbd> escape level.
     *
     * @return the escape level.
     */
    public int getEscapeLevel() {
        return this.escapeLevel;
    }

}

