package com.acme.codegen;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * {@link javax.tools.JavaFileObject} backed by a string.
 */
class JavaStringObject extends SimpleJavaFileObject {

    private final String code;

    /**
     * Create a new instance.
     *
     * @param name class name
     * @param code source code
     */
    JavaStringObject(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
