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
 *
 * @param all all attributes
 */
public record DomAttrs(Map<String, String> all) {

    private static final Predicate<String> REGULAR_FILTER = k -> !CTRL_KEYS.contains(k);
    private static final Predicate<String> BINDING_FILTER = k -> Strings.filter(k, List.of(":"), CTRL_KEYS);
    private static final Predicate<String> STATIC_FILTER = k -> Strings.filter(k, List.of(), EXPR_KEYS);
    private static final BiPredicate<String, String> DYNAMIC_FILTER = Predicates.combine(BINDING_FILTER, Strings::isValid);

    /**
     * Get the dynamic attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> dynamics() {
        return Maps.filter(all, DYNAMIC_FILTER, k -> k.substring(1));
    }

    /**
     * Get the static attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> statics() {
        return Maps.filter(all, STATIC_FILTER);
    }

    /**
     * Get the regular attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> regulars() {
        return Maps.filter(all, REGULAR_FILTER);
    }

    /**
     * Get the control attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> controls() {
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
        return result;
    }
}
