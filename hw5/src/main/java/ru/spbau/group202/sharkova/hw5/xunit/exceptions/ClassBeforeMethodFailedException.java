package ru.spbau.group202.sharkova.hw5.xunit.exceptions;

public class ClassBeforeMethodFailedException extends Exception {
    public ClassBeforeMethodFailedException(Exception e) {
        super(e);
    }
}
