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
                <div>
                    <h1>Names</h1>
                    <br/>
                    <ul class="outlined">
                       <li
                         :for={{ var name : names }}
                         @click={{ this::onClick }}
                       >
                           <span>Name: {{ name }}.</span>
                       </li>
                    </ul>
                </div>
                """, names);
    }
}
