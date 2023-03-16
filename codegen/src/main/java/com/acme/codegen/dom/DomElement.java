package com.acme.codegen.dom;

import java.util.Map;

/**
 * Dom element.
 *
 * @param parent parent element, may be {@code null}
 * @param tag    tag
 * @param attrs  attributes
 * @param line   line number
 * @param col    column number
 */
public record DomElement(DomElement parent, String tag, DomAttrs attrs, int line, int col) {

    /**
     * Create a copy of this element with different attributes.
     *
     * @param attrs attributes
     * @return copy
     */
    public DomElement copy(Map<String, String> attrs) {
        return new DomElement(parent, tag, new DomAttrs(attrs), line, col);
    }
}
