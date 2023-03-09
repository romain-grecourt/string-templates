package com.acme.codegen;

import javax.lang.model.element.Name;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.acme.codegen.utils.Lists;
import com.acme.codegen.utils.Strings;

import static java.util.stream.Collectors.joining;

/**
 * Template generator.
 */
final class VNodeTemplateGenerator {

    private static final List<String> BASE_IMPORTS = Arrays.asList(
            "com.acme.api.vdom.VNode",
            "com.acme.api.vdom.VElement",
            "com.acme.api.vdom.VNodeTemplate",
            "java.util.List",
            "java.util.ArrayList");

    /**
     * Generate a template class.
     *
     * @param template template
     * @return String
     */
    static String generate(VNodeTemplateInfo template) throws IOException {
        List<String> importClasses = Lists.concat(BASE_IMPORTS, template.requiredTypeNames());
        String imports = renderImports(importClasses);
        String argDecls = renderArgDecls(template.args());
        String body = VNodeTemplateBodyGenerator.generate(template.literal());
        return String.format("""
                        package %s;
                                                
                        %s
                                                
                        /**
                         * {@link VNodeTemplate} implementation for {@link %s} at position {@code %d}.
                         * <br/>
                         * <pre>
                         * %s
                         * </pre>
                         */
                        final class %s implements VNodeTemplate {
                         
                            @Override
                            public VNode render(Object... args) {
                                %s
                                return %s;
                            }
                        }
                        """,
                template.pkg(),
                imports,
                template.enclosingClassName(),
                template.position(),
                template.literal()
                        .lines()
                        .map(HTMLEntities::encode)
                        .collect(joining("\n * ")),
                template.simpleName(),
                Strings.indent(" ".repeat(8), argDecls),
                Strings.indent(" ".repeat(8 + "return ".length()), body));
    }

    private static String renderImports(List<String> classes) {
        return classes.stream()
                      .distinct()
                      .sorted()
                      .map(imp -> "import " + imp + ";")
                      .collect(joining("\n"));
    }

    private static String renderArgDecls(List<VNodeTemplateArgInfo> args) {
        List<String> scope = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            VNodeTemplateArgInfo argInfo = args.get(i);
            String argType = argInfo.type().decl();
            Name argName = argInfo.name();
            scope.add(String.format("%s %s = (%s) args[%d];", argType, argName, argType, i));
        }
        return String.join("\n", scope);
    }
}
