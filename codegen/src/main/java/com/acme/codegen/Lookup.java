package com.acme.codegen;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * A utility to bridge between {@link Element} and {@link Tree}.
 *
 * @param types types utility
 * @param trees trees utility
 * @param unit  compilation unit
 */
record Lookup(Types types, Trees trees, CompilationUnitTree unit) {

    /**
     * Get the scope for the given tree node.
     *
     * @param node tree node
     * @return Scope
     */
    Scope scope(Tree node) {
        return trees.getScope(trees.getPath(unit, node));
    }

    /**
     * Get the {@link Element} instance for a given tree node.
     *
     * @param node tree node
     * @return Element
     */
    Element element(Tree node) {
        return trees.getElement(trees.getPath(unit, node));
    }

    /**
     * Get the type element for the given element.
     *
     * @param element element
     * @return TypeElement
     */
    TypeElement type(Element element) {
        return (TypeElement) types.asElement(element.asType());
    }
}
