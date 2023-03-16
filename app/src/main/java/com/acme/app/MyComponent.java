package com.acme.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.acme.api.Component;
import com.acme.api.dom.events.Event;
import com.acme.api.vdom.VElement;
import com.acme.api.vdom.VNode;

@SuppressWarnings("unused")
public class MyComponent extends Component {

    private final List<String> names = List.of("Bob", "Alice");

    private boolean bold;
    private Map<String, String> styles = new HashMap<>();
    private List<String> classes = List.of("foo", "bar");

    static <T extends List<Map<String, Integer>>, R extends Set<?>> T test(List<R> r) {
        return null;
    }

    static <T> T ok() {
        return null;
    }

    private static String OK = "ok";
    private static final VNode TEST = h("<span>## {{ OK }} ##</span>");

    private String ugh = "ugh?";
    private final VNode test = h("<p>?? {{ ugh }} ??</p>");

    private boolean state1;
    private boolean state2 = true;

    @Override
    public VNode render() {
        boolean pretty = false;
        boolean ugly = true;
        VNode vNode = processNode(h("""
                <div>
                    <!-- <component :name={{ myComponent }} /> -->
                    <!--<my-title
                        :bold={{ bold }}
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
                    <h1 :if={{ state1 }}>Hello</h1>
                    <h1 :else-if={{ state2 }}>Bonjour</h1>
                    <h1 :else>Yuck!</h1>
                    <h2>Well..</h2>
                    <br/>
                    <NameList :names={{ names }} />
                </div>
                """));
        if (vNode instanceof VElement) {
            System.getenv("test");
        }
        return vNode;
    }

    private static VNode processNode(VNode v) {
        return v;
    }

    private List<String> classes() {
        List<String> cls = new ArrayList<>();
        if (bold) {
            cls.add("bold");
        } else {
            cls.add("not-bold");
        }
        return cls;
    }

    private void onClick(Event event) {
    }
}
