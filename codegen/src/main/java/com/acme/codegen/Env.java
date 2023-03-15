package com.acme.codegen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

/**
 * Compiler environment.
 *
 * @param types     types utility
 * @param trees     trees utility
 * @param compiler  compiler
 * @param treeMaker tree maker
 */
record Env(Types types, Trees trees, JavaCompiler compiler, TreeMaker treeMaker) {

    private static final Map<CompilationUnitTree, Lookup> LOOKUPS = new ConcurrentHashMap<>();

    /**
     * Create a new lookup from the given element.
     *
     * @param element element
     * @return Lookup
     */
    Lookup lookup(Element element) {
        CompilationUnitTree unit = trees.getPath(element).getCompilationUnit();
        return LOOKUPS.computeIfAbsent(unit, u -> new Lookup(this, u));
    }

    /**
     * Parse the given source.
     *
     * @param name   class name
     * @param source source
     * @return CompilationUnitTree
     */
    CompilationUnitTree parse(String name, CharSequence source) {
        JavaFileObject fileObject = new JavaSourceObject(name, source);
        return compiler.parse(fileObject);
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
