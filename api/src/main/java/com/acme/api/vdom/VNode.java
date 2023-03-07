package com.acme.api.vdom;

public abstract class VNode {
    private VNode parent;

    public VNode getParent() {
        return parent;
    }
}
