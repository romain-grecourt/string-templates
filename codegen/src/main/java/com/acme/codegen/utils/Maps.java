package com.acme.codegen.utils;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Map utility.
 */
public final class Maps {

    private Maps() {
        // cannot be instantiated
    }

    /**
     * Test if the given map contains any of the given keys.
     *
     * @param map  map
     * @param keys keys
     * @param <K>  key type
     * @param <V>  value type
     * @return {@code true} if matched, {@code false} otherwise
     */
    public static <K, V> boolean containsKeys(Map<K, V> map, Iterable<K> keys) {
        for (K key : keys) {
            if (map.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the first entry that matches any of the given keys.
     *
     * @param map  map
     * @param keys keys
     * @param <K>  key type
     * @param <V>  value type
     * @return {@code null} if not found
     */
    public static <K, V> Map.Entry<K, V> first(Map<K, V> map, Iterable<K> keys) {
        for (K key : keys) {
            V value = map.get(key);
            if (value != null) {
                return Map.entry(key, value);
            }
        }
        return null;
    }

    /**
     * Filter the given map with the given predicate.
     *
     * @param map       map to filter
     * @param predicate predicate
     * @param <K>       key type
     * @param <V>       value type
     * @return new map
     */
    public static <K, V> Map<K, V> filter(Map<K, V> map, Predicate<K> predicate) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> predicate.test(entry.getKey()))
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
