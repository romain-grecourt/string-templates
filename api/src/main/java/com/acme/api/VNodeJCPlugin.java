package com.acme.api;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StringTemplateTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

public class VNodeJCPlugin implements Plugin {

    @Override
    public String getName() {
        return "v-node-template";
    }

    final Set<CompilationUnitTree> units = new HashSet<>();

    @Override
    public void init(JavacTask task, String... args) {
        Trees trees = Trees.instance(task);
        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent event) {
                if (event.getKind() == TaskEvent.Kind.ANALYZE) {
                    CompilationUnitTree unit = event.getCompilationUnit();
                    if (unit.getKind() == Tree.Kind.COMPILATION_UNIT) {
                        if (units.contains(unit)) {
                            return;
                        }
                        units.add(unit);
                        process(trees, unit);
                    }
                }
            }
        });
    }

    private List<StringTemplateTree> templates (Trees trees,  CompilationUnitTree unit) {
        List<StringTemplateTree> templates = new ArrayList<>();
        unit.accept(new TreeScanner<Void, Void>() {
            @Override
            public Void visitStringTemplate(StringTemplateTree node, Void arg) {
                String element = node.getProcessor().accept(new IdentifierResolver(trees, unit), null);
                if ("com.acme.api.VNodeProcessor$HTML".equals(element)) {
                    templates.add(node);
                }
                return null;
            }
        }, null);
        return templates;
    }

    private void process(Trees trees,  CompilationUnitTree unit) {
        List<StringTemplateTree> templates = templates(trees, unit);
        if (!templates.isEmpty()) {
            System.out.println(templates);
        }
        // TODO parse fragments
        // TODO generate code
        // TODO SPI
        //  fragment -> VNodeTemplate (match using String.equals)
        //  VNodeTemplate + value -> VNode
        // TODO template loops
    }

    private static final class IdentifierResolver extends TreeScanner<String, Void> {

        private final CompilationUnitTree unit;
        private final Trees trees;

        IdentifierResolver(Trees trees, CompilationUnitTree unit) {
            this.unit = unit;
            this.trees = trees;
        }

        @Override
        public String visitIdentifier(IdentifierTree node, Void arg) {
            TreePath path = trees.getPath(unit, node);
            Scope scope = trees.getScope(path);
            while (scope.getEnclosingScope() != null) {
                for (Element elt : scope.getLocalElements()) {
                    if (elt.getSimpleName() == node.getName()) {
                        String name = node.getName().toString();
                        Element enclosing = elt.getEnclosingElement();
                        if (enclosing instanceof QualifiedNameable) {
                            return ((QualifiedNameable) enclosing).getQualifiedName() + "$" + name;
                        }
                        return name;
                    }
                }
                scope = scope.getEnclosingScope();
            }
            return null;
        }
    }
}
