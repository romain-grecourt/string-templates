package com.acme.codegen.utils;

/**
 * Pair.
 *
 * @param first  first
 * @param second second
 * @param <K>    first type
 * @param <V>    second type
 */
public record Pair<K, V>(K first, V second) {
}
