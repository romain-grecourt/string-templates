package com.acme.api.vdom;

import com.acme.api.Component;

public class VComponent extends VElement {

    private final transient Component component;

    public VComponent(Component component) {
        super(component.getClass().getName());
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
