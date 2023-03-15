package com.acme.codegen;

import java.util.List;

import com.acme.codegen.utils.Lists;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Scope;
import com.sun.source.util.TreeScanner;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;

/**
 * Tree scanner that scans usages of {@code com.acme.api.vdom.VNodeCompiler.h}.
 */
final class VNodeTemplateScanner extends TreeScanner<List<MethodInvocationTree>, Lookup> {

    private static final String H = "com.acme.api.vdom.VNodeCompiler.h";
    private static final QNameVisitor QNAME_VISITOR = new QNameVisitor();

    @Override
    public List<MethodInvocationTree> visitAnnotatedType(AnnotatedTypeTree node, Lookup lookup) {
        return super.visitAnnotatedType(node, lookup);
    }

    @Override
    public List<MethodInvocationTree> visitMethodInvocation(MethodInvocationTree node, Lookup lookup) {
        String qName = node.getMethodSelect().accept(QNAME_VISITOR, lookup);
        if (H.equals(qName)) {
            return Lists.of(node);
        }
        return super.visitMethodInvocation(node, lookup);
    }

    @Override
    public List<MethodInvocationTree> reduce(List<MethodInvocationTree> r1, List<MethodInvocationTree> r2) {
        return Lists.concat(r1, r2);
    }

    private static final class QNameVisitor extends TreeScanner<String, Lookup> {

        @Override
        public String visitIdentifier(IdentifierTree node, Lookup lookup) {
            Scope scope = lookup.scope(node);
            while (scope.getEnclosingScope() != null) {
                for (Element elt : scope.getLocalElements()) {
                    if (elt.getSimpleName() == node.getName()) {
                        CharSequence name = node.getName();
                        Element enclosing = elt.getEnclosingElement();
                        if (enclosing instanceof QualifiedNameable) {
                            return ((QualifiedNameable) enclosing).getQualifiedName() + "." + name;
                        }
                        return name.toString();
                    }
                }
                scope = scope.getEnclosingScope();
            }
            return null;
        }
    }
}
