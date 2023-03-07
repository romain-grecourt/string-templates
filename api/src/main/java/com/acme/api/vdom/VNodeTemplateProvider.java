package com.acme.api.vdom;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * {@link VNodeTemplate} service provider.
 */
@SuppressWarnings("unused")
public class VNodeTemplateProvider {

    private static final Map<Object, VNodeTemplate> ALL_TEMPLATES = new IdentityHashMap<>();
    private final Map<Object, Supplier<VNodeTemplate>> suppliers = new IdentityHashMap<>();

    /**
     * Register a template supplier.
     * @param id whose reference is used as identity
     * @param supplier supplier of {@link VNodeTemplate}
     */
    protected void register(Object id, Supplier<VNodeTemplate> supplier) {
        suppliers.put(id, supplier);
    }

    /**
     * Get a {@link VNodeTemplate} by id.
     *
     * @param id object whose reference is used as identity
     * @return template
     * @throws IllegalArgumentException if the template is not found
     */
    public static VNodeTemplate get(Object id) {
        return ALL_TEMPLATES.computeIfAbsent(id, k ->
                Cache.PROVIDERS.stream()
                               .filter(p -> p.suppliers.containsKey(k))
                               .map(p -> p.suppliers.get(k).get())
                               .findFirst()
                               .orElseThrow(() -> new IllegalArgumentException("Template not found, id: " + k)));
    }

    private static class Cache {

        /**
         * Lazy singleton idiom.
         */
        static final List<VNodeTemplateProvider> PROVIDERS =
                ServiceLoader.load(VNodeTemplateProvider.class, VNodeTemplateProvider.class.getClassLoader())
                             .stream()
                             .map(ServiceLoader.Provider::get)
                             .toList();
    }
}