package com.acme.codegen;

import com.sun.source.tree.Tree;

/**
 * Scanner that finds tree node by start position.
 */
final class NodeFinder extends SimpleTreeScanner<Long> {

    private final Lookup lookup;
    private Tree result;
    private boolean found;

    NodeFinder(Lookup lookup) {
        this.lookup = lookup;
    }

    /**
     * The find result.
     *
     * @return Tree
     */
    Tree result() {
        return result;
    }

    @Override
    protected void visit(Tree node, Long pos) {
        if (!found) {
            if (lookup.startPosition(node) == pos) {
                found = true;
            }
        } else if (result == null) {
            result = node;
        }
    }
}
