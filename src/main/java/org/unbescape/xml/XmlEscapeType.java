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
 *   Types of escape operations to be performed on XML text:
 * </p>
 *
 * <ul>
 *     <li><kbd><strong>CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_DECIMAL</strong></kbd>: Replace escaped characters
 *         with Character Entity References whenever possible (depending on the specified
 *         {@link org.unbescape.xml.XmlEscapeLevel}), and default to using <em>Decimal Character References</em>
 *         for escaped characters that do not have an associated CER.</li>
 *     <li><kbd><strong>CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_DECIMAL</strong></kbd>: Replace escaped characters
 *         with Character Entity References whenever possible (depending on the specified
 *         {@link org.unbescape.xml.XmlEscapeLevel}), and default to using <em>Hexadecimal Character References</em>
 *         for escaped characters that do not have an associated CER.</li>
 *     <li><kbd><strong>DECIMAL_REFERENCES</strong></kbd>: Replace escaped characters with
 *         <em>Decimal Character References</em> (will never use CER).</li>
 *     <li><kbd><strong>HEXADECIMAL_REFERENCES</strong></kbd>: Replace escaped characters with
 *         <em>Hexadecimal Character References</em> (will never use CERs).</li>
 * </ul>
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
public enum XmlEscapeType {

    /**
     * Use Character Entity References if possible, default to Decimal Character References.
     */
    CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_DECIMAL(true, false),

    /**
     * Use Character Entity Referencess if possible, default to Hexadecimal Character References.
     */
    CHARACTER_ENTITY_REFERENCES_DEFAULT_TO_HEXA(true, true),

    /**
     * Always use Decimal Character References (no CERs will be used).
     */
    DECIMAL_REFERENCES(false, false),

    /**
     * Always use Hexadecimal Character References (no CERs will be used).
     */
    HEXADECIMAL_REFERENCES(false, true);


    private final boolean useCERs;
    private final boolean useHexa;

    XmlEscapeType(final boolean useCERs, final boolean useHexa) {
        this.useCERs = useCERs;
        this.useHexa = useHexa;
    }

    boolean getUseCERs() {
        return this.useCERs;
    }

    boolean getUseHexa() {
        return this.useHexa;
    }

}

