package com.acme.api.vdom;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link VNode} compiler.
 */
public final class VNodeCompiler {

    private VNodeCompiler() {
        // cannot be instantiated
    }

    /**
     * Get a reference to a code-generated {@link VNodeTemplate} instance.
     * At compile-time, usages of this method are scanned {@link VNodeTemplate} implementations are code-generated
     * from the template text literals.
     * <p>
     * Note that the template parameter <b>MUST BE A LITERAL</b> in the source code.
     *
     * @param template template text, <b>MUST BE A LITERAL</b>
     * @param args     args template arguments
     * @return VNode
     */
    public static VNode h(Object template, Object... args) {
        // TODO throw an exception...
        return new VNodeTemplateRef<>(() -> VNodeTemplateProvider.get(template), t -> t.render(args));
    }

    public static <T> Object f(Consumer<T> consumer) {
        // TODO throw an exception...
        return consumer;
    }

    public static <T> Object f(Supplier<T> consumer) {
        // TODO throw an exception...
        return consumer;
    }
}
