package com.acme.codegen;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.IOException;

import com.acme.codegen.utils.Strings;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;

/**
 * Utility for a given compilation unit.
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
        PreviousNodeScanner scanner = new PreviousNodeScanner();
        unit.accept(scanner, node);
        return scanner.result();
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

    /**
     * Parse the given source as a replacement of the given node.
     *
     * @param node   tree node
     * @param source source code
     * @return Tree
     */
    Tree parse(Tree node, String source) {
        long startPos = startPosition(node);
        long endPos = getEndPosition(node);

        // create a new java source with the segment of node substituted
        CharSequence originalSource = null;
        try {
            originalSource = unit.getSourceFile().getCharContent(true);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        CharSequence before = originalSource.subSequence(0, (int) startPos);
        CharSequence after = originalSource.subSequence((int) endPos, originalSource.length() - 1);
        String indent = Strings.indentOf(before.toString());
        String newSource = before + Strings.indent(indent, source) + after;

        // parse the new source
        JavaFileObject fileObject = new JavaStringObject(enclosingClassName(node), newSource);
        JCTree.JCCompilationUnit newUnit = env.compiler().parse(fileObject);

        // get the AST node for the source
        Tree previous = previous(node);
        long previousPos = startPosition(previous);
        return find(newUnit, previousPos);
    }
}
