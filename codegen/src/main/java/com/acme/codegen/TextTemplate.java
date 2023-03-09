package com.acme.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * Text template.
 *
 * @param fragments fragments
 * @param exprs     expressions
 */
record TextTemplate(List<String> fragments, List<String> exprs) {

    /**
     * Create a new instance.
     *
     * @param template raw template
     * @return TextTemplate
     */
    static TextTemplate create(String template) {
        char[] chars = template.toCharArray();
        int state = 0;
        List<String> fragments = new ArrayList<>();
        List<String> exprs = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (i + 1 < chars.length) {
                switch (state) {
                    case 0 -> {
                        if (c == '{' && chars[i + 1] == '{') {
                            fragments.add(buf.toString());
                            buf.setLength(0);
                            i++;
                            state = 1; // expr
                            continue;
                        }
                    }
                    case 1 -> {
                        if (c == '}' && chars[i + 1] == '}') {
                            if (!buf.isEmpty()) {
                                exprs.add(buf.toString().trim());
                                buf.setLength(0);
                            }
                            i++;
                            state = 0;
                            continue;
                        }
                    }
                }
            }
            buf.append(c);
        }
        fragments.add(buf.toString());
        return new TextTemplate(fragments, exprs);
    }
}
