package com.acme.api.vdom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Effective tree {@link VNode}.
 */
public final class VTreeNode {

    private static final VTreeNode PSEUDO_ROOT = new VTreeNode(null, null);

    private final VTreeNode parent;
    private final VElement elt;
    private List<VTreeNode> children;
    private String text;

    private VTreeNode(VTreeNode parent, VElement elt) {
        this.parent = parent;
        this.elt = elt;
    }

    /**
     * Test if this node is a root node.
     *
     * @return {@code true} if a root node, {@code false} otherwise
     */
    public boolean isRoot() {
        return parent == PSEUDO_ROOT;
    }

    /**
     * Get the parent.
     *
     * @return VTreeNode
     */
    public VTreeNode parent() {
        return parent;
    }

    /**
     * Get the tag.
     *
     * @return String
     */
    public String tag() {
        return elt.getTag();
    }

    /**
     * Get the attributes.
     *
     * @return Map<String, String>
     */
    public Map<String, String> attrs() {
        return elt.getAttributes();
    }

    /**
     * Get the node text.
     *
     * @return String
     */
    public String text() {
        if (text == null) {
            List<VNode> children = elt.getChildren();
            if (children.size() == 1) {
                VNode child = children.get(0);
                if (child instanceof VText) {
                    text = ((VText) child).getText();
                }
            }
        }
        return text;
    }

    /**
     * Get the children.
     *
     * @return List<VTreeNode>
     */
    public List<VTreeNode> children() {
        if (children == null) {
            children = new ArrayList<>();
            for (VNode child : elt.getChildren()) {
                if (child instanceof VText) {
                    // skip
                    continue;
                }
                children.add(of(this, child));
            }
        }
        return children;
    }

    /**
     * Create a new root instance.
     *
     * @param node node
     * @return VTreeNode
     */
    public static VTreeNode of(VNode node) {
        return of(PSEUDO_ROOT, node);
    }

    /**
     * Create a new nested instance.
     *
     * @param parent parent
     * @param node   node
     * @return VTreeNode
     */
    public static VTreeNode of(VTreeNode parent, VNode node) {
        if (node instanceof VNodeSupplier) {
            while (node instanceof VNodeSupplier) {
                node = ((VNodeSupplier) node).get();
            }
        } else if (!(node instanceof VElement)) {
            throw new IllegalArgumentException("Unsupported node type: " + node.getClass());
        }
        return new VTreeNode(parent, (VElement) node);
    }
}
