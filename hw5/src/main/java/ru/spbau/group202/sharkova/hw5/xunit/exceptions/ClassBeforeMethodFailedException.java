package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

/**
 * A custom exception for failed methods executed before all class methods.
 */
public class ClassBeforeMethodFailedException extends Exception {
    public ClassBeforeMethodFailedException(Exception e) {
        super(e);
    }
}
