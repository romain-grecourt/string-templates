package com.acme.codegen;

import com.sun.source.tree.Tree;

/**
 * A scanner that finds the tree node that precedes the given tree node.
 */
final class PreviousScanner extends SimpleTreeScanner<Tree> {

    private Tree result;
    private boolean found;

    /**
     * Get the result.
     *
     * @return Tree
     */
    Tree result() {
        return result;
    }

    @Override
    protected void visit(Tree node, Tree arg) {
        if (!found) {
            if (node == arg) {
                found = true;
            } else {
                result = node;
            }
        }
    }
}
