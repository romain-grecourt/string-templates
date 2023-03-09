package com.acme.codegen.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * List utility.
 */
public final class Lists {

    private Lists() {
        // cannot be instantiated
    }

    /**
     * Concatenate the given lists.
     *
     * @param l1  first list
     * @param l2  second list
     * @param <T> param type
     * @return {@code null} if both arguments are {@code null}
     */
    public static <T> List<T> concat(List<T> l1, List<T> l2) {
        List<T> result = new ArrayList<>();
        if (l1 != null) {
            result.addAll(l1);
            if (l2 != null) {
                result.addAll(l2);
            }
            return l1;
        }
        if (l2 != null) {
            result.addAll(l2);
            return result;
        }
        return null;
    }
}
