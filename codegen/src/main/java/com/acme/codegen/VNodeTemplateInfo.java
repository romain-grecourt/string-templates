package com.acme.codegen;

import java.util.List;
import java.util.stream.Collectors;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;

/**
 * Template info.
 *
 * @param literal            template literal
 * @param args               template arguments
 * @param pkg                name package name
 * @param enclosingClassName enclosing class name
 * @param position           start position
 */
record VNodeTemplateInfo(String literal,
                         List<VNodeTemplateArgInfo> args,
                         String pkg,
                         CharSequence enclosingClassName,
                         long position) {

    /**
     * Get the simple name.
     *
     * @return name
     */
    String simpleName() {
        return enclosingClassName + "_" + position;
    }

    /**
     * Create a new template info.
     *
     * @param node tree node
     * @param env  env
     * @return template info
     */
    static VNodeTemplateInfo create(MethodInvocationTree node, Env env) {
        List<? extends ExpressionTree> arguments = node.getArguments();
        String literal = arguments.get(0).accept(new StringLiteralVisitor(), null);
        List<VNodeTemplateArgInfo> args = arguments.stream()
                                                   .skip(1)
                                                   .map(e -> VNodeTemplateArgInfo.create(e, env))
                                                   .collect(Collectors.toList());
        String pkg = env.unit().getPackageName().toString();
        CharSequence className = env.scope(node).getEnclosingClass().getSimpleName();
        long position = env.startPosition(node);
        return new VNodeTemplateInfo(literal, args, pkg, className, position);
    }
}
