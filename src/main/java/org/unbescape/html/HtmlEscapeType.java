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
 *   Escape types for HTML code:
 * </p>
 *
 * <ul>
 *     <li><strong>HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL</strong>: xxx</li>
 * </ul>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0
 *
 */
public enum HtmlEscapeType {

    HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(true, false, false),
    HTML4_NAMED_REFERENCES_DEFAULT_TO_HEXA(true, true, false),
    HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL(true, false, true),
    HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA(true, true, true),
    DECIMAL_REFERENCES(false, false, false),
    HEXADECIMAL_REFERENCES(false, true, false);


    private final boolean useNCRs;
    private final boolean useHexa;
    private final boolean useHtml5;

    HtmlEscapeType(final boolean useNCRs, final boolean useHexa, final boolean useHtml5) {
        this.useNCRs = useNCRs;
        this.useHexa = useHexa;
        this.useHtml5 = useHtml5;
    }

    public boolean getUseNCRs() {
        return this.useNCRs;
    }

    public boolean getUseHexa() {
        return this.useHexa;
    }

    public boolean getUseHtml5() {
        return this.useHtml5;
    }
}

