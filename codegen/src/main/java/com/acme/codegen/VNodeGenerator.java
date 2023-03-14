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
import static java.util.stream.Collectors.joining;

/**
 * {@link DomReader} implementation.
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
        TextTemplate st = TextTemplate.create(data);
        String out = st.interpolate(f -> Strings.wrap(f, "\""), Function.identity(), " + ");
        out = String.format(".text(%s)", out);
        this.asc.push(new Pair<>(desc.peek(), out));
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
            String varName = String.format("l%dc%d", elt.line(), elt.col());
            Map<String, String> newAttrs = Maps.filter(elt.attrs(), k -> Strings.filter(k, List.of(), CTRL_KEYS));
            String actual = node(elt.copy(newAttrs), nested);
            actual = Strings.indent(" ".repeat(8 + varName.length() + 5), actual);
            out = String.format("""
                    .children(() -> {
                        List<VNode> %s = new ArrayList<>();
                        for (%s) {
                            %s.add(%s);
                        }
                        return %s;
                    })
                    """, varName, forExpr, varName, actual, varName);
        } else if (exprs.containsKey(IF_KEY)) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else if (exprs.containsKey(ELSE_KEY)) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else {
            out = node(elt, nested);
            if (!desc.isEmpty()) {
                out = String.format(".child(%s)", out);
                out = Strings.indent(" ".repeat(7), out);
            }
        }
        this.asc.push(new Pair<>(desc.peek(), out));
    }

    private String node(DomElement elt, String nested) {
        Map<String, String> statics = Maps.filter(elt.attrs(), k -> Strings.filter(k, List.of(), EXPR_KEYS));
        if (HTML_TAGS.contains(elt.tag())) {
            String out = String.format("com.acme.api.vdom.VElement.create(\"%s\")", elt.tag());
            String idt = " ".repeat("VElement".length());
            if (!statics.isEmpty()) {
                String attrsOut = statics.entrySet()
                                         .stream()
                                         .map(e -> String.format("""
                                                 .attr("%s", "%s")
                                                 """, e.getKey(), e.getValue()))
                                         .collect(joining());
                out += "\n" + idt + Strings.indent(idt, attrsOut);
            }
            if (!nested.isEmpty()) {
                out += "\n" + idt + Strings.indent(idt, nested);
            }
            return out;
        } else {
            // TODO map elt to component
            // TODO scan component classes
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }
}
