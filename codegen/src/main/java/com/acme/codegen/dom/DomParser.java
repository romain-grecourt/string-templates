package com.acme.codegen.dom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * DOM parser.
 */
public final class DomParser {

    private enum STATE {
        START,
        COMMENT,
        ELEMENT,
        END_ELEMENT,
        NAME,
        TEXT,
        ATTRIBUTES,
        ATTRIBUTE_VALUE,
        EXPR_KEY,
        EXPR_VALUE,
        SINGLE_QUOTED_VALUE,
        DOUBLE_QUOTED_VALUE,
    }

    private static final String COMMENT_START = "<!--";
    private static final String COMMENT_END = "-->";
    private static final String ELEMENT_SELF_CLOSE = "/>";
    private static final String CLOSE_MARKUP_START = "</";
    private static final char MARKUP_START = '<';
    private static final char MARKUP_END = '>';
    private static final char EXPR_KEY1 = ':';
    private static final char EXPR_KEY2 = '@';
    private static final char ATTRIBUTE_VALUE = '=';
    private static final String EXPR_START = "{{";
    private static final String EXPR_END = "}}";
    private static final char DOUBLE_QUOTE = '"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char[] ALLOWED_CHARS = new char[]{'_', '.', '-', ':'};

    private final char[] buf = new char[1024];
    private char c;
    private int position;
    private int lastPosition;
    private int limit = 0;
    private int lineNo = 1;
    private int charNo = 0;
    private StringBuilder nameBuilder = new StringBuilder();
    private StringBuilder textBuilder = new StringBuilder();
    private StringBuilder attrNameBuilder = new StringBuilder();
    private StringBuilder attrValueBuilder = new StringBuilder();
    private Map<String, String> attributes = new HashMap<>();
    private STATE state = STATE.START;
    private STATE resumeState = null;
    private final LinkedList<String> stack = new LinkedList<>();
    private final DomReader reader;
    private final Reader ir;

    /**
     * Create a new instance.
     *
     * @param is     input string
     * @param reader reader
     */
    public DomParser(String is, DomReader reader) {
        this.ir = new StringReader(is);
        this.reader = Objects.requireNonNull(reader, "reader is null");
    }

    /**
     * Get the current line number.
     *
     * @return line number
     */
    public int lineNumber() {
        return lineNo;
    }

    /**
     * Get the current line character number.
     *
     * @return line character number
     */
    public int charNumber() {
        return charNo;
    }

    private void processStart() {
        if (hasToken(MARKUP_START)) {
            state = STATE.ELEMENT;
        } else {
            position++;
        }
    }

    private void processElement() throws IOException {
        if (hasToken(COMMENT_START)) {
            state = STATE.COMMENT;
            resumeState = STATE.ELEMENT;
            position += COMMENT_START.length();
        } else if (hasToken(CLOSE_MARKUP_START)) {
            state = STATE.END_ELEMENT;
            position++;
        } else if (hasToken(MARKUP_START)) {
            resumeState = STATE.ATTRIBUTES;
            state = STATE.NAME;
            position++;
        } else if (Character.isWhitespace(c)) {
            position++;
        } else {
            state = STATE.TEXT;
            textBuilder.append(c);
            position++;
        }
    }

    private void processName() throws IOException {
        if (hasToken(MARKUP_END) || hasToken(ELEMENT_SELF_CLOSE)) {
            state = resumeState;
        } else if (Character.isWhitespace(c)) {
            position++;
            state = resumeState;
        } else {
            validateNameChar(c, nameBuilder.length() == 0);
            position++;
            nameBuilder.append(c);
        }
    }

    private void processEndElement() {
        if (Character.isWhitespace(c)) {
            position++;
        } else if (hasToken(MARKUP_END)) {
            String name = nameBuilder.toString();
            if (stack.isEmpty()) {
                throw new DomParserException(String.format(
                        "Missing opening element: %s", name));
            }
            String parentName = stack.pop();
            if (!name.equals(parentName)) {
                throw new DomParserException(String.format(
                        "Invalid closing element: %s, expecting: %s, line: %d, char: %d",
                        name, parentName, lineNo, charNo));
            }
            position++;
            state = STATE.ELEMENT;
            reader.elementText(textBuilder.toString());
            reader.endElement(name);
            nameBuilder = new StringBuilder();
            textBuilder = new StringBuilder();
        } else {
            resumeState = STATE.END_ELEMENT;
            state = STATE.NAME;
            position++;
        }
    }

    private void processAttributes() throws IOException {
        if (Character.isWhitespace(c)) {
            position++;
        } else if (hasToken(ELEMENT_SELF_CLOSE)) {
            position += ELEMENT_SELF_CLOSE.length();
            state = STATE.ELEMENT;
            String name = nameBuilder.toString();
            reader.startElement(name, attributes);
            reader.endElement(name);
            nameBuilder = new StringBuilder();
            attributes = new HashMap<>();
        } else if (hasToken(MARKUP_END)) {
            position++;
            state = STATE.ELEMENT;
            String name = nameBuilder.toString();
            stack.push(name);
            reader.startElement(name, attributes);
            nameBuilder = new StringBuilder();
            attributes = new HashMap<>();
        } else if (hasToken(ATTRIBUTE_VALUE)) {
            position++;
            state = STATE.ATTRIBUTE_VALUE;
        } else if (hasToken(EXPR_KEY1) || hasToken(EXPR_KEY2)) {
            state = STATE.EXPR_KEY;
        } else {
            position++;
            validateNameChar(c, attrNameBuilder.length() == 0);
            attrNameBuilder.append(c);
        }
    }

