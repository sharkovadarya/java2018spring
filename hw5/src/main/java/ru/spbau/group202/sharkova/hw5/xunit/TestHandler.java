package ru.spbau.group202.sharkova.hw5.xunit;

import ru.spbau.group202.sharkova.hw5.xunit.annotations.*;
import ru.spbau.group202.sharkova.hw5.xunit.exceptions.*;
import ru.spbau.group202.sharkova.hw5.xunit.results.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class finds test methods in a class and runs them, forming a list of results.
 */
public class TestHandler {

    private Class<?> cl;
    private List<Method> testMethods = new ArrayList<>();
    private Method beforeMethod = null;
    private Method afterMethod = null;
    private Method classBeforeMethod = null;
    private Method classAfterMethod = null;

    public TestHandler(Class<?> cl) throws ExtraAnnotatedMethodsException, ExtraAnnotationsException {
        this.cl = cl;
        for (Method method : cl.getMethods()) {
            int numberOfAnnotations = 0;
            if (isTestMethod(method)) {
                testMethods.add(method);
                numberOfAnnotations++;
            }
            if (isBeforeMethod(method)) {
                if (beforeMethod != null) {
                    throw new ExtraAnnotatedMethodsException("Extra 'before' methods detected.");
                }
                beforeMethod = method;
                numberOfAnnotations++;
            }
            if (isAfterMethod(method)) {
                if (afterMethod != null) {
                    throw new ExtraAnnotatedMethodsException("Extra 'after' method detected.");
                }
                afterMethod = method;
                numberOfAnnotations++;
            }
            if (isClassBeforeMethod(method)) {
                if (classBeforeMethod != null) {
                    throw new ExtraAnnotatedMethodsException("Extra 'before class' methods detected.");
                }
                classBeforeMethod = method;
                numberOfAnnotations++;
            }
            if (isClassAfterMethod(method)) {
                if (classAfterMethod != null) {
                    throw new ExtraAnnotatedMethodsException("Extra 'after class' methods detected.");
                }
                classAfterMethod = method;
                numberOfAnnotations++;
            }

            if (numberOfAnnotations > 1) {
                throw new ExtraAnnotationsException("Too many annotations on " + method.getName() + " method.");
            }
        }
    }

    /**
     * Run all tests in class and get results.
     * @return list of test results
     * @throws IncorrectTestException if the test couldn't have been properly invoked
     * @throws ClassBeforeMethodFailedException if before class methods couldn't have been executed
     * @throws ClassAfterMethodFailedException if after class methods couldn't have been executed
     */
    public List<TestResult> runTests()
            throws IncorrectTestException, ClassBeforeMethodFailedException,
            ClassAfterMethodFailedException {
        List<TestResult> results = new ArrayList<>();

        try {
            invokeMethod(null, classBeforeMethod);
        } catch (InvocationTargetException e) {
            throw new ClassBeforeMethodFailedException(e);
        }

        for (Method testMethod : testMethods) {
            results.add(runTest(testMethod));
        }

        try {
            invokeMethod(null, classAfterMethod);
        } catch (InvocationTargetException e) {
            throw new ClassAfterMethodFailedException(e);
        }


        return results;

    }

    /**
     * Runs a single test and returns the result.
     * @return test result
     * @throws IncorrectTestException if the test couldn't have been properly invoked
     */
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
            invokeMethod(o, beforeMethod);
            method.invoke(o);
            invokeMethod(o, afterMethod);
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

    private boolean isUsedForTestsStatic(Method method) {
        return  Modifier.isStatic(method.getModifiers()) &&
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
        return isUsedForTestsStatic(method) && method.getAnnotation(ClassBeforeMethod.class) != null;
    }

    private boolean isClassAfterMethod(Method method) {
        return isUsedForTestsStatic(method) && method.getAnnotation(ClassAfterMethod.class) != null;
    }

    private void invokeMethod(Object o, Method method) throws InvocationTargetException, IncorrectTestException {
        if (method == null) {
            return;
        }

        try {
            method.invoke(o);
        } catch (IllegalAccessException e) {
            throw new IncorrectTestException(e);
        }
    }
}
