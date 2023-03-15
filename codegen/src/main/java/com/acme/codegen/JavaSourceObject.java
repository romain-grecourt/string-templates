package com.acme.codegen;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * {@link javax.tools.JavaFileObject} backed by a {@link CharSequence}
 */
class JavaSourceObject extends SimpleJavaFileObject {

    private final CharSequence code;

    /**
     * Create a new instance.
     *
     * @param name class name
     * @param code source code
     */
    JavaSourceObject(String name, CharSequence code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
