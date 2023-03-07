package com.acme.codegen;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * Generic annotation processors that scan usages of {@code com.acme.api.vdom.VNodeCompiler.h}.
 */
@SupportedAnnotationTypes(value = {"*"})
public class VNodeCompilerAP extends AbstractProcessor {

    private static final String SERVICE_FILE = "META-INF/services/com.acme.api.vdom.VNodeTemplateProvider";
    private Trees trees;
    private boolean done;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (done) {
            return false;
        }
        Map<Element, List<VNodeTemplateInfo>> allTemplateInfos = new HashMap<>();

        // scan for templates
        for (Element rootElt : roundEnv.getRootElements()) {
            TreePath path = trees.getPath(rootElt);
            Tree node = path.getLeaf();
            CompilationUnitTree unit = path.getCompilationUnit();
            Lookup lookup = new Lookup(processingEnv.getTypeUtils(), trees, unit);
            List<VNodeTemplateInfo> scanned = node.accept(new VNodeTemplateScanner(), lookup);
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
                String pkg = packageName(element);
                Map<String, VNodeTemplateInfo> templateClasses = packages.computeIfAbsent(pkg, p -> new HashMap<>());
                for (int i = 0; i < templateInfos.size(); i++) {
                    VNodeTemplateInfo templateInfo = templateInfos.get(i);
                    String name = generateTemplateImpl(element, templateInfo, i + 1, pkg);
                    templateClasses.put(name, templateInfo);
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
                                        int index,
                                        String pkg) throws IOException {

        Filer filer = processingEnv.getFiler();
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
                  .append(" = (").append(argType).append(") ").append("args[").append(String.valueOf(i)).append("];");
                // TODO use DomParser and generate the actual code..
                bw.append("\n        return null;");
            }
            bw.append("\n    }")
              .append("\n}")
              .append("\n");
        }
        return qName;
    }

    private String generateProviderImpl(Map<String, VNodeTemplateInfo> templates, String pkg) throws IOException {
        String className = "VNodeTemplateProviderImpl";
        String qName = pkg + "." + className;
        Filer filer = processingEnv.getFiler();
        JavaFileObject fileObject = filer.createSourceFile(qName);
        try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
            bw.append("package ").append(pkg).append(";")
              .append("\n")
              .append("\nimport com.acme.api.VNodeTemplateProvider;")
              .append("\nimport com.acme.api.VNodeTemplate;")
              .append("\n")
              .append("\n/**")
              .append("\n * Template provider implementation for package {@code ").append(pkg).append("}.")
              .append("\n */")
              .append("\npublic class ").append(className).append(" extends VNodeTemplateProvider {")
              .append("\n")
              .append("\n    private static final Object[] IDS = new Object[]{");
            Set<Entry<String, VNodeTemplateInfo>> entries = templates.entrySet();
            for (int i = 0; i < entries.size(); i++) {
                Iterator<Entry<String, VNodeTemplateInfo>> it = entries.iterator();
                Entry<String, VNodeTemplateInfo> entry = it.next();
                VNodeTemplateInfo template = entry.getValue();
                bw.append("\n        \"")
                  .append(escape(template.literal()))
                  .append("\"");
                if (it.hasNext()) {
                    bw.append(",");
                }
            }
            bw.append("\n    };")
              .append("\n")
              .append("\n    /**")
              .append("\n     * Create a new instance.")
              .append("\n     */")
              .append("\n    public ").append(className).append("() {");
            for (int i = 0; i < templates.size(); i++) {
                String ref = "IDS[" + i + "]";
                bw.append(String.format("\n        register(%s, () -> null);", ref));
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

    private String packageName(Element element) {
        TreePath path = trees.getPath(element);
        CompilationUnitTree unit = path.getCompilationUnit();
        return unit.getPackageName().toString();
    }

    private static String escape(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\n", "\\n")
                  .replace("\b", "\\b")
                  .replace("\t", "\\t")
                  .replace("\r", "\\r")
                  .replace("\"", "\\");
    }
}
