package com.acme.codegen;

import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.ListBuffer;

import static com.sun.tools.javac.util.List.nil;

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
    static void translate(Tree node, List<Tree> newNodes, Lookup lookup) {
        if (newNodes.isEmpty()) {
            return;
        }
        JCBlock block = lookup.enclosing(node, JCBlock.class);
        if (block == null) {
            initializer(node, newNodes, lookup);
        } else {
            inline(block, node, newNodes, lookup);
        }
    }

    private static void initializer(Tree node, List<Tree> newNodes, Lookup lookup) {
        if (newNodes.size() > 1) {
            throw new IllegalArgumentException("Expecting only one node");
        }
        Tree newNode = newNodes.get(0);
        if (!(newNode instanceof JCMethodDecl methodDecl)) {
            throw new IllegalArgumentException("Expecting a method declaration");
        }
        JCClassDecl classTree = lookup.enclosing(node, JCClassDecl.class);
        if (classTree == null) {
            throw new IllegalStateException("Unable to find enclosing class");
        }
        ListBuffer<JCTree> newDefs = new ListBuffer<>();
        newDefs.addAll(classTree.defs);
        newDefs.add(methodDecl);
        classTree.defs = newDefs.toList();
        JCMethodInvocation inv = (JCMethodInvocation) node;
        TreeMaker treeMaker = lookup.env().treeMaker();
        inv.meth = treeMaker.Ident(methodDecl.name);
        inv.args = nil();
    }

    private static void inline(JCBlock block, Tree node, List<Tree> newNodes, Lookup lookup) {
        TreePath path = lookup.path(node);
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
        } else if (parent instanceof JCMethodInvocation jcm) {
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
