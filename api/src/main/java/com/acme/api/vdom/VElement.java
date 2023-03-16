package com.acme.api.vdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.acme.api.dom.events.EventListener;
import com.acme.api.dom.events.KeyboardEvent;
import com.acme.api.dom.events.MouseEvent;

/**
 * Tree {@link VNode}.
 */
@SuppressWarnings("unused")
public final class VElement implements VNode {

    private final String tag;
    private final Map<String, String> attributes = new HashMap<>();
    private final List<VNode> children = new ArrayList<>();
    private EventListener<KeyboardEvent> onKeyUp;
    private EventListener<MouseEvent> onClick;

    private VElement(String tag) {
        this.tag = tag;
    }

    /**
     * Unwrap the given {@link VNode} into a {@link VElement}.
     *
     * @param node node
     * @return VElement
     */
    public static VElement unwrap(VNode node) {
        if (node instanceof VNodeSupplier) {
            while (node instanceof VNodeSupplier) {
                node = ((VNodeSupplier) node).get();
            }
        } else if (!(node instanceof VElement)) {
            throw new IllegalArgumentException("Unsupported node type: " + node.getClass());
        }
        return (VElement) node;
    }

    /**
     * Create a new instance.
     *
     * @param tag tag
     * @return VElement
     */
    public static VElement create(String tag) {
        return new VElement(tag);
    }

    /**
     * Set an attribute.
     *
     * @param key   attribute name
     * @param value attribute value
     * @return this instance
     */
    public VElement attr(String key, String value) {
        attributes.put(key, value);
        return this;
    }

    /**
     * Set an attribute.
     *
     * @param key   attribute name
     * @param value attribute value
     * @return this instance
     */
    public VElement attr(String key, List<String> value) {
        StringBuilder sb;
        if (key.equals("class")) {
            sb = new StringBuilder();
            for (String v : value) {
                if (!sb.isEmpty()) {
                    sb.append(" ");
                }
                sb.append(v);
            }
        } else {
            throw new UnsupportedOperationException("Map attribute value not supported");
        }
        attributes.put(key, sb.toString());
        return this;
    }

    /**
     * Set an attribute.
     *
     * @param key   attribute name
     * @param value attribute value
     * @return this instance
     */
    public VElement attr(String key, Map<String, ? extends Object> value) {
        StringBuilder sb;
        switch (key) {
            case "class" -> {
                sb = new StringBuilder();
                value.forEach((k, v) -> {
                    if (v instanceof Boolean) {
                        if ((Boolean) v) {
                            if (!sb.isEmpty()) {
                                sb.append(" ");
                            }
                            sb.append(k);
                        }
                    }
                });
            }
            case "style" -> {
                sb = new StringBuilder();
                value.forEach((k, v) -> {
                    if (v instanceof String) {
                        sb.append(k)
                          .append(": ")
                          .append((String) v)
                          .append(";");
                    }
                });
            }
            default -> throw new UnsupportedOperationException("Map attribute value not supported");
        }
        attributes.put(key, sb.toString());
        return this;
    }

    /**
     * Set the {@code class} attribute.
     *
     * @param classes classes
     * @return this instance
     */
    public VElement classes(String... classes) {
        attributes.put("class", String.join(" ", classes).trim());
        return this;
    }

    /**
     * Add a child.
     *
     * @param child child
     * @return this instance
     */
    public VElement child(VNode child) {
        this.children.add(child);
        return this;
    }

    /**
     * Add a child.
     *
     * @param child supplier of child
     * @return this instance
     */
    public VElement child(Supplier<VNode> child) {
        this.children.add(child.get());
        return this;
    }

    /**
     * Add children.
     *
     * @param children supplier of children
     * @return this instance
     */
    public VElement children(List<VNode> children) {
        this.children.addAll(children);
        return this;
    }

    /**
     * Add children.
     *
     * @param children supplier of children
     * @return this instance
     */
    public VElement children(Supplier<List<VNode>> children) {
        this.children.addAll(children.get());
        return this;
    }

    /**
     * Add a text node.
     *
     * @param text text
     * @return this instance
     */
    public VElement text(String text) {
        this.children.add(VText.create(text));
        return this;
    }

    /**
     * Set the {@code keyUp} event handler.
     *
     * @param handler event handler
     * @return this instance
     */
    public VElement onKeyUp(EventListener<KeyboardEvent> handler) {
        this.onKeyUp = handler;
        return this;
    }

    /**
     * Set the {@code click} event handler.
     *
     * @param handler event handler
     * @return this instance
     */
    public VElement onClick(EventListener<MouseEvent> handler) {
        this.onClick = handler;
        return this;
    }

    /**
     * Get the tag.
     *
     * @return tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Get the attributes map.
     *
     * @return attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Get the {@code keyUp} event handler.
     *
     * @return EventListener
     */
    public EventListener<KeyboardEvent> getOnKeyUp() {
        return onKeyUp;
    }

    /**
     * Get the {@code click} event handler.
     *
     * @return EventListener
     */
    public EventListener<MouseEvent> getOnClick() {
        return onClick;
    }

    /**
     * Get the children.
     *
     * @return children
     */
    public List<VNode> getChildren() {
        return children;
    }
}
