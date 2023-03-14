package com.acme.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.acme.api.Component;
import com.acme.api.dom.events.Event;
import com.acme.api.vdom.VNode;

//import static com.acme.api.vdom.VNodeCompiler.f;
import static com.acme.api.vdom.VNodeCompiler.h;

public class MyComponent extends Component {

    private final List<String> names = List.of("Bob", "Alice");

    private boolean bold;
    private Map<String, String> styles = new HashMap<>();
    private List<String> classes = new ArrayList<>();

    static <T extends List<Map<String, Integer>>, R extends Set<?>> T test(List<R> r) {
        return null;
    }

    static <T> T ok() {
        return null;
    }

    @Override
    public VNode render() {
        // What is actually done at runtime...
        // return new VNodeTemplateRef<>(MyComponent_P601::new, t -> t.render(this.names, (Consumer<Event>) this::onClick));
        boolean pretty = false;
        boolean ugly = true;
        return h("""
                <div>
                    <!--<my-title
                        :bold="{{ bold }}"
                        :show={{ false }}
                    >
                    The title
                    </my-title>
                    <h1 :if={{ pretty }}>Names</h1>
                    <h1 :else-if={{ !ugly }}>Boo!</h1>
                    <h1 :else>Well..</h1> -->
                    <p :style={{ Map.of("border",  "1px solid red") }}>Oki</p>
                    <p :style={{ styles }}>No!</p>
                    <p :class={{ List.of("red", "bold") }}>Yes!</p>
                    <p :class={{ classes }}>Why!</p>
                    <p :class={{ classes() }}>Why!</p>
                    <h1 :else>Yuck!</h1>
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
                """);
    }

    private List<String> classes() {
        List<String> cls = new ArrayList<>();
        if (bold) {
            cls.add("bold");
        }
        return cls;
    }

    private void onClick(Event event) {
    }
}
