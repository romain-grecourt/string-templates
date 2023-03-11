package com.acme.codegen;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;

import javax.lang.model.element.Element;

/**
 * A lookup utility for a given compilation unit.
 *
 * @param env  env
 * @param unit compilation unit
 */
record Lookup(Env env, CompilationUnitTree unit) {

    /**
     * Get the enclosing class name.
     *
     * @param node tree node
     * @return fully qualified name of the enclosing class
     */
    String enclosingClassName(Tree node) {
        return scope(node).getEnclosingClass().getQualifiedName().toString();
    }

    /**
     * Get the scope for the given tree node.
     *
     * @param node tree node
     * @return Scope
     */
    Scope scope(Tree node) {
        return env.trees().getScope(path(node));
    }

    /**
     * Get the tree node for the given element.
     *
     * @param element element
     * @return Tree
     */
    Tree tree(Element element) {
        return env.trees().getPath(element).getLeaf();
    }

    /**
     * Find a node by start position.
     *
     * @param node     node to scan
     * @param startPos start position
     * @return Tree
     */
    Tree find(Tree node, long startPos) {
        NodeFinder scanner = new NodeFinder(this);
        node.accept(scanner, startPos);
        return scanner.result();
    }

    /**
     * Find the node that precedes the given node.
     *
     * @param node node to scan
     * @return Tree
     */
    Tree previous(Tree node) {
        PreviousScanner scanner = new PreviousScanner();
        unit.accept(scanner, node);
        return scanner.result();
    }

    /**
     * Get the {@link Element} instance for a given tree node.
     *
     * @param node tree node
     * @return Element
     */
    Element element(Tree node) {
        return env.trees().getElement(path(node));
    }

    /**
     * Get the tree path for the given node.
     *
     * @param node tree node
     * @return TreePath
     */
    TreePath path(Tree node) {
        return env.trees().getPath(unit, node);
    }

    /**
     * Get the start position for the given tree node.
     *
     * @param node tree node
     * @return position
     */
    long startPosition(Tree node) {
        return env.trees().getSourcePositions().getStartPosition(unit, node);
    }

    /**
     * Get the end position for the given tree node.
     *
     * @param node tree node
     * @return position
     */
    long getEndPosition(Tree node) {
        return env.trees().getSourcePositions().getEndPosition(unit, node);
    }
}
