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

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class Html5Entities {




    private static Integer getDistributionKey(final Map<Integer,int[]> distribution, final int codepoint) {
        for (final Map.Entry<Integer,int[]> distributionEntry : distribution.entrySet()) {
            final int limit = distributionEntry.getKey().intValue();
            if (codepoint < limit) {
                return distributionEntry.getKey();
            }
        }
        return null;
    }



    public static void main(String[] args) throws Exception {


        final List<String> entityLines = IOUtils.readLines(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("HTML5_entities.txt")));

        final Map<Integer, int[]> distribution = new LinkedHashMap<Integer, int[]>();
        distribution.put(Integer.valueOf(0x07F), new int[]{ 0, 0, 0 });
        distribution.put(Integer.valueOf(0x0FFF), new int[]{ 0, 0, 0 });
        distribution.put(Integer.valueOf(0x2FFF), new int[]{ 0, 0, 0 });
        distribution.put(Integer.valueOf(0x7FFF), new int[]{ 0, 0, 0 });
        distribution.put(Integer.valueOf(0xFFFF), new int[]{ 0, 0, 0 });
        distribution.put(Integer.valueOf(Integer.MAX_VALUE), new int[]{ 0, 0, 0 });

        int maxcp = Integer.MIN_VALUE;

        System.out.println("TOTAL: " + entityLines.size());
        int valid = 0;
        for (final String entityLine : entityLines) {
            final String[] lineParts = StringUtils.split(entityLine, ":");
            final String entity = lineParts[0];
            if (!entity.endsWith(";")) {
                continue;
            }
            valid++;
            final String[] codepoints = StringUtils.split(lineParts[1],",");
            final String[] characters = StringUtils.split(lineParts[2],"\\");

            if (codepoints.length == 1 && characters.length == 1) {

                final int codepoint = Integer.parseInt(codepoints[0]);
                final Integer distributionKey = getDistributionKey(distribution, codepoint);
                final int[] values = distribution.get(distributionKey);
                values[0]++;

                maxcp = Math.max(maxcp, codepoint);

                System.out.println("html5References.addReference(" + codepoint +  ", \"" + entity + "\");");

            } else if (codepoints.length == 1 && characters.length == 2) {

                final int codepoint = Integer.parseInt(codepoints[0]);
                final Integer distributionKey = getDistributionKey(distribution, codepoint);
                final int[] values = distribution.get(distributionKey);
                values[1]++;

                maxcp = Math.max(maxcp, codepoint);

                System.out.println("html5References.addReference(" + codepoint +  ", \"" + entity + "\");");

            } else if (codepoints.length == 2 && characters.length == 2) {

                final int codepoint = Integer.parseInt(codepoints[0]);
                final Integer distributionKey = getDistributionKey(distribution, codepoint);
                final int[] values = distribution.get(distributionKey);
                values[2]++;

                maxcp = Math.max(maxcp, codepoint);
                maxcp = Math.max(maxcp, Integer.valueOf(codepoints[1]));

                System.out.println("html5References.addReference(" + codepoint +  ", " + codepoints[1] +  ", \"" + entity + "\");");

            } else {
                throw new RuntimeException(
                        "Invalid combination: codepoints: " + codepoints.length + ", characters: " + characters.length +
                                " for \"" + entity + "\"");
            }

        }

        System.out.println("Maximum codepoint: " + maxcp);
        System.out.println("VALID: " + valid);
        for (final Map.Entry<Integer,int[]> distEntry : distribution.entrySet()) {
            final String values = Arrays.toString(distEntry.getValue());
            System.out.println(String.format("%20s : %15s",
                    ("< " + Integer.toHexString(distEntry.getKey().intValue())),
                    values));
        }

    }


    public Html5Entities() {
        super();
    }


}

