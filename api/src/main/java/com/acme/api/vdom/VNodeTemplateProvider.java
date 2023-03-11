package com.acme.api.vdom;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * {@link VNodeTemplate} service provider.
 */
public class VNodeTemplateProvider {

    private static final Map<Integer, VNodeTemplate> ALL_TEMPLATES = new IdentityHashMap<>();
    private final Map<Integer, Supplier<VNodeTemplate>> suppliers = new IdentityHashMap<>();

    /**
     * Register a template supplier.
     *
     * @param id       template id
     * @param supplier supplier of {@link VNodeTemplate}
     */
    protected void register(int id, Supplier<VNodeTemplate> supplier) {
        suppliers.put(id, supplier);
    }

    /**
     * Get a {@link VNodeTemplate} by id.
     *
     * @param id object whose reference is used as identity
     * @return template
     * @throws IllegalArgumentException if the id type is invalid or if the template is not found
     */
    public static VNodeTemplate get(Object id) {
        if (id instanceof String) {
            // not translated
            return get(id.hashCode());
        } else if (id instanceof Integer) {
            // translated
            return get((int) id);
        } else {
            throw new IllegalArgumentException("Invalid id type: " + id.getClass());
        }
    }

    /**
     * Get a {@link VNodeTemplate} by id.
     *
     * @param id object whose reference is used as identity
     * @return template
     * @throws IllegalArgumentException if the template is not found
     */
    public static VNodeTemplate get(int id) {
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
