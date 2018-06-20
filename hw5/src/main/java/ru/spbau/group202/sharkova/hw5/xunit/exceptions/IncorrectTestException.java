package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

/**
 * A custom exception for incorrect tests.
 * Thrown if the test could not be invoked (accessed).
 */
public class IncorrectTestException extends Exception {
    public IncorrectTestException(Exception e) {
        super(e);
    }
}
