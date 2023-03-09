package com.acme.api.vdom;

import com.acme.api.dom.events.EventListener;
import com.acme.api.dom.events.KeyboardEvent;
import com.acme.api.dom.events.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
