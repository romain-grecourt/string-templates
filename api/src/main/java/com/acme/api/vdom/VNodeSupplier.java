package com.acme.api.vdom;

import java.util.function.Supplier;

/**
 * Lazy {@link VNode}.
 */
public interface VNodeSupplier extends VNode, Supplier<VNode> {
}
