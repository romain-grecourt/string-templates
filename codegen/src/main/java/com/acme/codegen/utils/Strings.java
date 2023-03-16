package com.acme.codegen.utils;

import java.util.Collection;

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
    public static boolean startsWith(Collection<String> prefixes, String str) {
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
    public static boolean filter(String str, Collection<String> includes, Collection<String> excludes) {
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

    /**
     * Wrap the given string with the given token.
     *
     * @param str   string to wrap
     * @param token token
     * @return String
     */
    public static String wrap(String str, String token) {
        return token + str + token;
    }

    /**
     * Wrap the given string with quotes.
     *
     * @param str string to wrap
     * @return String
     */
    public static String quote(String str) {
        return Strings.wrap(str, "\"");
    }

    /**
     * Get the indentation of the last line in the given string.
     *
     * @param str string
     * @return indentation
     */
    public static String indentOf(String str) {
        int index = str.lastIndexOf('\n');
        int i = index;
        do {
            i++;
        } while (i < str.length() && Character.isWhitespace(str.charAt(i)));
        return " ".repeat(i - (index + 1));
    }

    /**
     * Test if a string is non {@code null} and non-empty.
     *
     * @param str string
     * @return {@code true} if valid, {@code false} otherwise
     */
    public static boolean isValid(String str) {
        return str != null && !str.isEmpty();
    }
}
