package com.acme.api.vdom;

/**
 * Text {@link VNode}.
 */
public final class VText implements VNode {

    private String text;

    private VText(String text) {
        this.text = text;
    }

    /**
     * Create a new instance.
     *
     * @param text text
     * @return VText
     */
    public static VText create(String text) {
        return new VText(text);
    }

    public String getText() {
        return text;
    }
}
