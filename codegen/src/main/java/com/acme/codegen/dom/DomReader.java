package com.acme.codegen.dom;

import java.util.Map;

/**
 * DOM Reader.
 */
@SuppressWarnings("unused")
public interface DomReader {

    /**
     * Receive notification of the start of an element.
     *
     * @param name       the element name
     * @param attributes the element attributes
     */
    default void startElement(String name, Map<String, String> attributes) {
    }

    /**
     * Receive notification of the end of an element.
     *
     * @param name the element name
     */
    default void endElement(String name) {
    }

    /**
     * Receive notification of text data inside an element.
     *
     * @param data the text data
     */
    default void elementText(String data) {
    }

    /**
     * Continue action, can be overridden to stop parsing.
     *
     * @return {@code true} to keep parsing, {@code false} to stop parsing
     */
    default boolean keepParsing() {
        return true;
    }
}
