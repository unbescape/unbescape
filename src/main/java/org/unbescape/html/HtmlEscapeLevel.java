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

/**
 * <p>
 *   Levels defined for HTML escape/unescape operations:
 * </p>
 *
 * <ul>
 *     <li><strong>Level 0</strong>: Escape only markup-significant characters, excluding the apostrophe. Therefore
 *         <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd> and <kbd>&quot</kbd> will be escaped. This level can be
 *         used for escaping texts and also tag attributes that are always surrounded by double quotes, whenever the
 *         apostrophe (<kbd>&apos;</kbd>) is considered a <em>safe</em> character and the user prefers it not to be
 *         escaped for legibility reasons (e.g. might denote literals in expression languages like OGNL).
 *         Note the result of a level-0 escape operation might still contain non-ASCII characters if they existed
 *         in input, and therefore you will still need to correctly manage your input/output character
 *         encoding settings.</li>
 *     <li><strong>Level 1</strong>: Escape only markup-significant characters (including the apostrophe). Therefore
 *         <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd>, <kbd>&quot</kbd> and <kbd>&apos;</kbd> will be escaped.
 *         This level is sometimes called <em>XML escape</em> or <strong><em>XML-style escape</em></strong>, though it
 *         is not exactly equivalent to XML due to some HTML specificities. It is equivalent to the JSP escape
 *         configured by the <kbd>escapeXml</kbd> attribute in JSTL's <kbd>&lt;c:out ... /&gt;</kbd> tags, and safe
 *         for use in texts and also tag attributes that are always quoted &mdash;be it with single (apostrophe) or
 *         double quotes. Note the result of a level-1 escape operation might still contain non-ASCII characters if they
 *         existed in input, and therefore you will still need to correctly manage your input/output character
 *         encoding settings.</li>
 *     <li><strong>Level 2</strong>: Escape all markup-significant characters (as defined in level 1), plus all
 *         non-ASCII characters. The result of a level-2 escape operation is therefore always ASCII-only text, and
 *         safer to use in complex scenarios with mixed input/output character encodings. This level can be used
 *         for escaping texts and also tag attributes that are always quoted &mdash;be it with single (apostrophe) or
 *         double quotes.</li>
 *     <li><strong>Level 3</strong>: Escape all non-alphanumeric characters, this is, all but those in the
 *         <kbd>A</kbd>-<kbd>Z</kbd>, <kbd>a</kbd>-<kbd>z</kbd> and <kbd>0</kbd>-<kbd>9</kbd> ranges. This level
 *         can be safely used for escaping texts and also tag attributes, even when these tag attributes are
 *         unquoted.</li>
 *     <li><strong>Level 4</strong>: Escape all characters, even alphanumeric ones.</li>
 * </ul>
 *
 * <p>
 *   For further information, see the <em>Glossary</em> and the <em>References</em> sections at the
 *   documentation for the {@link org.unbescape.html.HtmlEscape} class.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0.0
 *
 */
public enum HtmlEscapeLevel {

    /**
     * Level 0 escape: escape only markup-significant characters, excluding the apostrophe:
     * <kbd>&lt;</kbd>, <kbd>&gt;</kbd>, <kbd>&amp;</kbd> and <kbd>&quot</kbd>
     */
    LEVEL_0_ONLY_MARKUP_SIGNIFICANT_EXCEPT_APOS(0),

    /**
     * Level 1 escape (<em>XML-style</em>): escape only markup-significant characters (including the apostrophe):
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
    public static HtmlEscapeLevel forLevel(final int level) {
        switch (level) {
            case 0: return LEVEL_0_ONLY_MARKUP_SIGNIFICANT_EXCEPT_APOS;
            case 1: return LEVEL_1_ONLY_MARKUP_SIGNIFICANT;
            case 2: return LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT;
            case 3: return LEVEL_3_ALL_NON_ALPHANUMERIC;
            case 4: return LEVEL_4_ALL_CHARACTERS;
            default:
                throw new IllegalArgumentException("No escape level enum constant defined for level: " + level);
        }
    }


    HtmlEscapeLevel(final int escapeLevel) {
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

