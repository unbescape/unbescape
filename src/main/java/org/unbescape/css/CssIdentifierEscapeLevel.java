/*
 * =============================================================================
 * 
 *   Copyright (c) 2014-2017, The UNBESCAPE team (http://www.unbescape.org)
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
 *               <tt>&#92; </tt> (<tt>U+0020</tt>),
 *               <tt>&#92;!</tt> (<tt>U+0021</tt>),
 *               <tt>&#92;&quot;</tt> (<tt>U+0022</tt>),
 *               <tt>&#92;#</tt> (<tt>U+0023</tt>),
 *               <tt>&#92;$</tt> (<tt>U+0024</tt>),
 *               <tt>&#92;%</tt> (<tt>U+0025</tt>),
 *               <tt>&#92;&amp;</tt> (<tt>U+0026</tt>),
 *               <tt>&#92;&#39;</tt> (<tt>U+0027</tt>),
 *               <tt>&#92;(</tt> (<tt>U+0028</tt>),
 *               <tt>&#92;)</tt> (<tt>U+0029</tt>),
 *               <tt>&#92;*</tt> (<tt>U+002A</tt>),
 *               <tt>&#92;+</tt> (<tt>U+002B</tt>),
 *               <tt>&#92;,</tt> (<tt>U+002C</tt>),
 *               <tt>&#92;.</tt> (<tt>U+002E</tt>),
 *               <tt>&#92;&#47;</tt> (<tt>U+002F</tt>),
 *               <tt>&#92;;</tt> (<tt>U+003B</tt>),
 *               <tt>&#92;&lt;</tt> (<tt>U+003C</tt>),
 *               <tt>&#92;=</tt> (<tt>U+003D</tt>),
 *               <tt>&#92;&gt;</tt> (<tt>U+003E</tt>),
 *               <tt>&#92;?</tt> (<tt>U+003F</tt>),
 *               <tt>&#92;@</tt> (<tt>U+0040</tt>),
 *               <tt>&#92;[</tt> (<tt>U+005B</tt>),
 *               <tt>&#92;&#92;</tt> (<tt>U+005C</tt>),
 *               <tt>&#92;]</tt> (<tt>U+005D</tt>),
 *               <tt>&#92;^</tt> (<tt>U+005E</tt>),
 *               <tt>&#92;`</tt> (<tt>U+0060</tt>),
 *               <tt>&#92;{</tt> (<tt>U+007B</tt>),
 *               <tt>&#92;|</tt> (<tt>U+007C</tt>),
 *               <tt>&#92;}</tt> (<tt>U+007D</tt>) and
 *               <tt>&#92;~</tt> (<tt>U+007E</tt>).
 *               Note that the <tt>&#92;-</tt> (<tt>U+002D</tt>) escape sequence exists, but will only be used
 *               when an identifier starts with two hypens or hyphen + digit. Also, the <tt>&#92;_</tt>
 *               (<tt>U+005F</tt>) escape will only be used at the beginning of an identifier to avoid
 *               problems with Internet Explorer 6. In the same sense, note that the <tt>&#92;:</tt>
 *               (<tt>U+003A</tt>) escape sequence is also defined in the standard, but will not be
 *               used for escaping as Internet Explorer &lt; 8 does not recognize it.
 *           </li>
 *           <li>
 *               Two ranges of non-displayable, control characters: <tt>U+0000</tt> to <tt>U+001F</tt>
 *               and <tt>U+007F</tt> to <tt>U+009F</tt>.
 *           </li>
 *         </ul>
 *     </li>
 *     <li><strong>Level 2</strong>: Escape the basic escape set (as defined in level 1), plus all
 *         non-ASCII characters. The result of a level-2 escape operation is therefore always ASCII-only text, and
 *         safer to use in complex scenarios with mixed input/output character encodings.</li>
 *     <li><strong>Level 3</strong>: Escape all non-alphanumeric characters, this is, all but those in the
 *         <tt>A</tt>-<tt>Z</tt>, <tt>a</tt>-<tt>z</tt> and <tt>0</tt>-<tt>9</tt> ranges. This level
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
     * Level 1 escape: escape only the basic escape set: Backslash Escape plus non-displayable control chars.
     */
    LEVEL_1_BASIC_ESCAPE_SET(1),

    /**
     * Level 2 escape: escape the basic escape set plus all non-ASCII characters (result will always be ASCII).
     */
    LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET(2),

    /**
     * Level 3 escape: escape all non-alphanumeric characteres (escape all but those in the
     * <tt>A</tt>-<tt>Z</tt>, <tt>a</tt>-<tt>z</tt> and <tt>0</tt>-<tt>9</tt> ranges).
     */
    LEVEL_3_ALL_NON_ALPHANUMERIC(3),

    /**
     * Level 4 escape: escape all characters, including alphanumeric.
     */
    LEVEL_4_ALL_CHARACTERS(4);




    private final int escapeLevel;


    /**
     * <p>
     *   Utility method for obtaining an enum value from its corresponding <tt>int</tt> level value.
     * </p>
     *
     * @param level the level
     * @return the escape level enum constant, or <tt>IllegalArgumentException</tt> if level does not exist.
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
     * Return the <tt>int</tt> escape level.
     *
     * @return the escape level.
     */
    public int getEscapeLevel() {
        return this.escapeLevel;
    }

}

