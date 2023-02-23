package com.acme.api;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VNodeTemplateProvider {

    private final Map<String, Supplier<VNodeTemplate>> suppliers = new HashMap<>();
    private final Map<String, VNodeTemplate> templates = new IdentityHashMap<>();

    protected void register(String fragment, Supplier<VNodeTemplate> supplier) {
        suppliers.put(fragment, supplier);
    }

    public static VNodeTemplate get(String fragment) {
        VNodeTemplate template = Cache.ALL_TEMPLATES.computeIfAbsent(fragment, VNodeTemplateProvider::get0);
        if (template != null) {
            return template;
        }
        throw new IllegalStateException("Template not found for fragment: " + fragment);
    }

    private VNodeTemplate template(String fragment) {
        return templates.computeIfAbsent(fragment, f -> {
            Supplier<VNodeTemplate> supplier = suppliers.get(f);
            if (supplier != null) {
                return supplier.get();
            }
            return null;
        });
    }

    private static VNodeTemplate get0(String fragment) {
        return Cache.PROVIDERS.stream()
                              .map(provider -> provider.template(fragment))
                              .filter(Objects::nonNull)
                              .findFirst()
                              .orElse(null);
    }

    private static class Cache {

        static final List<VNodeTemplateProvider> PROVIDERS =
                ServiceLoader.load(VNodeTemplateProvider.class, VNodeTemplateProvider.class.getClassLoader())
                             .stream()
                             .map(ServiceLoader.Provider::get)
                             .collect(Collectors.toList());

        static final Map<String, VNodeTemplate> ALL_TEMPLATES = new IdentityHashMap<>();
    }
}
