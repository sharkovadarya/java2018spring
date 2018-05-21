package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

public class OneTestMethodClass {
    @TestMethod
    public void testMethod() {
        int a = 30;
        a += 59;
    }
}
