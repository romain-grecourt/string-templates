package com.acme.app;

import com.acme.api.VNode;

import java.util.List;

import static com.acme.api.VNode.h;

public class Main {

    public static void main(String[] args) {
        System.out.println(new MyComponent().render());
    }

    private final List<String> names = List.of("Bob", "Alice");

    VNode render() {
        return h("""
                <ul :for={{ names }}>
                   <li>{{ it.toFooBar() }}</li>"
                </ul>
                """, names);
    }
}
