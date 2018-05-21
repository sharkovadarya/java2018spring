package ru.spbau.group202.sharkova.hw5.xunit.results;

/**
 * Utility class storing test result (class name, test name, execution time, status, description).
 */
abstract public class TestResult {
    private String className;
    private String testName;
    private String description;
    private long time;
    private boolean passed;

    public TestResult(String className, String testName, String description, long time, boolean passed) {
        this.className = className;
        this.testName = testName;
        this.description = description;
        this.time = time;
        this.passed = passed;
    }

    public String getClassName() {
        return className;
    }

    public String getTestName() {
        return testName;
    }

    public String getDescription() {
        return description;
    }

    public long getTime() {
        return time;
    }

    public boolean passed() {
        return passed;
    }
}
