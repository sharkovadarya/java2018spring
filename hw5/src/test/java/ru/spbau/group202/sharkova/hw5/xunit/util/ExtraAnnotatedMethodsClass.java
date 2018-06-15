package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.BeforeMethod;
import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

public class ExtraAnnotatedMethodsClass {
    @BeforeMethod
    public void beforeMethod1() {}

    @BeforeMethod
    public void beforeMethod2() {}

    @TestMethod
    public void testMethod() {}
}
