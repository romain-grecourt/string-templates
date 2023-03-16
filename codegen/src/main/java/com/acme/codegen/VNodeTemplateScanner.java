package com.acme.codegen;

import java.util.List;

import com.acme.codegen.utils.Lists;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreeScanner;

/**
 * Tree scanner that scans usages of {@code com.acme.api.Component.h}.
 */
final class VNodeTemplateScanner extends TreeScanner<List<VNodeTemplate>, Lookup> {

    private static final String H = "com.acme.api.Component.h";

    @Override
    public List<VNodeTemplate> visitAnnotatedType(AnnotatedTypeTree node, Lookup lookup) {
        return super.visitAnnotatedType(node, lookup);
    }

    @Override
    public List<VNodeTemplate> visitMethodInvocation(MethodInvocationTree node, Lookup lookup) {
        String qName = lookup.qName(node);
        if (H.equals(qName)) {
            return Lists.of(new VNodeTemplate(node, lookup));
        }
        return super.visitMethodInvocation(node, lookup);
    }

    @Override
    public List<VNodeTemplate> reduce(List<VNodeTemplate> r1, List<VNodeTemplate> r2) {
        return Lists.concat(r1, r2);
    }
}
