package com.acme.codegen.dom;

/**
 * DOM parser exception.
 */
public class DomParserException extends IllegalStateException {

    /**
     * Create a new DOM parser exception.
     *
     * @param msg message
     */
    DomParserException(String msg) {
        super(msg);
    }
}
