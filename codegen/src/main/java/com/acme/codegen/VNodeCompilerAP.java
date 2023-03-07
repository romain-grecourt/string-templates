package com.acme.codegen;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.source.tree.Tree;

/**
 * Generic annotation processors that scan usages of {@code com.acme.api.vdom.VNodeCompiler.h}.
 */
@SupportedAnnotationTypes(value = {"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_20)
public class VNodeCompilerAP extends AbstractProcessor {

    private static final String SERVICE_FILE = "META-INF/services/com.acme.api.vdom.VNodeTemplateProvider";
    private boolean done;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (done) {
            return false;
        }
        Map<Element, List<VNodeTemplateInfo>> allTemplateInfos = new HashMap<>();

        // scan for templates
        for (Element rootElt : roundEnv.getRootElements()) {
            Env env = Env.create(processingEnv, rootElt);
            Tree node = env.tree(rootElt);
            List<VNodeTemplateInfo> scanned = node.accept(new VNodeTemplateScanner(), env);
            if (scanned != null && !scanned.isEmpty()) {
                allTemplateInfos.computeIfAbsent(rootElt, e -> new ArrayList<>()).addAll(scanned);
            }
        }

        if (allTemplateInfos.isEmpty()) {
            return false;
        }

        try {
            // map of all generated class references keyed by package
            Map<String, Map<String, VNodeTemplateInfo>> packages = new HashMap<>();

            // generate all template classes
            for (Entry<Element, List<VNodeTemplateInfo>> entry : allTemplateInfos.entrySet()) {
                Element element = entry.getKey();
                List<VNodeTemplateInfo> templateInfos = entry.getValue();
                for (int i = 0; i < templateInfos.size(); i++) {
                    VNodeTemplateInfo templateInfo = templateInfos.get(i);
                    String name = generateTemplateImpl(element, templateInfo, i + 1);
                    packages.computeIfAbsent(templateInfo.pkg(), pkg -> new HashMap<>()).put(name, templateInfo);
                }
            }

            // generate one provider class per package
            List<String> providers = new ArrayList<>();
            for (Entry<String, Map<String, VNodeTemplateInfo>> entry : packages.entrySet()) {
                String pkg = entry.getKey();
                Map<String, VNodeTemplateInfo> templates = entry.getValue();
                String qName = generateProviderImpl(templates, pkg);
                providers.add(qName);
            }

            // META-INF/services
            generateServiceFile(providers);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        done = true;
        return false;
    }

    private String generateTemplateImpl(Element element,
                                        VNodeTemplateInfo template,
                                        int index) throws IOException {

        Filer filer = processingEnv.getFiler();
        String pkg = template.pkg();
        String className = (template.name() == null ? "T" : template.name()) + String.valueOf(index);
        String qName = pkg + "." + className;
        JavaFileObject fileObject = filer.createSourceFile(qName, element);

        // gather all imports first.
        Set<CharSequence> imports = new HashSet<>();
        imports.add("com.acme.api.vdom.VNode");
        imports.add("com.acme.api.vdom.VNodeTemplate");
        for (VNodeTemplateArgInfo arg : template.args()) {
            imports.add(arg.type().getQualifiedName());
        }

        List<VNodeTemplateArgInfo> args = template.args();
        try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
            bw.append("package ").append(pkg).append(";")
              .append("\n");
            for (CharSequence importQName : imports) {
                bw.append("\nimport ").append(importQName).append(";");
            }
            bw.append("\n")
              .append("\n/**")
              .append("\n * Generated template implementation.")
              .append("\n */")
              .append("\nclass ").append(className).append(" implements VNodeTemplate {")
              .append("\n")
              .append("\n    ").append("@Override")
              .append("\n    ").append("public VNode render(Object... args) {");
            for (int i = 0; i < args.size(); i++) {
                VNodeTemplateArgInfo argInfo = args.get(i);
                Name argType = argInfo.type().getSimpleName();
                bw.append("\n        ")
                  .append(argType).append(" ").append(argInfo.name())
                  .append(" = (").append(argType).append(") ").append("args[").append(String.valueOf(i)).append("];")
                  .append("\n        return null;");
            }
            bw.append("\n    }")
              .append("\n}")
              .append("\n");
        }
        return className;
    }

    private String generateProviderImpl(Map<String, VNodeTemplateInfo> templates, String pkg) throws IOException {
        String className = "VNodeTemplateProviderImpl";
        String qName = pkg + "." + className;
        Filer filer = processingEnv.getFiler();
        JavaFileObject fileObject = filer.createSourceFile(qName);
        try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
            bw.append("package ").append(pkg).append(";")
              .append("\n")
              .append("\nimport com.acme.api.vdom.VNodeTemplateProvider;")
              .append("\nimport com.acme.api.vdom.VNodeTemplate;")
              .append("\n")
              .append("\n/**")
              .append("\n * Template provider implementation for package {@code ").append(pkg).append("}.")
              .append("\n */")
              .append("\npublic class ").append(className).append(" extends VNodeTemplateProvider {")
              .append("\n")
              .append("\n    /**")
              .append("\n     * Create a new instance.")
              .append("\n     */")
              .append("\n    public ").append(className).append("() {");
            Set<Entry<String, VNodeTemplateInfo>> entries = templates.entrySet();
            for (Entry<String, VNodeTemplateInfo> entry : entries) {
                int id = entry.getValue().literal().hashCode();
                String templateClassName = entry.getKey();
                bw.append(String.format("\n        register(%s, %s::new);", id, templateClassName));
            }
            bw.append("\n    }")
              .append("\n}")
              .append("\n");
        }
        return qName;
    }

    private void generateServiceFile(List<String> providers) throws IOException {
        Filer filer = processingEnv.getFiler();
        FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", SERVICE_FILE);
        for (String provider : providers) {
            try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
                bw.append(provider).append("\n");
            }
        }
    }
}
