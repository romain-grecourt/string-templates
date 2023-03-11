package com.acme.codegen;

import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SimpleTreeVisitor;

import static com.sun.source.tree.Tree.Kind.STRING_LITERAL;

/**
 * Simple visitor to extract the string literal out of a tree node.
 */
final class StringLiteral extends SimpleTreeVisitor<String, Void> {

    private static final StringLiteral INSTANCE = new StringLiteral();

    private StringLiteral() {
        // cannot be instantiated
    }

    /**
     * Get the string literal of the given node.
     *
     * @param node node
     * @return string
     */
    static String of(Tree node) {
        return node.accept(INSTANCE, null);
    }

    @Override
    public String visitLiteral(LiteralTree node, Void arg) {
        if (node.getKind() == STRING_LITERAL) {
            return (String) node.getValue();
        }
        return null;
    }
}
