package io.xsun.xcs4j.parser;

import io.xsun.xcs4j.base.Xcs4jException;

public class ParserException extends Xcs4jException {
    public ParserException() {
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }
}
