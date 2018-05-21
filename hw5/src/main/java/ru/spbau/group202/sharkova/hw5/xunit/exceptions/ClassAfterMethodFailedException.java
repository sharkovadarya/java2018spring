package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

/**
 * A custom exception for failed methods executed after all class methods.
 */
public class ClassAfterMethodFailedException extends Exception {
    public ClassAfterMethodFailedException(Exception e) {
        super(e);
    }
}
