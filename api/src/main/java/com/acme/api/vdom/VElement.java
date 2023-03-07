package com.acme.api.vdom;

import com.acme.api.dom.events.EventListener;
import com.acme.api.dom.events.KeyboardEvent;
import com.acme.api.dom.events.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class VElement extends VNode {

    private String tag;
    private Map<String, String> attributes = new HashMap<>();
    private List<VNode> children = new ArrayList<>();
    private EventListener<KeyboardEvent> onKeyUp;
    private EventListener<MouseEvent> onClick;

    public VElement(String tag) {
        this.tag = tag;
    }

    public VElement attr(String key, String value) {
        attributes.put(key, value);
        return this;
    }

    public VElement classes(String... classes) {
        attributes.put("class", String.join(" ", classes).trim());
        return this;
    }

    public VElement child(VNode child) {
        this.children.add(child);
        return this;
    }

    public VElement child(Supplier<VNode> child) {
        this.children.add(child.get());
        return this;
    }

    public VElement children(Supplier<List<VNode>> children) {
        this.children.addAll(children.get());
        return this;
    }

    public VElement text(String text) {
        this.children.add(VText.create(text));
        return this;
    }

    public VElement onKeyUp(EventListener<KeyboardEvent> handler) {
        this.onKeyUp = handler;
        return this;
    }

    public VElement onClick(EventListener<MouseEvent> handler) {
        this.onClick = handler;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public EventListener<KeyboardEvent> getOnKeyUp() {
        return onKeyUp;
    }

    public EventListener<MouseEvent> getOnClick() {
        return onClick;
    }

    public List<VNode> getChildren() {
        return children;
    }

}
