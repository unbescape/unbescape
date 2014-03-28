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

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class XmlReferences {




    static final MarkupEscapist.References REFERENCES;



    static {

        final MarkupEscapist.References xmlReferences = new MarkupEscapist.References();

        /*
         * ---------------------------------------------------------------------------
         *   XML ESCAPE ENTITIES
         *   Can be used both for XML markup and for XML-like escaping in HTML markup
         * ---------------------------------------------------------------------------
         */

        xmlReferences.addReference('\'', "&apos;");
        xmlReferences.addReference('"', "&quot;");
        xmlReferences.addReference('&', "&amp;");
        xmlReferences.addReference('<', "&lt;");
        xmlReferences.addReference('>', "&gt;");

        REFERENCES = xmlReferences;

    }


    private XmlReferences() {
        super();
    }



}

