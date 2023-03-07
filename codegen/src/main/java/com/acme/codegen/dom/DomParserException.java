package com.acme.codegen.dom;

/**
 * DOM parser exception.
 */
public class DomParserException extends RuntimeException {

    /**
     * Create a new DOM parser exception.
     *
     * @param msg message
     */
    protected DomParserException(String msg) {
        super(msg);
    }
}
