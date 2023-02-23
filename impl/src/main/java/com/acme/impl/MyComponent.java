package com.acme.impl;

import java.util.List;
import com.acme.api.VNode;

import static com.acme.api.VNodeProcessor.HTML;
import static com.acme.api.VNodeProcessor.forEach;

public class MyComponent {

    private List<String> names = List.of("Bob", "Alice");

    public VNode render() {
        return HTML."""
                <ul>
                \{ 
                    forEach(names, it -> HTML."""
                            <li>\{it}</li>
                        """)
                }</ul>
                """;
    }
}
