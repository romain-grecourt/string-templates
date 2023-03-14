package com.acme.codegen;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;

import com.sun.source.util.Trees;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

/**
 * Compiler environment.
 *
 * @param types       types utility
 * @param trees       trees utility
 * @param compiler    compiler
 * @param treeMaker   tree maker
 */
record Env(Types types, Trees trees, JavaCompiler compiler, TreeMaker treeMaker) {

    /**
     * Create a new lookup from the given element.
     *
     * @param element element
     * @return Lookup
     */
    Lookup lookup(Element element) {
        return new Lookup(this, trees.getPath(element).getCompilationUnit());
    }

    /**
     * Create a new env.
     *
     * @param processingEnv processing environment
     * @return Env
     */
    static Env create(ProcessingEnvironment processingEnv) {
        Types types = processingEnv.getTypeUtils();
        Trees trees = Trees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        JavaCompiler compiler = JavaCompiler.instance(context);
        TreeMaker treeMaker = TreeMaker.instance(context);
        return new Env(types, trees, compiler, treeMaker);
    }
}
