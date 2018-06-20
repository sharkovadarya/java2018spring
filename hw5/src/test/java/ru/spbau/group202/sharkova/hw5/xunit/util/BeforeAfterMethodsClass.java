package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.AfterMethod;
import ru.spbau.group202.sharkova.hw5.xunit.annotations.BeforeMethod;
import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

public class BeforeAfterMethodsClass {
    private static int a = 0;

    @BeforeMethod
    public void beforeMethod() {
        a = 30;
    }

    @TestMethod
    public void testMethod() {
        a++;
    }

    @AfterMethod
    public void afterMethod() {
        a -= 12;
    }

    public static int getA() {
        return a;
    }
}
