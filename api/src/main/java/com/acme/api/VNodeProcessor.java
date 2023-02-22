package com.acme.api;

import java.lang.template.StringTemplate;
import java.lang.template.TemplateProcessor;

public final class VNodeProcessor {

    private VNodeProcessor() {
    }

    public static TemplateProcessor<VNode> HTML = (StringTemplate st) -> {
        return new VNode(st.interpolate());
    };
}
