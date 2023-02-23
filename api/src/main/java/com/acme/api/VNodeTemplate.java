package com.acme.api;

public class VNodeTemplate {

    private final String html;

    public VNodeTemplate(String html) {
        this.html = html;
    }

    VNode toVNode() {
        return new VNode(html);
    }

    VNode toVNode(VNode value) {
        return new VNode(new VNode(html), value);
    }

    VNode toVNode(String value) {
        return new VNode(html + value);
    }
}
