package com.acme.codegen;

import com.sun.source.tree.ExpressionTree;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;

/**
 * Template argument info.
 *
 * @param type argument type
 * @param name argument name
 */
record VNodeTemplateArgInfo(TypeInfo type, Name name) {

    /**
     * Create a new literal argument info.
     *
     * @param node tree node
     * @param env  env
     * @return literal argument info
     */
    static VNodeTemplateArgInfo create(ExpressionTree node, Env env) {
        Element element = env.element(node);
        TypeInfo type = TypeInfo.of(element, env.types());
        Name name = element.getSimpleName();
        return new VNodeTemplateArgInfo(type, name);
    }
}
