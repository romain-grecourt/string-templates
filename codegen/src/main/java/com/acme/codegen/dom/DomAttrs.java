package com.acme.codegen.dom;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.acme.codegen.utils.Maps;
import com.acme.codegen.utils.Predicates;
import com.acme.codegen.utils.Strings;

import static com.acme.codegen.utils.Constants.BRANCH_KEYS;
import static com.acme.codegen.utils.Constants.CTRL_KEYS;
import static com.acme.codegen.utils.Constants.EXPR_KEYS;
import static com.acme.codegen.utils.Constants.FOR_KEY;

/**
 * Dom attributes.
 */
public final class DomAttrs {

    private static final Predicate<String> REGULAR_FILTER = k -> !CTRL_KEYS.contains(k);
    private static final Predicate<String> BINDING_FILTER = k -> Strings.filter(k, List.of(":"), CTRL_KEYS);
    private static final Predicate<String> EVENTS_FILTER0 = k -> Strings.filter(k, List.of("@"), List.of());
    private static final Predicate<String> STATIC_FILTER = k -> Strings.filter(k, List.of(), EXPR_KEYS);
    private static final BiPredicate<String, String> DYNAMIC_FILTER = Predicates.combine(BINDING_FILTER, Strings::isValid);
    private static final BiPredicate<String, String> EVENTS_FILTER = Predicates.combine(EVENTS_FILTER0, Strings::isValid);

    private final Map<String, String> all;
    private Map<String, String> dynamics;
    private Map<String, String> statics;
    private Map<String, String> regulars;
    private Map<String, String> events;
    private Map<String, String> controls;

    /**
     * Create a new instance.
     *
     * @param all all attributes
     */
    public DomAttrs(Map<String, String> all) {
        this.all = all;
    }

    /**
     * Get the dynamic attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> dynamics() {
        if (dynamics == null) {
            dynamics = Maps.filter(all, DYNAMIC_FILTER, k -> k.substring(1));
        }
        return dynamics;
    }

    /**
     * Get the static attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> statics() {
        if (statics == null) {
            statics = Maps.filter(all, STATIC_FILTER);
        }
        return statics;
    }

    /**
     * Get the regular attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> regulars() {
        if (regulars == null) {
            regulars = Maps.filter(all, REGULAR_FILTER);
        }
        return regulars;
    }

    /**
     * Get the event attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> events() {
        if (events == null) {
            events = Maps.filter(all, EVENTS_FILTER, k -> k.substring(1));
        }
        return events;
    }

    /**
     * Get the control attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> controls() {
        if (controls == null) {
            LinkedHashMap<String, String> result = new LinkedHashMap<>();
            Map<String, String> branch = Maps.filter(result, BRANCH_KEYS::contains);
            if (branch.size() > 1) {
                throw new IllegalStateException(String.format(
                        "Only one of [%s] is allowed",
                        String.join(",", BRANCH_KEYS)));
            }
            result.putAll(branch);
            String forExpr = all.get(FOR_KEY);
            if (forExpr != null) {
                result.put(FOR_KEY, forExpr);
            }
            controls = result;
        }
        return controls;
    }
}
