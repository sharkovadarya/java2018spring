package ru.spbau.group202.sharkova.hw5.xunit.results;

/**
 * Result of a test with an expected exception which was not thrown.
 */
public class TestResultExceptionNotThrown extends TestResult{
    public TestResultExceptionNotThrown(String className, String testName,
                                        Class<? extends Exception> expectedExceptionClass,
                                        long executionTime) {
        super(className, testName, "Expected " + expectedExceptionClass.getName() + " wasn't thrown", executionTime,
                false);
    }
}
