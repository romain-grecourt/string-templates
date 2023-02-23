package com.acme.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;

import java.util.function.Function;

final class QNameResolver extends TreeScanner<String, Function<Tree, Scope>> {

    @Override
    public String visitIdentifier(IdentifierTree node, Function<Tree, Scope> scopeLookup) {
        Scope scope = scopeLookup.apply(node);
        while (scope.getEnclosingScope() != null) {
            for (Element elt : scope.getLocalElements()) {
                if (elt.getSimpleName() == node.getName()) {
                    String name = node.getName().toString();
                    Element enclosing = elt.getEnclosingElement();
                    if (enclosing instanceof QualifiedNameable) {
                        return ((QualifiedNameable) enclosing).getQualifiedName() + "$" + name;
                    }
                    return name;
                }
            }
            scope = scope.getEnclosingScope();
        }
        return null;
    }
}
