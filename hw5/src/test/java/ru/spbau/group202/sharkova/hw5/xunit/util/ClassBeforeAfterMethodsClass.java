package ru.spbau.group202.sharkova.hw5.xunit.util;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.ClassAfterMethod;
import ru.spbau.group202.sharkova.hw5.xunit.annotations.ClassBeforeMethod;
import ru.spbau.group202.sharkova.hw5.xunit.annotations.TestMethod;

import java.util.ArrayList;
import java.util.List;

public class ClassBeforeAfterMethodsClass {
    private static List<Integer> list = new ArrayList<>();

    @ClassBeforeMethod
    public static void classBeforeMethod() {
        list.add(30);
    }

    @TestMethod
    public void testMethod1() {
        list.add(15);
    }

    @TestMethod
    public void testMethod2() {
        list.add(17);
    }

    @ClassAfterMethod
    public static void classAfterMethod() {
        list.add(11);
    }

    public static List<Integer> getList() {
        return list;
    }
}
