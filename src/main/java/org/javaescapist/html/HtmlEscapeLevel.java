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
package org.javaescapist.html;

/**
 * <p>
 *   Escape levels for HTML code:
 * </p>
 *
 * <ul>
 *     <li><strong>Level 0</strong>: Only markup-significant characters, excluding '.</li>
 *     <li><strong>Level 1</strong>: Only markup-significant characters, including '.</li>
 *     <li><strong>Level 2</strong>: Markup-significant characters including ', plus all ASCII.</li>
 *     <li><strong>Level 3</strong>: All non-alphanumeric characters.</li>
 *     <li><strong>Level 4</strong>: All characters.</li>
 * </ul>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public enum HtmlEscapeLevel {

    LEVEL_0_ONLY_MARKUP_SIGNIFICANT_WITHOUT_APOS(0),
    LEVEL_1_ONLY_MARKUP_SIGNIFICANT_WITH_APOS(1),
    LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT_WITH_APOS(2),
    LEVEL_3_ALL_NON_ALPHANUMERIC(3),
    LEVEL_4_ALL_CHARACTERS(4);


    private final int escapeLevel;

    HtmlEscapeLevel(final int escapeLevel) {
        this.escapeLevel = escapeLevel;
    }

    public int getEscapeLevel() {
        return this.escapeLevel;
    }

}

