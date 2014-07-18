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
package org.unbescape.css;

/**
 * <p>
 *   Levels defined for escape/unescape operations of CSS identifiers:
 * </p>
 *
 * <ul>
 *     <li><strong>Level 1</strong>: Escape only the basic escape set. Note the result of a level-1 escape
 *         operation might still contain non-ASCII characters if they existed in input, and therefore you
 *         will still need to correctly manage your input/output character encoding settings. Such
 *         <em>basic set</em> consists of:
 *         <ul>
 *           <li>The <em>Backslash Escapes</em>:
 *               <kbd>&#92; </kbd> (<kbd>U+0020</kbd>),
 *               <kbd>&#92;!</kbd> (<kbd>U+0021</kbd>),
 *               <kbd>&#92;&quot;</kbd> (<kbd>U+0022</kbd>),
 *               <kbd>&#92;#</kbd> (<kbd>U+0023</kbd>),
 *               <kbd>&#92;$</kbd> (<kbd>U+0024</kbd>),
 *               <kbd>&#92;%</kbd> (<kbd>U+0025</kbd>),
 *               <kbd>&#92;&amp;</kbd> (<kbd>U+0026</kbd>),
 *               <kbd>&#92;&apos;</kbd> (<kbd>U+0027</kbd>),
 *               <kbd>&#92;(</kbd> (<kbd>U+0028</kbd>),
 *               <kbd>&#92;)</kbd> (<kbd>U+0029</kbd>),
 *               <kbd>&#92;*</kbd> (<kbd>U+002A</kbd>),
 *               <kbd>&#92;+</kbd> (<kbd>U+002B</kbd>),
 *               <kbd>&#92;,</kbd> (<kbd>U+002C</kbd>),
 *               <kbd>&#92;.</kbd> (<kbd>U+002E</kbd>),
 *               <kbd>&#92;&#47;</kbd> (<kbd>U+002F</kbd>),
 *               <kbd>&#92;;</kbd> (<kbd>U+003B</kbd>),
 *               <kbd>&#92;&lt;</kbd> (<kbd>U+003C</kbd>),
 *               <kbd>&#92;=</kbd> (<kbd>U+003D</kbd>),
 *               <kbd>&#92;&gt;</kbd> (<kbd>U+003E</kbd>),
 *               <kbd>&#92;?</kbd> (<kbd>U+003F</kbd>),
 *               <kbd>&#92;@</kbd> (<kbd>U+0040</kbd>),
 *               <kbd>&#92;[</kbd> (<kbd>U+005B</kbd>),
 *               <kbd>&#92;&#92;</kbd> (<kbd>U+005C</kbd>),
 *               <kbd>&#92;]</kbd> (<kbd>U+005D</kbd>),
 *               <kbd>&#92;^</kbd> (<kbd>U+005E</kbd>),
 *               <kbd>&#92;`</kbd> (<kbd>U+0060</kbd>),
 *               <kbd>&#92;{</kbd> (<kbd>U+007B</kbd>),
 *               <kbd>&#92;|</kbd> (<kbd>U+007C</kbd>),
 *               <kbd>&#92;}</kbd> (<kbd>U+007D</kbd>) and
 *               <kbd>&#92;~</kbd> (<kbd>U+007E</kbd>).
 *               Note that the <kbd>&#92;-</kbd> (<kbd>U+002D</kbd>) escape sequence exists, but will only be used
 *               when an identifier starts with two hypens or hyphen + digit. Also, the <kbd>&#92;_</kbd>
 *               (<kbd>U+005F</kbd>) escape will only be used at the beginning of an identifier to avoid
 *               problems with Internet Explorer 6. In the same sense, note that the <kbd>&#92;:</kbd>
 *               (<kbd>U+003A</kbd>) escape sequence is also defined in the standard, but will not be
 *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
 *           </li>
 *           <li>
 *               Two ranges of non-displayable, control characters: <kbd>U+0000</kbd> to <kbd>U+001F</kbd>
 *               and <kbd>U+007F</kbd> to <kbd>U+009F</kbd>.
 *           </li>
 *         </ul>
 *     </li>
 *     <li><strong>Level 2</strong>: Escape the basic escape set (as defined in level 1), plus all
 *         non-ASCII characters. The result of a level-2 escape operation is therefore always ASCII-only text, and
 *         safer to use in complex scenarios with mixed input/output character encodings.</li>
 *     <li><strong>Level 3</strong>: Escape all non-alphanumeric characters, this is, all but those in the
 *         <kbd>A</kbd>-<kbd>Z</kbd>, <kbd>a</kbd>-<kbd>z</kbd> and <kbd>0</kbd>-<kbd>9</kbd> ranges. This level
 *         can be safely used for completely escaping texts, including whitespace, line feeds, punctuation, etc. in
 *         scenarios where this adds an extra level of safety.</li>
 *     <li><strong>Level 4</strong>: Escape all characters, even alphanumeric ones.</li>
 * </ul>
 *
 * <p>
 *   For further information, see the <em>Glossary</em> and the <em>References</em> sections at the
 *   documentation for the {@link CssEscape} class.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0.0
 *
 */
public enum CssIdentifierEscapeLevel {

    /**
     * Level 1 escape: escape only the basic escape set: Backslash Escape plus non-displayable control chars.</kbd>
     */
    LEVEL_1_BASIC_ESCAPE_SET(1),

    /**
     * Level 2 escape: escape the basic escape set plus all non-ASCII characters (result will always be ASCII).
     */
    LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET(2),

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
    public static CssIdentifierEscapeLevel forLevel(final int level) {
        switch (level) {
            case 1: return LEVEL_1_BASIC_ESCAPE_SET;
            case 2: return LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET;
            case 3: return LEVEL_3_ALL_NON_ALPHANUMERIC;
            case 4: return LEVEL_4_ALL_CHARACTERS;
            default:
                throw new IllegalArgumentException("No escape level enum constant defined for level: " + level);
        }
    }


    CssIdentifierEscapeLevel(final int escapeLevel) {
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

