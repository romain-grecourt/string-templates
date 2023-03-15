package com.acme.codegen;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.acme.codegen.dom.DomElement;
import com.acme.codegen.dom.DomParser;
import com.acme.codegen.dom.DomReader;
import com.acme.codegen.utils.Maps;
import com.acme.codegen.utils.Pair;
import com.acme.codegen.utils.Strings;

import static com.acme.codegen.utils.Constants.CTRL_KEYS;
import static com.acme.codegen.utils.Constants.ELSE_KEY;
import static com.acme.codegen.utils.Constants.EXPR_KEYS;
import static com.acme.codegen.utils.Constants.FOR_KEY;
import static com.acme.codegen.utils.Constants.HTML_TAGS;
import static com.acme.codegen.utils.Constants.IF_KEY;

/**
 * {@link DomReader} implementation that generates source code.
 */
final class VNodeGenerator implements DomReader {

    private final Deque<DomElement> desc = new ArrayDeque<>();
    private final Deque<Pair<DomElement, String>> asc = new ArrayDeque<>();
    private DomParser parser;

    private void read(String is) throws IOException {
        parser = new DomParser(is, this);
        parser.parse();
    }

    /**
     * Generate the body of {@code VNodeTemplate.render}.
     *
     * @param is input string
     * @return code
     * @throws IOException if an IO error occurs
     */
    static String generate(String is) throws IOException {
        VNodeGenerator reader = new VNodeGenerator();
        reader.read(is);
        Pair<DomElement, String> result = reader.asc.pop();
        return result.second();
    }

    @Override
    public void startElement(String name, Map<String, String> attrs) {
        if (desc.isEmpty() && Maps.containsKeys(attrs, CTRL_KEYS)) {
            throw new IllegalStateException("Root element cannot use control attributes");
        }
        desc.push(new DomElement(desc.peek(), name, attrs, parser.lineNumber(), parser.charNumber()));
    }

    @Override
    public void elementText(String data) {
        if (data.isEmpty()) {
            return;
        }
        DomElement elt = desc.peek();
        if (elt == null) {
            throw new IllegalStateException("Text without parent");
        }
        TextTemplate st = TextTemplate.create(data);
        String out = st.interpolate(f -> Strings.wrap(f, "\""), Function.identity(), " + ");
        out = String.format("l%dc%d.text(%s);", elt.line(), elt.col(), out);
        this.asc.push(new Pair<>(elt, out));
    }

    @Override
    public void endElement(String name) {
        DomElement elt = desc.pop();
        Map<String, String> exprs = Maps.filter(elt.attrs(), k -> Strings.filter(k, EXPR_KEYS, List.of()));
        List<String> nestedList = new ArrayList<>();
        while (!asc.isEmpty()) {
            if (asc.peek().first() == elt) {
                nestedList.add(0, asc.pop().second());
            } else {
                break;
            }
        }
        String nested = String.join("\n", nestedList);
        String out;
        if (exprs.containsKey(FOR_KEY)) {
            String forExpr = exprs.get(FOR_KEY).trim();
            Map<String, String> newAttrs = Maps.filter(elt.attrs(), k -> Strings.filter(k, List.of(), CTRL_KEYS));
            String actual = node(elt.copy(newAttrs), nested);
            out = String.format("for (%s) {\n    %s\n}", forExpr, Strings.indent("    ", actual));
        } else if (exprs.containsKey(IF_KEY)) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else if (exprs.containsKey(ELSE_KEY)) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else {
            out = node(elt, nested);
        }
        this.asc.push(new Pair<>(desc.peek(), out));
    }

    private String node(DomElement elt, String nested) {
        Map<String, String> statics = Maps.filter(elt.attrs(), k -> Strings.filter(k, List.of(), EXPR_KEYS));
        String out;
        String varName = String.format("l%dc%d", elt.line(), elt.col());
        if (HTML_TAGS.contains(elt.tag())) {
            out = String.format("com.acme.api.vdom.VElement %s = com.acme.api.vdom.VElement.create(\"%s\");",
                    varName,
                    elt.tag());
            if (!statics.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : statics.entrySet()) {
                    sb.append(String.format("%s.attr(\"%s\", \"%s\");",
                            varName,
                            entry.getKey(),
                            entry.getValue()));
                }
                out += sb.toString();
            }
            if (!nested.isEmpty()) {
                out += "\n" + nested;
            }
        } else {
            // TODO map elt to component
            // TODO scan component classes
            throw new UnsupportedOperationException("Not implemented yet");
        }
        if (elt.parent() != null) {
            return out + "\n" + String.format("l%dc%d.child(%s);",
                    elt.parent().line(),
                    elt.parent().col(),
                    varName);
        }
        return out;
    }
}
