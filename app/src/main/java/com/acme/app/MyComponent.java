package com.acme.app;

import java.util.List;

import com.acme.api.Component;
import com.acme.api.vdom.VNode;

import static com.acme.api.vdom.VNodeCompiler.h;

public class MyComponent extends Component {

    private final List<String> names = List.of("Bob", "Alice");

    @Override
    public VNode render() {
        return h("""
                <ul>
                   <li :for={{ var name : names }}>
                       <span>{{ name }</span>
                   </li>
                </ul>
                """, names);
    }
}
