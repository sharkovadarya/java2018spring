package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

public class ExceptionNotThrownClass {
    @TestMethod(expected = NullPointerException.class)
    public void exceptionNotThrownTestMethod() {

    }
}
