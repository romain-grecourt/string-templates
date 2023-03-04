package com.acme.codegen;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.StringTemplateTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

@SupportedAnnotationTypes(value = {"*"})
public class VNodeTemplateProcessor extends AbstractProcessor {

    private static final String SERVICE_FILE = "META-INF/services/com.acme.api.VNodeTemplateProvider";
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
        Map<Element, List<StringTemplateTree>> allTemplates = new HashMap<>();

        // scan for templates
        for (Element rootElt : roundEnv.getRootElements()) {
            TreePath path = trees.getPath(rootElt);
            Tree node = path.getLeaf();
            CompilationUnitTree unit = path.getCompilationUnit();
            List<StringTemplateTree> scanned = node.accept(new StringTemplateScanner(), null);
            if (scanned != null && !scanned.isEmpty()) {
                List<StringTemplateTree> templates = allTemplates.computeIfAbsent(rootElt, e -> new ArrayList<>());
                for (StringTemplateTree stt : scanned) {
                    String qName = stt.getProcessor().accept(new QNameResolver(), new ScopeLookup(trees, unit));
                    if ("com.acme.api.VNodeProcessor$HTML".equals(qName)) {
                        templates.add(stt);
                    }
                }
            }
        }

        if (allTemplates.isEmpty()) {
            return false;
        }

        // generate code
        Filer filer = processingEnv.getFiler();
        for (Map.Entry<Element, List<StringTemplateTree>> entry : allTemplates.entrySet()) {
            Element element = entry.getKey();
            String[] fragments = fragments(entry.getValue());
            String pkg = packageName(element);
            String className = "VNodeTemplateProviderImpl";
            String qName = pkg + "." + className;
            try {
                // generate provider implementation
                JavaFileObject fileObject = filer.createSourceFile(qName, element);
                try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
                    bw.append("package ").append(pkg).append(";")
                      .append("\n")
                      .append("\nimport com.acme.api.VNodeTemplateProvider;")
                      .append("\nimport com.acme.api.VNodeTemplate;")
                      .append("\n")
                      .append("\npublic class ").append(className).append(" extends VNodeTemplateProvider {")
                      .append("\n")
                      .append("\n    private static final String[] FRAGMENTS = new String[]{");
                    for (int i = 0; i < fragments.length; i++) {
                        bw.append("\n        \"")
                          .append(fragments[i].replace("\n", "\\n"))
                          .append("\"");
                        if (i < fragments.length + 1) {
                            bw.append(",");
                        }
                    }
                    bw.append("\n    };")
                      .append("\n")
                      .append("\n    public ").append(className).append("() {");
                    for (int i = 0; i < fragments.length; i++) {
                        // FIXME the generated code is a dummy place-holder
                        String ref = "FRAGMENTS[" + i + "]";
                        bw.append(String.format("\n        register(%s, () -> new VNodeTemplate(%s));", ref, ref));
                    }
                    bw.append("\n    }")
                      .append("\n}")
                      .append("\n");
                }

                // META-INF/services
                FileObject serviceFileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", SERVICE_FILE);
                try (BufferedWriter bw = new BufferedWriter(serviceFileObject.openWriter())) {
                    bw.append(qName).append("\n");
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        done = true;
        return false;
    }

    private String packageName(Element element) {
        TreePath path = trees.getPath(element);
        CompilationUnitTree unit = path.getCompilationUnit();
        return unit.getPackageName().toString();
    }

    private static String[] fragments(List<StringTemplateTree> nodes) {
        return nodes.stream()
                    .flatMap(node -> node.getFragments().stream())
                    .distinct()
                    .toArray(String[]::new);
    }
}
