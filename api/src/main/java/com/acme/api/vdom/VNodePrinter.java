package com.acme.api.vdom;

/**
 * {@link VNode.Visitor} implementation.
 */
public class VNodePrinter implements VNode.Visitor {

    private final StringBuilder buf = new StringBuilder();

    /**
     * Get the printed string.
     *
     * @return String
     */
    public String asString() {
        return buf.toString();
    }

    @Override
    public void visit(VTreeNode node, int depth) {
        buf.append(String.format("%s<%s", "  ".repeat(depth), node.tag()));
        node.attrs().forEach((k, v) -> {
            buf.append(String.format(" %s=\"%s\"", k, v));
        });
        buf.append(">");
        if (!node.children().isEmpty()) {
            buf.append("\n");
        }
        String text = node.text();
        if (text != null) {
            buf.append(text);
            if (!node.children().isEmpty()) {
                buf.append("\n");
            }
        }
    }

    @Override
    public void postVisit(VTreeNode node, int depth) {
        if (!node.children().isEmpty()) {
            buf.append("  ".repeat(depth));
        }
        buf.append(String.format("</%s>", node.tag()));
        if (!node.isRoot()) {
            buf.append("\n");
        }
    }
}
