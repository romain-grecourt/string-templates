package com.acme.codegen;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

import java.util.function.Function;

record ScopeLookup(Trees trees, CompilationUnitTree unit) implements Function<Tree, Scope> {

    Scope lookup(Tree node) {
        return trees.getScope(trees.getPath(unit, node));
    }

    @Override
    public Scope apply(Tree node) {
        return lookup(node);
    }
}
