package com.acme.app;

import com.acme.api.Component;
import com.acme.api.dom.events.Event;
import com.acme.api.vdom.VNode;

import java.util.List;

public class NameList extends Component {

    private final List<String> names;

    public NameList(List<String> names) {
        this.names = names;
    }

    @Override
    public VNode render() {
        return h("""
                <ul class="outlined">
                   <li
                     :for={{ var name : names }}
                     @click={{ this::onClick }}
                   >
                       <span>Name: {{ name }}.</span>
                   </li>
                </ul>
                """);
    }

    private void onClick(Event event) {
    }
}
