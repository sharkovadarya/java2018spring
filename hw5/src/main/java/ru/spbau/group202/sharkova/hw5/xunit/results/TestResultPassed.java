package ru.spbau.group202.sharkova.hw5.xunit.results;

/**
 * Successful test result.
 */
public class TestResultPassed extends TestResult {
    public TestResultPassed(String className, String testName, long time) {
        super(className, testName, "Test passed.", time, true);
    }
}
