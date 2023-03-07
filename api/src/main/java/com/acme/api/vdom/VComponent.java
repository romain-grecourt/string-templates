package com.acme.api.vdom;

import com.acme.api.Component;

/**
 * {@link VNodeSupplier} backed by a {@link Component}.
 */
public final class VComponent implements VNodeSupplier {

    private final transient Component component;

    private VComponent(Component component) {
        this.component = component;
    }

    /**
     * Create a new instance.
     *
     * @param component component
     */
    public static VComponent create(Component component) {
        return new VComponent(component);
    }

    /**
     * Get the component.
     *
     * @return Component
     */
    public Component getComponent() {
        return component;
    }

    @Override
    public VNode get() {
        return component.render();
    }
}
