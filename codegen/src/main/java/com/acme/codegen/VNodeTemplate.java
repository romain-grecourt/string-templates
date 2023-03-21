package com.acme.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor9;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;

/**
 * {@code VNode} template.
 *
 * @param inv    method invocation
 * @param lookup lookup
 */
record VNodeTemplate(MethodInvocationTree inv, Lookup lookup) {

    /**
     * Raw template.
     *
     * @return String
     */
    String raw() {
        return StringLiteral.of(inv.getArguments().get(0));
    }

    /**
     * Get the start position in the compilation unit source.
     *
     * @return int
     */
    int startPosition() {
        return lookup.startPosition(inv);
    }

    /**
     * Get the ordered component parameter names.
     *
     * @param type component type
     * @param keys markup keys
     * @return Set
     */
    Set<String> componentParams(TypeElement type, Set<String> keys) {
        return type.accept(new ConstructorVisitor(), keys);
    }

    /**
     * Apply the template.
     *
     * @param components components
     * @throws IOException if an IO error occurs
     */
    void apply(Map<String, TypeElement> components) throws IOException {
        String code = new VNodeGenerator(this, components).generate();
        List<Tree> nodes = lookup.parse(inv, code);
        lookup.translate(inv, nodes);
    }

    private class ConstructorVisitor extends SimpleElementVisitor9<Set<String>, Set<String>> {

        @Override
        public Set<String> visitType(TypeElement type, Set<String> arg) {
            for (Element e: type.getEnclosedElements()) {
                Set<String> result = e.accept(this, arg);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        @Override
        public Set<String> visitExecutable(ExecutableElement exe, Set<String> argNames) {
            if (exe.getKind() == ElementKind.CONSTRUCTOR && lookup.isAccessible(inv, exe)) {
                Set<String> names = new LinkedHashSet<>();
                for (VariableElement parameter : exe.getParameters()) {
                    String name = parameter.getSimpleName().toString();
                    if (!argNames.contains(name)) {
                        return null;
                    }
                    names.add(name);
                }
                return names;
            }
            return null;
        }
    }
}
