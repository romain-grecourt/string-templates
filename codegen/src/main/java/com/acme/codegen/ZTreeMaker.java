package com.acme.codegen;

import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * {@link com.sun.tools.javac.tree.TreeMaker} facade.
 */
class ZTreeMaker {

    private static final Map<ProcessingEnvironment, ZTreeMaker> INSTANCES = new IdentityHashMap<>();
    private final TreeMaker treeMaker;

    private ZTreeMaker(TreeMaker treeMaker) {
        this.treeMaker = treeMaker;
    }

    /**
     * Create a new instance.
     *
     * @param processingEnv processing environment
     * @return TreeMaker0
     */
    static ZTreeMaker instance(ProcessingEnvironment processingEnv) {
        return INSTANCES.computeIfAbsent(processingEnv, pEnv -> {
            try {
                Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
                return new ZTreeMaker(TreeMaker.instance(context));
            } catch (Throwable ex) {
                Messager messager = processingEnv.getMessager();
                messager.printMessage(MANDATORY_WARNING, "JDK compiler internal API not available");
                return new ZTreeMaker(null);
            }
        });
    }

    /**
     * Test if {@link TreeMaker} is enabled.
     *
     * @return {@code true} if enabled, {@code false} otherwise
     */
    boolean isEnabled() {
        return treeMaker != null;
    }

    /**
     * Create a new literal.
     *
     * @param value value
     * @return literal
     */
    JCTree.JCLiteral Literal(Object value) {
        if (treeMaker == null) {
            throw new UnsupportedOperationException();
        }
        return treeMaker.Literal(value);
    }
}
