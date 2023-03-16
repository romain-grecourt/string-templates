package com.acme.codegen.dom;

import java.util.LinkedHashMap;
import java.util.Map;

import com.acme.codegen.utils.Strings;

import static com.acme.codegen.utils.Constants.BINDING_PREFIX;
import static com.acme.codegen.utils.Constants.BRANCH_KEYS;
import static com.acme.codegen.utils.Constants.CTRL_KEYS;
import static com.acme.codegen.utils.Constants.EVENT_PREFIX;
import static com.acme.codegen.utils.Constants.EXPR_PREFIXES;
import static com.acme.codegen.utils.Constants.FOR_KEY;

/**
 * Dom attributes.
 */
public final class DomAttrs {

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
    public Map<String, String> bindings() {
        if (dynamics == null) {
            LinkedHashMap<String, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : all.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.startsWith(BINDING_PREFIX) && !CTRL_KEYS.contains(key) && Strings.isValid(value)) {
                    result.put(key.substring(1), value);
                }
            }
            dynamics = result;
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
            LinkedHashMap<String, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : all.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!Strings.startsWith(EXPR_PREFIXES, key)) {
                    result.put(key, value);
                }
            }
            statics = result;
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
            LinkedHashMap<String, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : all.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!CTRL_KEYS.contains(key)) {
                    result.put(key, value);
                }
            }
            regulars = result;
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
            LinkedHashMap<String, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : all.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.startsWith(EVENT_PREFIX) && Strings.isValid(value)) {
                    result.put(key.substring(1), value);
                }
            }
            events = result;
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
            for (Map.Entry<String, String> entry : all.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (BRANCH_KEYS.contains(key)) {
                    if (!result.isEmpty()) {
                        throw new IllegalStateException(String.format(
                                "Only one of [%s] is allowed", String.join(",", BRANCH_KEYS)));
                    }
                    result.put(key, value);
                }
            }
            String forExpr = all.get(FOR_KEY);
            if (forExpr != null) {
                result.put(FOR_KEY, forExpr);
            }
            controls = result;
        }
        return controls;
    }
}
