package com.acme.codegen;

import javax.lang.model.element.Modifier;
import java.util.Set;

import com.acme.codegen.utils.Strings;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;

/**
 * Partial source utility.
 *
 * @param source   actual partial source code
 * @param startPos start position in the original unit source
 * @param endPos   end position in the original unit source
 */
record PartialSource(CharSequence source, int startPos, int endPos) {

    /**
     * Create a new partial source.
     *
     * @param node   context tree node
     * @param source actual partial source code
     * @param lookup lookup
     * @return PartialSource
     */
    static PartialSource create(Tree node, String source, Lookup lookup) {
        StatementTree parent = lookup.enclosing(node, StatementTree.class);
        if (parent == null) {
            throw new IllegalStateException("Unable to find parent statement");
        }
        String theSource;
        if (lookup.enclosing(node, BlockTree.class) == null) {
            VariableTree varDecl = (VariableTree) parent;
            Set<Modifier> modifiers = varDecl.getModifiers().getFlags();
            int pos = lookup.startPosition(node);
            if (modifiers.contains(Modifier.STATIC)) {
                theSource = String.format("""
                                private static VNode __init_p%d() {
                                    %s
                                    return __p%d;
                                }
                                """,
                        pos,
                        Strings.indent("    ", source),
                        pos);
            } else {
                theSource = String.format("""
                                private VNode __init_p%d() {
                                    %s
                                    return __p%d;
                                }
                                """,
                        pos,
                        Strings.indent("    ", source),
                        pos);
            }
        } else {
            theSource = source;
        }

        CharSequence originalSource = lookup.unitSource();
        int startPos = lookup.startPosition(parent);
        CharSequence before = originalSource.subSequence(0, startPos);
        CharSequence after = originalSource.subSequence(startPos, originalSource.length() - 1);
        String indent = Strings.indentOf(before.toString());
        String newSource = before + Strings.indent(indent, theSource) + "\n" + indent + after;
        int endPos = newSource.length() - after.length();
        return new PartialSource(newSource, startPos, endPos);
    }
}
