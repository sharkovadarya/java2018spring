package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

public class IncorrectTestException extends Exception {
    public IncorrectTestException(Exception e) {
        super(e);
    }
}
