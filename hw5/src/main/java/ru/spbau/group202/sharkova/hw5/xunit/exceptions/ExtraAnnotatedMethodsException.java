package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

/**
 * Custom exception class for classes with too many annotated methods (like two 'before' methods).
 */
public class ExtraAnnotatedMethodsException extends Exception {
    public ExtraAnnotatedMethodsException(String message) {
        super(message);
    }
}
