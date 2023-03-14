package com.acme.codegen;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;

/**
 * A tree translator to replace an invocation with a given node.
 */
class InvocationTranslator extends TreeTranslator {

    private final JCMethodInvocation newInv;

    private InvocationTranslator(JCMethodInvocation newInv) {
        this.newInv = newInv;
    }

    /**
     * Translate the given invocation with the given replacement tree node.
     *
     * @param target      invocation
     * @param replacement replacement
     */
    static void translate(MethodInvocationTree target, Tree replacement) {
        // TODO replacement should be an expression
        //  find parent, insert statements and replace expression with replacement
        if (!(target instanceof JCMethodInvocation)) {
            throw new IllegalArgumentException("target is not a JCMethodInvocation");
        }
        if (!(replacement instanceof JCMethodInvocation)) {
            throw new IllegalArgumentException("replacement is not a JCMethodInvocation");
        }
        InvocationTranslator translator = new InvocationTranslator((JCMethodInvocation) replacement);
        ((JCMethodInvocation) target).accept(translator);
    }

    @Override
    public void visitApply(JCMethodInvocation tree) {
        // TODO get parent
        // and replace expression with replacement
        tree.meth = newInv.meth;
        tree.args = newInv.args;
        tree.typeargs = newInv.typeargs;
        tree.varargsElement = newInv.varargsElement;
        this.result = tree;
    }
}
