package com.acme.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;

/**
 * Tree scanner that resolves the fully qualified named of tree node.
 */
final class QName extends TreeScanner<String, Lookup> {

    private static final QName INSTANCE = new QName();

    private QName() {
        // cannot be instantiated
    }

    /**
     * Get the fully qualified named of a tree node.
     *
     * @param node   node
     * @param lookup env
     * @return fully qualified named, or {@code null}
     */
    static String of(Tree node, Lookup lookup) {
        return node.accept(INSTANCE, lookup);
    }

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
