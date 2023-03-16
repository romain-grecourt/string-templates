package com.acme.api.vdom;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Pretty printer.
 */
public class VNodePrinter {

    private final StringBuilder buf = new StringBuilder();
    private final Deque<Boolean> stack = new ArrayDeque<>();

    private VNodePrinter() {
    }

    /**
     * Pretty print the given node.
     *
     * @param node node
     * @return String
     */
    public static String print(VNode node) {
        VNodePrinter printer = new VNodePrinter();
        printer.visit(node);
        return printer.buf.toString();
    }

    private void visit(VNode root) {
        Deque<VElement> stack = new ArrayDeque<>();
        Deque<VElement> parents = new ArrayDeque<>();
        stack.push(VElement.unwrap(root));
        int depth = 0;
        while (!stack.isEmpty()) {
            VElement node = stack.peek();
            VElement parent = parents.peek();
            if (node == parent) {
                depth--;
                postVisit(node, depth);
                parents.pop();
                stack.pop();
            } else {
                List<VNode> children = node.getChildren();
                visit(node, depth++);
                boolean isLeaf = true;
                for (int i = children.size() - 1; i >= 0; i--) {
                    VNode child = children.get(i);
                    if (child instanceof VText) {
                        continue;
                    }
                    isLeaf = false;
                    stack.push(VElement.unwrap(child));
                }
                if (isLeaf) {
                    postVisit(node, --depth);
                    stack.pop();
                } else {
                    parents.push(node);
                }
            }
        }
    }

    private void visit(VElement node, int depth) {
        buf.append(String.format("%s<%s", "  ".repeat(depth), node.getTag()));
        node.getAttributes().forEach((k, v) -> buf.append(String.format(" %s=\"%s\"", k, v)));
        buf.append(">");
        String text = null;
        boolean hasChildren = false;
        for (VNode child : node.getChildren()) {
            if (child instanceof VText) {
                text = ((VText) child).getText();
                if (hasChildren) {
                    break;
                }
            } else {
                hasChildren = true;
                if (text != null) {
                    break;
                }
            }
        }
        if (hasChildren) {
            stack.push(true);
            buf.append("\n");
        } else {
            stack.push(false);
        }
        if (text != null) {
            buf.append(text);
            if (hasChildren) {
                buf.append("\n");
            }
        }
    }

    private void postVisit(VElement node, int depth) {
        if (stack.pop()) {
            buf.append("  ".repeat(depth));
        }
        buf.append(String.format("</%s>", node.getTag()));
        if (depth > 0) {
            buf.append("\n");
        }
    }
}
