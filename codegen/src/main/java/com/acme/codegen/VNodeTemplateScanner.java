package com.acme.codegen;

import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreeScanner;

/**
 * Tree scanner that scans usages of {@code com.acme.api.vdom.VNodeCompiler.h}.
 */
final class VNodeTemplateScanner extends TreeScanner<List<VNodeTemplateInfo>, Lookup> {

    private static final String H = "com.acme.api.VNodeCompiler.h";

    @Override
    public List<VNodeTemplateInfo> visitMethodInvocation(MethodInvocationTree node, Lookup lookup) {
        String qName = node.getMethodSelect().accept(new QNameResolver(), lookup);
        if (H.equals(qName)) {
            List<VNodeTemplateInfo> templates = new ArrayList<>();
            templates.add(VNodeTemplateInfo.create(node, lookup));
            return templates;
        }
        return null;
    }

    @Override
    public List<VNodeTemplateInfo> reduce(List<VNodeTemplateInfo> r1, List<VNodeTemplateInfo> r2) {
        if (r1 != null) {
            if (r2 != null) {
                r1.addAll(r2);
            }
            return r1;
        }
        return r2;
    }
}
