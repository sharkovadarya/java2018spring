package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

public class UnexpectedExceptionClass {
    @TestMethod
    public void unexpectedExceptionTestMethod() {
        throw new NullPointerException();
    }
}
