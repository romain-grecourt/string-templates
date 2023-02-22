package com.acme.impl;

import com.acme.api.Component;
import com.acme.api.VNode;

import static com.acme.api.VNodeProcessor.HTML;

@Component
public class MyComponent {

    private String name = "John";

    public VNode render() {
        return HTML."<div>hello \{name}</div>";
    }
}
