/*
 * =============================================================================
 * 
 *   Copyright (c) 2014-2025 Unbescape (http://www.unbescape.org)
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
 *   Levels defined for escape/unescape operations of CSS strings:
 * </p>
 *
 * <ul>
 *     <li><strong>Level 1</strong>: Escape only the basic escape set. Note the result of a level-1 escape
 *         operation might still contain non-ASCII characters if they existed in input, and therefore you
 *         will still need to correctly manage your input/output character encoding settings. Such
 *         <em>basic set</em> consists of:
 *         <ul>
 *           <li>The <em>Backslash Escapes</em>:
 *             <ul>
 *               <li>The quote symbols:
 *                   <tt>&#92;&quot;</tt> (<tt>U+0022</tt>) and
 *                   <tt>&#92;&#39;</tt> (<tt>U+0027</tt>).
 *               </li>
 *               <li>The backslash: <tt>&#92;&#92;</tt> (<tt>U+005C</tt>).</li>
 *               <li>
 *                   The slash (solidus) symbol (<tt>&#47;</tt>, <tt>U+002F</tt>), which will be escaped in
 *                   order to protect from code injection in HTML environments: browsers will parse
 *                   <tt>&lt;&#47;style&gt;</tt> close tags inside CSS literals and close the tag, therefore
 *                   allowing for further code injection.
 *               </li>
 *               <li>
 *                   The ampersand (<tt>&amp;</tt>, <tt>U+0026</tt>) and semi-colon (<tt>;</tt>, <tt>U+003B</tt>) symbols,
 *                   which will be escaped in order to protect from code injection in XHTML environments: browsers will
 *                   parse XHTML escape codes inside literals in <tt>&lt;style&gt;</tt> tags, therefore allowing
 *                   the closing of the literal and the <tt>&lt;style&gt;</tt> tag itself.
 *               </li>
 *             </ul>
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
public enum CssStringEscapeLevel {

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
    public static CssStringEscapeLevel forLevel(final int level) {
        switch (level) {
            case 1: return LEVEL_1_BASIC_ESCAPE_SET;
            case 2: return LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET;
            case 3: return LEVEL_3_ALL_NON_ALPHANUMERIC;
            case 4: return LEVEL_4_ALL_CHARACTERS;
            default:
                throw new IllegalArgumentException("No escape level enum constant defined for level: " + level);
        }
    }


    CssStringEscapeLevel(final int escapeLevel) {
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

