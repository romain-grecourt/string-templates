package com.acme.codegen;

import com.sun.source.tree.ExpressionTree;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

/**
 * Template argument info.
 *
 * @param type argument type
 * @param name argument name
 */
record VNodeTemplateArgInfo(TypeElement type, Name name) {

    /**
     * Create a new literal argument info.
     *
     * @param node tree node
     * @param env  env
     * @return literal argument info
     */
    static VNodeTemplateArgInfo create(ExpressionTree node, Env env) {
        Element element = env.element(node);
        TypeElement type = env.type(element);
        Name name = element.getSimpleName();
        return new VNodeTemplateArgInfo(type, name);
    }
}
