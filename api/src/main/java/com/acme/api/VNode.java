package com.acme.api;

public class VNode {

    private final String html;

    VNode(String html) {
        this.html = html;
    }

    VNode(VNode... nodes) {
        StringBuilder sb = new StringBuilder();
        for (VNode node : nodes) {
            sb.append(node.html);
        }
        this.html = sb.toString();
    }

    @Override
    public String toString() {
        return html;
    }
}
