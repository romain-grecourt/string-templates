package com.acme.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Scope;
import com.sun.source.util.TreeScanner;

/**
 * Tree scanner that resolves the fully qualified named of an {@link IdentifierTree} node.
 */
final class QNameResolver extends TreeScanner<String, Env> {

    @Override
    public String visitIdentifier(IdentifierTree node, Env scopeLookup) {
        Scope scope = scopeLookup.scope(node);
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
