package com.acme.api;

import com.acme.api.vdom.VNode;

/**
 * UI component.
 */
public abstract class Component {

    /**
     * Render the UI component.
     *
     * @return VNode
     */
    public abstract VNode render();

    /**
     * Compile the given template.
     * This method invocation will be replaced with generated code.
     *
     * @param template template text
     * @return VNode
     */
    protected static VNode h(String template) {
        throw new UnsupportedOperationException("Template not compiled: \n" + template);
    }
}
