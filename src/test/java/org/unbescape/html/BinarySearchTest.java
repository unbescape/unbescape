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

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */

public class BinarySearchTest {

    private static final String NOT_FOUND = "[NOT FOUND]";
    private static final String PARTIAL_FOUND = "[PARTIAL FOUND]";

    private static final String[] TEXTS = new String[] {
            "&zero", "&one", "&two", "&three", "&four" , "&five",
            "&six", "&seven", "&eight", "&nine", "&ten", "&eleven",
            "&twelve", "&thirteen", "&fourteen", "&fifteen", "&sixteen"
    };

    private final static char[][][] VALUES;

    private final static char[][] VALUESBASE = new char[][] {
        TEXTS[0].toCharArray(),
        TEXTS[1].toCharArray(),
        TEXTS[2].toCharArray(),
        TEXTS[3].toCharArray(),
        TEXTS[4].toCharArray(),
        TEXTS[5].toCharArray(),
        TEXTS[6].toCharArray(),
        TEXTS[7].toCharArray(),
        TEXTS[8].toCharArray(),
        TEXTS[9].toCharArray(),
        TEXTS[10].toCharArray(),
        TEXTS[11].toCharArray(),
        TEXTS[12].toCharArray(),
        TEXTS[13].toCharArray(),
        TEXTS[14].toCharArray()
    };


    static {

        VALUES = new char[15][][];

        for (int i = 0; i < 15; i++) {
            VALUES[i] = new char[i + 1][];
            for (int j = 0; j < i + 1; j++) {
                VALUES[i][j] = VALUESBASE[j];
            }
        }

        for (int i = 0; i < 15; i++) {
            Arrays.sort(VALUES[i], new Comparator<char[]>() {
                public int compare(final char[] o1, final char[] o2) {
                    return new String(o1).compareTo(new String(o2));
                }
            });
        }

    }


    private static String partialFoundText(final String found) {
        return found + "-" + PARTIAL_FOUND;
    }


    private static String search(int valuesIndex, final String text) {
        final int result = HtmlEscapeSymbols.binarySearch(VALUES[valuesIndex], text, 0, text.length());
        if (result == Integer.MIN_VALUE) {
            return NOT_FOUND;
        } else if (result < 0) {
            return partialFoundText(new String(VALUES[valuesIndex][(-1) * (result + 10)]));
        }
        return new String(VALUES[valuesIndex][result]);
    }


    @Test
    public void testBinarySearch() throws Exception {

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < TEXTS.length; j++) {
                final String result = search(i,TEXTS[j]);
                if (j > i) {
                    if (result.indexOf(PARTIAL_FOUND) != -1) {
                        final String found = result.substring(0,result.indexOf('-'));
                        Assert.assertTrue(TEXTS[j].startsWith(found));
                    } else {
                        Assert.assertEquals(NOT_FOUND, result);
                    }
                } else {
                    Assert.assertEquals(TEXTS[j], result);
                }
            }
        }

    }


}

