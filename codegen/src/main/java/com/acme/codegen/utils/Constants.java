package com.acme.codegen.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Constants.
 */
public final class Constants {

    private Constants() {
        // cannot be instantiated
    }

    /**
     * All HTML tags.
     */
    public static final List<String> HTML_TAGS = Arrays.asList(
            "html",
            "head",
            "styles",
            "body",
            "button",
            "div",
            "p",
            "ul",
            "li",
            "span",
            "b",
            "br",
            "h1",
            "h2",
            "h3",
            "h4",
            "h5",
            "h6",
            "i");

    /**
     * The attribute key for the for-loop control.
     */
    public static final String FOR_KEY = ":for";

    /**
     * The attribute key for the if-statement control.
     */
    public static final String IF_KEY = ":if";

    /**
     * The attribute key for the else-statement control.
     */
    public static final String ELSE_KEY = ":else";

    /**
     * The attribute key for the else-if-statement control.
     */
    public static final String ELSE_IF_KEY = ":else-if";

    /**
     * The branch attribute keys.
     */
    public static final Set<String> BRANCH_KEYS = Set.of(IF_KEY, ELSE_KEY, ELSE_IF_KEY);

    /**
     * The control attribute keys.
     */
    public static final Set<String> CTRL_KEYS = Set.of(FOR_KEY, IF_KEY, ELSE_KEY, ELSE_IF_KEY);

    /**
     * The attribute key prefixes for expression attributes.
     */
    public static final Set<String> EXPR_KEYS = Set.of(":", "@");
}
