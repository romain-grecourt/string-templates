package com.acme.codegen;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.ListBuffer;

/**
 * Translator !.
 */
class Translator {

    /**
     * Insert statements in the enclosing block and replace the parent expression with the name identifier of the first
     * variable declaration statement.
     *
     * @param node     tree node to translate
     * @param newNodes new nodes to insert
     * @param lookup   lookup
     */
    static void translate(Tree node, Iterable<Tree> newNodes, Lookup lookup) {
        TreePath path = lookup.path(node);
        JCBlock block = (JCBlock) lookup.enclosingBlock(path);
        ListBuffer<JCStatement> newStats = new ListBuffer<>();
        JCStatement first = null;
        for (JCStatement stat : block.stats) {
            if (lookup.hasAncestor(path, block, stat)) {
                for (Tree newNode : newNodes) {
                    if (!(newNode instanceof JCStatement)) {
                        throw new IllegalArgumentException("Not a statement: " + newNode);
                    }
                    if (first == null) {
                        first = (JCStatement) newNode;
                    }
                    newStats.add((JCStatement) newNode);
                }
            }
            newStats.add(stat);
        }
        block.stats = newStats.toList();
        if (!(first instanceof JCVariableDecl varDecl)) {
            throw new IllegalArgumentException("First node is not a variable declaration: " + first);
        }
        TreeMaker treeMaker = lookup.env().treeMaker();
        JCIdent ident = treeMaker.Ident(varDecl.name);
        Tree parent = path.getParentPath().getLeaf();
        if (parent instanceof JCReturn jcr) {
            jcr.expr = ident;
        } else if (parent instanceof JCVariableDecl jcv) {
            jcv.init = ident;
        } else if (parent instanceof JCTree.JCMethodInvocation jcm) {
            ListBuffer<JCExpression> newArgs = new ListBuffer<>();
            for (JCExpression arg : jcm.getArguments()) {
                if (arg == node) {
                    newArgs.add(ident);
                } else {
                    newArgs.add(arg);
                }
            }
            jcm.args = newArgs.toList();
        }
    }
}
