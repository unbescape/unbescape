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

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

        final Map<String, List<String>> references = new TreeMap<String, List<String>>(new Comparator<String>() {
            public int compare(final String o1, final String o2) {
                return o1.compareTo(o2);
            }
        });

        int valid = 0;
        for (final String entityLine : entityLines) {
            final String[] lineParts = StringUtils.split(entityLine, ":");
            final String entity = lineParts[0];
//            if (!entity.endsWith(";")) {
//                continue;
//            }
            valid++;
            final String[] codepoints = StringUtils.split(lineParts[1],",");
            final String[] characters = StringUtils.split(lineParts[2],"\\");

            if (codepoints.length == 1 && characters.length == 1) {

                final int codepoint = Integer.parseInt(codepoints[0]);
                final Integer distributionKey = getDistributionKey(distribution, codepoint);
                final int[] values = distribution.get(distributionKey);
                values[0]++;

                maxcp = Math.max(maxcp, codepoint);

//                System.out.println("html5References.addReference(" + codepoint +  ", \"" + entity + "\");");

                final String referencesKey = String.format("%6d",Integer.valueOf(codepoint));
                List<String> referencesForCodepoints = references.get(referencesKey);
                if (referencesForCodepoints == null) {
                    referencesForCodepoints = new ArrayList<String>();
                    references.put(referencesKey, referencesForCodepoints);
                }
                referencesForCodepoints.add(entity);
                Collections.sort(referencesForCodepoints, new Comparator<String>() {
                    public int compare(final String o1, final String o2) {
                        final int o1Search4 =
                                Arrays.binarySearch(HtmlEscapeSymbols.HTML4_SYMBOLS.SORTED_NCRS, o1.toCharArray(), new Comparator<char[]>() {
                                    public int compare(final char[] o1, final char[] o2) {
                                        return new String(o1).compareTo(new String(o2));
                                    }
                                });
                        final int o2Search4 =
                                Arrays.binarySearch(HtmlEscapeSymbols.HTML4_SYMBOLS.SORTED_NCRS, o2.toCharArray(), new Comparator<char[]>() {
                                    public int compare(final char[] o1, final char[] o2) {
                                        return new String(o1).compareTo(new String(o2));
                                    }
                                });
                        if (o1Search4 >= 0) {
                            return -1;
                        }
                        if (o2Search4 >= 0) {
                            return 1;
                        }

                        final boolean o1low = Character.isLowerCase(o1.charAt(1));
                        final boolean o2low = Character.isLowerCase(o2.charAt(1));
                        if (o1low && !o2low) {
                            return -1;
                        } else if (o2low && !o1low) {
                            return 1;
                        }
                        return o1.compareTo(o2);
                    }
                });

            } else if (codepoints.length == 1 && characters.length == 2) {

                final int codepoint = Integer.parseInt(codepoints[0]);
                final Integer distributionKey = getDistributionKey(distribution, codepoint);
                final int[] values = distribution.get(distributionKey);
                values[1]++;

                maxcp = Math.max(maxcp, codepoint);

//                System.out.println("html5References.addReference(" + codepoint +  ", \"" + entity + "\");");

                final String referencesKey = String.format("%6d", Integer.valueOf(codepoint));
                List<String> referencesForCodepoints = references.get(referencesKey);
                if (referencesForCodepoints == null) {
                    referencesForCodepoints = new ArrayList<String>();
                    references.put(referencesKey, referencesForCodepoints);
                }
                referencesForCodepoints.add(entity);
                Collections.sort(referencesForCodepoints, new Comparator<String>() {
                    public int compare(final String o1, final String o2) {
                        final int o1Search4 =
                                Arrays.binarySearch(HtmlEscapeSymbols.HTML4_SYMBOLS.SORTED_NCRS, o1.toCharArray(), new Comparator<char[]>() {
                                    public int compare(final char[] o1, final char[] o2) {
                                        return new String(o1).compareTo(new String(o2));
                                    }
                                });
                        final int o2Search4 =
                                Arrays.binarySearch(HtmlEscapeSymbols.HTML4_SYMBOLS.SORTED_NCRS, o2.toCharArray(), new Comparator<char[]>() {
                                    public int compare(final char[] o1, final char[] o2) {
                                        return new String(o1).compareTo(new String(o2));
                                    }
                                });
                        if (o1Search4 >= 0) {
                            return -1;
                        }
                        if (o2Search4 >= 0) {
                            return 1;
                        }

                        final boolean o1low = Character.isLowerCase(o1.charAt(1));
                        final boolean o2low = Character.isLowerCase(o2.charAt(1));
                        if (o1low && !o2low) {
                            return -1;
                        } else if (o2low && !o1low) {
                            return 1;
                        }
                        return o1.compareTo(o2);
                    }
                });

            } else if (codepoints.length == 2 && characters.length == 2) {

                final int codepoint = Integer.parseInt(codepoints[0]);
                final Integer distributionKey = getDistributionKey(distribution, codepoint);
                final int[] values = distribution.get(distributionKey);
                values[2]++;

                maxcp = Math.max(maxcp, codepoint);
                maxcp = Math.max(maxcp, Integer.valueOf(codepoints[1]));

//                System.out.println("html5References.addReference(" + codepoint +  ", " + codepoints[1] +  ", \"" + entity + "\");");

                final String referencesKey = String.format("%6d", Integer.valueOf(codepoints[0])) + "," +
                                             String.format("%6d", Integer.valueOf(codepoints[1]));
                List<String> referencesForCodepoints = references.get(referencesKey);
                if (referencesForCodepoints == null) {
                    referencesForCodepoints = new ArrayList<String>();
                    references.put(referencesKey, referencesForCodepoints);
                }
                referencesForCodepoints.add(entity);
                Collections.sort(referencesForCodepoints, new Comparator<String>() {
                    public int compare(final String o1, final String o2) {
                        final int o1Search4 =
                                Arrays.binarySearch(HtmlEscapeSymbols.HTML4_SYMBOLS.SORTED_NCRS, o1.toCharArray(), new Comparator<char[]>() {
                                    public int compare(final char[] o1, final char[] o2) {
                                        return new String(o1).compareTo(new String(o2));
                                    }
                                });
                        final int o2Search4 =
                                Arrays.binarySearch(HtmlEscapeSymbols.HTML4_SYMBOLS.SORTED_NCRS, o2.toCharArray(), new Comparator<char[]>() {
                                    public int compare(final char[] o1, final char[] o2) {
                                        return new String(o1).compareTo(new String(o2));
                                    }
                                });
                        if (o1Search4 >= 0) {
                            return -1;
                        }
                        if (o2Search4 >= 0) {
                            return 1;
                        }

                        final boolean o1low = Character.isLowerCase(o1.charAt(1));
                        final boolean o2low = Character.isLowerCase(o2.charAt(1));
                        if (o1low && !o2low) {
                            return -1;
                        } else if (o2low && !o1low) {
                            return 1;
                        }
                        return o1.compareTo(o2);
                    }
                });

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


        System.out.println("-----");
        System.out.println("HTML4");
        System.out.println("-----");
        for (int i = 0; i <= 0x7f; i++) {
            if (HtmlEscapeSymbols.HTML4_SYMBOLS.NCRS_BY_CODEPOINT[i] != HtmlEscapeSymbols.HTML4_SYMBOLS.NO_NCR) {
                System.out.println(String.format("%5d ", Integer.valueOf(i)) + new String(HtmlEscapeSymbols.HTML4_SYMBOLS.SORTED_NCRS[HtmlEscapeSymbols.HTML4_SYMBOLS.NCRS_BY_CODEPOINT[i]]));
            }
        }


        System.out.println("-----");
        System.out.println("HTML5");
        System.out.println("-----");
        for (int i = 0; i <= 0x7f; i++) {
            if (HtmlEscapeSymbols.HTML5_SYMBOLS.NCRS_BY_CODEPOINT[i] != HtmlEscapeSymbols.HTML5_SYMBOLS.NO_NCR) {
                System.out.println(String.format("%5d ", Integer.valueOf(i)) + new String(HtmlEscapeSymbols.HTML5_SYMBOLS.SORTED_NCRS[HtmlEscapeSymbols.HTML5_SYMBOLS.NCRS_BY_CODEPOINT[i]]));
            } else {
                System.out.println(String.format("%5d ", Integer.valueOf(i)) + (char)i);
            }
        }


        int count = 0;
        for (final Map.Entry<String,List<String>> referencesEntry : references.entrySet()) {
            final String key = referencesEntry.getKey();
            final List<String> values = referencesEntry.getValue();
            for (final String value : values) {
                System.out.println("html5References.addReference(" + key +  ", \"" + value + "\");");
                count++;
            }
        }
        System.out.println("COUNT: " +  count);


    }


    public Html5Entities() {
        super();
    }


}

