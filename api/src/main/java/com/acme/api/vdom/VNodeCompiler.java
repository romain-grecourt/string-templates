package com.acme.api.vdom;

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
     * Note that the id parameter <b>MUST BE A LITERAL</b> in the source code, however it may be translated during
     * post-compilation to reduce the final byte-code size.
     *
     * @param id   template text, <b>MUST BE A LITERAL</b>
     * @param args args template arguments
     * @return VNode
     */
    public static VNode h(Object id, Object... args) {
        return new VNodeTemplateRef(id, args);
    }
}
