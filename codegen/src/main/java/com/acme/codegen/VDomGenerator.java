package com.acme.codegen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import javax.lang.model.element.TypeElement;

import com.acme.codegen.dom.DomAttrs;
import com.acme.codegen.dom.DomElement;
import com.acme.codegen.dom.DomParser;
import com.acme.codegen.dom.DomReader;
import com.acme.codegen.utils.Maps;
import com.acme.codegen.utils.Strings;

import static com.acme.codegen.utils.Constants.CTRL_KEYS;
import static com.acme.codegen.utils.Constants.ELSE_IF_KEY;
import static com.acme.codegen.utils.Constants.ELSE_KEY;
import static com.acme.codegen.utils.Constants.FOR_KEY;
import static com.acme.codegen.utils.Constants.HTML_TAGS;
import static com.acme.codegen.utils.Constants.IF_KEY;

/**
 * {@link DomReader} implementation that generates template source code.
 */
final class VDomGenerator implements DomReader {

    private static final String V_ELT = "com.acme.api.vdom.VElement";
    private static final String V_COMP = "com.acme.api.vdom.VComponent";

    private final Deque<DomElement> desc = new ArrayDeque<>();
    private final Deque<DomOutput> asc = new ArrayDeque<>();
    private final VNodeTemplate template;
    private final int pos;
    private final Map<String, TypeElement> components;
    private DomParser parser;

    /**
     * Create a new instance.
     *
     * @param template   template
     * @param components components
     */
    VDomGenerator(VNodeTemplate template, Map<String, TypeElement> components) {
        this.components = components;
        this.template = template;
        this.pos = template.startPosition();
    }

    /**
     * Generate.
     *
     * @return String
     */
    String generate() {
        parser = new DomParser(template.raw(), this);
        try {
            parser.parse();
        } catch (Throwable ex) {
            throw new IllegalStateException(String.format(
                    "An unexpected error occurred, line: %d, char: %d",
                    parser.lineNumber(), parser.charNumber()), ex);
        }
        DomOutput result = asc.pop();
        return result.out;
    }

    @Override
    public void startElement(String name, Map<String, String> attrs) {
        if (desc.isEmpty() && Maps.containsKeys(attrs, CTRL_KEYS)) {
            throw new IllegalStateException("Root elt cannot use control attributes");
        }
        desc.push(new DomElement(desc.peek(), name, new DomAttrs(attrs), parser.lineNumber(), parser.charNumber()));
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
        this.asc.push(new DomOutput(elt, out));
    }

    @Override
    public void endElement(String name) {
        DomElement elt = desc.pop();
        DomAttrs attrs = elt.attrs();
        String nested = reduce(elt);
        String out;
        if (!attrs.controls().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            attrs.controls().forEach((k, v) -> {
                String actual = node(elt.copy(attrs.regulars()), nested);
                if (!sb.isEmpty()) {
                    sb.append("\n");
                }
                sb.append(genCtrlBlock(k, v, actual));
            });
            out = sb.toString();
        } else {
            out = node(elt, nested);
        }
        if (desc.isEmpty()) {
            String varName = varName(elt);
            out += "\n";
            out += String.format("VNode __p%d = %s;", pos, varName);
        }
        this.asc.push(new DomOutput(desc.peek(), out));
    }

    private String node(DomElement elt, String nested) {
        DomAttrs attrs = elt.attrs();
        String varName = varName(elt);
        String out;
        if (HTML_TAGS.contains(elt.tag())) {
            out = String.format("%s %s = %s.create(\"%s\");", V_ELT, varName, V_ELT, elt.tag());
            if (!attrs.statics().isEmpty()) {
                out += "\n";
                out += genAttrs(varName, attrs.statics(), Strings::quote);
            }
            if (!attrs.bindings().isEmpty()) {
                out += "\n";
                out += genAttrs(varName, attrs.bindings(), Function.identity());
            }
            if (!attrs.events().isEmpty()) {
                out += "\n";
                out += genEvents(varName, attrs.events());
            }
        } else {
            TypeElement type = components.get(elt.tag());
            if (type == null) {
                throw new IllegalStateException("Unresolved component class: " + elt.tag());
            }
            Map<String, String> literals = Maps.mapValue(attrs.statics(), Strings::quote);
            Map<String, String> params = Maps.combine(literals, attrs.bindings());
            Set<String> keys = template.componentParams(type, params.keySet());
            if (keys == null) {
                throw new IllegalStateException(String.format(
                        "Unresolved component constructor, tag: '%s', args: [%s]",
                        elt.tag(), String.join(",", params.keySet())));
            }
            String ctorArgs = String.join(", ", Maps.values(params, keys));
            CharSequence typeQName = type.getQualifiedName();
            out = String.format("%s %sc = new %s(%s);", typeQName, varName, typeQName, ctorArgs);
            out += "\n";
            out += String.format("%s %s = %s.create(%sc);", V_COMP, varName, V_COMP, varName);
        }
        if (!nested.isEmpty()) {
            out += "\n";
            out += nested;
        }
        if (elt.parent() != null) {
            String parentVarName = varName(elt.parent());
            out += "\n";
            out += String.format("%s.child((VNode) %s);", parentVarName, varName);
        }
        return out;
    }

    private String varName(DomElement elt) {
        return String.format("__p%dl%dc%d", pos, elt.line(), elt.col());
    }

    private String reduce(DomElement elt) {
        List<String> list = new ArrayList<>();
        while (!asc.isEmpty()) {
            if (asc.peek().elt == elt) {
                list.add(0, asc.pop().out);
            } else {
                break;
            }
        }
        return String.join("\n", list);
    }

    private static String genAttrs(String varName, Map<String, String> attrs, Function<String, String> mapper) {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<String, String>> it = attrs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            sb.append(String.format("%s.attr(\"%s\", %s);",
                    varName, entry.getKey(), mapper.apply(entry.getValue())));
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static String genEvents(String varName, Map<String, String> attrs) {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<String, String>> it = attrs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String event = entry.getKey();
            String method = switch (entry.getKey()) {
                case "click" -> "onClick";
                case "keyUp" -> "onKeyUp";
                default -> throw new IllegalArgumentException("Unsupported event: " + event);
            };
            sb.append(String.format("%s.%s(%s);", varName, method, entry.getValue()));
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static String genCtrlBlock(String key, String expr, String actual) {
        actual = Strings.indent("    ", actual);
        return switch (key) {
            case FOR_KEY -> String.format("for (%s) {\n    %s\n}", expr, actual);
            case IF_KEY -> String.format("if (%s) {\n    %s}", expr, actual);
            case ELSE_IF_KEY -> String.format("else if (%s) {\n    %s}", expr, actual);
            case ELSE_KEY -> String.format("else {\n    %s}", actual);
            default -> throw new IllegalStateException("Unsupported control: " + key);
        };
    }

    private record DomOutput(DomElement elt, String out) {
    }
}
