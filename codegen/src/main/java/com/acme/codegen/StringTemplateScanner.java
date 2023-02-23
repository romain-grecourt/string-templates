package com.acme.codegen;

import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.StringTemplateTree;
import com.sun.source.util.TreeScanner;

final class StringTemplateScanner extends TreeScanner<List<StringTemplateTree>, Void> {

    @Override
    public List<StringTemplateTree> visitStringTemplate(StringTemplateTree node, Void arg) {
        List<StringTemplateTree> nested = super.visitStringTemplate(node, arg);
        List<StringTemplateTree> nodes = new ArrayList<>();
        nodes.add(node);
        if (nested != null) {
            nodes.addAll(nested);
        }
        return nodes;
    }

    @Override
    public List<StringTemplateTree> reduce(List<StringTemplateTree> r1, List<StringTemplateTree> r2) {
        if (r1 != null) {
            if (r2 != null) {
                r1.addAll(r2);
            }
            return r1;
        }
        return r2;
    }
}
