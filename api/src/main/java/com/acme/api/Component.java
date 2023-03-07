package com.acme.api;

import com.acme.api.vdom.VNode;

/**
 * UI component.
 */
public abstract class Component {

    /**
     * Render the UI component.
     * @return VNode
     */
    public abstract VNode render();
}
