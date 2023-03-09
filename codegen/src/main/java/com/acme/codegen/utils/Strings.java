package com.acme.codegen.utils;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * String utility.
 */
public final class Strings {

    private Strings() {
        // cannot be instantiated
    }

    /**
     * Test if the given string is prefixed with any of the given prefixes.
     *
     * @param prefixes list of prefix
     * @param str      string to match
     * @return {@code true} if matched, {@code false} otherwise
     */
    public static boolean startsWith(List<String> prefixes, String str) {
        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test if the given string is included.
     *
     * @param str      string to match
     * @param includes list of include prefixes
     * @param excludes list of exclude prefixes
     * @return {@code true} if matched, {@code false} otherwise
     */
    public static boolean filter(String str, List<String> includes, List<String> excludes) {
        return !startsWith(excludes, str) && (includes.isEmpty() || startsWith(includes, str));
    }

    /**
     * Split the lines in the given string and join them with the given delimiter.
     *
     * @param delim delimiter
     * @param str   string to process
     * @return result string
     */
    public static String indent(String delim, String str) {
        return str.lines().collect(joining("\n" + delim));
    }
}
