package com.acme.codegen;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.acme.codegen.utils.Pair;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;

import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * Generic annotation processors that scan usages of {@code com.acme.api.vdom.VNodeCompiler.h}.
 */
@SupportedAnnotationTypes(value = {"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class VNodeCompilerAP extends AbstractProcessor {

    private static final String SERVICE_FILE = "META-INF/services/com.acme.api.vdom.VNodeTemplateProvider";
    private boolean done;
    private Env env;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        try {
            env = Env.create(processingEnv);
        } catch (Throwable ex) {
            Messager messager = processingEnv.getMessager();
            messager.printMessage(MANDATORY_WARNING, "JDK compiler internal API not available");
            env = null;
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (done || env == null) {
            return false;
        }
        try {
            List<Pair<MethodInvocationTree, Lookup>> templates = new ArrayList<>();

            // scan for templates
            for (Element rootElt : roundEnv.getRootElements()) {
                Lookup lookup = env.lookup(rootElt);
                Tree node = lookup.tree(rootElt);
                List<MethodInvocationTree> scanned = node.accept(new VNodeTemplateScanner(), lookup);
                if (scanned != null && !scanned.isEmpty()) {
                    for (MethodInvocationTree inv : scanned) {
                        templates.add(new Pair<>(inv, lookup));
                    }
                }
            }

            for (Pair<MethodInvocationTree, Lookup> entry : templates) {
                MethodInvocationTree inv = entry.first();
                Lookup lookup = entry.second();
                Tree node = VNodeTemplateBodyParser.parse(inv, lookup);
                InvocationTranslator.translate(inv, node);
            }

            if (templates.isEmpty()) {
                return false;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        done = true;
        return false;
    }
//
//    private String generateTemplateImpl(Element element, VNodeTemplateInfo template) throws IOException {
//        Filer filer = processingEnv.getFiler();
//        String className = template.simpleName();
//        String qName = template.pkg() + "." + className;
//        JavaFileObject fileObject = filer.createSourceFile(qName, element);
//        try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
//            bw.append(VNodeTemplateGenerator.generate(template));
//        }
//        return className;
//    }
//
//    private String generateProviderImpl(Map<String, VNodeTemplateInfo> templates, String pkg) throws IOException {
//        String qName = pkg + "." + VNodeTemplateProviderGenerator.CNAME;
//        Filer filer = processingEnv.getFiler();
//        JavaFileObject fileObject = filer.createSourceFile(qName);
//        try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
//            bw.append(VNodeTemplateProviderGenerator.generate(templates, pkg));
//        }
//        return qName;
//    }
//
//    private void generateServiceFile(List<String> providers) throws IOException {
//        Filer filer = processingEnv.getFiler();
//        FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", SERVICE_FILE);
//        for (String provider : providers) {
//            try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
//                bw.append(provider).append("\n");
//            }
//        }
//    }
}
