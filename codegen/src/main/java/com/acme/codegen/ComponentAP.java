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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.source.tree.Tree;

import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * Generic annotation processors that scan usages of {@code com.acme.api.Component.h}.
 */
@SupportedAnnotationTypes(value = {"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ComponentAP extends AbstractProcessor {

    private static final String COMPONENT_QNAME = "com.acme.api.Component";
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
            Map<String, TypeElement> components = new HashMap<>();
            List<VNodeTemplate> templates = new ArrayList<>();

            // scan for templates
            for (Element elt : roundEnv.getRootElements()) {
                if (!env.inherits(elt, COMPONENT_QNAME)) {
                    continue;
                }
                components.put(elt.getSimpleName().toString(), (TypeElement) elt);
                Lookup lookup = env.lookup(elt);
                Tree node = lookup.tree(elt);
                List<VNodeTemplate> scanned = node.accept(new VNodeTemplateScanner(), lookup);
                if (scanned != null) {
                    templates.addAll(scanned);
                }
            }

            for (VNodeTemplate template : templates) {
                template.apply(components);
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
}
