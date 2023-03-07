package com.acme.codegen;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.ListBuffer;

import java.util.Iterator;

/**
 * A tree translator to replace the template literal with its hash-code.
 */
class VNodeTemplateTranslator extends TreeTranslator {

    private final Env env;

    /**
     * Create a new instance.
     *
     * @param env env
     */
    VNodeTemplateTranslator(Env env) {
        this.env = env;
    }

    /**
     * Apply the translation.
     *
     * @param node node
     * @param env  env
     */
    static void apply(MethodInvocationTree node, Env env) {
        ((JCTree) node).accept(new VNodeTemplateTranslator(env));
    }

    @Override
    public void visitApply(JCTree.JCMethodInvocation tree) {
        // translate the method invocation tree
        // to replace the first argument (string) with its hash code (int)
        super.visitApply(tree);
        ZTreeMaker treeMaker = env.treeMaker();
        Iterator<JCTree.JCExpression> it = tree.args.iterator();
        ListBuffer<JCTree.JCExpression> listBuffer = new ListBuffer<>();
        String id = it.next().accept(new StringLiteralVisitor(), null);
        listBuffer.add(treeMaker.Literal(id.hashCode()));
        while (it.hasNext()) {
            listBuffer.add(it.next());
        }
        tree.args = listBuffer.toList();
        this.result = tree;
    }
}
