package com.acme.codegen.utils;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * {@link Predicate} utility.
 */
public final class Predicates {

    private Predicates() {
        // cannot be instantiated
    }

    /**
     * Combine the given predicates into a bi-predicate.
     *
     * @param first  first predicate
     * @param second second predicate
     * @param <T>    the type of the first argument to the predicate
     * @param <U>    the type of the second argument the predicate
     * @return BiPredicate
     */
    public static <T, U> BiPredicate<T, U> combine(Predicate<T> first, Predicate<U> second) {
        return (t, u) -> first.test(t) && second.test(u);
    }
}
