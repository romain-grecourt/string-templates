package com.acme.api.vdom;

public class VText extends VNode {
    private String text;

    public static  VText create(String text) {
        VText node = new VText();
        node.text = text;
        return node;
    }

    public String getText() {
        return text;
    }
}
