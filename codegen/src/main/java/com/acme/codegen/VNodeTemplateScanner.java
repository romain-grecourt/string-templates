package com.acme.codegen;

import java.util.ArrayList;
import java.util.List;

import com.acme.codegen.utils.Lists;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreeScanner;

/**
 * Tree scanner that scans usages of {@code com.acme.api.vdom.VNodeCompiler.h}.
 */
final class VNodeTemplateScanner extends TreeScanner<List<MethodInvocationTree>, Lookup> {

    private static final String H = "com.acme.api.vdom.VNodeCompiler.h";

    @Override
    public List<MethodInvocationTree> visitAnnotatedType(AnnotatedTypeTree node, Lookup lookup) {
        return super.visitAnnotatedType(node, lookup);
    }

    @Override
    public List<MethodInvocationTree> visitMethodInvocation(MethodInvocationTree node, Lookup lookup) {
        String qName = QName.of(node.getMethodSelect(), lookup);
        if (H.equals(qName)) {
            List<MethodInvocationTree> templates = new ArrayList<>();
            templates.add(node);
            return templates;
        }
        return null;
    }

    @Override
    public List<MethodInvocationTree> reduce(List<MethodInvocationTree> r1, List<MethodInvocationTree> r2) {
        return Lists.concat(r1, r2);
    }
}
