package com.acme.codegen;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple {@link javax.tools.JavaFileObject} backed by a {@link Path}.
 */
class JavaPathObject extends SimpleJavaFileObject {

    private final Path path;

    /**
     * Create a new instance.
     *
     * @param path path
     */
    JavaPathObject(Path path) {
        super(path.toUri(), Kind.SOURCE);
        this.path = path;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return Files.readString(path);
    }
}
