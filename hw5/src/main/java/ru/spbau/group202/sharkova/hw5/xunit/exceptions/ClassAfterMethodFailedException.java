package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

public class ClassAfterMethodFailedException extends Exception {
    public ClassAfterMethodFailedException(Exception e) {
        super(e);
    }
}
