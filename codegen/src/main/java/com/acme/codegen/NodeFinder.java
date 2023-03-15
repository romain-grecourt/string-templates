package com.acme.codegen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;

/**
 * Scanner that finds tree node by start position.
 */
final class NodeFinder extends TreeScanner<Void, Void> {

    private final Lookup lookup;
    private final int startPos;
    private final int endPos;
    private final Deque<Tree> stack = new ArrayDeque<>();
    private final List<Tree> result = new ArrayList<>();

    /**
     * Create a new instance.
     *
     * @param lookup   lookup
     * @param startPos start position
     * @param endPos   end position
     */
    NodeFinder(Lookup lookup, int startPos, int endPos) {
        this.lookup = lookup;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    /**
     * The find result.
     *
     * @return list of Tree
     */
    List<Tree> result() {
        return result;
    }

    @Override
    public Void scan(Tree node, Void arg) {
        if (node != null) {
            int pos = lookup.startPosition(node);
            if (pos >= startPos && pos < endPos) {
                boolean found = false;
                if (!result.isEmpty()) {
                    Tree last = result.get(result.size() - 1);
                    Iterator<Tree> it = stack.descendingIterator();
                    while (!found && it.hasNext()) {
                        Tree next = it.next();
                        if (last == next) {
                            found = true;
                        }
                    }
                }
                if (!found) {
                    result.add(node);
                }
            }
            stack.push(node);
            node.accept(this, arg);
            stack.pop();
        }
        return null;
    }
}
