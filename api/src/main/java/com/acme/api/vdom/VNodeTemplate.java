package com.acme.api.vdom;

/**
 * VNode template.
 */
public interface VNodeTemplate {

    /**
     * Render the template.
     *
     * @param args template arguments
     * @return rendered {@link VNode}
     */
    VNode render(Object... args);
}
