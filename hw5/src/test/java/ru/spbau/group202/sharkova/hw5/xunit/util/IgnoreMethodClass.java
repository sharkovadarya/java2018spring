package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

public class IgnoreMethodClass {

    @TestMethod
    public void noIgnoreTestMethod() {

    }

    @TestMethod(ignore = "reason")
    public void ignoreTestMethod() {

    }
}
