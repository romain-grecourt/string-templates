package com.acme.api.vdom;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Virtual DOM node.
 */
public interface VNode {

    /**
     * Pretty print this node.
     *
     * @return String
     */
    default String toPrettyString() {
        VNodePrinter printer = new VNodePrinter();
        visit(printer);
        return printer.asString();
    }

    /**
     * Visit this node.
     *
     * @param visitor visitor
     */
    default void visit(Visitor visitor) {
        Deque<VTreeNode> stack = new ArrayDeque<>();
        stack.push(VTreeNode.of(this));
        VTreeNode parent = null;
        int depth = 0;
        while (!stack.isEmpty()) {
            VTreeNode node = stack.peek();
            if (node == parent) {
                depth--;
                visitor.postVisit(node, depth);
                parent = node.parent();
                stack.pop();
            } else {
                List<VTreeNode> children = node.children();
                visitor.visit(node, depth++);
                if (!children.isEmpty()) {
                    for (int i = children.size() - 1; i >= 0; i--) {
                        stack.push(children.get(i));
                    }
                } else {
                    visitor.postVisit(node, --depth);
                    parent = node.parent();
                    stack.pop();
                }
            }
        }
    }

    /**
     * Visitor.
     */
    interface Visitor {

        /**
         * Enter a node.
         *
         * @param node  node
         * @param depth depth
         */
        void visit(VTreeNode node, int depth);

        /**
         * Leave a node.
         *
         * @param node  node
         * @param depth depth
         */
        void postVisit(VTreeNode node, int depth);
    }
}
