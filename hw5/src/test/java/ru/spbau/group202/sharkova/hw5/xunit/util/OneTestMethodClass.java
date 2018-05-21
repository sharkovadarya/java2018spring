package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

import java.util.concurrent.TimeUnit;

public class OneTestMethodClass {
    @TestMethod
    public void testMethod() throws InterruptedException {
        int a = 30;
        a += 59;
        TimeUnit.SECONDS.sleep(1);
    }

    public void notTestMethod() {
        int a =30;
        a += 59;
    }
}
