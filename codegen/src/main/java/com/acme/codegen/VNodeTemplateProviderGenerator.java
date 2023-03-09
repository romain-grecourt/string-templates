package com.acme.codegen;

import com.acme.codegen.utils.Strings;

import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 * Provider generator.
 */
final class VNodeTemplateProviderGenerator {

    /**
     * Provider class name.
     */
    static final String CNAME = "VNodeTemplateProviderImpl";

    private VNodeTemplateProviderGenerator() {
        // cannot be instantiated
    }

    /**
     * Generate a provider class.
     *
     * @param templates templates
     * @param pkg       package
     * @return String
     */
    static String generate(Map<String, VNodeTemplateInfo> templates, String pkg) {
        String constructor = renderConstructor(templates);
        constructor = Strings.indent(" ".repeat(8), constructor);
        return String.format("""
                package %s;
                                
                import com.acme.api.vdom.VNodeTemplateProvider;
                import com.acme.api.vdom.VNodeTemplate;
                                
                /**
                 * Template provider implementation for package {@code %s}.
                 */
                public final class %s extends VNodeTemplateProvider {
                                
                    /**
                     * Create a new instance.
                     */
                    public %s() {
                        %s
                    }
                }
                """, pkg, pkg, CNAME, CNAME, constructor);
    }

    private static String renderConstructor(Map<String, VNodeTemplateInfo> templates) {
        return templates.entrySet()
                        .stream()
                        .map(e -> {
                            int id = e.getValue().literal().hashCode();
                            String className = e.getKey();
                            return String.format("register(%s, %s::new);", id, className);
                        })
                        .collect(joining("\n"));
    }
}
