package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

/**
 * Custom exception class for methods with too many annotations.
 */
public class ExtraAnnotationsException extends Exception {
    public ExtraAnnotationsException(String message) {
        super(message);
    }
}
