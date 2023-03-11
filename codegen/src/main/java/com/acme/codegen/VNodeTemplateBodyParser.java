package com.acme.codegen;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VNodeTemplateBodyParser {

    static Tree parse(MethodInvocationTree inv, Lookup lookup) throws IOException {
        List<? extends ExpressionTree> arguments = inv.getArguments();
        String raw = StringLiteral.of(arguments.get(0));

        // TODO find the position of the start of the current line
        //  and derive indentation (good for debugging)
        String body = VNodeTemplateBodyGenerator.generate(raw);

        // TODO generate a source file that matches the scope
        long startPos = lookup.startPosition(inv);
        long endPos = lookup.getEndPosition(inv);
        CharSequence source = lookup.unit().getSourceFile().getCharContent(true);
        CharSequence before = source.subSequence(0, (int) startPos);
        CharSequence after = source.subSequence((int) endPos, source.length() - 1);
        String code = before + body + after;

        Env env = lookup.env();
        JavaCompiler compiler = env.compiler();

        Path location = env.sourceLocation();
        Path tmp = location.getParent().resolve("tmp");
        System.out.println("LOCATION: " + tmp);

        // TODO add start position as suffix
        Path sourceFile = tmp.resolve(lookup.enclosingClassName(inv).replace('.', '/') + ".java");
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, code);
        JavaFileObject fileObject = new JavaPathObject(sourceFile);
        JCTree.JCCompilationUnit newUnit = compiler.parse(fileObject);

        // TODO Once we switch to a scope instead of a copy
        //  we need to find a new way to grab the node from newUnit
        Tree previous = lookup.previous(inv);
        long previousPos = lookup.startPosition(previous);
        return lookup.find(newUnit, previousPos);
    }

}
