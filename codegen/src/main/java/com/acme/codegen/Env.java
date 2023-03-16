package com.acme.codegen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
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
     * Get the type for the given element.
     *
     * @param element element
     * @return TypeElement
     */
    TypeElement typeElement(Element element) {
        return typeElement(element.asType());
    }

    /**
     * Get the type for the given type mirror.
     *
     * @param typeMirror type mirror
     * @return TypeElement
     */
    TypeElement typeElement(TypeMirror typeMirror) {
        return (TypeElement) types.asElement(typeMirror);
    }

    /**
     * Get the super class.
     *
     * @param element element
     * @return TypeElement or {@code null}
     */
    TypeElement superClass(Element element) {
        if (element instanceof TypeElement) {
            return typeElement(((TypeElement) element).getSuperclass());
        }
        return null;
    }

    /**
     * Test if the given (type) element inherits the given type.
     *
     * @param element element
     * @param qName   qualified name of the inherited type
     * @return {@code true} if found, {@code false} otherwise
     */
    boolean inherits(Element element, String qName) {
        TypeElement superclass = superClass(element);
        while (superclass != null) {
            if (superclass.getQualifiedName().contentEquals(qName)) {
                return true;
            }
            superclass = superClass(superclass);
        }
        return false;
    }

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