    private void processAttributeValue() {
        if (Character.isWhitespace(c)) {
            position++;
        } else if (hasToken(SINGLE_QUOTE)) {
            position++;
            state = STATE.SINGLE_QUOTED_VALUE;
        } else if (hasToken(DOUBLE_QUOTE)) {
            position++;
            state = STATE.DOUBLE_QUOTED_VALUE;
        } else {
            throw new DomParserException(String.format(
                    "Invalid state, line: %d, char: %d", lineNo, charNo));
        }
    }

    private void processExprKey() throws IOException {
        if (Character.isWhitespace(c)) {
            position++;
            state = STATE.ATTRIBUTES;
            attributes.put(attrNameBuilder.toString(), "");
        } else if (hasToken(MARKUP_END) || hasToken(ELEMENT_SELF_CLOSE)) {
            state = resumeState;
            attributes.put(attrNameBuilder.toString(), "");
        } else if (hasToken(ATTRIBUTE_VALUE)) {
            position++;
            state = STATE.EXPR_VALUE;
        } else {
            position++;
            validateNameChar(c, attrNameBuilder.length() == 0);
            attrNameBuilder.append(c);
        }
    }

    private void processExprValue() throws IOException {
        if (hasToken(EXPR_START)) {
            position += 2;
            state = STATE.EXPR_VALUE;
        } else if (hasToken(EXPR_END)) {
            position += 2;
            state = STATE.ATTRIBUTES;
            attributes.put(attrNameBuilder.toString(), attrValueBuilder.toString());
            attrNameBuilder = new StringBuilder();
            attrValueBuilder = new StringBuilder();
        } else {
            position++;
            attrValueBuilder.append(c);
        }
    }

    private void processQuoteValue(char token) {
        if (hasToken(token)) {
            position++;
            state = STATE.ATTRIBUTES;
            attributes.put(attrNameBuilder.toString(), attrValueBuilder.toString());
            attrNameBuilder = new StringBuilder();
            attrValueBuilder = new StringBuilder();
        } else {
            validateAttrValueChar(c);
            position++;
            attrValueBuilder.append(c);
        }
    }

    private void processText() throws IOException {
        if (hasToken(COMMENT_START)) {
            state = STATE.COMMENT;
            resumeState = STATE.TEXT;
            position += COMMENT_START.length();
        } else if (hasToken(MARKUP_START)) {
            state = STATE.ELEMENT;
        } else {
            textBuilder.append(buf[position]);
            position++;
        }
    }

    private void processComment() throws IOException {
        if (hasToken(COMMENT_END)) {
            state = resumeState;
            position += COMMENT_END.length();
        } else {
            position++;
        }
    }

    /**
     * Start parsing.
     *
     * @throws IOException if an IO error occurs
     */
    public void parse() throws IOException {
        while (limit >= 0) {
            position = 0;
            limit = ir.read(buf);
            while (position < limit && reader.keepParsing()) {
                c = buf[position];
                if (c == '\n') {
                    lineNo++;
                    charNo = 1;
                }
                lastPosition = position;
                switch (state) {
                    case START -> processStart();
                    case ELEMENT -> processElement();
                    case END_ELEMENT -> processEndElement();
                    case NAME -> processName();
                    case ATTRIBUTES -> processAttributes();
                    case ATTRIBUTE_VALUE -> processAttributeValue();
                    case EXPR_KEY -> processExprKey();
                    case EXPR_VALUE -> processExprValue();
                    case SINGLE_QUOTED_VALUE -> processQuoteValue(SINGLE_QUOTE);
                    case DOUBLE_QUOTED_VALUE -> processQuoteValue(DOUBLE_QUOTE);
                    case TEXT -> processText();
                    case COMMENT -> processComment();
                    default -> throw new DomParserException(String.format(
                            "Unknown state: %s, line: %d, char: %d", state, lineNo, charNo));
                }
                charNo += (position - lastPosition);
            }
        }
        if (reader.keepParsing()) {
            if (!stack.isEmpty()) {
                throw new DomParserException(String.format("Unclosed element: %s", stack.peek()));
            }
            if (state != STATE.ELEMENT) {
                throw new DomParserException(String.format("Invalid state: %s", state));
            }
        }
    }

    private void validateAttrValueChar(char c) {
        if (c == MARKUP_START || c == MARKUP_END) {
            throw new DomParserException(String.format(
                    "Invalid character found in value: '%c', line: %d, char: %d", c, lineNo, charNo));
        }
    }

    private void validateNameChar(char c, boolean firstChar) {
        if (!((firstChar && (c == ':' || c == '@')) || isAllowedChar(c))) {
            throw new DomParserException(String.format(
                    "Invalid character found in name: '%c', line: %d, char: %d", c, lineNo, charNo));
        }
    }

    boolean hasToken(char expected) {
        return position < limit && buf[position] == expected;
    }

    boolean hasToken(CharSequence expected) throws IOException {
        int len = expected.length();
        if (position + len > limit) {
            int offset = limit - position;
            System.arraycopy(buf, position, buf, 0, offset);
            int read = ir.read(buf, offset, buf.length - offset);
            limit = offset + (read == -1 ? 0 : read);
            position = 0;
            lastPosition = 0;
        }
        return String.valueOf(buf, position, expected.length()).contentEquals(expected);
    }

    private static boolean isAllowedChar(char c) {
        if (Character.isLetter(c) || Character.isDigit(c)) {
            return true;
        }
        for (char a : ALLOWED_CHARS) {
            if (a == c) {
                return true;
            }
        }
        return false;
    }
}
