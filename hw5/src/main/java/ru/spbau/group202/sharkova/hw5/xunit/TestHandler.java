package ru.spbau.group202.sharkova.hw5.xunit;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.*;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.ClassAfterMethodFailedException;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.ClassBeforeMethodFailedException;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.IncorrectTestException;
import ru.spbau.group202.sharkova.hw5.xunit.results.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TestHandler {

    private Class<?> cl;
    private List<Method> testMethods = new ArrayList<>();
    private List<Method> beforeMethods = new ArrayList<>();
    private List<Method> afterMethods = new ArrayList<>();
    private List<Method> classBeforeMethods = new ArrayList<>();
    private List<Method> classAfterMethods = new ArrayList<>();

    public TestHandler(Class<?> cl) {
        this.cl = cl;
        for (Method method : cl.getMethods()) {
            if (isTestMethod(method)) {
                testMethods.add(method);
            } else if (isBeforeMethod(method)) {
                beforeMethods.add(method);
            } else if (isAfterMethod(method)) {
                afterMethods.add(method);
            } else if (isClassBeforeMethod(method)) {
                classBeforeMethods.add(method);
            } else if (isClassAfterMethod(method)) {
                classAfterMethods.add(method);
            }

        }
    }

    public List<TestResult> runTests()
            throws IncorrectTestException, ClassBeforeMethodFailedException,
            ClassAfterMethodFailedException {
        List<TestResult> results = new ArrayList<>();

        try {
            invokeMethods(null, classBeforeMethods);
        } catch (InvocationTargetException e) {
            throw new ClassBeforeMethodFailedException(e);
        }

        for (Method testMethod : testMethods) {
            results.add(runTest(testMethod));
        }

        try {
            invokeMethods(null, classAfterMethods);
        } catch (InvocationTargetException e) {
            throw new ClassAfterMethodFailedException(e);
        }


        return results;

    }

    private TestResult runTest(Method method) throws IncorrectTestException {
        TestMethod testMethodAnnotation = method.getAnnotation(TestMethod.class);
        if (!testMethodAnnotation.ignore().isEmpty()) {
            return new TestResultIgnored(cl.getName(), method.getName(), testMethodAnnotation.ignore(), 0);
        }

        Object o;
        Exception exception = null;
        try {
            o = cl.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new IncorrectTestException(e);
        }

        long start = System.currentTimeMillis();
        try {
            invokeMethods(o, beforeMethods);
            method.invoke(o);
            invokeMethods(o, afterMethods);
        } catch (InvocationTargetException e) {
            exception = (Exception) e.getCause();
        } catch (IllegalAccessException e) {
            throw new IncorrectTestException(e);
        }

        long executionTime = System.currentTimeMillis() - start;

        if (exception == null &&
                !testMethodAnnotation.expected().equals(TestMethod.NoExceptionExpected.class)) {
            return new TestResultExceptionNotThrown(cl.getName(), method.getName(),
                    testMethodAnnotation.expected(), executionTime);
        } else if (exception != null &&
                !testMethodAnnotation.expected().isInstance(exception)) {
            return new TestResultUnexpectedException(cl.getName(), method.getName(),
                    exception, executionTime);
        }

        return new TestResultPassed(cl.getName(), method.getName(), executionTime);
    }

    private boolean isUsedForTests(Method method) {
        return  !Modifier.isStatic(method.getModifiers()) &&
                Modifier.isPublic(method.getModifiers()) &&
                method.getReturnType().equals(Void.TYPE) &&
                method.getParameterCount() == 0;
    }

    private boolean isTestMethod(Method method) {
        return isUsedForTests(method) && method.getAnnotation(TestMethod.class) != null;
    }

    private boolean isBeforeMethod(Method method) {
        return isUsedForTests(method) && method.getAnnotation(BeforeMethod.class) != null;
    }

    private boolean isAfterMethod(Method method) {
        return isUsedForTests(method) && method.getAnnotation(AfterMethod.class) != null;
    }

    private boolean isClassBeforeMethod(Method method) {
        return isUsedForTests(method) && method.getAnnotation(ClassBeforeMethod.class) != null;
    }

    private boolean isClassAfterMethod(Method method) {
        return isUsedForTests(method) && method.getAnnotation(ClassAfterMethod.class) != null;
    }

    private void invokeMethods(Object o, List<Method> methods)
            throws InvocationTargetException, IncorrectTestException {
        for (Method method : methods) {
            try {
                method.invoke(o);
            } catch (IllegalAccessException e) {
                throw new IncorrectTestException(e);
            }
        }
    }



}
