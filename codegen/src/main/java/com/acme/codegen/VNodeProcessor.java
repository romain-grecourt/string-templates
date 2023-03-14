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
public class VNodeProcessor extends AbstractProcessor {

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
                String rawTemplate = StringLiteral.of(inv.getArguments().get(0));
                String templateCode = VNodeGenerator.generate(rawTemplate);
                System.out.println(templateCode);
                Tree newNode = lookup.parse(inv, templateCode);
                InvocationTranslator.translate(inv, newNode);
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
