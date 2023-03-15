package com.acme.codegen;

import com.acme.codegen.utils.Strings;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;

/**
 * Partial source utility.
 *
 * @param lookup   lookup
 * @param node     context tree node
 * @param source   actual partial source code
 * @param startPos start position in the original unit source
 * @param endPos   end position in the original unit source
 */
record PartialSource(Lookup lookup, Tree node, CharSequence source, int startPos, int endPos) {

    /**
     * Create a new partial source.
     *
     * @param node   context tree node
     * @param source actual partial source code
     * @param lookup lookup
     * @return PartialSource
     */
    static PartialSource create(Tree node, String source, Lookup lookup) {
        TreePath path = lookup.path(node);
        StatementTree parent = lookup.enclosingStatement(path);
        if (parent == null) {
            // TODO
            //  if static field, create a static method
            //  if instance field, create an instance method
            throw new IllegalStateException("Unable to find parent statement");
        }

        // create a new java source
        CharSequence originalSource = lookup.unitSource();
        int startPos = lookup.startPosition(parent);
        CharSequence before = originalSource.subSequence(0, startPos);
        CharSequence after = originalSource.subSequence(startPos, originalSource.length() - 1);
        String indent = Strings.indentOf(before.toString());
        String newSource = before + Strings.indent(indent, source) + "\n" + indent + after;
        int endPos = newSource.length() - after.length();
        return new PartialSource(lookup, node, newSource, startPos, endPos);
    }
}
