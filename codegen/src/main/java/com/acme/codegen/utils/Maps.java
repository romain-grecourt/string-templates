package com.acme.codegen.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

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
     * Map the given map values.
     *
     * @param map    input map
     * @param mapper mapping function
     * @param <K>    key type
     * @param <V>    original value type
     * @param <X>    mapped value type
     * @return new map
     */
    public static <K, V, X> Map<K, X> mapValue(Map<K, V> map, Function<V, X> mapper) {
        return map.entrySet()
                  .stream()
                  .collect(toMap(Map.Entry::getKey, e -> mapper.apply(e.getValue())));
    }

    /**
     * Combine the given maps.
     *
     * @param first  first map
     * @param second second map
     * @param <K>    key type
     * @param <V>    value type
     * @return first
     */
    public static <K, V> Map<K, V> combine(Map<K, V> first, Map<K, V> second) {
        second.forEach(first::putIfAbsent);
        return first;
    }

    /**
     * Get the values for the given keys in the map.
     *
     * @param map  map
     * @param keys keys
     * @param <K>  key type
     * @param <V>  value type
     * @return list
     */
    public static <K, V> List<V> values(Map<K, V> map, Collection<K> keys) {
        List<V> result = new ArrayList<>();
        for (K key : keys) {
            result.add(map.get(key));
        }
        return result;
    }
}
