package com.acme.codegen;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;

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
     * @param endPos   end position
     * @return Tree
     */
    List<Tree> find(Tree node, int startPos, int endPos) {
        NodeFinder finder = new NodeFinder(this, startPos, endPos);
        node.accept(finder, null);
        return finder.result();
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
     * Test if a path has an ancestor.
     *
     * @param path   path
     * @param limit  limit
     * @param parent parent
     * @return {@code true} if found, {@code false} otherwise
     */
    boolean hasAncestor(TreePath path, Tree limit, Tree parent) {
        TreePath pp = path.getParentPath();
        while (pp != null) {
            Tree leaf = pp.getLeaf();
            if (leaf == parent) {
                return true;
            }
            if (leaf == limit) {
                break;
            }
            pp = pp.getParentPath();
        }
        return false;
    }

    /**
     * Get an enclosing node.
     *
     * @param node  tree node
     * @param clazz node type
     * @return StatementTree
     */
    <T> T enclosing(Tree node, Class<T> clazz) {
        TreePath path = path(node);
        while (path != null) {
            Tree leaf = path.getLeaf();
            if (clazz.isInstance(leaf)) {
                return clazz.cast(leaf);
            }
            path = path.getParentPath();
        }
        return null;
    }

    /**
     * Get the start position for the given tree node.
     *
     * @param node tree node
     * @return position
     */
    int startPosition(Tree node) {
        return (int) env.trees().getSourcePositions().getStartPosition(unit, node);
    }

    /**
     * Get the source code for the compilation unit.
     *
     * @return CharSequence
     */
    CharSequence unitSource() {
        try {
            return unit.getSourceFile().getCharContent(true);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Parse the given source in the context of the given tree node.
     *
     * @param node   tree node
     * @param source source code
     * @return list of tree nodes
     */
    List<Tree> parse(Tree node, String source) {
        PartialSource partial = PartialSource.create(node, source, this);
        String className = enclosingClassName(node);
        CompilationUnitTree newUnit = env.parse(className, partial.source());
        return find(newUnit, partial.startPos(), partial.endPos());
    }

    /**
     * Insert the given nodes.
     *
     * @param node  tree node to translate
     * @param nodes nodes to insert
     */
    void translate(Tree node, List<Tree> nodes) {
        Translator.translate(node, nodes, this);
    }
}
