package io.xsun.xcs4j.base;

public class Xcs4jException extends RuntimeException {
    public Xcs4jException() {
    }

    public Xcs4jException(String message) {
        super(message);
    }

    public Xcs4jException(String message, Throwable cause) {
        super(message, cause);
    }

    public Xcs4jException(Throwable cause) {
        super(cause);
    }
}
