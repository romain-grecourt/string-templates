package com.acme.codegen;

import com.sun.source.tree.LiteralTree;
import com.sun.source.util.SimpleTreeVisitor;

import static com.sun.source.tree.Tree.Kind.STRING_LITERAL;

/**
 * Simple visitor to get a tree node for a string literal as a {@link String}.
 */
final class StringLiteralVisitor extends SimpleTreeVisitor<String, Void> {

    @Override
    public String visitLiteral(LiteralTree node, Void arg) {
        if (node.getKind() == STRING_LITERAL) {
            return (String) node.getValue();
        }
        return null;
    }
}
