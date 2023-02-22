package com.acme.impl;

import com.acme.api.VNode;

import static com.acme.api.VNodeProcessor.HTML;

public class MyComponent {

    private String name = "John";

    public VNode render() {
        return HTML."<div>hello \{name}</div>";
    }
}
