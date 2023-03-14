package com.acme.api.vdom;

/**
 * {@link VNode} compiler.
 */
public final class VNodeCompiler {

    private VNodeCompiler() {
        // cannot be instantiated
    }

    /**
     * Compile the given template.
     * This method invocation will be replaced with generated code.
     *
     * @param template template text
     * @return VNode
     */
    @SuppressWarnings("unused")
    public static VNode h(String template) {
        throw new UnsupportedOperationException("Not supported at runtime");
    }
}
