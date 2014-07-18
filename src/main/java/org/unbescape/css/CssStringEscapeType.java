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
 *   Types of escape operations to be performed on CSS strings:
 * </p>
 *
 * <ul>
 *     <li><kbd><strong>BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA</strong></kbd>: Use
 *         backslash escapes whenever possible (depending on the specified
 *         {@link CssStringEscapeLevel}). For escaped characters that do
 *         not have an associated backslash escape, default to using
 *         <kbd>&#92;FF*</kbd> variable-length hexadecimal escapes.</li>
 *     <li><kbd><strong>BACKSLASH_ESCAPES_DEFAULT_TO_SIX_DIGIT_HEXA</strong></kbd>: Use
 *         backslash escapes whenever possible (depending on the specified
 *         {@link CssStringEscapeLevel}). For escaped characters that do
 *         not have an associated backslash escape, default to using
 *         <kbd>&#92;FFFFFF</kbd> 6-digit hexadecimal escapes.</li>
 *     <li><kbd><strong>COMPACT_HEXA</strong></kbd>: Replace escaped characters with
 *         <kbd>&#92;FF*</kbd> variable-length hexadecimal escapes.</li>
 *     <li><kbd><strong>SIX_DIGIT_HEXA</strong></kbd>: Replace escaped characters with
 *         <kbd>&#92;FFFFFF</kbd> 6-digit hexadecimal escapes.</li>
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
public enum CssStringEscapeType {

    /**
     * Use backslash escapes if possible, default to &#92;FF* variable-length hexadecimal escapes.
     */
    BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA(true, true),

    /**
     * Use backslash escapes if possible, default to &#92;FFFFFF 6-digit hexadecimal escapes.
     */
    BACKSLASH_ESCAPES_DEFAULT_TO_SIX_DIGIT_HEXA(true, false),

    /**
     * Always use &#92;FF* variable-length hexadecimal escapes.
     */
    COMPACT_HEXA(false, true),

    /**
     * Always use &#92;FFFFFF 6-digit hexadecimal escapes.
     */
    SIX_DIGIT_HEXA(false, false);


    private final boolean useBackslashEscapes;
    private final boolean useCompactHexa;

    CssStringEscapeType(final boolean useBackslashEscapes, final boolean useCompactHexa) {
        this.useBackslashEscapes = useBackslashEscapes;
        this.useCompactHexa = useCompactHexa;
    }

    public boolean getUseBackslashEscapes() {
        return useBackslashEscapes;
    }

    public boolean getUseCompactHexa() {
        return useCompactHexa;
    }

}

