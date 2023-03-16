package com.acme.codegen.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    /**
     * Filter the given map with the given predicate.
     *
     * @param map       map to filter
     * @param predicate predicate
     * @param <K>       key type
     * @param <V>       value type
     * @return new map
     */
    public static <K, V> Map<K, V> filter(Map<K, V> map, BiPredicate<K, V> predicate, Function<K, K> mapper) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> predicate.test(entry.getKey(), entry.getValue()))
                  .collect(Collectors.toMap(e -> mapper.apply(e.getKey()), Map.Entry::getValue));
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
