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
import static com.acme.codegen.utils.Constants.ELSE_IF_KEY;
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
    private final int pos;
    private DomParser parser;

    private VNodeGenerator(int pos) {
        this.pos = pos;
    }

    private void read(String is) throws IOException {
        parser = new DomParser(is, this);
        parser.parse();
    }

    /**
     * Generate the body of {@code VNodeTemplate.render}.
     *
     * @param is  input string
     * @param pos position
     * @return String
     * @throws IOException if an IO error occurs
     */
    static String generate(String is, int pos) throws IOException {
        VNodeGenerator reader = new VNodeGenerator(pos);
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
        String text = st.interpolate(f -> Strings.wrap(f, "\""), Function.identity(), " + ");
        String varName = varName(elt);
        String out = String.format("%s.text(%s);", varName, text);
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
        Map.Entry<String, String> ctrlEntry = Maps.first(exprs, CTRL_KEYS);
        if (ctrlEntry != null) {
            Map<String, String> attrs = Maps.filter(elt.attrs(), k -> Strings.filter(k, List.of(), CTRL_KEYS));
            String actual = node(elt.copy(attrs), nested);
            actual = Strings.indent("    ", actual);
            String ctrlKey = ctrlEntry.getKey();
            String expr = ctrlEntry.getValue();
            out = switch (ctrlKey) {
                case FOR_KEY -> String.format("for (%s) {\n    %s\n}", expr, actual);
                case IF_KEY -> String.format("if (%s) {\n    %s}", expr, actual);
                case ELSE_IF_KEY -> String.format("else if (%s) {\n    %s}", expr, actual);
                case ELSE_KEY -> String.format("else {\n    %s}", actual);
                default -> throw new IllegalStateException("Unsupported control: " + ctrlKey);
            };
        } else {
            out = node(elt, nested);
        }
        if (desc.isEmpty()) {
            String varName = varName(elt);
            out = out + "\n" + String.format("VNode __p%d = %s;", pos, varName);
        }
        this.asc.push(new Pair<>(desc.peek(), out));
    }

    private String node(DomElement elt, String nested) {
        Map<String, String> statics = Maps.filter(elt.attrs(), k -> Strings.filter(k, List.of(), EXPR_KEYS));
        String out;
        String varName = varName(elt);
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
            String parentVarName = varName(elt.parent());
            return out + "\n" + String.format("%s.child(%s);", parentVarName, varName);
        }
        return out;
    }

    private String varName(DomElement elt) {
        return String.format("__p%dl%dc%d", pos, elt.line(), elt.col());
    }
}
