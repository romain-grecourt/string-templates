package com.acme.codegen;

import java.util.List;
import java.util.stream.Collectors;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;

import javax.lang.model.element.Element;

/**
 * Template info.
 *
 * @param literal template literal
 * @param name    template base name
 * @param args    template arguments
 */
record VNodeTemplateInfo(String literal, CharSequence name, List<VNodeTemplateArgInfo> args) {

    /**
     * Create a new template info.
     *
     * @param node   tree node
     * @param lookup lookup
     * @return template info
     */
    static VNodeTemplateInfo create(MethodInvocationTree node, Lookup lookup) {
        List<? extends ExpressionTree> arguments = node.getArguments();
        String template = arguments.get(0).accept(new StringLiteralVisitor(), null);
        List<VNodeTemplateArgInfo> args = arguments.stream()
                                                   .skip(1)
                                                   .map(e -> VNodeTemplateArgInfo.create(e, lookup))
                                                   .collect(Collectors.toList());

        Element element = lookup.element(node);
        CharSequence name = null;
        while (element != null && name == null) {
            switch (element.getKind()) {
                case CLASS, INTERFACE, RECORD -> name = element.getSimpleName();
                default -> element = element.getEnclosingElement();
            }
        }
        return new VNodeTemplateInfo(template, name, args);
    }
}
