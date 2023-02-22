package com.acme.api;

public class VNode {

    private final String html;

    VNode(String html) {
        this.html = html;
    }

    @Override
    public String toString() {
        return html;
    }
}
