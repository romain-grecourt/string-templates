package com.acme.api;

import java.lang.template.StringTemplate;
import java.lang.template.TemplateProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class VNodeProcessor {

    private VNodeProcessor() {
    }

    public static <T> VNode forEach(Iterable<T> iterable, Function<T, VNode> function) {
        List<VNode> vNodes = new ArrayList<>();
        for (T item : iterable) {
            vNodes.add(function.apply(item));
        }
        return new VNode(vNodes.toArray(new VNode[0]));
    }

    public static TemplateProcessor<VNode> HTML = (StringTemplate st) -> {
        List<String> fragments = st.fragments();
        List<Object> values = st.values();
        int fragmentsSize = fragments.size();
        int valuesSize = values.size();
        if (fragmentsSize != valuesSize + 1) {
            throw new IllegalArgumentException("fragments must have one more element than values");
        }
        return interpolate(fragments, values);
    };

    private static VNode interpolate(List<String> fragments, List<Object> values) {
        // FIXME (this is a dummy implementation)
        int fragmentsSize = fragments.size();
        VNode[] vNodes = new VNode[fragmentsSize];
        int i = 0;
        if (fragmentsSize > 1) {
            for (; i < fragmentsSize - 1; i++) {
                VNodeTemplate tpl = VNodeTemplateProvider.get(fragments.get(i));
                Object value = values.get(i);
                if (value instanceof VNode) {
                    vNodes[i] = tpl.toVNode((VNode) value);
                } else {
                    vNodes[i] = tpl.toVNode(String.valueOf(values.get(i)));
                }
            }
        }
        vNodes[i] = VNodeTemplateProvider.get(fragments.get(i)).toVNode();
        return new VNode(vNodes);
    }
}
