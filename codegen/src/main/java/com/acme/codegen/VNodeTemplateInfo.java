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
        return enclosingClassName + "_P" + position;
    }

    /**
     * Get the list of required type names.
     *
     * @return list of String
     */
    List<String> requiredTypeNames() {
        return requiredTypes().stream()
                              .map(TypeInfo::qualifiedName)
                              .collect(Collectors.toList());
    }

    /**
     * Get the list of required types.
     *
     * @return list of TypeInfo
     */
    List<TypeInfo> requiredTypes() {
        return args().stream()
                     .flatMap(a -> a.type().allTypeParams().stream())
                     .collect(Collectors.toList());
    }

    /**
     * Create a new template info.
     *
     * @param node   tree node
     * @param lookup lookup
     * @return template info
     */
    static VNodeTemplateInfo create(MethodInvocationTree node, Lookup lookup) {
        List<? extends ExpressionTree> arguments = node.getArguments();
        String literal = StringLiteral.of(arguments.get(0));
        List<VNodeTemplateArgInfo> args = arguments.stream()
                                                   .skip(1)
                                                   .map(e -> VNodeTemplateArgInfo.create(e, lookup))
                                                   .collect(Collectors.toList());
        String pkg = lookup.unit().getPackageName().toString();
        CharSequence className = lookup.scope(node).getEnclosingClass().getSimpleName();
        long position = lookup.startPosition(node);
        return new VNodeTemplateInfo(literal, args, pkg, className, position);
    }
}
