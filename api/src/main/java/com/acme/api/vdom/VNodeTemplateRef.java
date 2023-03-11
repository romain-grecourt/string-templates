package com.acme.api.vdom;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link VNodeSupplier} backed by a template reference.
 */
public final class VNodeTemplateRef<T extends VNodeTemplate> implements VNodeSupplier {

    private final Supplier<T> factory;
    private final Function<T, VNode> invoker;
    private volatile T template;

    /**
     * Create a new instance.
     *
     * @param factory template factory
     * @param invoker template invoker
     */
    public VNodeTemplateRef(Supplier<T> factory, Function<T, VNode> invoker) {
        this.factory = Objects.requireNonNull(factory, "factory is null");
        this.invoker = Objects.requireNonNull(invoker, "invoker is null");
    }

    @Override
    public VNode get() {
        if (template == null) {
            template = factory.get();
        }
        return invoker.apply(template);
    }
}
