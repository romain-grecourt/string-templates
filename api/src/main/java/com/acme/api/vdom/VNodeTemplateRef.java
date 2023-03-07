package com.acme.api.vdom;

import java.util.Arrays;

/**
 * A {@link VNodeSupplier} backed by a template reference.
 */
final class VNodeTemplateRef implements VNodeSupplier {

    private final int id;
    private final Object[] args;
    private volatile VNodeTemplate template;

    /**
     * Create a new instance.
     *
     * @param id   template id
     * @param args template arguments
     */
    VNodeTemplateRef(Object id, Object[] args) {
        if (id instanceof String) {
            // not translated
            this.id = id.hashCode();
        } else if (id instanceof Integer) {
            // translated
            this.id = (int) id;
        } else {
            throw new IllegalArgumentException("Invalid id type: " + id.getClass());
        }
        this.args = args;
    }

    @Override
    public VNode get() {
        if (template == null) {
            template = VNodeTemplateProvider.get(id);
        }
        return template.render(args);
    }

    @Override
    public String toString() {
        return "VNodeTemplateRef{"
                + "id=" + id
                + ", args=" + Arrays.toString(args)
                + '}';
    }
}
