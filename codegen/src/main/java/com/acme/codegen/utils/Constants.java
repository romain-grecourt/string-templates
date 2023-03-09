package com.acme.codegen.utils;

import java.util.Arrays;
import java.util.List;

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
     * The flow control attribute keys.
     */
    public static final List<String> CTRL_KEYS = List.of(FOR_KEY, IF_KEY, ELSE_KEY, ELSE_IF_KEY);

    /**
     * The attribute key prefixes for expression attributes.
     */
    public static final List<String> EXPR_KEYS = List.of(":", "@");
}
