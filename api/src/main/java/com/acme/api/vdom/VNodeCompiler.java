package com.acme.api.vdom;

/**
 * {@link VNode} compiler.
 */
public final class VNodeCompiler {

    private VNodeCompiler() {
        // cannot be instantiated
    }

    /**
     * VNode template compiler entrypoint.
     * <ul>
     *     <li>At compile-time, the {@code id} parameter must be a string literal. The string content is parsed
     *     and a class implementing {@link VNodeTemplate} is generated.</li>
     *     <li>At run-time, the {@code id} parameter is used to match the generated  {@link VNodeTemplate} class.</li>
     * </ul>
     * <br/>
     * Note that the id string may be replaced post-compilation with an object in order to reduce the byte-code size,
     * hence the {@link Object} type.
     *
     * @param id   object whose reference is used as identity
     * @param args args template arguments
     * @return VNode
     */
    public static VNode h(Object id, Object... args) {
        return VNodeTemplateProvider.get(id).render(args);
    }
}
